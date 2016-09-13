package com.example.misho.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class Menu extends AppCompatActivity {
    private String firstname,lastname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_menu);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    public void search(View v){
        Intent searchactivity = new Intent(this,SearchActivity.class);
        startActivity(searchactivity);
    }
    public void about(View v){
        Intent abouthactivity = new Intent(this,About.class);
        startActivity(abouthactivity);
    }
}