package media.around.beacon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.perples.recosdk.RECOBeacon;

import java.util.ArrayList;
import java.util.Collection;

public class BeaconAdapter extends BaseAdapter {
    private static ArrayList<RECOBeacon> mBeacons;
    private LayoutInflater mLayoutInflater;

    public BeaconAdapter(Context context){
        super();
        mBeacons = new ArrayList<RECOBeacon>();
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void updateBeacon(RECOBeacon beacon) {
        synchronized (mBeacons) {
            if(mBeacons.contains(beacon)) {
                mBeacons.remove(beacon);
            }
            mBeacons.add(beacon);
        }
    }

    public static void updateAllBeacons(Collection<RECOBeacon> beacons) {
        synchronized (beacons) {
            mBeacons = new ArrayList<RECOBeacon>(beacons);
        }
    }



    public void clear() {
        mBeacons.clear();
    }

    @Override
    public int getCount() { return mBeacons.size(); }

    @Override
    public Object getItem(int position) { return mBeacons.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
