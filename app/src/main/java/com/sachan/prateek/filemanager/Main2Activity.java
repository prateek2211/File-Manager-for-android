package com.sachan.prateek.filemanager;

import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.view.View;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import static com.sachan.prateek.filemanager.MainActivity.audioSize;
import static com.sachan.prateek.filemanager.MainActivity.docsSize;
import static com.sachan.prateek.filemanager.MainActivity.imagesSize;

public class Main2Activity extends AppCompatActivity {
    PieChart pieChart;
    PieChart sdPieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pieChart = findViewById(R.id.pie_chart);
        sdPieChart = findViewById(R.id.sd_pie_chart);
        if (!MainActivity.isExternalSD_available)
            sdPieChart.setVisibility(View.GONE);
        setPieData();
        if (MainActivity.isExternalSD_available)
            setSdPieData();
    }

    private void setSdPieData() {
        List<PieEntry> pieEntryList = new ArrayList<>();
        StatFs stat = new StatFs(MainActivity.externalSD_root.getPath());
        pieEntryList.add(new PieEntry(MainActivity.sdaudioSize, ""));
        pieEntryList.add(new PieEntry(stat.getFreeBytes(), "Free"));
        pieEntryList.add(new PieEntry(imagesSize, "Images"));
        pieEntryList.add(new PieEntry(audioSize, "Audio"));
        pieEntryList.add(new PieEntry(docsSize, "Documents"));
        pieEntryList.add(new PieEntry(stat.getTotalBytes() - audioSize - imagesSize - docsSize - stat.getFreeBytes(), "Other"));
        PieDataSet pieDataSet = new PieDataSet(pieEntryList, "File Type");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        sdPieChart.setData(new PieData(pieDataSet));
        sdPieChart.animateY(1000);
        sdPieChart.setUsePercentValues(true);
        sdPieChart.invalidate();
        sdPieChart.setCenterText(Formatter.formatFileSize(Main2Activity.this, stat.getFreeBytes()) + " Free");
        sdPieChart.setDrawEntryLabels(false);
        Description description = new Description();
        description.setText("External Storage");
        description.setTextSize(16);
        sdPieChart.setDescription(description);
        Legend legend = sdPieChart.getLegend();
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
    }

    void setPieData() {
        List<PieEntry> pieEntries = new ArrayList<>();
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
//        String s1 = Formatter.formatFileSize(this, stat.getAvailableBytes());
//        String s2 = Formatter.formatFileSize(this, stat.getTotalBytes());
        pieEntries.add(new PieEntry(stat.getFreeBytes(), "Free"));
        pieEntries.add(new PieEntry(imagesSize, "Images"));
        pieEntries.add(new PieEntry(audioSize, "Audio"));
        pieEntries.add(new PieEntry(docsSize, "Documents"));
        pieEntries.add(new PieEntry(stat.getTotalBytes() - audioSize - imagesSize - docsSize - stat.getFreeBytes(), "Other"));
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "File Types");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.setData(new PieData(pieDataSet));
        pieChart.animateY(1000);
        pieChart.setUsePercentValues(true);
        pieChart.invalidate();
        pieChart.setCenterText(Formatter.formatFileSize(Main2Activity.this, stat.getFreeBytes()) + " Free");
        pieChart.setDrawEntryLabels(false);
        Description description = new Description();
        description.setText("Internal Storage");
        description.setTextSize(16);
        pieChart.setDescription(description);
        Legend legend = pieChart.getLegend();
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
    }
}
