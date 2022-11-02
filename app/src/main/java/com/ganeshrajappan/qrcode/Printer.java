package com.ganeshrajappan.qrcode;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Printer {

    public static String readAssetFiles(Context context) {
        InputStream input = null;
        ByteArrayOutputStream output = null;
        AssetManager assetManager = context.getAssets();
        String outputStr = "";
        try {
            input = assetManager.open("printer_profiles.JSON");
            output = new ByteArrayOutputStream(3000);
            byte[] buf = new byte[1024];
            int len;
            while (true) {
                if (!((len = input.read(buf)) > 0)) break;
                output.write(buf, 0, len);
            }
            input.close();
            input = null;
            output.flush();
            output.close();
            outputStr = output.toString();
            output = null;

        } catch (Exception ex) {
            Log.d("QRCODE", ex.getMessage());
        } finally {
            try {
                if (input != null) {
                    input.close();
                    input = null;
                }
                if (output != null) {
                    output.close();
                    output = null;
                }
            } catch (IOException e) {
            }
        }
        return outputStr;
    }

    public static byte[] convertTo1BPP(Bitmap inputBitmap, int darknessThreshold) {
        int width = inputBitmap.getWidth();
        int height = inputBitmap.getHeight();
        ByteArrayOutputStream mImageStream = new ByteArrayOutputStream();
        int BITMAPFILEHEADER_SIZE = 14;
        int BITMAPINFOHEADER_SIZE = 40;
        short biPlanes = 1;
        short biBitCount = 1;
        int biCompression = 0;
        int biSizeImage = (width * biBitCount + 31 & -32) / 8 * height;
        int biXPelsPerMeter = 0;
        int biYPelsPerMeter = 0;
        int biClrUsed = 2;
        int biClrImportant = 2;
        byte[] bfType = new byte[]{66, 77};
        short bfReserved1 = 0;
        short bfReserved2 = 0;
        int bfOffBits = BITMAPFILEHEADER_SIZE + BITMAPINFOHEADER_SIZE + 8;
        int bfSize = bfOffBits + biSizeImage;
        byte[] colorPalette = new byte[]{0, 0, 0, -1, -1, -1, -1, -1};
        int monoBitmapStride = (width + 31 & -32) / 8;
        byte[] newBitmapData = new byte[biSizeImage];
        try {
            mImageStream.write(bfType);
            mImageStream.write(intToDWord(bfSize));
            mImageStream.write(intToWord(bfReserved1));
            mImageStream.write(intToWord(bfReserved2));
            mImageStream.write(intToDWord(bfOffBits));
            mImageStream.write(intToDWord(BITMAPINFOHEADER_SIZE));
            mImageStream.write(intToDWord(width));
            mImageStream.write(intToDWord(height));
            mImageStream.write(intToWord(biPlanes));
            mImageStream.write(intToWord(biBitCount));
            mImageStream.write(intToDWord(biCompression));
            mImageStream.write(intToDWord(biSizeImage));
            mImageStream.write(intToDWord(biXPelsPerMeter));
            mImageStream.write(intToDWord(biYPelsPerMeter));
            mImageStream.write(intToDWord(biClrUsed));
            mImageStream.write(intToDWord(biClrImportant));
            mImageStream.write(colorPalette);
            int[] imageData = new int[height * width];
            inputBitmap.getPixels(imageData, 0, width, 0, 0, width, height);
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    int pixelIndex = y * width + x;
                    int mask = 128 >> (x & 7);
                    int pixel = imageData[pixelIndex];
                    int R = Color.red(pixel);
                    int G = Color.green(pixel);
                    int B = Color.blue(pixel);
                    int A = Color.alpha(pixel);
                    boolean set = A < darknessThreshold || R + G + B > darknessThreshold * 3;
                    if (set) {
                        int index = (height - y - 1) * monoBitmapStride + (x >>> 3);
                        newBitmapData[index] = (byte) (newBitmapData[index] | mask);
                    }
                }
            }
            mImageStream.write(newBitmapData);
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return mImageStream.toByteArray();
    }

    private static byte[] intToWord(int parValue) {
        byte[] retValue = new byte[]{(byte) (parValue & 255), (byte) (parValue >> 8 & 255)};
        return retValue;
    }

    private static byte[] intToDWord(int parValue) {
        byte[] retValue = new byte[]{(byte) (parValue & 255), (byte) (parValue >> 8 & 255), (byte) (parValue >> 16 & 255), (byte) (parValue >> 24 & 255)};
        return retValue;
    }

}
