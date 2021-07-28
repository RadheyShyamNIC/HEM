package in.nic.hem.mypkg;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class ImageOperation {
    private int ivWidth = 0;
    private int ivHeight = 0;
    public Bitmap CompressResizeImage(Bitmap bm)
    {
        int bmWidth = bm.getWidth();
        int bmHeight = bm.getHeight();
        computeCompressionRatio(bmWidth,bmHeight);
        Bitmap newbitMap = Bitmap.createScaledBitmap(bm, ivWidth, ivHeight, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        newbitMap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        Bitmap bm1 = BitmapFactory.decodeByteArray(b, 0, b.length);
        return bm1;
    }
    private Bitmap ByteArrayToBitmap(byte[] byteArray)
    {
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
        return  bitmap;
    }
    private byte[] BitmapToByteArray(Bitmap bitmap)
    {
        byte[] bytes = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        bytes = byteArrayOutputStream.toByteArray();
        return bytes;
    }
    private void computeCompressionRatio(int bmWidth,int bmHeight){
        int MAXSIZE = 700;//pixels
        int ivWidth = bmWidth;
        int ivHeight = bmHeight;
        double ratio = 1;
        if(bmWidth<bmHeight){
            ratio = (1.0 * bmHeight)/MAXSIZE;
        }else{
            ratio = (1.0 * bmWidth)/MAXSIZE;
        }
        ivWidth = (int) (bmWidth / ratio);
        ivHeight = (int) (bmHeight / ratio);
    }
}
