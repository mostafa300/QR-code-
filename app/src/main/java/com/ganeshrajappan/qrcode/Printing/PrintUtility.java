package com.ganeshrajappan.qrcode.Printing;

import static com.ganeshrajappan.qrcode.MainActivity.doubleToStringCurrency;
import static com.ganeshrajappan.qrcode.MainActivity.getCurrentDateFormatted;
import static com.ganeshrajappan.qrcode.MainActivity.getCurrentTime;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Base64;
import android.util.Log;
import android.util.Size;

import com.ganeshrajappan.qrcode.zatca.QRCode;
import com.honeywell.mobility.print.LinePrinter;
import com.honeywell.mobility.print.PrinterException;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.printer.ZebraPrinter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PrintUtility {
    private final int printwith = 560;
    private final int printLineSpace = 10;
    private final int headerMarginSpace = 10;

    private boolean RTL = true;
    private String jsonCmdAttribStr = null;
    private String base64LogoPng = null;
    private List<PrintLine> printLines;
    private List<PrintLine> headerLines;
    private List<PrintLine> footerLines;

    private arabic682x arabic6824 = null;

    //private UIHelper helper;
    Connection printerConnection = null;
    ZebraPrinter printer = null;
    LinePrinter lp = null;
    Context context;


    public PrintUtility(Context context, LinePrinter lp, Bitmap bitmap, Size size, int dark,
                        List<PrintLine> printLines, List<PrintLine> headerLines, List<PrintLine> footerLines) {
        arabic6824 = new arabic682x();
        RTL = true;
        this.lp = lp;
        this.printLines = printLines;
        this.headerLines = headerLines;
        this.footerLines = footerLines;
        this.context = context;
        readAssetFiles();

        LinePrinter.ExtraSettings exSettings = new LinePrinter.ExtraSettings();
        exSettings.setContext(context);


        try {
            PrintDocumentDotMatrixNegm(bitmap, size, dark);
        } catch (PrinterException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void PrintDocumentDotMatrixNegm(Bitmap bitmap, Size printSize, int dark) throws PrinterException, InterruptedException {
        //region negm 24082020
        int pageLines = 50; // full page lines
        int pagePrintLines = 50; // number of line should be printed pair one page

        int noLines = 0;
        int pageNo = 1;
        int noActualLines = printLines.size() + footerLines.size() - headerLines.size();
        int noEffectPage = pageLines + footerLines.size() - headerLines.size();
        int noOfPages = (noActualLines + noEffectPage - 1) / noEffectPage;

        int totalPrintedLines = noOfPages * noEffectPage;
        int remainLines;
        if (noOfPages < 2) {
            remainLines = Math.abs(totalPrintedLines - noActualLines);
        } else {
            remainLines = Math.abs(totalPrintedLines - noActualLines - footerLines.size());
        }

        boolean forceNewPage = false;
        if (remainLines < headerLines.size() - 1) {
            forceNewPage = true;
            noOfPages++;
        }

        for (PrintLine pl : printLines) {
            if (pl.lineType == PrintLineType.Remove_Force_New_Page) {
                forceNewPage = false;
            }
        }

        //endregion
        for (PrintLine pl : printLines) {
            if (noLines > pagePrintLines) {
                lp.newLine(pageLines - noLines);
                lp.flush();
                PrintDocumentDotMatrixNoPages(pageNo, noOfPages);
                if (noOfPages < 2) {
                    lp.newLine(headerMarginSpace);
                } else {
                    lp.newLine(headerMarginSpace + (headerMarginSpace / 2));
                }
                pageNo += 1;
                lp.flush();
                noLines = PrintDocumentDotMatrixHeader();
            }
            lp.setCompress(pl.printLineCharNum.equals(PrintLineCharNum.Compress));

            switch (pl.lineType) {
                case TLVQRCodeImage:
                    Log.d("PrintDocument", "PrintDocumentDotMatrixNegm: "
                            + printSize.getWidth() + ", " + printSize.getHeight() + ", " + dark);
                    printQRCode(bitmap,printSize, dark);
                    break;
                case Space:
                    lp.newLine(1);
                    lp.flush();
                    break;
                case HeaderImage:
                    break;
                case FooterImage:
                    break;
                case Line:
                    lp.write(fillStr("-", PrintLineCharNum.Normal.getValue()));
                    lp.newLine(1);
                    lp.flush();
                    break;
                case Elements:
                    if (RTL) {
                        for (int i = pl.NumberOfElements - 1; i >= 0; i--) {
                            int size = pl.elementsRelativeWidth[i];
                            int ellen = pl.elements[i].element.length();
                            int spaceb = 0;
                            int spacea = 0;
                            String str = "";
                            switch (pl.elements[i].alignment) {
                                case Relative:
                                case Right:
                                    if (size > ellen) {
                                        spaceb = size - ellen;
                                        str = pl.elements[i].element;
                                    } else
                                        str = pl.elements[i].element.substring(0, size - 1);
                                    break;
                                case Left:
                                    if (size > ellen) {
                                        spacea = size - ellen;
                                        ;
                                        str = pl.elements[i].element;
                                    } else
                                        str = pl.elements[i].element.substring(0, size - 1);
                                    break;
                                case Center:
                                    if (size > ellen) {
                                        spacea = (size - ellen) / 2;
                                        spaceb = (size - ellen) / 2;
                                        str = pl.elements[i].element;
                                    } else
                                        str = pl.elements[i].element.substring(0, size - 1);
                                    break;
                            }

                            byte[] ar_data = arabic6824.Convert(str, false);
                            if (spaceb > 0) lp.write(fillStr(" ", spaceb));

                            if (str.length() > 0)
                                if (isProbablyArabic(str))
                                    lp.write(ar_data);
                                else
                                    lp.write(str);
                            if (spacea > 0) lp.write(fillStr(" ", spacea));
                        }
                    } else {
                        for (int i = 0; i < pl.NumberOfElements; i++) {
                            int size = pl.elementsRelativeWidth[i];
                            int ellen = pl.elements[i].element.length();
                            int spaceb = 0;
                            int spacea = 0;
                            String str = " ";
                            switch (pl.elements[i].alignment) {
                                case Relative:
                                case Left:
                                    if (size > ellen) {
                                        spacea = size - ellen;
                                        str = pl.elements[i].element;
                                    } else
                                        str = pl.elements[i].element.substring(0, size);
                                    break;
                                case Right:
                                    if (size > ellen) {
                                        spaceb = size - ellen;
                                        str = pl.elements[i].element;
                                    } else
                                        str = pl.elements[i].element.substring(0, size);
                                    break;
                                case Center:
                                    if (size > ellen) {
                                        spacea = (size - ellen) / 2;
                                        spaceb = (size - ellen) / 2;
                                        str = pl.elements[i].element;
                                    } else
                                        str = pl.elements[i].element.substring(0, size);
                                    break;
                            }
                            byte[] ar_data = arabic6824.Convert(str, false);
                            if (spaceb > 0) lp.write(fillStr(" ", spaceb));
                            if (str.length() > 0)
                                if (isProbablyArabic(str))
//                                    lp.write(str);
                                    lp.write(ar_data);
                                else
                                    lp.write(str);
                            if (spacea > 0) lp.write(fillStr(" ", spacea));
                        }
                    }
                    lp.newLine(1);
                    lp.flush();
                    sleep(300);
                    break;
            }
            lp.setCompress(false);
            noLines += 1;
        }

        if (forceNewPage) { //
            lp.newLine(pageLines - noLines);
            lp.flush();
            PrintDocumentDotMatrixNoPages(pageNo, noOfPages);
            pageNo += 1;
            lp.newLine(headerMarginSpace);
            lp.flush();
            noLines = PrintDocumentDotMatrixHeader();
        }
        noLines += PrintDocumentDotMatrixFooter();
        lp.newLine(pageLines - noLines);
        lp.flush();
        PrintDocumentDotMatrixNoPages(pageNo, noOfPages);
    }

    private void printQRCode(Bitmap bitmap,Size size, int dark) {
        try {
            byte[] output = convertTo1BPP(bitmap, dark);
            String base64QRcode = Base64.encodeToString(output, Base64.DEFAULT);
            lp.writeGraphicBase64(base64QRcode,
                    LinePrinter.GraphicRotationDegrees.DEGREE_0,
                    72,  // Offset in printhead dots from the left of the page
                    size.getWidth(), // Desired graphic width on paper in printhead dots
                    size.getHeight()); // Desired graphic height on paper in printhead dots
            lp.newLine(1);
            lp.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int PrintDocumentDotMatrixHeader() throws PrinterException {
        int noLines = 0;

        for (PrintLine pl : headerLines) {
            noLines += 1;
            String lineStr = "";
            if (pl.printLineCharNum.equals(PrintLineCharNum.Compress)) {
                lp.setCompress(true);
            } else {
                lp.setCompress(false);
            }

            switch (pl.lineType) {
                case Space:
                    lp.newLine(1);
                    lp.flush();
                    break;
                case HeaderImage:
                    break;
                case FooterImage:
                    break;
//                case TLVQRCodeImage:
//                    noLines = printQRCode(noLines);
//                    break;
                case Line:
                    lp.write(fillStr("-", PrintLineCharNum.Normal.getValue()));
                    lp.newLine(1);
                    lp.flush();
                    break;
                case Elements:
                    if (RTL) {
                        for (int i = pl.NumberOfElements - 1; i >= 0; i--) {
                            int size = pl.elementsRelativeWidth[i];
                            int ellen = pl.elements[i].element.length();
                            int spaceb = 0;
                            int spacea = 0;
                            String str = "";
                            switch (pl.elements[i].alignment) {
                                case Relative:
                                case Right:
                                    if (size > ellen) {
                                        spaceb = size - ellen;
                                        str = pl.elements[i].element;
                                    } else
                                        str = pl.elements[i].element.substring(0, size - 1);
                                    break;
                                case Left:
                                    if (size > ellen) {
                                        spacea = size - ellen;
                                        ;
                                        str = pl.elements[i].element;
                                    } else
                                        str = pl.elements[i].element.substring(0, size - 1);
                                    break;
                                case Center:
                                    if (size > ellen) {
                                        spacea = (size - ellen) / 2;
                                        spaceb = (size - ellen) / 2;
                                        str = pl.elements[i].element;
                                    } else
                                        str = pl.elements[i].element.substring(0, size - 1);
                                    break;
                            }

                            byte[] ar_data = arabic6824.Convert(str, false);
                            if (spaceb > 0) lp.write(fillStr(" ", spaceb));

                            if (str.length() > 0)
                                if (isProbablyArabic(str))
//                                    lp.write(str);
                                    lp.write(ar_data);
                                else
                                    lp.write(str);
                            if (spacea > 0) lp.write(fillStr(" ", spacea));
                        }
                        lp.newLine(1);
                        lp.flush();
                        sleep(300);
                    } else {
                        for (int i = 0; i < pl.NumberOfElements; i++) {
                            int size = pl.elementsRelativeWidth[i];
                            int ellen = pl.elements[i].element.length();
                            int spaceb = 0;
                            int spacea = 0;
                            String str = " ";
                            switch (pl.elements[i].alignment) {
                                case Relative:
                                case Left:
                                    if (size > ellen) {
                                        spacea = size - ellen;
                                        str = pl.elements[i].element;
                                    } else
                                        str = pl.elements[i].element.substring(0, size);
                                    break;
                                case Right:
                                    if (size > ellen) {
                                        spaceb = size - ellen;
                                        str = pl.elements[i].element;
                                    } else
                                        str = pl.elements[i].element.substring(0, size);
                                    break;
                                case Center:
                                    if (size > ellen) {
                                        spacea = (size - ellen) / 2;
                                        spaceb = (size - ellen) / 2;
                                        str = pl.elements[i].element;
                                    } else
                                        str = pl.elements[i].element.substring(0, size);
                                    break;
                            }
                            byte[] ar_data = arabic6824.Convert(str, false);
                            if (spaceb > 0) lp.write(fillStr(" ", spaceb));
                            if (str.length() > 0)
                                if (isProbablyArabic(str))
//                                    lp.write(str);
                                    lp.write(ar_data);
                                else
                                    lp.write(str);
                            if (spacea > 0) lp.write(fillStr(" ", spacea));
                        }
                        lp.newLine(1);
                        lp.flush();
                        sleep(300);
                    }
                    break;
            }
            lp.setCompress(false);
        }
        return noLines;
    }

    private int PrintDocumentDotMatrixFooter() throws PrinterException {
        for (PrintLine pl : footerLines) {
            String lineStr = "";
            if (pl.printLineCharNum.equals(PrintLineCharNum.Compress)) {
                lp.setCompress(true);
            } else {
                lp.setCompress(false);
            }

            switch (pl.lineType) {
                case Space:
                    lp.newLine(1);
                    lp.flush();
                    break;
                case HeaderImage:
                    break;
                case FooterImage:
                    break;
                case Line:
                    lp.write(fillStr("-", PrintLineCharNum.Normal.getValue()));
                    lp.newLine(1);
                    lp.flush();
                    break;
                case Elements:
                    if (RTL) {
                        for (int i = pl.NumberOfElements - 1; i >= 0; i--) {
                            int size = pl.elementsRelativeWidth[i];
                            int ellen = pl.elements[i].element.length();
                            int spaceb = 0;
                            int spacea = 0;
                            String str = "";
                            switch (pl.elements[i].alignment) {
                                case Relative:
                                case Right:
                                    if (size > ellen) {
                                        spaceb = size - ellen;
                                        str = pl.elements[i].element;
                                    } else
                                        str = pl.elements[i].element.substring(0, size - 1);
                                    break;
                                case Left:
                                    if (size > ellen) {
                                        spacea = size - ellen;
                                        ;
                                        str = pl.elements[i].element;
                                    } else
                                        str = pl.elements[i].element.substring(0, size - 1);
                                    break;
                                case Center:
                                    if (size > ellen) {
                                        spacea = (size - ellen) / 2;
                                        spaceb = (size - ellen) / 2;
                                        str = pl.elements[i].element;
                                    } else
                                        str = pl.elements[i].element.substring(0, size - 1);
                                    break;
                            }

                            byte[] ar_data = arabic6824.Convert(str, false);
                            if (spaceb > 0) lp.write(fillStr(" ", spaceb));

                            if (str.length() > 0)
                                if (isProbablyArabic(str))
//                                    lp.write(str);
                                    lp.write(ar_data);
                                else
                                    lp.write(str);
                            if (spacea > 0) lp.write(fillStr(" ", spacea));
                        }
                        lp.newLine(1);
                        lp.flush();
                        sleep(300);
                    } else {
                        for (int i = 0; i < pl.NumberOfElements; i++) {
                            int size = pl.elementsRelativeWidth[i];
                            int ellen = pl.elements[i].element.length();
                            int spaceb = 0;
                            int spacea = 0;
                            String str = " ";
                            switch (pl.elements[i].alignment) {
                                case Relative:
                                case Left:
                                    if (size > ellen) {
                                        spacea = size - ellen;
                                        str = pl.elements[i].element;
                                    } else
                                        str = pl.elements[i].element.substring(0, size);
                                    break;
                                case Right:
                                    if (size > ellen) {
                                        spaceb = size - ellen;
                                        str = pl.elements[i].element;
                                    } else
                                        str = pl.elements[i].element.substring(0, size);
                                    break;
                                case Center:
                                    if (size > ellen) {
                                        spacea = (size - ellen) / 2;
                                        spaceb = (size - ellen) / 2;
                                        str = pl.elements[i].element;
                                    } else
                                        str = pl.elements[i].element.substring(0, size);
                                    break;
                            }
                            byte[] ar_data = arabic6824.Convert(str, false);
                            if (spaceb > 0) lp.write(fillStr(" ", spaceb));
                            if (str.length() > 0)
                                if (isProbablyArabic(str))
//                                    lp.write(str);
                                    lp.write(ar_data);
                                else
                                    lp.write(str);
                            if (spacea > 0) lp.write(fillStr(" ", spacea));
                        }
                        lp.newLine(1);
                        lp.flush();
                        sleep(300);
                    }
                    break;
            }
            lp.setCompress(false);
        }
        return footerLines.size();
    }

    private int PrintDocumentDotMatrixNoPages(int page, int noPages) throws PrinterException, InterruptedException {
        String noPagesString = page + " of " + noPages;
        PrintLine pl = new PrintLine(new PrintElement(false, TextAllignment.Left, PrintFontSize.Medium, noPagesString));
        lp.setCompress(pl.printLineCharNum.equals(PrintLineCharNum.Compress));

        switch (pl.lineType) {
            case Space:
                lp.newLine(1);
                lp.flush();
                break;
            case HeaderImage:
            case FooterImage:
                break;
            case Line:
                lp.write(fillStr("-", PrintLineCharNum.Normal.getValue()));
                lp.newLine(1);
                lp.flush();
                break;
            case Elements:
                if (RTL) {
                    for (int i = pl.NumberOfElements - 1; i >= 0; i--) {
                        int size = pl.elementsRelativeWidth[i];
                        int ellen = pl.elements[i].element.length();
                        int spaceb = 0;
                        int spacea = 0;
                        String str = "";
                        switch (pl.elements[i].alignment) {
                            case Relative:
                            case Right:
                                if (size > ellen) {
                                    spaceb = size - ellen;
                                    str = pl.elements[i].element;
                                } else
                                    str = pl.elements[i].element.substring(0, size - 1);
                                break;
                            case Left:
                                if (size > ellen) {
                                    spacea = size - ellen;
                                    ;
                                    str = pl.elements[i].element;
                                } else
                                    str = pl.elements[i].element.substring(0, size - 1);
                                break;
                            case Center:
                                if (size > ellen) {
                                    spacea = (size - ellen) / 2;
                                    spaceb = (size - ellen) / 2;
                                    str = pl.elements[i].element;
                                } else
                                    str = pl.elements[i].element.substring(0, size - 1);
                                break;
                        }

                        byte[] ar_data = arabic6824.Convert(str, false);
                        if (spaceb > 0) lp.write(fillStr(" ", spaceb));

                        if (str.length() > 0)
                            if (isProbablyArabic(str))
//                                    lp.write(str);
                                lp.write(ar_data);
                            else
                                lp.write(str);
                        if (spacea > 0) lp.write(fillStr(" ", spacea));
                    }
                } else {
                    for (int i = 0; i < pl.NumberOfElements; i++) {
                        int size = pl.elementsRelativeWidth[i];
                        int ellen = pl.elements[i].element.length();
                        int spaceb = 0;
                        int spacea = 0;
                        String str = " ";
                        switch (pl.elements[i].alignment) {
                            case Relative:
                            case Left:
                                if (size > ellen) {
                                    spacea = size - ellen;
                                    str = pl.elements[i].element;
                                } else
                                    str = pl.elements[i].element.substring(0, size);
                                break;
                            case Right:
                                if (size > ellen) {
                                    spaceb = size - ellen;
                                    str = pl.elements[i].element;
                                } else
                                    str = pl.elements[i].element.substring(0, size);
                                break;
                            case Center:
                                if (size > ellen) {
                                    spacea = (size - ellen) / 2;
                                    spaceb = (size - ellen) / 2;
                                    str = pl.elements[i].element;
                                } else
                                    str = pl.elements[i].element.substring(0, size);
                                break;
                        }
                        byte[] ar_data = arabic6824.Convert(str, false);
                        if (spaceb > 0) lp.write(fillStr(" ", spaceb));
                        if (str.length() > 0)
                            if (isProbablyArabic(str))
//                                    lp.write(str);
                                lp.write(ar_data);
                            else
                                lp.write(str);
                        if (spacea > 0) lp.write(fillStr(" ", spacea));
                    }
                }
                lp.newLine(1);
                lp.flush();
                sleep(300);
                break;
        }
        lp.setCompress(false);
        return 1;
    }

    public int stringWidth(String Str, int FontSize) {
        if (Str != null) {
            Paint.FontMetrics f = new Paint.FontMetrics();

            Paint p = new Paint();
            Rect bounds = new Rect();
            p.setTextSize(FontSize);
            p.getTextBounds(Str, 0, Str.length(), bounds);
            return bounds.width();
        }
        return 0;

    }

    private String fillStr(String C, int len) {
//        String result = "";
//        for (int i = 0; i <len; i++) result += C;
//
//        return result;
        if (len > 0) {
            StringBuffer outputBuffer = new StringBuffer(len);
            for (int i = 0; i < len; i++) {
                outputBuffer.append(C);
            }
            return outputBuffer.toString();
        } else
            return "";
    }

    public static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //region printing classes
    public enum TextAllignment {
        Relative,
        Right,
        Left,
        Center;
    }

    public enum PrintFontSize {
        Small(25),
        SmallMedium(35),
        Medium(50),
        Large(70);

        private PrintFontSize(final int value) {
            this.value = value;
        }

        private final int value;

        public int getValue() {
            return value;
        }
    }

    public enum PrintLineType {
        Elements,
        Line,
        HeaderImage,
        FooterImage,
        TLVQRCodeImage,
        Space,
        ForceNewPage,
        Remove_Force_New_Page,
    }

    public enum PrintLineScale {
        Light(1),
        Medium(3),
        Heavy(5);

        private PrintLineScale(final int value) {
            this.value = value;
        }

        private final int value;

        public int getValue() {
            return value;
        }
    }

    public enum PrintLineCharNum {
        Normal(78),
        Compress(134),
        Wide(44);

        private PrintLineCharNum(final int value) {
            this.value = value;
        }

        private final int value;

        public int getValue() {
            return value;
        }
    }
    //endregion

    public class BadPrinterStateException extends Exception {
        static final long serialVersionUID = 1;

        public BadPrinterStateException(String message) {
            super(message);
        }
    }

    private void readAssetFiles() {
        InputStream input = null;
        ByteArrayOutputStream output = null;
        AssetManager assetManager = context.getAssets();
        String[] files = {"printer_profiles.JSON", "honeywell_logo.bmp"};
        int fileIndex = 0;
        int initialBufferSize;

        try {
            for (String filename : files) {
                input = assetManager.open(filename);
                initialBufferSize = (fileIndex == 0) ? 8000 : 2500;
                output = new ByteArrayOutputStream(initialBufferSize);

                byte[] buf = new byte[1024];
                int len;
                while ((len = input.read(buf)) > 0) {
                    output.write(buf, 0, len);
                }
                input.close();
                input = null;

                output.flush();
                output.close();
                switch (fileIndex) {
                    case 0:
                        jsonCmdAttribStr = output.toString();
                        break;
                    case 1:
                        base64LogoPng = Base64.encodeToString(output.toByteArray(), Base64.DEFAULT);
                        break;
                }

                fileIndex++;
                output = null;
            }
        } catch (Exception ex) {
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
    }

    public static int lastElementWidth(PrintLineCharNum printLineCharNum, int[] widths) {
        int line = 0;
        for (int i = 0; i < widths.length - 1; i++) {
            line += widths[i];
        }
        return printLineCharNum.getValue() - line;

    }

    public byte[] convertTo1BPP(Bitmap inputBitmap, int darknessThreshold) {
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

    private byte[] intToWord(int parValue) {
        byte[] retValue = new byte[]{(byte) (parValue & 255), (byte) (parValue >> 8 & 255)};
        return retValue;
    }

    private byte[] intToDWord(int parValue) {
        byte[] retValue = new byte[]{(byte) (parValue & 255), (byte) (parValue >> 8 & 255), (byte) (parValue >> 16 & 255), (byte) (parValue >> 24 & 255)};
        return retValue;
    }

    private int getAlignment(TextAllignment allignment) {
        int align = 60;
        if (allignment == TextAllignment.Center) {
            align = (960 - 200) / 2;
        } else if (allignment == TextAllignment.Right) {
            align = (960 - 10) - 200;
        }
        return align;
    }

    public static boolean isProbablyArabic(String s) {
        for (int i = 0; i < s.length(); ) {
            int c = s.codePointAt(i);
            if (c >= 0x0600 && c <= 0x06E0)
                return true;
            i += Character.charCount(c);
        }
        return false;
    }
}
