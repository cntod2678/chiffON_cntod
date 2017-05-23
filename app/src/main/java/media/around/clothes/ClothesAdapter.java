package media.around.clothes;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import media.around.Around;
import media.around.R;

public class ClothesAdapter extends PagerAdapter {

    private static final String IMAGE_URL = Around.url + "/Image/";

    Around around;
    private Context mContext;
    LayoutInflater mLayoutInflater;
    JSONArray jsonArray = new JSONArray();
    private String beaconId;

    private List<String> clothes_img = new ArrayList<>();
    private List<String> clothes_brand = new ArrayList<>();
    private List<String> clothes_type = new ArrayList<>();
    private List<String> clothes_name = new ArrayList<>();
    private List<String> clothes_price = new ArrayList<>();

    public ClothesAdapter(Context context) {
        mContext = context;
    }

    public ClothesAdapter(Context context, JSONArray jArr) {
        this(context);
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        jsonArray = jArr;
        getDataFromJson(jsonArray);

        around = (Around) mContext.getApplicationContext();
        beaconId = around.getMyBeaconId();
        Log.i("beaconId", beaconId + " : ClothesAdapter");
    }

    @Override
    public int getCount() {
        return jsonArray.length();
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //ViewPager에서 보이지 않는 View는 제거
        container.removeView((View)object);
    }

    //instantiateItem() 메소드에서 리턴된 Ojbect가 View가  맞는지 확인하는 메소드
    @Override
    public boolean isViewFromObject(View v, Object obj) {
        return v==obj;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view=null;

        //새로운 View 객체를 Layoutinflater를 이용해서 생성
        view = mLayoutInflater.inflate(R.layout.viewpager_clothes_child, null);

        //만들어진 View안에 있는 ImageView 객체 참조
        ImageView img= (ImageView)view.findViewById(R.id.img_viewpager_childimage);
        TextView textBrand = (TextView)view.findViewById(R.id.txt_Clothes_Brand);
        TextView textType = (TextView)view.findViewById(R.id.txt_Clothes_Type);
        TextView textPrice = (TextView)view.findViewById(R.id.txt_Clothes_Price);
        TextView textName = (TextView)view.findViewById(R.id.txt_Clothes_Name);


        try {
            textBrand.setText(clothes_brand.get(position));
            textType.setText(clothes_type.get(position));
            textName.setText(clothes_name.get(position));
            textPrice.setText(clothes_price.get(position));
        } catch(Exception e){
            e.printStackTrace();
        }

        Glide
                .with(mContext)
                .load(IMAGE_URL + clothes_img.get(position) + ".jpg")
                .override(250, 250)
                .fitCenter()
                .centerCrop()
                .thumbnail(0.1f)
                .into(img);

        //ViewPager에 만들어 낸 View 추가
        container.addView(view);

        //Image가 세팅된 View를 리턴
        return view;
    }

    public void getDataFromJson(JSONArray jArr){
        try {
            for (int i = 0; i < jArr.length(); i++) {
                JSONObject json = jArr.getJSONObject(i);
                clothes_img.add(json.getString("cloth_number"));
                clothes_brand.add(json.getString("cloth_brand"));
                clothes_type.add(json.getString("cloth_small_type"));
                clothes_name.add(json.getString("cloth_name"));
                clothes_price.add(json.getString("cloth_price"));

                Log.i("getData",(clothes_img.get(i)));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
