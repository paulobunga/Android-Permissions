package com.example.androidpermissions;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Button mPermission;
    public static final int REQUEST_PERMISSIONS = 1;
    String[] appPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPermission = findViewById(R.id.button);
        mPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAndRequestPermissions()) {
                    Toast.makeText(MainActivity.this, "semua izin diberikan", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean checkAndRequestPermissions() {
        // check which permission are granted
        // periksa izin mana yang diberikan
        List<String> listPermissionsNeed = new ArrayList<>();
        for (String perm : appPermissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeed.add(perm);
            }
        }

        // ask for non-granted permissions
        // meminta izin yang tidak diberikan
        if (!listPermissionsNeed.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeed.toArray(new String[listPermissionsNeed.size()]),
                    REQUEST_PERMISSIONS
            );
            return false;
        }

        // App has all permissions. proceed ahead
        // Aplikasi memiliki semua izin. lanjutkan
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS) {
            HashMap<String, Integer> permissionResult = new HashMap<>();
            int deniedCount = 0;

            // gather permissions grant results
            // mengumpulkan hasil izin izin
            for (int i = 0; i < grantResults.length; i++) {
                // add only permissions which are denied
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    permissionResult.put(permissions[i], grantResults[i]);
                    deniedCount++;
                }
            }

            if (deniedCount == 0) {
                // proceed ahead with the app
                // lanjutkan dengan aplikasi
                Toast.makeText(this, "Success grant allow", Toast.LENGTH_SHORT).show();

            } else {
                // at least one or all permissions are denied
                // setidaknya satu atau semua izin ditolak
                for (Map.Entry<String, Integer> entry : permissionResult.entrySet()) {
                    String permName = entry.getKey();
                    int permResult = entry.getValue();

                    // permission is denied (this is the first time, when "never ask again" is not checked)
                    // izin ditolak (ini adalah pertama kalinya, ketika "tidak pernah bertanya lagi" tidak dicentang)

                    // so ask again explaining the usage permission
                    // jadi tanyakan lagi menjelaskan izin penggunaan

                    // shouldShowRequestPermissionRationale will return true
                    // shouldShowRequestPermissionRationale akan mengembalikan true
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permName)) {
                        // show dialog of explanation
                        showDialog("", "This app need contact and storage permissions to work without problem.",
                                "Yes, Grant Permissions",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        checkAndRequestPermissions();
                                    }
                                },
                                "No, Exit app",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                }, false);
                        break;

                        // permissions is denied (and never ask again if checked)
                        // izin ditolak (dan tidak pernah bertanya lagi jika dicentang)
                        // shouldShowRequestPermissionRationale will return false
                        // shouldShowRequestPermissionRationale akan mengembalikan false
                    } else {
                        // Ask user to go to settings and manually allow permissions
                        // Minta pengguna untuk masuk ke pengaturan dan secara manual mengizinkan izin
                        showDialog("",
                                "You have denied some permissions. Allow all permissions at [Setting] > [Permissions]",
                                "Goto Settings",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        // goto app settings
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", getPackageName(), null));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                },
                                "No, Exit app", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                }, false
                                );
                        break;
                    }
                }
            }
        }
    }

    private AlertDialog showDialog(String title, String msg, String positiveLabel,
                            DialogInterface.OnClickListener positiveClik,
                            String negativeLabel, DialogInterface.OnClickListener negativeOnClik,
                            boolean isCancelable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setCancelable(isCancelable);
        builder.setMessage(msg);
        builder.setPositiveButton(positiveLabel, positiveClik);
        builder.setNegativeButton(negativeLabel, negativeOnClik);

        AlertDialog alert = builder.create();
        alert.show();
        return alert;
    }
}
