package media.around.clothes;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import media.around.Around;
import media.around.R;

/**
 * Created by Dongjin on 2016. 3. 6..
 */
public class ClothesGridAdapter extends ArrayAdapter {

    private static final String IMAGE_URL = Around.url + "/Image/";

    Context mContext;
    LayoutInflater mLayoutInflater;

    public List<String> img = new ArrayList<>();

    public ClothesGridAdapter(Context context, List<String> imgPath) {
        super(context, R.layout.item_clothes_grid, imgPath);
        this.mContext = context;
        this.img = imgPath;

        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (null == convertView) {
            convertView = mLayoutInflater.inflate(R.layout.item_clothes_grid, parent, false);
        }

        Glide
                .with(mContext)
                .load(IMAGE_URL + img.get(position) + ".jpg")
                .override(200, 200)
                .fitCenter()
                .centerCrop()
                .thumbnail(0.1f)
                .into((ImageView) convertView);

        return convertView;
    }
}
