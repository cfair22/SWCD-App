package com.tuesday6.swcd_app;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ListResult extends ListActivity {

    TextView databaseMessage;

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> stainsList;

    // url to get the idiom list
    private static String url_search = "http://southwestcd.com/search.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_STAINS = "stains";
    private static final String TAG_STAIN_NAME_DB = "stain_name_db";
    private static final String TAG_STAIN_ID = "stain_id";

    // products JSONArray
    JSONArray stains = null;
    //search key value
    public String searchkey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_result);
        Intent myIntent = getIntent();

        SWCDApp.stainFound = true;
        Bundle extras = getIntent().getExtras();
        searchkey = extras.getString("keyword");

        stainsList = new ArrayList<HashMap<String, String>>();

        databaseMessage = (TextView) findViewById(R.id.admin_database_message);
        databaseMessage.setVisibility(View.INVISIBLE);

        new LoadStains().execute();

        ListView lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String iid = ((TextView) view.findViewById(R.id.stain_id)).getText().toString();
                System.out.println("The id click is " + iid);
                //Starting new intent
                Intent intent = new Intent(getApplicationContext(), StepsForStainActivity.class);
                intent.putExtra(TAG_STAIN_ID, iid);
                startActivityForResult(intent, 100);
            }
        });
    }

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

    class LoadStains extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ListResult.this);
            pDialog.setMessage("Loading Stains. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            //value captured from previous intent
            params.add(new BasicNameValuePair("keyword", searchkey));

            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_search, "GET", params);

            // Check your log cat for JSON response
            Log.d("Search Stains: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    stains = json.getJSONArray(TAG_STAINS);

                    // looping through All Products
                    for (int i = 0; i < stains.length(); i++) {
                        JSONObject c = stains.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_STAIN_ID);
                        String stain_name = c.getString(TAG_STAIN_NAME_DB);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_STAIN_ID, id);
                        map.put(TAG_STAIN_NAME_DB, stain_name);

                        // adding HashList to ArrayList
                        stainsList.add(map);
                    }
                } else {
                    SWCDApp.stainFound = false;


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //return "success";
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * *
         */
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting the related idioms
            if (pDialog != null) {
                pDialog.dismiss();
                pDialog = null;
            }
            if (!SWCDApp.stainFound){
                databaseMessage.setVisibility(View.VISIBLE);
                databaseMessage.setText(SWCDApp.noStainResult);
            }
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter;
                    adapter = new SimpleAdapter(
                            ListResult.this, stainsList,
                            R.layout.list_view, new String[]{TAG_STAIN_ID, TAG_STAIN_NAME_DB},
                            new int[]{R.id.stain_id, R.id.stain_name_id});

                    // updating listview
                    setListAdapter(adapter);
                }
            });

        }
    }
}
