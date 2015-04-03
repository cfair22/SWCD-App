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


public class AdminActivity extends Activity implements View.OnClickListener {

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
    Button newStain;

    //url to create new stain
    private static String urlNewStain = "http://southwestcd.com/add_stain.php";

    //JSON node names
    private static final String TAG_SUCCESS = "success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        newStainName = (EditText) findViewById(R.id.admin_stain_name);
        newCarpetHowto = (EditText)findViewById(R.id.admin_carpet_howto);
        newCarpetNotes = (EditText) findViewById(R.id.admin_carpet_notes);
        newTileHowto = (EditText) findViewById(R.id.admin_tile_howto);
        newTileNotes = (EditText) findViewById(R.id.admin_tile_notes);
        newRugHowto = (EditText) findViewById(R.id.admin_rugs_howto);
        newRugNotes = (EditText) findViewById(R.id.admin_rugs_notes);
        newUpholsteryHowto = (EditText) findViewById(R.id.admin_upholstery_howto);
        newUpholsteryNotes = (EditText) findViewById(R.id.admin_upholstery_notes);

        newStain = (Button) findViewById(R.id.submit_stain);
        newStain.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        if(v.getId() == R.id.submit_stain){
            new AddStain().execute();
        }
    }

    class AddStain extends AsyncTask<String, String, String>{
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(AdminActivity.this);
            progressDialog.setMessage("Adding New Stain");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        protected String doInBackground(String... args){
            String stain_name_db = newStainName.getText().toString();
            String carpet_howto_db = newCarpetHowto.getText().toString();
            String carpet_notes_db = newCarpetNotes.getText().toString();
            String tile_howto_db = newTileHowto.getText().toString();
            String tile_notes_db = newTileNotes.getText().toString();
            String area_rugs_howto_db = newRugHowto.getText().toString();
            String area_rugs_notes_db = newRugNotes.getText().toString();
            String upholstery_howto_db = newUpholsteryHowto.getText().toString();
            String upholstery_notes_db = newUpholsteryNotes.getText().toString();

            //Building Parameters
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

            Log.d("Create Stain", jsonObject.toString());

            //check for success tag
            try{
                int success = jsonObject.getInt(TAG_SUCCESS);

                if(success == 1){
                    Intent intent = new Intent(AdminActivity.this, StepsForStainActivity.class);
                    intent.putExtra("stain_name_db", newStainName.getText().toString());
                    startActivity(intent);
                } else {
                    System.out.println("Failed to create stain");
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

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
