package com.tuesday6.swcd_app;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AdminEditStain extends Activity implements View.OnClickListener {

    JSONParser jsonParser = new JSONParser();

    ProgressDialog progressDialog;
    EditText newStainName;
    EditText newCarpetHowto;
    EditText newCarpetNotes;
    EditText newTileHowto;
    EditText newTileNotes;
    EditText newRugHowto;
    EditText newRugNotes;
    EditText newUpholsteryHowto;
    EditText newUpholsteryNotes;
    Button editStain;
    Button deleteStain;

    String stain_id;
    //url to edit existing stain
    private static String urlEditStain = "http://southwestcd.com/update_stain.php";

    //url to load stain
    private static String urlLoadStain = "http://southwestcd.com/return_stain.php";

    //url to delete existing stain
    private static String urlDeleteStain = "http://southwestcd.com/delete_stain.php";

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

    HashMap<String, String> staindata = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_stain);

        //Getting stain_id from intent
        Intent intent = getIntent();
        stain_id = intent.getStringExtra(TAG_STAIN_ID);
        //For testing purposes the above line is commented delete the line below




        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Call load Stain method
        new LoadStain().execute();

        editStain = (Button) findViewById(R.id.admin_edit_stain);
        editStain.setOnClickListener(this);

        deleteStain = (Button) findViewById(R.id.admin_delete_stain_button);
        deleteStain.setOnClickListener(this);

    }

    @Override
    public void onClick(View v){
        if(v.getId() == R.id.admin_edit_stain){
           new EditStain().execute();
        }
        if (v.getId() == R.id.admin_delete_stain_button){
            new DeleteStain().execute();
        }
    }

    class LoadStain extends AsyncTask<String, String, String> {
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(AdminEditStain.this);
            progressDialog.setMessage("Updating Stain");
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
                        JSONObject jsonObject = jsonParser.makeHttpRequest(urlLoadStain, "GET", params);
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
                    //stainName = (TextView) findViewById(R.id.stain_name);

                    //Getting variables of EDitText's on edit_stain
                    newStainName = (EditText) findViewById(R.id.admin_edit_stain_name);
                    newCarpetHowto = (EditText)findViewById(R.id.admin_edit_carpet_howto);
                    newCarpetNotes = (EditText) findViewById(R.id.admin_edit_carpet_notes);
                    newTileHowto = (EditText) findViewById(R.id.admin_edit_tile_howto);
                    newTileNotes = (EditText) findViewById(R.id.admin_edit_tile_notes);
                    newRugHowto = (EditText) findViewById(R.id.admin_edit_rugs_howto);
                    newRugNotes = (EditText) findViewById(R.id.admin_edit_rugs_notes);
                    newUpholsteryHowto = (EditText) findViewById(R.id.admin_edit_upholstery_howto);
                    newUpholsteryNotes = (EditText) findViewById(R.id.admin_edit_upholstery_notes);


                    newStainName.setText(staindata.get(TAG_STAIN_NAME));
                    newCarpetHowto.setText(staindata.get(TAG_CARPET_HOW));
                    newCarpetNotes.setText(staindata.get(TAG_CARPET_NOTES));
                    newTileHowto.setText(staindata.get(TAG_TILE_HOW));
                    newTileNotes.setText(staindata.get(TAG_TILE_NOTES));
                    newRugHowto.setText(staindata.get(TAG_AREARUGS_HOW));
                    newRugNotes.setText(staindata.get(TAG_AREARUGS_NOTES));
                    newUpholsteryHowto.setText(staindata.get(TAG_UPHOLSTERY_HOW));
                    newUpholsteryNotes.setText(staindata.get(TAG_UPHOLSTERY_NOTES));
                }
            });
        }

    }

    //Background Async Task to update stain
    class EditStain extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(AdminEditStain.this);
            progressDialog.setMessage("Updating Stain");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        //Updating Stain
        protected String doInBackground(String... args){

            System.out.println("The stain id is " + stain_id);


            // getting updated data from editTexts
            String stainName = newStainName.getText().toString();
            String carpetHowto = newCarpetHowto.getText().toString();
            String carpetNotes = newCarpetNotes.getText().toString();
            String tileHowto = newTileHowto.getText().toString();
            String tileNotes = newTileNotes.getText().toString();
            String rugsHowto = newRugHowto.getText().toString();
            String rugsNotes = newRugNotes.getText().toString();
            String upHowto = newUpholsteryHowto.getText().toString();
            String upNotes = newUpholsteryNotes.getText().toString();

            //BUilding parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_STAIN_ID, stain_id));
            params.add(new BasicNameValuePair(TAG_STAIN_NAME, stainName));
            params.add(new BasicNameValuePair(TAG_CARPET_HOW, carpetHowto));
            params.add(new BasicNameValuePair(TAG_CARPET_NOTES, carpetNotes));
            params.add(new BasicNameValuePair(TAG_TILE_HOW, tileHowto));
            params.add(new BasicNameValuePair(TAG_TILE_NOTES, tileNotes));
            params.add(new BasicNameValuePair(TAG_AREARUGS_HOW, rugsHowto));
            params.add(new BasicNameValuePair(TAG_AREARUGS_NOTES, rugsNotes));
            params.add(new BasicNameValuePair(TAG_UPHOLSTERY_HOW, upHowto));
            params.add(new BasicNameValuePair(TAG_UPHOLSTERY_NOTES, upNotes));

            //sending updated data through http request
            JSONObject jsonObject = jsonParser.makeHttpRequest(urlEditStain, "POST", params);

            try{
                int success = jsonObject.getInt(TAG_SUCCESS);

                if(success == 1){
                    Intent intent = getIntent();
                    setResult(100, intent);
                    finish();
                } else {
                    // Failed tp update stain
                    System.out.println("Failed to update stain");
                    Log.d("Failure", "failure");
                    Log.d("Message", jsonObject.getString("message"));
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url){
            if (progressDialog != null){
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }

    //Background Async task to Delete Product
    class DeleteStain extends AsyncTask<String, String, String>{
        // show progress dialog
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(AdminEditStain.this);
            progressDialog.setMessage("Deleting Stain...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        //test testest
        //Deleting Stain
        protected String doInBackground(String... args){
            //check for success tag
            int success;
            System.out.println("The stain id is " + stain_id);
            try{
                //Building parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("stain_id", stain_id));

                //getting stain details
                JSONObject jsonObject = jsonParser.makeHttpRequest(urlDeleteStain, "GET", params);

                Log.d("Delete Stain!", jsonObject.toString());

                success = jsonObject.getInt(TAG_SUCCESS);
                if (success == 1){
                    //stain deleted
                    Intent intent = getIntent();
                    setResult(100, intent);
                    finish();
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url){
            if (progressDialog != null){
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_admin_edit_stain, menu);
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
