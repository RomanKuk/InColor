package com.example.incolor.ui.color_models;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.incolor.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class ColorModelsFragment extends Fragment implements View.OnClickListener {

    private static final int CAMERA_IMAGE_REQUEST_CODE = 1000;

    private ImageView imgPhoto;
    private ImageView imgColor;
    private SeekBar redSeekBar;
    private SeekBar greenSeekBar;
    private SeekBar blueSeekBar;
    private TextView txtRedColorValue;
    private TextView txtGreenColorValue;
    private TextView txtBlueColorValue;
    private TextView txtHex;
    private TextView txtCmyk;
    private TextView txtHsl;
    private TextView txtHsv;
    private LinearLayout bars;
    private LinearLayout buttons;
    private ConstraintLayout colorValues;

    private Bitmap bitmap;
    private Uri imgUri;
    private Uri tempImgUri;
    private File photo;
    private File tempPhoto;

    private ExecutorService threads;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_color_models, container, false);
    }

    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ImageButton btnTakeAPicture = Objects.requireNonNull(getView()).
                findViewById(R.id.btnTakePicture);
        ImageButton btnSaveImg = getView().findViewById(R.id.btnSave);
        ImageButton btnShareImg = getView().findViewById(R.id.btnShare);
        imgPhoto = getView().findViewById(R.id.imgPhoto);
        imgColor = getView().findViewById(R.id.imgColor);
        redSeekBar = getView().findViewById(R.id.redColorSeekBar);
        greenSeekBar = getView().findViewById(R.id.greenColorSeekBar);
        blueSeekBar = getView().findViewById(R.id.blueColorSeekBar);
        txtRedColorValue = getView().findViewById(R.id.txtRedColorValue);
        txtGreenColorValue = getView().findViewById(R.id.txtGreenColorValue);
        txtBlueColorValue = getView().findViewById(R.id.txtBlueColorValue);
        txtHex = getView().findViewById(R.id.txtHexColor);
        txtCmyk = getView().findViewById(R.id.txtCmykColor);
        txtHsl = getView().findViewById(R.id.txtHslColor);
        txtHsv = getView().findViewById(R.id.txtHsvColor);
        bars = getView().findViewById(R.id.barsLayout);
        buttons = getView().findViewById(R.id.buttonsLayout);
        colorValues = getView().findViewById(R.id.valuesLayout);

        bitmap = null;
        imgUri = null;
        tempImgUri = null;
        photo = null;
        tempPhoto = null;

        btnTakeAPicture.setOnClickListener(ColorModelsFragment.this);
        btnSaveImg.setOnClickListener(ColorModelsFragment.this);
        btnShareImg.setOnClickListener(ColorModelsFragment.this);

        ColorizationHandler colorizationHandler = new ColorizationHandler();
        threads = Executors.newCachedThreadPool();

        redSeekBar.setOnSeekBarChangeListener(colorizationHandler);
        greenSeekBar.setOnSeekBarChangeListener(colorizationHandler);
        blueSeekBar.setOnSeekBarChangeListener(colorizationHandler);

        clearValues();
        bars.setVisibility(View.INVISIBLE);
        buttons.setVisibility(View.INVISIBLE);
        colorValues.setVisibility(View.INVISIBLE);

        if (bitmap == null) {
            capturePhoto();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        deletePhoto(photo);
    }

    private void deletePhoto(File image) {
        if (image != null) {
            if (image.exists()) {
                Objects.requireNonNull(getActivity()).
                        getApplicationContext().deleteFile(image.getName());
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void clearValues() {
        redSeekBar.setProgress(0);
        greenSeekBar.setProgress(0);
        blueSeekBar.setProgress(0);
        txtRedColorValue.setText("-");
        txtGreenColorValue.setText("-");
        txtBlueColorValue.setText("-");

        imgColor.setColorFilter(Color.WHITE);
        txtHex.setText("#FFFFFF");
        //txtCmyk.setText("rgb("+redProgress+", "+greenProgress+", "+blueProgress+")");
        txtCmyk.setText("CMYK(" + 0 + "%, " + 0 + "%, " + 0 + "%, " + 0 + "%)");
        txtHsv.setText("HSV(" + 0 + "º, " + 0 + "%, " + 100 + "%)");
        txtHsl.setText("HSL(" + 0 + "º, " + 0 + "%, " + 100 + "%)");
    }

    private void capturePhoto() {
        int cameraPermission = ContextCompat.checkSelfPermission(
                Objects.requireNonNull(getActivity()), Manifest.permission.CAMERA);
        if (cameraPermission == PackageManager.PERMISSION_GRANTED) {
            int writePermission = ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (writePermission == PackageManager.PERMISSION_GRANTED) {
                PackageManager packageManager = getActivity().getPackageManager();
                if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        tempImgUri = imgUri;
                        tempPhoto = photo;
                        photo = photoFile;
                        imgUri = FileProvider.getUriForFile(getActivity(),
                                getActivity().getApplicationContext().getPackageName()
                                        + ".provider", photoFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (photoFile != null) {
                        final Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                        startActivityForResult(cameraIntent, CAMERA_IMAGE_REQUEST_CODE);

                    } else {
                        Toast.makeText(getActivity(),
                                "Photo is not captured!",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {

                    Toast.makeText(getActivity(),
                            "Your device does not have a camera",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        2000);
            }
        } else {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnTakePicture) {
            capturePhoto();
        } else if (view.getId() == R.id.btnSave) {

            int permissionCheck = ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

                threads.submit(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap changedImg = changeFilter(bitmap, imgPhoto.getColorFilter());
                        try {
                            SaveFile.saveFile(getActivity(), changedImg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Toast.makeText(getActivity(), "The image is successfully " +
                        "saved to External Storage", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        2000);
            }

        } else if (view.getId() == R.id.btnShare) {

            Intent shareIntent = new Intent(Intent.ACTION_SEND);

            Bitmap changedImg = changeFilter(bitmap, imgPhoto.getColorFilter());
            File myPictureFile = null;
            try {
                myPictureFile = SaveFile.saveFile(getActivity(), changedImg);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Uri myUri = Uri.fromFile(myPictureFile);

            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT,
                    "This picture is sent from InColor");
            shareIntent.putExtra(Intent.EXTRA_STREAM, myUri);

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            startActivity(Intent.createChooser(shareIntent,
                    "Let's share your image with others"));
        }
    }

    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + "_";
        File filepath = Objects.requireNonNull(getActivity()).getCacheDir();
        File dir = null;
        if (filepath != null) {
            dir = new File(filepath.getAbsolutePath());
        }

        return File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                dir      // directory
        );
    }

    private Bitmap changeFilter(Bitmap original, ColorFilter filter) {
        Bitmap bitmap = Bitmap.createBitmap(original.getWidth(),
                original.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColorFilter(filter);
        canvas.drawBitmap(original, 0, 0, paint);

        return bitmap;
    }

    private void checkRotation() throws IOException {
        InputStream in = Objects.requireNonNull(getActivity()).
                getContentResolver().openInputStream(imgUri);
        ExifInterface ei = null;
        if (in != null) {
            ei = new ExifInterface(in);
        }
        int orientation = 0;
        if (ei != null) {
            orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
        }

        switch (orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                bitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                bitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                bitmap = rotateImage(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                deletePhoto(tempPhoto);

                imgPhoto.clearColorFilter();
                bars.setVisibility(View.VISIBLE);
                buttons.setVisibility(View.VISIBLE);
                colorValues.setVisibility(View.VISIBLE);
                clearValues();

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(
                            Objects.requireNonNull(getActivity()).getContentResolver(), imgUri);
                    checkRotation();
                    imgPhoto.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (bitmap != null) {
                    try {
                        bitmap = changeFilter(bitmap, imgPhoto.getColorFilter());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }


                imgPhoto.setImageBitmap(bitmap);

            } else if (resultCode == RESULT_CANCELED) {

                deletePhoto(photo);
                photo = tempPhoto;
                imgUri = tempImgUri;

                if (bitmap == null) {
                    NavHostFragment.findNavController(ColorModelsFragment.this)
                            .navigate(R.id.action_ColorModelsFragment_to_StartColorModelsFragment);

                    clearValues();
                    bars.setVisibility(View.INVISIBLE);
                    buttons.setVisibility(View.INVISIBLE);
                    colorValues.setVisibility(View.INVISIBLE);
                }
            }
        }
    }


    public class ColorizationHandler implements SeekBar.OnSeekBarChangeListener {

        int redProgress;
        int greenProgress;
        int blueProgress;
        String hexColor;
        int[] hsv;
        int[] hsl;
        int[] cmyk;

        public ColorizationHandler() {
            redProgress = 0;
            greenProgress = 0;
            blueProgress = 0;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            if (fromUser) {
                if (seekBar == redSeekBar) {
                    redProgress = progress;
                    txtRedColorValue.setText(progress + "");
                } else if (seekBar == greenSeekBar) {
                    greenProgress = progress;
                    txtGreenColorValue.setText(progress + "");
                } else if (seekBar == blueSeekBar) {
                    blueProgress = progress;
                    txtBlueColorValue.setText(progress + "");
                }
            }

            imgColor.setColorFilter(Color.rgb(redProgress, greenProgress, blueProgress));
            hsv = Conversions.rgbToHsv(redProgress, greenProgress, blueProgress);
            hsl = Conversions.rgbToHsl(redProgress, greenProgress, blueProgress);
            cmyk = Conversions.rgbToCmyk(redProgress, greenProgress, blueProgress);
            hexColor = Conversions.toHex(redProgress, greenProgress, blueProgress);
            imgPhoto.setColorFilter(Color.rgb(redProgress, greenProgress, blueProgress),
                    PorterDuff.Mode.MULTIPLY);

            txtHex.setText(hexColor);
            //txtCmyk.setText("rgb("+redProgress+", "+greenProgress+", "+blueProgress+")");
            txtCmyk.setText("CMYK(" + cmyk[0] + "%, " + cmyk[1] + "%, " + cmyk[2] + "%, " + cmyk[3] + "%)");
            txtHsv.setText("HSV(" + hsv[0] + "º, " + hsv[1] + "%, " + hsv[2] + "%)");
            txtHsl.setText("HSL(" + hsl[0] + "º, " + hsl[1] + "%, " + hsl[2] + "%)");


            /*threads.submit(new Runnable() {
                @Override
                public void run() {

                }
            });
*/
        }


        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
