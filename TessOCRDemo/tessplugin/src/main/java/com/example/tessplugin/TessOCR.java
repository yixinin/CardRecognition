package com.example.tessplugin;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.Pixa;
import com.googlecode.tesseract.android.TessBaseAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.content.ContextWrapper;
import android.widget.Toast;

import net.sf.json.JsonConfig;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.List;

/**
 * Created by yixin on 30.08.17.
 */

public class TessOCR {
    private static Context unityContext;
    private static Activity unityActivity;

    public  static  String res="";
    private static TessBaseAPI mTess;

    public static void init(Context paramContext){
        unityContext = paramContext.getApplicationContext();
        unityActivity = (Activity) paramContext;
    }

    public static boolean Init(String path,String lang){
        boolean inited=false;
        mTess=new TessBaseAPI();
        if (unityContext!=null)
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_13, unityContext, mLoaderCallback);
        try {
            inited = mTess.init(path,lang);
        }
        catch (Exception ex){
            inited=false;
        }
        return inited;
    }

    public static String getutf8String(String path,boolean saveClip){

        DeteteRec detete=new DeteteRec();
        String text,result="null";
        //File imgfile;
        try {
            Bitmap bmp = detete.getCard(path,saveClip);
            //imgfile=new File(path);

            mTess.setImage(bmp);
            //mTess.setImage(imgfile);
            text = mTess.getUTF8Text();
            res=text;
            Pixa boxLines = mTess.getTextlines();
            int line=boxLines.size();
            int index=0;
            String[] textLines;//=new String[line];
            String[] oldlines=text.split("\n");
            if(oldlines.length!=line){
                textLines=new String[line];
                for (int i=0;i<oldlines.length;i++){
                    if (!oldlines[i].isEmpty()){
                        textLines[index]=oldlines[i];
                        index+=1;
                    }
                }
            }
            else textLines=oldlines;

        result=getJsonString(textLines,boxLines);
        }
        catch (Exception ex){
        }
        finally {
            mTess.clear();
        }
        return result;
    }

    private static BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(unityContext) {

        @Override
        public void onManagerConnected(int status) {
            // TODO Auto-generated method stub
            switch (status){
                case BaseLoaderCallback.SUCCESS:
                    //Log.i(TAG, "成功加载");
                    //ShowToase("Init Successed !");
                    break;
                default:
                    super.onManagerConnected(status);
                    //Log.i(TAG, "加载失败");
                   ShowToase("Init Failed !");
                    break;
            }
        }
    };

    private static void ShowToase(final String msg){
        unityActivity.runOnUiThread(new Runnable()
        {
            public void run()
            {
                Toast.makeText(unityContext,msg, Toast.LENGTH_LONG).show();
            }
        });
    }
    public static String getJsonString(String[] textlines, Pixa boxs) throws JSONException {
        JSONObject json=new JSONObject();
        JSONArray jsonMembers = new JSONArray();

        for(int i=0;i<textlines.length;i++){
            android.graphics.Rect box=boxs.getBox(i).getRect();
            JSONObject member1 = new JSONObject();
            member1.put("Text",textlines[i]);
            member1.put("Width",box.width());
            member1.put("Height",box.height());
            jsonMembers.put(member1);
        }
        json.put("TextLine", jsonMembers);

        return json.toString();
    }

    //public  static
}