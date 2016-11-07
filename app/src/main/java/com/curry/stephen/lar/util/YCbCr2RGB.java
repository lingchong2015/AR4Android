package com.curry.stephen.lar.util;

/**
 * Created by lingchong on 16/11/7.
 */

public class YCbCr2RGB {

    public static void yCbCrToRGB565(byte[] YCBCRs, int width, int height, byte[] rgbs) {
        // the end of the luminance data
        final int lumEnd = width * height;
        // points to the next luminance value pair
        int lumPtr = 0;
        // points to the next chromiance value pair
        int chrPtr = lumEnd;
        // points to the next byte output pair of RGB565 value
        int outPtr = 0;
        // the end of the current luminance scanline
        int lineEnd = width;

        while (true) {

            // skip back to the start of the chromiance values when necessary
            if (lumPtr == lineEnd) {
                if (lumPtr == lumEnd)
                    break; // we've reached the end
                // division here is a bit expensive, but's only done once per
                // scanline
                chrPtr = lumEnd + ((lumPtr >> 1) / width) * width;
                lineEnd += width;
            }

            // read the luminance and chromiance values
            final int Y1 = YCBCRs[lumPtr++] & 0xff;
            final int Y2 = YCBCRs[lumPtr++] & 0xff;
            final int Cr = (YCBCRs[chrPtr++] & 0xff) - 128;
            final int Cb = (YCBCRs[chrPtr++] & 0xff) - 128;
            int R, G, B;

            // generate first RGB components
            B = Y1 + ((454 * Cb) >> 8);
            if (B < 0)
                B = 0;
            else if (B > 255)
                B = 255;
            G = Y1 - ((88 * Cb + 183 * Cr) >> 8);
            if (G < 0)
                G = 0;
            else if (G > 255)
                G = 255;
            R = Y1 + ((359 * Cr) >> 8);
            if (R < 0)
                R = 0;
            else if (R > 255)
                R = 255;
            // NOTE: this assume little-endian encoding
            rgbs[outPtr++] = (byte) (((G & 0x3c) << 3) | (B >> 3));
            rgbs[outPtr++] = (byte) ((R & 0xf8) | (G >> 5));

            // generate second RGB components
            B = Y2 + ((454 * Cb) >> 8);
            if (B < 0)
                B = 0;
            else if (B > 255)
                B = 255;
            G = Y2 - ((88 * Cb + 183 * Cr) >> 8);
            if (G < 0)
                G = 0;
            else if (G > 255)
                G = 255;
            R = Y2 + ((359 * Cr) >> 8);
            if (R < 0)
                R = 0;
            else if (R > 255)
                R = 255;
            // NOTE: this assume little-endian encoding
            rgbs[outPtr++] = (byte) (((G & 0x3c) << 3) | (B >> 3));
            rgbs[outPtr++] = (byte) ((R & 0xf8) | (G >> 5));
        }
    }
}
