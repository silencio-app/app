package io.github.silencio_app.silencio;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerListnerActivity extends AppCompatActivity {
    private TextView data;
    private static String LOGIN_URL = "http://35.163.237.103/silencio";
    private static String POST_URL = "http://35.163.237.103/silencio/post/";
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_listner);

        data = (TextView)findViewById(R.id.data);
        new GetDataTask().execute(LOGIN_URL);
    }

    class GetDataTask extends AsyncTask<String, Void, JSONObject>{

        @Override
        protected JSONObject doInBackground(String... strings) {
            try{
                return downloadUrl(strings[0]);
            }
            catch (IOException | JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(ServerListnerActivity.this);
            mDialog.setTitle("Logging In");
            mDialog.show();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                data.setText(jsonObject.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mDialog.dismiss();
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
