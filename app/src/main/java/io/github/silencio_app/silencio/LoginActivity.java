package io.github.silencio_app.silencio;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;

import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    private EditText username_et;
    private EditText password_et;
    public static String USERNAME = "User name of User";
    private static String LOGIN_URL = "http://35.163.237.103/main/login/";
    private URL login_url;
    private static final String PREFS_NAME = "SILENCIO_PREFS";
    private static final String PREFS_FIRST_START_KEY = "isFirstStart";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isFirstStart = settings.getBoolean(PREFS_FIRST_START_KEY, true);
        if (isFirstStart) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(PREFS_FIRST_START_KEY, false);
            editor.commit();
            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
            finish();
        }
        username_et = (EditText)findViewById(R.id.username_et);
        password_et = (EditText)findViewById(R.id.password_et);

        // Show response on activity
    }
    public void login(View view){
        String username = username_et.getText().toString();
        String password = password_et.getText().toString();
        if (username.equals("vipin") && password.equals("1234")){
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(USERNAME, username);
            startActivity(intent);
        }
    }
    public void data() throws JSONException {

    }

}
