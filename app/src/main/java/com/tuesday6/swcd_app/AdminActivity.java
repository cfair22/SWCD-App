package com.tuesday6.swcd_app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*
    This class is used for adding stains to the database on the admin side
 */

public class AdminActivity extends Activity implements View.OnClickListener {

    //Make a json Parser object from json class
    JSONParser jsonParser = new JSONParser();

    //Declare all variables, all the editText's from XML layout
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
    Button newStain;

    //url to create new stain
    private static String urlNewStain = "http://southwestcd.com/add_stain.php";

    //JSON node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_STAIN_ID = "stain_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //activity_admin XML is used here
        setContentView(R.layout.activity_admin);

        //set variables to correct editText's on layout
        newStainName = (EditText) findViewById(R.id.admin_stain_name);
        newCarpetHowto = (EditText)findViewById(R.id.admin_carpet_howto);
        newCarpetNotes = (EditText) findViewById(R.id.admin_carpet_notes);
        newTileHowto = (EditText) findViewById(R.id.admin_tile_howto);
        newTileNotes = (EditText) findViewById(R.id.admin_tile_notes);
        newRugHowto = (EditText) findViewById(R.id.admin_rugs_howto);
        newRugNotes = (EditText) findViewById(R.id.admin_rugs_notes);
        newUpholsteryHowto = (EditText) findViewById(R.id.admin_upholstery_howto);
        newUpholsteryNotes = (EditText) findViewById(R.id.admin_upholstery_notes);

        //Initalize button and set a listener to the button
        newStain = (Button) findViewById(R.id.submit_stain);
        newStain.setOnClickListener(this);
    }

    // Listener action here, when button clicked run addStain class
    @Override
    public void onClick(View v){
        if(v.getId() == R.id.submit_stain){
            new AddStain().execute();
        }
    }

    // AddStain class runs the thread that adds a new stain to database
    class AddStain extends AsyncTask<String, String, String>{

        //PreExecute runs progress dialog so user knows what is going on
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(AdminActivity.this);
            progressDialog.setMessage("Adding New Stain");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        //doInBackground runs the function of the class
        protected String doInBackground(String... args){

            //Declare and initalize variables to get strings values from editText's on layout
            String stain_name_db = newStainName.getText().toString();
            String carpet_howto_db = newCarpetHowto.getText().toString();
            String carpet_notes_db = newCarpetNotes.getText().toString();
            String tile_howto_db = newTileHowto.getText().toString();
            String tile_notes_db = newTileNotes.getText().toString();
            String area_rugs_howto_db = newRugHowto.getText().toString();
            String area_rugs_notes_db = newRugNotes.getText().toString();
            String upholstery_howto_db = newUpholsteryHowto.getText().toString();
            String upholstery_notes_db = newUpholsteryNotes.getText().toString();

            //Building Parameters list to be sent to database
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("stain_name",stain_name_db ));
            params.add(new BasicNameValuePair("carpet_howto", carpet_howto_db));
            params.add(new BasicNameValuePair("carpet_notes", carpet_notes_db));
            params.add(new BasicNameValuePair("tile_howto", tile_howto_db));
            params.add(new BasicNameValuePair("tile_notes", tile_notes_db));
            params.add(new BasicNameValuePair("area_rugs_howto", area_rugs_howto_db));
            params.add(new BasicNameValuePair("area_rugs_notes", area_rugs_notes_db));
            params.add(new BasicNameValuePair("upholstery_howto", upholstery_howto_db));
            params.add(new BasicNameValuePair("upholstery_notes", upholstery_notes_db));

            //getting JSON object
            JSONObject jsonObject = jsonParser.makeHttpRequest(urlNewStain, "POST", params);

            //Logcat used for developer to see what data is sent to database
            Log.d("Create Stain", jsonObject.toString());

            //check for success tag
            try{

                //success is used in php to see if connection was successful
                int success = jsonObject.getInt(TAG_SUCCESS);
                String stain_id = jsonObject.getString(TAG_STAIN_ID);

                //if all data required was sent then perform action
                if(success == 1){

                    //New intent to sent user to steps for stain class
                    Intent intent = new Intent(getApplicationContext(), StepsForStainActivity.class);

                    //PutExtra is used to send which stain_id was just created and tells steps for
                    //stain activity class which stain should be displayed in layout
                    intent.putExtra(TAG_STAIN_ID, stain_id);
                    startActivityForResult(intent, 100);
                } else {
                    //Failed to create stain
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        //Post execute is used to remove the progress dialog
        protected void onPostExecute(String file_url){
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_admin, menu);
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
