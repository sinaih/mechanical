package com.example.robot.mechanical;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class Payment extends Activity {
    public TextView myfooter, credit_no, credit_no_amount, credit_no_paid, credit_no_balance, myheader;
    public EditText amount_to_save;
    public Button save, cancel, show;
    public TextView error, entramount;
    public String extension_officer_id = "",bal="0",credit_id="0",extension_officer_name,farmer_data,farmer_no,nrc,cr_no,card_no,credit_paid;
    public ProgressDialog chillaxdialog;
    public int responce=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        setupVariables();
        save.setEnabled(false);
        myheader = (TextView) findViewById(R.id.myheader);
        myfooter = (TextView) findViewById(R.id.myfooter);
        credit_no = (TextView) findViewById(R.id.credit_no);
        credit_no_amount = (TextView) findViewById(R.id.credit_no_amount);
        credit_no_paid = (TextView) findViewById(R.id.credit_no_paid);
        credit_no_balance = (TextView) findViewById(R.id.credit_no_balance);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("extension_name");
            extension_officer_name=value;
            farmer_data= extras.getString("farmer_data");
            farmer_no= extras.getString("farmer_no");
            card_no=extras.getString("card_no");
            nrc= extras.getString("nrc");
            myheader.setText("Welcome : " + extras.getString("farmer_data") + "\n  FARMER NO:" + extras.getString("farmer_no")
                    + "\n  NRC NO:" + extras.getString("nrc"));
            myfooter.setText("Loan Officer: " + value);
            credit_no.setText(extras.getString("credit_no"));
            cr_no=extras.getString("credit_no");
            credit_id=extras.getString("credit_id");
            credit_no_amount.setText(extras.getString("credit_amount"));
            credit_no_paid.setText(extras.getString("credit_paid"));
            credit_paid=extras.getString("credit_paid");
            bal=extras.getString("credit_balance");
            credit_no_balance.setText(extras.getString("credit_balance"));
            extension_officer_id = extras.getString("extension_officer_id");
        }
        try {
            boolean isReady = Double.parseDouble(bal) >0;
            show.setEnabled(isReady);
        }
        catch (RuntimeException $e) {
            show.setEnabled(false);
        }
        amount_to_save.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                enableSubmitIfReady();
            }
        });
    }
    public void enableSubmitIfReady() {
        try {
            boolean isReady = Double.parseDouble(amount_to_save.getText().toString()) >= 1;
            save.setEnabled(isReady);
        }
        catch (RuntimeException $e) {
            save.setEnabled(false);
        }
    }
    public void show(View view) {
        try {
            entramount.setVisibility(View.VISIBLE);
            amount_to_save.setVisibility(View.VISIBLE);
            save.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.VISIBLE);
        } catch (RuntimeException $e) {

        }
    }
    public void cancel(View view) {
        try {
            Intent myIntent = new Intent(Payment.this, FarmerSwipe.class);
            myIntent.putExtra("extension_name", extension_officer_name); //Optional parameters
            myIntent.putExtra("active_season", 1); //Optional parameters
            myIntent.putExtra("extension_officer_id",extension_officer_id);
            Log.e("Mechanical", "logout ");
            Payment.this.startActivity(myIntent);
        } catch (RuntimeException $e) {

        }
    }
    public void logout(View view){
        Intent myIntent = new Intent(Payment.this, FarmerSwipe.class);
        myIntent.putExtra("extension_name", extension_officer_name); //Optional parameters
        myIntent.putExtra("active_season", 1); //Optional parameters
        myIntent.putExtra("extension_officer_id",extension_officer_id);
        Log.e("Mechanical", "logout ");
        Payment.this.startActivity(myIntent);

    }
    public void save(View view) {
        try {
            String am=amount_to_save.getText().toString();
            if (am.isEmpty()) {
                error.setVisibility(View.VISIBLE);
                error.setText("Please Enter Amount ");
            } else if (Double.parseDouble(am)>Double.parseDouble(bal)) {
                error.setVisibility(View.VISIBLE);
                error.setText("Amount can not be greater than credit balance ");
            }else if(Double.parseDouble(am)<1){
                error.setVisibility(View.VISIBLE);
                error.setText("Amount should be greater than 1");
            }
            else{
                if (isConnected()) {

                    chillaxdialog = ProgressDialog.show(this, "Saving", "Please wait ...", true);

                    try {
                        new CountDownTimer(300, 100) {
                            public void onTick(long millisUntilFinished) {
                            }

                            public void onFinish() {
                                continuewhat();
                            }
                        }.start();
                    } catch (Exception f) {

                    }
                }
            }

        } catch (Exception e) {
            Log.e("Casu Evoucher", " Got Error " + e.getMessage() + " " + e.getCause() + e.toString());

        }
        return;
    }
    public void continuewhat() {
        StrictMode.ThreadPolicy policy1 = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy1);
        Httpreader rr1 = new Httpreader();


        try {
            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            String imeinumber=telephonyManager.getDeviceId();
            String rep11 = rr1.GetUrlData("apicredit/savecredit?CMD=PAY&AMOUNT="+amount_to_save.getText().toString()+"&CRED="+credit_id+"&EXTID="+extension_officer_id+"&SER="+imeinumber+"&FMNO="+farmer_no+"&CNO="+card_no+"&CRNO="+cr_no);
            JSONObject jObj = null;
            String[] separated = rep11.split("\\|");


            for (String arraydata : separated) {
                if (arraydata.contains("{")) {

                    try {
                        jObj = new JSONObject(arraydata);
                        if (arraydata.contains("RSP"))

                        {
                            try {
                                responce = jObj.getInt("RSP");

                            } catch (Exception f) {
                                responce = 0;
                                Log.e("Casu Evoucher", " Got Error " + f.getMessage() + " " + f.getCause() + f.toString());
                            }
                        }
                    } catch (Exception f) {
                        responce = 0;
                        Log.e("Casu Evoucher", " Got Error " + f.getMessage() + " " + f.getCause() + f.toString());
                    }
                }
            }


            chillaxdialog.dismiss();

        } catch (Exception e) {
            chillaxdialog.dismiss();
            responce=5;
            if (MainActivity.debugmode)
                Log.e("Casu Evoucher", " Got Error " + e.getMessage() + " " + e.getCause() + e.toString());
        }
        if (responce == 1) {

            Intent myIntent = new Intent(Payment.this, Confirm.class);
            myIntent.putExtra("extension_name", extension_officer_name); //Optional parameters
            myIntent.putExtra("active_season", 1); //Optional parameters
            myIntent.putExtra("extension_officer_id",extension_officer_id);
            myIntent.putExtra("amount_paid",amount_to_save.getText().toString());
            myIntent.putExtra("farmer_data", farmer_no);
            myIntent.putExtra("farmer_no", farmer_no);
            myIntent.putExtra("nrc", nrc);
            myIntent.putExtra("balance",bal);
            myIntent.putExtra("credit_paid",credit_paid);
            error.setVisibility(View.INVISIBLE);

            Payment.this.startActivity(myIntent);
        } else {
           // errorMessage = responce == 2 ? "Enter Username and Password" : responce == 3 ? "Your account is inactive" : responce == 4 ? "Wrong Username and password combination" : responce == 5 ?"Unable to connect to the internet": "Invalid Server Responce";
            error.setVisibility(View.VISIBLE);
            error.setText("NOT SAVED ");
        }
    }
    private void setupVariables() {
        amount_to_save = (EditText) findViewById(R.id.amount_to_save);
        save = (Button) findViewById(R.id.save);
        cancel = (Button) findViewById(R.id.cancel);
        entramount = (TextView) findViewById(R.id.entramount);
        error = (TextView) findViewById(R.id. save_error);
        show=(Button) findViewById(R.id.show);
    }
    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else {
            if (MainActivity.debugmode)
                Log.e("Casu Evoucher", "Please connect to the Internet to Maintain server Connection");

            if (findViewById(R.id.farmererror) != null) {
                error.setVisibility(View.VISIBLE);
                error = (TextView) findViewById(R.id.error);
                error.setText("Server Unreachable");
            } else {
                error.setVisibility(View.VISIBLE);

                if (findViewById(R.id.error) != null) {
                    error = (TextView) findViewById(R.id.error);
                    error.setText("Server Unreachable");
                }

            }
            return false;

        }

    }
}
