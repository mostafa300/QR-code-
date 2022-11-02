package com.ganeshrajappan.qrcode;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class Zxing {

    public static Bitmap drawQRCode(String text, int Width, int Height) {
        Bitmap bitmap = null;
        HashMap hintMap = new HashMap();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);
        //we'll make the padding around qrcode which is called quiet zone as 0.
        //you can remove this statement to have the default quite zone printed if you need.
        hintMap.put(EncodeHintType.MARGIN, 0);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, Width, Height, hintMap);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static String saveBitmap(Bitmap bm, String filename, Context context) {

        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File dir = new File(path, "DemoQrcode.jpg");

        //File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/QRCode");
        if (!dir.exists()) {
            dir.mkdir();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(dir.getAbsolutePath() + "/" + filename + ".jpg");
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        MediaScannerConnection.scanFile(context,
                new String[]{dir.getAbsolutePath() + "/" + filename + ".jpg"}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                    }
                }
        );
        return dir.getAbsolutePath() + "/" + filename + ".jpg";
    }
}
