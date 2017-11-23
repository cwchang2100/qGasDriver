package tw.com.qgas.www.qgasdriver;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.uk.tsl.rfid.ModelBase;
import com.uk.tsl.rfid.TSLBluetoothDeviceActivity;
import com.uk.tsl.rfid.WeakHandler;
import com.uk.tsl.rfid.asciiprotocol.AsciiCommander;
import com.uk.tsl.rfid.asciiprotocol.commands.FactoryDefaultsCommand;
import com.uk.tsl.rfid.asciiprotocol.responders.LoggerResponder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.HeaderIterator;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.protocol.HttpClientContext;
import cz.msebera.android.httpclient.cookie.Cookie;
import cz.msebera.android.httpclient.entity.mime.Header;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;

import static android.R.id.list;

//public class MainActivity extends AppCompatActivity {
public class MainActivity extends TSLBluetoothDeviceActivity {

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

    static WebAPITask mWebTask = null;
    static CookieStore mCookieStore = null;

    String mAccount;
    String mPassword;

    AsyncResponse mLoginResponse;
    AsyncResponse mNullResponse;

    private static final int REQUEST_ENABLE_BT = 1;

    private static ArrayList<BluetoothDevice> pairedDeviceArrayList;
    private static RFIDAdapter pairedDeviceAdapter;

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket               mBtSocket;
    //private readerBroadcastReceiver mReceiver;

    private static UUID myUUID;
    private final String UUID_STRING_WELL_KNOWN_SPP = "00001101-0000-1000-8000-00805F9B34FB";
    private final String DRFID_READER_PREFIX = "RFID08"; // "RFID08V7-35d";
    //private static final String HOST_SERVER = "http://192.168.11.110:8080";
    private static final String HOST_SERVER = "http://www.qgas.com.tw";

    static ThreadConnectBTdevice myThreadConnectBTdevice = null;
    static ThreadConnected myThreadConnected = null;
    static BluetoothDevice bluetoothDevice;

    static ArrayList<GasContainer> rfidArrayList;
    static ContainerAdapter rfidAdapter;

    static ArrayList<GasContainer> oldrfidArrayList_14kg;
    static OldContainerAdapter oldrfidAdapter_14kg;
    static ArrayList<GasContainer> oldrfidArrayList_20kg;
    static OldContainerAdapter oldrfidAdapter_20kg;

    static ArrayList<GasContainer> newrfidArrayList_14kg;
    static NewContainerAdapter newrfidAdapter_14kg;
    static ArrayList<GasContainer> newrfidArrayList_20kg;
    static NewContainerAdapter newrfidAdapter_20kg;

    static ArrayList<GasContainer> carrfidArrayList_14kg;
    static ContainerAdapter carrfidAdapter_14kg;
    static ArrayList<GasContainer> carrfidArrayList_20kg;
    static ContainerAdapter carrfidAdapter_20kg;

    static ArrayList<GasContainer> outArrayList_14kg;
    static ContainerAdapter outAdapter_14kg;
    static ArrayList<GasContainer> outArrayList_20kg;
    static ContainerAdapter outAdapter_20kg;

    static ArrayList<GasContainer> inArrayList_14kg;
    static ContainerAdapter inAdapter_14kg;
    static ArrayList<GasContainer> inArrayList_20kg;
    static ContainerAdapter inAdapter_20kg;
    static ArrayList<String> inArrayList;

    private static int mOld14 = 0;
    private static int mOld20 = 0;
    private static int mNew14 = 0;
    private static int mNew20 = 0;


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static boolean mLogin = false;
    private static String mCustomerID = "";
    private static String mName = "";
    private static String mAddress = "";
    private static String mPersonalID = "";

    private static CookieManager cookieManager;

    private static ListView listViewPairedDevice;

    static private GasOrder mCurrentOrder = null;

    static TextView mMessage;
    static TextView mBTMessage;
    static TextView mStatus;

    private static SignaturePad mSignaturePad;

    public static final int BTCONNECT = 0;
    public static final int BTRFID = 1;
    public static final int BTFAIL = 2;
    public static final int SETMSG = 3;
    public static final int OLDCMSG = 4;
    public static final int NEWCMSG = 5;
    public static final int CARCMSG = 6;
    public static final int SEARCHMSG = 7;
    public static final int SEARCHBCMSG = 8;
    public static final int OUTMSG = 9;
    public static final int FILLINMSG = 10;
    public static final int STATUSMSG = 11;
    public static final int UPMSG = 12;
    public static final int DOWNMSG = 13;

    static Handler mHandler;

    AppCompatActivity mActivity;

    public static final int NORFID = 0;
    public static final int OLD_CONTAINER = 1;
    public static final int NEW_CONTAINER = 2;
    public static final int SUMMARY_VIEW  = 3;
    public static final int SIGNATURE_VIEW = 4;
    public static final int DONE_VIEW = 6;
    public static final int ALL_VIEW = 7;
    public static final int SEARCH_VIEW = 8;
    public static final int CAR_VIEW = 9;
    public static final int OUT_VIEW = 10;
    public static final int FILLIN_VIEW = 11;
    public static final int STATUS_VIEW = 12;

    public static final int UP_STATE = 0;
    public static final int DOWN_STATE = 1;

    static int mState = NORFID;
    static int mSubState = UP_STATE;
    static int mPosition = -1;

    static TextView mNumber14kg;
    static TextView mNumber20kg;
    static TextView moutNumber14kg;
    static TextView moutNumber20kg;
    static TextView minNumber14kg;
    static TextView minNumber20kg;
    static ArrayList<GasOrder> mSearchItems;
    static EditText mSearchRfidInput;
    static EditText mSearchBarcodeInput;
    static String mReceivedRfid;
    static String mReceivedBarCode;

    static private MainModel mModel;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                Log.d("qGas", "onPageSelected " + position);
                mPosition = position;
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mActivity = this;

        cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);

        pairedDeviceArrayList = new ArrayList<BluetoothDevice>();
        pairedDeviceAdapter = new RFIDAdapter(this, R.layout.rfid_list_item, pairedDeviceArrayList);

        rfidArrayList = new ArrayList<GasContainer>(); // store RFID
        rfidAdapter = new ContainerAdapter(this, R.layout.container_list_item, rfidArrayList);

        oldrfidArrayList_14kg = new ArrayList<GasContainer>(); // store RFID
        oldrfidArrayList_20kg = new ArrayList<GasContainer>(); // store RFID
        newrfidArrayList_14kg = new ArrayList<GasContainer>(); // store RFID
        newrfidArrayList_20kg = new ArrayList<GasContainer>(); // store RFID
        carrfidArrayList_14kg = new ArrayList<GasContainer>(); // store RFID
        carrfidArrayList_20kg = new ArrayList<GasContainer>(); // store RFID
        outArrayList_14kg = new ArrayList<GasContainer>(); // store RFID
        outArrayList_20kg = new ArrayList<GasContainer>(); // store RFID
        inArrayList_14kg = new ArrayList<GasContainer>(); // store RFID
        inArrayList_20kg = new ArrayList<GasContainer>(); // store RFID
        // listRFID.setAdapter(rfidAdapter);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case BTCONNECT:
                        mBTMessage.setText(bluetoothDevice.getName() + " is connected!");
                        break;
                    case BTRFID:
                        rfidAdapter.notifyDataSetChanged();
                        //Perform action
                        break;
                    case BTFAIL:
                        mBTMessage.setText("連接失敗!!!");
                        break;
                    case SETMSG:
                        mMessage.setText("登入中...");
                        break;
                    case OLDCMSG:
                        oldrfidAdapter_14kg.notifyDataSetChanged();
                        oldrfidAdapter_20kg.notifyDataSetChanged();
                        break;
                    case NEWCMSG:
                        newrfidAdapter_14kg.notifyDataSetChanged();
                        newrfidAdapter_20kg.notifyDataSetChanged();
                        break;
                    case CARCMSG:
                        carrfidAdapter_14kg.notifyDataSetChanged();
                        carrfidAdapter_20kg.notifyDataSetChanged();
                        mNumber14kg.setText(String.valueOf(carrfidArrayList_14kg.size()));
                        mNumber20kg.setText(String.valueOf(carrfidArrayList_20kg.size()));
                        break;
                    case OUTMSG:
                        outAdapter_14kg.notifyDataSetChanged();
                        outAdapter_20kg.notifyDataSetChanged();
                        moutNumber14kg.setText(String.valueOf(outArrayList_14kg.size()));
                        moutNumber20kg.setText(String.valueOf(outArrayList_20kg.size()));
                        break;
                    case FILLINMSG:
                        inAdapter_14kg.notifyDataSetChanged();
                        inAdapter_20kg.notifyDataSetChanged();
                        minNumber14kg.setText(String.valueOf(inArrayList_14kg.size()));
                        minNumber20kg.setText(String.valueOf(inArrayList_20kg.size()));
                        break;
                    case SEARCHMSG:
                        mSearchRfidInput.setText(mReceivedRfid);
                        break;
                    case SEARCHBCMSG:
                        mSearchBarcodeInput.setText(mReceivedBarCode);
                        break;
                    case STATUSMSG:
                        if (mSubState == UP_STATE) {
                            mStatus.setText("氣瓶上車運出");
                        } else {
                            mStatus.setText("氣瓶運入下車");
                        }
                        break;
                }
            }
        };

        mSubState = UP_STATE;

        //Log.v("qGas", "startDiscovery.");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        Log.v("qGas", "bluetoothAdapter.getBondedDevices " + pairedDevices.size());

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().contains(DRFID_READER_PREFIX)) {
                    Log.v("qGas", "find paired device " + device.getName());

                    pairedDeviceArrayList.add(device);
                }
            }
        }

        //mReceiver = new readerBroadcastReceiver();
        //IntentFilter ifilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        //this.registerReceiver(mReceiver, ifilter);
        //ifilter = new IntentFilter(AsciiCommander.STATE_CHANGED_NOTIFICATION);
        //this.registerReceiver(mReceiver, ifilter);

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();

        //
        // An AsciiCommander has been created by the base class
        //
        AsciiCommander commander = getCommander();

        // Add the LoggerResponder - this simply echoes all lines received from the reader to the log
        // and passes the line onto the next responder
        // This is added first so that no other responder can consume received lines before they are logged.
        commander.addResponder(new LoggerResponder());

        // Add a synchronous responder to handle synchronous commands
        commander.addSynchronousResponder();

        //Create a (custom) model and configure its commander and handler
        mModel = new MainModel();
        mModel.setCommander(getCommander());
        mModel.setHandler(mGenericModelHandler);


        SharedPreferences sharedPref = mActivity.getPreferences(Context.MODE_PRIVATE);
        String account = sharedPref.getString("account", null);
        String password = sharedPref.getString("password", null);

        mNullResponse = new AsyncResponse() {
            @Override
            public void processFinish(String output) {
            }
        };

        mLoginResponse = new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                //Here you will receive the result fired from async class
                //of onPostExecute(result) method.
                Log.v("qGas", output);
                try {
                    if (output != null && !output.equals("NG")) {
                        JSONObject json = new JSONObject(output);
                        mCustomerID = json.getString("id");
                        mName = json.getString("name");
                        mAddress = json.getString("address");
                        mPersonalID = json.getString("personal_id");
                        mLogin = true;
                        if (mMessage != null)
                            mMessage.setText(mName + " 登入!!!");

                        SharedPreferences sharedPref = mActivity.getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("account", mAccount);
                        editor.putString("password", mPassword);
                        editor.commit();

                    } else {
                        if (mMessage != null)
                            mMessage.setText("登入錯誤!!!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Log.v("qGas", output);
                    if (mMessage != null)
                        mMessage.setText("登入錯誤!");
                }
            }
        };

        if (account != null && password != null) {
            mWebTask = new WebAPITask(mLoginResponse);
            String url;
            mAccount = account;
            mPassword = password;
            url = HOST_SERVER + "/api/login/1?account=" + account + "&password=" + password;
            Log.v("qGas", url);
            mWebTask.execute(url);
            if (mMessage != null)
                mMessage.setText("登入中...");
        }
    }
/*
    private class readerBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction(); //may need to chain this to a recognizing function
            Log.v("qGas", "readerBroadcastReceiver...action:" + action);

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a Toast
                String derp = device.getName() + " - " + device.getAddress();
                //Toast.makeText(context, derp, Toast.LENGTH_LONG);
                Log.v("qGas", "readerBroadcastReceiver..." + derp);

                if (device.getName().contains(DRFID_READER_PREFIX)) {
                    pairedDeviceArrayList.add(device);
                 }
            }
            if (AsciiCommander.STATE_CHANGED_NOTIFICATION.equals(action)) {
                String connectionStateMsg = intent.getStringExtra(AsciiCommander.REASON_KEY);
                Toast.makeText(context, connectionStateMsg, Toast.LENGTH_SHORT).show();

                displayReaderState();
            }
        }

    }
    */

    //----------------------------------------------------------------------------------------------
    // Model notifications
    //----------------------------------------------------------------------------------------------

    private final WeakHandler<MainActivity> mGenericModelHandler = new WeakHandler<MainActivity>(this) {

        @Override
        public void handleMessage(Message msg, MainActivity thisActivity) {
            try {
                switch (msg.what) {
                    case ModelBase.BUSY_STATE_CHANGED_NOTIFICATION:
                        //TODO: process change in model busy state
                        break;

                    case ModelBase.MESSAGE_NOTIFICATION:
                        // Examine the message for prefix
                        String message = (String)msg.obj;
                        if ( message.startsWith("ER:")) {
                            //mResultTextView.setText( message.substring(3));
                        } else if( message.startsWith("BC:")) {
                            //mBarcodeResultsArrayAdapter.add(message);
                            //scrollBarcodeListViewToBottom();
                            Log.v("qGas", "processBC->" + message.substring(3));
                            processBC(message.substring(3)); // BC
                        } else if( message.startsWith("EPC:")) {
                            Log.v("qGas", "processEPC->" + message.substring(4));
                            processEPC(message.substring(4)); // EPC
                        } else {
                           // mResultsArrayAdapter.add(message);
                           // scrollResultsListViewToBottom();
                            Log.v("qGas", "processEPC->" + message.substring(4));
                            //processEPC(message.substring(4)); // EPC

                        }
                        //UpdateUI();
                        break;

                    default:
                        break;
                }
            } catch (Exception e) {
            }
        }
    };

    private static class ThreadConnectBTdevice extends Thread {

        private BluetoothSocket bluetoothSocket = null;

        private ThreadConnectBTdevice(BluetoothDevice device) {

            bluetoothDevice = device;

            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                mHandler.sendEmptyMessage(BTFAIL);
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
                mHandler.sendEmptyMessage(BTFAIL);
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
                mHandler.sendEmptyMessage(BTCONNECT);
            }
        }

        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                mHandler.sendEmptyMessage(BTFAIL);
                e.printStackTrace();
            }
        }
    }

    private static class ThreadConnected extends Thread {

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
            int index = 0;
            int mode = 0;
            int stage = 0;
            int length = 0;
            int len = 0;
            String wholeEPC = "";
            String EPCStr = "";
            byte c;

            while (true) {
                try {
                    bytes = connectedInputStream.read(buffer);

                    String Received = bytesToHex(buffer);
                    final String msgReceived = String.valueOf(bytes) + " bytes received:\n" + Received;
                    Log.v("qGas", msgReceived);

                    for (int i = 0; i < bytes; i++) {
                        c = buffer[i];
                        //Log.v("qGas", "i: " + i + "index " + index + "read " + c);
                        if (index == 0) {
                            if (c == 0x02) {
                                stage = 1;
                                index++;
                                //Log.v("qGas", "start 0x02");
                            }
                        } else if (index == 1) {
                            if (c == 0x53) {
                                stage = 2;
                                mode  = 1;
                                index++;
                                //Log.v("qGas", "mode 0x53");
                            } else if (c == 0x54) {
                                stage = 2;
                                mode  = 2;
                                index++;
                                ///Log.v("qGas", "mode 0x54");
                            }
                        } else if ((mode == 1 || mode == 2) && index == 2) {
                            length = c;
                            len = 0;
                            index++;
                            Log.v("qGas", "length " + length);
                            if (mode == 2 && c != 0x13) {
                                Log.v("qGas", "wrong length " + c);
                            }
                        } else if (mode == 1 && index == 3) {
                            length = (length << 8) | c;
                            Log.v("qGas", "epc length " + length);
                            len = 0;
                            index++;
                        } else if (mode == 2 && index >= 3 && index < (3 + length)) {
                            if (len == 7) {
                                count = 0;
                                epc[count] = c;
                                count++;
                                //Log.v("qGas", "epc start " + c);
                            } else if (len > 7) {
                                epc[count] = c;
                                count++;
                                //Log.v("qGas", "epc " + c);
                                if (count == 12) {
                                    String epcstrReceived = bytesToHex(epc);

                                    final String epcgReceived = "EPC bytes received:\n" + epcstrReceived;
                                    Log.v("qGas", epcgReceived);

                                    processEPC(epcgReceived);

                                    index  = 0;
                                    mode   = 0;
                                    len    = 0;
                                    length = 0;
                                    count  = 0;
                                }
                            } else {
                                //
                                //Log.v("qGas", "inner " + c);
                            }
                            len++;
                            index++;
                        } else {
                            //Log.v("qGas", "other " + c);
                            index = 0;
                            mode  = 0;
                        }
                    }

                    /*
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (count == 23) {
                                rfidArrayList.add(epcstrReceived);
                                rfidAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                    */

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

    public static void processBC(String bcStr) {
        if (mPosition == 3) {
            mReceivedBarCode = bcStr;
            mHandler.sendEmptyMessage(SEARCHBCMSG);
        }
    }

    public static void processEPC(String epcStr) {

        if (mPosition == 1 && mState == OLD_CONTAINER) {
            Boolean hasRFID = false;

            for (GasContainer co : oldrfidArrayList_14kg) {
                if (co.rfid.equals(epcStr)) {
                    hasRFID = true;
                    co.presented = 1;
                    mHandler.sendEmptyMessage(OLDCMSG);
                }
            }
            for (GasContainer co : oldrfidArrayList_20kg) {
                if (co.rfid.equals(epcStr)) {
                    hasRFID = true;
                    co.presented = 1;
                    mHandler.sendEmptyMessage(OLDCMSG);
                }
            }
        } else if (mPosition == 1 && mState == NEW_CONTAINER) {

            WebAPITask mQueryTask = null;
            AsyncResponse mResponse = new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    //Here you will receive the result fired from async class
                    //of onPostExecute(result) method.
                    if (output != null) {
                        Log.v("qGas", "NEW_CONTAINER mResponse:" + output);
                    } else {
                        return;
                    }
                    try {
                        GasContainer newco = new GasContainer();
                        JSONObject json = new JSONObject(output);
                        if (json.has("barcode")) {
                            newco.barcode = json.getString("barcode");
                        } else {
                            newco.barcode = "";
                        }
                        if (json.has("rfid")) {
                            newco.rfid = json.getString("rfid");
                        } else {
                            newco.rfid = "";
                        }
                        if (json.has("spec")) {
                            newco.spec = json.getInt("spec");
                        } else {
                            newco.spec = 0;
                        }

                        newco.presented = 1;

                        Boolean hasRFID = false;

                        for (GasContainer co : newrfidArrayList_14kg) {
                            if (co.rfid.equals(newco.rfid)) {
                                hasRFID = true;
                            }
                        }
                        if (!hasRFID && newco.spec == 1) {

                            hasRFID = false;
                            for (GasContainer co : carrfidArrayList_14kg) {
                                if (co.rfid.equals(newco.rfid)) {
                                    hasRFID = true;
                                }
                            }
                            if (hasRFID) {
                                newrfidArrayList_14kg.add(newco);
                                mHandler.sendEmptyMessage(NEWCMSG);
                            }
                        }

                        hasRFID = false;

                        for (GasContainer co : newrfidArrayList_20kg) {
                            if (co.rfid.equals(newco.rfid)) {
                                hasRFID = true;
                            }
                        }
                        if (!hasRFID && newco.spec == 2) {
                            hasRFID = false;
                            for (GasContainer co : carrfidArrayList_20kg) {
                                if (co.rfid.equals(newco.rfid)) {
                                    hasRFID = true;
                                }
                            }
                            if (hasRFID) {
                                newrfidArrayList_20kg.add(newco);
                                mHandler.sendEmptyMessage(NEWCMSG);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.v("qGas", "json error");
                    }
                }
            };

            mQueryTask = new WebAPITask(mResponse);
            //mOrderTask.SetResponse(mResponse);

            if (mLogin) {
                mQueryTask.execute(HOST_SERVER + "/api/setcontainer/4?rfid=" +  epcStr);
            }
        } else if (mPosition == 2) {

            WebAPITask mQueryTask = null;
            AsyncResponse mResponse = new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    //Here you will receive the result fired from async class
                    //of onPostExecute(result) method.
                    if (output != null) {
                        Log.v("qGas", "UP/DOWN:" + output);
                    } else {
                        return;
                    }
                    try {
                        GasContainer newco = new GasContainer();
                        JSONObject json = new JSONObject(output);
                        if (json.has("barcode")) {
                            newco.barcode = json.getString("barcode");
                        } else {
                            newco.barcode = "";
                        }
                        if (json.has("rfid")) {
                            newco.rfid = json.getString("rfid");
                        } else {
                            newco.rfid = "";
                        }
                        if (json.has("spec")) {
                            newco.spec = json.getInt("spec");
                        } else {
                            newco.spec = 0;
                        }

                        newco.presented = 1;

                        Boolean hasRFID = false;

                        for (GasContainer co : carrfidArrayList_14kg) {
                            if (co.rfid.equals(newco.rfid)) {
                                hasRFID = true;
                            }
                        }
                        if (!hasRFID && newco.spec == 1) {
                            carrfidArrayList_14kg.add(newco);
                            mHandler.sendEmptyMessage(CARCMSG);
                        }
                        hasRFID = false;

                        for (GasContainer co : carrfidArrayList_20kg) {
                            if (co.rfid.equals(newco.rfid)) {
                                hasRFID = true;
                            }
                        }
                        if (!hasRFID && newco.spec == 2) {
                            carrfidArrayList_20kg.add(newco);
                            mHandler.sendEmptyMessage(CARCMSG);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.v("qGas", "json error");
                    }
                }
            };

            mQueryTask = new WebAPITask(mResponse);

            if (mLogin) {
                if (mSubState == UP_STATE) {
                     mQueryTask.execute(HOST_SERVER + "/api/setcontainer/3?rfid=" + epcStr);
                } else {
                    mQueryTask.execute(HOST_SERVER + "/api/setcontainer/1?rfid=" + epcStr);
                }
            }
        } else if (mPosition == 3) {
            mReceivedRfid = epcStr;
            mHandler.sendEmptyMessage(SEARCHMSG);
        } else if (mPosition == 4) {

            WebAPITask mQueryTask = null;
            AsyncResponse mResponse = new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    //Here you will receive the result fired from async class
                    //of onPostExecute(result) method.
                    Log.v("qGas", output);
                    try {
                        GasContainer newco = new GasContainer();
                        JSONObject json = new JSONObject(output);
                        if (json.has("barcode")) {
                            newco.barcode = json.getString("barcode");
                        } else {
                            newco.barcode = "";
                        }
                        if (json.has("rfid")) {
                            newco.rfid = json.getString("rfid");
                        } else {
                            newco.rfid = "";
                        }
                        if (json.has("spec")) {
                            newco.spec = json.getInt("spec");
                        } else {
                            newco.spec = 0;
                        }

                        newco.presented = 1;

                        Boolean hasRFID = false;

                        for (GasContainer co : outArrayList_14kg) {
                            if (co.rfid.equals(newco.rfid)) {
                                hasRFID = true;
                            }
                        }
                        if (!hasRFID && newco.spec == 1) {
                            outArrayList_14kg.add(newco);
                            mHandler.sendEmptyMessage(OUTMSG);
                        }
                        hasRFID = false;

                        for (GasContainer co : outArrayList_20kg) {
                            if (co.rfid.equals(newco.rfid)) {
                                hasRFID = true;
                            }
                        }
                        if (!hasRFID && newco.spec == 2) {
                            outArrayList_20kg.add(newco);
                            mHandler.sendEmptyMessage(OUTMSG);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.v("qGas", "json error");
                    }
                }
            };

            mQueryTask = new WebAPITask(mResponse);

            if (mLogin) {
                mQueryTask.execute(HOST_SERVER + "/api/querycontainer?rfid=" +  epcStr);
            }
        } else if (mPosition == 5) {

            WebAPITask mQueryTask = null;
            AsyncResponse mResponse = new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    //Here you will receive the result fired from async class
                    //of onPostExecute(result) method.
                    Log.v("qGas", output);
                    try {
                        GasContainer newco = new GasContainer();
                        JSONObject json = new JSONObject(output);
                        if (json.has("barcode")) {
                            newco.barcode = json.getString("barcode");
                        } else {
                            newco.barcode = "";
                        }
                        if (json.has("rfid")) {
                            newco.rfid = json.getString("rfid");
                        } else {
                            newco.rfid = "";
                        }
                        if (json.has("spec")) {
                            newco.spec = json.getInt("spec");
                        } else {
                            newco.spec = 0;
                        }

                        newco.presented = 1;

                        Boolean hasRFID = false;

                        for (GasContainer co : inArrayList_14kg) {
                            if (co.rfid.equals(newco.rfid)) {
                                hasRFID = true;
                            }
                        }
                        if (!hasRFID && newco.spec == 1) {
                            inArrayList_14kg.add(newco);
                            mHandler.sendEmptyMessage(FILLINMSG);
                        }
                        hasRFID = false;

                        for (GasContainer co : inArrayList_20kg) {
                            if (co.rfid.equals(newco.rfid)) {
                                hasRFID = true;
                            }
                        }
                        if (!hasRFID && newco.spec == 2) {
                            inArrayList_20kg.add(newco);
                            mHandler.sendEmptyMessage(FILLINMSG);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.v("qGas", "json error");
                    }
                }
            };

            mQueryTask = new WebAPITask(mResponse);

            if (mLogin) {
                mQueryTask.execute(HOST_SERVER + "/api/querycontainer?rfid=" +  epcStr);
            }
        }
    }

    @Override
    public synchronized void onPause() {
        super.onPause();

        mModel.setEnabled(false);

        // Unregister to receive notifications from the AsciiCommander
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    @Override
    public synchronized void onResume() {
    	super.onResume();

        mModel.setEnabled(true);

        // Register to receive notifications from the AsciiCommander
        //LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
        //	      new IntentFilter(AsciiCommander.STATE_CHANGED_NOTIFICATION));

        displayReaderState();
    }


	private MenuItem mReconnectMenuItem;
	private MenuItem mConnectMenuItem;
	private MenuItem mDisconnectMenuItem;
	private MenuItem mResetMenuItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
		getMenuInflater().inflate(R.menu.reader_menu, menu);

		mResetMenuItem = menu.findItem(R.id.reset_reader_menu_item);
		mReconnectMenuItem = menu.findItem(R.id.reconnect_reader_menu_item);
		mConnectMenuItem = menu.findItem(R.id.insecure_connect_reader_menu_item);
		mDisconnectMenuItem= menu.findItem(R.id.disconnect_reader_menu_item);
        return true;
    }

	/**
	 * Prepare the menu options
	 */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

    	boolean isConnected = getCommander().isConnected();
    	mResetMenuItem.setEnabled(isConnected);
    	mDisconnectMenuItem.setEnabled(isConnected);

    	mReconnectMenuItem.setEnabled(!isConnected);
    	mConnectMenuItem.setEnabled(!isConnected);
    	
    	return super.onPrepareOptionsMenu(menu);
    }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {

        case R.id.reconnect_reader_menu_item:
            Toast.makeText(this.getApplicationContext(), "Reconnecting...", Toast.LENGTH_LONG).show();
        	reconnectDevice();
         	return true;

        case R.id.insecure_connect_reader_menu_item:
            // Choose a device and connect to it
        	selectDevice();
            return true;

        case R.id.disconnect_reader_menu_item:
            Toast.makeText(this.getApplicationContext(), "Disconnecting...", Toast.LENGTH_SHORT).show();
        	disconnectDevice();
        	displayReaderState();
        	return true;

        case R.id.reset_reader_menu_item:
        	resetReader();
        	return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void displayReaderState() {
		String connectionMsg = "Reader: " + (getCommander().isConnected() ? getCommander().getConnectedDeviceName() : "Disconnected");
		setTitle(connectionMsg);
    }
	
	//
    // Handle reset controls
    //
    private void resetReader() {
		try {
			// Reset the reader
			FactoryDefaultsCommand fdCommand = FactoryDefaultsCommand.synchronousCommand();
			getCommander().executeCommand(fdCommand);
			String msg = "Reset " + (fdCommand.isSuccessful() ? "succeeded" : "failed");
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        ListView listRFID;

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance() {
            PlaceholderFragment fragment = new PlaceholderFragment();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            mMessage = (TextView) rootView.findViewById(R.id.message_text);
            mBTMessage = (TextView)rootView.findViewById(R.id.bt_text);
            listRFID = (ListView)rootView.findViewById(R.id.reader_list_view);

            if (listRFID != null) {

                //rfidAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, rfidArrayList);

                listRFID.setAdapter(pairedDeviceAdapter);
                listRFID.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //BluetoothDevice item = (BluetoothDevice) parent.getListAdapter().getItem(position);
                        BluetoothDevice device = pairedDeviceArrayList.get(position);

                        myThreadConnectBTdevice = new ThreadConnectBTdevice(device);
                        myThreadConnectBTdevice.start();
                        mBTMessage.setText("尋找中...");
                    }

                });

            } else {
                Log.v("qGas", "listRFID does not exist.");
            }

            return rootView;
        }

   }

    public static class AllOrderFragment extends Fragment {

        private WebAPITask mOrderTask = null;
        private JSONObject mRes = null;
        private ArrayList<GasOrder> items;
        //private ArrayAdapter<GasOrder> listAdapter;
        private OrderAdapter listAdapter;
        private ListView mList;

        private MapView mMapView;
        private GoogleMap googleMap = null;

        private Float mGpsX = 0.0f;
        private Float mGpsY = 0.0f;
        private Boolean mAllOrder = false;

        public AllOrderFragment() {
        }

        public static AllOrderFragment newInstance() {
            AllOrderFragment fragment = new AllOrderFragment();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_order, container, false);

            mList = (ListView) rootView.findViewById(R.id.order_list_view);

            AsyncResponse mResponse = new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    //Here you will receive the result fired from async class
                    //of onPostExecute(result) method.
                    Log.v("qGas", output);
                    items = new ArrayList<GasOrder>();
                    String previous_time = "";

                    try {
                        JSONArray json = new JSONArray(output);

                        for (int i = 0; i < json.length(); i++) {
                            JSONObject e = json.getJSONObject(i);
                            JSONObject s = new JSONObject(e.getString("gas_order"));

                            int kg14 = 0;
                            int kg20 = 0;

                            if (s.has("w14kg")) {
                                kg14 = s.getInt("w14kg");
                            } else {
                                kg14 = 0;
                            }
                            if (s.has("w20kg")) {
                                kg20 = s.getInt("w20kg");
                            } else {
                                kg20 = 0;
                            }

                            GasOrder tmp = new GasOrder();

                            tmp.id = e.getInt("id");

                            tmp.name = e.getString("name");

                            if (e.has("address")) {
                                tmp.address = e.getString("address");
                            } else {
                                tmp.address = "";
                            }
                            tmp.price = e.getString("cost");
                            tmp.start_time = e.getString("start_time");
                            tmp.end_time = e.getString("end_time");
                            tmp.order_time = e.getString("order_time");
                            tmp.kg14 = kg14;
                            tmp.kg20 = kg20;
                            tmp.gpsx = Float.valueOf(e.getString("gpsx"));
                            tmp.gpsy = Float.valueOf(e.getString("gpsy"));
                            if (mGpsX == 0.0f) {
                                mGpsX = tmp.gpsx;
                            }
                            if (mGpsY == 0.0f) {
                                mGpsY = tmp.gpsy;
                            }
                            if (e.has("family")) {
                                tmp.family = e.getInt("family");
                            } else {
                                tmp.family = 0;
                            }
                            if (e.has("remain")) {
                                tmp.remain = e.getInt("remain");
                            } else {
                                tmp.remain = 0;
                            }
                            if (e.has("type")) {
                                tmp.type = e.getInt("type");
                            } else {
                                tmp.type = 0;
                            }

                            tmp.p20kg = new ArrayList<GasContainer>();

                            if (e.has("p20kg")) {

                                JSONArray jsonarray = e.getJSONArray("p20kg");

                                for (int j = 0; j < jsonarray.length(); j++) {
                                    JSONObject je = jsonarray.getJSONObject(j);
                                    GasContainer dummy = new GasContainer();

                                    dummy.id = je.getInt("id");
                                    dummy.gas = 0;
                                    tmp.p20kg.add(dummy);
                                }
                            }

                            tmp.p14kg = new ArrayList<GasContainer>();

                            if (e.has("p14kg")) {
                                JSONArray jsonarray = e.getJSONArray("p14kg");

                                for (int j = 0; j < jsonarray.length(); j++) {
                                    JSONObject je = jsonarray.getJSONObject(j);
                                    GasContainer dummy = new GasContainer();

                                    dummy.id = je.getInt("id");
                                    dummy.gas = 0;
                                    tmp.p14kg.add(dummy);
                                }
                            }

                            tmp.s20kg = new ArrayList<GasContainer>();

                            if (e.has("s20kg")) {
                                JSONArray jsonarray = e.getJSONArray("s20kg");

                                for (int j = 0; j < jsonarray.length(); j++) {
                                    JSONObject je = jsonarray.getJSONObject(j);
                                    GasContainer dummy = new GasContainer();
                                    dummy.id = je.getInt("id");
                                    dummy.gas = 0;
                                    dummy.rfid = je.getString("rfid");
                                    dummy.barcode = je.getString("barcode");
                                    tmp.s20kg.add(dummy);
                                }
                            }

                            tmp.s14kg = new ArrayList<GasContainer>();

                            if (e.has("s14kg")) {
                                JSONArray jsonarray = e.getJSONArray("s14kg");

                                for (int j = 0; j < jsonarray.length(); j++) {
                                    JSONObject je = jsonarray.getJSONObject(j);
                                    GasContainer dummy = new GasContainer();
                                    dummy.id = je.getInt("id");
                                    dummy.gas = 0;
                                    dummy.rfid = je.getString("rfid");
                                    dummy.barcode = je.getString("barcode");
                                    tmp.s14kg.add(dummy);
                                }
                            }
                            tmp.type = GasOrder.NORMAL_ORDER;
                            /*
                            if (previous_time != tmp.start_time) {
                                GasOrder title = new GasOrder();
                                title.start_time = tmp.start_time;
                                title.type = GasOrder.TITLE_ORDER;
                                items.add(title);
                                previous_time = tmp.start_time;
                            }
                            */

                            items.add(tmp);
                        }
                        mAllOrder = true;


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.v("qGas", "json error");
                    }

                    // sorting
                    Collections.sort(items);
                    boolean setTitle = false;
                    for (int k = 0; k < items.size(); k++) {
                        GasOrder front, back;

                        if (k == items.size()-1) {
                            front = items.get(k);
                            if (k == 0) {
                                front.hasTitle = true;
                            }
                            break;
                        }
                        front = items.get(k);
                        back = items.get(k+1);
                        if (k == 0) {
                            front.hasTitle = true;
                        }
                        if (front.start_time.equals(back.start_time)) {
                            back.hasTitle = false;
                        } else {
                            back.hasTitle = true;
                        }
                    }

                    listAdapter = new OrderAdapter(getActivity(), R.layout.list_item, items);

                    /*
                    listAdapter = new ArrayAdapter<GasOrder>(getActivity(), android.R.layout.simple_expandable_list_item_2, android.R.id.text1, items) {
                        @Override
                        public View getView(int pos, View convert, ViewGroup group) {
                            View v = super.getView(pos, convert, group);
                            TextView t1 = (TextView) v.findViewById(android.R.id.text1);
                            TextView t2 = (TextView) v.findViewById(android.R.id.text2);
                            t1.setText(getItem(pos).name);
                            t2.setText(getItem(pos).start_time);
                            return v;
                        }
                    };
                    */

                    mList.setAdapter(listAdapter);
                    mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            GasOrder item = items.get(position);
                            if (item.type != GasOrder.NORMAL_ORDER) {
                                return;
                            }
                            mCurrentOrder = item;

                            FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
                            Fragment fragment = new SingleOrderFragment();

                            trans.replace(R.id.order_fragment, fragment);
                            trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            trans.addToBackStack(null);

                            trans.commit();
                        }

                    });
                    drawMarks();
                }
            };

            mOrderTask = new WebAPITask(mResponse);
            //mOrderTask.SetResponse(mResponse);

            if (mLogin && !mAllOrder) {
                mOrderTask.execute(HOST_SERVER + "/api/allorder?pid=" +  mPersonalID);
            }

            mMapView = (MapView) rootView.findViewById(R.id.ordermapView);
            mMapView.onCreate(savedInstanceState);

            mMapView.onResume(); // needed to get the map to display immediately

            try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }

            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap mMap) {
                    googleMap = mMap;

                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    googleMap.setMyLocationEnabled(true);

                    // For dropping a marker at a point on the Map

                    //LatLng taipei = new LatLng(25, 121.5);
                    //googleMap.addMarker(new MarkerOptions().position(taipei).title("Marker Title").snippet("Marker Description"));

                    // For zooming automatically to the location of the marker
                    //CameraPosition cameraPosition = new CameraPosition.Builder().target(taipei).zoom(12).build();
                    //googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    drawMarks();
                }
            });

            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            mMapView.onResume();
        }

        @Override
        public void onPause() {
            super.onPause();
            mMapView.onPause();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mMapView.onDestroy();
        }

        @Override
        public void onLowMemory() {
            super.onLowMemory();
            mMapView.onLowMemory();
        }

        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);

            if (isVisibleToUser) {
                if (mLogin && !mAllOrder) {
                    mOrderTask.execute(HOST_SERVER + "/api/allorder?pid=" +  mPersonalID);
                }
            } else {
                Log.d("qGas", "Fragment is not visible.");
            }
        }

        public void drawMarks() {
            if (googleMap != null && mGpsX != 0.0f && mGpsY != 0.0f) {
                LatLng spot = new LatLng(mGpsX, mGpsY);
                //Log.v("qGas", "mGpsX mGpsY: " + mGpsX + " " + mGpsY);
                //googleMap.addMarker(new MarkerOptions().position(spot).title("Marker Title").snippet("Marker Description"));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(spot).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                for (int i = 0; i < items.size(); i++) {
                    GasOrder item = items.get(i);
                    if (item.type == GasOrder.NORMAL_ORDER) {
                        spot = new LatLng(item.gpsx, item.gpsy);
                        //Log.v("qGas", "item.gpsx item.gpsy: " + item.gpsx + " " + item.gpsy);
                        googleMap.addMarker(new MarkerOptions().position(spot).title(item.name).snippet(item.start_time));
                    }
                }

            }
        }
    }

    public static class SingleOrderFragment extends Fragment {

        private WebAPITask mSubmitTask = null;

        public SingleOrderFragment() {
        }

        public static SingleOrderFragment newInstance() {
            SingleOrderFragment fragment = new SingleOrderFragment();

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_single_order, container, false);

            EditText mNameField = (EditText)rootView.findViewById(R.id.name_input);
            EditText mAddressField = (EditText)rootView.findViewById(R.id.address_input);
            EditText mPriceField = (EditText)rootView.findViewById(R.id.price_input);
            EditText mFamilyField = (EditText)rootView.findViewById(R.id.family_input);

            EditText m14kg = (EditText)rootView.findViewById(R.id.conatiner14_input);
            EditText m20kg = (EditText)rootView.findViewById(R.id.conatiner20_input);

            if (mCurrentOrder != null) {
                mNameField.setText(mCurrentOrder.name);
                mAddressField.setText(mCurrentOrder.address);
                mPriceField.setText(mCurrentOrder.price);
                mFamilyField.setText(String.valueOf(mCurrentOrder.family));
                m14kg.setText(String.valueOf(mCurrentOrder.kg14));
                m20kg.setText(String.valueOf(mCurrentOrder.kg20));
                if (mCurrentOrder.s14kg.size() > 0) {

                    for (int i = 0; i < mCurrentOrder.s14kg.size(); i++) {
                        GasContainer co = mCurrentOrder.s14kg.get(i);
                        co.presented = 0;
                        oldrfidArrayList_14kg.add(co);
                    }
                }
                if (mCurrentOrder.s20kg.size() > 0) {

                    for (int i = 0; i < mCurrentOrder.s20kg.size(); i++) {
                        GasContainer co = mCurrentOrder.s20kg.get(i);
                        co.presented = 0;
                        oldrfidArrayList_20kg.add(co);
                    }
                }
            }

            //mList = (ListView) rootView.findViewById(R.id.conatiner_list_view);
            //mList.setAdapter(rfidAdapter);

            mSubmitTask = new WebAPITask(new AsyncResponse() {
                @Override
                public void processFinish(String output) {

                }
            });
            if (mLogin) {
                //mAuthTask.execute("http://192.168.11.110:8080/api/urgentorder");
            }

            return rootView;
        }
    }

    public static class OldContainerFragment extends Fragment {

        private WebAPITask mSubmitTask = null;
        private ListView mList_14kg;
        private ListView mList_20kg;

        public OldContainerFragment() {
        }

        public static OldContainerFragment newInstance() {
            OldContainerFragment fragment = new OldContainerFragment();

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_old_container, container, false);
            TextView p14no = (TextView)rootView.findViewById(R.id.old_14kg_no);
            TextView p20no = (TextView)rootView.findViewById(R.id.old_20kg_no);

            mState = OLD_CONTAINER;

            if (mCurrentOrder != null) {
                mList_14kg = (ListView) rootView.findViewById(R.id.old_conatiner_list_14kg_view);
                oldrfidAdapter_14kg = new OldContainerAdapter(getActivity(), R.layout.old_container_list_item, oldrfidArrayList_14kg);
                mList_14kg.setAdapter(oldrfidAdapter_14kg);
                mList_20kg = (ListView) rootView.findViewById(R.id.old_conatiner_list_20kg_view);
                oldrfidAdapter_20kg = new OldContainerAdapter(getActivity(), R.layout.old_container_list_item, oldrfidArrayList_20kg);
                mList_20kg.setAdapter(oldrfidAdapter_20kg);

                int p14 = mCurrentOrder.s14kg.size();
                p14no.setText(String.valueOf(p14));
                int p20 = mCurrentOrder.s20kg.size();
                p20no.setText(String.valueOf(p20));
            }

            mSubmitTask = new WebAPITask(new AsyncResponse() {
                @Override
                public void processFinish(String output) {

                }
            });
            if (mLogin) {
                //mAuthTask.execute("http://192.168.11.110:8080/api/urgentorder");
            }

            return rootView;
        }
    }

    public static class NewContainerFragment extends Fragment {

        private WebAPITask mSubmitTask = null;
        private ListView mList_14kg;
        private ListView mList_20kg;

        public NewContainerFragment() {
        }

        public static NewContainerFragment newInstance() {
            NewContainerFragment fragment = new NewContainerFragment();

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_new_container, container, false);

            TextView p14no = (TextView)rootView.findViewById(R.id.new_14kg_no);
            TextView p20no = (TextView)rootView.findViewById(R.id.new_20kg_no);

            mState = NEW_CONTAINER;

            if (mCurrentOrder != null) {
                mList_14kg = (ListView) rootView.findViewById(R.id.new_conatiner_list_14kg_view);
                newrfidAdapter_14kg = new NewContainerAdapter(getActivity(), R.layout.new_container_list_item, newrfidArrayList_14kg);
                mList_14kg.setAdapter(newrfidAdapter_14kg);
                mList_20kg = (ListView) rootView.findViewById(R.id.new_conatiner_list_20kg_view);
                newrfidAdapter_20kg = new NewContainerAdapter(getActivity(), R.layout.new_container_list_item, newrfidArrayList_20kg);
                mList_20kg.setAdapter(newrfidAdapter_20kg);

                int p14 = mCurrentOrder.p14kg.size();
                p14no.setText(String.valueOf(p14));
                int p20 = mCurrentOrder.p20kg.size();
                p20no.setText(String.valueOf(p20));
            }

            mSubmitTask = new WebAPITask(new AsyncResponse() {
                @Override
                public void processFinish(String output) {

                }
            });
            if (mLogin) {
                //mAuthTask.execute("http://192.168.11.110:8080/api/urgentorder");
            }

            return rootView;
        }
    }

    public static class SummaryFragment extends Fragment {

        private WebAPITask mSubmitTask = null;

        public SummaryFragment() {
        }

        public static SummaryFragment newInstance() {
            SummaryFragment fragment = new SummaryFragment();

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_summary, container, false);

            TextView mS14rNo      = (TextView)rootView.findViewById(R.id.s14r_no);
            TextView mS14rBarCode = (TextView)rootView.findViewById(R.id.s14r_barcode);
            TextView mS14iNo      = (TextView)rootView.findViewById(R.id.s14i_no);
            TextView mS14iBarCode = (TextView)rootView.findViewById(R.id.s14i_barcode);
            TextView mS20rNo      = (TextView)rootView.findViewById(R.id.s20r_no);
            TextView mS20rBarCode = (TextView)rootView.findViewById(R.id.s20r_barcode);
            TextView mS20iNo      = (TextView)rootView.findViewById(R.id.s20i_no);
            TextView mS20iBarCode = (TextView)rootView.findViewById(R.id.s20i_barcode);

            TextView mA14iNo      = (TextView)rootView.findViewById(R.id.a14i_no);
            TextView mA14iBarCode = (TextView)rootView.findViewById(R.id.a14i_barcode);
            TextView mA20iNo      = (TextView)rootView.findViewById(R.id.a20i_no);
            TextView mA20iBarCode = (TextView)rootView.findViewById(R.id.a20i_barcode);

            TextView mD14rNo      = (TextView)rootView.findViewById(R.id.d14r_no);
            TextView mD14rBarCode = (TextView)rootView.findViewById(R.id.d14r_barcode);
            TextView mD20rNo      = (TextView)rootView.findViewById(R.id.d20r_no);
            TextView mD20rBarCode = (TextView)rootView.findViewById(R.id.d20r_barcode);

            mState = SUMMARY_VIEW;

            mOld14 = oldrfidArrayList_14kg.size();
            mOld20 = oldrfidArrayList_20kg.size();
            mNew14 = newrfidArrayList_14kg.size();
            mNew20 = newrfidArrayList_20kg.size();

            mS14rNo.setText(String.valueOf(mOld14));

            String barcodes = "";
            for (GasContainer co : oldrfidArrayList_14kg) {
                if (co.presented == 1) {
                    barcodes += co.barcode + " ";
                }
            }

            mS14rBarCode.setText(barcodes);

            mS14iNo.setText(String.valueOf(mNew14));
            barcodes = "";
            for (GasContainer co : newrfidArrayList_14kg) {
                if (co.presented == 1) {
                    barcodes += co.barcode + " ";
                }
            }
            mS14iBarCode.setText(barcodes);

            mS20rNo.setText(String.valueOf(mOld20));

            barcodes = "";
            for (GasContainer co : oldrfidArrayList_20kg) {
                if (co.presented == 1) {
                    barcodes += co.barcode + " ";
                }
            }

            mS20rBarCode.setText(barcodes);

            mS20iNo.setText(String.valueOf(mNew20));
            barcodes = "";
            for (GasContainer co : newrfidArrayList_20kg) {
                if (co.presented == 1) {
                    barcodes += co.barcode + " ";
                }
            }
            mS20iBarCode.setText(barcodes);

            if (mCurrentOrder != null) {
            }

            mSubmitTask = new WebAPITask(new AsyncResponse() {
                @Override
                public void processFinish(String output) {

                }
            });

            if (mLogin) {
                //mAuthTask.execute("http://192.168.11.110:8080/api/urgentorder");
            }

            return rootView;
        }
    }

    public static class SignFragment extends Fragment {

        private WebAPITask mSubmitTask = null;

        public SignFragment() {
        }

        public static SignFragment newInstance() {
            SignFragment fragment = new SignFragment();

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_sign, container, false);

            mState = SIGNATURE_VIEW;

            if (mCurrentOrder != null) {
                mSignaturePad = (SignaturePad) rootView.findViewById(R.id.signature_pad);
                mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {

                    @Override
                    public void onStartSigning() {
                        //Event triggered when the pad is touched
                    }

                    @Override
                    public void onSigned() {
                        //Event triggered when the pad is signed
                    }

                    @Override
                    public void onClear() {
                        //Event triggered when the pad is cleared
                    }
                });

            }

            mSubmitTask = new WebAPITask(new AsyncResponse() {
                @Override
                public void processFinish(String output) {

                }
            });
            if (mLogin) {
                //mAuthTask.execute("http://192.168.11.110:8080/api/urgentorder");
            }

            return rootView;
        }
    }


    public static class DoneFragment extends Fragment {

        private WebAPITask mSubmitTask = null;

        public DoneFragment() {
        }

        public static DoneFragment newInstance() {
            DoneFragment fragment = new DoneFragment();

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_done, container, false);

            mState = DONE_VIEW;

            if (mCurrentOrder != null) {
            }

            mSubmitTask = new WebAPITask(new AsyncResponse() {
                @Override
                public void processFinish(String output) {

                }
            });

            if (mLogin) {
                //mAuthTask.execute("http://192.168.11.110:8080/api/urgentorder");
                String url;
                url = HOST_SERVER + "/api/orderdone/" + mCurrentOrder.id + "?order=" + mCurrentOrder.id;
                //Log.v("qGas", url);
                mSubmitTask.execute(url);
            }

            return rootView;
        }
    }

    public static class CarContainerFragment extends Fragment {

        private WebAPITask mSubmitTask = null;
        private ListView mList_14kg;
        private ListView mList_20kg;

        public CarContainerFragment() {
        }

        public static CarContainerFragment newInstance() {
            CarContainerFragment fragment = new CarContainerFragment();

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_container, container, false);

            mState = CAR_VIEW;

            mList_14kg = (ListView) rootView.findViewById(R.id.car_container_list_view14);
            carrfidAdapter_14kg = new ContainerAdapter(getActivity(), R.layout.container_list_item, carrfidArrayList_14kg);
            mList_14kg.setAdapter(carrfidAdapter_14kg);
            mList_20kg = (ListView) rootView.findViewById(R.id.car_container_list_view20);
            carrfidAdapter_20kg = new ContainerAdapter(getActivity(), R.layout.container_list_item, carrfidArrayList_20kg);
            mList_20kg.setAdapter(carrfidAdapter_20kg);
            mStatus = (TextView) rootView.findViewById(R.id.status_text);

            mNumber14kg = (TextView) rootView.findViewById(R.id.number_14kg);
            mNumber20kg = (TextView) rootView.findViewById(R.id.number_20kg);

            mSubmitTask = new WebAPITask(new AsyncResponse() {
                @Override
                public void processFinish(String output) {

                }
            });
            if (mLogin) {
                //mAuthTask.execute("http://192.168.11.110:8080/api/urgentorder");
            }

            return rootView;
        }
    }

    public static class ContainerOutFragment extends Fragment {

        private WebAPITask mSubmitTask = null;
        private ListView mList_14kg;
        private ListView mList_20kg;

        public ContainerOutFragment() {
        }

        public static ContainerOutFragment newInstance() {
            ContainerOutFragment fragment = new ContainerOutFragment();

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_container_out, container, false);

            mState = OUT_VIEW;
            Log.d("qGas", "OUT_VIEW 1.");

            mList_14kg = (ListView) rootView.findViewById(R.id.out_container_list_view14);
            outAdapter_14kg = new ContainerAdapter(getActivity(), R.layout.container_list_item, outArrayList_14kg);
            mList_14kg.setAdapter(outAdapter_14kg);
            mList_20kg = (ListView) rootView.findViewById(R.id.out_container_list_view20);
            outAdapter_20kg = new ContainerAdapter(getActivity(), R.layout.container_list_item, outArrayList_20kg);
            mList_20kg.setAdapter(outAdapter_20kg);

            moutNumber14kg = (TextView) rootView.findViewById(R.id.number_14kg);
            moutNumber20kg = (TextView) rootView.findViewById(R.id.number_20kg);

            mSubmitTask = new WebAPITask(new AsyncResponse() {
                @Override
                public void processFinish(String output) {

                }
            });
            if (mLogin) {
                //mAuthTask.execute("http://192.168.11.110:8080/api/urgentorder");
            }

            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            mState = OUT_VIEW;
            Log.d("qGas", "OUT_VIEW 2.");
        }

        @Override
        public void onPause() {
            super.onPause();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        @Override
        public void onLowMemory() {
            super.onLowMemory();
        }

        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);

            if (isVisibleToUser) {
                mState = OUT_VIEW;
                Log.d("qGas", "OUT_VIEW 3.");
            } else {
                Log.d("qGas", "Fragment is not visible OUT_VIEW.");
            }
        }

    }

    public static class ContainerFillinFragment extends Fragment {

        private WebAPITask mSubmitTask = null;
        private ListView mList_14kg;
        private ListView mList_20kg;

        public ContainerFillinFragment() {
        }

        public static ContainerFillinFragment newInstance() {
            ContainerFillinFragment fragment = new ContainerFillinFragment();

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_container_fillin, container, false);

            mState = FILLIN_VIEW;
            Log.d("qGas", "FILLIN_VIEW 1.");

            mList_14kg = (ListView) rootView.findViewById(R.id.fillin_container_list_view14);
            inAdapter_14kg = new ContainerAdapter(getActivity(), R.layout.container_list_item, inArrayList_14kg);
            mList_14kg.setAdapter(inAdapter_14kg);
            mList_20kg = (ListView) rootView.findViewById(R.id.fillin_container_list_view20);
            inAdapter_20kg = new ContainerAdapter(getActivity(), R.layout.container_list_item, inArrayList_20kg);
            mList_20kg.setAdapter(inAdapter_20kg);

            minNumber14kg = (TextView) rootView.findViewById(R.id.number_14kg);
            minNumber20kg = (TextView) rootView.findViewById(R.id.number_20kg);

            mSubmitTask = new WebAPITask(new AsyncResponse() {
                @Override
                public void processFinish(String output) {

                }
            });
            if (mLogin) {
                //mAuthTask.execute("http://192.168.11.110:8080/api/urgentorder");
            }

            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            mState = FILLIN_VIEW;
            Log.d("qGas", "FILLIN_VIEW 2.");
        }

        @Override
        public void onPause() {
            super.onPause();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        @Override
        public void onLowMemory() {
            super.onLowMemory();
        }

        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);

            if (isVisibleToUser) {
                mState = FILLIN_VIEW;
                Log.d("qGas", "FILLIN_VIEW 3.");
            } else {
                Log.d("qGas", "Fragment is not visible FILLIN_VIEW.");
            }
        }

    }

    public static class SearchOrderFragment extends Fragment {

        public SearchOrderFragment() {
        }

        public static SearchOrderFragment newInstance() {
            SearchOrderFragment fragment = new SearchOrderFragment();

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_order_search, container, false);

            mState = SEARCH_VIEW;

            mSearchRfidInput = (EditText) rootView.findViewById(R.id.search_rfid_input);
            mSearchBarcodeInput  = (EditText) rootView.findViewById(R.id.search_barcode_input);

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return PlaceholderFragment.newInstance();
                case 1:
                    return AllOrderFragment.newInstance();
                case 2:
                    return CarContainerFragment.newInstance();
                case 3:
                    return SearchOrderFragment.newInstance();
                case 4:
                    return ContainerOutFragment.newInstance();
                case 5:
                    return ContainerFillinFragment.newInstance();
             }
            return PlaceholderFragment.newInstance();
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 6;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "司機設定";
                case 1:
                    return "訂單排程";
                case 2:
                    return "車載瓦斯";
                case 3:
                    return "尋找訂單";
                case 4:
                    return "載出灌氣";
                case 5:
                    return "灌氣載回";             }
            return null;
        }
    }

    static class WebAPITask extends AsyncTask<String, Void, String> {

        private Exception exception;

        private CloseableHttpClient httpclient;
        private HttpClientContext context;
        private HttpGet httpget;
        private CloseableHttpResponse response;

        private String server_response;
        public AsyncResponse delegate = null;

        WebAPITask() {
        }

        WebAPITask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        public void SetResponse(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        protected String doInBackground(String... urls) {
            try {
                httpclient = HttpClients.createDefault();
                context = HttpClientContext.create();
                httpget = new HttpGet(urls[0]);

                // Create a custom response handler
                ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
                    @Override
                    public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
                        int status = response.getStatusLine().getStatusCode();
                        if (status >= 200 && status < 300) {
                            HttpEntity entity = response.getEntity();
                            return entity != null ? EntityUtils.toString(entity) : null;
                        } else {
                            throw new ClientProtocolException("Unexpected response status: " + status);
                        }
                    }
                };

                if (mCookieStore != null) {
                    context.setCookieStore(mCookieStore);
                }
                String responseBody = httpclient.execute(httpget, responseHandler, context);
                try {
                    mCookieStore = context.getCookieStore();
                    List<Cookie> cookies = mCookieStore.getCookies();
                    for (int i = 0, len = cookies.size(); i < len; i++) {
                        if ("PHPSESSID".equals(cookies.get(i).getName())) {
                            String phpsessid = cookies.get(i).getValue();
                        }
                    }
                } finally {
                }
                return responseBody;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                this.exception = e;
                return null;
            } finally {
            }
             return null;
        }

        protected void onPostExecute(String result) {
            // TODO: check this.exception
            // TODO: do something with the feed
            delegate.processFinish(result);
        }
    }

    public void LoginOKClick(View view) {

        EditText mAccountField;
        EditText mPasswordField;

        WebAPITask mAuthTask = null;

        mAccountField  = (EditText) findViewById(R.id.account_input);
        mPasswordField = (EditText) findViewById(R.id.password_input);

        mAccount = mAccountField.getText().toString();
        mPassword = mPasswordField.getText().toString();

        mAuthTask = new WebAPITask(mLoginResponse);

        String url;
        url = HOST_SERVER + "/api/login/1?account=" + mAccount + "&password=" + mPassword;
        //Log.v("qGas", url);
        mAuthTask.execute(url);
        mMessage.setText("登入中...");
    }

    public void ProcessOrderClick(View view) {

        int total_old = 0;

        total_old = mCurrentOrder.s14kg.size() + mCurrentOrder.s20kg.size();

        FragmentTransaction trans = mActivity.getSupportFragmentManager().beginTransaction();
        Fragment fragment;

        if (total_old > 0) {
            fragment = new OldContainerFragment();

        } else {
            fragment = new NewContainerFragment();
        }
        trans.replace(R.id.single_order_fragment, fragment);
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        trans.addToBackStack(null);

        trans.commit();

    }

    public void ProcessNewClick(View view) {

        FragmentTransaction trans = mActivity.getSupportFragmentManager().beginTransaction();
        Fragment fragment = new NewContainerFragment();
        trans.replace(R.id.old_container_fragment, fragment);
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        trans.addToBackStack(null);

        trans.commit();
    }

    public void ProcessNewClean14Click(View view) {
        newrfidArrayList_14kg.clear();
        mHandler.sendEmptyMessage(NEWCMSG);
    }

    public void ProcessNewClean20Click(View view) {
        newrfidArrayList_20kg.clear();
        mHandler.sendEmptyMessage(NEWCMSG);
    }

    public void ProcessSumClick(View view) {

        FragmentTransaction trans = mActivity.getSupportFragmentManager().beginTransaction();
        Fragment fragment = new SummaryFragment();
        trans.replace(R.id.new_container_fragment, fragment);
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        trans.addToBackStack(null);

        trans.commit();
    }

    public void ProcessSigClick(View view) {

        FragmentTransaction trans = mActivity.getSupportFragmentManager().beginTransaction();
        Fragment fragment = new SignFragment();
        trans.replace(R.id.summary_fragment, fragment);
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        trans.addToBackStack(null);

        trans.commit();
    }

    public void SignCleanClick(View view) {
        mSignaturePad.clear();
    }

    public void SignOKClick(View view) {

        for (GasContainer co : oldrfidArrayList_14kg) {
            if (co.presented == 1) {
                WebAPITask mModTask = new WebAPITask(mNullResponse);

                String url;
                url = HOST_SERVER + "/api/oldcontainer/" + mCurrentOrder.id + "?rfid=" + co.rfid + "&gas=" + co.gas;
                //Log.v("qGas", url);
                mModTask.execute(url);
            }
        }
        for (GasContainer co : oldrfidArrayList_20kg) {
            if (co.presented == 1) {
                WebAPITask mModTask = new WebAPITask(mNullResponse);

                String url;
                url = HOST_SERVER + "/api/oldcontainer/" + mCurrentOrder.id + "?rfid=" + co.rfid + "&gas=" + co.gas;
                //Log.v("qGas", url);
                mModTask.execute(url);
            }
        }
        for (GasContainer co : newrfidArrayList_14kg) {
            if (co.presented == 1) {
                WebAPITask mModTask = new WebAPITask(mNullResponse);

                String url;
                url = HOST_SERVER + "/api/newcontainer/" + mCurrentOrder.id + "?rfid=" + co.rfid;
                //Log.v("qGas", url);
                mModTask.execute(url);
            }
        }
        for (GasContainer co : newrfidArrayList_20kg) {
            if (co.presented == 1) {
                WebAPITask mModTask = new WebAPITask(mNullResponse);

                String url;
                url = HOST_SERVER + "/api/newcontainer/" + mCurrentOrder.id + "?rfid=" + co.rfid;
                //Log.v("qGas", url);
                mModTask.execute(url);
            }
        }

        FragmentTransaction trans = mActivity.getSupportFragmentManager().beginTransaction();
        Fragment fragment = new DoneFragment();
        trans.replace(R.id.sign_fragment, fragment);
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        trans.addToBackStack(null);

        trans.commit();

    }

    public void DoneOKClick(View view) {

        FragmentTransaction trans = mActivity.getSupportFragmentManager().beginTransaction();
        Fragment fragment = new AllOrderFragment();
        trans.replace(R.id.done_fragment, fragment);
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        trans.addToBackStack(null);

        trans.commit();

    }

    public ArrayList<GasOrder> parseContainer(String jsonStr) {

        ArrayList<GasOrder> mContainer;
        Float mGpsX = 0.0f;
        Float mGpsY = 0.0f;

        if (jsonStr.isEmpty() || jsonStr.length() == 0) {
            return null;
        }
        mContainer = new ArrayList<GasOrder>();

        try {
            JSONArray json = new JSONArray(jsonStr);

            if (json.length() > 0) {
                for (int i = 0; i < json.length(); i++) {
                    JSONObject e = json.getJSONObject(i);
                    JSONObject s = new JSONObject(e.getString("gas_order"));

                    int kg14 = 0;
                    int kg20 = 0;

                    if (s.has("w14kg")) {
                        kg14 = s.getInt("w14kg");
                    } else {
                        kg14 = 0;
                    }
                    if (s.has("w20kg")) {
                        kg20 = s.getInt("w20kg");
                    } else {
                        kg20 = 0;
                    }

                    GasOrder tmp = new GasOrder();

                    tmp.id = e.getInt("id");

                    tmp.name = e.getString("name");

                    if (e.has("address")) {
                        tmp.address = e.getString("address");
                    } else {
                        tmp.address = "";
                    }
                    tmp.price = e.getString("cost");
                    tmp.start_time = e.getString("start_time");
                    tmp.end_time = e.getString("end_time");
                    tmp.order_time = e.getString("order_time");
                    tmp.kg14 = kg14;
                    tmp.kg20 = kg20;
                    tmp.gpsx = Float.valueOf(e.getString("gpsx"));
                    tmp.gpsy = Float.valueOf(e.getString("gpsy"));
                    if (mGpsX == 0.0f) {
                        mGpsX = tmp.gpsx;
                    }
                    if (mGpsY == 0.0f) {
                        mGpsY = tmp.gpsy;
                    }
                    if (e.has("family")) {
                        tmp.family = e.getInt("family");
                    } else {
                        tmp.family = 0;
                    }
                    if (e.has("remain")) {
                        tmp.remain = e.getInt("remain");
                    } else {
                        tmp.remain = 0;
                    }

                    tmp.p20kg = new ArrayList<GasContainer>();

                    if (e.has("p20kg")) {

                        JSONArray jsonarray = e.getJSONArray("p20kg");

                        for (int j = 0; j < jsonarray.length(); j++) {
                            JSONObject je = jsonarray.getJSONObject(j);
                            GasContainer dummy = new GasContainer();

                            dummy.id = je.getInt("id");
                            dummy.gas = 0;
                            tmp.p20kg.add(dummy);
                        }
                    }

                    tmp.p14kg = new ArrayList<GasContainer>();

                    if (e.has("p14kg")) {
                        JSONArray jsonarray = e.getJSONArray("p14kg");

                        for (int j = 0; j < jsonarray.length(); j++) {
                            JSONObject je = jsonarray.getJSONObject(j);
                            GasContainer dummy = new GasContainer();

                            dummy.id = je.getInt("id");
                            dummy.gas = 0;
                            tmp.p14kg.add(dummy);
                        }
                    }

                    tmp.s20kg = new ArrayList<GasContainer>();

                    if (e.has("s20kg")) {
                        JSONArray jsonarray = e.getJSONArray("s20kg");

                        for (int j = 0; j < jsonarray.length(); j++) {
                            JSONObject je = jsonarray.getJSONObject(j);
                            GasContainer dummy = new GasContainer();
                            dummy.id = je.getInt("id");
                            dummy.gas = 0;
                            dummy.rfid = je.getString("rfid");
                            dummy.barcode = je.getString("barcode");
                            tmp.s20kg.add(dummy);
                        }
                    }

                    tmp.s14kg = new ArrayList<GasContainer>();

                    if (e.has("s14kg")) {
                        JSONArray jsonarray = e.getJSONArray("s14kg");

                        for (int j = 0; j < jsonarray.length(); j++) {
                            JSONObject je = jsonarray.getJSONObject(j);
                            GasContainer dummy = new GasContainer();
                            dummy.id = je.getInt("id");
                            dummy.gas = 0;
                            dummy.rfid = je.getString("rfid");
                            dummy.barcode = je.getString("barcode");
                            tmp.s14kg.add(dummy);
                        }
                    }

                    mContainer.add(tmp);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.v("qGas", "json error");
            return null;
        }

        return mContainer;
    }

    public void SearchRfidClick(View view) {

        WebAPITask mSearchTask = null;
        AsyncResponse mSearchResponse;

        mSearchResponse = new AsyncResponse() {
            @Override
            public void processFinish(String output) {

                if (output == null || output.length() == 0 || output.compareTo("[]") == 0 || output.compareTo("NG") == 0) {
                    return;
                }

                mSearchItems = parseContainer(output);

                GasOrder item = mSearchItems.get(0);
                mCurrentOrder = item;

                FragmentTransaction trans = mActivity.getSupportFragmentManager().beginTransaction();
                Fragment fragment = new SingleOrderFragment();

                trans.replace(R.id.search_order_fragment, fragment);
                trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                trans.addToBackStack(null);

                trans.commit();
            }
        };

        mSearchTask = new WebAPITask(mSearchResponse);

        String mRfid;
        mRfid = mSearchRfidInput.getText().toString();

        String url;
        url = HOST_SERVER + "/api/findorder/" + mRfid;
        //Log.v("qGas", url);
        mSearchTask.execute(url);

    }

    public void SearchBarcodeClick(View view) {

        WebAPITask mSearchTask = null;
        AsyncResponse mSearchResponse;

        mSearchResponse = new AsyncResponse() {
            @Override
            public void processFinish(String output) {

                if (output == null || output.length() == 0 || output.compareTo("[]") == 0  || output.compareTo("NG") == 0) {
                    return;
                }

                mSearchItems = parseContainer(output);

                GasOrder item = mSearchItems.get(0);
                mCurrentOrder = item;

                FragmentTransaction trans = mActivity.getSupportFragmentManager().beginTransaction();
                Fragment fragment = new SingleOrderFragment();

                trans.replace(R.id.search_order_fragment, fragment);
                trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                trans.addToBackStack(null);

                trans.commit();
            }
        };

        mSearchTask = new WebAPITask(mSearchResponse);

        String mBarcode;
        mBarcode = mSearchBarcodeInput.getText().toString();

        String url;
        url = HOST_SERVER + "/api/findorder/0?barcode=" + mBarcode;
        //Log.v("qGas", url);
        mSearchTask.execute(url);
    }

    public void ProcessCarClean14Click(View view) {
        carrfidArrayList_14kg.clear();
        mHandler.sendEmptyMessage(CARCMSG);
    }

    public void ProcessCarClean20Click(View view) {
        carrfidArrayList_20kg.clear();
        mHandler.sendEmptyMessage(CARCMSG);
    }

    public void ProcessOutClean14Click(View view) {
        outArrayList_14kg.clear();
        mHandler.sendEmptyMessage(OUTMSG);
    }

    public void ProcessOutClean20Click(View view) {
        outArrayList_20kg.clear();
        mHandler.sendEmptyMessage(OUTMSG);
    }

    public void ProcessFillinClean14Click(View view) {
        inArrayList_14kg.clear();
        mHandler.sendEmptyMessage(FILLINMSG);
    }

    public void ProcessFillinClean20Click(View view) {
        inArrayList_20kg.clear();
        mHandler.sendEmptyMessage(FILLINMSG);
    }

    public void ProcessOutClick(View view) {

        JSONArray outJsonArray;
        ArrayList<String> outArrayList;
        WebAPITask mOutTask = null;
        AsyncResponse mOutResponse;

        outArrayList = new ArrayList<String>();

        for (GasContainer co : outArrayList_14kg) {
            outArrayList.add(co.rfid);
        }

        for (GasContainer co : outArrayList_20kg) {
             outArrayList.add(co.rfid);
        }

        mOutResponse = new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (output != null) {
                    Log.v("qGas", output);
                }
                outArrayList_14kg.clear();
                outArrayList_20kg.clear();
                mHandler.sendEmptyMessage(OUTMSG);
            }
        };

        mOutTask = new WebAPITask(mOutResponse);

        outJsonArray = new JSONArray(outArrayList);

        String all = "";
        try {
            all = URLEncoder.encode(outJsonArray.toString(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url;
        //url = HOST_SERVER + "/api/containerout?rfids=" +  all;
        url = HOST_SERVER + "/api/setallcontainers/1?rfids=" +  all; // 送出灌氣
        Log.v("qGas", url);
        mOutTask.execute(url);
    }

    public void ProcessFillinClick(View view) {

        JSONArray inJsonArray;
        ArrayList<String> inArrayList;
        WebAPITask mOutTask = null;
        AsyncResponse mOutResponse;

        inArrayList = new ArrayList<String>();

        for (GasContainer co : inArrayList_14kg) {
            inArrayList.add(co.rfid);
        }

        for (GasContainer co : inArrayList_20kg) {
            inArrayList.add(co.rfid);
        }

        mOutResponse = new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                inArrayList_14kg.clear();
                inArrayList_20kg.clear();
                mHandler.sendEmptyMessage(FILLINMSG);
            }
        };

        mOutTask = new WebAPITask(mOutResponse);
        inJsonArray = new JSONArray(inArrayList);

        String all = "";
        try {
            all = URLEncoder.encode(inJsonArray.toString(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url;
        url = HOST_SERVER + "/api/setallcontainers/2?rfids=" +  all; // 灌氣完成
        Log.v("qGas", url);
        mOutTask.execute(url);
    }

    public void ProcessUpClick(View view) {

        mSubState = UP_STATE;
        mHandler.sendEmptyMessage(STATUSMSG);

        JSONArray upJsonArray;
        ArrayList<String> upArrayList;
        WebAPITask mUpTask = null;
        AsyncResponse mUpResponse;

        upArrayList = new ArrayList<String>();

        for (GasContainer co : carrfidArrayList_14kg) {
            upArrayList.add(co.rfid);
        }

        for (GasContainer co : carrfidArrayList_20kg) {
            upArrayList.add(co.rfid);
        }

        mUpResponse = new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                //carrfidArrayList_14kg.clear();
                //carrfidArrayList_20kg.clear();
                mHandler.sendEmptyMessage(CARCMSG);
            }
        };

        mUpTask = new WebAPITask(mUpResponse);
        upJsonArray = new JSONArray(upArrayList);

        String all = "";
        try {
            all = URLEncoder.encode(upJsonArray.toString(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url;
        url = HOST_SERVER + "/api/setallcontainers/8?rfids=" +  all; // 灌氣完成
        Log.v("qGas", url);
        mUpTask.execute(url);

    }

    public void ProcessDownClick(View view) {

        mSubState = DOWN_STATE;
        mHandler.sendEmptyMessage(STATUSMSG);

        JSONArray downJsonArray;
        ArrayList<String> downArrayList;
        WebAPITask mDownTask = null;
        AsyncResponse mDownResponse;

        downArrayList = new ArrayList<String>();

        for (GasContainer co : carrfidArrayList_14kg) {
            downArrayList.add(co.rfid);
        }

        for (GasContainer co : carrfidArrayList_20kg) {
            downArrayList.add(co.rfid);
        }

        mDownResponse = new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                carrfidArrayList_14kg.clear();
                carrfidArrayList_20kg.clear();
                mHandler.sendEmptyMessage(CARCMSG);
            }
        };

        mDownTask = new WebAPITask(mDownResponse);
        downJsonArray = new JSONArray(downArrayList);

        String all = "";
        try {
            all = URLEncoder.encode(downJsonArray.toString(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url;
        url = HOST_SERVER + "/api/setallcontainers/9?rfids=" +  all; // 灌氣完成
        Log.v("qGas", url);
        mDownTask.execute(url);

    }


}
