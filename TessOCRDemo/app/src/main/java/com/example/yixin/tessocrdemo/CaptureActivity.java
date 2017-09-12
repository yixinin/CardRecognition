package com.example.yixin.tessocrdemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.security.AccessController.getContext;

/**
 * Created by yixin on 08.09.17.
 */

public class CaptureActivity extends Activity {
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    ImageView picture;
    Button opencam;
    Uri imageUri;
    String path;
    File mCameraFile;
    String rootPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent it=getIntent();
        String msg=it.getStringExtra("test");
        setContentView(R.layout.activity_capture);
        picture=(ImageView) findViewById(R.id.resimg);
        opencam=(Button) findViewById(R.id.open);
        rootPath= Environment.getExternalStorageDirectory() + "/tesseract/";
        //opencam.setOnClickListener(openCam(opencam));
    }
    private void setPic() {
        // Get the dimensions of the View
        int targetW = picture.getWidth();
        int targetH = 1920;//picture.getHeight()==0? targetW:picture.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        //BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(1, 1);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
        picture.setImageBitmap(bitmap);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);//由getExternalFilesDir(),以及getFilesDir()创建的目录，应用卸载后会被删除！

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        path = "file:" + image.getAbsolutePath();
        return image;
    }

    public void openCam(View v){

        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       // File photoFile = null;
        try {
            mCameraFile = createImageFile();
        } catch (IOException ex) {}

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && mCameraFile!=null) {//7.0及以上
                Uri uriForFile = FileProvider.getUriForFile(this, "com.example.yixin.tessocrdemo.fileprovider", mCameraFile);
                intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
                intentFromCapture.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intentFromCapture.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            } else if(mCameraFile!=null){
                intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCameraFile));
            }

        startActivityForResult(intentFromCapture, TAKE_PHOTO);

    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public void openAlbum(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, CHOOSE_PHOTO);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
//            Uri selectedImage = FileProvider.getUriForFile(this, "com.example.yixin.tessocrdemo.fileprovider", mCameraFile);
//            String[] filePathColumns = {MediaStore.Images.Media.DATA};
//            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
//            c.moveToFirst();
//            int columnIndex=-1;
//            for(int i=0;i<filePathColumns.length;i++){
//                columnIndex = c.getColumnIndex(filePathColumns[i]);
//            }
//
//            String imagePath = c.getString(columnIndex);
            FileInputStream stream;
            try{
                copyFile(mCameraFile.getPath(),rootPath+"test1.jpg");
                stream=new FileInputStream(mCameraFile);

                Bitmap bmp=BitmapFactory.decodeStream(stream);
                picture.setImageBitmap(bmp);
            }
            catch (Exception ex){}
            //showImage(imagePath);
            //c.close();
        }
        //获取图片路径
//       else if ((requestCode == CHOOSE_PHOTO) && resultCode == Activity.RESULT_OK && data != null) {
//            Uri selectedImage = data.getData();
//            String[] filePathColumns = {MediaStore.Images.Media.DATA};
//            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
//            c.moveToFirst();
//            int columnIndex = c.getColumnIndex(filePathColumns[0]);
//            String imagePath = c.getString(columnIndex);
//            showImage(imagePath);
//            c.close();
//        }
//      else   if(requestCode==TAKE_PHOTO){
//            Uri inputUri;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                //File file=new File(GetImagePath.getPath(getContext(), data.getData()));
//                 inputUri = FileProvider.getUriForFile(this, "com.example.yixin.tessocrdemo.fileprovider", mCameraFile);//通过FileProvider创建一个content类型的Uri
//
//            } else {
//                 inputUri = Uri.fromFile(mCameraFile);
//                //startPhotoZoom(inputUri);
//            }
//            String[] filePathColumns = {MediaStore.Images.Media.DATA};
//            Cursor c = getContentResolver().query(inputUri, filePathColumns, null, null, null);
//            c.moveToFirst();
//            int columnIndex = c.getColumnIndex(filePathColumns[0]);
//            String imagePath = c.getString(columnIndex);
//            showImage(imagePath);
//            c.close();
//        }
        //if(requestCode==)

    }



    //加载图片
    private void showImage(String imaePath){
        Bitmap bm = BitmapFactory.decodeFile(imaePath);
        picture.setImageBitmap(bm);
    }
    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }
        catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }
}

