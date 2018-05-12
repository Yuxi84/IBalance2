package com.example.android.wifidirect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Cite: https://github.com/deansponholz/Fish_Game/blob/master/app/src/main/java/com/example/deansponholz/fish_game/SensorHandler.java
 */

public class CalibrationFragment extends Fragment {

    private static final int PORT = 8988;

    //display settings
    private WindowManager wm;
    private int width;
    private int height;
    // Assume "big device" with screen size greater than 6 inches
    private int circleRadius = 90;
    private int lineX = 50;
    private int lineY = 30;

    int patient_offsetX = 0, patient_offsetY = 0;
    float patientX, patientY;

    public CalibrationFragment() {
        // Required empty public constructor
    }

    //TODO oncreate start server socket here


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.calibration_fragment, container, false);

        // Get display spec
        wm = (WindowManager) rootView.getContext().getSystemService(Context.WINDOW_SERVICE);
        getDimens();

        Button start_btn = (Button) rootView.findViewById(R.id.btn_start_train);
        final TextView status_bar = (TextView) rootView.findViewById(R.id.canvas_status);

        // add canvas
        //TODO context necessary?
        FrameLayout calibration_frag = (FrameLayout) rootView;
        final drawView dView = new drawView(getActivity());
        calibration_frag.addView(dView);

        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MotionAsyncTask(status_bar, dView, PORT).execute();
            }
        });
        return rootView;
    }

    public void getDimens(){
        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);
        width = size.x;
        height = size.y;


    }

    public class drawView extends View {

        // used for onDraw
        Canvas canvas;
        Paint paint;

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

        public void setPosition(float x, float y){

            //TODO : sign of value here to be adjusted
            patientX = -x*15 + width/2 - 55;
            patientY = y*15 + height/2 -60;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            //TODO: simplified version for now
            canvas.drawCircle(width/2, height/2, circleRadius, paint);

            canvas.drawLine(patientX,patientY,width/2,height/2,paint);
        }
    }
}





