package com.e.photoeditor.BaseClass;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.ImageView;

import com.e.photoeditor.R;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ServicesClass.ServicesClass;

public class ImageCroper extends AppCompatActivity {

    Button btnBack, btnDone;
    ImageView imageView;
    Uri imgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_croper);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        btnBack = findViewById(R.id.btnBack);
        btnDone = findViewById(R.id.btnDone);
        imageView = findViewById(R.id.image_view);

        Uri uri = (Uri) getIntent().getParcelableExtra("uri");

        CropImage.activity(uri).start(this);

        btnDone.setOnClickListener(view -> {
            //CropImage.activity(imgUri).start(this);
            ServicesClass.imagesavetomyphonegallery(imageView, "SaveImages");

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("ImgUri", imgUri);
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                imgUri = result.getUri();
                imageView.setImageURI(imgUri);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        imgUri = null;
    }
}