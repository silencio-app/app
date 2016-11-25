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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ServerListnerActivity extends AppCompatActivity {
    private TextView data;
    private static String LOGIN_URL = "http://vipin/silencio/";
    private static String POST_URL = "http://vipin/silencio/post/";
    private static String NEW_CSRF_TOKEN_URL = "http://vipin/silencio/get_new_cookie/";
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
        protected String doInBackground(String... strings) {
            String response="";
            String urlParameters = get_data_to_post();
            try {

                // Manage Cookies
                String cookieString="";
                String csrftoken="";

                URL url = new URL(NEW_CSRF_TOKEN_URL);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                CookieManager cookieManager=io.getCookiesFromURLConnection(urlConnection);
                List<HttpCookie> cookies=cookieManager.getCookieStore().getCookies();
                Iterator<HttpCookie> cookieIterator=cookies.iterator();
                while(cookieIterator.hasNext()){
                    HttpCookie cookie=cookieIterator.next();
                    cookieString+=cookie.getName()+"="+cookie.getValue()+";";

                    if(cookie.getName().equals("csrftoken")){
                        csrftoken=cookie.getValue();
                    }
                }


                url= new URL(POST_URL);
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                urlConnection.setRequestProperty("X-CSRFToken", csrftoken);
                urlConnection.setRequestProperty("Cookies", cookieString);

                OutputStreamWriter streamWriter=new OutputStreamWriter(urlConnection.getOutputStream());

                streamWriter.write(urlParameters);
                streamWriter.flush();
                streamWriter.close();
                int responseCode = urlConnection.getResponseCode();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(ServerListnerActivity.this);
            mDialog.setTitle("Logging In");
            mDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
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
