package com.example.apenadetect.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.apenadetect.ApenaApplication;
import com.example.apenadetect.R;
import com.example.apenadetect.data.Breathing;
import com.example.apenadetect.service.SoundService;
import com.example.apenadetect.ui.statistic.StatisticActivity;

public class HomeActivity extends AppCompatActivity {
    TextView tvNhipTho;
    Button btnTatCanhBao, btnThongKe;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("SEND_NHIP_THO")) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    // get breathing rate from boardcast
                    Breathing breathing = intent.getSerializableExtra("nhipTho", Breathing.class);
                    // show breath rate
                    tvNhipTho.setText(Double.toString(breathing.getBreathRate()));
                    // check and notify when breath reate equal zero
                    if(breathing.getBreathRate() == 0d && HomeActivity.this.btnTatCanhBao.getVisibility() == View.GONE){
                        startService(new Intent(HomeActivity.this, SoundService.class));
                        HomeActivity.this.btnTatCanhBao.setVisibility(View.VISIBLE);
                    }
                }
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
        btnTatCanhBao = findViewById(R.id.btnTatCanhBao);
        btnThongKe = findViewById(R.id.btnThongKe);
    }

    private void setEvent() {
        btnTatCanhBao.setOnClickListener(v -> {
            stopService(new Intent(this, SoundService.class));
            btnTatCanhBao.setVisibility(View.GONE);
        });

        btnThongKe.setOnClickListener(v -> {
            Intent staticticsIntent = new Intent(this, StatisticActivity.class);
            startActivity(staticticsIntent);
        });
    }
}