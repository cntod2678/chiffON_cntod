package media.around.find;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import media.around.Around;
import media.around.R;
import media.around.RoundImageTransform;
import media.around.network.JSONParser;

public class ImagePopupActivity extends Activity {
    private ImageView img_popup;
    private ImageButton btn_goShopping;
    private ToggleButton toggleButton_favorite;
    private TextView txtBrand, txtPrice, txtName, txtBigType;

    private boolean toggle_click = false;
    Around around;
    private String item, cloth_url, cloth_number, cloth_price, cloth_brand, cloth_name, beacon_id, cloth_big_type;

    JSONObject json;

    private static final String IMAGE_URL = Around.url + "/Image/";
    private static final String CHECK_FAVORITE_URL = Around.url + "/Coi/coi_confirm.jsp";
    private static final String MODIFY_URL = Around.url + "/Coi/coi_insert_delete.jsp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null) {
            setContentView(R.layout.activity_image_popup);
        } else {
            finish();
            return;
        }

        around = (Around) getApplication();
        beacon_id = around.getMyBeaconId();

        getItemFromIntent();
        initView();
        setView();
        eventClick();
    }

    private void getItemFromIntent() {
        Intent intent = getIntent();
        item = intent.getStringExtra("item");

        try {
            json = new JSONObject(item);

            cloth_url = json.getString("cloth_url");
            cloth_number = json.getString("cloth_number");
            cloth_name = json.getString("cloth_name");
            cloth_brand = json.getString("cloth_brand");
            cloth_price = json.getString("cloth_price");
            cloth_big_type = json.getString("cloth_big_type");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {

        img_popup = (ImageView) findViewById(R.id.img_popup);

        txtName = (TextView) findViewById(R.id.txtName);
        txtBrand = (TextView) findViewById(R.id.txtBrand);
        txtPrice = (TextView) findViewById(R.id.txtPrice);
        txtBigType = (TextView) findViewById(R.id.txtBigType);

        btn_goShopping = (ImageButton) findViewById(R.id.btn_goShopping);
        toggleButton_favorite = (ToggleButton) findViewById(R.id.toggleButton);

        String result = "hi";
        try {
            result = new CheckFavorite().execute(beacon_id, cloth_number).get();
            if (result.equals("1")) {
                toggleButton_favorite.setChecked(true);
                toggle_click = true;
            } else {
                toggleButton_favorite.setChecked(false);
                toggle_click = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setView() {
        txtName.setText(cloth_name);
        txtBrand.setText(cloth_brand);
        txtPrice.setText(cloth_price);
        txtBigType.setText(cloth_big_type);

        Glide
                .with(this)
                .load(IMAGE_URL + cloth_number + ".jpg")
                .thumbnail(0.1f)
                .override(500, 500)
                .fitCenter()
                .transform(new RoundImageTransform(this))
                .error(R.drawable.main)
                .into(img_popup);
    }


    private void eventClick() {
        // coi 에 insert / delete
        toggleButton_favorite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!toggle_click) {
                    Toast.makeText(getApplicationContext(), "즐겨찾기에 추가했습니다.",
                            Toast.LENGTH_LONG).show();
                    toggle_click = true;
                } else {
                    Toast.makeText(getApplicationContext(), "즐겨찾기를 해제했습니다.",
                            Toast.LENGTH_LONG).show();
                    toggle_click = false;
                }
                new ModifyCoi().execute(beacon_id, cloth_number);
            }
        });

        btn_goShopping.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                intent.putExtra("url", cloth_url);
                Log.d("url", cloth_url);
                startActivity(intent);
            }
        });
    }




    class CheckFavorite extends AsyncTask<String, String, String> {

        JSONParser jsonParser = new JSONParser();

        @Override
        protected String doInBackground(String... args) {

            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("beacon_id", args[0]);
                params.put("cloth_number", args[1]);

                Log.d("request", "starting");

                JSONArray jArr = jsonParser.makeHttpRequest(
                        CHECK_FAVORITE_URL, "POST", params);

                JSONObject json = jArr.getJSONObject(0);

                if (json != null) {
                    Log.d("JSON result", json.toString());

                    String result = json.getString("check_result");

                    //반환시에 post 부분과 맨처음 호출한 부분
                    return result;

                } else
                    Log.d("JSON result", "JSON IS NULL");

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

    }


    class ModifyCoi extends AsyncTask<String, String, Void> {
        JSONParser jsonParser = new JSONParser();

        @Override
        protected Void doInBackground(String... args) {
            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("beacon_id", args[0]);
                params.put("cloth_number", args[1]);

                Log.d("request", "starting");

                JSONArray jArr = jsonParser.makeHttpRequest(
                        MODIFY_URL, "POST", params);

                JSONObject json = jArr.getJSONObject(0);

                if (json != null) {
                    Log.d("JSON result", json.toString());

                } else
                    Log.d("JSON result", "JSON IS NULL");

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }


}