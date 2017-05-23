package media.around.closet;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.support.design.widget.FloatingActionButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.HashMap;

import media.around.Around;
import media.around.MainActivity;
import media.around.NonDataFragment;
import media.around.R;
import media.around.clothes.ClothesActivity;
import media.around.network.JSONParser;

/**
 * Created by Dongjin on 2016. 2. 11..
 */
public class ClosetFragment extends Fragment implements MainActivity.OnBackKeyPressedListener, View.OnClickListener {

    private static final String CLOTHES_URL = Around.url + "/Cloth/cloth_type.jsp";

    private Button btnOuter, btnTop, btnBottom, btnShoes, btnAcc;
    private FloatingActionButton btnFab;
    private TextView txtClosetName;

    private String beaconId, member_id;
    Around around;

    public ClosetFragment() {}

    public static ClosetFragment newInstance() {
        ClosetFragment fragment = new ClosetFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        around = (Around) this.getActivity().getApplication();
        String myBeacon  = around.getMyBeaconId();
        member_id = "My Closet";

        if(getArguments().getString("beacon_id") != null) {
            beaconId = getArguments().getString("beacon_id");
            if (myBeacon.equals(beaconId)) {
                beaconId = myBeacon;
                member_id = "My Closet";
            } else {
                beaconId = getArguments().getString("beacon_id");
                member_id = getArguments().getString("member_id");
            }
        } else {
            beaconId = myBeacon;
            member_id = "My Closet";
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(savedInstanceState == null) {
            View view = inflater.inflate(R.layout.fragment_closet, container, false);

            txtClosetName = (TextView) view.findViewById(R.id.txt_closet_name);
            btnOuter = (Button) view.findViewById(R.id.btnOuter);
            btnTop = (Button) view.findViewById(R.id.btnTop);
            btnBottom = (Button) view.findViewById(R.id.btnBottom);
            btnShoes = (Button) view.findViewById(R.id.btnShoes);
            btnAcc = (Button) view.findViewById(R.id.btnAcc);

            btnFab = (FloatingActionButton) view.findViewById(R.id.btnFloatingAction);
            return view;
        } else {
            return container;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        txtClosetName.setText(member_id);

        btnOuter.setOnClickListener(this);
        btnTop.setOnClickListener(this);
        btnBottom.setOnClickListener(this);
        btnShoes.setOnClickListener(this);
        btnAcc.setOnClickListener(this);
        btnFab.setOnClickListener(this);
    }

    public void onClick(View v) {
        Log.d("myBeacon", "Closet " + beaconId);
        String type = "";

        Intent intent = new Intent(getContext().getApplicationContext(), ClothesActivity.class);

        if(v.getId() == R.id.btnFloatingAction) {
            Toast.makeText(getContext(), "준비중인 기능입니다. 홈페이지를 이용해주세요", Toast.LENGTH_SHORT).show();
        }

        else {
            if (v.getId() == R.id.btnOuter) {
                type = "Outer";

            } else if (v.getId() == R.id.btnTop) {
                type = "Top";
                //intent.putExtra("type", type);

            } else if (v.getId() == R.id.btnBottom) {
                type = "Bottom";
               // intent.putExtra("type", type);

            } else if (v.getId() == R.id.btnAcc) {
                type = "Acc";
                //intent.putExtra("type", type);

            } else if (v.getId() == R.id.btnShoes) {
                type = "Shoes";
                //intent.putExtra("type", type);
            }

            try {
                JSONArray jArr = new GetCloth().execute(beaconId, type).get();
                if(jArr == null) {
                    Fragment fragment = null;
                    Class fragmentClass = NonDataFragment.class;

                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                    } catch (Exception ec) {
                        ec.printStackTrace();
                    }

                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit();
                } else {
                    intent.putExtra("beacon_id", beaconId);
                    intent.putExtra("type", type);
                    startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBack() {
        getFragmentManager().popBackStack();
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
