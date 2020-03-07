package ucla.erlab.brainresearch;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class PulseOxConnActivity extends AppCompatActivity {
    private final int BUF_SIZE = 64;

    private final byte STX = 0x02;
    private final byte ETX = 0x03;

    private final int ONYX_INIT_RES_SIZE = 1;

    private ProgressDialog mProgress;
    private BlueToothTask mBTTask;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mSocket;
    private BluetoothDevice mDevice;
    private OutputStream mOutputStream;
    private InputStream mInputStream;

    private boolean mBTFetchSuccess;

    private CommType mCommType = CommType.DataIn;

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
        mBTFetchSuccess = false;
        mProgress = new ProgressDialog(this, R.style.NewDialog);

        mProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.d("BR", "ProgressBar cancel");
                if (!mBTFetchSuccess) {
                    if (mBTTask != null) {
                        mBTTask.cancel(true);
                    }
                    //finish();
                    return;
                }
            }
        });
        mProgress.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.d("BR", "ProgressBar dismiss");
            }
        });
    }

    public void closeBlueToothSocket() {
        if (mSocket != null && mSocket.isConnected()) {
            Log.d("BR", "Trying to close BlueToothSocket");
            try {
                mSocket.close();
                mSocket = null;
            } catch (IOException e) {
                Log.w("BR", "BlueToothSocket close error");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("BR", "PulseOx Activity destroy");
        if (mBTTask != null) {
            mBTTask.cancel(true);
        }
        closeBlueToothSocket();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!mBTFetchSuccess) {
            TextView tv = (TextView) findViewById(R.id.tv_pulse_ox);
            tv.setText("");

            mProgress.setMessage("Bluetooth connecting...");
            mProgress.show();
            mBTTask = new BlueToothTask();
            mBTTask.execute();
        }
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

    private class BlueToothTask extends AsyncTask<Void, String, Void> {
        Utils.Format13Data mResultData;

        @Override
        protected Void doInBackground(Void... params) {
            connectBlueTooth();
            publishProgress("Data collecting...");
            runBlueTooth();
            return null;
        }

        private void connectBlueTooth() {
            boolean isConnected = false;
            while (!Thread.currentThread().isInterrupted() && !isCancelled() && !isConnected) {
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
                    Log.w("BR", "1st trial failed for bluetooth connection");
                    try {
                        Thread.sleep(300);
                        Class<?> clazz = mSocket.getRemoteDevice().getClass();
                        Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};

                        Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                        Object[] jrParams = new Object[]{Integer.valueOf(1)};

                        mSocket = (BluetoothSocket) m.invoke(mSocket.getRemoteDevice(), jrParams);
                        mSocket.connect();
                        mOutputStream = mSocket.getOutputStream();
                        mInputStream = mSocket.getInputStream();
                        isConnected = true;
                    } catch (Exception ex) {
                        Log.w("BR", "2nd trial failed for bluetooth connection");
                    }
                }
            }
        }

        private void runBlueTooth() {
            boolean isStopWorker = false;
            byte[] packetBytes = new byte[BUF_SIZE];
            int buffOffset = 0;
            while(!Thread.currentThread().isInterrupted()&& !isCancelled() && !isStopWorker) {
                if (!mSocket.isConnected()) {
                    Log.e("BR", "Bluetooth already disconnected for background task");
                    break;
                }

                try {
                    int bytesAvailable = mInputStream.available();
                    if (bytesAvailable > 0) {
                        int readSize = mInputStream.read(packetBytes, buffOffset, bytesAvailable);
                        Log.d("BR", "Bluetooth input stream read : " + readSize + " " + mCommType.toString());
                        buffOffset += readSize;
                    }

                    switch (mCommType) {
                        case InitReq: {
                            byte[] initialCommand = {STX, 0x70, 0x02, 0x02, 0x0D, ETX};
                            Log.d("BR", "Bluetooth init request");
                            mOutputStream.write(initialCommand);
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
                                    mCommType = CommType.DataIn;
                                } else if (res == 0x15) {
                                    Log.d("BR", "Bluetooth init fails");
                                    isStopWorker = true;
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
                            mCommType = CommType.TimeGetReq;
                            break;
                        }
                        case TimeGetReq: {
                            byte[] getTimeComm = {STX, 0x72, 0x00, ETX};
                            mOutputStream.write(getTimeComm);
                            Log.d("BR", "Bluetooth time get request " + Utils.bytesToHex(getTimeComm));
                            Arrays.fill(packetBytes, (byte)0);
                            mCommType = CommType.TimeGetRes;
                            break;
                        }
                        case TimeGetRes: {
                            if (bytesAvailable > 0) {
                                Utils.ByteReadResult res = Utils.extractResponse(packetBytes, buffOffset);
                                if (res.success) {
                                    byte[] timeGetResult = res.result;
                                    packetBytes = Utils.copyByteArr(
                                            packetBytes, res.byteRead, buffOffset, 0);
                                    buffOffset -= res.byteRead;

                                    Log.i("BR", "Bluetooth time get response (" + res.byteRead + " bytes) : "
                                            + Utils.bytesToHex(timeGetResult));
                                    mCommType = CommType.DataIn;
                                }
                            }
                            break;
                        }
                        case DataIn: {
                            if (bytesAvailable > 0) {
                                Utils.ByteReadResult res = Utils.extractResponse(packetBytes, buffOffset);
                                if (res.success && res.result.length > 20) {
                                    byte[] dataInResult = res.result;
                                    packetBytes = Utils.copyByteArr(
                                            packetBytes, res.byteRead, buffOffset, 0);
                                    Log.i("BR", "Bluetooth data in (" + res.byteRead + " bytes) : "
                                            + Utils.bytesToHex(dataInResult));
                                    buffOffset -= res.byteRead;
                                    mResultData = Utils.parseData13(res.result);

                                    isStopWorker = true;
                                    mBTFetchSuccess = true;
                                }
                            }
                        }
                        break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + mCommType);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    isStopWorker = true;
                }
            }
        }

        @Override
        protected void onPreExecute() {
        }
        @Override
        protected void onPostExecute(Void result) {
            mProgress.dismiss();
            if (mBTFetchSuccess) {
                TextView tv = (TextView) findViewById(R.id.tv_pulse_ox);
                String text = String.format("Pulse - %d\nSpO2 - %d", mResultData.pulse, mResultData.spo2);
                tv.setText(text);
            } else {
                finish();
            }
        }
        @Override
        protected void onProgressUpdate(String... values) {
            mProgress.setMessage(values[0]);
        }
    }
}
