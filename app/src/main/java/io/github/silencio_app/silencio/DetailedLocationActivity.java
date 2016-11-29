package io.github.silencio_app.silencio;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

/**
 * Created by vipin on 29/11/16.
 */
public class DetailedLocationActivity extends FragmentActivity {
    private ArrayList<Location> locationList;
    private ViewPager mPager;
    private int open_index;
    private PagerAdapter mPagerAdapter;
    private PagerTabStrip mPagerTabStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_location_activity);
        locationList = ServerListnerActivity.locationList;
        // Instantiate a ViewPager and a PagerAdapter.

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        open_index = getIntent().getExtras().getInt("open_index");
        mPager.setCurrentItem(open_index, true);

        mPagerTabStrip = (PagerTabStrip) findViewById(R.id.pagerTitle);
        mPagerTabStrip.setTextColor(Color.parseColor("#ffffff"));
        mPagerTabStrip.setTabIndicatorColor(Color.parseColor("#ffffff"));
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }
//        public ScreenSlidePagerAdapter(Activity activity, ViewPager pager){
//            super(activity.getFragmentManager());
//            mContext = activity;
//            mPager = pager;
//
//        }

        @Override
        public Fragment getItem(int position) {
            Location location = locationList.get(position);
            LocationDetailFragment locationDetailFragment = new LocationDetailFragment();
            Bundle args = new Bundle();
            args.putString("location_name", location.getName());
            args.putString("location_db", String.valueOf(location.getDb_level()));
            args.putString("location_mac", location.getMac());

            locationDetailFragment.setArguments(args);
            return locationDetailFragment;
            /*return new ScreenSlidePageFragment();*/
        }


        @Override
        public int getCount() {
            return locationList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position >= 0){
                return "TASK "+ (position + 1);
            }
            return null;
        }
    }
}
