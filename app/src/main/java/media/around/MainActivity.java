package media.around;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Stack;

import media.around.closet.ClosetFragment;
import media.around.favorite.FavoriteFragment;
import media.around.find.FindFragment;
import media.around.preference.PreferenceFragment;
import media.around.rank.RankFragment;
import media.around.searchother.SearchOtherFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Around around;
    String myBeacon;

    public interface OnBackKeyPressedListener {
        void onBack();
    }

    private Stack<OnBackKeyPressedListener> mFragmentBackStack = new Stack<>();

    public void pushOnBackKeyPressedListener(OnBackKeyPressedListener listener) {
        mFragmentBackStack.push(listener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null) {
            setContentView(R.layout.activity_main);
        } else {
            finish();
            return;
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        around = (Around) getApplication();
        myBeacon = around.getMyBeaconId();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flContent, ClosetFragment.newInstance())
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!mFragmentBackStack.isEmpty()) {
                mFragmentBackStack.pop().onBack();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass = null;

        if (id == R.id.nav_closet) {
            fragmentClass = ClosetFragment.class;

        } else if (id == R.id.nav_find) {
            fragmentClass = FindFragment.class;

        } else if (id == R.id.nav_favorite) {
            fragmentClass = FavoriteFragment.class;

        } else if (id == R.id.nav_board) {
            fragmentClass = SearchOtherFragment.class;

        } else if (id == R.id.nav_info) {
            fragmentClass = RankFragment.class;

        } else if (id == R.id.nav_preferences) {
            fragmentClass = PreferenceFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
            Bundle args = new Bundle();
            args.putString("beacon_id", myBeacon);
            fragment.setArguments(args);
        } catch (Exception e) {
            e.printStackTrace();
        }


        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // 현재 Fragment를 지우지 않고 스택으로 쌓음
    public void switchFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flContent, fragment, null)
                .addToBackStack(null)
                .commit();
    }


}
