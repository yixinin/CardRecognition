package com.example.yixin.tessocrdemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.tessplugin.DeteteRec;

import org.opencv.android.OpenCVLoader;

/**
 * Created by yixin on 08.09.17.
 */

public class CamActivity extends Activity implements OnClickListener, PictureCallback {
    private CameraSurfacePreview mCameraSurPreview = null;
    private Button mCaptureButton = null;
    private String TAG = "Dennis";
    public static CamActivity Current;
    public TextView tv;
    ImageView img;
    Canvas canvas;
    Paint paint;
    DeteteRec detete;
    Bitmap bmp;
    myView v;
    org.opencv.core.Point[] points;
    LinearLayout lay;

    public Handler mHandler=new Handler()
    {
        public void handleMessage(Message msg)
        {
            //img.invalidate();
            switch(msg.what)
            {
                case 1:
//                    canvas=new Canvas(bmp);
//                    img.setImageBitmap(bmp);
                    //canvas.setBitmap();
                    points = (org.opencv.core.Point[]) msg.obj;
//                    piant.setStrokeWidth(5);
//                    piant.setColor(Color.argb(255,255,255,0));
//                    //canvas.drawLines(pts,piant);
                    v.invalidate();

                    //Log.d("debug",String.valueOf(points.length));

                    break;
                default:
                    break;
                case 2:
                    //Log.v((String.valueOf("debug")),(String)msg.obj);
                    //canvas.drawLine(0f,0f,1920f,1080f,piant);
                   // System.out.println((String) msg.obj);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent it=getIntent();
        String msg=it.getStringExtra("test");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_cam);
        Current=this;
        tv=(TextView)findViewById(R.id.tv);
        img=(ImageView)findViewById(R.id.transimg);
        detete=new DeteteRec();
        lay=(LinearLayout) findViewById(R.id.lay);
        // Create our Preview view and set it as the content of our activity.
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        mCameraSurPreview = new CameraSurfacePreview(this);
        preview.addView(mCameraSurPreview);

        paint = new Paint();
        // 去锯齿
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        v=new myView(CamActivity.this);
        lay.addView(v);
        //canvas.drawLine(0f,0f,1920f,1080f,piant);
        // Add a listener to the Capture button
        mCaptureButton = (Button) findViewById(R.id.button_capture);
        mCaptureButton.setOnClickListener(this);

    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        //save the picture to sdcard
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null){
            Log.d(TAG, "Error creating media file, check storage permissions: ");
            return;
        }

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }

        // Restart the preview and re-enable the shutter button so that we can take another picture
        camera.startPreview();
        //See if need to enable or not
        mCaptureButton.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        //mCaptureButton.setEnabled(false);
        mCameraSurPreview.getPreViewImage();
        // get an image from the camera
        //mCameraSurPreview.takePicture(this);
    }

    private File getOutputMediaFile(){
        //get the mobile Pictures directory
        File picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        //get the current time
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        return new File(picDir.getPath() + File.separator + "IMAGE_"+ timeStamp + ".jpg");
    }
    public class myView extends View{//定义一个类，继承于view

        public myView(Context context){
            super(context);
        }
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Log.d(TAG, "start draw");
            // 把整张画布绘制成淡蓝色

            canvas.drawColor(Color.argb(0,0,0,0));

            try {
                if(points!=null){
                    canvas.drawLine((float) points[0].x,(float) points[0].y,(float) points[1].x,(float) points[1].y,paint);
                    canvas.drawLine((float) points[1].x,(float) points[1].y,(float) points[2].x,(float) points[2].y,paint);
                    canvas.drawLine((float) points[2].x,(float) points[2].y,(float) points[3].x,(float) points[3].y,paint);
                    canvas.drawLine((float) points[3].x,(float) points[3].y,(float) points[0].x,(float) points[0].y,paint);
                }
            }
           catch (Exception ex){
               //canvas.drawLine(0,0,1920,1080,paint);
           }


        }

    }
}
