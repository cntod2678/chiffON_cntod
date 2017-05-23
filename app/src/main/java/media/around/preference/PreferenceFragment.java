package media.around.preference;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import media.around.Around;
import media.around.MainActivity;
import media.around.R;
import media.around.SplashActivity;
import media.around.network.JSONParser;


public class PreferenceFragment extends Fragment implements MainActivity.OnBackKeyPressedListener {

    private static final String SHARE_URL = Around.url + "/Member/member_share.jsp";
    private static final String CHECK_SHARE_URL = Around.url + "/Member/member_check_share.jsp";

    Switch mSwitch;
    TextView beacon_id, my_id, my_point;
    ImageButton img_button_right1, img_button_right2, btn_logout;

    Around around;
    String myBeacon, myId;
    String check = "1";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        around = (Around) this.getActivity().getApplication();
        myBeacon = around.getMyBeaconId();
        myId = around.getMyId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(savedInstanceState == null) {
            View view = inflater.inflate(R.layout.fragment_preference, container, false);
            initView(view);
            return view;
        } else {
            return container;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setView();
        clickEvent();
    }

    private void initView(View view) {
        btn_logout = (ImageButton) view.findViewById(R.id.btn_logout);
        mSwitch = (Switch) view.findViewById(R.id.mySwitch);
        beacon_id = (TextView) view.findViewById(R.id.txt_beaconId);
        my_id = (TextView) view.findViewById(R.id.txt_myId);
        my_point = (TextView) view.findViewById(R.id.txt_myPoint);
        img_button_right1 = (ImageButton) view.findViewById(R.id.img_button_detail1);
        img_button_right2 = (ImageButton) view.findViewById(R.id.img_button_detail2);

    }

    private void setView() {
        beacon_id.setText("내 비콘 번호 : " + myBeacon);
        my_id.setText("내 아이디 : " + myId);
        my_point.setText("내 포인트 : " + "1000P");
        img_button_right1.setScaleType(ImageView.ScaleType.FIT_CENTER);
        img_button_right2.setScaleType(ImageView.ScaleType.FIT_CENTER);
        btn_logout.setScaleType(ImageView.ScaleType.FIT_XY);

        try {
            check = new CheckSwitch().execute(myBeacon).get();
            if(check.equals("1")) {
                mSwitch.setChecked(true);
            } else {
                mSwitch.setChecked(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clickEvent() {
        btn_logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().finish();
                Intent intent = new Intent(getContext(), SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (check.equals("1")) {
                    check = "0";
                    mSwitch.setChecked(false);
                } else {
                    check = "1";
                    mSwitch.setChecked(true);
                }
                new ChangeSwitch().execute(myBeacon, check);
            }
        });

        img_button_right2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),ClausesActivity.class);
                startActivity(intent);
            }
        });

    }

    class ChangeSwitch extends AsyncTask<String, String, Void> {

        JSONParser jsonParser = new JSONParser();

        @Override
        protected Void doInBackground(String... args) {

            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("beacon_id", args[0]);
                params.put("member_share", args[1]);

                JSONArray jArr = jsonParser.makeHttpRequest(
                        SHARE_URL, "POST", params);

                JSONObject json = jArr.getJSONObject(0);


            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    class CheckSwitch extends AsyncTask<String, String, String> {

        JSONParser jsonParser = new JSONParser();

        @Override
        protected String doInBackground(String... args) {

            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("beacon_id", args[0]);

                JSONArray jArr = jsonParser.makeHttpRequest(
                        CHECK_SHARE_URL, "POST", params);

                JSONObject json = jArr.getJSONObject(0);

                if (json != null) {
                    Log.d("JSON result", json.toString());

                    String result = json.getString("check_share_result");

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

    @Override
    public void onBack() {
        getFragmentManager().popBackStack();
    }

}
