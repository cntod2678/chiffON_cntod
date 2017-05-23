package media.around.find;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONObject;

import media.around.MainActivity;
import media.around.R;

public class FindResultFragment extends Fragment implements MainActivity.OnBackKeyPressedListener{
    JSONArray jsonArray;
    GridView gridView;
    private FindResultAdapter adapter;

//    public static FindResultFragment newInstance() {
//        FindResultFragment fragment = new FindResultFragment();
//        Bundle args = new Bundle();
//
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String findResult = getArguments().getString("find_result");
        try {
            jsonArray = new JSONArray(findResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(savedInstanceState == null) {
            View view = inflater.inflate(R.layout.fragment_find_result, container, false);
            gridView = (GridView) view.findViewById(R.id.gridView_find);

            adapter = new FindResultAdapter(this.getContext(), jsonArray);
            gridView.setAdapter(adapter);
            return view;
        }
       return container;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        selectItemDetail();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FindFragment.mRangedBeacons.clear();
        try {
            FindFragment.mRecoManager.unbind();
        } catch (Exception e) {
            e.printStackTrace();
        }
        getFragmentManager().popBackStack();
    }

    private void selectItemDetail() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
    }

    @Override
    public void onBack() {
        getFragmentManager().popBackStack();
    }
}
