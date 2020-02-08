package ucla.erlab.brainresearch;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class PulseOxConnActivity extends AppCompatActivity {
    private final int BUF_SIZE = 32;

    private final byte STX = 0x02;
    private final byte ETX = 0x03;

    private final int ONYX_INIT_RES_SIZE = 1;
    private final int ONYX_GET_TIME_RES_SIZE = 10;

    private ProgressDialog mProgress;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mSocket;
    private BluetoothDevice mDevice;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private Thread mWorkerThread;
    private byte[] mReadBuffer;
    private int mReadBufferPosition;
    private int mCounter;
    private volatile boolean mStopWorker;
    private CommType mCommType = CommType.InitReq;

    private enum CommType {
        InitReq,
        InitRes,
        TimeSetReq,
        TimeGetReq,
        TimeGetRes,
        DataIn
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pulse_ox_conn);

        mProgress = new ProgressDialog(this, R.style.NewDialog);
        new BlueToothTask().execute();

        /*
        mProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //goToBloodPressure(null);
            }
        });
        */
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void goToBloodPressure(View view) {
        Intent intent = new Intent(PulseOxConnActivity.this, BloodPressureActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(Config.BP_TYPE, Config.BPType.Setup);
        startActivity(intent);
    }

    private class BlueToothTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            boolean isConnected = false;
            while (!isConnected) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetooth, 0);
                }

                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {
                        if (device.getName().contains("Nonin_Medical_Inc")) {
                            mDevice = device;
                            break;
                        }
                    }
                }

                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
                try {
                    mSocket = mDevice.createRfcommSocketToServiceRecord(uuid);
                    mSocket.connect();
                    mOutputStream = mSocket.getOutputStream();
                    mInputStream = mSocket.getInputStream();
                    isConnected = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        private void runBlueTooth() {
            mStopWorker = false;
            mReadBufferPosition = 0;
            mReadBuffer = new byte[1024];
            mWorkerThread = new Thread(new BlueToothRunnable());
            mWorkerThread.start();
        }

        @Override
        protected void onPreExecute() {
            mProgress.setMessage("Bluetooth connecting...");
            mProgress.show();
        }
        @Override
        protected void onPostExecute(Void result) {
            runBlueTooth();
            mProgress.cancel();
        }
        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private class BlueToothRunnable implements Runnable {
        @Override
        public void run() {
            byte[] packetBytes = new byte[BUF_SIZE];
            int buffOffset = 0;

            while(!Thread.currentThread().isInterrupted() && !mStopWorker) {
                try {
                    int bytesAvailable = mInputStream.available();
                    if (bytesAvailable > 0) {
                        int readSize = mInputStream.read(packetBytes, buffOffset, bytesAvailable);
                        buffOffset += readSize;
                    }

                    switch (mCommType) {
                        case InitReq: {
                            byte[] initialCommand = {STX, 0x70, 0x02, 0x02, 0x0D, ETX};
                            mOutputStream.write(initialCommand);
                            mOutputStream.flush();
                            mCommType = CommType.InitRes;
                            break;
                        }
                        case InitRes: {
                            if (bytesAvailable > 0) {
                                int res = packetBytes[0];
                                packetBytes = Utils.copyByteArr(packetBytes, 1, buffOffset, 0);
                                buffOffset -= ONYX_INIT_RES_SIZE;
                                if (res == 0x06) {
                                    Log.d("BR", "Bluetooth init success");
                                    mCommType = CommType.TimeSetReq;
                                } else {
                                    mCommType = CommType.InitReq;
                                }
                            }
                            break;
                        }
                        case TimeSetReq: {
                            Date time = Calendar.getInstance().getTime();
                            byte[] setTimeComm = {STX, 0x72, 0x06,
                                    (byte) (time.getYear() % 100), (byte) (time.getMonth() + 1), (byte) time.getDate(),
                                    (byte) time.getHours(), (byte) time.getMinutes(), (byte) time.getSeconds(),
                                    ETX};
                            Log.d("BR", "Bluetooth time set request " + Utils.bytesToHex(setTimeComm));
                            mOutputStream.write(setTimeComm);
                            mOutputStream.flush();
                            mCommType = CommType.TimeGetReq;
                            break;
                        }
                        case TimeGetReq: {
                            byte[] getTimeComm = {STX, 0x72, 0x00, ETX};
                            mOutputStream.write(getTimeComm);
                            mOutputStream.flush();
                            Log.d("BR", "Bluetooth time get request " + Utils.bytesToHex(getTimeComm));
                            Arrays.fill(packetBytes, (byte)0);
                            mCommType = CommType.TimeGetRes;
                            break;
                        }
                        case TimeGetRes: {
                            if (bytesAvailable > 0) {
                                Utils.ByteReadResult res = Utils.extractResponse(packetBytes, buffOffset, ONYX_GET_TIME_RES_SIZE);
                                if (res.success) {
                                    byte[] timeGetResult = res.result;
                                    packetBytes = Utils.copyByteArr(
                                            packetBytes, res.byteRead, buffOffset, 0);
                                    buffOffset -= res.byteRead;
                                    Log.i("BR", "Bluetooth time get response : " + Utils.bytesToHex(timeGetResult));
                                    mCommType = CommType.DataIn;
                                }
                            }
                            break;
                        }
                        case DataIn: {
                            if (bytesAvailable > 0) {
                                //Log.e("33523", "" + bytesAvailable);
                            }
                        }
                        break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + mCommType);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    mStopWorker = true;
                }
            }
        }
    }
}
