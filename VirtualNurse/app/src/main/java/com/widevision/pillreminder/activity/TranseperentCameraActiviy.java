package com.widevision.pillreminder.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Window;

import com.widevision.pillreminder.util.PreferenceConnector;

import java.io.File;
import java.io.IOException;

/**
 * Created by mercury-one on 19/12/15.
 */
public class TranseperentCameraActiviy extends Activity {
    Uri mUri;
    Bitmap bt;
    private static final int PICK_IMAGE_REQUEST = 2;
    private static final int REQUEST_TAKE_PHOTO = 3;
    private String mCurrentPhotoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photoFile));
                mUri = Uri.fromFile(photoFile);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }

    }
    private File createImageFile() throws IOException {

        String imageFileName = "JPEG_"  + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg",storageDir);

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                PreferenceConnector.writeString(getApplicationContext(), "imagepath", mCurrentPhotoPath);
                TranseperentCameraActiviy.this.finish();
            }
            else{
                PreferenceConnector.writeString(getApplicationContext(), "imagepath", "");
                TranseperentCameraActiviy.this.finish();
            }
        }


    }

}
