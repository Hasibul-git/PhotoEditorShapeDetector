package com.e.photoeditor;

import static org.opencv.imgproc.Imgproc.cvtColor;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.ImageView;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import java.io.FileInputStream;

public class ObjectDetectionActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    ImageView imageView;
    Bitmap bitmap = null;

//    public static final String TAG = "src";
//
//    static {
//        if (!OpenCVLoader.initDebug()) {
//            Log.wtf(TAG, "OpenCV failed to load!");
//        }
//    }
//
//    private JavaCameraView cameraView;
//    CameraBridgeViewBase cameraBridgeViewBase;
//
//    private BaseLoaderCallback loaderCallback = new BaseLoaderCallback(this) {
//        @Override
//        public void onManagerConnected(int status) {
//            switch (status) {
//                case BaseLoaderCallback.SUCCESS:
//                    Log.i(TAG, "OpenCV loaded successfully");
//                    cameraBridgeViewBase.enableView();
//                    break;
//                default:
//                    super.onManagerConnected(status);
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_detection);

        imageView = findViewById(R.id.imageView);

//        cameraBridgeViewBase = findViewById(R.id.cameraView);
//        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
//        cameraBridgeViewBase.setCvCameraViewListener(this);

        if(OpenCVLoader.initDebug()){
            Log.d("Open_CV", "OpenCv Status True");
            recieveBitmap();
            detectCircle2();
        }
        else {
            Log.d("Open_CV", "OpenCv Status False");
        }

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

        //imageView.setImageBitmap(bitmap);
    }

    private void detectCircle2(){
        try{
            int centercount = 0;
            Mat input = new Mat();
            Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            Utils.bitmapToMat(bmp32, input);

            Imgproc.cvtColor(input, input, Imgproc.COLOR_BGR2GRAY,0);
            //Imgproc.cvtColor(input, input, Imgproc.COLOR_GRAY2RGBA, 4);

            //Mat circles = new Mat();
            Mat circles = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC1);

            Imgproc.blur(input, input, new Size(3, 3), new Point(2, 2));
            Imgproc.HoughCircles(input, circles, Imgproc.CV_HOUGH_GRADIENT, 2, 100, 100, 90, 0, 0);
            //Imgproc.GaussianBlur(input, input, new Size(3, 3), 5, 10);
            //Canny(input, circles, 80, 100 * 0.5, 3, true);

            int numberOfCircles = (circles.rows() == 0) ? 0 : circles.cols();

            Log.i("numberOfCircles", numberOfCircles+"");

            //Log.i("Circles", String.valueOf("size: " + circles.cols()) + ", " + String.valueOf(circles.rows()));

            if (circles.cols() > 0) {
                for (int x=0; x < numberOfCircles; x++ ) { //Math.min(circles.cols(), 500)
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

            //circles.release();
            //input.release();


//        Mat mat = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC1);
//        Mat input = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC1);
//
//        Utils.bitmapToMat(bitmap, mat);
//
//        /* convert to grayscale */
//        int colorChannels = (mat.channels() == 3) ? Imgproc.COLOR_BGR2GRAY: ((mat.channels() == 4) ? Imgproc.COLOR_BGRA2GRAY : 1);
//
//        Imgproc.cvtColor(mat, input, colorChannels);
//
//        //Mat input = bitmap.gray();
//
//        Mat srcMat = new Mat();
//        Utils.bitmapToMat(bitmap,srcMat);
//
//        Mat mGray = new Mat();
//        cvtColor(srcMat, mGray, Imgproc.COLOR_BGR2GRAY, 1);
//        Imgproc.GaussianBlur(mGray, mGray, new Size(3, 3), 5, 10);
//        Canny(srcMat, mGray, 80, 100 * 0.5, 3, true); // edge detection using canny edge detection algorithm
//
//        List<MatOfPoint> contours = new ArrayList<>();
//        Mat hierarchy = new Mat();
//        Imgproc.findContours(srcMat,contours,hierarchy,Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
//
//        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++){
//            Imgproc.drawContours(srcMat, contours, contourIdx, new Scalar(0, 0, 255),-1);
//        }

            Utils.matToBitmap(input, bitmap);

            imageView.setImageBitmap(bitmap);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void detectCircle(){
        try{
            /* convert bitmap to mat */
            Mat mat = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC1);
            Mat grayMat = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC1);

            Utils.bitmapToMat(bitmap, mat);

            /* convert to grayscale */
            int colorChannels = (mat.channels() == 3) ? Imgproc.COLOR_BGR2GRAY: ((mat.channels() == 4) ? Imgproc.COLOR_BGRA2GRAY : 1);

            cvtColor(mat, grayMat, colorChannels);

            /* reduce the noise so we avoid false circle detection */
            Imgproc.GaussianBlur(grayMat, grayMat, new Size(9, 9), 2, 2);

            // accumulator value
            double dp = 1.2d;
            // minimum distance between the center coordinates of detected circles in pixels
            double minDist = 100;

            // min and max radii (set these values as you desire)
            int minRadius = 0, maxRadius = 0;

            // param1 = gradient value used to handle edge detection
            // param2 = Accumulator threshold value for the
            // cv2.CV_HOUGH_GRADIENT method.
            // The smaller the threshold is, the more circles will be
            // detected (including false circles).
            // The larger the threshold is, the more circles will
            // potentially be returned.
            double param1 = 70, param2 = 72;

            /* create a Mat object to store the circles detected */
            Mat circles = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC1);

            /* find the circle in the image */
            Imgproc.HoughCircles(grayMat, circles, Imgproc.CV_HOUGH_GRADIENT, dp, minDist, param1, param2, minRadius, maxRadius);

            /* get the number of circles detected */
            int numberOfCircles = (circles.rows() == 0) ? 0 : circles.cols();

            Log.e("numberOfCircles",numberOfCircles+"");

            /* draw the circles found on the image */
            for (int i=0; i<numberOfCircles; i++) {
                /* get the circle details, circleCoordinates[0, 1, 2] = (x,y,r)
                 * (x,y) are the coordinates of the circle's center
                 */
                double circleCoordinates[] = circles.get(0, i);

                if (circleCoordinates == null) {
                    break;
                }

                int x = (int) circleCoordinates[0], y = (int) circleCoordinates[1];

                Point center = new Point(x, y);

                int radius = (int) circleCoordinates[2];

                /* circle's outline */
                Imgproc.circle(grayMat, center, radius, new Scalar(0, 255, 0), 4);

                /* circle's center outline */
                Imgproc.rectangle(grayMat, new Point(x - 5, y - 5), new Point(x + 5, y + 5), new Scalar(0, 128, 255), -1);
            }

            /* convert back to bitmap */
            Utils.matToBitmap(grayMat, bitmap);
            imageView.setImageBitmap(bitmap);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat input = inputFrame.gray();
        Mat circles = new Mat();
        Imgproc.blur(input, input, new Size(7, 7), new Point(2, 2));
        Imgproc.HoughCircles(input, circles, Imgproc.CV_HOUGH_GRADIENT, 2, 100, 100, 90, 0, 1000);

        Log.i("SRC", String.valueOf("size: " + circles.cols()) + ", " + String.valueOf(circles.rows()));

        if (circles.cols() > 0) {
            for (int x=0; x < Math.min(circles.cols(), 5); x++ ) {
                double circleVec[] = circles.get(0, x);

                if (circleVec == null) {
                    break;
                }

                Point center = new Point((int) circleVec[0], (int) circleVec[1]);
                int radius = (int) circleVec[2];

                Imgproc.circle(input, center, 3, new Scalar(255, 255, 255), 5);
                Imgproc.circle(input, center, radius, new Scalar(255, 255, 255), 2);
            }
        }

        circles.release();
        input.release();
        return inputFrame.rgba();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        //OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_3_0, this, loaderCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (cameraView != null)
//            cameraView.disableView();
    }
}