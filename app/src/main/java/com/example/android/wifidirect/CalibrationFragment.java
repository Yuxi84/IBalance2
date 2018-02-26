package com.example.android.wifidirect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CalibrationFragment extends Fragment {


    public CalibrationFragment() {
        // Required empty public constructor
    }

    //TODO oncreate start server socket here

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.calibration_fragment, container, false);
    }

    public class drawView extends View {

        // used for onDraw
        Canvas canvas;
        Paint paint;

        int patient_offsetX = 0, patient_offsetY = 0;

        public drawView(Context context){
            super(context);
            initView();
        }

        public void initView(){
            // Drawing tools
            canvas = new Canvas();
            paint = new Paint();
            paint.setColor(getResources().getColor(R.color.deep_orange));
            paint.setStrokeWidth(2);
            paint.setStyle(Paint.Style.STROKE);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            //canvas.drawCircle();
        }
    }
}





