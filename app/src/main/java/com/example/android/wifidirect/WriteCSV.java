package com.example.android.wifidirect;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by yzhan14 on 2/4/2018.
 */

public class WriteCSV extends AsyncTask<Object, Void, Void>{

    private static final String DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";

    private String fileName;
    private TrialData data;
    private Context mContext;
    private FileWriter fileWriter = null;

//    public  WriteCSV(TrialData data, String fileName){
//        this.data = data;
//        this.fileName = fileName;
//    }

    /* Checks if external storage is available for read and write */
    /* from https://developer.android.com/training/data-storage/files.html#java   Accessed 2/4/2018*/
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state));
    }

    private File getSensorDataDir(String fileName) throws IOException {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), fileName);
        if (!file.createNewFile()) {
            Log.e("IO", "File not created");
        }
        return file;
    }

    public WriteCSV(Context aContext) {
        mContext = aContext;
    }

    //delete later for runnable
    //@Override
    public void run() {
        // use this later
        boolean storageState = isExternalStorageWritable();
        //TODO: error handling


        try {
            File f = getSensorDataDir(fileName);
            if (f.exists()) {
                fileWriter = new FileWriter(f, true);
            }else{
                Log.d("IO", "run: file not created");
            }

            //write header
            boolean first = true;
            String[] header = this.data.get_header();
            for (String h: header){
               if (!first){
                   fileWriter.append(DELIMITER);
               }
               fileWriter.append(h);
                first = false;
            }
            fileWriter.append(NEW_LINE_SEPARATOR);

            //write trial data
            ArrayList<double[]> sensor_data = data.get_sensor_data();
            for (double[] row : sensor_data){
                first = true;
                for (double value: row){
                    if (!first){
                        fileWriter.append(DELIMITER);
                    }
                    fileWriter.append(String.valueOf(value));
                    first = false;
                }
                fileWriter.append(NEW_LINE_SEPARATOR);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try{
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.i("INFO","ohoh");
    }


    @Override
    protected Void doInBackground(Object... params) {
        data = (TrialData)params[0];
        fileName = (String)params[1];

        // use this later
        boolean storageState = isExternalStorageWritable();
        //TODO: error handling


        try {
            File f = getSensorDataDir(fileName);
            if (f.exists()) {
                fileWriter = new FileWriter(f, true);
            }else{
                Log.d("IO", "run: file not created");
            }

            //write header
            boolean first = true;
            String[] header = this.data.get_header();
            for (String h: header){
                if (!first){
                    fileWriter.append(DELIMITER);
                }
                fileWriter.append(h);
                first = false;
            }
            fileWriter.append(NEW_LINE_SEPARATOR);

            //write trial data
            ArrayList<double[]> sensor_data = data.get_sensor_data();
            for (double[] row : sensor_data){
                first = true;
                for (double value: row){
                    if (!first){
                        fileWriter.append(DELIMITER);
                    }
                    fileWriter.append(String.valueOf(value));
                    first = false;
                }
                fileWriter.append(NEW_LINE_SEPARATOR);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try{
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return  null;
    }

/*    @Override
    protected Void doInBackground(Object... params) {

        Log.i("INFO","Background");
        return null;
    }*/

    @Override
    protected void onPostExecute(Void aVoid) {
        //TODO
        //super.onPostExecute(aVoid);
        Toast.makeText(mContext,"Data exported to Download folder",Toast.LENGTH_SHORT).show();

    }
}
