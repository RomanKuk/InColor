package com.example.incolor.ui.color_models;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.ProgressBar;

enum RGB {
    RED,
    GREEN,
    BLUE
}

class Colorful {

    private Bitmap bitmap;
    private int redColorValue;
    private int greenColorValue;
    private int blueColorValue;
    private RGB activeColor;
    private int progress = 0;
    private int[] pixels;
    private int divideNum = 10;
    private int[] localPixels;

    Colorful(Bitmap bitmap, int redColorValue, int greenColorValue,
             int blueColorValue) {
        this.bitmap = bitmap;
        setRedColorValue(redColorValue);
        setGreenColorValue(greenColorValue);
        setBlueColorValue(blueColorValue);
        //pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        //int length = pixels.length;
        /*while(length/(divideNum) > 50000)
        {
            divideNum += 10;
        }*/
        // bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0,
        //         bitmap.getWidth(), bitmap.getHeight());
    }

    int getRedColorValue() {
        return redColorValue;
    }

    void setRedColorValue(int redColorValue) {
        this.redColorValue = redColorValue;
        this.activeColor = RGB.RED;
    }

    int getGreenColorValue() {
        return greenColorValue;
    }

    void setGreenColorValue(int greenColorValue) {
        this.greenColorValue = greenColorValue;
        this.activeColor = RGB.GREEN;
    }

    int getBlueColorValue() {
        return blueColorValue;
    }

    void setBlueColorValue(int blueColorValue) {
        this.blueColorValue = blueColorValue;
        this.activeColor = RGB.BLUE;
    }

    Bitmap getBitmap() {
        return bitmap;
    }

    RGB getActiveColor() {
        return activeColor;
    }

    /*Bitmap returnColorizedBitmap(final ProgressBar bar) {


        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < pixels.length; i++)
                {
                    progress = (i + 1) / pixels.length * 100;
                    final int finalI = i;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            switch (activeColor){
                                case RED:
                                    pixels[finalI] = Color.argb(Color.alpha(pixels[finalI]),
                                            (int) (redColorValue / 100.0 * Color.red(pixels[finalI])),
                                            Color.green(pixels[finalI]), Color.blue(pixels[finalI]));
                                    break;
                                case GREEN:
                                    pixels[finalI] = Color.argb(Color.alpha(pixels[finalI]),
                                            Color.red(pixels[finalI]),
                                            (int) (greenColorValue / 100.0 * Color.green(pixels[finalI])),
                                            Color.blue(pixels[finalI]));
                                    break;
                                case BLUE:
                                    pixels[finalI] = Color.argb(Color.alpha(pixels[finalI]),
                                            Color.red(pixels[finalI]),
                                            Color.green(pixels[finalI]),
                                            (int) (blueColorValue / 100.0 * Color.blue(pixels[finalI])));
                                    break;
                            }
                            bar.setProgress(progress);
                        }
                    });
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            bar.setVisibility(View.INVISIBLE);
                        }
                    });

                }
            }
        }).start();


        bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0,
                bitmap.getWidth(), bitmap.getHeight());

        return bitmap;
    }*/

    Bitmap returnColorizedBitmap(Bitmap changedImg, final ProgressBar bar) {

        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        Bitmap.Config bitmapConfig = bitmap.getConfig();
        Bitmap localBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, bitmapConfig);

        for (int row = 0; row < bitmapWidth; row++) {

            final int progress = (row + 1) / bitmapWidth * 100;
            for (int column = 0; column < bitmapHeight; column++) {

                int pixelColor = bitmap.getPixel(row, column);
                int changedPixel = changedImg.getPixel(row, column);

                switch (activeColor) {
                    case RED:
                        pixelColor = Color.argb(Color.alpha(pixelColor),
                                (int) (redColorValue / 100.0 * Color.red(pixelColor)),
                                Color.green(changedPixel), Color.blue(changedPixel));
                        break;
                    case GREEN:
                        pixelColor = Color.argb(Color.alpha(pixelColor), Color.red(changedPixel),
                                (int) (greenColorValue / 100.0 * Color.green(pixelColor)),
                                Color.blue(changedPixel));
                        break;
                    case BLUE:
                        pixelColor = Color.argb(Color.alpha(pixelColor),
                                Color.red(changedPixel), Color.green(changedPixel),
                                (int) (blueColorValue / 100.0 * Color.blue(pixelColor)));
                        break;
                }
                localBitmap.setPixel(row, column, pixelColor);
            }
            /*handler.post(new Runnable() {
                @Override
                public void run() {
                    bar.setProgress(progress);
                }
            });*/

        }

        return localBitmap;
    }

    /*Bitmap returnColorizedBitmap(*//*final ProgressBar bar,*//*final Bitmap changedImg) {
        int iteration = 0;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        //final int[] changedPixels = new int[width * height];
       // changedImg.getPixels(changedPixels, 0, width, 0, 0,
       //         width, height);
       // final int length = 1000000;
        Bitmap.Config bitmapConfig = bitmap.getConfig();
        final Bitmap localBitmap = Bitmap.createBitmap(width, height, bitmapConfig);
        *//*while(iteration != divideNum) {
            final int finalIteration = iteration;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = pixels.length/divideNum*finalIteration;
                         i < pixels.length/divideNum*(finalIteration+1); i++) {
                       *//**//* switch (activeColor) {
                            case RED:
                                int red = Color.red(pixels[i]);
                                int green = Color.green(changedPixels[i]);
                                int blue = Color.blue(changedPixels[i]);
                                changedPixels[i] = Color.argb(Color.alpha(pixels[i]),
                                        (int) (redColorValue / 100.0 * Color.red(pixels[i])),
                                        Color.green(changedPixels[i]), Color.blue(changedPixels[i]));
                                break;
                            case GREEN:
                                changedPixels[i] = Color.argb(Color.alpha(pixels[i]),
                                        Color.red(changedPixels[i]),
                                        (int) (greenColorValue / 100.0 * Color.green(pixels[i])),
                                        Color.blue(changedPixels[i]));
                                break;
                            case BLUE:
                                changedPixels[i] = Color.argb(Color.alpha(pixels[i]),
                                        Color.red(changedPixels[i]),
                                        Color.green(changedPixels[i]),
                                        (int) (blueColorValue / 100.0 * Color.blue(pixels[i])));
                                break;
                        }*//**//*

                       int pixel = Color.argb(Color.alpha(pixels[i]),
                                (int)(redColorValue/100.0 * Color.red(pixels[i])),
                                (int) (greenColorValue/100.0 * Color.green(pixels[i])),
                                (int)(blueColorValue/100.0 * Color.blue(pixels[i])));
                       localPixels[i] =
                    }
                }
            }).start();
            iteration++;
            //iteration = divideNum;
        }
       // changedImg.setPixels(changedPixels, 0, width, 0, 0,
        //        width, height);*//*

        return changedImg;
    }*/
}

