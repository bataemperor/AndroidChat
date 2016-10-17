package com.tehnicomsoft.androidtest.utility;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Bata on 17.10.2016..
 */

public class Utility {

    public static Bitmap getBitmap(String uri, Context mContext) {

        Bitmap bitmap = null;
        Uri actualUri = Uri.parse(uri);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inTempStorage = new byte[16 * 1024];
        options.inSampleSize = 2;
        ContentResolver cr = mContext.getContentResolver();
        float degree = 0;
        try {
            ExifInterface exif = new ExifInterface(actualUri.getPath());
            String exifOrientation = exif
                    .getAttribute(ExifInterface.TAG_ORIENTATION);
            bitmap = BitmapFactory.decodeStream(cr.openInputStream(actualUri),
                    null, options);
            if (bitmap != null) {
                degree = getDegree(exifOrientation);
                if (degree != 0)
                    bitmap = createRotatedBitmap(bitmap, degree);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private static float getDegree(String exifOrientation) {
        float degree = 0;
        if (exifOrientation.equals("6"))
            degree = 90;
        else if (exifOrientation.equals("3"))
            degree = 180;
        else if (exifOrientation.equals("8"))
            degree = 270;
        return degree;
    }

    private static Bitmap createRotatedBitmap(Bitmap bm, float degree) {
        Bitmap bitmap = null;
        if (degree != 0) {
            Matrix matrix = new Matrix();
            matrix.preRotate(degree);
            bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                    bm.getHeight(), matrix, true);
        }

        return bitmap;
    }
}
