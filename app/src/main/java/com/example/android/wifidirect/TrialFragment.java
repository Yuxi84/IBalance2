package com.example.android.wifidirect;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by yzhan14 on 11/10/2017.
 */

public class TrialFragment extends Fragment{
    TextView tmpView = null;
    EditText fileNameInput = null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.t_fragment, container,false);
        tmpView = (TextView)rootView.findViewById(R.id.tmp_result);
        fileNameInput = (EditText)rootView.findViewById(R.id.file_name_input);
        Button start_btn = (Button) rootView.findViewById(R.id.t_start_btn);
        Button stop_btn = (Button) rootView.findViewById(R.id.t_stop_btn);
        Button export_btn = (Button) rootView.findViewById(R.id.t_export_btn);

        // default file name
        Calendar myCalendar = Calendar.getInstance();
        String endTime = String.valueOf(myCalendar.get(Calendar.HOUR_OF_DAY))
                +"_"+String.valueOf(myCalendar.get(Calendar.MINUTE));
        fileNameInput.setText(endTime);

        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DeviceActionListener)getActivity()).startTrial();
            }
        });

        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DeviceActionListener)getActivity()).onTrialStopped();
            }
        });

        export_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: check input
                String fileName = String.valueOf(fileNameInput.getText());
                ((DeviceActionListener)getActivity()).startExport(fileName);
            }
        });
        return rootView;
    }

    public void showStats(String result){
        tmpView.setText(result);
    }
}
