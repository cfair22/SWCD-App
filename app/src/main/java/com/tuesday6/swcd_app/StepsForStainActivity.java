package com.tuesday6.swcd_app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class StepsForStainActivity extends Activity implements View.OnClickListener {

    // Declare layouts
    TextView stainName;
    Button tile;
    Button carpet;
    Button rug;
    Button upholstery;
    TextView howto;
    TextView notes;
    Button backToAdminPanel;

    JSONParser jsonParser = new JSONParser();

    ProgressDialog progressDialog;

    HashMap <String, String> staindata = new HashMap<String, String>();

    String stain_id;

    Context context = this;

    private static final String URL_SINGLE_STAIN = "http://southwestcd.com/return_stain.php";
    //private static final String url_stain = "http://southwestcd.com/return_stain.php?stain_id=32";

    //JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_STAINS = "stains";
    private static final String TAG_STAIN_NAME = "stain_name_db";
    private static final String TAG_STAIN_ID = "stain_id";
    private static final String TAG_CARPET_HOW = "carpet_howto_db";
    private static final String TAG_CARPET_NOTES = "carpet_notes_db";
    private static final String TAG_TILE_HOW = "tile_howto_db";
    private static final String TAG_TILE_NOTES = "tile_notes_db";
    private static final String TAG_AREARUGS_HOW = "area_rugs_howto_db";
    private static final String TAG_AREARUGS_NOTES = "area_rugs_notes_db";
    private static final String TAG_UPHOLSTERY_HOW = "upholstery_howto_db";
    private static final String TAG_UPHOLSTERY_NOTES = "upholstery_notes_db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_steps_for_stain);

        Bundle extras = getIntent().getExtras();
        stain_id = extras.getString("stain_id");

        policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        new LoadSingleStain().execute();

        tile = (Button) findViewById(R.id.tile_button);
        carpet = (Button) findViewById(R.id.carpet_button);
        rug = (Button) findViewById(R.id.rug_button);
        upholstery = (Button) findViewById(R.id.upholstery_button);

        tile.setOnClickListener(this);
        carpet.setOnClickListener(this);
        rug.setOnClickListener(this);
        upholstery.setOnClickListener(this);

        howto = (TextView) findViewById(R.id.how_to);
        notes = (TextView) findViewById(R.id.notes);

        SWCDApp.stainFound = true;
        backToAdminPanel = (Button) findViewById(R.id.BackToAdminHOme);
        backToAdminPanel.setVisibility(View.INVISIBLE);

        if (SWCDApp.isLoggedIn){
            backToAdminPanel.setVisibility(View.VISIBLE);
            backToAdminPanel.setOnClickListener(this);
        }


    }

    @Override
    public void onClick(View v){
        if (v.getId() == R.id.tile_button){
            howto.setText(staindata.get(TAG_TILE_HOW));
            notes.setText(staindata.get(TAG_TILE_NOTES));
        }
        if (v.getId() == R.id.carpet_button){
            howto.setText(staindata.get(TAG_CARPET_HOW));
            notes.setText(staindata.get(TAG_CARPET_NOTES));
        }
        if (v.getId() == R.id.rug_button){
            howto.setText(staindata.get(TAG_AREARUGS_HOW));
            notes.setText(staindata.get(TAG_AREARUGS_NOTES));
        }
        if (v.getId() == R.id.upholstery_button){
            howto.setText(staindata.get(TAG_UPHOLSTERY_HOW));
            notes.setText(staindata.get(TAG_UPHOLSTERY_NOTES));
        }
        if (v.getId() == R.id.BackToAdminHOme){
            Intent backToAdmin = new Intent(StepsForStainActivity.this, AdminPanel.class);
            startActivity(backToAdmin);
        }
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
                        System.out.println("The id should be 81: it is " + stain_id);
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("stain_id", stain_id));

                        //getting JSON string from URL
                        JSONObject jsonObject = jsonParser.makeHttpRequest(URL_SINGLE_STAIN, "GET", params);
                        //JSONObject jsonObject1 = jsonParser.getJSONFromUrl(url_stain);


                        //check your log cat for JSON response
                        Log.d("Single Stain Details: ", jsonObject.toString());

                        success = jsonObject.getInt(TAG_SUCCESS);
                        if (success == 1){
                            //product found
                            //depending on the php code this might nor be neccessary.
                            JSONArray stains_one = jsonObject.getJSONArray(TAG_STAINS);

                            //if there is only one product returned by the php then you can make a direct json object from the php
                            JSONObject c = stains_one.getJSONObject(0);



                            //Storing each json item in variable
                            // each if statement blocks against null values

                            String id;
                            String name;
                            String carpet_how;
                            String carpet_notes;
                            String tile_how;
                            String tile_notes;
                            String area_rugs_how;
                            String area_rugs_notes;
                            String upholstry_how;
                            String upholstry_notes;

                            if (c.getString(TAG_STAIN_ID) != null){
                                id = c.getString(TAG_STAIN_ID);
                            } else {
                                id = " ";
                            }
                            if (c.getString(TAG_STAIN_NAME) != null){
                                name = c.getString(TAG_STAIN_NAME);
                            } else {
                                name = " ";
                            }
                            if (c.getString(TAG_CARPET_HOW) != null){
                                carpet_how = c.getString(TAG_CARPET_HOW);
                            } else {
                                carpet_how = " ";
                            }
                            if (c.has(TAG_CARPET_NOTES)){
                                carpet_notes = c.getString(TAG_CARPET_NOTES);
                            } else {
                                carpet_notes = " ";
                            }
                            if (c.getString(TAG_TILE_HOW) != null){
                                tile_how = c.getString(TAG_TILE_HOW);
                            } else {
                                tile_how = " ";
                            }
                            if (c.getString(TAG_TILE_NOTES) != null){
                                tile_notes = c.getString(TAG_TILE_NOTES);
                            } else {
                                tile_notes = " ";
                            }
                            if (c.getString(TAG_AREARUGS_HOW) != null){
                                area_rugs_how = c.getString(TAG_AREARUGS_HOW);
                            } else {
                                area_rugs_how = " ";
                            }
                            if (c.getString(TAG_AREARUGS_NOTES) != null){
                                area_rugs_notes = c.getString(TAG_AREARUGS_NOTES);
                            } else {
                                area_rugs_notes = " ";
                            }
                            if (c.getString(TAG_UPHOLSTERY_HOW) != null){
                                upholstry_how = c.getString(TAG_UPHOLSTERY_HOW);
                            } else {
                                upholstry_how = " ";
                            }
                            if (c.getString(TAG_UPHOLSTERY_NOTES) != null){
                                upholstry_notes = c.getString(TAG_UPHOLSTERY_NOTES);
                            } else {
                                upholstry_notes = " ";
                            }

                            //add each stains info tag to the hashmap passed into this class when called.
                            staindata.put(TAG_STAIN_ID, id);
                            staindata.put(TAG_STAIN_NAME, name);
                            staindata.put(TAG_CARPET_HOW, carpet_how);
                            staindata.put(TAG_CARPET_NOTES, carpet_notes);
                            staindata.put(TAG_TILE_HOW, tile_how);
                            staindata.put(TAG_TILE_NOTES, tile_notes);
                            staindata.put(TAG_AREARUGS_HOW, area_rugs_how);
                            staindata.put(TAG_AREARUGS_NOTES, area_rugs_notes);
                            staindata.put(TAG_UPHOLSTERY_HOW, upholstry_how);
                            staindata.put(TAG_UPHOLSTERY_NOTES, upholstry_notes);


                        } else {
                            //no stain with id was found
                        }
                    } catch (JSONException e){
                        e.printStackTrace();
                    } catch(Exception e1){
                        e1.printStackTrace();
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
                    stainName = (TextView) findViewById(R.id.stain_name);



                    System.out.println("Stain name = " + staindata.get(TAG_STAIN_NAME));

                    stainName.setText("Stain Name: " + staindata.get(TAG_STAIN_NAME));
                    howto.setText(staindata.get(TAG_CARPET_HOW));
                    notes.setText(staindata.get(TAG_CARPET_NOTES));



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
