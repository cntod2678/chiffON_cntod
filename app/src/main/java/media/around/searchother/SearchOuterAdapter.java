package media.around.searchother;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import media.around.R;

public class SearchOuterAdapter extends BaseAdapter {
    JSONArray jsonArray = null;
    Context mContext;
    LayoutInflater mLayoutInflater;

    private List<String> user_id = new ArrayList<>();
    private List<String> register_cnt = new ArrayList<>();
    private List<String> beacon_id = new ArrayList<>();

    protected SearchOuterAdapter(Context context, JSONArray jArr) {
        mContext = context;
        jsonArray = jArr;
        mLayoutInflater = LayoutInflater.from(mContext);

        getData(jsonArray);
    }

    private void getData(JSONArray jArr) {
        for(int i = 0; i < jArr.length(); i++) {
            try {
                JSONObject json = jArr.getJSONObject(i);

                user_id.add(json.getString("member_id"));
                register_cnt.add(json.getString("member_cloth_count"));
                beacon_id.add(json.getString("beacon_id"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getCount() {
        return jsonArray.length();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    protected class ViewHolder {
        TextView member_id;
        TextView regiseter_count;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.item_search_other, null);

            viewHolder.member_id = (TextView) convertView.findViewById(R.id.txt_id);
            viewHolder.regiseter_count = (TextView) convertView.findViewById(R.id.txt_count);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.member_id.setText(user_id.get(position));
        viewHolder.regiseter_count.setText("등록한 옷 개수 : " + register_cnt.get(position));

        return convertView;
    }


    protected void setSource(JSONArray jArr) {
        user_id.clear();
        register_cnt.clear();
        beacon_id.clear();

        jsonArray = jArr;
        getData(jsonArray);

        notifyDataSetChanged();
    }

}
