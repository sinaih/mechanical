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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class MainActivity extends Activity {

    public EditText username;
    public EditText password;
    public Button login;
    public TextView error;
    public static String ServerUrl;
    public static boolean seasonactive, requesttimeout, redeemtimeout, debugmode = true;
    public int responce = 0;
    public String errorMessage = "";
    public static String extension_officer_name="",extension_officer_id="";
    public ProgressDialog chillaxdialog;
    public String user,pass;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupVariables();
        ServerUrl = "http://207.58.189.167/fivms/";
        debugmode = true;
        seasonactive = false;
        redeemtimeout = false;
        requesttimeout = false;

    }

    private void setupVariables() {
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        error = (TextView) findViewById(R.id.error);

    }


    /**
     * When login button is clicked
     *
     * @param view
     */
    public void login(View view) {
        // Do something in response to button

        try {
             user = username.getText().toString();
             pass = password.getText().toString();
            if (user.isEmpty()) {
                error.setVisibility(View.VISIBLE);
                error.setText("Please Enter Username ");
            } else if (pass.isEmpty()) {
                error.setVisibility(View.VISIBLE);
                error.setText("Please Enter Password ");
            } else {

                if (isConnected()) {

                    chillaxdialog = ProgressDialog.show(this, "Validating Loan Officer Credentials", "Please wait ...", true);

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
        } catch (RuntimeException $e) {

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
    public static void TellServer(final String urltotell,TelephonyManager telephonyManager,final Context c){

        if (MainActivity.debugmode)  Log.e("Mechanical","TellServer" + urltotell);

        new Thread(new Runnable() {
            @Override
            public void run() {

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

                StrictMode.setThreadPolicy(policy);
                Httpreader rr= new Httpreader();
                String rpt="";
                try {
                    rpt = rr.GetUrlData(urltotell);
                } catch (Exception e) {
                    if (MainActivity.debugmode)  Log.e("Mechanical","Error writing Server log " + e.getMessage());
                }
            }
        }).start();


    }
    public void continuewhat() {
        StrictMode.ThreadPolicy policy1 = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy1);
        Httpreader rr1 = new Httpreader();
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String imeinumber=telephonyManager.getDeviceId();

        try {
            String rep11 = rr1.GetUrlData("apicredit/login?CMD=LOGIN&USERNAME=" + user + "&PASS=" + pass+"&+SER="+imeinumber);
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
                                if(responce==1) {
                                    extension_officer_name = jObj.getString("NAME").toString();
                                    extension_officer_id = jObj.getString("EXTID").toString();
                                }
                                seasonactive=true;

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

            Intent myIntent = new Intent(MainActivity.this, FarmerSwipe.class);
            myIntent.putExtra("extension_name", extension_officer_name); //Optional parameters
            myIntent.putExtra("active_season", seasonactive); //Optional parameters
            myIntent.putExtra("extension_officer_id",extension_officer_id);
            error.setVisibility(View.INVISIBLE);
            MainActivity.this.startActivity(myIntent);
        } else {
            errorMessage = responce == 2 ? "Enter Username and Password" : responce == 3 ? "Your account is inactive" : responce == 4 ? "Wrong Username and password combination" : responce == 5 ?"Unable to connect to the internet": "Invalid Server Responce";
            error.setVisibility(View.VISIBLE);
            error.setText(errorMessage);
        }
    }
    public void onBackPressed() { }
    public void exit(View view) {
        this.finishAffinity();
    }
}
