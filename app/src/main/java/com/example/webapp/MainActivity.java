package com.example.webapp;
import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int MANAGE_STORAGE_PERMISSION_REQUEST_CODE = 101;
    PDFView pdfView;
    public static String asset = "nid.pdf";
    LottieAnimationView pdf;
    Button downloadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        pdf = findViewById(R.id.pdf);
        pdfView = findViewById(R.id.pdfView);
        downloadButton = findViewById(R.id.downloadButton);

        // PDF লোড করা
        pdfView.fromAsset(asset)
                .onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {
                        Toast.makeText(MainActivity.this, "PDF loaded", Toast.LENGTH_SHORT).show();
                    }
                })
                .load();

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                        downloadPDF();
                    } else {
                        showManageStoragePermissionAlert();
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkPermission()) {
                        downloadPDF();
                    } else {
                        showPermissionAlert();
                    }
                } else {
                    downloadPDF();
                }
            }
        });
    }

    // পারমিশন চেক
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    // পারমিশন রিকোয়েস্ট
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    // MANAGE_EXTERNAL_STORAGE পারমিশন ডায়ালগ
    private void showManageStoragePermissionAlert() {
        new AlertDialog.Builder(this)
                .setTitle("Manage Storage Permission Needed")
                .setMessage("This app needs manage storage permission to download the PDF. Please grant the permission.")
                .setPositiveButton("OK", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, MANAGE_STORAGE_PERMISSION_REQUEST_CODE);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    // পারমিশন ডায়ালগ
    private void showPermissionAlert() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Needed")
                .setMessage("This app needs storage permission to download the PDF. Please grant the permission.")
                .setPositiveButton("OK", (dialog, which) -> requestPermission())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    // পারমিশন রেসাল্ট হ্যান্ডেল করা
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                downloadPDF();
            } else {
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // PDF ডাউনলোড ফাংশন
    private void downloadPDF() {
        try {
            InputStream asset = getAssets().open(MainActivity.asset);

            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, MainActivity.asset);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            Uri uri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), contentValues);

            OutputStream outputStream = getContentResolver().openOutputStream(uri);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = asset.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            asset.close();

            Toast.makeText(MainActivity.this, "PDF Downloaded successfully", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Download failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
