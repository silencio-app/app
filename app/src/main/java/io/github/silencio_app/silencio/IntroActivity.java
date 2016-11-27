package io.github.silencio_app.silencio;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by Divay Prakash on 24-Nov-16.
 */

public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String slideTitle = getString(R.string.slide1title);
        String slideDescription = getString(R.string.slide1description);
        int slideBackground = getResources().getColor(android.R.color.holo_orange_dark);
        addSlide(AppIntroFragment.newInstance(slideTitle, slideDescription, R.drawable.slide1image, slideBackground));
        slideTitle = getString(R.string.slide2title);
        slideDescription = getString(R.string.slide2description);
        slideBackground = getResources().getColor(android.R.color.holo_blue_dark);
        addSlide(AppIntroFragment.newInstance(slideTitle, slideDescription, R.drawable.slide2image, slideBackground));
        slideTitle = getString(R.string.slide3title);
        slideDescription = getString(R.string.slide3description);
        slideBackground = getResources().getColor(android.R.color.holo_green_light);
        addSlide(AppIntroFragment.newInstance(slideTitle, slideDescription, R.drawable.slide3image, slideBackground));
        slideTitle = getString(R.string.slide4title);
        slideDescription = getString(R.string.slide4description);
        slideBackground = getResources().getColor(android.R.color.holo_purple);
        addSlide(AppIntroFragment.newInstance(slideTitle, slideDescription, R.drawable.slide4image, slideBackground));
        showSkipButton(false);
        setDepthAnimation();
    }
    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }
}
