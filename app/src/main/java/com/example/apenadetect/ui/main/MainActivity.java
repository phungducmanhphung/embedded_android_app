package com.example.apenadetect.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.apenadetect.ApenaApplication;
import com.example.apenadetect.R;
import com.example.apenadetect.helper.Helper;
import com.example.apenadetect.service.WSClientService;

public class MainActivity extends AppCompatActivity {

    Button btnConnect;
    EditText edtProccesserIp;
    ClickListener clickListener;
    ApenaApplication apenaApplication;
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

        setControl();
        setEvent();
    }

    private void setControl() {
        btnConnect = findViewById(R.id.btnConnect);
        edtProccesserIp = findViewById(R.id.edtProccesserIp);
    }

    private void setEvent() {
        apenaApplication = new ApenaApplication(this);
        String hostIp = ApenaApplication.WS_HOST;
        edtProccesserIp.setText(hostIp);

        clickListener = new ClickListener();
        btnConnect.setOnClickListener(clickListener);
    }

    public class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(v == MainActivity.this.btnConnect){
                ApenaApplication.WS_HOST = MainActivity.this.edtProccesserIp.getText().toString();
                startService(new Intent(MainActivity.this, WSClientService.class));
            }
        }
    }
}