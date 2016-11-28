package io.github.silencio_app.silencio;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity {
    private EditText username_et;
    private EditText password_et;
    private static String USERNAME = "User name of user";
    private static String LOGIN_URL = "http://35.163.237.103/silencio/login/";
    private int RETURNED_LOGIN_FLAG;
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
        String username;
        username = username_et.getText().toString();
        String password;
        password = password_et.getText().toString();
        try {
            String encodedUrl = "&username=" + URLEncoder.encode(username, "UTF-8") +
                    "&password=" + URLEncoder.encode(password, "UTF-8");
            if (!username.equals("") && !password.equals("")){
                new LoginTask().execute(encodedUrl);
                switch (RETURNED_LOGIN_FLAG){
                    case 1:
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.putExtra(USERNAME, username);
                        startActivity(intent);
                        break;
                    case 0:
                        Log.d("MESSAGE", "INVALID METHOD");
                        break;
                    case -1:
                        Log.d("MESSAGE", "USERname password invalid");
                }
            }
            else{
                // Deal with Blank fields
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    public void data() throws JSONException {

    }

    class LoginTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings)
        {
            String parameter = strings[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {

                URL url = new URL(LOGIN_URL);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Accept", "application/x-www-form-urlencoded");

                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(parameter);
                writer.close();
                int response_code = urlConnection.getResponseCode();
                Log.d("DEBUGGER", "****************** RESPONSE CODE = "+response_code);

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine);
                if (buffer.length() == 0) {
                    // Stream was empty. No point in parsing.
                    return null;
                }
                String return_value = buffer.toString();
                return return_value;
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("TAG RESPONSE", "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            handle_login(s);
        }
    }

    private void handle_login(String code) {

        if (code.equals("1")){
            RETURNED_LOGIN_FLAG = 1;
        }
        else if(code.equals("0")){
            RETURNED_LOGIN_FLAG = 0;
        }
        else {
            RETURNED_LOGIN_FLAG = -1;
        }
    }

}
