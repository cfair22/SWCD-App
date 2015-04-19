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
 * Created by cfair_000 on 4/4/2015.
 */
public class Admin_ListAllStains extends ListActivity{

    TextView AdminDatabaseMessage;
    String message;
    private ProgressDialog progressDialog;

    JSONParser jsonParser = new JSONParser();

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
        setContentView(R.layout.activity_list_result);

        //Locate TextView for database Message
        AdminDatabaseMessage = (TextView) findViewById(R.id.admin_database_message);
//        Intent newintent = getIntent();
//        message = newintent.getExtras().getString("message");
//        AdminDatabaseMessage.setText(message);



        //Hashmap for ListView
        stainList = new ArrayList<HashMap<String, String>>();

        new LoadAllStains().execute();

        //get Listview
        ListView listView = getListView();

        //on selecting single stain launch edit stain activity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String iid = ((TextView) view.findViewById(R.id.stain_id)).getText().toString();
                System.out.println("The id click is " + iid);
                //Starting new intent
                Intent intent = new Intent(getApplicationContext(), AdminEditStain.class);
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
            //intent.getExtras();

            finish();

            startActivity(intent);

//            //Get message from Intent
//            Bundle extras = getIntent().getExtras();
//            message = extras.getString("message");


        }

    }

    //Background Async task to load all stains
    class LoadAllStains extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(Admin_ListAllStains.this);
            progressDialog.setMessage("Loading Stains... Please Wait");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        //Getting all stains from url
        protected String doInBackground(String... args){
            //Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            //getting JSON string from URL
            JSONObject jsonObject = jsonParser.makeHttpRequest(url_List_Stains, "GET", params);

            Log.d("All Stains: ", jsonObject.toString());

            try{
                int success = jsonObject.getInt(TAG_SUCCESS);

                if (success == 1){
                    //Stains were found!
                    stains = jsonObject.getJSONArray(TAG_STAINS);

                    //looping through all stains
                    for (int i = 0; i < stains.length(); i++){
                        JSONObject c = stains.getJSONObject(i);

                        //Storing each json item in variable
                        String id = c.getString(TAG_STAIN_ID);
                        String name = c.getString(TAG_STAIN_NAME);

                        //create new hash map
                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(TAG_STAIN_ID, id);
                        map.put(TAG_STAIN_NAME, name);

                        stainList.add(map);
                    }
                } else {
                    // no stains found in database
                    System.out.println("Couldn't find any stains. OH SHIT!");
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }
        //After completeing backgorund task dismiss the progress dialog
        protected void onPostExecute(String file_url){
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }

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
