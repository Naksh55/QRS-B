package com.naksh.qrbs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface QRCodeDao {

    @Insert
    void insert(QRCodeEntity qrCodeEntity);

    // Ensure only one method for getting QR code history
    @Query("SELECT * FROM qr_codes ORDER BY id DESC")
    LiveData<List<QRCodeEntity>> getAllQRCodeHistory();  // Single definition
}
