package com.example.incolor.ui.color_models;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.example.incolor.ui.color_models.RGB.BLUE;
import static com.example.incolor.ui.color_models.RGB.GREEN;
import static com.example.incolor.ui.color_models.RGB.RED;

//import androidx.lifecycle.Observer;
//import androidx.lifecycle.ViewModelProviders;

public class ColorModelsFragment extends Fragment implements View.OnClickListener {

    private static final int CAMERA_IMAGE_REQUEST_CODE = 1000;

    private ImageView imgPhoto;
    private SeekBar redSeekBar;
    private SeekBar greenSeekBar;
    private SeekBar blueSeekBar;
    private TextView txtRedColorValue;
    private TextView txtGreenColorValue;
    private TextView txtBlueColorValue;
    //private ProgressBar progressBar;
    private LinearLayout bars;
    private LinearLayout buttons;
    //private ConstraintLayout screenStart;

    private Bitmap bitmap;
    private Uri imgUri;
    private Uri tempImgUri;
    private File photo;
    private File tempPhoto;

    private Colorful colorful;
    private Handler handler = new Handler();

    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_color_models, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ImageButton btnTakeAPicture = getView().findViewById(R.id.btnTakePicture);
        ImageButton btnSaveImg = getView().findViewById(R.id.btnSave);
        ImageButton btnShareImg = getView().findViewById(R.id.btnShare);
        imgPhoto = getView().findViewById(R.id.imgPhoto);
        redSeekBar = getView().findViewById(R.id.redColorSeekBar);
        greenSeekBar = getView().findViewById(R.id.greenColorSeekBar);
        blueSeekBar = getView().findViewById(R.id.blueColorSeekBar);
        txtRedColorValue = getView().findViewById(R.id.txtRedColorValue);
        txtGreenColorValue = getView().findViewById(R.id.txtGreenColorValue);
        txtBlueColorValue = getView().findViewById(R.id.txtBlueColorValue);
        //progressBar = getView().findViewById(R.id.progressBar);
        bars = getView().findViewById(R.id.barsLayout);
        buttons = getView().findViewById(R.id.buttonsLayout);
        //screenStart = view.findViewById(R.id.startLayout);

        bitmap = null;
        imgUri = null;
        tempImgUri = null;
        photo = null;
        tempPhoto = null;

        btnTakeAPicture.setOnClickListener(ColorModelsFragment.this);
        btnSaveImg.setOnClickListener(ColorModelsFragment.this);
        btnShareImg.setOnClickListener(ColorModelsFragment.this);
        imgPhoto.setOnClickListener(ColorModelsFragment.this);

        ColorizationHandler colorizationHandler = new ColorizationHandler();

        redSeekBar.setOnSeekBarChangeListener(colorizationHandler);
        greenSeekBar.setOnSeekBarChangeListener(colorizationHandler);
        blueSeekBar.setOnSeekBarChangeListener(colorizationHandler);

        redSeekBar.setProgress(0);
        greenSeekBar.setProgress(0);
        blueSeekBar.setProgress(0);

/*        screenStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        if (bitmap == null) {
            capturePhoto();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        File file = photo;
        if (photo != null) {
            file.delete();
            if (file.exists()) {
                try {
                    file.getCanonicalFile().delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (file.exists()) {
                    getActivity().getApplicationContext().deleteFile(file.getName());
                }
            }
        }
    }

    private void capturePhoto() {
        int cameraPermission = ContextCompat.checkSelfPermission(
                getActivity(), Manifest.permission.CAMERA);
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
                        //new ColorfulTask().execute();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                startActivityForResult(cameraIntent, CAMERA_IMAGE_REQUEST_CODE);
                            }
                        }).start();
                        //screenStart.setEnabled(false);
                        //screenStart.setVisibility(View.INVISIBLE);
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
            //new ColorfulTask().execute();
        } else if (view.getId() == R.id.btnSave) {
            int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

                try {
                    SaveFile.saveFile(getActivity(), bitmap);
                    Toast.makeText(getActivity(), "The image is successfully " +
                            "saved to External Storage", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        2000);
            }
        } else if (view.getId() == R.id.btnShare) {

            try {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                File myPictureFile = SaveFile.saveFile(getActivity(), bitmap);
                Uri myUri = Uri.fromFile(myPictureFile);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT,
                        "This picture is sent from Colorfy");
                shareIntent.putExtra(Intent.EXTRA_STREAM, myUri);
                startActivity(Intent.createChooser(shareIntent,
                        "Let's share your image with others"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + "_";
        File filepath = getActivity().getCacheDir();
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

    private void checkRotation() throws IOException {
        InputStream in = getActivity().getContentResolver().openInputStream(imgUri);
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
                if (tempPhoto != null) {
                    File file = tempPhoto;
                    file.delete();
                    if (file.exists()) {
                        try {
                            file.getCanonicalFile().delete();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (file.exists()) {
                            getActivity().getApplicationContext().deleteFile(file.getName());
                        }
                    }
                }

                bars.setVisibility(View.VISIBLE);
                buttons.setVisibility(View.VISIBLE);
                redSeekBar.setProgress(0);
                greenSeekBar.setProgress(0);
                blueSeekBar.setProgress(0);
                txtRedColorValue.setText("-");
                txtGreenColorValue.setText("-");
                txtBlueColorValue.setText("-");

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(
                            getActivity().getContentResolver(), imgUri);
                    checkRotation();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                colorful = new Colorful(bitmap, 0, 0, 0);

                imgPhoto.setImageBitmap(bitmap);
            } else if (resultCode == RESULT_CANCELED) {
                File file = photo;
                file.delete();
                if (file.exists()) {
                    try {
                        file.getCanonicalFile().delete();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (file.exists()) {
                        getActivity().getApplicationContext().deleteFile(file.getName());
                    }
                }
                photo = tempPhoto;
                imgUri = tempImgUri;

                if (bitmap == null) {
                    //screenStart.setEnabled(true);
                    //screenStart.setVisibility(View.VISIBLE);

                    NavHostFragment.findNavController(ColorModelsFragment.this)
                            .navigate(R.id.action_ColorModelsFragment_to_StartColorModelsFragment);

                    bars.setVisibility(View.INVISIBLE);
                    buttons.setVisibility(View.INVISIBLE);
                    redSeekBar.setProgress(0);
                    greenSeekBar.setProgress(0);
                    blueSeekBar.setProgress(0);
                }
            }
        }
    }

    private class ColorizationHandler implements SeekBar.OnSeekBarChangeListener {

        int redProgress = 0;
        int greenProgress = 0;
        int blueProgress = 0;
        RGB activeColor;
        ExecutorService redService;
        ExecutorService greenService;
        ExecutorService blueService;
        //Thread[] threads;


        ColorizationHandler() {
            redProgress = 0;
            greenProgress = 0;
            blueProgress = 0;
            /*hreads = new Thread[3];
            for(Thread temp: threads)
                temp = null;*/
            redService = Executors.newSingleThreadExecutor();
            greenService = Executors.newSingleThreadExecutor();
            blueService = Executors.newSingleThreadExecutor();
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onProgressChanged(final SeekBar seekBar, final int progress, boolean fromUser) {

            if (fromUser) {
                if (seekBar == redSeekBar) {
                    //colorful.setRedColorValue(progress);
                    redProgress = progress;
                    txtRedColorValue.setText(progress + "%");
                    activeColor = RED;
                } else if (seekBar == greenSeekBar) {
                    //colorful.setGreenColorValue(progress);
                    greenProgress = progress;
                    txtGreenColorValue.setText(progress + "%");
                    activeColor = GREEN;
                } else if (seekBar == blueSeekBar) {
                    //colorful.setBlueColorValue(progress);
                    blueProgress = progress;
                    txtBlueColorValue.setText(progress + "%");
                    activeColor = BLUE;
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            final Bitmap changedBitmap = bitmap;

            switch (activeColor) {
                case RED:
                    colorful.setRedColorValue(redProgress);
                    break;
                case GREEN:
                    colorful.setGreenColorValue(greenProgress);
                    break;
                case BLUE:
                    colorful.setBlueColorValue(blueProgress);
                    break;
            }
            /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) // Above Api Level 13
            {
                new ImageProcess().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }*/

            //progressBar.setProgress(0);
            //progressBar.setVisibility(View.VISIBLE);
            final int red = colorful.getRedColorValue();
            final int green = colorful.getGreenColorValue();
            final int blue = colorful.getBlueColorValue();

            switch (activeColor) {
                case RED:
                    /*if(threads[0] != null) {
                        threads[0].interrupt();
                        threads[0] = null;
                    }*/
                    redService.submit(new Runnable() {
                        @Override
                        public void run() {
                            pixelProcess(changedBitmap, red, green, blue);
                        }
                    }
                    );
                    //threads[0].start();
                    break;
                case GREEN:
                    /*if(threads[1] != null) {
                        threads[1].interrupt();
                        threads[1] = null;
                    }*/
                    //greenSeekBar.setEnabled(false);
                    //if(!greenService.isTerminated())
                    //    greenService.shutdownNow();
                    greenService.submit(new Runnable() {
                        @Override
                        public void run() {
                            pixelProcess(changedBitmap, red, green, blue);
                            //greenSeekBar.setEnabled(true);
                            //greenSeekBar.setEnabled(true);
                        }
                    }
                    );


                    //threads[1].start();
                    break;
                case BLUE:
                    /*if(threads[2] != null) {
                        threads[2].stop();
                        //threads[2] = null;
                    }*/
                    blueService.submit(new Runnable() {
                        @Override
                        public void run() {
                            pixelProcess(changedBitmap, red, green, blue);
                        }
                    }
                    );
                    //threads[2].start();
                    break;
            }
           /* service.submit(new Runnable() {
                @Override
                public void run() {
                    int bitmapWidth = bitmap.getWidth();
                    int bitmapHeight = bitmap.getHeight();

                    Bitmap.Config bitmapConfig = bitmap.getConfig();
                    final Bitmap localBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, bitmapConfig);

                    for (int row = 0; row < bitmapWidth; row++) {

                        final int progress = (int) (((row + 1) / (float) bitmapWidth) * 100);
                        for (int column = 0; column < bitmapHeight; column++) {
                            int pixelColor = colorful.getBitmap().getPixel(row, column);
                            int changedPixel = changedBitmap.getPixel(row, column);

                            switch (colorful.getActiveColor()) {
                                case RED:
                                    pixelColor = Color.argb(Color.alpha(pixelColor),
                                            (int) (red / 100.0 * Color.red(pixelColor)),
                                            Color.green(changedPixel), Color.blue(changedPixel));
                                    break;
                                case GREEN:
                                    pixelColor = Color.argb(Color.alpha(pixelColor), Color.red(changedPixel),
                                            (int) (green / 100.0 * Color.green(pixelColor)),
                                            Color.blue(changedPixel));
                                    break;
                                case BLUE:
                                    pixelColor = Color.argb(Color.alpha(pixelColor),
                                            Color.red(changedPixel), Color.green(changedPixel),
                                            (int) (blue / 100.0 * Color.blue(pixelColor)));
                                    break;
                            }
                            localBitmap.setPixel(row, column, pixelColor);

                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                *//*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    progressBar.setProgress(progress, true);
                                } else
                                    progressBar.setProgress(progress);*//*
                                imgPhoto.setImageBitmap(localBitmap);
                            }
                        });
                        //imgPhoto.setImageBitmap(localBitmap);
                    }
                    imgPhoto.post(new Runnable() {
                        @Override
                        public void run() {
                            bitmap = localBitmap;
                            imgPhoto.setImageBitmap(bitmap);
                            //progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                    *//*handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });*//*
                }
            });*/
        }

        private void pixelProcess(final Bitmap changedBitmap, final int red, final int green,
                                  final int blue) {
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();

            Bitmap.Config bitmapConfig = bitmap.getConfig();
            final Bitmap localBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, bitmapConfig);

            for (int row = 0; row < bitmapWidth; row++) {

                final int progress = (int) (((row + 1) / (float) bitmapWidth) * 100);
                for (int column = 0; column < bitmapHeight; column++) {
                    int pixelColor = colorful.getBitmap().getPixel(row, column);
                    int changedPixel = changedBitmap.getPixel(row, column);

                    switch (colorful.getActiveColor()) {
                        case RED:
                            pixelColor = Color.argb(Color.alpha(pixelColor),
                                    (int) (red / 100.0 * Color.red(pixelColor)),
                                    Color.green(changedPixel), Color.blue(changedPixel));
                            break;
                        case GREEN:
                            pixelColor = Color.argb(Color.alpha(pixelColor), Color.red(changedPixel),
                                    (int) (green / 100.0 * Color.green(pixelColor)),
                                    Color.blue(changedPixel));
                            break;
                        case BLUE:
                            pixelColor = Color.argb(Color.alpha(pixelColor),
                                    Color.red(changedPixel), Color.green(changedPixel),
                                    (int) (blue / 100.0 * Color.blue(pixelColor)));
                            break;
                    }
                    localBitmap.setPixel(row, column, pixelColor);

                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    progressBar.setProgress(progress, true);
                                } else
                                    progressBar.setProgress(progress);*/
                        imgPhoto.setImageBitmap(localBitmap);

                    }
                });
                //imgPhoto.setImageBitmap(localBitmap);
            }
            imgPhoto.post(new Runnable() {
                @Override
                public void run() {
                    bitmap = localBitmap;
                    imgPhoto.setImageBitmap(bitmap);
                    greenSeekBar.setEnabled(true);
                    //progressBar.setVisibility(View.INVISIBLE);
                }
            });

                    /*handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });*/
        }
    }
}
