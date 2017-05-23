package media.around.find;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.perples.recosdk.RECOBeacon;
import com.perples.recosdk.RECOBeaconManager;
import com.perples.recosdk.RECOBeaconRegion;
import com.perples.recosdk.RECOErrorCode;
import com.perples.recosdk.RECORangingListener;
import com.perples.recosdk.RECOServiceConnectListener;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import media.around.Around;
import media.around.MainActivity;
import media.around.NonDataFragment;
import media.around.R;
import media.around.closet.ClosetFragment;
import media.around.network.JSONParser;


public class FindFragment extends Fragment implements RECORangingListener, RECOServiceConnectListener, MainActivity.OnBackKeyPressedListener {
    private ImageButton btnSearch;
    private Spinner spinner_bigType, spinner_smallType, spinner_sex, spinner_color;

    private static final String SEARCH_URL = Around.url + "/Coi/coi_find.jsp";

    private List<String> beaconList = new ArrayList<>();

    boolean check_myBeacon = false;

    String[] item_sex = {"남성", "여성"};
    String[] item_bigType = {"Outer", "Top", "Bottom", "Acc", "Shoes"};

    String[] outer_smallType = {"코트", "패딩", "재킷", "블루종", "기타"};

    String[] top_male_smallType = { "티셔츠", "셔츠", "후드", "니트", "가디건"};
    String[] top_female_smallType = { "티셔츠", "셔츠", "후드", "니트", "가디건", "원피스"};

    String[] bottom_male_smallType = {"데님팬츠", "슬렉스", "면바지", "트레이닝", "기타"};
    String[] bottom_female_smallType = {"데님팬츠", "치마", "슬렉스", "면바지", "트레이닝", "기타"};

    String[] acc_smallType = {"가방", "머플러", "모자", "기타"};
    String[] shoes_smallType = {"구두", "워커", "스니커즈", "샌들", "운동화"};

    String[] item_color = {"Black", "White", "Navy", "Gray", "Brown", "Blue", "Green", "Red", "Pink"};

    String sex, bigType, smallType, color;

    ArrayAdapter<String> sex_adapter;
    ArrayAdapter<String> bigType_adapter;
    ArrayAdapter<String> color_adapter;

    //todo reco
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    protected static RECOBeaconManager mRecoManager;
    protected ArrayList<RECOBeaconRegion> mRegions;
    protected static ArrayList<RECOBeacon> mRangedBeacons;

    //todo 2

    public static final String RECO_UUID = "24DDF411-8CF1-440C-87CD-E368DAF9C93E";

    public static final boolean DISCONTINUOUS_SCAN = false;
    public static final boolean SCAN_RECO_ONLY = true;

    public static final boolean ENABLE_BACKGROUND_RANGING_TIMEOUT = true;

    protected static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_LOCATION = 10;

    private String myBeacon;
    Around around;

    //todo end reco


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        around = (Around)getContext().getApplicationContext();
        myBeacon = around.getMyBeaconId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(savedInstanceState == null) {
            View view= inflater.inflate(R.layout.fragment_find, container, false);
            initView(view);
            return view;
        }
        return container;
    }

    public void initView(View v) {
        btnSearch = (ImageButton)v.findViewById(R.id.btnSearch);
        spinner_sex =  (Spinner)v.findViewById(R.id.spinner_sex);
        spinner_bigType = (Spinner)v.findViewById(R.id.spinner_bigType);
        spinner_smallType = (Spinner)v.findViewById(R.id.spinner_smallType);
        spinner_color = (Spinner)v.findViewById(R.id.spinner_color);
    }

    public void addItemOnAdapter() {
        sex_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, item_sex);
        sex_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_sex.setAdapter(sex_adapter);

        bigType_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, item_bigType);
        bigType_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        color_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, item_color);
        color_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_color.setAdapter(color_adapter);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRangedBeacons = new ArrayList<RECOBeacon>();
        //btnSearch.setEnabled(false);

        // 스피너 아이템을 담는 함수
        addItemOnAdapter();

        // 스피너 셋팅
        setSpinnerAdapter();

        //사용자가 블루투스를 켜도록 요청합니다.
        mBluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }

        //위치권한 설정
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.i("FindFragment", "The location permission (ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION) is not granted.");
                this.requestLocationPermission();
            } else {
                Log.i("FindFragment", "The location permission (ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION) is already granted.");
            }
        }

        //---------- RecoActivity ---------- //
        mRecoManager = RECOBeaconManager.getInstance(this.getContext(), SCAN_RECO_ONLY, ENABLE_BACKGROUND_RANGING_TIMEOUT);
        mRegions = this.generateBeaconRegion();

        // -------- Range ---------- //
        //RECORangingListener 를 설정합니다. (필수)
        mRecoManager.setRangingListener(this);
        mRecoManager.bind(this);

        buttonClick();
    }



    public void setSpinnerAdapter() {

        // 성별 정하는 스피너
        spinner_sex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                if (selected.equals("남성")) {
                    sex = "M";
                } else {
                    sex = "W";
                }

                spinner_bigType.setAdapter(bigType_adapter);
                Log.i("sex", sex);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 대분류 정하는 스피너
        spinner_bigType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                ArrayAdapter<String> smallType_adapter;
                smallType_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, outer_smallType);
                smallType_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // 대분류 스피너가 outer 일때
                if(spinner_bigType.getSelectedItem().equals("Outer")){
                    smallType_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, outer_smallType);
                    smallType_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                }

                // 대분류 스피너가 top
                else if(spinner_bigType.getSelectedItem().equals("Top")){

                    if(sex.equals("M")){
                        // 남자에 상의
                        smallType_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, top_male_smallType);
                        smallType_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    }

                    else {
                        // 여자에 상의
                        smallType_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, top_female_smallType);
                        smallType_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    }

                }

                // 대분류 스피너가 bottom
                else if(spinner_bigType.getSelectedItem().equals("Bottom")){
                    if(sex.equals("M")) {
                        // 남자에 하의
                        smallType_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, bottom_male_smallType);
                        smallType_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    }

                    else {
                        // 여자에 하의
                        smallType_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, bottom_female_smallType);
                        smallType_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    }

                }

                // 대분류 스피너가 Acc
                else if(spinner_bigType.getSelectedItem().equals("Acc")){
                    smallType_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, acc_smallType);
                    smallType_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                }

                // 대분류 스피너가 Shoes
                else {
                    smallType_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, shoes_smallType);
                    smallType_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                }

                spinner_smallType.setAdapter(smallType_adapter);
                bigType = selected;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        // 소분류 스피너
        spinner_smallType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            // 소분류 한글을 영어로 바꿔줌 -> 추후 HashMap을 통해서
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                if (selected.equals("코트")) {
                    smallType = "coat";
                } else if (selected.equals("패딩")) {
                    smallType = "padding";
                } else if (selected.equals("재킷")) {
                    smallType = "jacket";
                } else if (selected.equals("블루종")) {
                    smallType = "blouson";
                } else if (selected.equals("티셔츠")) {
                    smallType = "Tshirt";
                } else if (selected.equals("셔츠")) {
                    smallType = "shirt";
                } else if (selected.equals("후드")) {
                    smallType = "hood";
                } else if (selected.equals("니트")) {
                    smallType = "knitwear";
                } else if (selected.equals("가디건")) {
                    smallType = "cardigan";
                } else if (selected.equals("원피스")) {
                    smallType = "dress";
                } else if (selected.equals("데님팬츠")) {
                    smallType = "jeans";
                } else if (selected.equals("슬렉스")) {
                    smallType = "slacks";
                } else if (selected.equals("면바지")) {
                    smallType = "cottons";
                } else if (selected.equals("트레이닝")) {
                    smallType = "training";
                } else if (selected.equals("치마")) {
                    smallType = "skirt";
                } else if (selected.equals("가방")) {
                    smallType = "bag";
                } else if (selected.equals("머플러")) {
                    smallType = "muffler";
                } else if (selected.equals("모자")) {
                    smallType = "cap";
                } else if (selected.equals("구두")) {
                    smallType = "shoes";
                } else if (selected.equals("워커")) {
                    smallType = "walker";
                } else if (selected.equals("스니커즈")) {
                    smallType = "sneakers";
                } else if (selected.equals("샌들")) {
                    smallType = "sandal";
                } else if (selected.equals("운동화")) {
                    smallType = "running";
                } else {
                    smallType = "etc";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 색상 정하는 스피너
        spinner_color.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                color = selected;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void buttonClick() {
        // 검색 버튼 클릭시
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!check_myBeacon) {
                    Snackbar.make(getView(), "내 비콘을 검색할 수 없습니다.", Snackbar.LENGTH_LONG).show();
                    return;
                }

                JSONArray findResult = new JSONArray();
                try {
                    StringBuffer sb = new StringBuffer();

                    Log.i("beacon_values_size", Integer.toString(mRangedBeacons.size()));
                    for (int i = 0; i < mRangedBeacons.size(); i++) {
                        Log.i("beacon_value1", Integer.toString(mRangedBeacons.get(i).getMinor()));

                        sb.append("{\"beacon_id\":" + "\"" + Integer.toString(mRangedBeacons.get(i).getMinor()) + "\"" + ",\"cloth_sex\":" + "\"" + sex + "\"" + ",\"cloth_big_type\":" + "\"" + bigType + "\"" + ",\"cloth_small_type\":" + "\"" + smallType + "\"" + ",\"cloth_color\":" + "\"" + color + "\"" + "}");
                        sb.append("*");
                    }

                    Log.i("Search buffer", sb.toString());

                    findResult = new FindClothes().execute(sb.toString()).get();
                    Log.i("findresult", findResult.toString());

                    // 이동시킬 프래그먼트
                    Fragment fragment = null;
                    Class fragmentClass = FindResultFragment.class;
                    fragment = (Fragment) fragmentClass.newInstance();

                    // 해당 프래그먼트로 이동하기 전 전달할 인자 셋팅
                    Bundle args = new Bundle();
                    String result = findResult.toString();
                    args.putString("find_result", result);
                    fragment.setArguments(args);

                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit();

                } catch (Exception e) {
                    Log.i("findresult", "실패했졍");

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

            }
        });

    }


    //todo Reco

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            //사용자가 블루투스 요청을 허용하지 않았을 경우, 옷장 프래그먼트로 이동
            Log.i("Bluetooth", "블루투스 허용하지 않음");

            Fragment fragment = null;
            Class fragmentClass = ClosetFragment.class;

            try {
                fragment = (Fragment) fragmentClass.newInstance();
                Bundle args = new Bundle();
                args.putString("beacon_id", myBeacon);

                fragment.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {
            case REQUEST_LOCATION : {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(getView(), R.string.location_permission_granted, Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(getView(), R.string.location_permission_not_granted, Snackbar.LENGTH_LONG).show();
                }
            }
            default :
                break;
        }

    }

    /*
        안드로이드 API 23 (마시멜로우)이상 버전부터, 정상적으로 RECO SDK를 사용하기 위해서는
        위치 권한 (ACCESS_COARSE_LOCATION 혹은 ACCESS_FINE_LOCATION)을 요청해야 합니다.
        본 샘플 프로젝트에서는 "ACCESS_COARSE_LOCATION"을 요청하지만, 필요에 따라 "ACCESS_FINE_LOCATION"을 요청할 수 있습니다.
        당사에서는 ACCESS_COARSE_LOCATION 권한을 권장합니다.
    */
    private void requestLocationPermission() {
        if(!ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
            return;
        }

        Snackbar.make(getView(), R.string.location_permission_rationale, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
                    }
                })
                .show();
    }

    // ---------------------- RecoActivity ------------------------ //

    private ArrayList<RECOBeaconRegion> generateBeaconRegion() {
        ArrayList<RECOBeaconRegion> regions = new ArrayList<RECOBeaconRegion>();

        RECOBeaconRegion recoRegion;
        recoRegion = new RECOBeaconRegion(RECO_UUID, "RECO Sample Region");
        regions.add(recoRegion);

        return regions;
    }

    // -------------------- Ranging ---------------------------//
    @Override
    public void onResume() {
        super.onResume();
        mRangedBeacons = new ArrayList<RECOBeacon>();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.stop(mRegions);
        this.unbind();

    }

    private void unbind() {
        try {
            mRecoManager.unbind();
        } catch (RemoteException e) {
            Log.i("RECORangingActivity", "Remote Exception");
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceConnect() {
        Log.i("RECORangingActivity", "onServiceConnect()");
        mRecoManager.setDiscontinuousScan(DISCONTINUOUS_SCAN);
        this.start(mRegions);
        //Write the code when RECOBeaconManager is bound to RECOBeaconService
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<RECOBeacon> recoBeacons, RECOBeaconRegion recoRegion) {
        Log.i("RECORangingActivity", "didRangeBeaconsInRegion() region: " + recoRegion.getUniqueIdentifier() + ", number of beacons ranged: " + recoBeacons.size());

        updateAllBeacons(recoBeacons);
//        mRangedBeacons = new ArrayList<>(recoBeacons);

        Log.i("Msize", Integer.toString(mRangedBeacons.size()));
        if(mRangedBeacons.size() != 0) {
            Iterator iter = mRangedBeacons.iterator();

            while(iter.hasNext()) {
                RECOBeacon beacon = (RECOBeacon)iter.next();
                Log.d("beacon_values", Integer.toString(beacon.getMinor()));

                //todo myBeacon
                if(Integer.toString(beacon.getMinor()).equals(myBeacon)) {
                    check_myBeacon = true;
                    Log.d("beacon_values", "내 비콘 찾았다");
                    break;
                }
            }
            if(check_myBeacon) {
               btnSearch.setEnabled(true);
            }

        } else
            Log.i("beacon_values", "null");
    }

    protected void start(ArrayList<RECOBeaconRegion> regions) {

        for(RECOBeaconRegion region : regions) {
            try {
                mRecoManager.startRangingBeaconsInRegion(region);
            } catch (RemoteException e) {
                Log.i("RECORangingActivity", "Remote Exception");
                e.printStackTrace();
            } catch (NullPointerException e) {
                Log.i("RECORangingActivity", "Null Pointer Exception");
                e.printStackTrace();
            }
        }
    }


    protected void stop(ArrayList<RECOBeaconRegion> regions) {
        for(RECOBeaconRegion region : regions) {
            try {
                mRecoManager.stopRangingBeaconsInRegion(region);
            } catch (RemoteException e) {
                Log.i("RECORangingActivity", "Remote Exception");
                e.printStackTrace();
            } catch (NullPointerException e) {
                Log.i("RECORangingActivity", "Null Pointer Exception");
                e.printStackTrace();
            }
        }
    }

    public static void updateAllBeacons(Collection<RECOBeacon> beacons) {
        synchronized (beacons) {
            mRangedBeacons = new ArrayList<RECOBeacon>(beacons);
        }
    }

    @Override
    public void onServiceFail(RECOErrorCode errorCode) {
        //Write the code when the RECOBeaconService is failed.
        //See the RECOErrorCode in the documents.
        return;
    }


    public void rangingBeaconsDidFailForRegion(RECOBeaconRegion region, RECOErrorCode errorCode) {
        //Write the code when the RECOBeaconService is failed to range beacons in the region.
        //See the RECOErrorCode in the documents.
        return;
    }
    //todo end reco



    class FindClothes extends AsyncTask<String, String, JSONArray> {
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
                params.put("find", args[0]);

                JSONArray jArr = jsonParser.makeHttpRequest(
                        SEARCH_URL, "GET", params);

                if (jArr != null) {
                    return jArr;

                } else {
                    Log.d("JSON result", "JSON IS NULL");
                    Fragment fragment = null;
                    Class fragmentClass = NonDataFragment.class;

                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                    } catch (Exception ec) {
                        ec.printStackTrace();
                    }

                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit();

                }

            } catch (Exception e) {
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

    @Override
    public void onBack() {
        getFragmentManager().popBackStack();
    }

}
