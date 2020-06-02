package com.example.incolor.ui.newton_fractal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class SaveFile {

    static File saveFile(Activity myActivity, Bitmap bitmap) throws IOException {

        String externalStorageState = Environment.getExternalStorageState();
        // myFile = null;
        if (externalStorageState.equals(Environment.MEDIA_MOUNTED)) {

            @SuppressLint("SimpleDateFormat")
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = timeStamp + "_";
            File filepath = myActivity.getExternalFilesDir("NewtonFractals");
            File dir = null;
            if (filepath != null) {
                dir = new File(filepath.getAbsolutePath());
            }

            File photo = File.createTempFile(
                    imageFileName,  // prefix
                    ".jpg",         // suffix
                    dir      // directory
            );

            long remainingSpace = 0;
            if (dir != null) {
                remainingSpace = dir.getFreeSpace();
            }
            long requiredSpace = bitmap.getByteCount();

            if (requiredSpace * 1.8 < remainingSpace) {

                try (OutputStream fileOutputStream = new FileOutputStream(photo)) {
                    boolean isImageSaveWell = bitmap.compress(Bitmap.CompressFormat.JPEG,
                            100, fileOutputStream);
                    if (isImageSaveWell) {
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE, imageFileName);
                        values.put(MediaStore.Images.Media.DESCRIPTION, "Fractal image was made with InColor");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                                    photo.getName().toLowerCase(Locale.US));
                            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                            values.put(MediaStore.Images.ImageColumns.BUCKET_ID,
                                    photo.toString().toLowerCase(Locale.US).hashCode());
                        }
                        values.put("_data", photo.getAbsolutePath());

                        ContentResolver cr = myActivity.getContentResolver();
                        cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                        return photo;
                    } else {
                        throw new IOException("The fractal image is not saved successfully " +
                                "to External Storage");
                    }
                } catch (Exception e) {
                    throw new IOException("The operation of saving the fractal image " +
                            "to External Storage went wrong");
                }

            } else {

                throw new IOException("There is no enough space " +
                        "in order to save the fractal image to External Storage");
            }

        } else {
            throw new IOException("This device does not have an external storage");
        }
    }

}
