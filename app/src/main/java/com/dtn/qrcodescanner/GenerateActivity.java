package com.dtn.qrcodescanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class GenerateActivity extends AppCompatActivity {

    private ImageView img_qrcode;
    private Button btn_generate;
    private EditText ed_name, ed_gmail, ed_code;
    private Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate);

        init();

    }
    public void init()
    {
        img_qrcode = findViewById(R.id.img_qrcode);
        btn_generate = findViewById(R.id.btn_generate);
        ed_name = findViewById(R.id.ed_name);
        ed_gmail = findViewById(R.id.ed_gmail);
        ed_code = findViewById(R.id.ed_stcode);

        btn_generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ed_name.length() == 0 || ed_gmail.getText().toString().equals("") || ed_code.getText().toString().equals(""))
                {
                    noti();
                }
                else
                {
                    generateQR();
                }

            }
        });
    }
    public void generateQR()
    {
        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append("Student's name: " + ed_name.getText() + "\n");
        stringBuilder.append("Student's gmail: " + ed_gmail.getText() + "\n");
        stringBuilder.append("Student's code: " + ed_code.getText() + "\n");
        String infor = stringBuilder.toString();
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(infor, BarcodeFormat.QR_CODE, 250, 250);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(bitMatrix);

            img_qrcode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
    public void noti()
    {
        Toast.makeText(this, "Please enter your information!!", Toast.LENGTH_LONG).show();
    }
}
