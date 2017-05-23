package media.around.join;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import media.around.Around;
import media.around.MainActivity;
import media.around.R;
import media.around.network.JSONParser;

public class LoginActivity extends AppCompatActivity {
    ImageButton btnLogin, btnRegister;
    EditText editUserName, editPassword;
    Around around;
    private String member_id, member_password;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private static final String LOGIN_URL = Around.url + "/Member/member_login.jsp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null) {
            setContentView(R.layout.activity_login);
        } else {
            finish();
            return;
        }


        initView();
        around = (Around)getApplication();
    }


    private void initView() {
        btnLogin = (ImageButton) findViewById(R.id.btnLogin);
        btnRegister = (ImageButton) findViewById(R.id.btnRegister);
        editUserName = (EditText) findViewById(R.id.editUserName);
        editPassword = (EditText) findViewById(R.id.editPassword);
        pref = getSharedPreferences("chiffOn", Activity.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void onButtonClicked(View v) {
        ImageButton btn = (ImageButton) v;
        if (btn.getId() == R.id.btnLogin) {
            member_id = String.valueOf(editUserName.getText());
            member_password = String.valueOf(editPassword.getText());

            try {
                // [{"login_result":"숫자"}] 에서 숫자 부분이 전달 받아 온다.
                // 1: 아이디와 비밀번호 일치(로그인 가능), 0:아이디와 비밀번호 불일치, 2: 등록된 아이디 없음
                String result = new LoginPost().execute(member_id, member_password).get();


                //new LoginPost().execute(member_id, member_password);
                if (result.equals("0")) {
                    //아이디와 비밀번호 불일치
                    Log.d("final_result", result + " / 아이디 비밀번호 불일치");

                    Toast.makeText(LoginActivity.this, "비밀번호가 일치하지 않습니다",
                            Toast.LENGTH_LONG).show();
                }  else if (result.equals("2")) {
                    //등록된 아이디 없음
                    Log.d("final_result", result + " / 등록된 아이디가 없음");

                    Toast.makeText(LoginActivity.this, "등록된 아이디가 없습니다.",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    around.setMyBeaconId(result);
                    around.setMyId(member_id);

                    editor.putString("member_id", member_id);
                    editor.putString("member_password", member_password);
                    editor.commit();

                    Toast.makeText(LoginActivity.this, "환영합니다.", Toast.LENGTH_LONG).show();
                    Log.d("myBeacon", around.getMyBeaconId());

                    //다음화면으로
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                    Bundle args = new Bundle();
                    String beacon = around.getMyBeaconId();
                    args.putString("beacon_id", beacon);

                    startActivity(intent);
                    finish();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (btn.getId() == R.id.btnRegister) {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
        }
    }

    class LoginPost extends AsyncTask<String, String, String> {

        JSONParser jsonParser = new JSONParser();
        ProgressDialog pDialog;

        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("로그인 중...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

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

        @Override
        protected void onPostExecute(String result) {

            String message =result;

            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (message != null) {
                // [{"login_result":"숫자"}] 에서 숫자 부분이 전달 받아 온다.
                // 1: 아이디와 비밀번호 일치(로그인 가능), 0:아이디와 비밀번호 불일치, 2: 등록된 아이디 없음
                Log.d("message :", message);
                pDialog.dismiss();
            }

            if (message.equals("1")) {
                Log.d("final_success", message);
            } else if(message.equals("0")){
                Log.d("not match (id,password)", message);
            } else if(message.equals("2")){
                Log.d("not registered id", message);
            }
        }
    }
}
