package com.example.android.wifidirect;

import android.nfc.NfcEvent;

import java.util.ArrayList;

/**
 * Created by yzhan14 on 11/8/2017.
 */

public class TrialData {
    private ArrayList<double[]> sensor_event_data;
    private static final String[] HEADER = {"time", "ML(X-axis)", "AP(Z-axis)"};
    private static final int TIME_INDEX = 0;
    private static final int ML_INDEX = 1;
    private static final int AP_INDEX = 2;

    public TrialData(){
        sensor_event_data = new ArrayList<>();
    }

    public void addData(double t, double x, double z){
        double[] row = {t,x,z};
        sensor_event_data.add(row);
    }

    public ArrayList<double[]> get_sensor_data(){
        return sensor_event_data;
    }

    public String[] get_header(){
        return HEADER;
    }

    /**
     * sway jerkiness
     * @return time derivative of acceleration
     */
    public double getJERK(){
        // use Riemann sum to estimate since our data is discrete
        //TODO: possible go wrong? but even if size is 0 for loop still works fine
        double sum = 0;
        double[] curr = null;
        double t1 = 0;
        double ap1 = 0;
        double ml1 = 0;
        for (int i=0;i<sensor_event_data.size()-1;i++){

            if (curr == null) {
                curr = sensor_event_data.get(i);
                t1 = curr[TIME_INDEX];
                ap1 = curr[AP_INDEX];
                ml1 = curr[ML_INDEX];

            }
            double[] next = sensor_event_data.get(i+1);

            // derivatives
            double t2 = next[TIME_INDEX];
            double ap2 = next[AP_INDEX];
            double ml2 = next[ML_INDEX];

            double time_diff = t2 - t1;
            sum += Math.pow((ap2-ap1)/time_diff,2)+Math.pow((ml2-ml1)/time_diff, 2);

            // preserve values of next to curr variable
            t1 = t2;
            ap1 = ap2;
            ml1 = ml2;
        }
        return sum;
    }
}
