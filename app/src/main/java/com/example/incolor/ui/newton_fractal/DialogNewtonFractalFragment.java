package com.example.incolor.ui.newton_fractal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.incolor.R;


public class DialogNewtonFractalFragment extends Fragment {

    static double[] coefficients;
    private TextView z0Field;
    private TextView z3Field;
    private TextView z4Field;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_newton_fractal, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        z0Field = getView().findViewById(R.id.z0Field);
        z3Field = getView().findViewById(R.id.z3Field);
        z4Field = getView().findViewById(R.id.z4Field);
        Button btnGenerate = getView().findViewById(R.id.btnGenerate);

        coefficients = new double[5];

        getView().findViewById(R.id.dialogLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                } catch (NullPointerException e) {
                }
            }
        });

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ShowToast")
            @Override
            public void onClick(View v) {
                try {
                    coefficients[0] = Double.parseDouble(z0Field.getText().toString());
                    coefficients[1] = 0;
                    coefficients[2] = 0;
                    coefficients[3] = Double.parseDouble(z3Field.getText().toString());
                    coefficients[4] = Double.parseDouble(z4Field.getText().toString());

                    double absCoeffSum = 0;
                    for (int i = 4; i > 1; i--) {
                        absCoeffSum += Math.abs(coefficients[i]);
                    }

                    if (absCoeffSum == 0) {
                        Toast.makeText(getActivity(),
                                "Error! The polynomial must be of at least " +
                                        "order 2, ie include a Z² or higher term",
                                Toast.LENGTH_LONG);
                        return;
                    }

//                    progressMonitor = new ProgressMonitor(FractalViewer.this, "Generating fractal...", "",0, 100);
//                    progressMonitor.setProgress(0);
//
//                    operation = new NewtonFractal(DEFAULT_WIDTH-4, DEFAULT_WIDTH-65, new Polynomial(coefficients));
//                    operation.addPropertyChangeListener(this);
//                    operation.execute();

                    try {
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                    } catch (NullPointerException e) {
                    }

                    NavHostFragment.findNavController(DialogNewtonFractalFragment.this)
                            .navigate(R.id.action_DialogNewtonFragmentFragment_to_NewtonFractalFragment);

                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(),
                            "Error! You should input a valid number (" + e + ")",
                            Toast.LENGTH_LONG);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
