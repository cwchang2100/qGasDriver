package tw.com.qgas.www.qgasdriver;



import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by cwchang on 2017/6/5.
 */

public class ContainerAdapter extends ArrayAdapter<GasContainer> {

    ArrayList<GasContainer> containerList = new ArrayList<GasContainer>();

    TextView rfidView;
    TextView dummyView;
    TextView barcodeView;
    String RFIDstr, Barcode;
    GasContainer co;

    public ContainerAdapter(Context context, int textViewResourceId, ArrayList<GasContainer> objects) {
        super(context, textViewResourceId, objects);
        containerList = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.container_list_item, null);

        co = containerList.get(position);

        RFIDstr = co.rfid;
        Barcode = co.barcode;
        rfidView = (TextView) v.findViewById(R.id.conatinerRFID);
        rfidView.setText(RFIDstr);
        barcodeView = (TextView) v.findViewById(R.id.conatinerBar);
        barcodeView.setText(Barcode);

        return v;
    }

}
