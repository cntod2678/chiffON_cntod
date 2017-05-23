package media.around;

/**
 * Created by Dongjin on 2016. 2. 15..
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import media.around.join.LoginActivity;
import media.around.network.JSONParser;

public class SplashActivity extends AppCompatActivity {
    private static final String LOGIN_URL = Around.url + "/Member/member_login.jsp";

    Handler handler = new Handler();
    Intent intent;

    String member_id, member_password;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = getSharedPreferences("chiffOn", Activity.MODE_PRIVATE);
        member_id = pref.getString("member_id", "chiffOn");
        member_password = pref.getString("member_id", "chiffOn");

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                overridePendingTransition(R.animator.fadein, R.animator.fadeout);

                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
//
//                try {
//                    String result = new Login().execute(member_id, member_password).get();
//
//                    if(result.equals("0") || result.equals("2"))
//                        intent = new Intent(getApplicationContext(), LoginActivity.class);
//                    else
//                        intent = new Intent(getApplicationContext(), MainActivity.class);
//
//                    startActivity(intent);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

            }
        }, 2000);
    }

    public void onBackPressed(){}

    class Login extends AsyncTask<String, String, String> {

        JSONParser jsonParser = new JSONParser();

        @Override
        protected String doInBackground(String... args) {

            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("member_id", args[0]);
                Log.i("check_mem", args[0]);
                params.put("member_password", args[1]);
                Log.i("check_mem", args[1]);

                Log.d("request", "starting");

                JSONArray jArr = jsonParser.makeHttpRequest(
                        LOGIN_URL, "POST", params);

                JSONObject json = jArr.getJSONObject(0);

                if (json != null) {
                    Log.d("JSON result", json.toString());

                    //[{"login_result":"숫자"}]
                    String result = json.getString("login_result");

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

}