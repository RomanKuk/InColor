package com.example.incolor.ui.newton_fractal;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.incolor.R;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewtonFractalFragment extends Fragment implements View.OnClickListener, PropertyChangeListener {

    static ImageView imgFractal;
    static ProgressBar progressBar;
    static Bitmap fractalBitmap;
    static LinearLayout buttons;

    NewtonFractal operation;
    private ExecutorService threads;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_newton_fractal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageButton btnTakeAPicture = requireView().findViewById(R.id.btnRegenerate);
        ImageButton btnSaveImg = getView().findViewById(R.id.btnSaveFractal);
        ImageButton btnShareImg = getView().findViewById(R.id.btnShareFractal);
        imgFractal = getView().findViewById(R.id.imgFractal);
        progressBar = getView().findViewById(R.id.progressBar);
        buttons = getView().findViewById(R.id.buttonsFractalLayout);

        btnTakeAPicture.setOnClickListener(NewtonFractalFragment.this);
        btnSaveImg.setOnClickListener(NewtonFractalFragment.this);
        btnShareImg.setOnClickListener(NewtonFractalFragment.this);

        threads = Executors.newCachedThreadPool();
        fractalBitmap = null;
        buttons.setVisibility(View.INVISIBLE);

        //ImageView fractal = getView().findViewById(R.id.imgFractal);
        Bitmap bitmap = ((BitmapDrawable) imgFractal.getDrawable()).getBitmap();
        operation = new NewtonFractal(/*bitmap.getWidth(), bitmap.getHeight(),*/325, 575,
                new Polynomial(DialogNewtonFractalFragment.coefficients));
        progressBar.setVisibility(View.VISIBLE);
        threads.submit(new Runnable() {
            @Override
            public void run() {
                operation.execute();
            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnRegenerate) {
            NavHostFragment.findNavController(NewtonFractalFragment.this)
                    .navigate(R.id.action_NewtonFractalFragment_to_DialogNewtonFragmentFragment);
        } else if (view.getId() == R.id.btnSaveFractal) {
            int permissionCheck = ContextCompat.checkSelfPermission(requireActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

                threads.submit(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap changedImg = fractalBitmap;
                        try {
                            SaveFile.saveFile(getActivity(), changedImg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Toast.makeText(getActivity(), "The fractal image is successfully " +
                        "saved to External Storage", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        2000);
            }

        } else if (view.getId() == R.id.btnShareFractal) {

            Intent shareIntent = new Intent(Intent.ACTION_SEND);

            //Bitmap changedImg = changeFilter(bitmap, imgPhoto.getColorFilter());
            File myPictureFile = null;
            try {
                myPictureFile = SaveFile.saveFile(getActivity(), fractalBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Uri myUri = Uri.fromFile(myPictureFile);

            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT,
                    "This fractal image is sent from InColor");
            shareIntent.putExtra(Intent.EXTRA_STREAM, myUri);

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            startActivity(Intent.createChooser(shareIntent,
                    "Let's share your fractal image with others"));
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //int progress = ((Integer) event.getNewValue()).intValue();
        //progressBar.setProgress(progress);

        try {
            fractalBitmap = operation.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        imgFractal.setImageBitmap(fractalBitmap);
    }

}
