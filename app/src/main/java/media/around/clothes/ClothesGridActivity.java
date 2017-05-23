package media.around.clothes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import media.around.R;

public class ClothesGridActivity extends AppCompatActivity {
    GridView gridView;
    private ClothesGridAdapter gridAdapter;

    public List<String> clothes_img = new ArrayList<>();
    public List<String> clothes_brand = new ArrayList<>();
    public List<String> clothes_type = new ArrayList<>();
    public List<String> clothes_name = new ArrayList<>();
    public List<String> clothes_price = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null) {
            setContentView(R.layout.activity_clothes_grid);
        } else {
            finish();
            return;
        }

        gridView = (GridView) findViewById(R.id.clothes_grid);

        Intent intent = getIntent();
        String jsonArray = intent.getStringExtra("jsonArray");

        Log.d("jsonArray clothesGrid", jsonArray);

        if(jsonArray != null) {
            try {
                JSONArray jArr = new JSONArray(jsonArray);
                getDataFromJson(jArr);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            Log.d("Json", "null");

        gridAdapter = new ClothesGridAdapter(this, clothes_img);
        gridView.setAdapter(gridAdapter);
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
