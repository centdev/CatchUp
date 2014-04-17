package com.example.usingintent.app;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by timfong224 on 3/14/14.
 */
public class DisplayEventActivity extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jsonParser = new JSONParser();

    // single product url
    String url_event_detials  = "http://www.centhk.com/event2/v1/events/";
    String url_event_options = "http://www.centhk.com/event2/v1/events/";


    // JSON Node names
    private static final String TAG_ID  = "event_id";
    private static final String TAG_ERR = "error";
    private static final String TAG_MSG = "message";
    private static final String TAG_TITLE = "event_title";
    private static final String TAG_VENUE = "event_venue";
    private static final String TAG_INVITEES = "invitees";
    private static final String TAG_CDATE = "confirmed_date";
    private static final String TAG_CTIME = "confirmed_time";

    ImageView ivDelete;
    ImageView ivSave;
    EditText etTitle;
    EditText etVenue;
    TextView tvDate1;
    TextView tvDate2;
    TextView tvTime1;
    TextView tvTime2;
    ImageView ivAddPhoto;
    ListView lvInvitees;


    @Override
    public void onCreate(Bundle savedInstanceState) {

//        http://droidparadise.blogspot.hk/2012/11/networkonmainthreadexception.html
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD)  {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_event);

        ivDelete = (ImageView) findViewById(R.id.ivDelete);
        ivSave = (ImageView) findViewById(R.id.ivSave);

        etTitle = (EditText) findViewById(R.id.etTitle);
        etVenue = (EditText) findViewById(R.id.etVenue);
        tvDate1 = (TextView) findViewById(R.id.tvDate1);
        tvDate2 = (TextView) findViewById(R.id.tvDate2);
        tvTime1 = (TextView) findViewById(R.id.tvTime1);
        tvTime2 = (TextView) findViewById(R.id.tvTime2);
        ivAddPhoto = (ImageView) findViewById(R.id.ivAddPhoto);
        lvInvitees = (ListView) findViewById(R.id.lvInvitees);

//        ivSave = (ImageView) findViewById(R.id.ivSave);
//        ivDelete = (ImageView) findViewById(R.id.ivDelete);

        // getIntent() is a method from the started activity
        Intent myIntent = getIntent(); // gets the previously created intent
        String id = myIntent.getStringExtra(TAG_ID);
        url_event_detials =  url_event_detials + id;
        url_event_options = url_event_options + id + "/options";

        Log.d("****** url_event_detials *********",url_event_detials);

        new GetEventDetails().execute();

        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SaveEventDetails().execute();
            }
        });

//        ivDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new DeleteEvent().execute();
//            }
//        });

    }

    /**
     * Background Async Task to Get complete product details
     * */
    class GetEventDetails extends AsyncTask<String, String, String> {

        String[] invitees;

        /*** Before starting background thread Show Progress Dialog   **/
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DisplayEventActivity.this);
            pDialog.setMessage("Loading event details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /** * Getting product details in background thread  * */
        protected String doInBackground(String... params) {

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    // Check for success tag
                    int success;
                    try {
                        //**************************** Get Event Detail *********************************
                        // Building Parameters
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        JSONObject json = jsonParser.makeHttpRequest(url_event_detials, "GET", params);

                        // check your log for json response
                        Log.d("Single Event Details", json.toString());

                        boolean b = json.getBoolean(TAG_ERR);
                        String event_title = json.getString(TAG_TITLE);
                        String event_venue = json.getString(TAG_VENUE);
                        String confirm_date = json.getString(TAG_CDATE);
                        String confirm_time = json.getString(TAG_CTIME);

                        if (b == false) {

                            // successfully received product details
                            JSONArray jaInvitees = json.getJSONArray(TAG_INVITEES); // JSON Array
                            invitees = new String[jaInvitees.length()];




                            etTitle.setText(event_title);
                            etVenue.setText(event_venue);
                            tvDate2.setText(confirm_date);
                            tvTime2.setText(confirm_time);

                            // Loop through all events
                            for (int i = 0; i < jaInvitees.length(); i++) {
                                JSONObject c = jaInvitees.getJSONObject(i);

                                // Storing each json item in variable
                                invitees[i] = c.getString(TAG_INVITEES);
                                Log.d("invitees: ", invitees[i].toString());
                            }

                        }else{
                            // product with pid not found
                        }

                        //**************************** Get Event Option *********************************
                        JSONObject jsonOption = jsonParser.makeHttpRequest(url_event_options, "GET", params);





                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            return null;
        }

        /*** After completing background task Dismiss the progress dialog  * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once got all details
            pDialog.dismiss();
        }
    }


    //    /*****************************************************************
    /*** Background Async Task to  Save product Details   * */
    class SaveEventDetails extends AsyncTask<String, String, String> {

        /*** Before starting background thread Show Progress Dialog    * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DisplayEventActivity.this);
            pDialog.setMessage("Saving event ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Saving product
         * */
        protected String doInBackground(String... args) {

            // getting updated data from EditTexts
            etTitle = (EditText) findViewById(R.id.etTitle);
            etVenue = (EditText) findViewById(R.id.etVenue);
            tvDate1 = (TextView) findViewById(R.id.tvDate1);
            tvDate2 = (TextView) findViewById(R.id.tvDate2);
            tvTime1 = (TextView) findViewById(R.id.tvTime1);
            tvTime2 = (TextView) findViewById(R.id.tvTime2);
            ivAddPhoto = (ImageView) findViewById(R.id.ivAddPhoto);
            lvInvitees = (ListView) findViewById(R.id.lvInvitees);

            String title = etTitle.getText().toString();
            String venue = etVenue.getText().toString();
            String photo = ivAddPhoto.toString();
            String confirmed_date = (String) tvDate2.getText();
            String confirmed_time = (String) tvTime2.getText();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("event_title", title));
            params.add(new BasicNameValuePair("event_venue", venue));
            params.add(new BasicNameValuePair("event_photo_path", photo));
            params.add(new BasicNameValuePair("confirmed_date", confirmed_date));
            params.add(new BasicNameValuePair("confirmed_time", confirmed_time));
            params.add(new BasicNameValuePair("fb_album_id", ""));

            // sending modified data through http request
            // Notice that update product url accepts POST method
            try {
                JSONObject json = jsonParser.makeHttpRequest(url_event_detials, "PUT", params);
                Log.d("*********** url_event_detials: ", url_event_detials.toString());
                Log.d("*********** params: ", params.toString());
                Log.d("*********** result: ", json.toString());

                boolean b = json.getBoolean(TAG_ERR);

                if (b == false) {

                    startActivity(new Intent(getApplicationContext(), EventActivity.class));
                    finish();

                }else{
                    // product with pid not found
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product uupdated
            pDialog.dismiss();
        }
    }
//
//    /*****************************************************************
//     * Background Async Task to Delete Product
//     * */
//    class DeleteEvent extends AsyncTask<String, String, String> {
//
//        /**
//         * Before starting background thread Show Progress Dialog
//         */
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pDialog = new ProgressDialog(DisplayEventActivity.this);
//            pDialog.setMessage("Deleting Event...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(true);
//            pDialog.show();
//        }
//
//        /**
//         * Deleting product
//         */
//        protected String doInBackground(String... args) {
//
//            // Check for success tag
//            int success;
//            try {
//                // Building Parameters
//                List<NameValuePair> params = new ArrayList<NameValuePair>();
//                params.add(new BasicNameValuePair("pid", pid));
//
//                // getting product details by making HTTP request
//                JSONObject json = jsonParser.makeHttpRequest(
//                        url_delete_product, "POST", params);
//
//                // check your log for json response
//                Log.d("Delete Product", json.toString());
//
//                // json success tag
//                success = json.getInt(TAG_SUCCESS);
//                if (success == 1) {
//                    // product successfully deleted
//                    // notify previous activity by sending code 100
//                    Intent i = getIntent();
//                    // send result code 100 to notify about product deletion
//                    setResult(100, i);
//                    finish();
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//
//        /**
//         * After completing background task Dismiss the progress dialog
//         * *
//         */
//        protected void onPostExecute(String file_url) {
//            // dismiss the dialog once product deleted
//            pDialog.dismiss();
//
//        }
//    }


}
