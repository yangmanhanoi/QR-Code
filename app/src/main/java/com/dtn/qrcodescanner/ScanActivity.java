package com.dtn.qrcodescanner;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.io.IOException;
import java.util.List;

public class ScanActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_camera, btn_gallery;
    private Button btn_scan;
    private ImageView img_qrcode;
    private TextView tv_result;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int GALLERY_REQUEST_CODE = 101;

    private String[] cameraPermission;
    private String[] galleryPermission;
    // Uri of the image code that we take from camera/gallery
    private Uri codeImageUri = null;
    private static final String TAG = "Main_TAG";
    // Scanner
    private BarcodeScannerOptions barcodeScannerOptions;
    private BarcodeScanner barcodeScanner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        initView();
    }

    private void initView() {
        btn_camera = findViewById(R.id.btn_camera);
        btn_gallery = findViewById(R.id.btn_gallery);
        btn_scan = findViewById(R.id.btn_scan);
        img_qrcode = findViewById(R.id.img_qrcode);
        tv_result = findViewById(R.id.tv_infor);

        btn_scan.setOnClickListener(this);
        btn_gallery.setOnClickListener(this);
        btn_camera.setOnClickListener(this);
        // String of permission
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        galleryPermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        // Set up for Scanner

//        /init/setup BarcodeScannerOptions, put comma separated types in setBorcodeFormats (Borcode.FORMAT ALL FORMATS) or add Barcode. FORMAT ALL FOR
//• if you want to scan all formats
//        The following formats are supported:
//        Code 128 (FORMAT_CODE_128), Code 39 (FORMAT CODE 39), Code 93 (FORMAT_CODE_93),
//                Codobor (FORMAT COOABAR), EAN-13 (FORMAT EAN-13),
//        EAN-8 (FORMAT EAN 8),
//        ITF (FORMAT ITF),
//                UPC-A (FORMAT UPC A),
//                UPC-E (FORMAT UPCE),
//                QR Code (FORMAT QR CODE),
//                PDF417 (FORMAT PDF417),
//                Aztec (FORMAT AZTEC)
//                , Data Matrix (FORMAT_DATA_MATRIX)+/
        barcodeScannerOptions = new BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build();

        barcodeScanner = BarcodeScanning.getClient(barcodeScannerOptions);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        // Handle camera click, check Camera's permission (i.e WRITE STORAGE & CAMERA) and take image from Camera
        if (id == R.id.btn_camera) {
            btn_camera.setSelected(true);
            btn_gallery.setSelected(false);
            if (checkCameraPermission()) {
                getImageCamera();
            } else {
                requestCameraPermission();
            }
        }
        // Handle gallery click, check Permission,
        else if (id == R.id.btn_gallery) {
            btn_gallery.setSelected(true);
            btn_camera.setSelected(false);
            if (checkGalleryPermission()) {
                getGalleryImage();
            } else {
                requestGalleryPermission();
            }
        }
        // Handle scan click, scan the QR/Barcode from image got from Camera/Gallery
        else if (id == R.id.btn_scan) {
            if (codeImageUri == null) {
                Toast.makeText(this, "Pick Image first", Toast.LENGTH_LONG).show();
            } else {
                showResult();
            }
        }
    }

    private void showResult() {
        try {
            // Prepare img for scanning
            InputImage inputImage = InputImage.fromFilePath(this, codeImageUri);
            // Scanning
            Task<List<Barcode>> barcodeResult = barcodeScanner.process(inputImage).addOnSuccessListener(
                    new OnSuccessListener<List<Barcode>>() {
                        @Override
                        public void onSuccess(List<Barcode> barcodes) {
                            // Task completed successfully
                            extractBarcodeQRCodeInfor(barcodes);
                        }
                    }
            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ScanActivity.this, "Failed to scan due to " + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(ScanActivity.this, "Failed to scan due to " + e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void extractBarcodeQRCodeInfor(List<Barcode> barcodes) {
        // Get infor from barcode
        for (Barcode item : barcodes) {
            /*The following types are supported:
            Barcode. TYPE UNKNOWN, Barcode. TYPE CONTACT INFO, Barcode. TYPE_EMAIL,
            Barcode. TYPE ISBN, Barcode. TYPE PHONE,
            Barcode. TYPE PRODUCT, Barcode, TYPE SHS,
            Barcode.TYPE_TEXT,
            Barcode.TYPE_URL, Barcode. TYPE WIFI,
            Barcode.TYPE_GEO, Barcode. TYPE CALENDAR EVENT, Barcode. TYPE DRIVER LICENSE */

            int valueType = item.getValueType();
            if(valueType == Barcode.TYPE_EMAIL)
            {
                Barcode.Email email = item.getEmail();

                String address = email.getAddress();
                String body = email.getBody();

                tv_result.setText(" TYPE: TYPE_EMAIL \n ADDRESS: " + address + "\n BODY: " +body);
            }
            else if(valueType == Barcode.TYPE_URL)
            {
                Barcode.UrlBookmark typeUrl = item.getUrl();

                String title = typeUrl.getTitle();
                String url = typeUrl.getUrl();

                tv_result.setText(" TYPE: TYPE_URL \n TITLE: " + title + "\n URL: " +url);
            }
            else if(valueType == Barcode.TYPE_CONTACT_INFO)
            {
                Barcode.ContactInfo contactInfo = item.getContactInfo();

                String title = contactInfo.getTitle();
                String name = contactInfo.getName().getFirst() + " " + contactInfo.getName().getLast();
                String email = contactInfo.getEmails().get(0).getAddress();
                String phone = contactInfo.getPhones().get(0).getNumber();

                tv_result.setText(" TYPE: TYPE_CONTACT_INFO \n TITLE: " + title + "\n NAME: " +name +
                        "\n EMAIL: " + email + "\n PHONE: " + phone);
            }
            Rect bound = item.getBoundingBox();
            Point[] corners = item.getCornerPoints();

            String rawValue = item.getRawValue();

        }
    }

    private void getGalleryImage() {
        // Intent to pick image from gallery, will show all resources from where we can pick image
        Intent intent = new Intent(Intent.ACTION_PICK);
        // Set type we want to pick: image
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // check if we have received image from gallery
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Get the uri of the image
                        Intent data = result.getData();
                        codeImageUri = data.getData();
                        // Set to imageview
                        img_qrcode.setImageURI(codeImageUri);
                        Log.e(TAG, "onActivityResult: imageUri: " + codeImageUri.toString());
                    } else {
                        Toast.makeText(ScanActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
                    }
                }
            }
    );

    private void getImageCamera() {
        // get ready the image data to store in MediaStore
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Sample Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Sample Image Description");
        // ImageUri
        codeImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        // Intent to launch camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraResultLauncher.launch(intent);

    }

    private final ActivityResultLauncher<Intent> cameraResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent imageData = result.getData();
                        codeImageUri = imageData.getData();
                        img_qrcode.setImageURI(codeImageUri);
                        Log.e(TAG, "onActivityResult: imageUri: " + codeImageUri.toString());

                    } else {
                        Toast.makeText(ScanActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
                    }
                }
            }
    );

    // check if storage permission is granted or not
    private boolean checkGalleryPermission() {
        Boolean check = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return check;
    }

    // request storage permission
    private void requestGalleryPermission() {
        ActivityCompat.requestPermissions(this, galleryPermission, GALLERY_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        Boolean checkCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        Boolean checkStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        return checkCamera && checkStorage;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    // Handle runtime permission results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    Boolean cameraCheck = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    Boolean storageCheck = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraCheck && storageCheck) {
                        getImageCamera();
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case GALLERY_REQUEST_CODE:
                if (grantResults.length > 0) {
                    Boolean storageCheck = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageCheck) {
                        getGalleryImage();
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }

                break;
        }
    }
}
