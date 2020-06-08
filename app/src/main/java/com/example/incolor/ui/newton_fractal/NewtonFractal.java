package com.example.incolor.ui.newton_fractal;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.view.View;

import com.example.incolor.ui.color_models.Conversions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewtonFractal extends AsyncTask<Void, Integer, Bitmap> {


    static final double DEFAULT_ZOOM = 10.0;
    static final double DEFAULT_TOP_LEFT_X = -16.5;//-4.5;
    static final double DEFAULT_TOP_LEFT_Y = 28.0;//4.0;
    private static final int MAXITER = 100;
    private static double TOLERANCE = Math.pow(10, -6);
    private final ExecutorService threads;

    double zoomFactor = DEFAULT_ZOOM;
    double topLeftX = DEFAULT_TOP_LEFT_X;
    double topLeftY = DEFAULT_TOP_LEFT_Y;
    private int width, height;
    private Map<Point, RootPoint> roots;
    private ArrayList<Complex> rootColors;
    private Polynomial polinomial;

    public NewtonFractal(int width, int height, Polynomial polinomial) {
        this.width = width;
        this.height = height;
        this.polinomial = polinomial;
        this.threads = Executors.newCachedThreadPool();
    }

    private double getXPos(double x) {
        return (x / zoomFactor) + topLeftX;
    }

    private double getYPos(double y) {
        return (y / zoomFactor) - topLeftY;
    }

    private float clamp01(float value) {
        return Math.max(0, Math.min(1, value));
    }

    private int getColorFromRoot(RootPoint rootPoint, int[] rgb) {
        for (int i = 0; i < rootColors.size(); i++) {
            if (rootColors.get(i).equals(rootPoint.getPoint(), TOLERANCE)) {

                float hue, saturation, brightness;

                hue = clamp01(Math.abs((float) (0.5f - rootPoint.getPoint().arg() / (Math.PI * 2.0f))));

                saturation = clamp01(Math.abs(0.59f / (float) rootPoint.getPoint().abs()));

                brightness = 0.95f * Math.max(1.0f - (float) rootPoint.getNumIter() * 0.025f, 0.05f);

                if (rootPoint.getPoint().abs() < 0.1) {
                    saturation = 0.0f;
                }

                return Conversions.HSBtoRGB(hue, saturation, brightness, rgb);
            }
        }
        for (int i = 0; i < 3; i++)
            rgb[i] = 0;
        return Color.BLACK;
    }

    private void applyNewtonMethod(int x, int y) {
        Complex point = new Complex(getXPos(x), getYPos(y));

        RootApproximator rtApprox = new RootApproximator(polinomial, point);

        RootPoint rootPoint = rtApprox.getRootPoint();

        if (!containsRoot(rootPoint.getPoint())) {
            rootColors.add(rootPoint.getPoint());
        }
        roots.put(new Point(x, y), rootPoint);
    }

    private boolean containsRoot(Complex root) {
        for (Complex z : rootColors) {
            if (z.equals(root, TOLERANCE)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        NewtonFractalFragment.progressBar.setVisibility(View.INVISIBLE);
        NewtonFractalFragment.fractalBitmap = bitmap;
        NewtonFractalFragment.imgFractal.setImageBitmap(bitmap);
        NewtonFractalFragment.buttons.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        NewtonFractalFragment.progressBar.setProgress(values[0]);
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        publishProgress(0);
        final Bitmap fractalImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        rootColors = new ArrayList<>();
        roots = new HashMap<>();

        int totalSteps = 0;
        int calculationSteps = width * height;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                applyNewtonMethod(x, y);
                int[] rgb = new int[3];
                int color = getColorFromRoot(roots.get(new Point(x, y)), rgb);
                fractalImage.setPixel(x, y, Color.rgb(rgb[0], rgb[1], rgb[2]));

                super.publishProgress(Math.round(100.0f * (totalSteps++) / calculationSteps));
            }
        }
        return fractalImage;
    }


    private static class RootApproximator {

        private Polynomial pol, dpol;
        private Complex guess;

        public RootApproximator(Polynomial pol, Complex guess) {
            this.pol = pol;
            this.dpol = pol.derivative();
            this.guess = guess;
        }

        private double nextGuess() {
            Complex nextGuess = guess.subtract(pol.evaluate(guess).divide(dpol.evaluate(guess)));
            double distance = nextGuess.euclideanDistance(guess);
            guess = nextGuess;
            return distance;
        }

        public RootPoint getRootPoint() {
            double diff = 10;
            int iter = 0;
            while (diff > TOLERANCE && iter < MAXITER) {
                iter++;
                diff = nextGuess();
            }

            return new RootPoint(this.guess, iter);
        }
    }

    private static class RootPoint {
        private Complex point;
        private int numIter;

        public RootPoint(Complex point, int numIter) {
            this.point = point;
            this.numIter = numIter;
        }

        public Complex getPoint() {
            return this.point;
        }

        public int getNumIter() {
            return this.numIter;
        }

    }

    private class MyThread implements Runnable {

        CountDownLatch latch;
        int finalK;
        Bitmap fractalImage;

        MyThread(CountDownLatch latch, int finalK, Bitmap fractalImage) {
            this.latch = latch;
            this.finalK = finalK;
            this.fractalImage = fractalImage;
            new Thread(this);
        }

        @Override
        public void run() {
            for (int y = finalK * height / 16; y < (finalK + 1) * height / 16; y++) {
                for (int x = 0; x < width; x++) {

                    applyNewtonMethod(x, y);
                    int[] rgb = new int[3];
                    try {
                        int color = getColorFromRoot(roots.get(new Point(x, y)), rgb);
                    } catch (NullPointerException e) {
                        System.out.println("x = " + x + " y = " + y);
                        return;
                    }
                    fractalImage.setPixel(x, y, Color.rgb(rgb[0], rgb[1], rgb[2]
                            /*Color.green(color), Color.blue(color)*/));

                    //super.publishProgress(Math.round(100.0f * (totalSteps++) / calculationSteps));
                }
            }
        }
    }
}
