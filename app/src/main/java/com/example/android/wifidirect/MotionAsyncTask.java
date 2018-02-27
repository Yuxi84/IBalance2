package com.example.android.wifidirect;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by yzhan14 on 2/26/2018.
 */

public class MotionAsyncTask extends AsyncTask<Void,String,Void> {
    private int port;
    private TextView status_bar;
    private CalibrationFragment.drawView drawView;
    private static final String TAG = "MotionAsyncTask";
    private static final String TERMINATE_SIGNAL = " ";
    public MotionAsyncTask(TextView status_bar, CalibrationFragment.drawView dView, int PORT){
        this.port = PORT;
        this.status_bar = status_bar;
        this.drawView = dView;
    }

    @Override
    protected void onPreExecute() {
        status_bar.setText("Opening server socket");
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Log.d(TAG, "Server: Socket opened");
            Socket client = serverSocket.accept();
            Log.d(TAG, "Server: connection done");
            InputStream inputstream = client.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputstream));
            String str;
            while (!(str = br.readLine()).equals(TERMINATE_SIGNAL)) {
                publishProgress(str);
            }
            br.close();
            client.close();
            serverSocket.close();

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        String[] position_str = values[0].split(" ");
        float x = Float.parseFloat(position_str[0]);
        float y =Float.parseFloat(position_str[1]);
        drawView.setPosition(x,y);
        drawView.invalidate();

    }
}
