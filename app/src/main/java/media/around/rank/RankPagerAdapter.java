package media.around.rank;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import media.around.Around;
import media.around.R;
import media.around.network.JSONParser;

public class RankPagerAdapter extends PagerAdapter {

    private static final String RANK_URL = Around.url + "/Products/product_show_count.jsp";
    private static final String IMAGE_URL = Around.url + "/Image/";

    LayoutInflater mLayoutInflater;
    Context mContext;

    RankListAdapter rankListAdapter;

    JSONArray typeResultJson;

    private String[] tabTitle = {" Outer ", "  T o p  ", "Bottom", "  A c c  ", " Shoes "};

    RankPagerAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public boolean isViewFromObject(View v, Object obj) {
        //instantiateItem() 메소드에서 리턴된 Ojbect가 View가  맞는지 확인하는 메소드
        return v==obj;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitle[position];
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //ViewPager에서 보이지 않는 View는 제거
        container.removeView((View)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view;
        view = mLayoutInflater.inflate(R.layout.item_rank, container, false);

        ListView listView = (ListView) view.findViewById(R.id.list_rank);

        // tab title 공백 제거
        String type;
        type = tabTitle[position].replaceAll("\\s", "");

        Log.i("99999", type);

        // 제거된 공백으로부터 서버 통신
        try {
            typeResultJson = new GetRankByType().execute(type).get();

            rankListAdapter = new RankListAdapter(typeResultJson);
            listView.setAdapter(rankListAdapter);

        } catch (Exception e) {
            Log.i("99999", "실패했졍");
            e.printStackTrace();
        }

        container.addView(view);

        return view;
    }


    class RankListAdapter extends BaseAdapter {
        private JSONArray jsonArray;

        List<String> cloth_number = new ArrayList<>();
        List<String> cloth_brand = new ArrayList<>();
        List<String> cloth_name = new ArrayList<>();
        List<String> cloth_count = new ArrayList<>();
        List<String> cloth_small_type = new ArrayList<>();

        RankListAdapter(JSONArray jArr) {
            jsonArray = jArr;
            getData();
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

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null) {
                viewHolder = new ViewHolder();
                convertView = mLayoutInflater.inflate(R.layout.item_rank_list, null);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.img_rank);
                viewHolder.txtBrand = (TextView) convertView.findViewById(R.id.txt_rank_brand);
                viewHolder.txtName = (TextView) convertView.findViewById(R.id.txt_rank_name);
                viewHolder.txtCount = (TextView) convertView.findViewById(R.id.txt_rank_count);
                viewHolder.txtSmallType = (TextView) convertView.findViewById(R.id.txt_rank_small_type);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.txtBrand.setText(cloth_brand.get(position));
            viewHolder.txtName.setText(cloth_name.get(position));
            viewHolder.txtCount.setText("찾은 횟수 : " + cloth_count.get(position));
            viewHolder.txtSmallType.setText(cloth_small_type.get(position));

            Glide
                    .with(mContext)
                    .load(IMAGE_URL + cloth_number.get(position) + ".jpg")
                    .error(R.drawable.main)
                    .thumbnail(0.1f)
                    .override(120, 120)

                    .into(viewHolder.imageView);


            return convertView;
        }

        protected class ViewHolder {
            ImageView imageView;
            TextView txtBrand;
            TextView txtName;
            TextView txtSmallType;
            TextView txtCount;
        }

        public void getData() {
            for(int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject json = jsonArray.getJSONObject(i);
                    cloth_number.add(json.getString("cloth_number"));
                    cloth_brand.add(json.getString("cloth_brand"));
                    cloth_count.add(json.getString("cloth_coi_count"));
                    cloth_name.add(json.getString("cloth_name"));
                    cloth_small_type.add(json.getString("cloth_small_type"));

                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }


    class GetRankByType extends AsyncTask<String, String, JSONArray> {
        JSONParser jsonParser = new JSONParser();
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(mContext);
            pDialog.setMessage("로딩 중...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        public JSONArray doInBackground(String... args) {

            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("cloth_big_type", args[0]);

                JSONArray jArr = jsonParser.makeHttpRequest(
                        RANK_URL, "GET", params);

                if (jArr != null) {
                    return jArr;

                } else
                    Log.d("JSON result", "JSON IS NULL");

            } catch (Exception e) {

                e.printStackTrace();
            }
            return null;
        }


        @Override
        public void onPostExecute(JSONArray jArr) {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }

    }


}
