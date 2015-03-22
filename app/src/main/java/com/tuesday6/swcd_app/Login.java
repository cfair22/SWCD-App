package com.tuesday6.swcd_app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.List;


public class Login extends Activity implements View.OnClickListener {

    private Button loginButton;
    private EditText passwordEditText;

    //progress Dialog
    private ProgressDialog progressDialog;

    //JSON parser class
    JSONParser jsonParser = new JSONParser();

    //url to login.php
    private static final String urlLogin = "http://southwestcd.com/login.php";

    //JSON names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PASSWORD = "password";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        passwordEditText = (EditText) findViewById(R.id.admin_login_editText);

        loginButton = (Button)findViewById(R.id.admin_login_at_login);
        loginButton.setOnClickListener(this);


    }

    @Override
    public void onClick(View v){
        if (v.getId() == R.id.admin_login_at_login){
            new AdminLogin().execute();
        }
    }


    class AdminLogin extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(Login.this);
            progressDialog.setMessage("Attempting Login..");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        protected String doInBackground(String... args){
            //Check for success tag
            int success;
            String password = passwordEditText.getText().toString();


            try{
                //Building parameteres
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("password", password));

                Log.d("Attempt Login...", jsonParser.toString());

                JSONObject jsonObject = jsonParser.makeHttpRequest(urlLogin, "GET", params);

                System.out.println("Json parser has made http request");

                Log.d("Login Attempt", jsonObject.toString());
                success = jsonObject.getInt(TAG_SUCCESS);
                if (success == 1){
                    System.out.println("Login was sucessful");
                    Log.d("Login Sucessful", jsonObject.toString());

                    //Intent to send password
                    Intent intent = new Intent(Login.this, AdminActivity.class);
                    startActivity(intent);
                } else{
                   System.out.println("Attempt was not successful");
                   progressDialog.dismiss();


                }

            } catch (JSONException e){
                e.printStackTrace();
            }

            return null;
        }
    }

    protected void onPostExecute(String file_url){
        //loginMessage.setText("HEllo WOrld");

        if (progressDialog != null){
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
