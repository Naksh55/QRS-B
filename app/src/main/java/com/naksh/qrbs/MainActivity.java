package com.naksh.qrbs;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.naksh.qrbs.CaptureAct;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btn_scan;
    Bitmap qrBitmap;


    EditText et_input;
    Button btn_generate_qr;
    ImageView iv_qr_code;
    Button history_btn;
    private QRCodeHistoryManager historyManager;

    private RecyclerView recyclerView;
    private QRCodeAdapter qrCodeAdapter;
    private QRCodeDatabase qrCodeDatabase;
    private List<QRCodeEntity> qrCodeHistory;
    // Inside your scan result listener, call saveScannedQRCode()
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            String scannedContent = result.getContents();

            // Save the scanned content (URL or text) to history
            saveScannedQRCode(scannedContent);

            // Check if the scanned content is a URL
            if (scannedContent.startsWith("http://") || scannedContent.startsWith("https://")) {
                // Open the URL in the browser
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(scannedContent));
                startActivity(browserIntent);
            } else {
                // Show the scanned content in a dialog if it's not a URL
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Result");
                builder.setMessage(scannedContent);
                builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss()).show();
            }
        }
    });




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_input = findViewById(R.id.et_input);
        btn_generate_qr = findViewById(R.id.btn_generate_qr);
        qrCodeAdapter = new QRCodeAdapter(this, new ArrayList<QRCodeEntity>());

        iv_qr_code = findViewById(R.id.iv_qr_code);
        historyManager = new QRCodeHistoryManager(getApplication());
        historyManager.getAllQRCodeHistory().observe(this, qrCodeEntities -> {
            // When the data changes, update the adapter with the new list of QR code entries
            if (qrCodeEntities != null) {
                qrCodeAdapter.setQRCodeHistory(qrCodeEntities); // Pass the list to the adapter
            }
        });
        history_btn=findViewById(R.id.btn_view_history);
        history_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,HistoryActivity.class);
                startActivity(intent);
            }
        });
        historyManager.getAllQRCodeHistory().observe(this, qrCodeEntities -> {
            // Check if the list is not null
            if (qrCodeEntities != null) {
                qrCodeAdapter.setQRCodeHistory(qrCodeEntities);  // Update the adapter with the new list
            }
        });
//        recyclerView = findViewById(R.id.recyclerView);
//        qrCodeDatabase = QRCodeDatabase.getDatabase(this);
//
//        // Set up RecyclerView
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        // You can later set the adapter to display the QR code history
//        qrCodeAdapter = new QRCodeAdapter(new ArrayList<>());
//        recyclerView.setAdapter(qrCodeAdapter);
//
//        // Load history from database
//        loadHistory();

        Button btnShare = findViewById(R.id.btn_share_qr);

        btnShare.setOnClickListener(v -> {
            if (qrBitmap != null) {
                shareQRCode(qrBitmap);
            } else {
                Toast.makeText(MainActivity.this, "Please generate a QR code first", Toast.LENGTH_SHORT).show();
            }
        });


        btn_generate_qr.setOnClickListener(v -> generateQRCode());
        btn_scan = findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(v -> scanCode());
    }

    private void generateQRCode() {
        String text = et_input.getText().toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(this, "Please enter text", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 400, 400);
            iv_qr_code.setImageBitmap(bitmap);
            saveQRCode(bitmap);

            // Save the generated QR code to qrBitmap
            qrBitmap = bitmap;

            // Save the generated QR code to history (isScanned = false)
            saveQRCodeToHistory(text, false);  // False means this is a generated QR code

            // Make the QR CardView and share button visible
            findViewById(R.id.qr_card).setVisibility(View.VISIBLE);  // Make QR CardView visible
            findViewById(R.id.btn_share_qr).setVisibility(View.VISIBLE);  // Ensure Share button is visible
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to generate QR Code", Toast.LENGTH_SHORT).show();
        }
    }



    private void saveQRCode(Bitmap bitmap) {
        try {
            String fileName = "QRCode_" + System.currentTimeMillis() + ".png";
            MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, fileName, "Generated QR Code");
            Toast.makeText(this, "QR Code saved to gallery", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save QR Code", Toast.LENGTH_SHORT).show();
        }
    }

    private void scanCode() {
        ScanOptions scanOptions = new ScanOptions();
        scanOptions.setPrompt("Volume up to flash on");
        scanOptions.setBeepEnabled(true);
        scanOptions.setOrientationLocked(true);
        scanOptions.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(scanOptions);
    }


    private void shareQRCode(Bitmap bitmap) {
        try {
            // Create a file to save the QR code
            File qrCodeFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "QRCode.png");

            // Create a file output stream
            FileOutputStream fos = new FileOutputStream(qrCodeFile);
            // Compress the bitmap into the file
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            // Get URI using FileProvider
            Uri uri = FileProvider.getUriForFile(this, "com.naksh.qrbs.fileprovider", qrCodeFile);

            // Create share intent
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/png");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra(Intent.EXTRA_TEXT, "Here is a QR Code generated using my app!");

            // Grant temporary permission to the receiving app
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Start the share intent
            startActivity(Intent.createChooser(intent, "Share QR Code via"));

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to share QR Code", Toast.LENGTH_SHORT).show();
        }
    }
//    private void loadHistory() {
//        new Thread(() -> {
//            qrCodeHistory = qrCodeDatabase.qrCodeDao().getAllQRCodeHistory();
//            runOnUiThread(() -> qrCodeAdapter.notifyDataSetChanged());
//        }).start();
//    }
    private void saveQRCodeToHistory(String qrText, boolean isScanned) {
        historyManager.saveQRCodeHistory(qrText, isScanned); // Save QR code to history
    }
    private void saveScannedQRCode(String scannedContent) {
        // Save the scanned QR code to history (it can be a URL or text)
        saveQRCodeToHistory(scannedContent, true); // true indicates it was scanned
    }


    // Call saveQRCodeToHistory() when QR code is generated or scanned

}
