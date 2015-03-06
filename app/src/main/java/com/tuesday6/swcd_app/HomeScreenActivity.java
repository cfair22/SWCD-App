package com.tuesday6.swcd_app;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
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


public class HomeScreenActivity extends ActionBarActivity {

    private ListView listView;
    private SearchView searchView;

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
    private static final String TAG_STAIN_NAME_ID = "stain_name_id";
    private static final String TAG_STAIN_ID = "stain_id";


    // products JSONArray
    JSONArray stains = null;

    //search key value
    public String searchkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Get listview
        listView = (ListView) findViewById(R.id.search_list_view);
        searchView = (SearchView) findViewById(R.id.search);

        Intent myIntent = getIntent();

        // gets the arguments from previously created intent
        searchkey = myIntent.getStringExtra("keyword");

        // Hashmap for ListView
        stainsList = new ArrayList<HashMap<String, String>>();

        // Loading idioms in Background Thread
        new LoadStains().execute();





        // on selecting single idioms
        // to do something
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String iid = ((TextView) view.findViewById(R.id.stain_id)).getText()
                        .toString();

            }
        });
    }


    /**
     * Background Async Task to Load Idioms by making HTTP Request
     * */
    class LoadStains extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(HomeScreenActivity.this);
            pDialog.setMessage("Loading Stains. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Idioms from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            //value captured from previous intent
            params.add(new BasicNameValuePair("keyword", searchkey));

            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_search, "GET", params);

            // Check your log cat for JSON response
            Log.d("Search idioms: ", json.toString());

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
                        String stain_name = c.getString(TAG_STAIN_NAME_ID);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_STAIN_ID, id);
                        map.put(TAG_STAIN_NAME_ID, stain_name);

                        // adding HashList to ArrayList
                        stainsList.add(map);
                    }
                } else {
                    // no idioms found
                    //do something
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //return "success";
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting the related idioms
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                    * Updating parsed JSON data into ListView
                    * */
                    ListAdapter adapter = new SimpleAdapter(
                            HomeScreenActivity.this, stainsList,
                            R.layout.activity_home_screen, new String[]{TAG_STAIN_ID, TAG_STAIN_NAME_ID},
                            new int[]{R.id.stain_id, R.id.stain_name_id});

                    // updating listview
                    listView.setAdapter(adapter);
                }
            });

        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home_screen, menu);


        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
