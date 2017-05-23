package media.around.favorite;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import media.around.Around;
import media.around.MainActivity;
import media.around.NonDataFragment;
import media.around.R;
import media.around.find.ImagePopupActivity;
import media.around.network.JSONParser;

public class FavoriteFragment extends Fragment implements MainActivity.OnBackKeyPressedListener{
    ListView listView;
    FavoriteAdapter adapter;
    String beacon_id;
    Around around;
    JSONArray jsonArray;

    private static final String FAVORITE_URL = Around.url + "/Coi/coi_list.jsp";
    private static final String MODIFY_URL = Around.url + "/Coi/coi_insert_delete.jsp";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        around = (Around) getActivity().getApplication();
        beacon_id = around.getMyBeaconId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        initView(view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        itemClick();
    }


    private void initView(View view) {
        listView = (ListView) view.findViewById(R.id.list_favorite);
    }

    private void initAdapter() {
        try {
            jsonArray = new GetFavorite().execute(beacon_id).get();

            // set Adapter
            adapter = new FavoriteAdapter(getContext(), jsonArray);
            listView.setAdapter(adapter);

        } catch(Exception e) {
            // 받은 jsonArray 가 없을 때
            Fragment fragment = null;
            Class fragmentClass = NonDataFragment.class;

            try {
                fragment = (Fragment) fragmentClass.newInstance();

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit();
            } catch (Exception ec) {
                ec.printStackTrace();
            }

            e.printStackTrace();
        }
    }

    private void itemClick() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject json = null;
                try {
                    json = jsonArray.getJSONObject(position);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(getContext(), ImagePopupActivity.class);

                try {
                    intent.putExtra("item", json.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }

                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject json = null;
                String cloth_num;

                try {
                    json = jsonArray.getJSONObject(position);
                    cloth_num = json.getString("cloth_number");

                    new ModifyCoi().execute(beacon_id, cloth_num);

                    jsonArray = new GetFavorite().execute(beacon_id).get();


                    adapter.setSource(jsonArray);
                    Toast.makeText(getActivity().getApplicationContext(), "즐겨찾기에서 해제되었습니다", Toast.LENGTH_LONG).show();


                } catch (Exception e) {
                    e.printStackTrace();
                }


                return true;
            }
        });

    }



    class GetFavorite extends AsyncTask<String, String, JSONArray> {
        JSONParser jsonParser = new JSONParser();
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("검색 중 ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        public JSONArray doInBackground(String... args) {

            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("beacon_id", args[0]);

                JSONArray jArr = jsonParser.makeHttpRequest(
                        FAVORITE_URL, "GET", params);

                if (jArr != null) {
                    return jArr;

                } else
                    Log.d("JSON result", "JSON IS NULL");

            } catch (Exception e) {

                // 받은 jsonArray 가 없을 때
                Fragment fragment = null;
                Class fragmentClass = NonDataFragment.class;

                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception ec) {
                    ec.printStackTrace();
                }

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit();

                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jArr) {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
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

    @Override
    public void onBack() {
        getFragmentManager().popBackStack();
    }

}
