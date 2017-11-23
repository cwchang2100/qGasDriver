package tw.com.qgas.www.qgasdriver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by cwchang on 2017/6/14.
 */

public class GasOrder implements Comparable<GasOrder> {
    public int id;
    public String name;
    public String address;
    public String start_time;
    public String end_time;
    public String order_time;
    public String price;
    public String area;
    public Float gpsx;
    public Float gpsy;
    public int kg14;
    public int kg20;
    public int route_id;
    public int station_id;
    public int family;
    public int remain;
    public int type;
    public boolean hasTitle;

    public ArrayList<GasContainer> p20kg; // prepared
    public ArrayList<GasContainer> p14kg; // prepared
    public ArrayList<GasContainer> s20kg; // prepared
    public ArrayList<GasContainer> s14kg; // prepared

    public static final int NORMAL_ORDER = 1;
    public static final int TITLE_ORDER = 100;

    GasOrder() {
    }

    @Override
    public int compareTo(GasOrder o) {
        return (start_time.compareTo(o.start_time));
    }
}
