package ServicesClass;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.widget.ImageView;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ServicesClass {

    public static String getFilePath(Context context, Bitmap bitmapImg){

        String filename = "bitmap.png";

        try{
            //Write file
            FileOutputStream stream = context.openFileOutput(filename, context.MODE_PRIVATE);
            bitmapImg.compress(Bitmap.CompressFormat.PNG, 100, stream);

            //Cleanup
            stream.close();
            bitmapImg.recycle();

        }catch (Exception e){
            e.printStackTrace();
        }

        return filename;
    }

    public static void imagesavetomyphonegallery(ImageView imageView, String pathName) {

        BitmapDrawable draw = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = draw.getBitmap();

        FileOutputStream outStream = null;
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() +"/"+ pathName);
        dir.mkdirs();
        String fileName = String.format("%d.jpg", System.currentTimeMillis());
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

    public static double angle(Point pt1, Point pt2, Point pt0) {
        double dx1 = pt1.x - pt0.x;
        double dy1 = pt1.y - pt0.y;
        double dx2 = pt2.x - pt0.x;
        double dy2 = pt2.y - pt0.y;
        return (dx1 * dx2 + dy1 * dy2) / Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10);
    }

    public static void setLabel(Mat im, String label, MatOfPoint contour) {
        int fontface = Core.FONT_HERSHEY_SIMPLEX;
        double scale = 3;//0.4;
        int thickness = 3;//1;
        int[] baseline = new int[1];
        Size text = Imgproc.getTextSize(label, fontface, scale, thickness, baseline);
        Rect r = Imgproc.boundingRect(contour);
        Point pt = new Point(r.x + ((r.width - text.width) / 2),r.y + ((r.height + text.height) / 2));
        Imgproc.putText(im, label, pt, fontface, scale, new Scalar(255, 0, 0), thickness);
    }
}
