package com.symbol.scanapp;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import java.util.ArrayList;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.ScrollView;
import android.widget.Button;
import com.symbol.scanning.Scanner;
import com.symbol.scanning.Scanner.DataListener;
import com.symbol.scanning.ScannerException;
import com.symbol.scanning.ScanDataCollection;
import com.symbol.scanning.ScanDataCollection.ScanData;
//test for BarcodeManager
import com.symbol.scanning.BarcodeManager;
import java.util.List;
import com.symbol.scanning.ScannerInfo;

public class ScanApp extends Activity {
	private String TAG = "ScanApp";
	//test BarcodeManager.getDevice
	private BarcodeManager mBarcodeManager = new BarcodeManager();
	/*private Scanner mScanner =
		mBarcodeManager.getDevice(BarcodeManager.DeviceIdentifier.INTERNAL_CAMERA1);*/
	private ScannerInfo mInfo =
		new ScannerInfo("se4710_cam_builtin", "DECODER_2D");
	private Scanner mScanner = mBarcodeManager.getDevice(mInfo);
	private List<ScannerInfo> scanInfoList = mBarcodeManager.getSupportedDevicesInfo();

	//private Scanner mScanner = new Scanner();
	private DataListener mDataListener;
	private TextView mScanResult = null;
	private ScrollView mScrollView = null;
	private boolean canDecode = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.main);

		Button mbutton = (Button) findViewById(R.id.button);
		mScanResult = (TextView) findViewById(R.id.result);
		mScanResult.setMovementMethod(ScrollingMovementMethod.getInstance());
		mScrollView = (ScrollView) findViewById(R.id.scroll);

		mbutton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				if(canDecode)
				{
					canDecode = false;
					try
					{
						mScanner.read();
						//test ScannerConfig.isParamSupported(String)
						boolean upcaEnabled = mScanner.getConfig().isParamSupported(
							"mScannerConfig.decoderParams.upca.enabled");
						Log.d(TAG, "upcaEnabled isParamSupported - "+upcaEnabled);
						Log.d(TAG, "upcaEnabled = "+
							mScanner.getConfig().decoderParams.upca.enabled);
						//test BarcodeManager.getSupportedDevicesInfo()
						if(!scanInfoList.isEmpty())
						{
							Log.d(TAG, "scanInfoList is not empty");
							for(ScannerInfo info : scanInfoList)
							{
								Log.d(TAG, "scanning supprot "+info.getDeviceType());
							}
						}
					}
					catch (ScannerException se)
					{
						se.printStackTrace();
					}
				}
				else
				{
					canDecode = true;

					try
					{
						mScanner.cancelRead();
					}
					catch (ScannerException se)
					{
						se.printStackTrace();
					}
				}
			}
		});
	}

	public void setDecodeListener()
	{
		mDataListener =  new DataListener()
		{
			public void onData(ScanDataCollection scanDataCollection)
			{
				String data = "";
				ArrayList<ScanData> scanDataList = scanDataCollection.getScanData();

				for(ScanData scanData :scanDataList)
				{
					data = scanData.getData();
				}

				mScanResult.append(data + "\n");
				mScrollView.fullScroll(View.FOCUS_DOWN);
				mScanResult.setSelected(true);

				canDecode = true;
			}
		};

		mScanner.addDataListener(mDataListener);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if((keyCode == KeyEvent.KEYCODE_BUTTON_L1)
			|| (keyCode == KeyEvent.KEYCODE_BUTTON_R1)
			|| (keyCode == KeyEvent.KEYCODE_BUTTON_L2))
		{
			Log.i("ScanApp", "onKeyDown");
			if(canDecode && (event.getRepeatCount() == 0))
			{
				canDecode = false;
				try
				{
					mScanner.read();
				}
				catch (ScannerException se)
				{
					se.printStackTrace();
				}
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		if((keyCode == KeyEvent.KEYCODE_BUTTON_L1)
			|| (keyCode == KeyEvent.KEYCODE_BUTTON_R1)
			|| (keyCode == KeyEvent.KEYCODE_BUTTON_L2))
		{
			Log.i("ScanApp", "onKeyUp");
			if(!canDecode)
			{
				try
				{
					mScanner.cancelRead();
				}
				catch (ScannerException se)
				{
					se.printStackTrace();
				}
				canDecode = true;
				return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}
 
	protected void onResume()
	{
		try
		{
			mScanner.enable();
			setDecodeListener();
		}
		catch(ScannerException se)
		{
			se.printStackTrace();
		}

		super.onResume();
	}

	protected void onPause()
	{
		try
		{
			if(!canDecode)
				mScanner.cancelRead();
			mScanner.disable();
		}
		catch(ScannerException se)
		{
			se.printStackTrace();
		}
		finally
		{
			mScanner.removeDataListener(mDataListener);
			canDecode = true;
		}
		super.onPause();
	}
}
