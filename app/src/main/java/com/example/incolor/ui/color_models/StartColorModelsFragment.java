package com.example.incolor.ui.color_models;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.incolor.R;

public class StartColorModelsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_start_color_models, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.startLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cameraPermission = ContextCompat.checkSelfPermission(
                        requireActivity(), Manifest.permission.CAMERA);
                int writePermission = ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (cameraPermission == PackageManager.PERMISSION_GRANTED &&
                        writePermission == PackageManager.PERMISSION_GRANTED) {
                        NavHostFragment.findNavController(StartColorModelsFragment.this)
                                .navigate(R.id.action_StartColorModelsFragment_to_ColorModelsFragment);
                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
        });
    }
}
