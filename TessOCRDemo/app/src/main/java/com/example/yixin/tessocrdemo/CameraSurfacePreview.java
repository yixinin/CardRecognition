package com.example.yixin.tessocrdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.tessplugin.DeteteRec;

import org.opencv.core.Point;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by yixin on 08.09.17.
 */

public class CameraSurfacePreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;


    DeteteRec detete;

    public CameraSurfacePreview(Context context) {
        super(context);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        detete=CamActivity.Current.detete;
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {

        Log.d("Dennis", "surfaceCreated() is called");

        try {
            // Open the Camera in preview mode
            mCamera = Camera.open();
            Camera.Parameters params = mCamera.getParameters();
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            Log.d("Dennis", "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

        Log.d("Dennis", "surfaceChanged() is called");

        try {
            mCamera.startPreview();

        } catch (Exception e){
            Log.d("Dennis", "Error starting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

        Log.d("Dennis", "surfaceDestroyed() is called");
    }

    public void takePicture(Camera.PictureCallback imageCallback) {
        mCamera.takePicture(null, null, imageCallback);
    }

    public void getPreViewImage() {

        mCamera.setPreviewCallback(new Camera.PreviewCallback(){

            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                Camera.Parameters params = mCamera.getParameters();
                //params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                //mCamera.setParameters(params);
                Camera.Size size = params.getPreviewSize();
                try{
                    YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
                    if(image!=null){
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);

                        Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                        mCamera.autoFocus(null);
                        //**********************
                        //因为图片会放生旋转，因此要对图片进行旋转到和手机在一个方向上
                        rotateMyBitmap(bmp);
                        //**********************************

                        stream.close();
                    }
                }catch(Exception ex){
                    Log.e("Sys","Error:"+ex.getMessage());
                }
            }
        });
    }
    //int index=0;
    public void rotateMyBitmap(final Bitmap bmp){

        detete=CamActivity.Current.detete;
        final Handler mHandler=CamActivity.Current.mHandler;
        //*****旋转一下
        if(detete.busy){
            Message message=new Message();
            message.what=2;
            message.obj="false";
            mHandler.sendMessage(message);
            return;
        }else {
           // Matrix matrix = new Matrix();
           // matrix.postRotate(90);

            //final Bitmap bitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);


            //final Bitmap nbmp2 = Bitmap.createBitmap(bmp, 0,0, bmp.getWidth(),  bmp.getHeight(), matrix, true);

            Thread th=new Thread(new Runnable()
            {
                @Override
                public void run()
                {

                    long startTime = System.currentTimeMillis();
                    Point[] points=detete.getCorners(bmp,200);

                    long endTime = System.currentTimeMillis();

                    Message message=new Message();
                    message.what=1;
                    message.obj=points;
                    mHandler.sendMessage(message);
                    detete.setBusy(false);
                }
            });
            th.start();
        }

    };
}
