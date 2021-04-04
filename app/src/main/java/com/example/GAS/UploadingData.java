

package com.example.GAS;

        import android.os.Bundle;
        import android.app.Activity;
        import android.app.PendingIntent;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.hardware.usb.UsbDevice;
        import android.hardware.usb.UsbDeviceConnection;
        import android.hardware.usb.UsbManager;
        import android.os.Bundle;
        import android.preference.PreferenceManager;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;

        import com.felhr.usbserial.UsbSerialDevice;
        import com.felhr.usbserial.UsbSerialInterface;

        import java.io.UnsupportedEncodingException;
        import java.util.HashMap;
        import java.util.Map;
        import android.content.SharedPreferences;
public class UploadingData extends Activity {
    public final String ACTION_USB_PERMISSION = "com.hariharan.arduinousb.USB_PERMISSION";
    Button startButton, sendButton, clearButton, stopButton, back;
    TextView textView;
    EditText editText;
    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;
    String data = null;
    char data2 = 'u';

    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String stepCount = "stepCount" ;
    public static final String stepWidth = "stepWidth" ;
    public static final String sessionTime = "sessionTime" ;
    public static final String rightArmCount = "rightArmCount" ;
    public static final String leftArmCount = "leftArmCount" ;
    public static volatile byte[] arg1 = null;
    public static volatile String prevData = "wow";
    String sc, sw, st, ra, la;
    public static int []variables={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
      static int counter = 0;
    Context context = this;
    static boolean start = false;
    static boolean stop = false;
    static String number = "";
    static int numCounter = 0 ;
    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.

        @Override
        public void onReceivedData(byte[] arg0) {

            try {
                int intValue = 0;
                data = new String(arg0, "UTF-8");

                //byte[] dataByte = "stepCount:".getBytes("UTF-8");

                //data.concat("/n");
                try {
//                    if (data == "s"){  //start recieving data
//                        tvAppend(textView, "starttt");
//                        start = true;
//                    }
//                    else if (data == "p"){
//                        tvAppend(textView, "stoppp");
//                        start = false;
//                        intValue= Integer.parseInt(number);
//                        number="";
//                    }
//                    else{
//                        intValue =  Integer.parseInt(data);
//                        tvAppend(textView, "good");
//                        if (start == true){
//                            tvAppend(textView, "add");
//
//                            number = number + data;
//                            intValue=0;
//                        }
//
//                    }


                    intValue= Integer.parseInt(data);
                    if (intValue>=0){
                        tvAppend(textView, data);

                        if (counter >=15){

                            tvAppend(textView, "DONE!!!/n");
                            ra = data;

//                           SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//                           SharedPreferences.Editor editor = preferences.edit();



                            sharedpreferences = getSharedPreferences("MyPrefs", 0);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(stepCount, String.valueOf(variables[0] *100 + variables[1] *10 + variables[2]));
                            editor.putString(stepWidth, String.valueOf(variables[3] *100 + variables[4] *10+ variables[5]));
                            editor.putString(sessionTime, String.valueOf(variables[6]*100  + variables[7]*10 + variables[8]));
                            editor.putString(leftArmCount, String.valueOf(variables[9] *100 + variables[10] *10 + variables[11]));
                            editor.putString(rightArmCount, String.valueOf(variables[12] *100 + variables[13]*10 + variables[14]));
                            boolean val = editor.commit();

                            tvAppend(textView, String.valueOf(variables[0]));
                            tvAppend(textView, String.valueOf(val));
                            counter=0;
                        }
                        variables[counter] = intValue;
                        counter = counter + 1;

                    }

                } catch (NumberFormatException e) {

                    arg1 = arg0;

                    tvAppend(textView, data);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    };
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) {
                    connection = usbManager.openDevice(device);
                    serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                    if (serialPort != null) {
                        if (serialPort.open()) { //Set Serial Connection Parameters.
                            setUiEnabled(true);
                            serialPort.setBaudRate(115200);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            serialPort.read(mCallback);
                            tvAppend(textView,"Serial connection opened!\n");

                        } else {
                            Log.d("SERIAL", "PORT NOT OPEN");
                        }
                    } else {
                        Log.d("SERIAL", "PORT IS NULL");
                    }
                } else {
                    Log.d("SERIAL", "PERM NOT GRANTED");
                }
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                onClickStart(startButton);
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                onClickStop(stopButton);

            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploading_data);
        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
        startButton = (Button) findViewById(R.id.buttonStart);
        sendButton = (Button) findViewById(R.id.buttonSend);
        clearButton = (Button) findViewById(R.id.buttonClear);
        stopButton = (Button) findViewById(R.id.buttonStop);
        back = (Button) findViewById(R.id.button_second);
       // editText = (EditText) findViewById(R.id.editText);
        textView = (TextView) findViewById(R.id.textView);
        setUiEnabled(false);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);


    }

    public void setUiEnabled(boolean bool) {
        startButton.setEnabled(!bool);
        sendButton.setEnabled(bool);
        stopButton.setEnabled(bool);
        textView.setEnabled(bool);

    }
    public void onClickStart(View view) {
        tvAppend(textView, "\nStarting connecting and uploading... ");
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();

        if (!usbDevices.isEmpty()) {
            tvAppend(textView, "\nDevices found... ");
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                if (deviceVID == 0x2341)//Arduino Vendor ID
                {
                    PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(device, pi);
                    keep = false;
                } else {
                    connection = null;
                    device = null;
                    tvAppend(textView, "data is null");
                }

                if (!keep)
                    break;
            }
        }
        else {
            tvAppend(textView, "\nNo devices found!");
        }


    }

    public void onClickBack(View view) {

        Intent intent = new Intent(UploadingData.this,
                MainActivity.class);
        startActivity(intent);


    }

    public void onClickSend(View view) {
        String string = "upload";
        serialPort.write(string.getBytes());
        tvAppend(textView, "\n<Sending upload command>" + string + "\n");

    }

    public void onClickStop(View view) {
        setUiEnabled(false);
        serialPort.close();
        tvAppend(textView,"\nSerial Connection Closed! \n");

    }

    public void onClickClear(View view) {
        textView.setText(" ");
    }

    private void tvAppend(TextView tv, CharSequence text) {
        final TextView ftv = tv;
        final CharSequence ftext = text;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ftv.append(ftext);
            }
        });
    }

}

