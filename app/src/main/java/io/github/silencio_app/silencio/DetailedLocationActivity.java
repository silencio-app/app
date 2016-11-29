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
    private ArrayList<Location> locationList, filteredList;
    private ViewPager mPager;
    private int open_index;
    private PagerAdapter mPagerAdapter;
    private PagerTabStrip mPagerTabStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_location_activity);

        open_index = getIntent().getExtras().getInt("open_index");
        String regex = "";
        switch (open_index){
            case 0:
                regex = "Boys";
                break;
            case 1:
                regex = "Girls";
                break;
            case 2:
                regex = "Academic";
                break;
            case 3:
                regex = "Library";
                break;
            case 4:
                regex = "SC";
                break;
            case 5:
                regex = "Lab";
                break;
            default:
                // nothing
        }
        locationList = ServerListnerActivity.locationList;
        filteredList = filter_objects(regex);

        // Instantiate a ViewPager and a PagerAdapter.

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        mPagerTabStrip = (PagerTabStrip) findViewById(R.id.pagerTitle);
        mPagerTabStrip.setTextColor(Color.parseColor("#ffffff"));
        mPagerTabStrip.setTabIndicatorColor(Color.parseColor("#ffffff"));
    }
    public ArrayList<Location> filter_objects(String regex){
        ArrayList<Location> arrayList = new ArrayList<>();
        for(Location obj:locationList){
            if(obj.getName().contains(regex)){
                arrayList.add(obj);
            }
        }
        return arrayList;
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
            Location location = filteredList.get(position);
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
            return filteredList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position >= 0){
                return filteredList.get(position).getName();
            }
            return null;
        }
    }
}
