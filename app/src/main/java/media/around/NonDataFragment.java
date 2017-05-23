package media.around;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class NonDataFragment extends Fragment implements MainActivity.OnBackKeyPressedListener{
    TextView textView;

    public NonDataFragment() {}

    public static NonDataFragment newInstance() {
        NonDataFragment fragment = new NonDataFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nondata, container, false);
        textView = (TextView) view.findViewById(R.id.txt_message);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onBack() {
        getFragmentManager().popBackStack();
    }
}
