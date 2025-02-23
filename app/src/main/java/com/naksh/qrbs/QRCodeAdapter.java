package com.naksh.qrbs;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class QRCodeAdapter extends RecyclerView.Adapter<QRCodeAdapter.QRCodeViewHolder> {

    private List<QRCodeEntity> qrCodeHistoryList;
    private Context context;

    // Ensure constructor accepts Context and List<QRCodeEntity> properly
    public QRCodeAdapter(Context context, List<QRCodeEntity> qrCodeHistoryList) {
        this.context = context;
        this.qrCodeHistoryList = qrCodeHistoryList;
    }

    @NonNull
    @Override
    public QRCodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_qr_code, parent, false);
        return new QRCodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QRCodeViewHolder holder, int position) {
        QRCodeEntity qrCodeEntity = qrCodeHistoryList.get(position);

        holder.qrCodeTextView.setText(qrCodeEntity.qrText);
        holder.isScannedTextView.setText(qrCodeEntity.isScanned ? "Scanned" : "Generated");

        // Handle click to open URL or show dialog with the content
        holder.itemView.setOnClickListener(v -> {
            String qrContent = qrCodeEntity.qrText;
            if (qrContent.startsWith("http://") || qrContent.startsWith("https://")) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(qrContent));
                context.startActivity(browserIntent);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("QR Code Content")
                        .setMessage(qrContent)
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return qrCodeHistoryList.size();
    }

    public void setQRCodeHistory(List<QRCodeEntity> newQRCodeHistoryList) {
        qrCodeHistoryList.clear();
        qrCodeHistoryList.addAll(newQRCodeHistoryList);
        notifyDataSetChanged();
    }

    public static class QRCodeViewHolder extends RecyclerView.ViewHolder {
        TextView qrCodeTextView;
        TextView isScannedTextView;

        public QRCodeViewHolder(@NonNull View itemView) {
            super(itemView);
            qrCodeTextView = itemView.findViewById(R.id.tv_qr_code_text);
            isScannedTextView = itemView.findViewById(R.id.tv_is_scanned);
        }
    }
}
