package com.symbol.scanapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.symbol.scanning.BarcodeManager;
import com.symbol.scanning.ScanDataCollection;
import com.symbol.scanning.Scanner;
import com.symbol.scanning.ScannerException;
import com.symbol.scanning.StatusData;


/**
 * Created by EricH on 8/6/2016.
 */
public class ScanAct extends Activity implements View.OnClickListener, Scanner.StatusListener, Scanner.DataListener {


    private Button button;
    private TextView result;

    //internal use
    private Scanner mScanner;
    private BarcodeManager mBarcodeManager = new BarcodeManager();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main2);
        button = (Button) findViewById(R.id.button);
        result = (TextView) findViewById(R.id.result);
        button.setOnClickListener(this);
        mScanner = new Scanner();

    }

    @Override
    public void onClick(View v) {
        startScanning();
    }

    private void startScanning() {
        try {
            if (!mScanner.isEnable())
                mScanner.enable();
            mScanner.read();
        } catch (ScannerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatus(StatusData statusData) {

    }

    @Override
    public void onData(ScanDataCollection scanDataCollection) {
        ScanDataCollection.ScanData scanData = scanDataCollection.getScanData().get(scanDataCollection.getScanData().size()-1);
        result.setText(scanData.getData());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScanner.removeDataListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScanner.addDataListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("ScanAct",String.valueOf(keyCode));

        // 102,103,104 the desire key.
        if(keyCode == KeyEvent.KEYCODE_BUTTON_L1 || keyCode == KeyEvent.KEYCODE_BUTTON_R1 || keyCode == KeyEvent.KEYCODE_BUTTON_L2){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
