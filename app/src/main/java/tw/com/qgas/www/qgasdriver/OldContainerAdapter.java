package tw.com.qgas.www.qgasdriver;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by cwchang on 2017/6/5.
 */

public class OldContainerAdapter extends ArrayAdapter<GasContainer> {

    ArrayList<GasContainer> containerList = new ArrayList<GasContainer>();

    TextView rfidView;
    CheckBox checked;
    EditText gasView;
    String RFIDstr, Barcode;
    GasContainer co;

    public OldContainerAdapter(Context context, int textViewResourceId, ArrayList<GasContainer> objects) {
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
        v = inflater.inflate(R.layout.old_container_list_item, null);

        co = containerList.get(position);
        RFIDstr = co.rfid;
        Barcode = co.barcode;
        rfidView = (TextView) v.findViewById(R.id.conatinerRFID);
        rfidView.setText(Barcode);

        checked = (CheckBox) v.findViewById(R.id.check);
        if (co.presented == 1) {
            checked.setChecked(true);
        } else {
            checked.setChecked(false);
        }

        checked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox c = (CheckBox)view;
                if (c.isChecked() == true) {
                    co.presented = 1;
                } else {
                    co.presented = 0;
                }
            }
        });

        gasView = (EditText) v.findViewById(R.id.gas_input);
        gasView.setText(String.valueOf(co.gas));

        gasView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String valuestr = gasView.getText().toString();
                    co.gas = Integer.valueOf(valuestr);
                }

            }

        });

        return v;
    }

}
