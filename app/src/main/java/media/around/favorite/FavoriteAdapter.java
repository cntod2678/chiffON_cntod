package media.around.favorite;

import android.content.Context;
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

public class FavoriteAdapter extends BaseAdapter {
    Context mContext;
    JSONArray jsonArray;
    LayoutInflater mLayoutInflater;

    private static final String IMAGE_URL = Around.url + "/Image/";

    List<String> cloth_img = new ArrayList<>();
    List<String> cloth_name = new ArrayList<>();
    List<String> cloth_brand = new ArrayList<>();
    List<String> cloth_big_type = new ArrayList<>();

    public FavoriteAdapter(Context context, JSONArray jArr) {
        mContext = context;
        jsonArray = jArr;
        mLayoutInflater = LayoutInflater.from(mContext);
        getData(jsonArray);
    }

    private void getData(JSONArray jArr) {
        try {
            for (int i = 0; i < jArr.length(); i++) {
                JSONObject json = jArr.getJSONObject(i);
                cloth_img.add(json.getString("cloth_number"));
                cloth_big_type.add(json.getString("cloth_big_type"));
                cloth_name.add(json.getString("cloth_name"));
                cloth_brand.add(json.getString("cloth_brand"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void setSource(JSONArray jArr) {
        cloth_img.clear();
        cloth_name.clear();
        cloth_brand.clear();
        cloth_big_type.clear();

        jsonArray = jArr;
        getData(jsonArray);

        notifyDataSetChanged();
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
        TextView txtName;
        TextView txtBigType;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.item_favorite_list, null);

            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.img_favorite);
            viewHolder.txtBrand = (TextView) convertView.findViewById(R.id.txt_favorite_brand);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txt_favorite_name);
            viewHolder.txtBigType = (TextView) convertView.findViewById(R.id.txt_favorite_big_type);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtBrand.setText(cloth_brand.get(position));
        viewHolder.txtBigType.setText(cloth_big_type.get(position));
        viewHolder.txtName.setText(cloth_name.get(position));

        Glide
                .with(mContext)
                .load(IMAGE_URL + cloth_img.get(position) + ".jpg")
//                .override(100, 100)
                .fitCenter()
                .error(R.drawable.main)
                .transform(new RoundImageTransform(mContext))
                .thumbnail(0.1f)
                .into(viewHolder.imageView);

        return convertView;
    }
}
