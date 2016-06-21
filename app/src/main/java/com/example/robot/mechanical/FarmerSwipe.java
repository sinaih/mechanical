package com.example.robot.mechanical;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class FarmerSwipe extends Activity {
    private NfcAdapter nfcAdapter;
    private String farmerdata;
    public static boolean ours, taglost, seasonopen;
    private PendingIntent mPendingIntent;
    public boolean decryptkey = true;
    public String farmernosend;
    public TextView myfooter, swipeinfo, errorlabel;
    public static String farmerno, extension_officer_name = "", extension_officer_id = "";
    private static final String[] blocks = new String[]{"0", "1", "2"};

    private byte[] key = {(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0xff, (byte) 0xff};

    private byte[] newkey = {(byte) 0x6d, (byte) 0x77, (byte) 0x69, (byte) 0x7a,
            (byte) 0x69, (byte) 0x32};


    private byte[] formatkey = {(byte) 0x6d, (byte) 0x77, (byte) 0x69, (byte) 0x7a,
            (byte) 0x69, (byte) 0x32, (byte) 0xff, (byte) 0x0f, (byte) 0x00, (byte) 0x32, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupVariables();
        initnfcparams();

    }

    @SuppressLint("ShowToast")
    private void initnfcparams() {
        setContentView(R.layout.activity_farmer_swipe);
        seasonopen = false;
        myfooter = (TextView) findViewById(R.id.myfooter);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("extension_name");
            seasonopen = extras.getBoolean("active_season");
            extension_officer_name = value;
            extension_officer_id = extras.getString("extension_officer_id");
            myfooter.setText("Loan Officer: " + value);
        }
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, getString(R.string.nfc_device_not_support),
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        if (!nfcAdapter.isEnabled()) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.nfc_not_open_go_open))
                    .setPositiveButton(getString(R.string.yes),
                            new DialogInterface.OnClickListener() {


                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub
                                    Intent intent = new Intent(
                                            "android.settings.NFC_SETTINGS");
                                    startActivityForResult(intent, 100);
                                }
                            }).create();
            dialog.show();
        }
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()), 0);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Mechanical", "NFC activity Resumed");

        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, mPendingIntent, null,
                    null);
        }
    }

    public void exit(View view) {
        this.finishAffinity();
    }

    protected void onNewIntent(Intent intent) {
        errorlabel = (TextView) findViewById(R.id.errorlabel);

        taglost = false;
        Tag tagt = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        if (tagt == null) {
            Log.e("Mechanical", "Unable to read Tag");
            return;
        }

        Log.e("Mechanical", farmerdata + "Tag after scanning nfc tag = " + bytesToHexString(tagt.getId()));

        String xx = String.valueOf(bytesToHexString(tagt.getId()));
        Log.e("Mechanical", "Scanned Card number " + Long.parseLong(xx.trim(), 16));
        String str = String.valueOf(Long.parseLong(xx.trim(), 16));
        ours = false;
        farmerno = "";

        if (str.length() > 0) {
            try {
                ours = readTag(tagt);

                if (farmerdata.equals(null)) {
                    farmerdata = "";
                    Log.e("Mechanical", "null farmer data ");
                } else {
                    Log.e("Mechanical", "has farmer data ");
                }
                farmerdata = hexToAscii(farmerdata);
                Log.e("Mechanical", "has farmer data " + farmerdata);
                if (decryptkey)
                    farmerdata = decryptNum(farmerdata);
                Log.e("Mechanical", "has farmer datanew " + farmerdata);
                farmernosend = farmerdata.substring(0, 12);
                ;
                Log.e("Mechanical", "read Farmer number " + farmerdata + " ours " + ours + str + "farmerno" + farmerno);

                if (farmerno == "00000000000000000000000000000000")
                    ours = false;
                if (ours) {

                    if (farmerdata.length() > 10) {
                        if (farmerdata.substring(0, 16) != "0000000000000000" && !farmerdata.substring(0, 16).equals("7575757575757575") && farmerdata.length() > 10)
                            ours = true;
                        else
                            ours = false;
                    } else
                        ours = false;


                    if (ours) {
                        farmerno = farmerdata.substring(0, 12);
                    }

                }
            } catch (Exception f) {

                ours = false;
            }
        }
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String imeinumber = telephonyManager.getDeviceId();
        if (ours) {
            MainActivity.TellServer("apicredit/swipe?CMD=SWIPE&SER=" + imeinumber + "&EXTID=" + extension_officer_id + "&STATUS=1&FMNO=" + farmernosend + "&CNO=" + str, (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE), this);

            Intent myIntent = new Intent(FarmerSwipe.this, FarmerLogin.class);
            myIntent.putExtra("extension_name", extension_officer_name); //Optional parameters
            myIntent.putExtra("farmer_no", farmernosend); //Optional parameters
            myIntent.putExtra("card_no", str); //Optional parameters
            myIntent.putExtra("extension_officer_id", extension_officer_id);

            errorlabel.setVisibility(View.INVISIBLE);
            FarmerSwipe.this.startActivity(myIntent);
        } else {
            // show error page.
            if (taglost) {
                errorlabel.setVisibility(View.VISIBLE);
                errorlabel.setText("Unable to read Card. Hold the Card on the scanner");
            } else {
                MainActivity.TellServer("apicredit/swipe?CMD=SWIPE&SER=" + imeinumber + "&EXTID=" + extension_officer_id + "&STATUS=0&FMNO=" + farmernosend + "&CNO=" + str, (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE), this);
                errorlabel.setVisibility(View.VISIBLE);
                errorlabel.setText("Sorry,The Scanned Credit Card was not issued by CASU");
            }
            return;

        }

    }

    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            System.out.println(buffer);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }

    public Boolean readTag(Tag tag) {
        MifareClassic mfc = MifareClassic.get(tag);
        for (String tech : tag.getTechList()) {
            System.out.println(tech);
        }
        farmerdata = "0000000000000000";
        boolean auth = false;
        String metaInfo = "";

        try {

            mfc.connect();
            int type = mfc.getType();
            int sectorCount = mfc.getSectorCount();
            String typeS = "";
            switch (type) {
                case MifareClassic.TYPE_CLASSIC:
                    typeS = "TYPE_CLASSIC";
                    break;
                case MifareClassic.TYPE_PLUS:
                    typeS = "TYPE_PLUS";
                    break;
                case MifareClassic.TYPE_PRO:
                    typeS = "TYPE_PRO";
                    break;
                case MifareClassic.TYPE_UNKNOWN:
                    typeS = "TYPE_UNKNOWN";
                    break;
            }
            metaInfo += " " + typeS + "\n" + sectorCount + " \n"
                    + mfc.getBlockCount() + " : " + mfc.getSize()
                    + "B\n";

            auth = mfc.authenticateSectorWithKeyA(1,
                    MifareClassic.KEY_NFC_FORUM);

            if (!auth) {

                auth = mfc.authenticateSectorWithKeyA(1,
                        MifareClassic.KEY_DEFAULT);
                //if (auth)
                //	Toast.makeText(this, "key B PAssed", Toast.LENGTH_LONG).show();
                //else
                //Toast.makeText(this, "key B Failed", Toast.LENGTH_LONG).show();
            }
            //else
            //Toast.makeText(this, "key A PAssed", Toast.LENGTH_LONG).show();


            int bCount;
            int bIndex;
            if (auth) {
                //	Toast.makeText(this, "authed", Toast.LENGTH_LONG).show();
                metaInfo += "Sector " + 1 + ":¦\n";

                bCount = mfc.getBlockCountInSector(1);
                bIndex = mfc.sectorToBlock(1);

                byte[] data = mfc.readBlock(5);
                farmerdata = bytesToHexString(data) + " ";
                metaInfo += " Data " + farmerdata;
                farmerdata = farmerdata.trim();
                return true;

            } else {
                metaInfo += "Sector " + 1 + "\n";

            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().toUpperCase().contains("LOST"))
                taglost = true;
            Log.e("Casu E-Voucher", " Rad tag A Error " + e.getMessage());
            return false;
        } finally {
            if (mfc != null) {
                try {
                    mfc.close();
                } catch (IOException e) {
                    Log.e("Casu E-Voucher", " Rad tag B Error " + e.getMessage());
                    if (e.getMessage().toUpperCase().contains("LOST"))
                        taglost = true;
                    return false;
                }
            }

        }
    }

    private String hexToAscii(String s) {
        int n = s.length();
        StringBuilder sb = new StringBuilder(n / 2);
        for (int i = 0; i < n; i += 2) {
            char a = s.charAt(i);
            char b = s.charAt(i + 1);
            sb.append((char) ((hexToInt(a) << 4) | hexToInt(b)));
        }
        return sb.toString();
    }

    private static int hexToInt(char ch) {
        if ('a' <= ch && ch <= 'f') {
            return ch - 'a' + 10;
        }
        if ('A' <= ch && ch <= 'F') {
            return ch - 'A' + 10;
        }
        if ('0' <= ch && ch <= '9') {
            return ch - '0';
        }
        throw new IllegalArgumentException(String.valueOf(ch));
    }

    private int getDec(byte[] bytes) {
        int result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    public static String decryptNum(String num) {
        //String textBack = new String(num.toByteArray());
        String temp = num;
        int[] n = new int[temp.length()];
        String convertedNum = "";
        for (int i = 0; i < temp.length(); i++) {
            int newNum = temp.charAt(i) - '0';
            int convert = 9 - newNum;
            n[i] = convert;
            convertedNum += convert;
        }
        String newString = convertedNum;
        String inverse = new StringBuffer(newString).reverse().toString();

        return inverse;
    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            if (!nfcAdapter.isEnabled()) {
                Log.e("Mechanised", "Go and enable NFC");
            }
        }
    }

    private void setupVariables() {
        // myfooter = (TextView) findViewById(R.id.myfooter);

    }
}
