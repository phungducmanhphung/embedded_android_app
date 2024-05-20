package com.example.apenadetect.ui.statistic;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.apenadetect.R;
import com.example.apenadetect.data.BreathDatabase;
import com.example.apenadetect.data.Breathing;
import com.example.apenadetect.helper.DateUtils;
import com.example.apenadetect.ui.component.PickDate;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

public class StatisticActivity extends AppCompatActivity {
    boolean isTest = false;
    Date startTime, endTime;
    LineChart lineChart;
    Button btnThongKe, btnChartPre, btnChartNext;
    PickDate pickDateStartTime, pickDateEndTime;
    BreathDatabase breathDatabase;
    TextView tvSoLanNgungTho, tvNhipThoTrungBinh, tvSoLanDo, tvPage;
    LinearLayout statistic, soLanNgungThoTime;
    int pageNumber = 1, pageSize = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_statistic);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        breathDatabase = new BreathDatabase(this);
        
        setControl();
        setEvent();
    }
    private void setControl() {
        lineChart = findViewById(R.id.lineChart);
        btnThongKe = findViewById(R.id.btnThongKe);
        pickDateStartTime = findViewById(R.id.pickDateStartTime);
        pickDateEndTime = findViewById(R.id.pickDateEndTime);
        tvSoLanNgungTho = findViewById(R.id.tvSoLanNgungTho);
        tvNhipThoTrungBinh = findViewById(R.id.tvNhipThoTrungBinh);
        tvSoLanDo = findViewById(R.id.tvSoLanDo);
        statistic = findViewById(R.id.statistic);
        btnChartPre = findViewById(R.id.btnChartPre);
        btnChartNext = findViewById(R.id.btnChartNext);
        soLanNgungThoTime = findViewById(R.id.soLanNgungThoTime);
        tvPage = findViewById(R.id.tvPage);
    }
    private void setEvent() {

        btnThongKe.setOnClickListener(v -> {

            pageNumber = 1;

            if(isTest){
                getAllDataFromDB();
                return;
            }

            startTime = pickDateStartTime.getDate();
            endTime = pickDateEndTime.getDate();

            if(startTime == null){
                pickDateStartTime.showPickDate();
                return;
            }
            if(endTime == null){
                pickDateEndTime.showPickDate();
                return;
            }

            long startTimeStamp = startTime.getTime() / 1000;
            long endTimeStamp = endTime.getTime() / 1000;

            getDataFromDB(startTimeStamp, endTimeStamp);

            statistic.setVisibility(View.VISIBLE);
        });

        pickDateStartTime.setCallBack(object -> {
            Date newStartTime = (Date) object[0];
            Date oldEndTime = pickDateEndTime.getDate();
            if(oldEndTime == null){
                return;
            }
            if(newStartTime.after(oldEndTime)){
                pickDateStartTime.setDate(oldEndTime);
            }
        });

        pickDateEndTime.setCallBack(object -> {
            Date newEndTime = (Date) object[0];
            Date oldStartTime = pickDateStartTime.getDate();
            if(oldStartTime == null){
                return;
            }
            if(newEndTime.before(oldStartTime)){
                pickDateStartTime.setDate(oldStartTime);
            }
        });

        btnChartPre.setOnClickListener(v -> {
            startTime = pickDateStartTime.getDate();
            endTime = pickDateEndTime.getDate();

            if(startTime == null){
                return;
            }
            if(endTime == null){
                return;
            }

            long startTimeStamp = startTime.getTime() / 1000;
            long endTimeStamp = endTime.getTime() / 1000;

            if(pageNumber > 1)
                pageNumber -=1;
            else{
                Toast.makeText(this, "is first page", Toast.LENGTH_SHORT).show();
                return;
            }

            getDataFromDB(startTimeStamp, endTimeStamp);
        });
        btnChartNext.setOnClickListener(v -> {
            startTime = pickDateStartTime.getDate();
            endTime = pickDateEndTime.getDate();

            if(startTime == null){
                return;
            }
            if(endTime == null){
                return;
            }

            long startTimeStamp = startTime.getTime() / 1000;
            long endTimeStamp = endTime.getTime() / 1000;

            pageNumber +=1;

            try {
                getDataFromDB(startTimeStamp, endTimeStamp);
            }
            catch (Exception ex){
                pageNumber -=1;
                Toast.makeText(this, "Is last page", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void getDataFromDB(long startTime, long endTime){
        try {
            List<Breathing> breathings = breathDatabase.getBreathDataBetween(startTime, endTime, pageNumber, pageSize);
            // tính toán và cập nhật ui
            showStatisticData(breathings);
            showChartData(breathings);

            tvPage.setText(Integer.toString(pageNumber));
        }
        catch (Exception ex){
            Log.d("HBNJNKKL", ex.getMessage());
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void getAllDataFromDB(){
        List<Breathing> breathings = breathDatabase.getAllData();
        // tính toán và cập nhật ui
        showStatisticData(breathings);
        showChartData(breathings);
    }
    public void showStatisticData(List<Breathing> breathings){
        /*
        * caculate số lần ngưng thở và nhịp thở trung bình
        * */
        int apenaNum = 0;
        double totalBreathing = 0d;
        double averageBreathing = 0d;
        List<Long> apenaTimestamps = new ArrayList<>();

        Double beforeBreathReate = null;
        for (Breathing breathing:breathings) {
            // bắt đầu xảy ra ngưng thở khi ngủ
            if(beforeBreathReate != null && beforeBreathReate != 0 && breathing.getBreathRate() == 0){
                apenaNum += 1;
                apenaTimestamps.add(breathing.getTimestamp());
            }
            totalBreathing += breathing.getBreathRate();
            beforeBreathReate = breathing.getBreathRate();
        }
        averageBreathing = totalBreathing / breathings.size();
        /*
        * cập nhật UI
        * */
        tvSoLanNgungTho.setText(Integer.toString(apenaNum));
        tvNhipThoTrungBinh.setText(Double.toString(averageBreathing));
        tvSoLanDo.setText(Double.toString(breathings.size()));

        soLanNgungThoTime.removeAllViews();

        for (long apenaTimestamp: apenaTimestamps) {
                TextView tv = new TextView(this);
                Date date = new Date(apenaTimestamp * 1000);
                tv.setText(date.toString());
                soLanNgungThoTime.addView(tv);
        }
    }
    public void showChartData(List<Breathing> breathings){
        // calculate breath reate
        List<Float> breathRates = new ArrayList<>();
        for (Breathing breathing: breathings) {
            float breathRate = (float) breathing.getBreathRate();
            breathRates.add(breathRate);
        }
        /*
        * show breath in chart UI
        * */
        reRenderLineChart(breathRates);
    }
    public void reRenderLineChart(List<Float> breathRates){

        // entries
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < breathRates.size(); i++) {
            entries.add(new Entry(i, breathRates.get(i)));
        }

        // line dataset
        LineDataSet lineDataSet = new LineDataSet(entries, "breathing");
        lineDataSet.setCircleColors(ColorTemplate.MATERIAL_COLORS);
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setValueTextSize(12f);

        // line data
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        // axis x
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        // axis y
        YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.setGranularity(1f);
        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setEnabled(false);

        // Cho phép cuộn theo chiều ngang
        // Hiển thị tối đa 10 điểm tại một thời điểm
        lineChart.setVisibleXRangeMaximum(10);
        lineChart.moveViewToX(lineData.getEntryCount());
        // Cập nhật biểu đồ
        lineChart.invalidate();
    }
}