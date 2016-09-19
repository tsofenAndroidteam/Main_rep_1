package com.example.misho.login;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.misho.login.Item.ViewPagerActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

//import com.example.misho.login.Item.ViewPagerActivity;

public class ListviewActivity extends Activity {

    ArrayList<Book> bookList;
    String myURL="any";
    private String ID;
    bookAdapter adapter;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listviewactivity);
        bookList = new ArrayList<Book>();
        Bundle extras = getIntent().getExtras();
         if(extras != null) {
              myURL = extras.getString("Value1");
         }

        //new JSONAsyncTask().execute("http://ec2-52-43-108-148.us-west-2.compute.amazonaws.com:8080/useraccount/search/dosearchbytitle?userid=123123&title=me&fromyear=1960&toyear=1970");
        //new JSONAsyncTask().execute("http://52.29.110.203:8080/LibArab/search/booktitle?userId=23&title=any");
        Log.v("test",myURL);
        new JSONAsyncTask().execute(myURL);
        //http://ec2-52-43-108-148.us-west-2.compute.amazonaws.com:8080/useraccount/search/dosearchbytitle?userid=123123&title=me&fromyear=1960&toyear=1970
        ListView listview = (ListView)findViewById(R.id.list);
        adapter = new bookAdapter(getApplicationContext(), R.layout.row, bookList);

        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long id) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), bookList.get(position).getRecordid(), Toast.LENGTH_LONG).show();
                //Intent intent = getIntent();
                //ID = intent.getStringExtra("recordID");
                ID = "nananana";
                Intent intent1=new Intent(getApplicationContext(),ViewPagerActivity.class);
                intent1.putExtra("recordID",ID);
                startActivity(intent1);

            }
        });
    }


    class JSONAsyncTask extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ListviewActivity.this);
            dialog.setMessage("Loading, please wait");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {

                //------------------>>
                HttpGet httppost = new HttpGet(urls[0]);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);

                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);

                    JSONObject jsono = new JSONObject(data);
                    JSONArray jarray = jsono.getJSONArray("docs");

                    for (int i = 0; i < jarray.length(); i++)
                    {
                        JSONObject object = jarray.getJSONObject(i);
                        Book currentbook = new Book();
                        currentbook.setRecordid(object.getString("recordId"));
                        String  mishel = new String(object.getString("author").getBytes("ISO-8859-1"), "UTF-8");
                        currentbook.setTitle(new String(object.getString("title").getBytes("ISO-8859-1"), "UTF-8"));
                        currentbook.setCreationdate(new String (object.getString("creationdate").getBytes("ISO-8859-1"), "UTF-8"));
                        currentbook.setPublisher(new String(object.getString("publisher").getBytes("ISO-8859-1"), "UTF-8"));
                        currentbook.setAuthor(mishel);
                        //   currentbook.setAuthor(object.getString("author"));
                        bookList.add(currentbook);
                    }
                    return true;
                }

                //------------------>>

            } catch (ParseException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            dialog.cancel();
            adapter.notifyDataSetChanged();
            if(result == false)
                Toast.makeText(getApplicationContext(), "Unable to fetch data from server", Toast.LENGTH_LONG).show();

        }
    }



}