package com.naksh.qrbs;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private QRCodeAdapter qrCodeAdapter;
    private QRCodeHistoryManager historyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        historyManager = new QRCodeHistoryManager(getApplication());

        // Set up the adapter
        qrCodeAdapter = new QRCodeAdapter(this, new ArrayList<>());
        qrCodeAdapter.setOnDeleteClickListener(entity -> {
            historyManager.deleteQRCode(entity); // You must implement this
        });

        recyclerView.setAdapter(qrCodeAdapter);

        // Observe history changes
        historyManager.getAllQRCodeHistory().observe(this, qrCodeEntities -> {
            qrCodeAdapter.setQRCodeHistory(qrCodeEntities);
        });
    }
}
