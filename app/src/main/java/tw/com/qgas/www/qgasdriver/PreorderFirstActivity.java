package tw.com.qgas.www.qgasdriver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class PreorderFirstActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    public interface AsyncResponse {
        public void processFinish(String output);
    }

    private static final int REQUEST_ENABLE_BT = 1;

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket  mBtSocket;
    private SingBroadcastReceiver mReceiver;

    private UUID myUUID;
    private final String UUID_STRING_WELL_KNOWN_SPP = "00001101-0000-1000-8000-00805F9B34FB";
    private final String DRFID_READER = "RFID08V7-35d";

    ThreadConnectBTdevice myThreadConnectBTdevice;
    ThreadConnected myThreadConnected;

    ArrayList<String> rfidArrayList;
    ListView listRFID;
    ArrayAdapter<String> rfidAdapter;
    TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int value = -1; // or other values
        int kg15 = 0, kg20 = 0;
        String id = "", name = "", address = "", price = "";

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preorder_first);

        EditText mNameField = (EditText)findViewById(R.id.name_input);
        EditText mAddressField = (EditText)findViewById(R.id.address_input);
        EditText mPriceField = (EditText)findViewById(R.id.price_input);

        EditText m15kg = (EditText)findViewById(R.id.conatiner15_input);
        EditText m20kg = (EditText)findViewById(R.id.conatiner20_input);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            value = b.getInt("key");
            id    = b.getString("id");
            name    = b.getString("name");
            address    = b.getString("address");
            price    = b.getString("price");
            kg15 = b.getInt("kg15");
            kg20 = b.getInt("kg20");
        }
        mNameField.setText(name);
        mAddressField.setText(address);
        mPriceField.setText(price);
        m15kg.setText(String.valueOf(kg15));
        m20kg.setText(String.valueOf(kg20));

        /// bluetooth

        myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);

        listRFID = (ListView)findViewById(R.id.bt2_list_view);

        rfidArrayList = new ArrayList<String>();
        rfidAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rfidArrayList);
        listRFID.setAdapter(rfidAdapter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                //Log.v("qGas", "bluetoothAdapter.getBondedDevices " + device.getName());
                if (DRFID_READER.equals(device.getName())) {

                    myThreadConnectBTdevice = new ThreadConnectBTdevice(device);
                    myThreadConnectBTdevice.start();
                }
            }
        } else {
            if (bluetoothAdapter.isDiscovering()){
                bluetoothAdapter.cancelDiscovery();
            }
            //re-start discovery
            bluetoothAdapter.startDiscovery();
            mReceiver = new SingBroadcastReceiver();
            IntentFilter ifilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            this.registerReceiver(mReceiver, ifilter);
        }
    }

    private class SingBroadcastReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction(); //may need to chain this to a recognizing function
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a Toast
                String derp = device.getName() + " - " + device.getAddress();
                //Toast.makeText(context, derp, Toast.LENGTH_LONG);

                if (DRFID_READER.equals(device.getName())) {

                    myThreadConnectBTdevice = new ThreadConnectBTdevice(device);
                    myThreadConnectBTdevice.start();
                }
            }
        }
    }

    private class ThreadConnectBTdevice extends Thread {

        private BluetoothSocket bluetoothSocket = null;
        private final BluetoothDevice bluetoothDevice;

        private ThreadConnectBTdevice(BluetoothDevice device) {

            bluetoothDevice = device;

            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            boolean success = false;
            try {
                bluetoothSocket.connect();
                success = true;

            } catch (IOException e) {
                e.printStackTrace();

                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }

            if (success) {

                myThreadConnected = new ThreadConnected(bluetoothSocket);
                myThreadConnected.start();
            }
        }

        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class ThreadConnected extends Thread {

        private final BluetoothSocket connectedBluetoothSocket;
        private final InputStream connectedInputStream;
        private final OutputStream connectedOutputStream;

        int count = 0;
        String epcgReceived;
        String epcstrReceived;

        public ThreadConnected(BluetoothSocket socket) {
            connectedBluetoothSocket = socket;
            InputStream in = null;
            OutputStream out = null;

            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            connectedInputStream = in;
            connectedOutputStream = out;
        }

        @Override
        public void run() {

            byte[] buffer = new byte[1024];
            byte[] epcbuffer = new byte[1024];
            byte[] epc = new byte[12];
            int bytes;

            String wholeEPC = "";
            String EPCStr = "";

            while (true) {
                try {
                    bytes = connectedInputStream.read(buffer);

                    if (count + bytes <= 23) {
                        for (int i = 0; i < bytes; i++) {
                            epcbuffer[count+i] = buffer[i];
                        }
                        count += bytes;
                    } else {
                        for (int i = 0; i < bytes; i++) {
                            epcbuffer[i] = buffer[i];
                        }
                        count = bytes;
                    }
                    if (count == 23) {
                        for (int i = 0; i < 12; i++) {
                            epc[i] = epcbuffer[10+i];
                        }
                        epcstrReceived = bytesToHex(epc);
                        //epcgReceived = "EPC bytes received:\n" + epcstrReceived;
                        //Log.v("qGas", epcstrReceived);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (count == 23) {
                                rfidArrayList.add(epcstrReceived);
                                rfidAdapter.notifyDataSetChanged();
                            }
                        }
                    });

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                connectedOutputStream.write(buffer);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void cancel() {
            try {
                connectedBluetoothSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        private final char[] hexArray = "0123456789ABCDEF".toCharArray();

        public String bytesToHex(byte[] bytes) {
            char[] hexChars = new char[bytes.length * 2];
            for ( int j = 0; j < bytes.length; j++ ) {
                int v = bytes[j] & 0xFF;
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            }
            return new String(hexChars);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    static class WebAPITask extends AsyncTask<String, Void, String> {

        private Exception exception;
        private URL url;
        private HttpURLConnection urlConnection = null;
        private String server_response;
        public AsyncResponse delegate = null;

        WebAPITask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        protected String doInBackground(String... urls) {
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    server_response = readStream(urlConnection.getInputStream());
                    //Log.v("CatalogClient", server_response);
                    urlConnection.disconnect();
                    return server_response;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
            urlConnection.disconnect();

            return null;
        }

        private String readStream(InputStream is) {
            try {
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                int i = is.read();
                while (i != -1) {
                    bo.write(i);
                    i = is.read();
                }
                return bo.toString();
            } catch (IOException e) {
                return "";
            }
        }

        protected void onPostExecute(String result) {
            // TODO: check this.exception
            // TODO: do something with the feed
            //textView.setText(result);
            delegate.processFinish(result);
        }
    }
 }
