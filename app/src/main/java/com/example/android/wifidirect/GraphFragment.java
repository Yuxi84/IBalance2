package com.example.android.wifidirect;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by yzhan14 on 11/5/2017.
 */

public class GraphFragment extends Fragment {

    private GraphView graphView;

    private LineGraphSeries<DataPoint> series1;
    private LineGraphSeries<DataPoint> series2;

    private double acclX = 0;
    private double acclZ = 0;
    //TODO: linked this to time, since display time at which accl data changed
    private int data_count = 1;

    private SensorManager sensorManager;
    private Sensor sensor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.graph, container, false);
        graphView = (GraphView) rootView.findViewById(R.id.live);

        series1 = new LineGraphSeries<>(new DataPoint[]{});
        graphView.addSeries(series1);
        series1.setTitle(getString(R.string.ml_x_axis_string));
        series1.setColor(Color.RED);

        series2 = new LineGraphSeries<>(new DataPoint[]{});
        graphView.addSeries(series2);
        series2.setTitle(getString(R.string.ap_z_axis_string));
        series2.setColor(Color.GREEN);

        // set viewport, namely window for displaying data
        //TODO: customize
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMaxX(100);
        graphView.getViewport().setScalable(true);
        //graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScalableY(true);
        //graphView.getViewport().setScrollableY(true);

        // set legend
        graphView.getLegendRenderer().setVisible(true);
        graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        return rootView;

    }


    public void addData(double acclX, double acclZ) {

        series1.appendData(new DataPoint(data_count, acclX),true, Integer.MAX_VALUE);
        series2.appendData(new DataPoint(data_count, acclZ), true, Integer.MAX_VALUE);

        data_count++;
    }
}
