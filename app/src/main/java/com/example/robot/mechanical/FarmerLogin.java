package com.example.robot.mechanical;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FarmerLogin extends Activity {

    public EditText nrcno;
    public EditText pinno;
    public Button login;
    public TextView farmererror;
    String SequenceId = "";
    public String farmernrcno;
    ProgressDialog chillaxdialog;
    private String nextactionname;
    public static String farmercardnum, farmerid;
    public Boolean cancontinue;
    public static String farmername = "";
    public static String farmer_no = "";
    public static String card_no = "";
    public static String nrc = "";
    public TextView myfooter;
    public String extension_officer_name = "",extension_officer_id="",credit_id="",credit_no="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_login);
        setupVariables();
        myfooter = (TextView) findViewById(R.id.myfooter);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("extension_name");
            extension_officer_name = value;
            farmer_no=extras.getString("farmer_no");
            card_no=extras.getString("card_no");
            extension_officer_id=extras.getString("extension_officer_id");
            myfooter.setText("Loan Officer: " + value);
        }
    }

    private void setupVariables() {
        nrcno = (EditText) findViewById(R.id.nrcno);
        pinno = (EditText) findViewById(R.id.pinno);
        farmererror = (TextView) findViewById(R.id.farmererror);
        //  myfooter = (TextView) findViewById(R.id.myfooter);


    }

    /**
     * When login button is clicked
     *
     * @param view
     */
    public void login(View view) {
        // Do something in response to button
        ProcessFarmPin();
    }
    public void cancel(View view) {
        try {
            Intent myIntent = new Intent(FarmerLogin.this, FarmerSwipe.class);
            myIntent.putExtra("extension_name", extension_officer_name); //Optional parameters
            myIntent.putExtra("active_season", 1); //Optional parameters
            myIntent.putExtra("extension_officer_id",extension_officer_id);
            Log.e("Mechanical", "logout ");
            FarmerLogin.this.startActivity(myIntent);
        } catch (RuntimeException $e) {

        }
    }

    private void ProcessFarmPin() {

        SequenceId = new SimpleDateFormat("yyyMMddHHmmss").format(Calendar.getInstance().getTime());
        if (pinno.getText().toString().length() < 2) {
            farmererror.setVisibility(View.VISIBLE);
            farmererror.setText(" Kindly Input a Valid PIN No ");
            return;
        }

        if (nrcno.getText().toString().length() != 11) {
            farmererror.setVisibility(View.VISIBLE);
            farmererror.setText(" NRC no Should be 11 characters ");
            return;
        }

        farmernrcno = nrcno.getText().toString();
        if (isConnected()) {

            chillaxdialog = ProgressDialog.show(this, "Validating Farmer Credentials", "Please wait ...", true);
            nextactionname = "VALIDATE_FARMER";

            try {
                new CountDownTimer(300, 100) {
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        continuewhat(nextactionname);
                    }
                }.start();
            } catch (Exception f) {

            }
        }
    }

    public void continuewhat(String whatnext) {

        if (whatnext == "VALIDATE_FARMER") {

            StrictMode.ThreadPolicy policy1 = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy1);
            Httpreader rr1 = new Httpreader();
            try {
                TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                String imeinumber=telephonyManager.getDeviceId();
                String rep11 = rr1.GetUrlData("apicredit/farmerlogin?CMD=FMT&CNO="+card_no+"&PASS="+pinno.getText().toString()+"&FMID="+farmer_no+"&NRC="+nrcno.getText().toString()+"&SER="+imeinumber+"&EXTID="+extension_officer_id);
                //
                //api/csp?CMD=FMT&CNO=8912602166&PASS=8085


                JSONObject jObj = null;
                String[] separated = rep11.split("\\|");


                for (String arraydata : separated) {
                    if (arraydata.contains("{")) {
                        cancontinue = true;
                        boolean resposne = false;
                        boolean hascredit = false;

                        try {
                            jObj = new JSONObject(arraydata);
                            if (arraydata.contains("RSP"))

                            {
                                try {
                                    resposne = jObj.getBoolean("RSP");
                                } catch (Exception f) {
                                    resposne = false;
                                }
                            }
                            if (arraydata.contains("HASCREDIT"))

                            {
                                try {
                                    hascredit = jObj.getBoolean("HASCREDIT");
                                } catch (Exception f) {
                                    resposne = false;
                                }
                            }


                            chillaxdialog.dismiss();
                            if (resposne && hascredit) {
                                SequenceId = new SimpleDateFormat("yyyMMddHHmmss").format(Calendar.getInstance().getTime());


                                farmername = jObj.getString("NAME").toString();
                                nrc = jObj.getString("NRC").toString();
                                //farmer_no = jObj.getString("NRC").toString();

                                arraydata = arraydata.toUpperCase();


                                farmererror.setVisibility(View.INVISIBLE);
                                Intent myIntent = new Intent(FarmerLogin.this, Payment.class);
                                myIntent.putExtra("extension_name", extension_officer_name); //Optional parameters
                                myIntent.putExtra("farmer_data", farmername);
                                myIntent.putExtra("farmer_no", farmer_no);
                                myIntent.putExtra("nrc", nrc);
                                myIntent.putExtra("credit_no", jObj.getString("CRNO").toString());
                                myIntent.putExtra("credit_id", jObj.getString("CRID").toString());
                                myIntent.putExtra("credit_amount", jObj.getString("CRA").toString());
                                myIntent.putExtra("credit_paid", jObj.getString("CRP").toString());
                                myIntent.putExtra("credit_balance", jObj.getString("CRB").toString());
                                myIntent.putExtra("card_no", card_no);

                                myIntent.putExtra("extension_officer_id",extension_officer_id);
                                FarmerLogin.this.startActivity(myIntent);
                                return;

                            } else {

                                farmererror.setVisibility(View.VISIBLE);

                                if (arraydata.toUpperCase().contains("INACTIVE"))
                                    farmererror.setText("\nSorry, Credit card is inactive\n");
                                else if (arraydata.toUpperCase().contains("PIN_INVALID"))
                                    farmererror.setText("\nSorry, your PIN number/NRC Number are invalid.\n");
                                else if (arraydata.toUpperCase().contains("MISMATCH"))
                                    farmererror.setText("\nSorry, The smart card is invalid.\n Kindly contact CASU Team.\n");else if (arraydata.toUpperCase().contains("NO VOUCHER"))
                                    farmererror.setText("\nDear " + jObj.getString("NAME").toString() + ", NRC- " + jObj.getString("NRC").toString() + ",\nSorry, You do not have a valid Credit Voucher.\nKindly contact CASU team.\n");
                                else if (!hascredit)
                                    farmererror.setText("\nDear " + jObj.getString("NAME").toString() + ", NRC- " + jObj.getString("NRC").toString() + "\nYour Credit Voucher should have been redeemed atleast once.\n");

                                else
                                    farmererror.setText("\nSorry unable to Login the Farmer. Kindly contact the CASU team");


                                return;
                            }
                        } catch (Exception f) {
                            chillaxdialog.dismiss();
                            if (MainActivity.debugmode)
                                Log.e("Mechanical", "Got Err " + f.getMessage() + " " + f.getCause() + f.toString());
                        }
                    }

                }


            } catch (Exception e) {
                // TODO Auto-generated catch block

                if (MainActivity.debugmode)
                    Log.e("Mechanical", " Got Error " + e.getMessage() + " " + e.getCause() + e.toString());

                chillaxdialog.dismiss();
                chillaxdialog.dismiss();
                farmererror.setVisibility(View.VISIBLE);
                farmererror.setText("Error. Server Unreachable ");

                return;

            }
        }

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
                farmererror.setVisibility(View.VISIBLE);
                farmererror = (TextView) findViewById(R.id.farmererror);
                farmererror.setText("Server Unreachable");
            } else {
                farmererror.setVisibility(View.VISIBLE);

                if (findViewById(R.id.farmererror) != null) {
                    farmererror = (TextView) findViewById(R.id.farmererror);
                    farmererror.setText("Server Unreachable");
                }

            }
            return false;

        }
    }
    public void onBackPressed() { }
}
