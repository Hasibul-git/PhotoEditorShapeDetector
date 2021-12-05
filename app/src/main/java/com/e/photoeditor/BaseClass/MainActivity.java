package com.e.photoeditor.BaseClass;

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

import com.e.photoeditor.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ServicesClass.ServicesClass;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button btnOpen, btnGallery, btnCrop, btnTools, btnAIFilter;
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
        btnAIFilter = findViewById(R.id.btnliveFilter);

        if (getIntent().getExtras() != null){
            imageUri = (Uri) getIntent().getParcelableExtra("ImgUri");
            imageView.setImageURI(imageUri);

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
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
            sentBitmapFile();
        });

        btnAIFilter.setOnClickListener(view -> {
            Intent in1 = new Intent(this, DetectActivity.class);
            startActivity(in1);
        });
    }

    private void uploadImage(){
            Intent gallery = new Intent();
            gallery.setType("image/*");
            gallery.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(gallery, "Sellect Photo"), PICK_IMAGE);
    }
    private void sentBitmapFile() {
        try{
            //Write file
            String filename = ServicesClass.getFilePath(this, bitmap);
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

    @Override
    protected void onResume() {
        super.onResume();
    }
}