package com.ganeshrajappan.qrcode;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.ganeshrajappan.qrcode.Printing.PrintLine;
import com.ganeshrajappan.qrcode.Printing.PrintUtility;
import com.ganeshrajappan.qrcode.zatca.QRCode;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.honeywell.mobility.print.LinePrinter;
import com.honeywell.mobility.print.LinePrinterException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.ganeshrajappan.qrcode.Zxing.saveBitmap;
import static com.ganeshrajappan.qrcode.Zxing.drawQRCode;
import static com.ganeshrajappan.qrcode.Printer.readAssetFiles;
import static com.ganeshrajappan.qrcode.Printer.convertTo1BPP;


public class MainActivity extends AppCompatActivity {

    private EditText qrcodeText, sizeWidth, sizeHeight, customDark;
    private MaterialRadioButton custombtn;
    private MaterialRadioButton customBarCode, customNoBarCode;
    private ShapeableImageView qrcodeView;
    private MaterialTextView statusView;
    private MaterialButton btn100, btn150, btn200, btn250;
    private MaterialButton btnMiddle;
    private MaterialButton btnRight;
    private FloatingActionButton fab, printFab;
    private String activeFileName = "";

    private Handler handler;
    private Bitmap bitmap = null;

    private String jsonCmdAttribStr = null;
    private Size SIZE = new Size(150 , 150);
    private int DARKNESS = 100;
    private boolean printBarCode = true;


    private List<PrintLine> printLines;
    private List<PrintLine> headerPrintLines;
    private List<PrintLine> footerPrintLines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();

        printLines = getPrintLinesList(this);
        headerPrintLines = getHeaderPrintLinesList(this);
        footerPrintLines = getFooterPrintLinesList(this);

        handler = new Handler();
        boolean isPermision = isStoragePermissionGranted();
        Log.d("isPermision", String.valueOf(isPermision));

        btn150.setOnClickListener(view -> {
            custombtn.setChecked(false);
            getQrCode(getBase64());
        });
        btn100.setOnClickListener(view -> {
            custombtn.setChecked(false);

            getQrCode(getBase64());
        });
        btn200.setOnClickListener(view -> {
            custombtn.setChecked(false);
            getQrCode(getBase64());
        });
        btn250.setOnClickListener(view -> {
            custombtn.setChecked(false);

            getQrCode(getBase64());
        });
        fab.setOnClickListener(view -> showBottomSheet(getBase64()));
        printFab.setOnClickListener(view -> {
            //only when QRCode is loaded and showing in Imageview
            if (qrcodeView.getDrawable() != null) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDate = new SimpleDateFormat("ddMMyyyyHHMMSS");
                String strDt = simpleDate.format(new Date());
                //save the bitmap to a local file in QRCode folder
                activeFileName = saveBitmap(bitmap, strDt, MainActivity.this);
                CallSnack("Saved " + activeFileName);

                Log.d("before call Fn","before call Fn");
                //SaveImage(bitmap);
                //show printer selection dialog
                showPrintDialog();



            } else {
                Snackbar.make(view, "Please generate QRCode!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });
        sizeHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (charSequence.toString().trim().length() > 0) {
                        if (Integer.parseInt(charSequence.toString()) <= 1000) {
                            if (sizeWidth.getText().toString().trim().length() > 0) {
                                if (Integer.parseInt(sizeWidth.getText().toString()) <= 1000) {

                                    getQrCode(getBase64());
                                }
                            }
                        }
                    }
                } catch (NumberFormatException nfe) {
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        sizeWidth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (charSequence.toString().trim().length() > 0) {
                        if (Integer.parseInt(charSequence.toString()) <= 1000) {
                            if (sizeHeight.getText().toString().trim().length() > 0) {
                                if (Integer.parseInt(sizeHeight.getText().toString()) <= 1000) {
                                    getQrCode(getBase64());
                                }
                            }
                        }
                    }
                } catch (NumberFormatException nfe) {
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        customDark.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (charSequence.toString().trim().length() > 0) {
                        DARKNESS = Integer.parseInt(charSequence.toString());
                    }
                } catch (Exception nfe) {
                    nfe.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        // custom size radio button selected
        custombtn.setOnClickListener(view -> getQrCode(getBase64()));
        customBarCode.setOnClickListener(view -> printBarCode = true);
        customNoBarCode.setOnClickListener(view -> printBarCode = false);

    }

    private static void SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File myDir = new File(root);
        Log.d("root folder",root);
        myDir.mkdirs();

        String fname = "Image-"+ "qrcode" +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Log.d("log","Inside The Try");

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("EndOfMethod Tag", "End Of Method");
    }
    private void CallSnack(String msg) {
        Snackbar sbar;
        sbar = Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);
        View sbView = sbar.getView();
        sbView.setBackgroundColor(getResources().getColor(R.color.black));
        sbar.setAction("Action", null).show();
    }

    private void showPrintDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Printer");
        //bring up the bluetooth printers list already bonded
        final List<String> bluetoothRP4 = new ArrayList<>();
        try {
            Set<BluetoothDevice> bondedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
            for (BluetoothDevice device : bondedDevices) {
                int deviceBTMajorClass = device.getBluetoothClass().getMajorDeviceClass();
                if (deviceBTMajorClass == BluetoothClass.Device.Major.IMAGING) {
                    //only 682x printers
                    if (device.getName().startsWith("682") || device.getName().contains("6824") || device.getName().contains("6822")) {
                        bluetoothRP4.add(device.getName() + "\n" + device.getAddress());
                    }
                }
            }
        } catch (IllegalStateException ise) {
            ise.printStackTrace();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, bluetoothRP4);
        builder.setAdapter(dataAdapter, (dialog, which) -> {
            //split the printer name and mac address
            String[] bluetoothPrinter = bluetoothRP4.get(which).split("\n");
            //get the printer_profiles.JSON from assets folder and read it into variable
            jsonCmdAttribStr = readAssetFiles(getApplicationContext());
            new printTask(bluetoothPrinter[1], jsonCmdAttribStr, SIZE, getAlignment(), DARKNESS, printBarCode) {
                @SuppressLint("StaticFieldLeak")
                @Override
                public void onPostExecute(String result) {
                    DisplayStatusMessage(result);
                }

                @Override
                public void onPreExecute() {
                    DisplayStatusMessage("Printing QRCode image...");
                }

                @Override
                protected void onProgressUpdate(String... progress) {
                    DisplayStatusMessage(progress[0]);
                }
            }.execute();

        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setupUI() {
        qrcodeView = findViewById(R.id.sImageView);
        qrcodeText = findViewById(R.id.qrcodeText);
        fab = findViewById(R.id.qrCodeFab);
        printFab = findViewById(R.id.printqrCodeFab);
        btn150 = findViewById(R.id.btn150px);
        btn100 = findViewById(R.id.btn100px);
        btn200 = findViewById(R.id.btn200px);
        btn250 = findViewById(R.id.btn250px);
        statusView = findViewById(R.id.statusView);
        MaterialButton btnLeft = findViewById(R.id.btnLEFT);
        btnMiddle = findViewById(R.id.btnCenter);
        btnRight = findViewById(R.id.btnRight);
        sizeWidth = findViewById(R.id.customWidth);
        sizeHeight = findViewById(R.id.customHeight);
        custombtn = findViewById(R.id.customrb);
        customBarCode = findViewById(R.id.customBarCode);
        customNoBarCode = findViewById(R.id.customNoBarCode);
        customDark = findViewById(R.id.customDark);
    }

    private void DisplayStatusMessage(final String MsgStr) {
        handler.post(() -> statusView.setText(MsgStr));
    }

    private Size getSizeQrCode() {
        Size size = new Size(100, 100);
        if (custombtn.isChecked()) {
            String widthTxt = sizeWidth.getText().toString().trim();
            String heightTxt = sizeHeight.getText().toString().trim();
            if ((widthTxt.length() > 0) && (heightTxt.length() > 0)) {
                try {
                    int widthSize = Integer.parseInt(widthTxt);
                    int heightSize = Integer.parseInt(heightTxt);
//                    if ((widthSize < 250) && (heightSize < 250)) {
                        size = new Size(widthSize, heightSize);
//                    }
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                    Toast.makeText(this, nfe.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        } else {
            if (btn150.isChecked())
                size = new Size(150, 150);
            else if (btn200.isChecked())
                size = new Size(200, 200);
            else if (btn250.isChecked())
                size = new Size(250, 250);
        }
        return size;
    }

    private int getAlignment() {
        //LEFT = 72 dots
        int align = 72;
        if (btnMiddle.isChecked()) {
            // deduct the qrcode width from the total print width and divide by 2
            align = (960 - bitmap.getWidth()) / 2;
        } else if (btnRight.isChecked()) {
            // total print width - the margin 10 dots
            // then minus the qrcode width
            align = (960 - 10) - bitmap.getWidth();
        }
        return align;
    }

    private boolean isStoragePermissionGranted() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return false;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        } else {
            Toast.makeText(getApplicationContext(), "User denied storage permission which is required for this app to work!", Toast.LENGTH_LONG).show();
            finish();

        }
    }

    private void getQrCode(String base64) {
        if (qrcodeText.getText().toString().trim().length() == 0) {
            CallSnack("Please enter QRCode text to encode");
            qrcodeText.requestFocus();
            return;
        }
        SIZE = getSizeQrCode();
        if (isStoragePermissionGranted()) {
            try {
                bitmap = drawQRCode(new String(base64.getBytes("UTF-8")), SIZE.getWidth(), SIZE.getHeight());
            } catch (UnsupportedEncodingException e) {
                CallSnack(e.getMessage());
            }
            // update the imageview to show the QRCode
            qrcodeView.setImageBitmap(bitmap);
        }
    }

    private class printTask extends AsyncTask<String, String, String> {
        private static final String PROGRESS_CANCEL_MSG = "Printing cancelled";
        private static final String PROGRESS_COMPLETE_MSG = "Printing completed";
        private static final String PROGRESS_ENDDOC_MSG = "End of document";
        private static final String PROGRESS_FINISHED_MSG = "Printer connection closed";
        private static final String PROGRESS_NONE_MSG = "Unknown progress message";
        private static final String PROGRESS_STARTDOC_MSG = "Start printing document";

        String printer_address;
        String json;
        Size size;
        int position;
        int dark;
        boolean printBarCode;

        public printTask(String PrinterAddress, String JSON, Size SIZE, int alignpos, int dark, boolean printBarCode) {
            printer_address = PrinterAddress;
            json = JSON;
            size = SIZE;
            position = alignpos;
            this.dark = dark;
            this.printBarCode = printBarCode;
        }

        @Override
        protected String doInBackground(String... args) {
            LinePrinter lp = null;
            String sPrinterID = "P6824BT";
            String sPrinterURI = "bt://" + printer_address;
            LinePrinter.ExtraSettings exSettings = new LinePrinter.ExtraSettings();
            exSettings.setContext(MainActivity.this);
            try {
                publishProgress("Creating LP...");
                lp = new LinePrinter(
                        json,
                        sPrinterID,
                        sPrinterURI,
                        exSettings);
                //A retry sequence in case the bluetooth socket is temporarily not ready
                publishProgress("Trying to connect...");
                int numtries = 0;
                int maxretry = 4;
                while (numtries < maxretry) {
                    try {
                        lp.connect();  // Connects to the printer
                        break;
                    } catch (LinePrinterException ex) {
                        numtries++;
                        Thread.sleep(1000);
                    }
                }
                if (numtries == maxretry) lp.connect();//Final retry
                // Check the state of the printer and abort printing if there are
                // any critical errors detected.
                int[] results = lp.getStatus();
                if (results != null) {
                    for (int err = 0; err < results.length; err++) {
                        if (results[err] == 223) {
                            // Paper out.
                            throw new BadPrinterStateException("Paper out");
                        } else if (results[err] == 227) {
                            // Lid open.
                            throw new BadPrinterStateException("Printer lid open");
                        }
                    }
                }
                publishProgress("Connected LP...");
                bitmap = drawQRCode(new String(getBase64().getBytes("UTF-8")), SIZE.getWidth(), SIZE.getHeight());

                byte[] output = convertTo1BPP(bitmap, dark);
                publishProgress("Converted to 1BPP...");
                //region above qr code
                lp.write("Test Print Data top Of Paper");
                lp.newLine(1);
                //Start
                //region print of qr
                String base64QRcode = Base64.encodeToString(output, Base64.DEFAULT);
                // Prints the Honeywell logo graphic.
                publishProgress(PROGRESS_STARTDOC_MSG);
                lp.write("--------------------------------------- Start Page");
                lp.writeGraphicBase64(base64QRcode,
                        LinePrinter.GraphicRotationDegrees.DEGREE_0,
                        position,  // Offset in printhead dots from the left of the page
                        size.getWidth(), // Desired graphic width on paper in printhead dots
                        size.getHeight()); // Desired graphic height on paper in printhead dots
                //endregion
                //region bottom of qr
                lp.write("--------------------------------------- End Page");
                lp.newLine(4);
                lp.flush();
                //endregion

//                new PrintUtility(MainActivity.this, lp, bitmap, size, dark,
//                        printLines,
//                        headerPrintLines,
//                        footerPrintLines);

                publishProgress(PROGRESS_ENDDOC_MSG);
                publishProgress(PROGRESS_COMPLETE_MSG);
                return "Completed.";
            } catch (BadPrinterStateException ex) {
                return "Printer error detected: " + ex.getMessage() + ". Please correct the error and try again.";
            } catch (LinePrinterException ex) {
                return "LinePrinterException: " + ex.getMessage();
            } catch (Exception ex) {
                if (ex.getMessage() != null)
                    return "Unexpected exception: " + ex.getMessage();
                else
                    return "Unexpected exception.";
            } finally {
                if (lp != null) {
                    try {
                        lp.disconnect();  // Disconnects from the printer
                        lp.close();  // Releases resources
                        publishProgress(PROGRESS_FINISHED_MSG);
                    } catch (Exception ex) {
                    }
                }
            }

        }

    }

    public class BadPrinterStateException extends Exception {
        static final long serialVersionUID = 1;

        public BadPrinterStateException(String message) {
            super(message);
        }
    }

    private void showBottomSheet(String base64) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog, null);
        TextView base64TextView = view.findViewById(R.id.base64text);
        ImageView qrCodeImageView = view.findViewById(R.id.qrcode_img);

        base64TextView.setText(base64);
        getQrCode(base64);
        qrCodeImageView.setImageBitmap(bitmap);

        dialog.setContentView(view);
        dialog.show();
    }

    private String getBase64() {
        String currentDate = getCurrentDateFormatted() + " " + getCurrentTime();
        return QRCode.eInvoiceBase62("Al-Watania Poultry", "300000329310003",
                currentDate
                , 43750 + "", 110432 + "");
    }

    //region printing lines list 01062022
    public static void setPrintLinesList(Context context, List<PrintLine> printLines) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("PRINT_LINES_LINES", new Gson().toJson(printLines));
        prefsEditor.apply();
    }

    public List<PrintLine> getPrintLinesList(Context context) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = mPrefs.getString("PRINT_LINES_LINES", "");
        return new Gson().fromJson(json, new TypeToken<List<PrintLine>>() {
        }.getType());
    }

    public List<PrintLine> getHeaderPrintLinesList(Context context) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = mPrefs.getString("HEADER_PRINT_LINES_LINES", "");

        return new Gson().fromJson(json, new TypeToken<List<PrintLine>>() {
        }.getType());
    }

    public List<PrintLine> getFooterPrintLinesList(Context context) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = mPrefs.getString("FOOTER_PRINT_LINES_LINES", "");

        return new Gson().fromJson(json, new TypeToken<List<PrintLine>>() {
        }.getType());
    }
    //endregion end by negm 09122019

    public static String doubleToStringCurrency(double number) {
        DecimalFormatSymbols symbolsEN_US = DecimalFormatSymbols.getInstance(Locale.US);
        DecimalFormat formatEN_US = new DecimalFormat("#,##0.00", symbolsEN_US);
        return formatEN_US.format(number);
    }


    public static String getCurrentDateFormatted() {
        Calendar c = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date tmp = c.getTime();
        return dateFormat.format(tmp);
    }

    public static String getCurrentTime() {
        Calendar c = Calendar.getInstance();
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        Date tmp = c.getTime();
        return timeFormat.format(tmp);
    }
}