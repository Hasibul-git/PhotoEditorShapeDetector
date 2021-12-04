package com.e.photoeditor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button btnOpen, btnGallery, btnCrop, btnTools;
    private static final int PICK_IMAGE = 1;
    Uri imageUri = null;
    Bitmap bitmap;
    String fileName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image_view);
        btnOpen = findViewById(R.id.btnOpen);
        btnGallery = findViewById(R.id.btnGallery);
        btnCrop = findViewById(R.id.btnCrop);
        btnTools = findViewById(R.id.btnTool);

        if (getIntent().getExtras() != null){
            imageUri = (Uri) getIntent().getParcelableExtra("ImgUri");
            imageView.setImageURI(imageUri);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.CAMERA
            },100);
        }

        btnOpen.setOnClickListener(view -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent,100);
        });

        btnGallery.setOnClickListener(view -> {
            uploadImage();
        });

        btnCrop.setOnClickListener(view -> {
            try {

                Intent intent = new Intent(this, ImageCroper.class);
                intent.putExtra("uri",imageUri);
                startActivity(intent);

            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        btnTools.setOnClickListener(view -> {
//            Intent in1 = new Intent(this, ObjectDetectionActivity.class);
//            startActivity(in1);

            sentBitmapFile();
        });
    }

    private void uploadImage(){
            Intent gallery = new Intent();
            gallery.setType("image/*");
            gallery.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(gallery, "Sellect Photo"), PICK_IMAGE);
    }

    private void importAndCropImage(){
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this);
    }

    private void sentBitmapFile() {

        try{
            //Write file
            String filename = "bitmap.png";
            FileOutputStream stream = this.openFileOutput(filename, this.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            //Cleanup
            stream.close();
            bitmap.recycle();

            //Pop intent
            Intent in1 = new Intent(this, ObjectDetectionActivity.class);
            in1.putExtra("image", filename);
            startActivity(in1);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 100){
            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            imageUri = getImageUri(this, bitmap);
            //imagesavetomyphonegallery();
        }
        else if (requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            imageUri = data.getData();
            try{

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                imageView.setImageBitmap(bitmap);

                Log.i("Image_Uri",imageUri.toString());

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                imageUri = result.getUri();
                imageView.setImageURI(imageUri);
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void imagesavetomyphonegallery() {

        BitmapDrawable draw = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = draw.getBitmap();

        FileOutputStream outStream = null;
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + "/SaveImages");
        dir.mkdirs();
        fileName = String.format("%d.jpg", System.currentTimeMillis());

        Log.i("Image Saved",dir.toString());

        File outFile = new File(dir, fileName);
        try {
            outStream = new FileOutputStream(outFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
        try {
            outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //imageUri = (Uri) getIntent().getParcelableExtra("ImgUri");
        //imageView.setImageURI(imageUri);
    }
}