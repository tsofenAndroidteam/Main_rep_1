package com.example.misho.login.Item;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.misho.login.BookinfoActivity;
import com.example.misho.login.Item.facebook.samples.zoomable.ZoomableDraweeView;
import com.example.misho.login.R;
import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//import android.support.design.widget.Snackbar;
//import android.support.design.widget.TextInputLayout;

public class ViewPagerActivity extends AppCompatActivity {
    List<String> pagesStr = new ArrayList<String>();
    private String ID = "NNL_ALEPH003157499";
    private ViewItemTask mAuthTask = null;
    int i = 0;
    int j = 0;
    ViewPager vpGallery;
    EditText etchange;
    TextView textView1;
    int number;
    public void bookinfo(View v){
        Intent bookinfoactivity = new Intent(this,BookinfoActivity.class);
        startActivity(bookinfoactivity);
    }

    public void changepage(View v){

        String stringnumber = etchange.getText().toString();
        if (!(stringnumber.matches(""))) {
            vpGallery.setCurrentItem(Integer.parseInt(stringnumber) - 1);
            textView1.setText(Integer.parseInt(stringnumber)  + "/" + pagesStr.size());
            //// TODO: 13/09/16 fix page number after change ( Mostafa ) 
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        FLog.setMinimumLoggingLevel(FLog.VERBOSE);
        setContentView(R.layout.activity_view_pager);
        etchange =(EditText)findViewById(R.id.et_changepage);
        textView1=(TextView) findViewById(R.id.textView);
        Intent intent = getIntent();
        // ID = intent.getStringExtra("recordID");
        mAuthTask = new ViewItemTask(ID);
        mAuthTask.execute((Void) null);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Fresco.shutDown();
    }

    public class ViewItemTask extends AsyncTask<Void, Void, JSONObject> {

        private final String bookId;

        ViewItemTask(String bookId) {
            this.bookId = bookId;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            if (params.length == 0) {
                return null;
            }

            Log.v("connect", "CONNECTED");
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String serverJsonStr = null;

            try {
                final String SERVER_BASE_URL =
                        "http://iiif.nli.org.il/IIIF/DOCID/NNL_ALEPH003157499/manifest";
                //TODO: change according to the server function format
//               final String ID_PARAM = "recordID";
//
//                Uri builtUri = Uri.parse(SERVER_BASE_URL).buildUpon()
//                        .appendQueryParameter(ID_PARAM, bookId)
//                        .build();
//
//                URL url = new URL(builtUri.toString());
//

                // Log.v("URL", builtUri.toString());
                URL url = new URL(SERVER_BASE_URL);
                //Log.v("URL", builtUri.toString());
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

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                serverJsonStr = buffer.toString();
                Log.e("PROBLEM", serverJsonStr);

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
            }

            JSONObject serverJson = null;
            try {
                serverJson = new JSONObject(serverJsonStr);
                return serverJson;

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        protected void onPostExecute(final JSONObject success) {
            JSONArray pages=null;

            JSONArray pages3 = null;
            try {
                pages3 = success.getJSONArray("structures");

                JSONObject page2 = pages3.getJSONObject(0);
                pages = page2.getJSONArray("canvases");

                // JSONObject object = pages.getJSONObject(0);
                //JSONObject object1 = pages.getJSONObject(pages.length() - 1);

                String first = "http://iiif.nli.org.il/IIIF/";
                String last = "/full/full/0/default.jpg";
                String tmp = "";
                for (int i = 0; i < pages.length(); i++) {

                    String  book = pages.getString(i);
                    tmp = first +book + last;
                    pagesStr.add(tmp);
                    Log.e("Book pages",tmp);
                }
                // ViewPagerActivity.GalleryAdapter(pagesStr);

                // Intent intent=new Intent(getApplicationContext(),ViewPagerActivity.class);
                // startActivity(intent);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            vpGallery = (ViewPager) findViewById(R.id.vp_gallery);
            vpGallery.setAdapter(new GalleryAdapter(pagesStr));
        }


        protected void onCancelled() {
            mAuthTask = null;
            //   showProgress(false);
        }

    }

    class GalleryAdapter extends PagerAdapter {

        List<String> items;


        //  String[] items = new String[]{
        //         "http://iiif.nli.org.il/IIIF/FL13407243/full/full/0/default.jpg",
        //       "http://iiif.nli.org.il/IIIF/FL13407246/full/full/0/default.jpg",
        //     "http://iiif.nli.org.il/IIIF/FL13407245/full/full/0/default.jpg",
        //   "http://iiif.nli.org.il/IIIF/FL13407153/full/full/0/default.jpg",
        //    "http://iiif.nli.org.il/IIIF/FL13407216/full/full/0/default.jpg",
        //  "http://iiif.nli.org.il/IIIF/FL13407202/full/full/0/default.jpg",
        //"http://iiif.nli.org.il/IIIF/FL13407227/full/full/0/default.jpg",
        //         "http://iiif.nli.org.il/IIIF/FL13407202/full/full/0/default.jpg",
        //       "http://iiif.nli.org.il/IIIF/FL13407108/full/full/0/default.jpg",
        //      "http://iiif.nli.org.il/IIIF/FL13407109/full/full/0/default.jpg",
        //    "http://iiif.nli.org.il/IIIF/FL13407121/full/full/0/default.jpg",
        //   "http://iiif.nli.org.il/IIIF/FL13407218/full/full/0/default.jpg"

        // };
        public GalleryAdapter(List<String> pages){
            items=new ArrayList<String>();
            if(items.addAll(pagesStr)==false){
                return;
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {


            if(j==0) {
                textView1.setText(position + "/" + items.size());
                textView1.setTextSize(20);
                if(position==items.size()-1){
                    i=1;
                }
            }
            if(j==1){
                textView1.setText(items.size()-1 + "/" + items.size());
                j=0;
            }

            ZoomableDraweeView view = new ZoomableDraweeView(container.getContext());
            view.setController(
                    Fresco.newDraweeControllerBuilder()
                            .setUri(Uri.parse(items.get(position)))
                            .build());



            GenericDraweeHierarchy hierarchy =
                    new GenericDraweeHierarchyBuilder(container.getResources())
                            .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                            .setProgressBarImage(new ProgressBarDrawable())
                            .build();

            view.setHierarchy(hierarchy);


            container.addView(view,
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            if(i==0){
                textView1.setText((position - 1) + "/" + items.size());

            }
            else {
                j=1;
                textView1.setText(items.size() + "/" + items.size());
                i=0;
            }

            container.removeView((View) object);


        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}


