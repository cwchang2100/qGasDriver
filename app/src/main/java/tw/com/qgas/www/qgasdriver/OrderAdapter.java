package tw.com.qgas.www.qgasdriver;

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

public class OrderAdapter extends ArrayAdapter<GasOrder> {

    ArrayList<GasOrder> orderList = new ArrayList<GasOrder>();

    TextView SeparateView;
    TextView orderView;
    TextView secondView;
    String Orderstr, SecondStr;
    GasOrder order;

    TextView sectionView;
    String Titlestr;

    public OrderAdapter(Context context, int textViewResourceId, ArrayList<GasOrder> objects) {
        super(context, textViewResourceId, objects);
        orderList = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        order = orderList.get(position);

        v = inflater.inflate(R.layout.list_item, null);
        SeparateView= (TextView) v.findViewById(R.id.separator);
        if (order.hasTitle) {
            SeparateView.setText(order.start_time);
            SeparateView.setVisibility(View.VISIBLE);
        } else {
            SeparateView.setVisibility(View.GONE);
        }

        Orderstr = order.name;
        SecondStr = order.address;
        orderView = (TextView) v.findViewById(R.id.order_title);
        orderView.setText(Orderstr);
        secondView = (TextView) v.findViewById(R.id.second_title);
        secondView.setText(SecondStr);

        return v;
    }

}
