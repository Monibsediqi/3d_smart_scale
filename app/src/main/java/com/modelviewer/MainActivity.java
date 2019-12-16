package com.modelviewer;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

import c0m.WildCom.Project.R;
import smartscaledatabase.PigAdapter;
import smartscaledatabase.PigMath;
import smartscaledatabase.PigTable;
import smartscaledatabase.PigViewModel;

import static java.lang.StrictMath.abs;

public class MainActivity extends AppCompatActivity implements PigAdapter.OnItemListener {


    public static final String TAG = "Main Activity";
    private PigViewModel pigViewModel;
    private GraphView graphView;
    private PigTable pigTable = new PigTable("", 0, 0, 0);
    private double SHIPMENT_WEIGHT = 120;
    private List<PigTable> pigTables = new ArrayList<>();
    PigTable currentPig;
    PigMath pigMath = new PigMath();

    TextView weightShortFallTextView, shipmentWeightTextView,pigDetailsTextView, scannedDateTextView2;
    TextView readyForSaleView, foodRequiredView;


    Intent intent = getIntent();
//    Bundle extras = intent.getExtras();
//    String pigName = extras.getString(AddDataActivity.EXTRA_PIG_NAME);
//    String pigWeight = extras.getString(AddDataActivity.EXTRA_WEIGHT);
//    int priority = extras.getInt(AddDataActivity.EXTRA_PRIORITY, 1);

    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
            new DataPoint(17,0),
            new DataPoint(45,20),
            new DataPoint(73,40),
            new DataPoint(101,60),
            new DataPoint(129,80),
            new DataPoint(157,100),
            new DataPoint(172,120),
            new DataPoint(199,140),
            new DataPoint(217,160),
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        graphView = findViewById(R.id.graph);
        weightShortFallTextView = findViewById(R.id.text_view_weight_shortfall);
        shipmentWeightTextView = findViewById(R.id.text_view_shipment_weight);
        pigDetailsTextView = findViewById(R.id.text_view_details);
        scannedDateTextView2 = findViewById(R.id.text_view_date_details);
        readyForSaleView = findViewById(R.id.text_view_ready_for_sale);
        foodRequiredView = findViewById(R.id.text_view_required_food);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        runGraphView();


        final PigAdapter adapter = new PigAdapter(this);
        recyclerView.setAdapter(adapter);
        pigViewModel = ViewModelProviders.of(this).get(PigViewModel.class);
        pigViewModel.getAllPigs().observe(this, new Observer<List<PigTable>>() {
            @Override
            public void onChanged(@Nullable List<PigTable> pigTables) {
                adapter.setPigTables(pigTables);
            }
        });
    }


    private void runGraphView(){
        graphView.setTitle("Pig Growth");
        graphView.setTitleTextSize(34);
        graphView.getGridLabelRenderer().setVerticalAxisTitle("Weight(Kg)");
        graphView.getGridLabelRenderer().setHorizontalAxisTitle("Days");
        graphView.setPadding(0,0,8,0);
        graphView.getViewport().setMinX(17);
        graphView.getViewport().setMaxX(217);
        graphView.getViewport().setMinY(0);
        graphView.getViewport().setMaxY(160);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getGridLabelRenderer().setNumVerticalLabels(9);
        graphView.getGridLabelRenderer().setNumHorizontalLabels(9);
        graphView.getViewport().setScalable(true);
        graphView.getViewport().setScalableY(true);
        graphView.setVisibility(View.VISIBLE);
        series.setAnimated(true);
        series.setColor(Color.rgb(2,108,69));
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);
        series.setThickness(10);
        graphView.addSeries(series);
    }


    @Override
    public void onItemClick(int position) {
        int pos = position;
        Log.d(TAG, "onItemClick: Clicked." + pos);

        pigTable = pigViewModel.getPig(pos);
        String pigName = pigTable.getPigName();
        double pigWeight = pigTable.getWeight();
        int dateAndTime = pigTable.getDateAndTime();

        /*
        TODO:
        1- Handle exception if the weight exceeds the shipment weight
        2- handle exception if the days left for sale exceeds the limit
        3- On the first run of the app display and editText view so that user enter shipment weight for their pigs
         */
        double weightShortFallValue = SHIPMENT_WEIGHT - pigWeight;
        double ready_for_sale = abs(pigMath.getAgeInDays(pigWeight) - 161);

        int ready_for_s = (int) ready_for_sale;
        Log.d(TAG, "Date and Time " + dateAndTime);
        weightShortFallTextView.setText((weightShortFallValue + "Kg"));
        readyForSaleView.setText((ready_for_s + " Days Left"));
        pigDetailsTextView.setText(("Details of " + pigName ));
        scannedDateTextView2.setText(String.valueOf(dateAndTime));
        int requiredFood = (int) (pigMath.estimateReqFood());
        foodRequiredView.setText(String.valueOf(requiredFood));

    }
}
