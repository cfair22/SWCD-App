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
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
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

import javax.xml.transform.Result;

//Login class used to handle login of admin
public class Login extends Activity implements View.OnClickListener {

    //declare variables
    private Button loginButton;
    private EditText passwordEditText;
    public boolean loginStatus = true;
    private TextView databaseMessage;
    private String message;
    private int loginCounter;
    public boolean exit;

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

        //initialize edit text and button for login and set listener to it
        passwordEditText = (EditText) findViewById(R.id.admin_login_editText);
        loginButton = (Button)findViewById(R.id.admin_login_at_login);
        loginButton.setOnClickListener(this);

        //initialize database message for user feedback
        databaseMessage = (TextView) findViewById(R.id.admin_login_database_message);
        databaseMessage.setText("Login failed");
        databaseMessage.setVisibility(View.INVISIBLE);

        //variable used to record the amount of failed attempts to login to admin
        loginCounter = 0;
    }

    //Handler for login button
    @Override
    public void onClick(View v){
        if (v.getId() == R.id.admin_login_at_login){
            new AdminLogin().execute();
        }
    }

    //AdminLogin class handles the function of the class
    class AdminLogin extends AsyncTask<String, String, String>{

        //PreExecute loads and runs progress dialog
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

            //password variable gets password entered by user
            String password = passwordEditText.getText().toString();


            try{
                //Building parameteres
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("password", password));

                //Logcat for developer
                Log.d("Attempt Login...", jsonParser.toString());

                //JsonObject for sending password to database and check it against database value
                JSONObject jsonObject = jsonParser.makeHttpRequest(urlLogin, "GET", params);

                Log.d("Login Attempt", jsonObject.toString());
                success = jsonObject.getInt(TAG_SUCCESS);

                if (success == 1){
                    Log.d("Login Sucessful", jsonObject.toString());

                    //Intent to send user to adminpanel when password matches
                    Intent intent = new Intent(Login.this, AdminPanel.class);
                    startActivity(intent);
                } else{

                    //login counter used to track number of failed attempts
                    loginCounter++;

                    //message received from database
                    message = jsonObject.getString(TAG_MESSAGE);

                    loginStatus = false;
                   progressDialog.dismiss();


                }

            } catch (JSONException e){
                e.printStackTrace();
            }

            return null;
        }

        //Post execute used for dismiss progress dialog
        protected void onPostExecute(String result){
            //super.onPostExecute(result);

            if (progressDialog != null){
                progressDialog.dismiss();
                progressDialog = null;
            }

            //if user entered wrong password then update UI with message
            if (!loginStatus){
                UpdateUi(true);
            }
        }
    }

    //Method used to kick user out of app after so many failed attempts to login
    public void AppExit()
    {
        this.finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //Method used to show user of failed attempt and display remaining login attempts before app closes
    public void UpdateUi(boolean status){
        if (status){
            databaseMessage.setText(message + " You have " + (3-loginCounter) + " more attempts before App Closes");
            databaseMessage.setVisibility(View.VISIBLE);
        }
        if (loginCounter > 3){
            AppExit();
            exit = true;
            System.exit(1);
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
