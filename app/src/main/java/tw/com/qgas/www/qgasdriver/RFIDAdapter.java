package tw.com.qgas.www.qgasdriver;



import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by cwchang on 2017/6/5.
 */

public class RFIDAdapter extends ArrayAdapter<BluetoothDevice> {

    ArrayList<BluetoothDevice> rfidList = new ArrayList<>();

    public RFIDAdapter(Context context, int textViewResourceId, ArrayList<BluetoothDevice> objects) {
        super(context, textViewResourceId, objects);
        rfidList = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.rfid_list_item, null);
        TextView nameView = (TextView) v.findViewById(R.id.rfidName);
        TextView addressView = (TextView) v.findViewById(R.id.rfidAddress);
        nameView.setText(rfidList.get(position).getName());
        addressView.setText(rfidList.get(position).getAddress());
        return v;

    }

}
