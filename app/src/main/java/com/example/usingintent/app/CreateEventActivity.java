package com.example.usingintent.app;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by timfong224 on 14年2月28日.
 */
public class CreateEventActivity extends Activity{

    //private static final int TIMEOUT_MILLISEC = 5000;
    private TextView tvDate1;
    private TextView tvDate2;
    private TextView tvTime1;
    private TextView tvTime2;
    private ListView listview;
    private EditText etTitle;
    private EditText etVenue;
    private AutoCompleteTextView actvInvite;
    private Button buAddPhoto;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int second;

    static final int DATE_PICKER_ID1 = 1001;
    static final int DATE_PICKER_ID2 = 1002;

    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    public ArrayList<String> c_Name = new ArrayList<String>();
//    private String jsonResult;
//    private String url;
//    private String user_id = "timfong224";
//    private String user_name = "TimFong";


    //----- http://www.androidhive.info/2012/05/how-to-connect-android-with-php-mysql/
    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    // url to create new product
    private static String url_create_event = "http://www.centhk.com/event2/v1/events";
    private static final String EVENT_ID = "event_id";
    private static final String EVENT_ERR = "error";
    private static final String EVENT_MSG = "message";


    // ******************************** Begin of onCreate ***********************************************
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);


        etTitle = (EditText) findViewById(R.id.etTitle);
        etVenue = (EditText) findViewById(R.id.etVenue);
        buAddPhoto = (Button) findViewById(R.id.buAddPhoto);


        actvInvite = (AutoCompleteTextView)findViewById(R.id.actvInvite);
        setCurrentDateOnView();
        fetchContacts();
        //actvInvite.setThreshold(2);
        actvInvite.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, c_Name));

        listview = (ListView) findViewById(R.id.listView);
        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        listview.setAdapter(adapter);


        Button buSonSon = (Button) findViewById(R.id.buSonSon);
        buSonSon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CreateNewEvent().execute();
            }
        });

    }

    /**     * Background Async Task to Create new product     * */
    class CreateNewEvent extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CreateEventActivity.this);
            pDialog.setMessage("Creating Event..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        /**         * Creating Event         * */
        protected String doInBackground(String... args) {
            String title = etTitle.getText().toString();
            String venue = etVenue.getText().toString();
            String photo = buAddPhoto.getText().toString();
            String tentative_date = (String) tvDate1.getText();
            String tentative_time = (String) tvTime1.getText();
            String confirmed_date = (String) tvDate2.getText();
            String confirmed_time = (String) tvTime2.getText();

            // Building Parameters
            //List<NameValuePair> params = new ArrayList<NameValuePair>();
//            params.add(new BasicNameValuePair("event_title", title));
//            params.add(new BasicNameValuePair("event_venue", venue));
//            params.add(new BasicNameValuePair("event_photo_path", photo));
//            params.add(new BasicNameValuePair("confirmed_date", confirmed_date));
//            params.add(new BasicNameValuePair("confirmed_time",confirmed_time));
//            params.add(new BasicNameValuePair("fb_album_id",""));
//            params.add(new BasicNameValuePair("tentative_date",tentative_date));
//            params.add(new BasicNameValuePair("tentative_time",tentative_time));

            JSONObject jEvent = new JSONObject();
            JSONObject jEventOption = new JSONObject();
            JSONObject jInvitees = new JSONObject();
            JSONArray jrInvitees = new JSONArray();

            //http://stackoverflow.com/questions/8706046/create-json-in-android
            try {
                jEvent.put("event_title", title);
                jEvent.put("event_venue", venue);
                jEvent.put("event_photo_path", photo);
                jEvent.put("confirmed_date", confirmed_date);
                jEvent.put("confirmed_time", confirmed_time);
                jEvent.put("fb_album_id", "");

                jEventOption.put("tentative_date", tentative_date);
                jEventOption.put("tentative_time", tentative_time);

                jrInvitees.put("WNpGeQqIKz");
                jrInvitees.put("BDewUvBvtp");
//                for (PhoneNumber pn : person.getPhoneList() ) {
//                    JSONObject pnObj = new JSONObject();
//                    pnObj.put("num", pn.getNumber());
//                    pnObj.put("type", pn.getType());
//                    jsonArr.put(pnObj);
//                }



                //Log.d("*************** Params: ", params.toString());

                // getting JSON Object
                // Note that create product url accepts POST method
                JSONObject json = new JSONObject();
                json.put("event", jEvent);
                json.put("eventOptions", jEventOption);
                json.put("invitees", jrInvitees);
                Log.d("*************** json: ", json.toString());
                //Log.d("*************** param: ", params.toString());
                json = jsonParser.makeJsonHttpRequest(url_create_event, "POST", json.toString());



                //int event_id = json.getInt(EVENT_ID);
                boolean event_err = json.getBoolean(EVENT_ERR);
                //String event_msg = json.getString(EVENT_MSG);

                if (event_err == false) {
                    // successfully created product
                    //Toast.makeText(getApplicationContext(), "Event Created. Event ID:" + event_id, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), EventActivity.class));
                    // closing this screen
                    finish();
                } else {
                    // failed to create product
                    //Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Toast.makeText(getApplicationContext(), "Test", Toast.LENGTH_SHORT).show();
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }
    }
    // ******************************** end of onCreate *************************************************

    public void onAddContactClick(View v) {

        AutoCompleteTextView contact = (AutoCompleteTextView)findViewById(R.id.actvInvite);
        listItems.add(contact.getText()+"");
        adapter.notifyDataSetChanged();
    }



    public void fetchContacts() {
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);

        // Loop for every contact in the phone
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                //String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
                String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));
                if (hasPhoneNumber > 0) {
                    c_Name.add(name);
                }
            }
        }
    }

    // display current date
    public void setCurrentDateOnView() {

        // Get current date by calender
        final Calendar c = Calendar.getInstance();
        year  = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day   = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        second = c.get(Calendar.SECOND);

        tvDate1 = (TextView) findViewById(R.id.tvDate1);
        tvTime1 = (TextView) findViewById(R.id.tvTime1);

        tvDate2 = (TextView) findViewById(R.id.tvDate2);
        tvTime2 = (TextView) findViewById(R.id.tvTime2);

        // Show current date
        //tvDate1.setText(new StringBuilder().append(day).append("/").append(month + 1).append("/").append(year).append(" "));
        tvDate1.setText(new StringBuilder().append(year).append("-").append(month + 1).append("-").append(day).append(" "));
        tvTime1.setText(new StringBuilder().append(hour).append(":").append(second));
        //tvDate2.setText(new StringBuilder().append(day).append("/").append(month + 1).append("/").append(year).append(" "));
        tvDate2.setText(new StringBuilder().append(year).append("-").append(month + 1).append("-").append(day).append(" "));
        tvTime2.setText(new StringBuilder().append(hour).append(":").append(second));


        // Button listener to show date picker dialog
        tvDate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DATE_PICKER_ID1);
            }
        });
        tvDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DATE_PICKER_ID2);
            }
        });
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID1:
                // open datepicker dialog.                // set date picker for current date                // add pickerListener listner to date picker
                return new DatePickerDialog(this, pickerListener, year, month, day);
            case DATE_PICKER_ID2:
                return  new DatePickerDialog(this, pickerListener2, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {
        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,  int selectedMonth, int selectedDay) {
            year  = selectedYear;
            month = selectedMonth;
            day   = selectedDay;
            // Show selected date
//            tvDate1.setText(new StringBuilder()
//                    .append(day).append("/")
//                    .append(month + 1)
//                    .append("/").append(year)
//                    .append(" "));
            tvDate1.setText(new StringBuilder()
                    .append(year).append("-")
                    .append(month + 1).append("-")
                    .append(day)
                    .append(" "));
        }
    };

    private DatePickerDialog.OnDateSetListener pickerListener2 = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,  int selectedMonth, int selectedDay) {

            year  = selectedYear;
            month = selectedMonth;
            day   = selectedDay;

            // Show selected date
            tvDate2.setText(new StringBuilder()
                    .append(year).append("-")
                    .append(month + 1).append("-")
                    .append(day)
                    .append(" "));
        }
    };
}



//
//    // Async Task to access the web
//    // http://codeoncloud.blogspot.hk/2013/07/android-mysql-php-json-tutorial.html
//    private class JsonReadTask extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... params) {
//            HttpClient httpclient = new DefaultHttpClient();
//            HttpPost httppost = new HttpPost(params[0]);
//            try {
//                HttpResponse response = httpclient.execute(httppost);
//                jsonResult = inputStreamToString(response.getEntity().getContent()).toString();
//            }
//
//            catch (ClientProtocolException e) {
//                e.printStackTrace();
//                Toast.makeText(getApplicationContext(), "Error1:" + e.toString(), Toast.LENGTH_LONG).show();
//            } catch (IOException e) {
//                e.printStackTrace();
//                Toast.makeText(getApplicationContext(), "Error2:" + e.toString(), Toast.LENGTH_LONG).show();
//            }
//            return null;
//        }
//
//        private StringBuilder inputStreamToString(InputStream is) {
//            String rLine = "";
//            StringBuilder answer = new StringBuilder();
//            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
//
//            try {
//                while ((rLine = rd.readLine()) != null) {
//                    answer.append(rLine);
//                }
//            }
//            catch (IOException e) {
//                e.printStackTrace();
//                Toast.makeText(getApplicationContext(), "Error3:" + e.toString(), Toast.LENGTH_LONG).show();
//            }
//            return answer;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//
//            show_result();
//        }
//    }// end async task
//
//    public void accessWebService() {
//        JsonReadTask task = new JsonReadTask();
//        // passes values for the urls string array
//        task.execute(new String[] { url });
//    }
//
//    // build hash set for list view
//    public void show_result() {
////        List<Map<String, String>> employeeList = new ArrayList<Map<String, String>>();
//
//        try {
//            JSONObject jsonResponse = new JSONObject(jsonResult);
//            JSONArray jsonMainNode = jsonResponse.optJSONArray("item");
//
//            for (int i = 0; i < jsonMainNode.length(); i++) {
//                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
//                String event_id = jsonChildNode.optString("event_id");
//                Toast.makeText(getApplicationContext(), "Event Created. Event ID:" + event_id, Toast.LENGTH_SHORT).show();
//
//            }
//            startActivity(new Intent(this, EventActivity.class));
//        } catch (JSONException e) {
//            Toast.makeText(getApplicationContext(), "Error:" + e.toString(), Toast.LENGTH_SHORT).show();
//        }
//    }


//        View.OnClickListener listener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(v==)
//
//            }
//        };

//    public void onClick(View v) {
//        if (v==this.)
//    }


//        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo netInfo = cm.getActiveNetworkInfo();
//        if (netInfo != null && netInfo.isConnected()) {
//            try {
//                URL url = new URL("http://www.google.com");
//                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
//                urlc.setConnectTimeout(3000);
//                urlc.connect();
//                if (urlc.getResponseCode() == 200) {
//                    Toast.makeText(this, "getResponseCode() == 200 ", Toast.LENGTH_LONG).show();
//
//                }
//            } catch (MalformedURLException e1) {
//                // TODO Auto-generated catch block
//                e1.printStackTrace();
//                Toast.makeText(this, "msg1: " + e1.toString(), Toast.LENGTH_LONG).show();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//                Toast.makeText(this, "msg2: " + e.toString(), Toast.LENGTH_LONG).show();
//            }
//        }
//        Toast.makeText(this, "done ", Toast.LENGTH_LONG).show();



//        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//        String result = null;
//        InputStream is = null;
//        StringBuilder sb=null;
//
//        //http post
//        try{
//            HttpClient httpclient = new DefaultHttpClient();
//            HttpPost httppost = new HttpPost("http://www.centhk.com/event/create_event.php?");
//            nameValuePairs.add(new BasicNameValuePair("user_id", "test006"));
//            nameValuePairs.add(new BasicNameValuePair("user_name", "fourfour"));
//            nameValuePairs.add(new BasicNameValuePair("event_title", "eventfour"));
//            nameValuePairs.add(new BasicNameValuePair("event_venue", "venuefour"));
//            nameValuePairs.add(new BasicNameValuePair("event_inviters", "invitersfour"));
//
//            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//            HttpResponse response = httpclient.execute(httppost);
//            HttpEntity entity = response.getEntity();
//            is = entity.getContent();
//        }catch(Exception e){
//            Log.e("log_tag", "Error in http connection"+e.toString());
//            Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
//        }



//        try{
//            String link = "http://www.centhk.com/event/create_event.php?user_id=test002&user_name=test&event_title=test&event_venue=test&event_inviters=test";
//            URL url = new URL(link);
//            HttpClient client = new DefaultHttpClient();
//            HttpGet request = new HttpGet();
//            request.setURI(new URI(link));
//            HttpResponse response = client.execute(request);
//            BufferedReader in = new BufferedReader
//                    (new InputStreamReader(response.getEntity().getContent()));
//
//            StringBuffer sb = new StringBuffer("");
//            String line="";
//            while ((line = in.readLine()) != null) {
//                sb.append(line);
//                break;
//            }
//            in.close();
//            //return sb.toString();
//            Toast.makeText(this, "msg1: " + sb.toString(), Toast.LENGTH_LONG).show();
//        }catch(Exception e){
//            //return new String("Exception: " + e.getMessage());
//            Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
//        }


//        try {
//            // http://androidarabia.net/quran4android/phpserver/connecttoserver.php
//
//            // Log.i(getClass().getSimpleName(), "send  task - start");
//            HttpParams httpParams = new BasicHttpParams();
//            HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
//            HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
//            //
//            HttpParams p = new BasicHttpParams();
//            // p.setParameter("name", pvo.getName());
//            p.setParameter("user_id", "jj456");
//
//            // Instantiate an HttpClient
//            HttpClient httpclient = new DefaultHttpClient(p);
//            String url = "http://www.centhk.com/event/" +
//                    "create_event.php?user_id=timfong123&user_name=Tim&event_title=test&event_venue=test&event_inviters=test";
//            HttpPost httppost = new HttpPost(url);
//
//            // Instantiate a GET HTTP method
//            try {
//                Log.i(getClass().getSimpleName(), "send  task - start");
//                //
//                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//                nameValuePairs.add(new BasicNameValuePair("user_id", "timtim123"));
//                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//                ResponseHandler<String> responseHandler = new BasicResponseHandler();
//                String responseBody = httpclient.execute(httppost,
//                        responseHandler);
//                // Parse
//                JSONObject json = new JSONObject(responseBody);
//                JSONArray jArray = json.getJSONArray("posts");
//                ArrayList<HashMap<String, String>> mylist =
//                        new ArrayList<HashMap<String, String>>();
//
//                for (int i = 0; i < jArray.length(); i++) {
//                    HashMap<String, String> map = new HashMap<String, String>();
//                    JSONObject e = jArray.getJSONObject(i);
//                    String s = e.getString("responseResult");
//                    JSONObject jObject = new JSONObject(s);
//
//                    map.put("event_id", jObject.getString("event_id"));
//                    map.put("event_user_id", jObject.getString("event_user_id"));
//                    map.put("event_use_flag", jObject.getString("event_use_flag"));
//
//                    mylist.add(map);
//                }
//                Toast.makeText(this, responseBody, Toast.LENGTH_LONG).show();
//
//            } catch (ClientProtocolException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            // Log.i(getClass().getSimpleName(), "send  task - end");
//
//        } catch (Throwable t) {
//            Toast.makeText(this, "Request failed: " + t.toString(),
//                    Toast.LENGTH_LONG).show();
//        }

//        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//        nameValuePairs.add(new BasicNameValuePair("user", "1"));
//
//        HttpClient httpclient = new DefaultHttpClient();
//        HttpPost httppost = new HttpPost("http://www.centhk.com/event/create_event.php?user_id=timfong224&user_name=Tim&event_title=test&event_venue=test&event_inviters=test");
//        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//        HttpResponse response = httpclient.execute(httppost);
//        HttpEntity entity = response.getEntity();
//        is = entity.getContent();

//        String link = "http://www.centhk.com/event/create_event.php?user_id=timfong224&user_name=Tim&event_title=test&event_venue=test&event_inviters=test";
//              // +etTitle.getText()+"event_venue"+etVenue.getText();
//        URL url = new URL(link);
//        HttpClient client = new DefaultHttpClient();
//        HttpGet request = new HttpGet();
//        request.setURI(new URI(link));
//
//        HttpResponse response = client.execute(request);
//        //BufferedReader in = new BufferedReader       (new InputStreamReader(response.getEntity().getContent()));

//        actvInvite.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(hasFocus){
//                    actvInvite.setHint("");
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
//                    //actvInvite.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//                    //actvInvite.makeText(getApplicationContext(), "got the focus", Toast.LENGTH_LONG).show();
//
//                }else {
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);
//                    //actvInvite.setText("Invite");
//                    //Toast.makeText(getApplicationContext(), "lost the focus", Toast.LENGTH_LONG).show();
//                }
//            }
//        });


//    public void fetchContacts() {
//
//        String phoneNumber = null;
//        String email = null;
//
//        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
//        String _ID = ContactsContract.Contacts._ID;
//        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
//        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
//
//        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
//        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
//        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
//
//        Uri EmailCONTENT_URI =  ContactsContract.CommonDataKinds.Email.CONTENT_URI;
//        String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
//        String DATA = ContactsContract.CommonDataKinds.Email.DATA;
//
//        StringBuffer output = new StringBuffer();
//
//        ContentResolver contentResolver = getContentResolver();
//
//        Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);
//
//        // Loop for every contact in the phone
//        if (cursor.getCount() > 0) {
//
//            while (cursor.moveToNext()) {
//                String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
//                String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));
//
//                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));
//
//                if (hasPhoneNumber > 0) {
//
//                    c_Name.add(name);
//
//                    output.append("\n First Name:" + name);
//
//                    // Query and loop for every phone number of the contact
//                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);
//
//                    while (phoneCursor.moveToNext()) {
//                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
//                        output.append("\n Phone number:" + phoneNumber);
//                    }
//                    phoneCursor.close();
//
//                    // Query and loop for every email of the contact
//                    Cursor emailCursor = contentResolver.query(EmailCONTENT_URI,	null, EmailCONTACT_ID+ " = ?", new String[] { contact_id }, null);
//
//                    while (emailCursor.moveToNext()) {
//                        email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
//                        output.append("\nEmail:" + email);
//                    }
//                    emailCursor.close();
//                }
//                output.append("\n");
//            }
//            outputText.setText(output);
//            txtPhoneNo.setText(output);
//        }
//    }



//**** can use generic listener *****************
//public void onClick(View v) {
//    if(v==this.addButton){
//        this.addItems(this.editText1.getText().toString());
//    }
//}
//
//    public boolean onKey(View v, int keyCode, KeyEvent event) {
//        if (event.getAction()==KeyEvent.ACTION_DOWN &&
//                keyCode==KeyEvent.KEYCODE_DPAD_CENTER)
//            this.addItems(this. editText1.getText().toString());
//        return false;
//    }


//** dynamic add content to listview *****
///** Note that here we are inheriting ListActivity class instead of Activity class **/
//public class MainActivity extends ListActivity {
//
//    /** Items entered by the user is stored in this ArrayList variable */
//    ArrayList<String> list = new ArrayList<String>();
//
//    /** Declaring an ArrayAdapter to set items to ListView */
//    ArrayAdapter<String> adapter;
//
//    /** Called when the activity is first created. */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        /** Setting a custom layout for the list activity */
//        setContentView(R.layout.main);
//
//        /** Reference to the button of the layout main.xml */
//        Button btn = (Button) findViewById(R.id.btnAdd);
//
//        /** Defining the ArrayAdapter to set items to ListView */
//        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
//
//        /** Defining a click event listener for the button "Add" */
//        OnClickListener listener = new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                EditText edit = (EditText) findViewById(R.id.txtItem);
//                list.add(edit.getText().toString());
//                edit.setText("");
//                adapter.notifyDataSetChanged();
//            }
//        };
//
//        /** Setting the event listener for the add button */
//        btn.setOnClickListener(listener);
//
//        /** Setting the adapter to the ListView */
//        setListAdapter(adapter);
//    }
//}