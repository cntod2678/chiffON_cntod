package media.around.clothes;

/**
 * Created by Dongjin on 2016. 3. 6..
 */
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.view.ViewPager;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.json.JSONArray;
import java.util.HashMap;

import media.around.Around;
import media.around.R;
import media.around.network.JSONParser;

public class ClothesActivity extends AppCompatActivity {
    public static String type;
    private static final String CLOTHES_URL = Around.url + "/Cloth/cloth_type.jsp";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    JSONArray jsonArray = new JSONArray();
    String beaconId;

    ImageButton btn_next, btn_pre;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null) {
            setContentView(R.layout.activity_clothes);
        } else {
            finish();
            return;
        }

        Intent intent = getIntent();

        type = intent.getStringExtra("type");
        beaconId = intent.getStringExtra("beacon_id");

        getSupportActionBar().setTitle(type);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();

        try {
            jsonArray = new GetCloth().execute(beaconId, type).get();
            ClothesAdapter adapter = new ClothesAdapter(this, jsonArray);
            viewPager.setAdapter(adapter);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.closet, menu);
        return true;
    }

    // 그리드 버튼 눌렀을 시
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
            return true;
        }

        if (id == R.id.action_button_grid) {
            Intent intent = new Intent(getApplicationContext(), ClothesGridActivity.class);
            intent.putExtra("jsonArray", jsonArray.toString());
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        btn_next = (ImageButton)findViewById(R.id.btn_next);
        btn_pre = (ImageButton)findViewById(R.id.btn_pre);
        viewPager = (ViewPager)findViewById(R.id.pager);

        btn_next.setScaleType(ImageView.ScaleType.FIT_XY);
        btn_pre.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    public void mOnClick(View v) {
        int pos;
        switch (v.getId()) {
            case R.id.btn_pre :
                pos = viewPager.getCurrentItem();
                viewPager.setCurrentItem(pos - 1, true );
                break;

            case R.id.btn_next :
                pos = viewPager.getCurrentItem();
                viewPager.setCurrentItem(pos + 1, true);
                break;
        }
    }


    class GetCloth extends AsyncTask<String, String, JSONArray> {
        JSONParser jsonParser = new JSONParser();

        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        public JSONArray doInBackground(String... args) {

            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("beacon_id", args[0]);
                params.put("cloth_big_type", args[1]);

                JSONArray jArr = jsonParser.makeHttpRequest(
                        CLOTHES_URL, "GET", params);

                if (jArr != null) {
                    return jArr;
                } else {
                    Log.d("JSON result", "ClothesActivity_JSON IS NULL");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


    }

}
