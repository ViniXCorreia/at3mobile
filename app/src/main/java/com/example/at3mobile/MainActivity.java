package com.example.at3mobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;

    private String currentPhotoPath;
    private File photoFile = null;
    private Location location = null;
    private DecimalFormat df = new DecimalFormat("0.0000");
    private Bitmap imageBitmap = null;

    private ImageUploader imageUploader = new ImageUploader() {
        @Override
        public void done() {
            Toast.makeText(MainActivity.this, "Upload concluído", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void error() {
            Toast.makeText(MainActivity.this, "Erro no upload", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        askForPermission();

        Button btnCamera = findViewById(R.id.btn_camera);

        btnCamera.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntentFullSize();
            }
        });

        Button btnUpload = findViewById(R.id.btn_upload);

        btnUpload.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageBitmap != null) {
                    Toast.makeText(MainActivity.this, "Iniciando upload", Toast.LENGTH_SHORT).show();
                    imageUploader.run(imageBitmap, location);
                }
            }
        });
    }

    private void dispatchTakePictureIntentFullSize() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = null;
        try {
            photoFile = createImageFile();

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider",  photoFile );
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg",storageDir);
        currentPhotoPath = image.getAbsolutePath();return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode,data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK  && photoFile !=null) {
            imageBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            ImageView iv = findViewById(R.id.imageViewHolder);
            iv.setImageBitmap(imageBitmap);
            this.updateLocationText(location);
        }
    }

    private void updateLocationText(Location loc) {
        TextView text = findViewById(R.id.positionTxt);

        if (loc != null) {
            text.setText(String.format("Lat: %.2f / Lng: %.2f", loc.getLatitude(), loc.getLongitude()));
        } else {
            text.setText("");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String
            permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    initLocationTraking();
                } else {
                    Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "No permission for GPS acessing", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void askForPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,  android.Manifest.permission.ACCESS_COARSE_LOCATION) !=  PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            initLocationTraking();
        }
    }
    public void initLocationTraking(){
        try {
            LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    MainActivity.this.location = location;
                }
                public void onStatusChanged(String provider, int status, Bundle extras) { }
                public void onProviderEnabled(String provider) { }
                public void onProviderDisabled(String provider) { }
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 500, locationListener);
        }catch(SecurityException ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}