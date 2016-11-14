package com.abstractx1.mydiary;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    public Animation scaleAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale);
        Button cameraButton = (Button) findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(this);
        cameraButton.setOnTouchListener(this);

        if(!this.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            ButtonHelper.disable(cameraButton);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cameraButton:
                view.startAnimation(scaleAnimation);
                try {
                    dispatchTakePictureIntent();
                } catch (Exception e) {

                    Toast.makeText(this, "An error occurred while taking the photo." + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                view.invalidate();
                break;
            }
            case MotionEvent.ACTION_UP: {
                view.getBackground().clearColorFilter();
                view.invalidate();
                break;
            }
        }
        return false;
    }

    //http://stackoverflow.com/questions/7720383/camera-intent-not-saving-photo
    static final int REQUEST_IMAGE_CAPTURE = 1;
    // your authority, must be the same as in your manifest file
    private static final String CAPTURE_IMAGE_FILE_PROVIDER = "com.abstractx1.fileprovider";
private String capturedImagePath;

    private void dispatchTakePictureIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go

            File photoFile = Utilities.createFile("image", ".jpg", getCacheDir());
            //capturedImagePath = photoFile.getAbsolutePath();
            Uri photoURI = FileProvider.getUriForFile(this, CAPTURE_IMAGE_FILE_PROVIDER, photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void showDebugDialog() {
        AlertDialog.Builder builder = new DebugDialogBuilder(this);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            String filePath = getCacheDir()+"/image.jpg";
            try {
                Bitmap bmp = BitmapFactory.decodeFile(filePath);
                ExifInterface exif = new ExifInterface(filePath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                Matrix matrix = new Matrix();
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                        break;
                    default:
                        break;
                }
                bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true); // rotating bitmap
                //((ImageView) findViewById(R.id.screenShotImageView)).setImageURI(Uri.fromFile(file));
                ImageView thumbnail = (ImageView) findViewById(R.id.screenShotImageView);
                thumbnail.setImageBitmap(bmp);
                thumbnail.invalidate();
            } catch (IOException e) {
                Toast.makeText(this, "An error occurred reading the photo file:" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if(getResources().getBoolean(R.bool.debug_mode))
            inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.debug:
                showDebugDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
