package com.tuesday6.swcd_app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
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
import android.widget.Button;
import android.widget.EditText;
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


public class HomeScreenActivity extends Activity implements View.OnClickListener {

    private Button searchButton;
    private Button loginButton;
    private EditText editSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        editSearch = (EditText)findViewById(R.id.edit_search);
        searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(this);

        loginButton = (Button) findViewById(R.id.textView2);
        loginButton.setOnClickListener(this);

        System.out.println(SWCDApp.databaseMessage);
        SWCDApp.databaseMessage = "Hello";
        System.out.println(SWCDApp.databaseMessage);
    }

    @Override
    public void onClick(View v){
        if (v.getId() == R.id.search_button){
            Intent searchIntent = new Intent(HomeScreenActivity.this, ListResult.class);
            searchIntent.putExtra("keyword", editSearch.getText().toString());
            startActivity(searchIntent);
        }

        if (v.getId() == R.id.textView2){
            Intent loginIntent = new Intent(HomeScreenActivity.this, Login.class);
            startActivity(loginIntent);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home_screen, menu);

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
