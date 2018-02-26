package com.example.android.wifidirect;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by yzhan14 on 11/5/2017.
 */

public class ServerAsyncTask extends AsyncTask<Void, String, Void> {

    private Context context;
    private TextView statusText;
    private int PORT;
    private static final String TERMINATE_SIGNAL = " ";

    /**
     * @param context
     * @param statusText
     */
    public ServerAsyncTask(Context context, View statusText, int PORT) {
        this.context = context;
        this.statusText = (TextView) statusText;
        this.PORT = PORT;
    }

    @Override
    protected Void doInBackground(Void... params) {
        //TODO: modify temporarily to test pw
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            Log.d(WiFiDirectActivity.TAG, "Server: Socket opened");
            Socket client = serverSocket.accept();
            Log.d(WiFiDirectActivity.TAG, "Server: connection done");
//				final File f = new File(Environment.getExternalStorageDirectory() + "/"
//						+ context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
//						+ ".jpg");
//
//				File dirs = new File(f.getParent());
//				if (!dirs.exists())
//					dirs.mkdirs();
//				f.createNewFile();
//
//				Log.d(WiFiDirectActivity.TAG, "server: copying files " + f.toString());
            InputStream inputstream = client.getInputStream();
//				copyFile(inputstream, new FileOutputStream(f));
//				serverSocket.close();
//				server_running = false;
//				return f.getAbsolutePath();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputstream));
            String str;
            while (!(str = br.readLine()).equals(TERMINATE_SIGNAL)) {
                publishProgress(str);
            }
            br.close();
            client.close();
            serverSocket.close();

        } catch (IOException e) {
            Log.e(WiFiDirectActivity.TAG, e.getMessage());
            //return null;
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        statusText.setText(values[0]);
        // notify the data to parent acitivity which can then tell graph view
        ((DeviceActionListener)context).onNewSensorData(values[0]);
    }

		/*
                         * (non-Javadoc)
                         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
                         */
//		@Override
//		protected void onPostExecute(Void result) {
//			if (result != null) {
//				statusText.setText("File copied - " + result);
//				Intent intent = new Intent();
//				intent.setAction(android.content.Intent.ACTION_VIEW);
//				intent.setDataAndType(Uri.parse("file://" + result), "image/*");
//				context.startActivity(intent);
//			}
//
//		}

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#onPreExecute()
     */
    @Override
    protected void onPreExecute() {
        statusText.setText("Opening a server socket");
    }

}
