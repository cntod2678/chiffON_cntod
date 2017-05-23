package media.around.rank;

import media.around.MainActivity;
import media.around.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RankFragment extends Fragment implements MainActivity.OnBackKeyPressedListener {

    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(savedInstanceState == null) {
            View view = inflater.inflate(R.layout.fragment_rank, container, false);
            mViewPager = (ViewPager) view.findViewById(R.id.pager_rank);
            mViewPager.setAdapter(new RankPagerAdapter(getContext()));

            mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
            mSlidingTabLayout.setViewPager(mViewPager);

            return view;
        } else
            return container;
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
