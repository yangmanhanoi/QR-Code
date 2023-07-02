package com.dtn.qrcodescanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView cd_generate, cd_scan, cd_send, cd_download;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    public void init()
    {
        cd_generate = findViewById(R.id.cd_genarate);
        cd_scan = findViewById(R.id.cd_scan);
        cd_send = findViewById(R.id.cd_send);
        cd_download = findViewById(R.id.cd_download);

        cd_generate.setOnClickListener(this);
        cd_scan.setOnClickListener(this);
        cd_send.setOnClickListener(this);
        cd_download.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent intent;
        switch (id)
        {
            case R.id.cd_genarate:
                intent = new Intent(MainActivity.this, GenerateActivity.class);
                startActivity(intent);
                break;
            case R.id.cd_scan:
                intent = new Intent(MainActivity.this, ScanActivity.class);
                startActivity(intent);
                break;
            case R.id.cd_send:
                intent = new Intent(MainActivity.this, SendActivity.class);
                startActivity(intent);
                break;
            case R.id.cd_download:
                intent = new Intent(MainActivity.this, DownloadActicity.class);
                startActivity(intent);
                break;
        }

    }
}