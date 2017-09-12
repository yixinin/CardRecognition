package com.example.yixin.tessocrdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.sax.TextElementListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tessplugin.DeteteRec;
import com.example.tessplugin.TessOCR;
import com.example.tessplugin.TextLine;

//import com.googlecode.tesseract.android.TessBaseAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.highgui.Highgui;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Button chooseFromAlbum;
    private ImageView picImageView;
    Bitmap bmp=null;
    String text="";
    String rootPath="";
    ImageView img;
    TextView tv;
    DeteteRec detete;
    String imgname="img8";
    private static final String TAG = "gao_chun";

    public Handler mHandler=new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case 1:
                    text=(String) msg.obj;
                    tv.setText(text);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootPath= Environment.getExternalStorageDirectory() + "/tesseract/";
        String imgpath=rootPath+imgname;
        String test="sfasf+sdas+sfas";
        String[] l=test.split("\\+");

        //initopencv();
        //detete=new DeteteRec();
        //TessOCR.Init(rootPath,"eng");
        //String text= TessOCR.getutf8String(imgpath);
        //bmp=detete.getCard(imgpath);
        //bmp=readBmp(rootPath+"v"+imgname);

        img=(ImageView)findViewById(R.id.img);
        tv=  (TextView) findViewById(R.id.tv);
       // tv.setText(text);

        //bmp=TessOCR.getGray(imgpath);
//        mTess.setImage(bmp);
//        String text=mTess.getUTF8Text();
        //TessOCR.Init(rootPath,"eng");
        //text = TessOCR.getutf8String(imgpath);
        //tv.setText(text);
        //bmp= getGray(imgpath);
//
        //img.setImageBitmap(bmp);
//
        //TessOCR.Init("","eng");

       // ShowOCR(imgpath);

    }
    public Bitmap getGray(String path){
        Mat gray = Highgui.imread(path,0);
        Bitmap bmp=Bitmap.createBitmap(gray.width(),gray.height(),Bitmap.Config.ARGB_8888);

        Utils.matToBitmap(gray,bmp);
        return bmp;
    }
    public void de_onClick(View v) throws JSONException {
        text="";
        EditText edit=(EditText) findViewById(R.id.edit);
        final String imgPath=rootPath+edit.getText()+".jpg";
//        long startTime = System.currentTimeMillis();
//        Point[] points=detete.getCorners(imgPath,200);
//        long endTime = System.currentTimeMillis();
//
//        text+=String.valueOf(endTime-startTime)+"ms";
//        tv.setText(text);

//        Thread thread=new Thread(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                long startTime = System.currentTimeMillis();
//                Point[] points=detete.getCorners(imgPath,200);
//                long endTime = System.currentTimeMillis();
//                String t="";
//                for(int i=0;i<points.length;i++){
//                    t+=points[i].toString()+"\n";
//                }
//                t+=String.valueOf(endTime-startTime);
//                Message message=new Message();
//                message.what=1;
//                message.obj=t;
//                mHandler.sendMessage(message);
//            }
//        });
//        thread.start();
        //imgname=edit.getText()+".jpg";
        //String text = TessOCR.getutf8String(imgPath,true);
        //bmp= readBmp(rootPath+"v"+imgname);
        //text= TessOCR.res;
//        JSONArray jsonarray = new JSONArray(text);
//        ArrayList<TextLine> textlines=new ArrayList<>();
//        String email="";
//        String name="";
//        String fname="";
//        String lname="";
//        String ffn="";
//        String fln="";
//        for(int i=0;i<jsonarray.length();i++){
//            JSONObject ob=jsonarray.getJSONObject(i);
//            TextLine tl=new TextLine();
//            tl.Text=ob.getString("Text");
//            tl.Width=ob.getInt("Width");
//            tl.Height=ob.getInt("Height");
//            textlines.add(tl);
//            if(tl.Text.contains("@") || tl.Text.contains("®")|| tl.Text.contains("©")){
//                email=tl.Text;
//                email.replace("©","@");
//                email.replace("®","@");
//                email.replace("'",".");
//            }
//            if(!email.isEmpty()){
//                String[] names=email.split("@");
//                name=names[0];
//                if(name.contains(".")){
//                    String[] mns=name.split(".");
//                    fname=mns[0];
//                    lname=mns[1];
//                }
//                if(name.length()==2){
//                    ffn=name.substring(0,1);
//                    fln=name.substring(1);
//                }
//
//
//            }
//        }
//        if(!name.isEmpty()&&!email.isEmpty())
//        for(int i=0;i<textlines.size();i++){
//            TextLine line=textlines.get(i);
//            if (line.Text.contains(name)||line.Text.contains(fname)){
//                name=line.Text;
//            }
//            else if(line.Text.contains(ffn)&& line.Text.contains(fln)){
//                name=line.Text;
//            }
//        }

        //bmp=detete.getCard(imgPath);//+"\n"+"name:"+name+"  <---->  "+"email:"+email
        tv.setText(text);
        img.setImageBitmap(bmp);
    }
    public Bitmap readBmp(String path){
        FileInputStream stream;
        try {
            stream=new FileInputStream(new File(path));
            bmp= BitmapFactory.decodeStream(stream);
            img.setImageBitmap(bmp);
            return bmp;
        }
        catch (Exception ex){

        }
        return null;
    }
    public void ShowOCR(String imgpath){
        //bmp=detete.getCard(imgpath);
       // boolean ex = TessOCR.Init(rootPath,"eng");

        //text=TessOCR.getutf8String(imgpath);


        if(!text.isEmpty()){
            tv.setText("reslut:\n"+text);
        }
    }
    private void initopencv(){
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_13, getApplicationContext(), mLoaderCallback);

    }
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            // TODO Auto-generated method stub
            switch (status){
                case BaseLoaderCallback.SUCCESS:
                    Log.i(TAG, "成功加载");
                    break;
                default:
                    super.onManagerConnected(status);
                    Log.i(TAG, "加载失败");
                    break;
            }
        }
    };

    public void navigatetoCapture(View v){
        Intent intent = new Intent(this, OpencvActivity.class);
        intent.putExtra("test","test string");
        startActivity(intent);
        //setContentView(R.layout.activity_capture);
    }
}
