package com.example.misho.login;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



public class SearchActivity extends AppCompatActivity {

    private EditText title ,
                     fromyear ,
                     toyear;
    private Button searchbutton ;
    private JSONObject returnedjson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        title = (EditText) findViewById(R.id.title_edittext);
        fromyear = (EditText) findViewById(R.id.fromyear_editText);
        toyear = (EditText) findViewById(R.id.toYear_editText);
        searchbutton = (Button) findViewById(R.id.searchbtn);
        final String _SEARCH_URL = Params.server+"search/booktitle?";


        searchbutton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //perform action on click

                Uri builtUri = Uri.parse(_SEARCH_URL).buildUpon()
                        .appendQueryParameter("userId",    "4")
                       // .appendQueryParameter("title",    title.getText().toString())
                        .appendQueryParameter("title",    title.getText().toString())
                        .appendQueryParameter("fromyear", fromyear.getText().toString())
                        .appendQueryParameter("toyear",   toyear.getText().toString())
                        .build();
                Log.v("URL", builtUri.toString());
                Intent i = new Intent(v.getContext() ,ListviewActivity.class);
                i.putExtra("Value1", builtUri.toString());
                startActivity(i);

              //  Search searchtask = new Search(total);
              //  searchtask.execute();
                //check if json fetched


            }
        });

    }

    public class Search extends AsyncTask<String[], Void, JSONObject> {

        //    private String _ssfor;
        //    private String _sby;
        //     private String _stxt;
        private String _title;
        private String _userID;
        private String _toyear;
        private String _fromyear;


        @Override
        protected JSONObject doInBackground(String[]... strings) {
            String _server_msg = null;
            HttpURLConnection urlConnection = null;
            String serverJsonStr = null;
            BufferedReader reader = null;


            try {
                final String _SEARCH_URL =
                        "http://ec2-52-43-108-148.us-west-2.compute.amazonaws.com:8080/useraccount/search/dosearchbytitle?";
                Log.v("doinbackground", "Here 1");

                Uri builtUri = Uri.parse(_SEARCH_URL).buildUpon()
                        .appendQueryParameter("userid", _userID)
                        .appendQueryParameter("title", _title)
                        .appendQueryParameter("fromyear", _fromyear)
                        .appendQueryParameter("toyear", _toyear)
                        .build();
                URL url = new URL(builtUri.toString());
                Log.v("URL", builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                serverJsonStr = buffer.toString();
                Log.v("JSON",serverJsonStr);


            } catch (IOException e) {
                Log.e("LOGE", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("LOGE", "Error closing stream", e);
                    }
                }
                final String LOG_TAG = "tag";
                final String LOG_STATUS = "status";

                JSONObject serverJson = null;
                try {
                    serverJson = new JSONObject(serverJsonStr);
                    return serverJson;

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;

            }

        }

        @Override
        protected void onPostExecute(final JSONObject success) {


            try {
                JSONArray list =success.getJSONArray("list");
                //get arraysize (length -1 )
                //int arraysize =list.length();
                //list.length();
                JSONObject book =list.getJSONObject(0);
                JSONObject book2 =list.getJSONObject(1);
                JSONObject book3 =list.getJSONObject(2);

                String bookid =book.getString("recordId");

                Log.e("back from do in back",bookid);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            returnedjson =success;
        }

    }
}


