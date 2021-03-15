package kr.co.ainus.petife2.view.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import kr.co.ainus.petife2.R;
import kr.co.ainus.petife2.databinding.ActivityFeedHistoryBinding;
import kr.co.ainus.petife2.util.GsonHelper;
import kr.co.ainus.petife2.view.etc.ValueFormatter;
import kr.co.ainus.petica_api.model.domain.FeedHistory;
import kr.co.ainus.petica_api.model.domain.Petica;

public class FeedHistoryActivity extends _BaseNavigationActivity implements OnChartValueSelectedListener {


    private ActivityFeedHistoryBinding dataBinding;
    private Petica petica;

    private ArrayList<BarEntry> feederValue = new ArrayList<>();
    private ArrayList<BarEntry> waterValue = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDataBinding();
        setViewModel();

        String peticaJson = getIntent().getStringExtra("peticaJson");
        petica = GsonHelper.getGson().fromJson(peticaJson, Petica.class);

        setBarChart(dataBinding.chart1);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (petica != null) {
            feedHistoryViewModel.loadFeedHistoryList(uuid, petica.getDeviceId(), Calendar.getInstance().get(Calendar.YEAR));
        }
    }

    @Override
    public void setDataBinding() {
        super.setDataBinding();

        baseNavigationBinding.tvTitle.setText(getString(R.string.statistics));
        baseNavigationBinding.btnRight.setVisibility(View.GONE);

        dataBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_feed_history, baseNavigationBinding.baseNavContainer, true);
        dataBinding.setLifecycleOwner(this);
    }

    @Override
    public void setViewModel() {
        super.setViewModel();

        feedHistoryViewModel.getFeederHistoryLiveData().observe(this, feedHistoryList -> {
            setFeedHiistory(dataBinding.chart1, feedHistoryList, 0);
        });

        feedHistoryViewModel.getWaterHistoryLiveData().observe(this, feedHistoryList -> {
            setFeedHiistory(dataBinding.chart1, feedHistoryList, 1);
        });
    }

    private void setBarChart(BarChart chart) {
        chart.setOnChartValueSelectedListener(this);
        chart.getDescription().setEnabled(false);

        chart.setDrawBorders(true);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(true);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);

        BarDataSet set1 = new BarDataSet(feederValue, getString(R.string.feed));
        set1.setColor(Color.rgb(255, 102, 0));

        BarDataSet set2 = new BarDataSet(waterValue, getString(R.string.water));
        set2.setColor(Color.rgb(164, 228, 251));

        BarData data = new BarData(set1, set2);
        data.setValueFormatter(new LargeValueFormatter());

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(true);

        l.setYOffset(0f);
        l.setXOffset(10f);
        l.setYEntrySpace(0f);
        l.setTextSize(16f);

        XAxis xAxis = chart.getXAxis();

        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setAxisMinimum(0f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(32f);
        leftAxis.setDrawZeroLine(true);
        leftAxis.setAxisMinimum(1f); // this replaces setStartAtZero(true)

        chart.getAxisRight().setEnabled(false);

        chart.setData(data);
    }

    private void setFeedHiistory(BarChart chart, List<FeedHistory> feederHistoryList, int index) {
        List<BarEntry> barEntryList = new ArrayList<>();

        for (int i = 0; i < feederHistoryList.size(); i++) {
            int amount = feederHistoryList.get(i).getAmount();
//            int amount = (int) (Math.random() * 15) + 5;
            BarEntry barEntry = new BarEntry(i, amount);
            barEntryList.add(barEntry);
        }

        BarDataSet data = ((BarDataSet) chart.getData().getDataSetByIndex(index));
        data.setValueTextSize(12f);
        data.setValues(barEntryList);

        // (0.2 + 0.03) * 4 + 0.08 = 1.00 -> interval per "group"
        float groupSpace = 0.08f;
        float barSpace = 0.06f;
        float barWidth = 0.4f;

        int groupCount = feederHistoryList.size();
        int startYear = 1;

        chart.getData().notifyDataChanged();
        chart.notifyDataSetChanged();

        // specify the width each bar should have
        chart.getBarData().setBarWidth(barWidth);

        // restrict the x-axis range
        chart.getXAxis().setAxisMinimum(startYear);

        // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
        chart.getXAxis().setAxisMaximum(startYear + chart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        chart.groupBars(startYear, groupSpace, barSpace);
        chart.invalidate();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
