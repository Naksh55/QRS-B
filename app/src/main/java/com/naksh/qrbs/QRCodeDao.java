package com.naksh.qrbs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface QRCodeDao {

    @Insert
    void insert(QRCodeEntity entity);

    @Delete
    void delete(QRCodeEntity entity);

    @Query("SELECT * FROM qr_codes ORDER BY id DESC")  // ✅ Correct table name
    LiveData<List<QRCodeEntity>> getAllQRCodeHistory();
}

