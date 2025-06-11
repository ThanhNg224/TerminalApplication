package com.atin.arcface.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.atin.arcface.R;
import com.atin.arcface.common.Constants;
//import com.rabbitmq.client.Channel;
//import com.rabbitmq.client.Connection;
//import com.rabbitmq.client.ConnectionFactory;
//import com.rabbitmq.client.MessageProperties;

import java.nio.charset.Charset;

public class WriteCardActivity extends AppCompatActivity {
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private Toast toast = null;
    private String cardCode;
    private String requestId;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_card);

        cardCode = getIntent().getStringExtra(Constants.CARD_NO);
        requestId = getIntent().getStringExtra(Constants.REQUEST_ID);
        pref = getSharedPreferences("PREF", MODE_PRIVATE);

        NfcManager mNfcManager = (NfcManager) getSystemService(Context.NFC_SERVICE);
        mNfcAdapter = mNfcManager.getDefaultAdapter();
        if (mNfcAdapter == null) {
            showToast(this.getString(R.string.message_nfc_notsupport));
        } else if ((mNfcAdapter != null) && (!mNfcAdapter.isEnabled())) {
            showToast(this.getString(R.string.message_nfc_notwork));
        } else if ((mNfcAdapter != null) && (mNfcAdapter.isEnabled())) {
            showToast(this.getString(R.string.message_nfc_working));
        }
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), 0);
        init_NFC();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
            if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(this.getIntent().getAction())) {
                processIntent(this.getIntent());
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processIntent(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            stopNFC_Listener();
        }
    }

    public void processIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Ndef ndef = Ndef.get(tag);
        writeToNfc(ndef, cardCode);
        startActivity(new Intent(this, RegisterAndRecognizeDualActivity.class));
    }

    private void readFromNFC(Ndef ndef) {

        try {
            ndef.connect();
            NdefMessage ndefMessage = ndef.getNdefMessage();
            String message = new String(ndefMessage.getRecords()[0].getPayload());
            showToast("readFromNFC: " + message);
            ndef.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeToNfc(Ndef ndef, String cardCode){

        if (ndef != null) {
            try {
                ndef.connect();
                NdefRecord mimeRecord = NdefRecord.createMime("text/plain", cardCode.getBytes(Charset.forName("US-ASCII")));
                ndef.writeNdefMessage(new NdefMessage(mimeRecord));
                ndef.close();
                showToast("Ghi thẻ thành công");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void init_NFC() {
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
    }

    private void stopNFC_Listener() {
        mNfcAdapter.disableForegroundDispatch(this);
    }

    private void showToast(String s) {
        if (toast == null) {
            toast = Toast.makeText(this, s, Toast.LENGTH_LONG);
            toast.show();
        } else {
            toast.setText(s);
            toast.show();
        }
    }
}
