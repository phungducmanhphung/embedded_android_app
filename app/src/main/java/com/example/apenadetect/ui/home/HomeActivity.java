package com.example.apenadetect.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.apenadetect.ApenaApplication;
import com.example.apenadetect.R;

public class HomeActivity extends AppCompatActivity {
    TextView tvNhipTho;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("SEND_NHIP_THO")) {
                Double data = intent.getDoubleExtra("nhipTho", -1d); // Lấy dữ liệu từ intent
                tvNhipTho.setText(data.toString());
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setControl();
        setEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("SEND_NHIP_THO");
        registerReceiver(mReceiver, filter);
    }

    private void setControl() {
        tvNhipTho = findViewById(R.id.tvNhipTho);
    }

    private void setEvent() {
    }
}