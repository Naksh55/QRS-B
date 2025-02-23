package com.naksh.qrbs;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

public class QRCodeHistoryManager {

    private QRCodeDao qrCodeDao;

    public QRCodeHistoryManager(Application application) {
        QRCodeDatabase db = QRCodeDatabase.getDatabase(application);
        qrCodeDao = db.qrCodeDao();
    }

    // Save a QR code entry (either generated or scanned)
    // Log inserted data inside saveQRCodeHistory method
    public void saveQRCodeHistory(final String qrText, final boolean isScanned) {
        QRCodeEntity qrCodeEntity = new QRCodeEntity();
        qrCodeEntity.setQrText(qrText);
        qrCodeEntity.setScanned(isScanned);

        new Thread(() -> {
            qrCodeDao.insert(qrCodeEntity);
            Log.d("QRCodeHistoryManager", "Inserted QR Code: " + qrText); // Log for verification
        }).start();
    }


    // Load all QR code history entries
    public LiveData<List<QRCodeEntity>> getAllQRCodeHistory() {
        return qrCodeDao.getAllQRCodeHistory();
    }
}
