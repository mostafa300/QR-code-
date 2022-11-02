package com.ganeshrajappan.qrcode.zatca;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

import com.ganeshrajappan.qrcode.zatca.tag.InvoiceDate;
import com.ganeshrajappan.qrcode.zatca.tag.InvoiceTaxAmount;
import com.ganeshrajappan.qrcode.zatca.tag.InvoiceTotalAmount;
import com.ganeshrajappan.qrcode.zatca.tag.QRBarcodeEncoder;
import com.ganeshrajappan.qrcode.zatca.tag.Seller;
import com.ganeshrajappan.qrcode.zatca.tag.TaxNumber;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Objects;

public class QRCode {

    private static final String fromFormat = "yyyyMMdd HHmmss";
    private static final String toFormat = "EEE, d MMM yyyy (h:mm a)";

    public static String eInvoiceBase62(String customerName, String invoiceNo, String date, String invTotal, String invTax) {
        return QRBarcodeEncoder.encode(new Seller(customerName),
                new TaxNumber(invoiceNo),
                new InvoiceDate(date),
                new InvoiceTotalAmount(invTotal)
                , new InvoiceTaxAmount(invTax));
    }

    @SuppressLint("SimpleDateFormat")
    public static String setDefaultDateFormat(String inputDate, String fromFormat, String toFormat) {
        SimpleDateFormat input = new SimpleDateFormat(fromFormat);
        SimpleDateFormat output = new SimpleDateFormat(toFormat);
        try {
            return output.format(Objects.requireNonNull(input.parse(inputDate)));
        } catch (ParseException e) {
            e.printStackTrace();
            return inputDate;
        }
    }

    public static Bitmap generateQRCodeFromText(String content, int Width, int Height) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            return barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, Width, Height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

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
}
