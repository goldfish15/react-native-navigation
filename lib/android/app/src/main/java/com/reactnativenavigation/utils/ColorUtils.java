package com.reactnativenavigation.utils;

public class ColorUtils {
    public static double[] colorToLAB(int color) {
        final double[] result = new double[3];
        androidx.graphics.ColorUtils.colorToLAB(color, result);
        return result;
    }

    public static int labToColor(double[] lab) {
        return androidx.graphics.ColorUtils.LABToColor(lab[0], lab[1], lab[2]);
    }
}
