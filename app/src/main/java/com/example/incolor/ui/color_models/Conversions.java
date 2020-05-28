package com.example.incolor.ui.color_models;

public class Conversions {
    public static int[] rgbToHsl(int red, int green, int blue) {
        double redValue = (red / 255.0);
        double greenValue = (green / 255.0);
        double blueValue = (blue / 255.0);

        double max = Math.max(Math.max(redValue, greenValue), blueValue);
        double min = Math.min(Math.min(redValue, greenValue), blueValue);
        double h = 0, s, l = (max + min) / 2;

        if (max == min) {
            s = 0; // achromatic
        } else {
            double difference = max - min;
            s = difference / (1 - Math.abs(2 * l - 1));
            if (max == redValue)
                h = (((greenValue - blueValue) / difference) % 6 + 6) % 6;
            else if (max == greenValue)
                h = (blueValue - redValue) / difference + 2;
            else if (max == blueValue)
                h = (redValue - greenValue) / difference + 4;

            h *= 60;
        }

        h = Math.round(h);
        s = Math.round(s * 100);
        l = Math.round(l * 100);

        return new int[]{(int) h, (int) s, (int) l};
    }

    public static int[] rgbToHsv(int red, int green, int blue) {
        double redValue = (red / 255.0);
        double greenValue = (green / 255.0);
        double blueValue = (blue / 255.0);

        double max = Math.max(Math.max(redValue, greenValue), blueValue);
        double min = Math.min(Math.min(redValue, greenValue), blueValue);
        double h = 0, s, v = max;

        if (max == 0) {
            s = 0; // achromatic
        } else {
            double difference = max - min;
            s = difference / max;
            if (max == redValue)
                h = (((greenValue - blueValue) / difference) % 6 + 6) % 6;
            else if (max == greenValue)
                h = (blueValue - redValue) / difference + 2;
            else if (max == blueValue)
                h = (redValue - greenValue) / difference + 4;

            h *= 60;
        }

        h = Math.round(h);
        s = Math.round(s * 100);
        v = Math.round(v * 100);

        return new int[]{(int) h, (int) s, (int) v};
    }

    public static int[] rgbToCmyk(int r, int g, int b) {
        double percentageR = r / 255.0;
        double percentageG = g / 255.0;
        double percentageB = b / 255.0;

        double k = 1 - Math.max(Math.max(percentageR, percentageG), percentageB);

        if (k == 1) {
            return new int[]{0, 0, 0, 100};
        }

        double c = (1 - percentageR - k) / (1 - k);
        double m = (1 - percentageG - k) / (1 - k);
        double y = (1 - percentageB - k) / (1 - k);

        c = Math.round(c * 100);
        m = Math.round(m * 100);
        y = Math.round(y * 100);
        k = Math.round(k * 100);

        return new int[]{(int) c, (int) m, (int) y, (int) k};
    }

    private static String getHexValues(int number) {
        StringBuilder builder = new StringBuilder(Integer.toHexString(number & 0xff));
        while (builder.length() < 2) {
            builder.append("0");
        }
        return builder.toString().toUpperCase();
    }

    public static String toHex(int r, int g, int b) {
        return "#" + getHexValues(r) + getHexValues(g) + getHexValues(b);
    }
}
