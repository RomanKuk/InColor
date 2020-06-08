package com.example.incolor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.incolor.ui.color_models.Conversions;

public class FirstFragment extends Fragment {

    private TextView txtHex;
    private TextView txtRgb;
    private TextView txtHsl;
    private TextView txtHsv;
    private TextView txtCmyk;
    private int dayColor;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dayColor = DayColorActivity.dayColor;
        txtCmyk = getView().findViewById(R.id.txtCmyk);
        txtHex = getView().findViewById(R.id.txtHex);
        txtHsl = getView().findViewById(R.id.txtHsl);
        txtHsv = getView().findViewById(R.id.txtHsv);
        txtRgb = getView().findViewById(R.id.txtRgb);
        Button btnBack = getView().findViewById(R.id.button_back);

        setColorValues();
        setTextColor();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MainActivity.active) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                }
                getActivity().finish();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setColorValues() {
        int red = Color.red(dayColor);
        int green = Color.green(dayColor);
        int blue = Color.blue(dayColor);

        String hexColor = Conversions.toHex(red, green, blue);
        int[] cmyk = Conversions.rgbToCmyk(red, green, blue);
        int[] hsl = Conversions.rgbToHsl(red, green, blue);
        int[] hsv = Conversions.rgbToHsv(red, green, blue);

        txtHex.setText(hexColor);
        txtRgb.setText("RGB(" + red + ", " + green + ", " + blue + ")");
        txtCmyk.setText("CMYK(" + cmyk[0] + "%, " + cmyk[1] + "%, " + cmyk[2] + "%, " + cmyk[3] + "%)");
        txtHsv.setText("HSV(" + hsv[0] + "º, " + hsv[1] + "%, " + hsv[2] + "%)");
        txtHsl.setText("HSL(" + hsl[0] + "º, " + hsl[1] + "%, " + hsl[2] + "%)");

    }

    @Override
    public void onStop() {
        super.onStop();
        setTextColor();
        setColorValues();
    }

    @Override
    public void onResume() {
        super.onResume();
        setTextColor();
        setColorValues();
    }

    private void setTextColor() {
        int red = Color.red(dayColor);
        int green = Color.green(dayColor);
        int blue = Color.blue(dayColor);

        if (red * 0.299 + green * 0.587 + blue * 0.114 > 134) {
            txtHex.setTextColor(Color.BLACK);
            txtHsv.setTextColor(Color.BLACK);
            txtRgb.setTextColor(Color.BLACK);
            txtHsl.setTextColor(Color.BLACK);
            txtHsv.setTextColor(Color.BLACK);
            txtCmyk.setTextColor(Color.BLACK);
        } else {
            txtHex.setTextColor(Color.WHITE);
            txtHsv.setTextColor(Color.WHITE);
            txtRgb.setTextColor(Color.WHITE);
            txtHsl.setTextColor(Color.WHITE);
            txtHsv.setTextColor(Color.WHITE);
            txtCmyk.setTextColor(Color.WHITE);
        }

    }
}
