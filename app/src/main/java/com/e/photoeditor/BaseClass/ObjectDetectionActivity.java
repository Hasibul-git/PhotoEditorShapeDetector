package com.e.photoeditor.BaseClass;

import static org.opencv.imgproc.Imgproc.cvtColor;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.e.photoeditor.BaseClass.RealTimeObjectDetection.RealTimeCircleDetectionActivity;
import com.e.photoeditor.R;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import java.io.FileInputStream;

public class ObjectDetectionActivity extends AppCompatActivity {

    ImageView imageView;
    Bitmap bitmap = null;
    Button btnLiveImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_detection);

        imageView = findViewById(R.id.imageView);
        btnLiveImage = findViewById(R.id.btnLiveImage);

        if(OpenCVLoader.initDebug()){
            Log.d("Open_CV", "OpenCv Status True");
            recieveBitmap();
            detectCircle2();
        }
        else {
            Log.d("Open_CV", "OpenCv Status False");
        }

        btnLiveImage.setOnClickListener(view -> {
            Intent intent = new Intent(this, RealTimeCircleDetectionActivity.class);
            startActivity(intent);
        });
    }

    private void recieveBitmap(){
        String filename = getIntent().getStringExtra("image");
        try {
            FileInputStream is = this.openFileInput(filename);
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void detectCircle2(){
        try{
            int centercount = 0;
            Mat input = new Mat();
            Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            Utils.bitmapToMat(bmp32, input);

            Imgproc.cvtColor(input, input, Imgproc.COLOR_BGR2GRAY,0);

            Mat circles = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC1);

            Imgproc.blur(input, input, new Size(3, 3), new Point(2, 2));
            Imgproc.HoughCircles(input, circles, Imgproc.CV_HOUGH_GRADIENT, 2, 100, 100, 90, 0, 1000);

            int numberOfCircles = (circles.rows() == 0) ? 0 : circles.cols();

            Log.i("numberOfCircles", numberOfCircles+"");

            if (circles.cols() > 0) {
                for (int x=0; x < Math.min(circles.cols(),100); x++ ) { //Math.min(circles.cols(), 500)
                    double circleVec[] = circles.get(0, x);

                    if (circleVec == null) {
                        break;
                    }

                    Point center = new Point((int) circleVec[0], (int) circleVec[1]);
                    int radius = (int) circleVec[2];

                    centercount++;

                    //Imgproc.circle(input, center, 3, new Scalar(255, 255, 255), 5);
                    Imgproc.circle(input, center, radius, new Scalar(255, 255, 255), 2);

                    //Imgproc.rectangle(input, new Point((int) circleVec[0] - 5, (int) circleVec[1] - 5), new Point((int) circleVec[0] + 5, (int) circleVec[1] + 5), new Scalar(0, 128, 255), -1);
                }
            }

            Log.i("Centers", centercount+"");

            centercount = 0;

            Utils.matToBitmap(input, bitmap);

            imageView.setImageBitmap(bitmap);

            Toast.makeText(this,"Number of Circle Detected is "+centercount,Toast.LENGTH_LONG).show();

        }catch (Exception e){
            Toast.makeText(this,"Detection is not possible due to poor Image quality!!",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}