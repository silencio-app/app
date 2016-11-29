package io.github.silencio_app.silencio;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ServerListnerActivity extends AppCompatActivity {
    private TextView data;
    private static final String POST_URL = "http://35.163.237.103/silencio/post/";
    private static final String GET_LOCATIONS_URL = "http://35.163.237.103/silencio/locations/";
    public static ArrayList<Location> locationList = new ArrayList<>();
    public static ArrayList<String> categoryList = new ArrayList<>();
    private ProgressDialog mDialog;
    private RecyclerView recyclerView;
    public static LocationAdapter mAdapter;
    public static CategoryAdapter cAdapter;
    private static final String PRE_FETCHED_LIST = "Pre Fetched Location List";
    private String location_json_string;
    ArrayList<String> colorList;
    private WifiManager mWifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_listner);

        categoryList = LocationMapper.categoryList;

        colorList = new ArrayList<>();
        colorList.add("#9b59b6");
        colorList.add("#f1c40f");
        colorList.add("#2ecc71");
        colorList.add("#3498d8");
        colorList.add("#e74c3c");
        colorList.add("#ec7e22");
        colorList.add("#1abc9c");
        colorList.add("#d35400");
        colorList.add("#f39c12");

        cAdapter = new CategoryAdapter(categoryList, colorList);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cAdapter);


        if (savedInstanceState == null){
            mWifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if(mWifiManager.isWifiEnabled()){
                WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
                if (wifiInfo.getNetworkId() == -1){
                    makeSnackbar("You are not connected to any access point");
                }
                else{
                    new GetLocationTask().execute();
                }
            }
            else{
                makeSnackbar("WiFi not enabled");
            }
        }
        else{
            location_json_string = savedInstanceState.getString(PRE_FETCHED_LIST, null);
            json_to_list();
            /*mAdapter = new LocationAdapter(locationList, colorList);
            recyclerView.setAdapter(mAdapter);*/
        }

    }

    public void makeSnackbar(String snackbarText) {
        Snackbar.make(getWindow().getDecorView().getRootView(), snackbarText, Snackbar.LENGTH_LONG)
                .setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {}
                })
                .show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putString(PRE_FETCHED_LIST, location_json_string);
    }

    class GetLocationTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL url = new URL(GET_LOCATIONS_URL);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setReadTimeout(10000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                int response = connection.getResponseCode();
                inputStream = connection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line;
                String buffer = "";
                while ((line = bufferedReader.readLine()) != null) {
                    buffer += line;
                    buffer += "\n";
                }
                connection.disconnect();
                bufferedReader.close();
                String ans =  new String(buffer);
                Log.d("RESPONSE CODE", "******************* "+response);
                Log.d("LETS SEE", ans);
                JSONArray list = new JSONArray(ans);

                return list.toString();
            } catch (JSONException | MalformedURLException | UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(ServerListnerActivity.this);
            mDialog.setTitle("Fetching data");
            mDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            location_json_string = s;
            json_to_list();

            /*mAdapter = new LocationAdapter(locationList, colorList);
            recyclerView.setAdapter(mAdapter);*/
            mDialog.dismiss();
        }
    }

    private void json_to_list(){
        JSONArray list = null;
        try {
            list = new JSONArray(location_json_string);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<Location> locations = new ArrayList<>();
        for (int i=0;i<list.length();i++){
            JSONObject jsonObject = null;
            try {
                jsonObject = list.getJSONObject(i);
                locations.add(new Location(jsonObject.getString("name"), Float.parseFloat(jsonObject.getString("db")), jsonObject.getString("mac")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        locationList = locations;
    }

    public void handle_card_click(View view){
        int open_index = recyclerView.getChildAdapterPosition(view);
        /*int open_index = mAdapter.item_position;*/
        Intent intent = new Intent(ServerListnerActivity.this, DetailedLocationActivity.class);
        intent.putExtra("open_index", open_index);
        startActivity(intent);
    }
    private String get_data_to_post(){
        String urlParameters = null;
        try {
            urlParameters= URLEncoder.encode("code","utf-8")+"="+URLEncoder.encode("100","utf-8");
            urlParameters+= "&"+URLEncoder.encode("username","utf-8")+"="+URLEncoder.encode("vipin","utf-8");
            urlParameters+= "&"+URLEncoder.encode("password","utf-8")+"="+URLEncoder.encode("1234","utf-8");
            return urlParameters;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    class PostData extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings)
        {
            String JsonResponse = null;
            String JsonDATA = strings[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(POST_URL);
                Log.d("DEBUGGER", url.getPath());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                // is output buffer writter
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Accept", "application/x-www-form-urlencoded");
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                String parameter = "&name=" + URLEncoder.encode("Sofa Room", "UTF-8") +
                        "&db_level=" + URLEncoder.encode("58.5", "UTF-8");
                writer.write(parameter);
                writer.close();
                Log.d("DEBUGGER", "CAME HERE"+urlConnection.getResponseCode());
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");
                if (buffer.length() == 0) {
                    // Stream was empty. No point in parsing.
                    return null;
                }
                JsonResponse = buffer.toString();
                Log.i("TAG RESPONSE",JsonResponse);
                return JsonResponse;
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
            mDialog = new ProgressDialog(ServerListnerActivity.this);
            mDialog.setTitle("Posting data");
            mDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mDialog.dismiss();
        }
    }
    private void doposting(String myurl) throws IOException, JSONException {
        InputStream inputStream = null;

        try {
            URL url = new URL(myurl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setReadTimeout(10000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            int response = connection.getResponseCode();
            inputStream = connection.getInputStream();

            Reader reader = null;
            reader = new InputStreamReader(inputStream, "UTF-8");
            char[] buffer = new char[500];
            reader.read(buffer);
            String ans =  new String(buffer);
            JSONArray list = new JSONArray(ans);
            Log.d("LETS SEE", list.getJSONObject(0).getString("name"));
        }
        finally {
            if (inputStream != null){
                inputStream.close();
            }
        }
    }
    private JSONObject downloadUrl(String myurl) throws IOException, JSONException{
        InputStream inputStream = null;
        try {
            URL url = new URL(myurl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setReadTimeout(10000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            int response = connection.getResponseCode();
            inputStream = connection.getInputStream();

            Reader reader = null;
            reader = new InputStreamReader(inputStream, "UTF-8");
            char[] buffer = new char[500];
            reader.read(buffer);
            String ans =  new String(buffer);
            Log.d("LOGGING IT MOFOS", ans);
            JSONArray list = new JSONArray(ans);
            Log.d("LETS SEE", list.getJSONObject(0).getString("name"));
            return list.getJSONObject(0);
        }
        finally {
            if (inputStream != null){
                inputStream.close();
            }
        }
    }
}
