package koplac.vyskovnice.tools;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Manipulates properties of images
 * @author Antonio Manuel Sanchez Amat
 * @author Juan Jose Espinosa Sanchez
 */
public class ImageManipulation {

    /**
     * Check orientation of current image
     * @param photoPath Path of the current image
     * @param yourSelectedImage Current image
     * @return Rotated image
     */
    public static Bitmap getOrientation(String photoPath, Bitmap yourSelectedImage) {
        ExifInterface ei = null;
        Bitmap bitmap = null;
        try {
            ei = new ExifInterface(photoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                bitmap = rotateImage(yourSelectedImage, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                bitmap = rotateImage(yourSelectedImage, 180);
                break;
            case ExifInterface.ORIENTATION_NORMAL:
                bitmap = rotateImage(yourSelectedImage, 0);
                break;
            case ExifInterface.ORIENTATION_UNDEFINED:
                bitmap = rotateImage(yourSelectedImage, 0);
                break;
        }
        return bitmap;
    }

    /**
     * Coverts current image to bytes array
     * @param image current image
     * @return bytes array with current image encoded
     */
    public static byte[] bitmapToArray(Bitmap image) {
        byte[] bytes;
        Bitmap bmp;

        bytes=null;
        if(image!=null){
            bmp = image;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            bytes = stream.toByteArray();
        }

        return bytes;
    }

    /**
     * Resized current image and rotates it
     * @param source Current image
     * @param angle Current angle to rotate
     * @return Manipulated image
     */
    public static Bitmap rotateImage(Bitmap source, float angle) {
        int original_width = source.getWidth();
        int original_height = source.getHeight();
        int bound_width = 150;
        int bound_height =150;
        int new_width = original_width;
        int new_height = original_height;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap image = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        // first check if we need to scale width
        if (original_width > bound_width) {
            //scale width to fit
            new_width = bound_width;
            //scale height to maintain aspect ratio
            new_height = (new_width * original_height) / original_width;
        }
        // then check if we need to scale even with the new height
        if (new_height > bound_height) {
            //scale height to fit instead
            new_height = bound_height;
            //scale width to maintain aspect ratio
            new_width = (new_height * original_width) / original_height;
        }

        return Bitmap.createScaledBitmap(image, new_width, new_height, true);
    }
}
