package io.github.silencio_app.silencio;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView tx = (TextView)findViewById(R.id.textViewSilencio);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Somatic-Rounded.otf");
        tx.setTypeface(custom_font);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME);
    }

}
