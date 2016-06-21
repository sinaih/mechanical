package com.example.robot.mechanical;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class Confirm extends Activity {
    public TextView confirm,myheader,myfooter;
    public Double balance,total;
    public String extension_officer_name,extension_officer_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            extension_officer_name = extras.getString("extension_name");
            extension_officer_id = extras.getString("extension_officer_id");
            myheader = (TextView) findViewById(R.id.myheader);
            myfooter = (TextView) findViewById(R.id.myfooter);
            myfooter.setText("Loan Officer: " + extension_officer_name);

            myheader.setText(" Welcome : " + extras.getString("farmer_data") + "\n  FARMER NO:" + extras.getString("farmer_no")
                    + "\n  NRC NO:" + extras.getString("nrc"));
            confirm = (TextView) findViewById(R.id.confirm);
            balance=Double.parseDouble(extras.getString("balance"))-Double.parseDouble(extras.getString("amount_paid"));
            String cr=extras.getString("credit_paid");
            if(cr.isEmpty()){
                total = Double.parseDouble(extras.getString("amount_paid"));
            }else {
                total = Double.parseDouble(extras.getString("amount_paid")) + Double.parseDouble(extras.getString("credit_paid"));
            }
            confirm.setText(" You have successfully paid "+extras.getString("amount_paid")+"(ZMK)\n" +
                    " Your Credit Amount is "+total+"(ZMK)\n Your credit Balance is "+balance+"(ZMK)");
        }
    }
    public void logout(View view){
        Intent myIntent = new Intent(Confirm.this, FarmerSwipe.class);
        myIntent.putExtra("extension_name", extension_officer_name); //Optional parameters
        myIntent.putExtra("active_season", 1); //Optional parameters
        myIntent.putExtra("extension_officer_id",extension_officer_id);
        Log.e("Mechanical", "logout ");
        Confirm.this.startActivity(myIntent);

    }
    public void cancel(View view) {
        try {
            Intent myIntent = new Intent(Confirm.this, FarmerSwipe.class);
            myIntent.putExtra("extension_name", extension_officer_name); //Optional parameters
            myIntent.putExtra("active_season", 1); //Optional parameters
            myIntent.putExtra("extension_officer_id",extension_officer_id);
            Log.e("Mechanical", "logout ");
            Confirm.this.startActivity(myIntent);
        } catch (RuntimeException $e) {

        }
    }
}
