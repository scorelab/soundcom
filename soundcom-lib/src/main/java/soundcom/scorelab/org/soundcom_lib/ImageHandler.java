package soundcom.scorelab.org.soundcom_lib;
import java.io.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;


public class ImageHandler {

    private static  ImageView imagePath;
    private static   Bitmap image;
    private static  ByteArrayOutputStream baos;
    private static  byte[] byteImage;
    private static  int  [] bitImage;
    private static  int data_size;

    public ImageHandler () {
    }

    public ImageHandler(ImageView imagePath) {
        this.imagePath = imagePath;
        readImage();
        }

    public void readImage(){

        try {
            image = ((BitmapDrawable)imagePath.getDrawable()).getBitmap();
            getBytesFromBitmap(image); //get bytes
            bitImage = new int[byteImage.length*8];
            data_size = byteImage.length*8;
            bitImage = getBitsFromBytes(byteImage);


        } catch (Exception e) {
            Log.d("RedTooth", e.toString());
        }


    }
    public void getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byteImage = new byte [baos.size()];
        byteImage = baos.toByteArray();
        try {
            baos.close();
        }catch (Exception e){
            Log.d("RedTooth",e.toString());
        }
    }
    public int[] getBitsFromBytes (byte[] bytes){
        int [] b = new int[bytes.length*8];
        for (int n =0; n<bytes.length; n++){
            // Use ints to avoid any possible confusion due to signed byte values
            int sourceByte = 0xFF & (int)(bytes[n]);  // Convert byte to unsigned int
            int mask = 0x80;
            for (int i = 0; i < 8; i++) {
                int maskResult = sourceByte & mask;  // Extract the single bit
                if (maskResult != 0) {
                    b[8*n + i] = 1;
                }
                else {
                    b[8*n + 1] = 0;  // Unnecessary since array is inited to zero but good documention
                }
                mask = mask >> 1;
            }
        }

        return b;

    }

    public int[] getBitImage() {
        return bitImage;
    }

    public int getData_size() {
        return data_size;
    }
}
