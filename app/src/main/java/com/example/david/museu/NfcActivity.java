package com.example.david.museu;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;


public class NfcActivity extends ActionBarActivity {

    private NfcAdapter mAdapter;
    private TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        //WebView wv = (WebView) findViewById(R.id.webView);
        //wv.loadUrl("https://museumalacarte-bustawin.c9.io/artworks");
    }

    private void guardaDades(String idBalissa) {

    }

}
