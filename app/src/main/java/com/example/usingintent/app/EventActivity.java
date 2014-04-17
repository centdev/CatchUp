package com.example.usingintent.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * Created by timfong224 on 14年2月28日.
 */

public class EventActivity extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> al_eventList;

    // url to get all events list
    private static String url_event_list = "http://www.centhk.com/event2/v1/myevents";

    // JSON Node names
    private static final String TAG_ERR = "error";
    private static final String TAG_MSG = "message";
    private static final String TAG_ID  = "event_id";
    private static final String TAG_TITLE = "event_title";
    private static final String TAG_VENUE = "event_venue";
    private static final String TAG_EVENT = "myEventList";
    private static final String TAG_DATE = "tentative_date";
    private static final String TAG_TIME = "tentative_time";

    ListView lv;
    EventListAdapter adapter;

    // products JSONArray
    JSONArray ja_events = null;

    //private ListView my_event;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event);

        // Hashmap for ListView
        al_eventList = new ArrayList<HashMap<String, String>>();

        // Loading products in Background Thread
        new LoadAllEvents().execute();

        // Get listview
        //ListView lv = getListView();

        lv = (ListView) findViewById(R.id.lvMyEvent);

        // on seleting single product
        // launching Edit Product Screen
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // getting values from selected ListItem
                String event_id = ((TextView) view.findViewById(R.id.tvID)).getText().toString();
                Log.d("***************** Clicked event:  ", event_id );

                // Starting new intent
                Intent in = new Intent(getApplicationContext(), DisplayEventActivity.class);
                // sending pid to next activity
                in.putExtra(TAG_ID, event_id);

                // starting new activity and expecting some response back
                startActivityForResult(in, 100);
            }
        });

    }

    public void onCreateEventClick(View view) {
        startActivity(new Intent(this, CreateEventActivity.class));
    }

    // Response from Edit Product Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // if result code 100 is received
            // means user edited/deleted product
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllEvents extends AsyncTask<String, String, String> {

        Integer[] event_id;
        String[] title;
        String[] venue;
        String[] confirm_time;
        String[] image;

        /*** Before starting background thread Show Progress Dialog     */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EventActivity.this);
            pDialog.setMessage("Loading events. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_event_list, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Events: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                boolean b = json.getBoolean(TAG_ERR);

                if (b == false) {
                    // event found
                    // Getting Array of Event
                    ja_events = json.getJSONArray(TAG_EVENT);
                    Log.d("Json Array: ", ja_events.toString());

                    event_id = new Integer[ja_events.length()];
                    title = new String[ja_events.length()];
                    venue = new String[ja_events.length()];
                    confirm_time = new String[ja_events.length()];
                    image = new String[ja_events.length()];

                    // Loop through all events
                    for (int i = 0; i < ja_events.length(); i++) {
                        JSONObject c = ja_events.getJSONObject(i);

                        // creating new HashMap
                        //HashMap<String, String> map = new HashMap<String, String>();
                        // adding each child node to HashMap key => value
                        //map.put(TAG_TITLE, title);
                        //map.put(TAG_VENUE, venue);
                        // adding HashList to ArrayList
                        //al_eventList.add(map);

                        // Storing each json item in variable
                        event_id[i] = c.getInt(TAG_ID);
                        title[i] = c.getString(TAG_TITLE);
                        venue[i] = c.getString(TAG_VENUE);
                        confirm_time[i] = c.getString(TAG_DATE).concat(" ").concat(c.getString(TAG_TIME));

                        Log.d("Events id ", event_id[i].toString() );
                    }

                } else {
                    // no products found
                    // Launch Add New product Activity
                    //Intent i = new Intent(getApplicationContext(), CreateEventActivity.class);
                    // Closing all previous activities
                    //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * *
         */
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();

            // updating UI from Background Thread
//            runOnUiThread(new Runnable() {
//                public void run() {
//                    /**
//                     * Updating parsed JSON data into ListView
//                     * */
//                     ListAdapter adapter = new SimpleAdapter(
//                            EventActivity.this,
//                            al_eventList,
//                            R.layout.list_single,
//                            new String[] {TAG_TITLE, TAG_VENUE},
//                            new int[] {R.id.tvTitle, R.id.tvVenue}
//                    );
//                    // updating listview
//                    setListAdapter(adapter);

            //**** http://www.learn2crack.com/2013/10/android-custom-listview-images-text-example.html ****
            adapter = new EventListAdapter(EventActivity.this, event_id, title, venue, confirm_time, image);
            lv.setAdapter(adapter);
//                }
//            });

        }

    }



//    // build hash set for list view
//    public void show_result() {
//        try {
//            JSONObject jsonResponse = new JSONObject(jsonResult);
//            JSONArray jsonMainNode = jsonResponse.optJSONArray("item");
//
//            EventListAdapter adapter;
//            String [] title = new String[jsonMainNode.length()];
//            String [] venue = new String[jsonMainNode.length()];
//            String [] confirm_time = new String[jsonMainNode.length()];
//            Integer[] imageId = new Integer[jsonMainNode.length()];
//
//            for (int i = 0; i < jsonMainNode.length(); i++) {
//                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
//                title[i] = jsonChildNode.optString("event_title");
//                venue[i] = jsonChildNode.optString("event_venue");
//                confirm_time[i] = jsonChildNode.optString("confirm_time");
//
//                //listItems.add("event id = ");
//                 //adapter.notifyDataSetChanged();
//            }
//
//            //**** http://www.learn2crack.com/2013/10/android-custom-listview-images-text-example.html ****
//            adapter = new EventListAdapter(EventActivity.this, title, venue, confirm_time, imageId);
//            my_event = (ListView) findViewById(R.id.lvMyEvent);
//            my_event.setAdapter(adapter);
//            my_event.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                }
//            });
//
//
//        } catch (JSONException e) {
//            Toast.makeText(getApplicationContext(),
//                    "Error (JSONException):" + e.toString(), Toast.LENGTH_LONG).show();
//        }
//    }
//
//


}
