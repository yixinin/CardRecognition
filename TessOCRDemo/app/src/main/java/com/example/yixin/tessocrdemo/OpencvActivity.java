package com.example.yixin.tessocrdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Created by yixin on 08.09.17.
 */

public class OpencvActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    CameraBridgeViewBase mCVCamera;
    BaseLoaderCallback mLoaderCallback;
    String TAG="debug";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_cv);

        mCVCamera = (CameraBridgeViewBase) findViewById(R.id.camera_view);
        mCVCamera.setCvCameraViewListener(this);
        //mCVCamera.setRotation(-90);
        mLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status){
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS:
                        Log.i(TAG,"OpenCV loaded successfully");
                        mCVCamera.enableView();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    public void onCameraViewStarted(int i, int i1) {

    }

    @Override
    public void onCameraViewStopped() {

    }
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        Mat res=new Mat();
        Mat img=inputFrame.rgba();
        int w=img.width();
        int h=img.height();
        Mat M= Imgproc.getRotationMatrix2D(new Point(0.5*w,0.5*h),-90,1);
        Imgproc.warpAffine(img,res,M,new Size(h,w));
        return res;
    }
    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG,"OpenCV library not found!");
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    };
}
