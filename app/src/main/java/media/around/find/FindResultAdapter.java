package media.around.find;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import media.around.Around;
import media.around.R;
import media.around.RoundImageTransform;

public class FindResultAdapter extends BaseAdapter {
    private static final String IMAGE_URL = Around.url + "/Image/";

    Context mContext;
    LayoutInflater mLayoutInflater;
    JSONArray jsonArray;

    public List<String> img = new ArrayList<>();
    public List<String> brand = new ArrayList<>();
    public List<String> name = new ArrayList<>();

    public FindResultAdapter(Context context, JSONArray jArr) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        jsonArray = jArr;

        getData();
    }

    public void getData() {
        Log.i("getData", Integer.toString(jsonArray.length()));
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                img.add(json.getString("cloth_number"));
                brand.add(json.getString("cloth_brand"));
                name.add(json.getString("cloth_name"));

            }
        } catch(Exception e) {
            e.printStackTrace();
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


    static class ViewHolder {
        ImageView imageView;
        TextView txtBrand;
        TextView txtPrice;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.item_find_result, null);

            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.find_image);
            viewHolder.txtBrand = (TextView) convertView.findViewById(R.id.txtBrand);
            viewHolder.txtPrice = (TextView) convertView.findViewById(R.id.txtName);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtBrand.setText(brand.get(position));
        viewHolder.txtPrice.setText(name.get(position));

        Glide
                .with(mContext)
                .load(IMAGE_URL + img.get(position) + ".jpg")
                .error(R.drawable.main)
                .thumbnail(0.1f)
                .override(180, 180)
                .transform(new RoundImageTransform(mContext))
                .into(viewHolder.imageView);

        return convertView;
    }
}