package com.inovaeg.androidtrainingpharos2020;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private String TAG = this.getClass().getSimpleName();
    private int cameraPermissionCode = 1;
    private int capturePhotoCode = 2;
    private int locationPermissionCode=3;
    private int accessStorage=4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG , "OnCreate:");
        findViewById(R.id.bt_image_capture).setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},accessStorage);
            } else {
                Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent,accessStorage);
            }

        });
        findViewById(R.id.bt_take_permission).setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},cameraPermissionCode);
            } else {
                Toast.makeText(getApplicationContext(),"you have permission to access the camera",Toast.LENGTH_SHORT).show();
            }

        });
        findViewById(R.id.bt_start_camera).setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),"please request permission first",Toast.LENGTH_SHORT).show();

            } else {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, capturePhotoCode);
                }

            }

        });
        findViewById(R.id.bt_check_location).setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        locationPermissionCode);
            } else {
                addLocationListener();
            }

        });
    }
    @SuppressLint("MissingPermission")
    private void addLocationListener() {
        showToast("adding location Listener");
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        Uri gmmIntentUri = Uri.parse("geo:" + location.getLatitude() + "," + location.getLongitude());
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        if (mapIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(mapIntent);
                        }
                        locationManager.removeUpdates(this);

                    }
                });
    }
    private void showToast(@NonNull String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG , "OnStart:");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG , "OnResume:");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG , "OnPause:");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG , "OnStop:");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG , "OnDestory:");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG , "OnRestart:");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == cameraPermissionCode) {
            if (checkGrantedPermission(grantResults)) {
                showToast("you have taken Camera permission");
            } else showToast("camera permission was denied");
        } else if (requestCode == locationPermissionCode) {
            if (checkGrantedPermission(grantResults)) {
                showToast("you have taken location permission");
                addLocationListener();
            } else showToast("location permission was denied");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == capturePhotoCode && resultCode == RESULT_OK && data != null && data.getExtras() != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ((ImageView) findViewById(R.id.imageView)).setImageBitmap(imageBitmap);
        }
        else if (requestCode == accessStorage && resultCode == -1 && data != null){
            Uri imageUri = data.getData();
            ImageView picturePosition = findViewById(R.id.imageView);
            picturePosition.setImageURI(imageUri);
        }

    }

    private boolean checkGrantedPermission(@NonNull int[] grantedPermission) {
        return grantedPermission.length > 0 &&
                grantedPermission[0] == PackageManager.PERMISSION_GRANTED;
    }
}