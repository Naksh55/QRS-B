package com.naksh.qrbs;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "qr_codes")
public class QRCodeEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String qrText;  // The text of the QR code
    public boolean isScanned;  // Whether the QR code was scanned or generated

    // Getter and Setter methods
    public String getQrText() {
        return qrText;
    }

    public void setQrText(String qrText) {
        this.qrText = qrText;
    }

    public boolean isScanned() {
        return isScanned;
    }

    public void setScanned(boolean scanned) {
        isScanned = scanned;
    }
}
