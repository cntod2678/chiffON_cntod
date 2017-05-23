package media.around.searchother;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import media.around.Around;
import media.around.MainActivity;
import media.around.R;
import media.around.closet.ClosetFragment;
import media.around.network.JSONParser;

public class SearchOtherFragment extends Fragment implements MainActivity.OnBackKeyPressedListener{
    private ListView listView;
    private EditText editSearch;
    private SearchOuterAdapter adapter;

    private static final String GETOTHER_URL = Around.url + "/Member/member_check_substring.jsp";
    JSONArray jsonArray = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_other, container, false);
        editSearch = (EditText)view.findViewById(R.id.edit_search);
        listView = (ListView) view.findViewById(R.id.list_search);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            String text = editSearch.getText().toString();


            jsonArray = new GetOtherId().execute(text).get();

            adapter = new SearchOuterAdapter(getContext(), jsonArray);
            listView.setAdapter(adapter);
        } catch(Exception e) {
            Log.i("88623", "실패했졍");
            e.printStackTrace();
        }
        onClick();
    }

    @Override
    public void onResume() {
        super.onResume();

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {

                    String text = editSearch.getText().toString();

                    jsonArray = new GetOtherId().execute(text).get();
                    adapter.setSource(jsonArray);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void onClick() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    JSONObject json;
                    json = jsonArray.getJSONObject(position);
                    String beacon = json.getString("beacon_id");
                    String member = json.getString("member_id");

                    Fragment fragment = null;
                    Class fragmentClass = ClosetFragment.class;
                    fragment = (Fragment) fragmentClass.newInstance();

                    // 해당 프래그먼트로 이동하기 전 전달할 인자 셋팅
                    Bundle args = new Bundle();
                    args.putString("beacon_id", beacon);
                    args.putString("member_id", member);
                    fragment.setArguments(args);

                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    @Override
    public void onBack() {
        getFragmentManager().popBackStack();
    }

    class GetOtherId extends AsyncTask<String, String, JSONArray> {

        JSONParser jsonParser = new JSONParser();

        @Override
        public JSONArray doInBackground(String... args) {

            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("member_id", args[0]);

                JSONArray jArr = jsonParser.makeHttpRequest(
                        GETOTHER_URL, "GET", params);

                if (jArr != null) {
                    return jArr;

                } else
                    Log.d("JSON result", "JSON IS NULL");

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}
