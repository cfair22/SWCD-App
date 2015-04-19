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


public class Login extends Activity implements View.OnClickListener {


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

        passwordEditText = (EditText) findViewById(R.id.admin_login_editText);
        loginButton = (Button)findViewById(R.id.admin_login_at_login);
        loginButton.setOnClickListener(this);

        databaseMessage = (TextView) findViewById(R.id.admin_login_database_message);
        databaseMessage.setText("Login failed");
        databaseMessage.setVisibility(View.INVISIBLE);

        loginCounter = 0;
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
                    Intent intent = new Intent(Login.this, AdminPanel.class);
                    startActivity(intent);
                } else{
                    loginCounter++;
                    message = jsonObject.getString(TAG_MESSAGE);
                    loginStatus = false;


                   System.out.println("Attempt was not successful");
                   progressDialog.dismiss();


                }

            } catch (JSONException e){
                e.printStackTrace();
            }

            return null;
        }
        protected void onPostExecute(String result){
            //super.onPostExecute(result);

            if (progressDialog != null){
                progressDialog.dismiss();
                progressDialog = null;
            }

            if (!loginStatus){
                UpdateUi(true);
            }
        }
    }

    public void AppExit()
    {
        this.finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    /*int pid = android.os.Process.myPid();=====> use this if you want to kill your activity. But its not a good one to do.
    android.os.Process.killProcess(pid);*/

    }




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
