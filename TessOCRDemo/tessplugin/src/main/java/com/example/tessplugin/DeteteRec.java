package com.example.tessplugin;

import android.graphics.Bitmap;
import android.text.Html;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class DeteteRec {

    public boolean busy=false;

    //get edges use canny
    public Mat getCanny(Mat src){
        Mat kernel=Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT,new Size(3,3));

        Mat bc=new Mat();
        Mat gc=new Mat();
        Mat rc=new Mat();

        Mat bgc=new Mat();
        Mat edge=new Mat();

        Mat dila=new Mat();

        List<Mat> bgr= new LinkedList<>();

        Core.split(src,bgr);
        Mat b=bgr.get(0);
        Mat g=bgr.get(1);
        Mat r=bgr.get(2);


        Imgproc.Canny(b,bc,25,70);
        Imgproc.Canny(g,gc,25,70);
        Imgproc.Canny(r,rc,25,70);
        Core.bitwise_or(bc,gc,bgc);
        Core.bitwise_or(rc,bgc,edge);

        Imgproc.dilate(edge,dila,kernel);

        b.release();
        g.release();
        r.release();
        bc.release();
        gc.release();
        rc.release();
        bgc.release();
        edge.release();
        src.release();
        return dila;
    }


    public Mat getPerspectiveAndClip(Mat src,Point[] points,int width,int height,double rate){
        Mat pers=new Mat();
        Mat srcarray=new Mat(4,1,CvType.CV_32FC2);
        Mat dest=new Mat(4,1, CvType.CV_32FC2);
        for (int i=0;i<points.length;i++){
            points[i].x=points[i].x*rate;
            points[i].y=points[i].y*rate;
        }

        srcarray.put(0,0,(int)points[0].x,(int)points[0].y,(int)points[1].x,(int)points[1].y,(int)points[3].x,(int)points[3].y,(int)points[2].x,(int)points[2].y);
        dest.put(0,0,(int)points[0].x,(int)points[0].y,(int)points[2].x,(int)points[0].y,(int)points[0].x,(int)points[2].y,(int)points[2].x,(int)points[2].y);
        Mat mym=Imgproc.getPerspectiveTransform(srcarray,dest);

        Imgproc.warpPerspective(src,pers,mym,new Size(width,height));

        Rect cardrect=new Rect((int) points[0].x,(int) points[0].y,(int) (points[2].x-points[0].x),(int) (points[2].y-points[0].y)); //Clip rectangle
        Mat result=new Mat(pers,cardrect);

        pers.release();
        src.release();
        srcarray.release();
        dest.release();
        mym.release();

        return result;
    }

    public Bitmap getCard(String path,boolean saveClip){
        File file=new File(path);
        if(!file.exists()){
            return null;
        }
        Mat rgb= Highgui.imread(path);

        int cannysize=512;
        int cardsize=1080;
        int w=rgb.width();
        int h=rgb.height();
        int min=Math.min(w,h);
        double cannyrate=(double)min/cannysize;
        double cardrate=(double)min/cardsize;
        double rate=(double)cardsize/cannysize;

        int cardwidth=(int)(w/cardrate);
        int cardheight=(int)(h/cardrate);
        int cannywidth=(int)(w/cannyrate);
        int cannyheight=(int)(h/cannyrate);

        Mat src=new Mat();
        Mat im=new Mat();
        Mat lines=new Mat();
        Mat hriy=new Mat();

        Imgproc.resize(rgb,im,new Size(cannywidth,cannyheight));
        Imgproc.resize(rgb,src,new Size(cardwidth,cardheight));

        List<MatOfPoint> contours=new LinkedList<>();

        Mat edge=getCanny(im);

        Imgproc.findContours(edge,contours,hriy,Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_NONE);
        int index=0;
        double maxArea=Imgproc.contourArea(contours.get(0));

        for(int i=1;i<contours.size();i++){
            MatOfPoint contour=contours.get(i);
            double area=Imgproc.contourArea(contour);
            if(area>maxArea){
                maxArea=area;
                index=i;
            }
        }

        Mat black=new Mat(cannyheight,cannywidth,CvType.CV_8UC1,new Scalar(0,0,0));

        Imgproc.drawContours(black,contours,index,new Scalar(255,255,255),2);

        Imgproc.HoughLinesP(black,lines,3,Math.PI/180,250, Math.min(cannywidth,cannyheight)/3,100);

//        for(int i=0;i<lines.cols();i+=1) {
//            double[] p = lines.get(0, i);
//            int x1 = (int) p[0];
//            int y1 = (int) p[1];
//            int x2 = (int) p[2];
//            int y2 = (int) p[3];
//            Core.line(im,new Point(x1,y1),new Point(x2,y2),new Scalar(255,255,255));
//        }
        Point[] points=getCornners(lines,cannywidth,cannyheight);
//        Random R=new Random();
//        int r,g,b=0;
//        r=R.nextInt(255);
//        g=R.nextInt(255);
//        b=R.nextInt(255);
//        Core.line(im,points[0],points[1],new Scalar(r,g,b));
//        Core.line(im,points[1],points[2],new Scalar(r,g,b));
//        Core.line(im,points[2],points[3],new Scalar(r,g,b));
//        Core.line(im,points[0],points[3],new Scalar(r,g,b));
        Mat result = getPerspectiveAndClip(src,points,cardwidth,cardheight,rate);
        Bitmap bmp=Bitmap.createBitmap(result.width(),result.height(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(result,bmp);
        String[] filenames = path.split("/");
        String root="/";
        for(int i=0;i<filenames.length-1;i++)
            root+=filenames[i]+"/";
        if(saveClip)
        Highgui.imwrite(root+"v"+filenames[filenames.length-1],result);
//        Bitmap bmp=Bitmap.createBitmap(cannywidth,cannyheight, Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(im,bmp);

        edge.release();
        //result.release();
        black.release();
        rgb.release();
        src.release();
        im.release();
        lines.release();
        hriy.release();
        return bmp;
    }

    public Point[] getCorners(Bitmap bmp,int size){

        busy=true;
        Mat rgb= new Mat();
        Utils.bitmapToMat(bmp,rgb);
        int cannysize=size;
        //int cardsize=1080;
        int w=rgb.width();
        int h=rgb.height();
        int min=1080;
        //cardsize=min;
        double cannyrate=(double)min/cannysize;
        //double cardrate=(double)min/cardsize;
        double rate=(double)min/cannysize;

        int cardwidth=w;
        int cardheight=h;

        int cannywidth=(int)(w/cannyrate);
        int cannyheight=(int)(h/cannyrate);

        //Mat src=new Mat();
        Mat im=new Mat();
        Mat lines=new Mat();
        Mat hriy=new Mat();

        Imgproc.resize(rgb,im,new Size(cannywidth,cannyheight));
        //Imgproc.resize(rgb,src,new Size(cardwidth,cardheight));

        List<MatOfPoint> contours=new LinkedList<>();

        Mat edge=getCanny(im);

        Imgproc.findContours(edge,contours,hriy,Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_NONE);
        int index=0;
        double maxArea=Imgproc.contourArea(contours.get(0));

        for(int i=1;i<contours.size();i++){
            MatOfPoint contour=contours.get(i);
            double area=Imgproc.contourArea(contour);
            if(area>maxArea){
                maxArea=area;
                index=i;
            }
        }

        Mat black=new Mat(cannyheight,cannywidth,CvType.CV_8UC1,new Scalar(0,0,0));

        Imgproc.drawContours(black,contours,index,new Scalar(255,255,255),2);

        Imgproc.HoughLinesP(black,lines,3,Math.PI/180,250, size/3,size/2);

        Point[] points=getCornners(lines,cannywidth,cannyheight);
        for(int i=0;i<4;i++){
            points[i].x*=rate;
            points[i].y*=rate;
        }
        return points;
    }
    public void setBusy(boolean v){
        busy=v;
    }

    public int max(int a,int b,int c,int d){
        return Math.max(Math.max(a,b),Math.max(c,d));
    }
    public int min(int a,int b,int c,int d){
        return Math.min(Math.min(a,b),Math.min(c,d));
    }

    public int getLength(int[] p,int orien){
        if(orien==0){
            return Math.abs(p[2]-p[0]);
        }
        else if(orien==1) {
            return Math.abs(p[3]-p[1]);
        }

        return  0;
    }
    public int getLength(int x1,int y1,int x2,int y2,int orien){

        if(orien==0){
            return  Math.abs(x2-x1);
        }
        else if(orien==1){
            return Math.abs(y2-y1);
        }
        return  0;
    }

    public Point[] getCornners(Mat lines,int w,int h){
        int[] top=new int[4];
        int[] bottom=new int[4];
        int[] left=new int[4];
        int[] right=new int[4];
        int[] p1=new int[]{0,0};
        int[] p2=new int[]{w,0};
        int[] p3=new int[]{w,h};
        int[] p4=new int[]{0,h};


        for(int i=0;i<lines.cols();i+=1){
            double[] p= lines.get(0,i);
            int x1=(int)p[0];
            int y1=(int)p[1];
            int x2=(int)p[2];
            int y2=(int)p[3];


            int x=Math.abs(x2-x1);
            int y=Math.abs(y2-y1);

            if(x>2*y){
                int len=getLength(x1,y1,x2,y2,0);
                if(y1<h/2){ //on top
                    int toplen=getLength(top,0);
                    if(toplen<len){
                        if(x1<x2){
                            top[0]=x1;
                            top[1]=y1;
                            top[2]=x2;
                            top[3]=y2;
                        }
                        else {
                            top[0]=x2;
                            top[1]=y2;
                            top[2]=x1;
                            top[3]=y1;
                        }
                    }
                }
                else { //on bottom
                    int bottomlen=getLength(bottom,0);
                    if(bottomlen<len && (y1-h/2)>100){
                        if(x1<x2){
                            bottom[0]=x1;
                            bottom[1]=y1;
                            bottom[2]=x2;
                            bottom[3]=y2;
                        }
                        else {
                            bottom[0]=x2;
                            bottom[1]=y2;
                            bottom[2]=x1;
                            bottom[3]=y1;
                        }
                    }
                }
            }
            else if (y>2*x){
                int len=getLength(x1,y1,x2,y2,1);
                if(x1<w/2){ //on left
                    int leftlen=getLength(left,1);
                    if(leftlen<len){
                        if(y1<y2){ 
                            left[0]=x1;
                            left[1]=y1;
                            left[2]=x2;
                            left[3]=y2;
                        }
                        else {
                            left[0]=x2;
                            left[1]=y2;
                            left[2]=x1;
                            left[3]=y1;
                        }
                    }
                }
                else{ //on right
                    int rightlen=getLength(right,1);
                    if(rightlen<len){
                        if(y1<y2){
                            right[0]=x1;
                            right[1]=y1;
                            right[2]=x2;
                            right[3]=y2;
                        }
                        else {
                            right[0]=x2;
                            right[1]=y2;
                            right[2]=x1;
                            right[3]=y1;
                        }
                    }
                }
            }

        }
        int topleng=getLength(top,0);
        int bottomleng=getLength(bottom,0);
        int leftleng=getLength(left,1);
        int rightleng=getLength(right,1);

//        Point p1=getPoint(top,left);
//        Point p2=getPoint(top,right);
//        Point p3=getPoint(right,bottom);
//        Point p4=getPoint(bottom,left);



        if (leftleng!=0 && rightleng!=0 )
        {p1[0]=left[0];
        p4[0]=left[2];
        p2[0]=right[0];
        p3[0]=right[2];}
        if (topleng!=0 && bottomleng!=0)
        {p1[1]=top[1];
        p2[1]=top[3];
        p3[1]=bottom[3];
        p4[1]=bottom[1];}

        if (leftleng==0 && rightleng!=0) {
            p2[0] = right[0];
            p3[0] = right[0];
            if(topleng!=0){
                p1[0]=top[0];
            }
            else if(bottomleng!=0)
                p1[0]=bottom[0];
            if(bottomleng!=0){
                p4[0]=bottom[0];
            }
            else if(topleng!=0)
                p4[0]=top[0];
        }
        else if (leftleng!=0 && rightleng==0) {
            p1[0] = left[0];
            p4[0] = left[2];
            if(topleng!=0){
                p2[0]=top[2];
            }
            else if(bottomleng!=0)
                p2[0]=bottom[2];
            if(bottomleng!=0){
                p3[0]=bottom[2];
            }
            else if(topleng!=0)
                p3[0]=top[2];
        }

        if (topleng==0 && bottomleng!=0) {
            p4[1] = bottom[1];
            p3[1] = bottom[3];
            if(leftleng!=0){
                p1[1]=left[1];
            }
            else if(rightleng!=0)
                p1[1]=right[1];
            if(rightleng!=0){
                p2[1]=right[1];
            }
            else if(leftleng!=0)
                p2[1]=left[1];
        }
        else if (topleng!=0 && bottomleng==0) {
            p2[1] = top[3];
            p1[1] = top[1];
            if(leftleng!=0){
                p4[1]=left[3];
            }
            else if(rightleng!=0)
                p3[1]=right[3];
            if(rightleng!=0){
                p3[1]=right[3];
            }
            else if(leftleng!=0)
                p4[1]=left[3];
        }


        Point[] points={
          new Point(p1[0],p1[1]),
                new Point(p2[0],p2[1]),
                new Point(p3[0],p3[1]),
                new Point(p4[0],p4[1])
        };
        //Point[] points=new Point[]{p1,p2,p3,p4};
        return points;
    }

    public Point getPoint(int[] line1,int[] line2){
        int b=1;
        double k1=(double)(line1[3]-line1[1])/(line1[2]-line1[0]);
        double k2=(double)(line2[3]-line2[1])/(line2[2]-line2[0]);

        double c1=k1*line1[0]-line1[1];
        double c2=k2*line2[0]-line2[1];
        int x=(int)((c2-c1)/(k2-k1));
        int y=(int)((k1*c2-k2*c1)/(k2-k1));
        return new Point(x,y);
    }

    public  Bitmap getTrasBmp(int width,int height){
        Mat black=new Mat(height,width,CvType.CV_8UC4,new Scalar(0,0,0,0));
        Bitmap bmp=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(black,bmp);
        return bmp;
    }

    Point LineIntersectionPoint(Point ps1, Point pe1, Point ps2, Point pe2) {
        // Get A,B,C of first line - points : ps1 to pe1
        double A1 = pe1.y-ps1.y;
        double B1 = ps1.x-pe1.x;
        double C1 = A1*ps1.x+B1*ps1.y;

        // Get A,B,C of second line - points : ps2 to pe2
        double A2 = pe2.y-ps2.y;
        double B2 = ps2.x-pe2.x;
        double C2 = A2*ps2.x+B2*ps2.y;

        // Get delta and check if the lines are parallel
        double delta = A1*B2 - A2*B1;
        if(delta == 0)
            return null;

        // now return the Vector2 intersection point
        return new Point(
                (B2*C1 - B1*C2)/delta,
                (A1*C2 - A2*C1)/delta
        );
    }
}
