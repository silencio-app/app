package io.github.silencio_app.silencio;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity {
    private EditText username_et;
    private EditText password_et;
    private static final String USERNAME = "LOGGING USER";
    private static final String LOGIN_URL = "http://35.163.237.103/silencio/login/";
    private static final String SIGNUP_URL = "http://35.163.237.103/silencio/signup/";
    public static final String PREFS_NAME = "SILENCIO_PREFS";
    public static final String PREFS_FIRST_START_KEY = "isFirstStart";
    public static final String PREFS_CURRENT_USER = "currentUser";
    private ProgressDialog mDialog;
    private String username;
    private WifiManager mWifiManager;
    private static final int MIC_PERMISSION_REQUEST_CODE = 1;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 2;

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
        else {
            checkMicPermission();
            String current_user = settings.getString(PREFS_CURRENT_USER, null);
            if (current_user != null) {
                username = current_user;
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(USERNAME, username);
                startActivity(intent);
                finish();
            }
        }
        username_et = (EditText)findViewById(R.id.username_et);
        password_et = (EditText)findViewById(R.id.password_et);
    }

    private void checkMicPermission() {
        String permission = Manifest.permission.RECORD_AUDIO;
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, MIC_PERMISSION_REQUEST_CODE);
        }
    }

    private void checkStoragePermission() {
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MIC_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkStoragePermission();
                }
                else {
                    makePermissionCancelDialog();
                }
            }
            case STORAGE_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {}
                else {
                    makePermissionCancelDialog();
                }
            }
        }
    }

    public void makePermissionCancelDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                .setTitle("And so it ends....")
                .setMessage("We are sorry to inform you that we cannot continue without this permission. Press OK to exit the application.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                        System.exit(0);
                    }
                });
        builder.show();
    }

    private boolean validateForm() {
        boolean setError = false;
        if (checkEmptySetError(username_et)) {
            setError = true;
        }
        if (checkEmptySetError(password_et)) {
            setError = true;
        }
        return setError;
    }

    private boolean checkEmptySetError(EditText editText){
        String text = editText.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            editText.setError("This field is required");
            return true;
        }
        else return false;
    }

    public void hideSoftKeyboard(){
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void makeSnackbar(String snackbarText) {
        Snackbar.make(getWindow().getDecorView().getRootView(), snackbarText, Snackbar.LENGTH_LONG)
                .setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {}
                })
                .show();
    }

    public void login(View view){
        hideSoftKeyboard();
        username = username_et.getText().toString();
        String password = password_et.getText().toString();
        if (!validateForm()) {
            try {
                String encodedUrl = "&username=" + URLEncoder.encode(username, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8");
                mWifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if(mWifiManager.isWifiEnabled()){
                    WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
                    if (wifiInfo.getNetworkId() == -1){
                        makeSnackbar("You are not connected to any access point");
                    }
                    else{
                        new LoginTask().execute(encodedUrl);
                    }
                }
                else{
                    makeSnackbar("WiFi not enabled");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
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
            mDialog = new ProgressDialog(LoginActivity.this);
            mDialog.setMessage("Logging you in");
            mDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            handle_login(s);
            mDialog.dismiss();
        }
    }

    public void signup(View view){
        hideSoftKeyboard();
        username = username_et.getText().toString();
        String password = password_et.getText().toString();
        if (!validateForm()) {
            try {
                String encodedUrl = "&username=" + URLEncoder.encode(username, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8");
                mWifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if(mWifiManager.isWifiEnabled()){
                    WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
                    if (wifiInfo.getNetworkId() == -1){
                        makeSnackbar("You are not connected to any access point");
                    }
                    else{
                        new SignupTask().execute(encodedUrl);
                    }
                }
                else{
                    makeSnackbar("WiFi not enabled");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
    class SignupTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings)
        {
            String parameter = strings[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {

                URL url = new URL(SIGNUP_URL);

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
            mDialog = new ProgressDialog(LoginActivity.this);
            mDialog.setMessage("Registering you on our server");
            mDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            handle_login(s);
            mDialog.dismiss();
        }
    }

    private void makeDialog(Class<?> cls, Context context, String title, String message) {
        final Intent intent = new Intent(context, cls);
        if (cls == MainActivity.class) intent.putExtra(USERNAME, username);
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(intent);
                        finish();
                    }
                });
        builder.show();
    };

    private void handle_login(String code) {

        if (code.equals("1")){
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(USERNAME, username);
            startActivity(intent);
            finish();
        }
        else if (code.equals("2")){
            makeDialog(LoginActivity.class, LoginActivity.this, "Login failed!", "The username " + username + " does not exist.");
        }
        else if (code.equals("3")){
            makeDialog(LoginActivity.class, LoginActivity.this, "Login failed!", "The password you entered is incorrect. Please correct the error.");
        }
        else if (code.equals("4")){
            makeDialog(LoginActivity.class, LoginActivity.this, "Login failed!", "Server error. Kindly try after some time");
        }
        else if (code.equals("5")){
            String msg = "You will now be logged in with your selected username " + username;
            makeDialog(MainActivity.class, LoginActivity.this, "Registration successful!", msg);
        }
        else if (code.equals("6")){
            makeDialog(LoginActivity.class, LoginActivity.this, "Registration failed!", "The username you entered already exists. Please select a different username.");
        }
        else if (code.equals("7")){
            makeDialog(LoginActivity.class, LoginActivity.this, "Registration failed!", "Server error. Kindly try after some time.");
        }
    }

}
