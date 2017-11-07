/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.wifidirect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.wifidirect.DeviceListFragment.DeviceActionListener;

/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
public class DeviceDetailFragment extends Fragment
		implements ConnectionInfoListener, SensorEventListener {

	//	todo: whether give a hard-coded initial value

	public static String IP_SERVER = null;
	public static int PORT = 8988;
	private static boolean server_running = false;
	public static final int MIN_GROUPOWNER_INTENT = 0;

	protected static final int CHOOSE_FILE_RESULT_CODE = 20;
	private View mContentView = null;
	private WifiP2pDevice device;
	private WifiP2pInfo info;
	ProgressDialog progressDialog = null;

	//TODO: test for sensor data client
	Socket cSocket = null;
	PrintWriter pw = null;

	private SensorManager sensorManager;
	private Sensor sensor;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mContentView = inflater.inflate(R.layout.device_detail, null);
		mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				WifiP2pConfig config = new WifiP2pConfig();
				config.deviceAddress = device.deviceAddress;
				config.wps.setup = WpsInfo.PBC;
				//TODO:
				config.groupOwnerIntent = MIN_GROUPOWNER_INTENT;
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
						"Connecting to :" + device.deviceAddress, true, true
						//                        new DialogInterface.OnCancelListener() {
						//
						//                            @Override
						//                            public void onCancel(DialogInterface dialog) {
						//                                ((DeviceActionListener) getActivity()).cancelDisconnect();
						//                            }
						//                        }
				);
				((DeviceActionListener) getActivity()).connect(config);

			}
		});

		mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						((DeviceActionListener) getActivity()).disconnect();
					}
				});

//		mContentView.findViewById(R.id.btn_start_client).setOnClickListener(
//				new View.OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						// Allow user to pick an image from Gallery or other
//						// registered apps
//						Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//						intent.setType("image/*");
//						startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);
//					}
//				});

		//sensors
		//TODO getActivity , OLD getContext
		sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

		//TODO
		mContentView.findViewById(R.id.btn_send_data).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								cSocket = new Socket();
								try {
									cSocket.bind(null);
									//TODO: time out
									cSocket.connect(new InetSocketAddress(IP_SERVER, PORT), 500);
									pw = new PrintWriter(cSocket.getOutputStream(), true);

//									TextView statusText = (TextView) mContentView.findViewById(R.id.status_text);
//									statusText.setText(R.string.sending_sensor_data);

									//TODO: close socket and pw later

									// register sensor listener if null
									sensorManager.registerListener(DeviceDetailFragment.this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}).start();


					}
				}
		);

		return mContentView;
	}


	@Override
	public void onPause() {
		super.onPause();
		sensorManager.unregisterListener(this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//TODO: valid?
		super.onActivityResult(requestCode, resultCode, data);

//		String localIP = Utils.getLocalIPAddress();
		// Trick to find the ip in the file /proc/net/arp
		//TODO
//		String client_mac_fixed = new String(device.deviceAddress).replace("99", "19");
//		String clientIP = Utils.getIPFromMac(client_mac_fixed);

		// User has picked an image. Transfer it to group owner i.e peer using
		// FileTransferService.
		Uri uri = data.getData();
		TextView statusText = (TextView) mContentView.findViewById(R.id.status_text);
		statusText.setText("Sending: " + uri);
		Log.d(WiFiDirectActivity.TAG, "Intent----------- " + uri);
		Intent serviceIntent = new Intent(getActivity(), FileTransferService.class);
		serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
		serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());
//		TODO
//		if(localIP.equals(IP_SERVER)){
//			serviceIntent.putExtra(FileTransferService.EXTRAS_ADDRESS, clientIP);
//		}else{
//			serviceIntent.putExtra(FileTransferService.EXTRAS_ADDRESS, IP_SERVER);
//		}

		serviceIntent.putExtra(FileTransferService.EXTRAS_ADDRESS, IP_SERVER);
		serviceIntent.putExtra(FileTransferService.EXTRAS_PORT, PORT);
		getActivity().startService(serviceIntent);
	}

	@Override
	public void onConnectionInfoAvailable(final WifiP2pInfo info) {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		this.info = info;
		this.getView().setVisibility(View.VISIBLE);

		// The owner IP is now known.
		IP_SERVER = info.groupOwnerAddress.getHostAddress();
		TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
		view.setText(getResources().getString(R.string.group_owner_text)
				+ ((info.isGroupOwner) ? getResources().getString(R.string.yes)
				: getResources().getString(R.string.no)));

		// InetAddress from WifiP2pInfo struct.
		view = (TextView) mContentView.findViewById(R.id.device_info);
		view.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());

//		mContentView.findViewById(R.id.btn_start_client).setVisibility(View.VISIBLE);
		mContentView.findViewById(R.id.btn_send_data).setVisibility(View.VISIBLE);

		if (!server_running) {
			new ServerAsyncTask(getActivity(), mContentView.findViewById(R.id.status_text), PORT).execute();
			server_running = true;
		}

		// hide the connect button
		mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
	}

	/**
	 * Updates the UI with device data
	 *
	 * @param device the device to be displayed
	 */
	public void showDetails(WifiP2pDevice device) {
		this.device = device;
		this.getView().setVisibility(View.VISIBLE);
		TextView view = (TextView) mContentView.findViewById(R.id.device_address);
		view.setText(device.deviceAddress);
		view = (TextView) mContentView.findViewById(R.id.device_info);
		view.setText(device.toString());

	}

	/**
	 * Clears the UI fields after a disconnect or direct mode disable operation.
	 */
	public void resetViews() {
		mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
		TextView view = (TextView) mContentView.findViewById(R.id.device_address);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.device_info);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.group_owner);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.status_text);
		view.setText(R.string.empty);
//		mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
		mContentView.findViewById(R.id.btn_send_data).setVisibility(View.GONE);
		this.getView().setVisibility(View.GONE);
		server_running = false;
	}

	@Override
	public void onSensorChanged(final SensorEvent event) {
		if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				pw.println(event.values[0] + " " + event.values[2]); // do not need y value
			}
		}).start();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int i) {
		//TODO:
	}

	/**
	 * A simple server socket that accepts connection and writes some data on
	 * the stream.
	 */
//	public static class ServerAsyncTask extends AsyncTask<Void, String, Void> {
//
//		private final Context context;
//		private final TextView statusText;
//
//		/**
//		 * @param context
//		 * @param statusText
//		 */
//		public ServerAsyncTask(Context context, View statusText) {
//			this.context = context;
//			this.statusText = (TextView) statusText;
//		}
//
//		@Override
//		protected Void doInBackground(Void... params) {
//			//TODO: modify temporarily to test pw
//			try {
//				ServerSocket serverSocket = new ServerSocket(PORT);
//				Log.d(WiFiDirectActivity.TAG, "Server: Socket opened");
//				Socket client = serverSocket.accept();
//				Log.d(WiFiDirectActivity.TAG, "Server: connection done");
////				final File f = new File(Environment.getExternalStorageDirectory() + "/"
////						+ context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
////						+ ".jpg");
////
////				File dirs = new File(f.getParent());
////				if (!dirs.exists())
////					dirs.mkdirs();
////				f.createNewFile();
////
////				Log.d(WiFiDirectActivity.TAG, "server: copying files " + f.toString());
//				InputStream inputstream = client.getInputStream();
////				copyFile(inputstream, new FileOutputStream(f));
////				serverSocket.close();
////				server_running = false;
////				return f.getAbsolutePath();
//				BufferedReader br = new BufferedReader(new InputStreamReader(inputstream));
//				String str;
//				while ((str = br.readLine()) != null) {
//					publishProgress(str);
//				}
//				br.close();
//				client.close();
//
//			} catch (IOException e) {
//				Log.e(WiFiDirectActivity.TAG, e.getMessage());
//				//return null;
//			}
//			return null;
//		}
//
//		@Override
//		protected void onProgressUpdate(String... values) {
//			statusText.setText(values[0]);
//		}
//
//		/*
//                         * (non-Javadoc)
//                         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
//                         */
////		@Override
////		protected void onPostExecute(Void result) {
////			if (result != null) {
////				statusText.setText("File copied - " + result);
////				Intent intent = new Intent();
////				intent.setAction(android.content.Intent.ACTION_VIEW);
////				intent.setDataAndType(Uri.parse("file://" + result), "image/*");
////				context.startActivity(intent);
////			}
////
////		}
//
//		/*
//		 * (non-Javadoc)
//		 * @see android.os.AsyncTask#onPreExecute()
//		 */
//		@Override
//		protected void onPreExecute() {
//			statusText.setText("Opening a server socket");
//		}
//
//	}
}
//	public static boolean copyFile(InputStream inputStream, OutputStream out) {
//		byte buf[] = new byte[1024];
//		int len;
//		try {
//			while ((len = inputStream.read(buf)) != -1) {
//				out.write(buf, 0, len);
//
//			}
//			out.close();
//			inputStream.close();
//		} catch (IOException e) {
//			Log.d(WiFiDirectActivity.TAG, e.toString());
//			return false;
//		}
//		return true;
//	}
//
//}
