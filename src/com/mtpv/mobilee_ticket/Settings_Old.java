package com.mtpv.mobilee_ticket;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mtpv.mobilee_ticket_services.DBHelper;
import com.mtpv.mobilee_ticket_services.ServiceHelper;
import com.pinpad.mtpv.GetPinPad;

@SuppressLint({ "SdCardPath", "WorldReadableFiles" })
public class Settings_Old extends Activity implements OnClickListener {
	
	String server = "192.168.11.9";
	int port = 99 ;
	String username = "ftpuser";
	String password = "Dk0r$l1qMp6" ;
	String filename = "Version-1.5.1.apk";
	
	@SuppressWarnings("unused")
	private static final int BUFFER_SIZE = 4096;
	ProgressBar progress;
	Dialog dialog;
	int downloadedSize = 0;
	int totalSize = 0;
	TextView cur_val;
	
	Button btn_ps_name;
	Button btn_pointby_ps_name;
	Button btn_save;
	Button btn_cancel;
	Button btn_back;
	EditText et_exact_location;
	EditText et_analyser;
	EditText et_web_url;

	final int PS_NAME_DIALOG = 0;
	final int PS_CODE_DIALOG = 1;
	final int PROGRESS_DIALOG = 2;

	int selected_ps_name = -1;
	int selected_pointby_psname = -1;
	int ps_code_pos;

	String ps_name_title = "Select PS Name";
	String ps_code_title = "Select PS By Point Name";

	String[][] psname_name_code_arr;// to get names and ps codes ex :
									// psname_name_code_arr[0][0]="uppal"; and
									// psname_name_code_arr[0][1]="2301";
	ArrayList<String> ps_names_arr, ps_codes_fr_names_arr;// to get ps names
															// from
															// psname_name_code_arr

	String[][] pointNameBYpsname_name_code_arr;
	ArrayList<String> pointNameBy_PsName_arr;// point name for second dialog
	ArrayList<String> pointNameBy_PsName_code_arr;// point code for second
													// dialog

	GenerateDrunkDriveCase dashboard;
	DBHelper db;
	Cursor c_psnames, pinpad_cursor, bt_cursor;
	String[] psname_code, psname_name;

	ListView lv_bt_items;
	TextView tv_stateBluetooth;
	Button btn_scan_bluetooth;
	EditText et_bt_address;
	
	TextView appversion , ffd; 
	
	public static ImageView update_apk ;

	BluetoothAdapter bluetoothAdapter;
	private BluetoothAdapter mBluetoothAdapter = null;
	static final UUID MY_UUID = UUID.randomUUID();
	ArrayAdapter<String> btArrayAdapter;
	private static final int REQUEST_ENABLE_BT = 1;
	String address = "";
	
	public static String apkurl ;
	public static String version;
	
	Button btn_pinpadscan_xml ;
	EditText et_pinpad ;
	public static String BLT_Name = "", PINpad_Name = "", PINpad_Adress = "", BTprinter_Name = "", BTprinter_Adress = "", blue_Adress = null, blue_Name =null;
	
	@SuppressWarnings("deprecation")
	@SuppressLint("WorldReadableFiles")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settings);
		
		appversion = (TextView)findViewById(R.id.textView2);
		version  = appversion.getText().toString().trim() + ".apk";
		
		Log.i("APP Version :::", ""+ version);
		
		apkurl = "ftp://192.168.11.9:99/23/TabAPK/"+version ;
		
		LoadUIComponents();

		dashboard = new GenerateDrunkDriveCase();
		db = new DBHelper(getApplicationContext());
		try {
			db.open();

			c_psnames = DBHelper.db.rawQuery("select * from "+ DBHelper.psName_table, null);
			if (c_psnames.getCount() == 0) {
				Log.i("WHEELER DB DETAILS", "0");
			} else {
				psname_code = new String[c_psnames.getCount()];
				psname_name = new String[c_psnames.getCount()];

				int count = 0;
				while (c_psnames.moveToNext()) {
					psname_code[count] = c_psnames.getString(1);
					psname_name[count] = c_psnames.getString(2);
					Log.i("code", "" + psname_code[count]);
					Log.i("name", "" + psname_name[count]);
					count++;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		et_pinpad = (EditText)findViewById(R.id.edt_pinpad_xml);
		try {
			db.open();
				android.database.sqlite.SQLiteDatabase db = openOrCreateDatabase(DBHelper.DATABASE_NAME,MODE_PRIVATE, null);
				String selectQuery = "SELECT  * FROM " + DBHelper.PINPAD_TABLE;
				pinpad_cursor = db.rawQuery(selectQuery, null);
			    
		        if (pinpad_cursor.moveToFirst()) {
		            do {
			            	PINpad_Adress = pinpad_cursor.getString(2);
			            	PINpad_Name = pinpad_cursor.getString(1);
			            	
			            	Log.i("PINpad_Adress :",""+ pinpad_cursor.getString(2));
			            	Log.i("PINpad_Name :",""+ pinpad_cursor.getString(1));
			           	 	
			           	 	et_pinpad.setText(PINpad_Adress);
			           	 
		           	 
	            		} while (pinpad_cursor.moveToNext());
		        		}
						db.close(); 
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(db!=null)
				db.close();
			if(pinpad_cursor!=null)
				pinpad_cursor.close();
		}

		/*---------------------------------------------*/

		dashboard.preferences = getSharedPreferences("preferences",MODE_WORLD_READABLE);
		dashboard.editor = dashboard.preferences.edit();

		/* FOR PS NAMES */
		dashboard.ps_code_set = dashboard.preferences.getInt("psname_code_toSet", selected_ps_name);
		dashboard.ps_name = dashboard.preferences.getString("psname_name","psname");
		Log.i("PS CODE", "" + dashboard.ps_code_set);
		Log.i("PS NAME", "" + dashboard.ps_name);
		/*---------------------------------------------*/

		/* FOR PS POINT NAME */
		dashboard.psnameby_point_code_set = dashboard.preferences.getInt("point_code_toSet", selected_pointby_psname);
		dashboard.point_name = dashboard.preferences.getString("point_name","pointname");

		Log.i("POINT CODE", "" + dashboard.psnameby_point_code_set);
		Log.i("POINT NAME", "" + dashboard.point_name);
		/*---------------------------------------------*/

		/* GETTING BLUETOOTH ADDRESS */
		dashboard.bluetooth_address = dashboard.preferences.getString("btaddress", "bt");
		/*---------------------------------------------*/

		/* GETTING WEB URL */
		Dashboard.modified_url = dashboard.preferences.getString("weburl","myurl");
		if (Dashboard.modified_url.equals("myurl")) {

		} else {
			et_web_url.setText("" + Dashboard.modified_url);
		}

		/* FOR PS NAME START */
		if (dashboard.ps_code_set == -1) {

		} else {
			selected_ps_name = dashboard.ps_code_set;
		}

		if (dashboard.ps_name.equals("psname")) {

		} else {
			btn_ps_name.setText("" + dashboard.ps_name);
			if (isOnline()) {
				selected_pointby_psname = -1;
				btn_pointby_ps_name.setText(""+ getResources().getString(R.string.select_pointbypsname));
				new Async_getPointNameByPsName().execute();
			} else {
				showToast("Please check your network connection!");
			}
		}
		/* FOR PS NAME END */

		/* FOR PS POINT NAME START */
		if (dashboard.psnameby_point_code_set == -1) {

		} else {
			selected_pointby_psname = dashboard.psnameby_point_code_set;
		}

		if (dashboard.point_name.equals("pointname")) {

		} else {
			btn_pointby_ps_name.setText("" + dashboard.point_name);
		}
		/* FOR PS POINT NAME END */

		dashboard.exact_location = dashboard.preferences.getString("exact_location", "location");
		Long anly_id = dashboard.preferences.getLong("analyser_id", 0);

		if (dashboard.exact_location.equals("location")) {

		} else {
			et_exact_location.setText("" + dashboard.exact_location);
		}

		if (anly_id == 0) {

		} else {
			et_analyser.setText("" + anly_id);
		}

		/* GETTING BLUETOOTH ADDRESS */

		if (dashboard.bluetooth_address.equals("bt")) {
			et_bt_address.setText("");
		} else {
			et_bt_address.setText("" + dashboard.bluetooth_address);
		}

		/* BLUETOOTH CONNECTIVITY */
		//if (et_bt_address.getText().toString().trim().equals("")) {
			bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			CheckBlueToothState();
			registerReceiver(ActionFoundReceiver, new IntentFilter(	BluetoothDevice.ACTION_FOUND));
			btArrayAdapter = new ArrayAdapter<String>(Settings_Old.this,android.R.layout.simple_list_item_1);
			lv_bt_items.setAdapter(btArrayAdapter);

			lv_bt_items.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					/** Toast.makeText(getApplicationContext(), ""+listDevicesFound.getCount(), Toast.LENGTH_SHORT).show(); */
					String selection = (String) (lv_bt_items.getItemAtPosition(position));
					Toast.makeText(getApplicationContext(),"BLUETOOTH ADDRESS IS SAVED SUCCESSFULLY",Toast.LENGTH_SHORT).show();
					address = "";
					address = selection.substring(0, 17);
					et_bt_address.setText(address);

					mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
					Alertmessage();
					
				}
			});
	
		//}
		
	}

	private void LoadUIComponents() {
		// TODO Auto-generated method stub
		btn_ps_name = (Button) findViewById(R.id.btnpsname_settings_xml);
		btn_pointby_ps_name = (Button) findViewById(R.id.btnpointby_psname_settings_xml);

		et_exact_location = (EditText) findViewById(R.id.edt_exctlocation_settings_xml);
		et_analyser = (EditText) findViewById(R.id.edt_breatheanalyser_settings_xml);

		lv_bt_items = (ListView) findViewById(R.id.listview_devicesfound);
		btn_scan_bluetooth = (Button) findViewById(R.id.btnscan_settings_xml);
		tv_stateBluetooth = (TextView) findViewById(R.id.tv_bluetoothState);
		et_bt_address = (EditText) findViewById(R.id.edt_bluetoothid_settings_xml);
		et_web_url = (EditText) findViewById(R.id.edt_weburl_settings_xml);

		btn_cancel = (Button) findViewById(R.id.btncancel_settings_xml);
		btn_save = (Button) findViewById(R.id.btnsubmit_settings_xml);
		btn_back = (Button) findViewById(R.id.btnback_settings_xml);
		
		et_pinpad = (EditText)findViewById(R.id.edt_pinpad_xml);
		btn_pinpadscan_xml = (Button)findViewById(R.id.btn_pinpadscan_xml);
		
		update_apk = (ImageView)findViewById(R.id.update_apk);
		
		ffd = (TextView)findViewById(R.id.ffd);

		btn_ps_name.setOnClickListener(this);
		update_apk.setOnClickListener(this);
		btn_pointby_ps_name.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		btn_save.setOnClickListener(this);
		btn_scan_bluetooth.setOnClickListener(this);
		btn_pinpadscan_xml.setOnClickListener(this);
		
		pointNameBy_PsName_code_arr = new ArrayList<String>();// point code
		pointNameBy_PsName_arr = new ArrayList<String>();// point name
	}

	/* BLUETOOTH CONNECTIVITY */
	private void CheckBlueToothState() {
		// TODO Auto-generated method stub
		if (bluetoothAdapter == null) {
			tv_stateBluetooth.setText("Bluetooth NOT support");
		} else {
			if (bluetoothAdapter.isEnabled()) {
				if (bluetoothAdapter.isDiscovering()) {
					tv_stateBluetooth.setText("Bluetooth is currently in device discovery process.");
				} else {
					tv_stateBluetooth.setText("Bluetooth is Enabled.");
					btn_scan_bluetooth.setEnabled(true);
				}
			} else {
				tv_stateBluetooth.setText("Bluetooth is NOT Enabled!");
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}
	}

	/* BLUETOOTH CONNECTIVITY */
	private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				btArrayAdapter.add(device.getAddress() + "\n"+ device.getName());
				btArrayAdapter.notifyDataSetChanged();
				
				BTprinter_Adress = device.getAddress();
				BTprinter_Name = device.getName();
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == REQUEST_ENABLE_BT) {
			CheckBlueToothState();
		}
	}

	public Boolean isOnline() {
		ConnectivityManager conManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nwInfo = conManager.getActiveNetworkInfo();
		return nwInfo != null;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("CommitPrefEdits")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		/* FIRST BUTTON DIALOG */
		case R.id.btnpsname_settings_xml:
			showDialog(PS_NAME_DIALOG);
			break;
		case R.id.btnpointby_psname_settings_xml:

			/* SECOND BUTTON DIALOG */
			if (btn_ps_name.getText().toString().equals(""+ getResources().getString(R.string.select_ps_name))) {
				showToast("Select PS Name");
			} else {
				Log.i("point by ps len b4 dialog call", ""+ pointNameBy_PsName_arr.size());
				showDialog(PS_CODE_DIALOG);
			}

			break;
		case R.id.btncancel_settings_xml:
			dashboard.preferences.edit().clear().commit();
			btn_ps_name.setText(""+ getResources().getString(R.string.select_ps_name));
			btn_pointby_ps_name.setText(""+ getResources().getString(R.string.select_pointbypsname));
			et_exact_location.setText("");
			et_analyser.setText("");
			et_bt_address.setText("");
			et_pinpad.setText("");
			selected_ps_name = -1;
			selected_pointby_psname = -1;

			Log.i("B4 RESET pointNameBy_PsName_arr", ""+ pointNameBy_PsName_arr.size());
			pointNameBy_PsName_code_arr.clear();
			pointNameBy_PsName_arr.clear();
			Log.i("RESET pointNameBy_PsName_arr","" + pointNameBy_PsName_arr.size());

			break;

		case R.id.btnback_settings_xml:
			startActivity(new Intent(this, Dashboard.class));
			break;
		case R.id.btnsubmit_settings_xml:
			if (btn_ps_name	.getText().toString().trim().equals(""+ getResources().getString(R.string.select_ps_name))) {
				showToast("Select PS Name");
			} else if (btn_pointby_ps_name.getText().toString().trim().equals(""+ getResources().getString(R.string.select_pointbypsname))) {
				showToast("Select Point Name");
			} else if ((et_bt_address.getText().toString().trim().equals(""))
					&& (et_bt_address.getText().toString().trim().length() < 10)) {
				//et_bt_address.setError("Check Bluetooth Details Properly");
				et_bt_address.setError(Html.fromHtml("<font color='black'>Check Bluetooth Details Properly</font>"));
			} else if ((et_pinpad.getText().toString().trim().equals(""))
					&& (et_pinpad.getText().toString().trim().length() < 10)) {
				//edt_pinpad_xml.setError("Check PIN pad Bluetooth Details Properly");
				et_pinpad.setError(Html.fromHtml("<font color='black'>Check PIN pad Bluetooth Details Properly</font>"));
			}
			else {
				dashboard.preferences = getSharedPreferences("preferences",	MODE_WORLD_READABLE);
				dashboard.editor = dashboard.preferences.edit();

				/* FOR EXACT LOCATION PREF VALUES */
				if (et_exact_location.getText().toString().trim().equals("")) {
					dashboard.editor.putString("exact_location", "");
				} else {
					dashboard.editor.putString("exact_location", et_exact_location.getText().toString().trim());
				}

				/* ANALYSER ID PREF VALUES */
				if (et_analyser.getText().toString().trim().equals("")) {
					dashboard.editor.putLong("analyser_id", 0);
				} else {
					dashboard.editor.putLong("analyser_id",	Integer.parseInt(et_analyser.getText().toString().trim()));
				}

				dashboard.editor.putString("btaddress", ""+ et_bt_address.getText().toString().trim());

				// dashboard.editor.putString("weburl", ""
				// + et_web_url.getText().toString().trim());
				dashboard.editor.commit();
				
				
				Cursor terminal_cursor = null ;
				String term_id=null , term_bt_name=null , term_bt_addr = null;
				try {
					db.open();
					android.database.sqlite.SQLiteDatabase db = openOrCreateDatabase(DBHelper.DATABASE_NAME,MODE_PRIVATE, null);
					String query2 = "SELECT TERMINAL_ID,BT_NAME,BT_ADDRESS FROM " + DBHelper.TERMINAL_DETAILS_TABLE +" WHERE BT_ADDRESS= '"+PINpad_Adress+"'";
					terminal_cursor = db.rawQuery(query2, null);
					
					Log.i("query2 :::", ""+query2);
				    
			        if (terminal_cursor.moveToFirst()) {
			            do {
			            	 term_id = terminal_cursor.getString(0);
			            	 term_bt_name = terminal_cursor.getString(1);
			            	 term_bt_addr = terminal_cursor.getString(2);
			            	
			            	/*Log.i("term_id :",""+ term_id);
			            	Log.i("term_bt_name :",""+ term_bt_name);*/
			            	Log.i("-------------------------","--------------------");
			            	Log.i("1 :",""+ terminal_cursor.getString(0));
			            	Log.i("2 :",""+ terminal_cursor.getString(1));
			            	Log.i("3 :",""+ terminal_cursor.getString(2));
			            	//Log.i("4 :",""+ terminal_cursor.getString(3));
				           	 	
	            		} while (terminal_cursor.moveToNext());
			            
			            if(term_id!=null && term_bt_name!=null){
			            	Log.i("If Log ","Entered ");
			            	if (term_bt_addr.equals(""+et_pinpad.getText().toString().trim())) {
			            		Log.i("term_bt_addr :::::: ","Entered "+term_bt_addr);
			            		Log.i("Edittext field :::::: ","Entered "+et_pinpad.getText().toString().trim());

			            		Intent dash = new Intent(getApplicationContext(), Dashboard.class);
			            		startActivity(dash);
			            		
			            		finish();
			            		
			            		showToast("Successfully Saved");
								
							}else {
								Log.i("If Log 1 ","Entered ");
								showToast("Invalid PIN pad Address Please Scan Again !!!");
								
							}
			            			
			            }else{
			            	Log.i("If Log 2 ","Entered ");
			            	showToast("Invalid PIN pad Address Please Scan Again !!!");
			            	
			            }
		        	}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}finally{
					if(db!=null)
						db.close();
					if(terminal_cursor!=null)
						terminal_cursor.close();
				}
			//		finish();					
			}

			break;
		case R.id.btnscan_settings_xml:
			ffd.setVisibility(View.VISIBLE);
			final ProgressDialog progressDialog = new ProgressDialog(Settings_Old.this);
	        progressDialog.setMessage("Please wait BlueTooth Scan is in Process!!!");
	        progressDialog.setCancelable(false);
	        progressDialog.show();

	        runOnUiThread(new Runnable() {
                @Override
                public void run() {
                	btArrayAdapter.clear();
 	    			bluetoothAdapter.startDiscovery();
               }
           });
	        
	        new Thread(){
	            @Override
	            public void run() {
	                super.run();
	                try {
	                    Thread.sleep(6000);
	                    if (progressDialog.isShowing())
	                        progressDialog.dismiss();
	                    
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	            }
	        }.start();
			break;
	
		case R.id.btn_pinpadscan_xml :
			
			Intent pin = new Intent(getApplicationContext(), GetPinPad.class);
			startActivity(pin);
			
		case R.id.update_apk :
			//new Async_UpdateApk().execute(); 
			
          	 new Async_UpdateApk().execute();
	      
            
		default:
			break;
		}
	}


	@SuppressWarnings("deprecation")
	private void showProgress(String server) {
		// TODO Auto-generated method stub
		dialog = new Dialog(Settings_Old.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.myprogressdialog);
        dialog.setTitle("Download Progress");
        dialog.setCancelable(false);
        
 
        TextView text = (TextView) dialog.findViewById(R.id.tv1);
        text.setText("Downloading file ... ");
        cur_val = (TextView) dialog.findViewById(R.id.cur_pg_tv);
        cur_val.setText("It may Take Few Minutes.....");
        dialog.show();
         
        progress = (ProgressBar)dialog.findViewById(R.id.progress_bar);
        progress.setProgress(0);//initially progress is 0  
        progress.setMax(100);
        progress.setIndeterminate(true);
        progress.setProgressDrawable(getResources().getDrawable(R.drawable.green_progress)); 
	}


	class Async_UpdateApk extends AsyncTask<Void, Void, String>{
		
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showDialog(PROGRESS_DIALOG);
			
		}

		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
				 
		    FTPClient ftpClient = new FTPClient();

		    try {
		        ftpClient.connect(server, port);
		        ftpClient.login(username, password);
		        ftpClient.enterLocalPassiveMode();
		        ftpClient.setBufferSize(1024*1024);
		        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

		        //ftp://192.168.11.9:99/23/TabAPK/Version-1.5.1.txt
		        File downloadFile1 = new File("/sdcard/Download/E_Ticket_HYD.apk");
		        String remoteFile1 = "/23/TabAPK"+"/"+version;
		        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile1));
		        boolean success = ftpClient.retrieveFile(remoteFile1, outputStream);
		        		        
		        FileOutputStream fileOutput = new FileOutputStream(downloadFile1);
		        InputStream inputStream = ftpClient.retrieveFileStream(remoteFile1);
		        if (inputStream == null || ftpClient.getReplyCode() == 550) {
		        	// it means that file doesn't exist.
		        	fileOutput.close();
		            outputStream.close();
		            
		        	runOnUiThread(new Runnable() {
						
						@SuppressWarnings("deprecation")
						@Override
						public void run() {
							// TODO Auto-generated method stub
							removeDialog(PROGRESS_DIALOG);
				            
							//showToast("your is Upto Date");	
							TextView title = new TextView(Settings_Old.this);
							title.setText("Hyderabad E-Ticket");
							title.setBackgroundColor(Color.RED);
							title.setGravity(Gravity.CENTER);
							title.setTextColor(Color.WHITE);
							title.setTextSize(26);
							title.setTypeface(title.getTypeface(), Typeface.BOLD);
							title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dialog_logo, 0, R.drawable.dialog_logo, 0);
							title.setPadding(20, 0, 20, 0);
							title.setHeight(70);
							
							String otp_message = "\n Your Application is Upto Date \n No Need to Update \n" ;
							
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Settings_Old.this, AlertDialog.THEME_HOLO_LIGHT);
							alertDialogBuilder.setCustomTitle(title);
							alertDialogBuilder.setIcon(R.drawable.dialog_logo);
							alertDialogBuilder.setMessage(otp_message);
							alertDialogBuilder.setCancelable(false);
							alertDialogBuilder.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub
											 
										}
									});

						        AlertDialog alertDialog = alertDialogBuilder.create();
						        alertDialog.show();
						      
						        alertDialog.getWindow().getAttributes();

						        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
						        textView.setTextSize(28);
						        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
						        textView.setGravity(Gravity.CENTER);
						        
						        Button btn1 = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
						        btn1.setTextSize(22);
						        btn1.setTextColor(Color.WHITE);
						        btn1.setTypeface(btn1.getTypeface(), Typeface.BOLD);
						        btn1.setBackgroundColor(Color.RED);  
							
						}
					});
	        	}
		        
		        else{

		    		try {
		            	Log.i("SUCCess LOG 1::::::::", "***********ENTERED*******");
		            	SQLiteDatabase db2 = openOrCreateDatabase(DBHelper.DATABASE_NAME,MODE_PRIVATE, null);
		    			db2.execSQL("DROP TABLE IF EXISTS "+DBHelper.psName_table ); 
		    			db2.execSQL("DROP TABLE IF EXISTS "+DBHelper.wheelercode_table );
		    			db2.execSQL(DBHelper.psNamesCreation);
		    			db2.execSQL(DBHelper.wheelerCodeCreation);
		    			db2.close();
		    		} catch (Exception e) {
		    			// TODO: handle exception
		    			e.printStackTrace();
		    			Log.i("CATCH BLOG 1::::::::", "***********ENTERED*******");
		    			
		    		}
		            totalSize = remoteFile1.length();
		 
		            runOnUiThread(new Runnable() {
		                @SuppressWarnings("deprecation")
						public void run() {
		                	removeDialog(PROGRESS_DIALOG);
		                	showProgress(server);
		                    progress.setMax(totalSize);
		                }               
		            });
		             
		            //create a buffer...
		            byte[] buffer = new byte[1024];
		            int bufferLength = 0;
		 
		            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
		                fileOutput.write(buffer, 0, bufferLength);
		                downloadedSize += bufferLength;
		                
		                // update the progressbar //
		                runOnUiThread(new Runnable() {
		                    public void run() {
		                        progress.setProgress(downloadedSize);
		                        float per = ((float)downloadedSize/totalSize) * 100;
		                       
		                       // cur_val.setText("Downloaded " + downloadedSize/1024*1024 + "MB / " + totalSize + "MB (" + ((int)per /1024*1024)/100 + "%)" );
		                        cur_val.setText((int)per/100000 + "%");
		                    }
		                });
		            }
		          //close the output stream when complete //
		            fileOutput.close();

		            outputStream.close();

			        if (success) {
			        	ftpClient.logout();
			            ftpClient.disconnect();
			            
			            try {
			            	Log.i("SUCCess LOG ::::::::", "***********ENTERED*******");
							db.open();
							db.execSQL("delete from " + DBHelper.psName_table);  
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
							Log.i("CATCH BLOG ::::::::", "***********ENTERED*******");
							db.close();
						}
			        	
			            System.out.println("File #1 has been downloaded successfully.");
			            
			            Intent intent = new Intent(Intent.ACTION_VIEW);
			            intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + "E_Ticket_HYD.apk")), "application/vnd.android.package-archive");
			            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			            startActivity(intent);
			            
			         }
	        	}
	            
		    } catch (SocketException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		    } catch (FileNotFoundException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		    } catch (IOException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		    }
		        return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			//removeDialog(PROGRESS_DIALOG);
		}
		
		 @Override
         protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
         }

	}
	
	
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case PS_NAME_DIALOG:
			TextView title = new TextView(this);
			title.setText("Select PS Name");
			title.setBackgroundColor(Color.parseColor("#007300"));
			title.setGravity(Gravity.CENTER);
			title.setTextColor(Color.WHITE);
			title.setTextSize(26);
			title.setTypeface(title.getTypeface(), Typeface.BOLD);
			title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dialog_logo, 0, R.drawable.dialog_logo, 0);
			title.setPadding(20, 0, 20, 0);
			title.setHeight(70);
			
			AlertDialog.Builder ad_ps_name = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
			ad_ps_name.setCustomTitle(title);
			ad_ps_name.setSingleChoiceItems(psname_name, selected_ps_name,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							selected_ps_name = which;
							btn_ps_name.setText(""
									+ psname_name[which].toString().trim());
							ps_code_pos = which;

							dashboard.preferences = getSharedPreferences(
									"preferences", MODE_WORLD_READABLE);
							dashboard.editor = dashboard.preferences.edit();
							dashboard.editor.putInt("psname_code_toSet", which);
							dashboard.editor.putString("psname_code",
									psname_code[which].toString());
							dashboard.editor.putString("psname_name",
									psname_name[which].toString());

							dashboard.editor.commit();

							removeDialog(PS_NAME_DIALOG);

							// if ((pointNameBy_PsName_code_arr.size() > 0)
							// && (pointNameBy_PsName_arr.size() > 0)) {
							// pointNameBy_PsName_code_arr.clear();
							// pointNameBy_PsName_arr.clear();
							// }

							if (isOnline()) {
								selected_pointby_psname = -1;

								dashboard.preferences = getSharedPreferences(
										"preferences", MODE_WORLD_READABLE);
								dashboard.editor = dashboard.preferences.edit();

								dashboard.editor.putInt("point_code_toSet",
										selected_pointby_psname);
								dashboard.editor.commit();

								btn_pointby_ps_name.setText(""
										+ getResources().getString(
												R.string.select_pointbypsname));
								new Async_getPointNameByPsName().execute();
							} else {
								showToast("Please check your network connection!");
							}

						}
					});
			Dialog dg_ps_name = ad_ps_name.create();
			return dg_ps_name;
		case PS_CODE_DIALOG:
			TextView title2 = new TextView(this);
			title2.setText("Select PS By Point Name");
			title2.setBackgroundColor(Color.parseColor("#007300"));
			title2.setGravity(Gravity.CENTER);
			title2.setTextColor(Color.WHITE);
			title2.setTextSize(26);
			title2.setTypeface(title2.getTypeface(), Typeface.BOLD);
			title2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dialog_logo, 0, R.drawable.dialog_logo, 0);
			title2.setPadding(20, 0, 20, 0);
			title2.setHeight(70);
			
			AlertDialog.Builder ad_ps_code = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
			ad_ps_code.setCustomTitle(title2);
			ad_ps_code.setSingleChoiceItems((pointNameBy_PsName_arr
					.toArray(new String[pointNameBy_PsName_arr.size()])),
					selected_pointby_psname,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							selected_pointby_psname = which;
							btn_pointby_ps_name.setText(""+ pointNameBy_PsName_arr.get(which).toString().trim());

							dashboard.preferences = getSharedPreferences("preferences", MODE_WORLD_READABLE);
							dashboard.editor = dashboard.preferences.edit();

							dashboard.editor.putInt("point_code_toSet", which);
							dashboard.editor.putString("point_code",pointNameBy_PsName_code_arr.get(which).toString().trim());// for sending
							// to service
							dashboard.editor.putString("point_name", pointNameBy_PsName_arr.get(which));

							dashboard.editor.commit();

							removeDialog(PS_CODE_DIALOG);
						}
					});
			Dialog dg_ps_code = ad_ps_code.create();
			return dg_ps_code;
			// for (int i = 0; i < pointNameBy_PsName_arr.size(); i++) {
			// showToast("" + pointNameBy_PsName_arr.get(i));
			// }
			// break;
		case PROGRESS_DIALOG:
			ProgressDialog pd = ProgressDialog.show(this, "", "",	true);
			pd.setContentView(R.layout.custom_progress_dialog);
			pd.setCancelable(false); 
			
			
			return pd;
			
		default:
			break;
		}
		return super.onCreateDialog(id);
	}

	/* TO GET POINT NAME BY PS-NAME */
	public class Async_getPointNameByPsName extends
			AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			ServiceHelper.getPointNameByPsNames(""
					+ psname_code[selected_ps_name].toString().trim());
			return null;
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showDialog(PROGRESS_DIALOG);
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			removeDialog(PROGRESS_DIALOG);

			if (ServiceHelper.PointNamesBypsNames_master.length > 0) {

				Log.i("Settings Async_getPointNameByPsName", ""
						+ ServiceHelper.PointNamesBypsNames_master.length);
				pointNameBYpsname_name_code_arr = new String[0][0];
				pointNameBYpsname_name_code_arr = new String[ServiceHelper.PointNamesBypsNames_master.length][2];

				for (int i = 1; i < ServiceHelper.PointNamesBypsNames_master.length; i++) {
					pointNameBYpsname_name_code_arr[i] = ServiceHelper.PointNamesBypsNames_master[i]
							.toString().trim().split("@");
					Log.i("**POINT DETAILS**", ""
							+ pointNameBYpsname_name_code_arr[i][1].toString()
									.trim());

				}
			}
			/*------------TO CLEAR POINT CODE OF SECOND BUTTON-----------*/
			pointNameBy_PsName_code_arr.clear();
			pointNameBy_PsName_arr.clear();

			for (int j = 1; j < pointNameBYpsname_name_code_arr.length; j++) {
				pointNameBy_PsName_code_arr
						.add(pointNameBYpsname_name_code_arr[j][0]);
				pointNameBy_PsName_arr
						.add(pointNameBYpsname_name_code_arr[j][1]);

			}
			Log.i("**PS NAMES**", "" + pointNameBy_PsName_arr.size());
			btn_pointby_ps_name.setClickable(true);
			// showDialog(PS_NAME_DIALOG);

		}
	}

	public void Alertmessage() {

		if (mBluetoothAdapter == null) {

			showToast("Bluetooth is not available");
			return;
		}

		if (!mBluetoothAdapter.isEnabled()) {
			showToast("Please enable your BT and re-run this program");
			finish();
			return;
		}

	}

	private void showToast(String msg) {
		// TODO Auto-generated method stub
	//	Toast.makeText(getApplicationContext(), "" + msg, Toast.LENGTH_SHORT).show();
		Toast toast = Toast.makeText(getApplicationContext(), "" + msg, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		View toastView = toast.getView();
		
		ViewGroup group = (ViewGroup) toast.getView();
	    TextView messageTextView = (TextView) group.getChildAt(0);
	    messageTextView.setTextSize(24);
		
    	toastView.setBackgroundResource(R.drawable.toast_background);
	    toast.show();
		
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onDestroy() {
		System.runFinalizersOnExit(true);
		// System.exit(0);
		super.onDestroy();
		unregisterReceiver(ActionFoundReceiver);

	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		
		showToast("Please Click on Back Button to go Back");
	}
}