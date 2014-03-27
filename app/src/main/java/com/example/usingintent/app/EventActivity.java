package com.example.usingintent.app;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by timfong224 on 14年2月28日.
 */



public class EventActivity extends Activity{

    private ListView my_event;
//    ArrayList<String> listItems = new ArrayList<String>();
//    ArrayAdapter<String> adapter;

    private String url;
    private String jsonResult;
    private String user_id = "timfong224";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event);

//        my_event = (ListView) findViewById(R.id.lvMyEvent);
//        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
//        my_event.setAdapter(adapter);
        url = "http://www.centhk.com/event/getMyEvent_New.php?"
                +"user_id="+ user_id;
        accessWebService();
    }

    public void onCreateEventClick(View view) {
        startActivity(new Intent(this, CreateEventActivity.class));
    }


    // Async Task to access the web
    // http://codeoncloud.blogspot.hk/2013/07/android-mysql-php-json-tutorial.html
    private class JsonReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
            try {
                HttpResponse response = httpclient.execute(httppost);
                jsonResult = inputStreamToString(response.getEntity().getContent()).toString();
            }

            catch (ClientProtocolException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Error (ClientProtocol:" + e.toString(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Error (IOException):" + e.toString(), Toast.LENGTH_LONG).show();
            }
            return null;
        }

        private StringBuilder inputStreamToString(InputStream is) {
            String rLine = "";
            StringBuilder answer = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            try {
                while ((rLine = rd.readLine()) != null) {
                    answer.append(rLine);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Error (inputStreamToString):" + e.toString(), Toast.LENGTH_LONG).show();
            }
            return answer;
        }

        @Override
        protected void onPostExecute(String result) {
            show_result();
        }
    }// end async task

    public void accessWebService() {
        JsonReadTask task = new JsonReadTask();
        // passes values for the urls string array
        task.execute(new String[] { url });
    }

    // build hash set for list view
    public void show_result() {
        try {
            JSONObject jsonResponse = new JSONObject(jsonResult);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("item");

            EventListAdapter adapter;
            String [] title = new String[jsonMainNode.length()];
            String [] venue = new String[jsonMainNode.length()];
            String [] confirm_time = new String[jsonMainNode.length()];
            Integer[] imageId = new Integer[jsonMainNode.length()];

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                title[i] = jsonChildNode.optString("event_title");
                venue[i] = jsonChildNode.optString("event_venue");
                confirm_time[i] = jsonChildNode.optString("confirm_time");

                //listItems.add("event id = ");
                 //adapter.notifyDataSetChanged();
            }

            //**** http://www.learn2crack.com/2013/10/android-custom-listview-images-text-example.html ****
            adapter = new EventListAdapter(EventActivity.this, title, venue, confirm_time, imageId);
            my_event = (ListView) findViewById(R.id.lvMyEvent);
            my_event.setAdapter(adapter);
            my_event.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });


        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(),
                    "Error (JSONException):" + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

}
