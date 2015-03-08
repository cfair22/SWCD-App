package com.tuesday6.swcd_app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class StepsForStainActivity extends Activity {

    JSONParser jsonParser = new JSONParser();

    ProgressDialog progressDialog;

    HashMap <String, String> staindata = new HashMap<String, String>();

    String stain_id;

    Context context = this;

    private static final String URL_SINGLE_STAIN = "http://southwestcd.com/return_stain.php";

    //JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_STAINS = "stains";
    private static final String TAG_STAIN_NAME_DB = "stain_name_db";
    private static final String TAG_STAIN_ID = "stain_id";
    private static final String TAG_CARPET_HOW = "carpet_howto_db";
    private static final String TAG_CARPET_NOTES = "carpet_noes_db";
    private static final String TAG_TILE_HOW = "tile_howto_db";
    private static final String TAG_TILE_NOTES = "tile_notes_db";
    private static final String TAG_AREARUGS_HOW = "area_rugs_howto_db";
    private static final String TAG_AREARUGS_NOTES = "area_rugs_notes_db";
    private static final String TAG_UPHOLSTERY_HOW = "upholstery_howto_db";
    private static final String TAG_UPHOLSTERY_NOTES = "upholstery_notes_db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_for_stain);

        Bundle extras = getIntent().getExtras();
        stain_id = extras.getString("stain_id");

        new LoadSingleStain().execute();


    }

    class LoadSingleStain extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Loading Stain Details");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        protected String doInBackground(String... args){

            //updated UI from Background Thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int success;
                    try{
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("stain_id", stain_id));

                        //getting JSON string from URL
                        JSONObject json = jsonParser.makeHttpRequest(URL_SINGLE_STAIN, "GET", params);

                        //check your log cat for JSON response
                        Log.d("Single Stain Details: ", json.toString());

                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1){
                            //product found
                            //depending on the php code this might nor be neccessary.
                            JSONArray stains = json.getJSONArray(TAG_STAINS);

                            //if there is only one product returned by the php then you can make a direct json object from the php
                            JSONObject c = stains.getJSONObject(0);

                            //Storing each json item in variable
                            String id = c.getString(TAG_STAIN_ID);
                            String name = c.getString(TAG_STAIN_NAME_DB);
                            String carpet_how = c.getString(TAG_CARPET_HOW);
                            String carpet_notes = c.getString(TAG_CARPET_NOTES);
                            String tile_how = c.getString(TAG_TILE_HOW);
                            String tile_notes = c.getString(TAG_TILE_NOTES);
                            String area_rugs_how = c.getString(TAG_AREARUGS_HOW);
                            String area_rugs_notes = c.getString(TAG_AREARUGS_NOTES);
                            String upholstry_how = c.getString(TAG_UPHOLSTERY_HOW);
                            String upholstry_notes = c.getString(TAG_UPHOLSTERY_NOTES);

                            //add each stains info tag to the hashmap passed into this class when called.
                            staindata.put(TAG_STAIN_ID, id);
                            staindata.put(TAG_STAIN_NAME_DB, name);
                            staindata.put(TAG_CARPET_HOW, carpet_how);
                            staindata.put(TAG_CARPET_NOTES, carpet_notes);
                            staindata.put(TAG_TILE_HOW, tile_how);
                            staindata.put(TAG_TILE_NOTES, tile_notes);
                            staindata.put(TAG_AREARUGS_HOW, area_rugs_how);
                            staindata.put(TAG_AREARUGS_NOTES, area_rugs_notes);
                            staindata.put(TAG_UPHOLSTERY_HOW, upholstry_how);
                            staindata.put(TAG_UPHOLSTERY_NOTES, upholstry_notes);

                            Log.d("staindata: ", staindata.get("name"));
                        } else {
                            //no stain with id was found
                        }
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            });

            return null;
        }

        protected void onPostExecute(String file_url){
            if (progressDialog != null){
                progressDialog.dismiss();
                progressDialog = null;
            }
            runOnUiThread(new Runnable(){
                public void run(){
                    TextView stainId = (TextView) findViewById(R.id.stain_id);

                    stainId.setText("ID: " + staindata.get(TAG_STAIN_ID));
                }
                });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_steps_for_stain, menu);
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
