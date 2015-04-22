package com.tuesday6.swcd_app;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Carl on 4/4/2015.
 * This class is used for the admin panel
 * it uses return_all_stains.php. The purpose
 * is the return all stains in database to be used
 * for edit/delete stain in the admin panel
 */

//Extends ListActivity to be displayed a listview
public class Admin_ListAllStains extends ListActivity{

    //TextView for displaying delete stain message from php
    TextView AdminDatabaseMessage;

    //Progress Dialog for displaying AsyncTask activity
    private ProgressDialog progressDialog;

    //Declare new jsonParser to handle json from php
    JSONParser jsonParser = new JSONParser();

    //ArrayList for stains to be filled from Json array
    //this is what will be used to fill the listview
    ArrayList<HashMap<String, String>> stainList;

    //url to get all stains in database
    private static String url_List_Stains = "http://southwestcd.com/return_all_stain.php";

    //JSON node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_STAINS = "stains";
    private static final String TAG_STAIN_ID = "stain_id";
    private static final String TAG_STAIN_NAME = "stain_name_db";

    //stain JSONArray
    JSONArray stains = null;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Use List Result xml as layout, which is listview
        setContentView(R.layout.activity_list_result);

        //Locate TextView for database Message
        AdminDatabaseMessage = (TextView) findViewById(R.id.admin_database_message);

        //Make TextView invisible and show only when admin deletes a stain
        AdminDatabaseMessage.setVisibility(View.INVISIBLE);

        //This is a global variable used when admin deletes stain
        //then the TextView is shown once boolean is set to true
        if (SWCDApp.isDeleted){
            AdminDatabaseMessage.setVisibility(View.VISIBLE);
            AdminDatabaseMessage.setText(SWCDApp.databaseMessage);
        }

        //Hashmap for ListView
        stainList = new ArrayList<HashMap<String, String>>();

        //Calls the class that Loads all stains from database
        new LoadAllStains().execute();

        //get Listview
        ListView listView = getListView();

        //on selecting single stain launch edit stain activity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //This variable is used to tell the php which stain was selected from the listview
                String iid = ((TextView) view.findViewById(R.id.stain_id)).getText().toString();

                //Start new intent to be sent to edit stain class
                Intent intent = new Intent(getApplicationContext(), AdminEditStain.class);

                //putExtra is used to send the stain id with the intent so the class knows which
                //stain was selected from the listview
                intent.putExtra(TAG_STAIN_ID, iid);
                startActivityForResult(intent, 100);
            }
        });
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

    //Background Async task to load all stains
    class LoadAllStains extends AsyncTask<String, String, String>{

        //PreExecute is used to display the progress dialog to tell user of what is happening
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(Admin_ListAllStains.this);
            progressDialog.setMessage("Loading Stains... Please Wait");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        //Getting all stains from PHP
        protected String doInBackground(String... args){

            //Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            //getting JSON string from URL and put into param list
            JSONObject jsonObject = jsonParser.makeHttpRequest(url_List_Stains, "GET", params);

            //Logcat displays jsonObects for developer to see what data is being sent/received
            Log.d("All Stains: ", jsonObject.toString());

            try{

                //success is sent from PHP so see if action was successful
                int success = jsonObject.getInt(TAG_SUCCESS);

                //Stains were found
                if (success == 1){

                    //put json array from PHP into arraylist declared above
                    stains = jsonObject.getJSONArray(TAG_STAINS);

                    //looping through all stains
                    for (int i = 0; i < stains.length(); i++){

                        //each object of array
                        JSONObject c = stains.getJSONObject(i);

                        //Storing each json item in variable
                        String id = c.getString(TAG_STAIN_ID);
                        String name = c.getString(TAG_STAIN_NAME);

                        //create new hash map
                        HashMap<String, String> map = new HashMap<String, String>();

                        //fill hashmap with stain data
                        map.put(TAG_STAIN_ID, id);
                        map.put(TAG_STAIN_NAME, name);

                        //then add map to stainlist for filling listview
                        stainList.add(map);
                    }
                } else {
                    // no stains found in database
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }
        //After completing background task dismiss the progress dialog
        protected void onPostExecute(String file_url){
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }

            //Thread used to fill listview with simpleAdapter, uses stainList arrayList
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ListAdapter adapter;
                    adapter = new SimpleAdapter(
                            Admin_ListAllStains.this, stainList,
                            R.layout.list_view, new String[]{TAG_STAIN_ID, TAG_STAIN_NAME},
                            new int[]{R.id.stain_id, R.id.stain_name_id});

                    // updating listview
                    setListAdapter(adapter);
                }
            });
        }
    }
}
