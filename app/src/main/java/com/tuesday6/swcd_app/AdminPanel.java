package com.tuesday6.swcd_app;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

//Admin panel is used to hold the buttons for add stain and edit/delete stain
public class AdminPanel extends Activity implements View.OnClickListener {

    //declare buttons
    Button addStain;
    Button editStain;
    Button deleteStain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        //Global variables
        SWCDApp.isDeleted = false;
        SWCDApp.isLoggedIn = true;

        //initialize button and set a Listener to them
        addStain = (Button) findViewById(R.id.admin_panel_addStain_button);
        addStain.setOnClickListener(this);

        //initialize button and set a Listener to them
        editStain = (Button) findViewById(R.id.admin_panel_editStain_button);
        editStain.setOnClickListener(this);
    }

    //Listener handler, for add stain and edit stain
    @Override
    public void onClick(View v){
        if (v.getId() == R.id.admin_panel_addStain_button){
            //Add Stain Intent
            Intent loginIntent = new Intent(AdminPanel.this, AdminActivity.class);
            startActivity(loginIntent);
        }
        if (v.getId() == R.id.admin_panel_editStain_button){
            //Edit Stain Intent
            Intent editIntent = new Intent(AdminPanel.this, Admin_ListAllStains.class);
            startActivity(editIntent);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_admin_panel, menu);
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
