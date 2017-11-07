package com.example.android.wifidirect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by yzhan14 on 11/5/2017.
 */

public class RoleActivity extends Activity{
    public static final String EXTRA_MODE = "MODE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roles);

        final Intent intent = new Intent(this, WiFiDirectActivity.class);

        Button btn_therapist = (Button) findViewById(R.id.therapist_btn);
        Button btn_patient = (Button) findViewById(R.id.patient_btn);
        btn_patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra(EXTRA_MODE, R.string.patient);
                startActivity(intent);
            }
        });
        btn_therapist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra(EXTRA_MODE, R.string.therapist);
                startActivity(intent);
            }
        });
    }
}
