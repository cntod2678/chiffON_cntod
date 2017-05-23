package media.around.join;

/**
 * Created by Dongjin on 2016. 2. 15..
 */
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import media.around.Around;
import media.around.R;
import media.around.network.JSONParser;

public class RegisterActivity extends AppCompatActivity {
    ImageButton btnCreate;
    EditText editId,editPassword1,editPassword2,editName;

    private static final String REGISTER_URL = Around.url + "/Member/member_join.jsp";
    String register_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
        button_clicked();
    }

    private void initView(){
        btnCreate = (ImageButton)findViewById(R.id.btnCreate);
        editId = (EditText)findViewById(R.id.editId);
        editName = (EditText)findViewById(R.id.editName);
        editPassword1 = (EditText)findViewById(R.id.editPasswd1);
        editPassword2 = (EditText)findViewById(R.id.editPasswd2);
    }

    private void button_clicked() {
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editId.getText())) {
                    Toast.makeText(getApplicationContext(), "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(editPassword1.getText())) {
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(editName.getText())) {
                    Toast.makeText(getApplicationContext(), "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(editPassword2.getText())) {
                    Toast.makeText(getApplicationContext(), "비밀번호 확인을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!(String.valueOf(editPassword1.getText()).equals(String.valueOf(editPassword2.getText())))) {
                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }


                String id = String.valueOf(editId.getText());
                String passowrd = String.valueOf(editPassword1.getText());
                String name = String.valueOf(editName.getText());
                String beacon = "10500";
                String age = "24";

                try {
                    register_result = new RegisterPost().execute(id, passowrd, name, beacon, age).get();
                } catch (Exception e){
                    Toast.makeText(getApplicationContext(), "회원가입을 성공하지 못했습니다.", Toast.LENGTH_SHORT).show();
                }

                if(register_result.equals("0")) {
                    Toast.makeText(getApplicationContext(), "회원가입을 성공했습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                } else if(register_result.equals("2")){
                    Toast.makeText(getApplicationContext(), "이미 사용하고 있는 id 입니다.", Toast.LENGTH_SHORT).show();
                } else {

                }

            }
        });

    }


    class RegisterPost extends AsyncTask<String, String, String> {

        JSONParser jsonParser = new JSONParser();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {

            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("member_id", args[0]);
                params.put("member_password", args[1]);
                params.put("member_name", args[2]);
                params.put("beacon_id", args[3]);
                params.put("member_age", args[4]);


                JSONArray jArr = jsonParser.makeHttpRequest(
                        REGISTER_URL, "POST", params);

                JSONObject json = jArr.getJSONObject(0);

                if (json != null) {
                    Log.d("JSON result", json.toString());

                    String result = json.getString("join_result");

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

            if (message != null) {

                Log.d("message :", message);
            }

            //todo response
            if (message.equals("0")) {
                Log.d("final_success", message);
            } else{
                Log.d("final_denied", message);
            }
        }
    }


}
