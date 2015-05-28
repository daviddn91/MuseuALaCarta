package com.example.david.museu;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Iterator;


public class NfcActivity extends ActionBarActivity {

    private NfcAdapter mAdapter;
    private TextView tv;
    SQLiteDatabase db;
    String idnfc = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        db=openOrCreateDatabase("Database", Context.MODE_PRIVATE, null);

        Cursor c=db.rawQuery("SELECT id FROM consulta_nfc",null);
        if(c.moveToNext()) {
            idnfc = c.getString(0).toString();
        }
        WebView wv = (WebView) findViewById(R.id.webViewNFC);
        String url = "https://museumalacarte-bustawin.c9.io/artworks?beacon=";
        url = url + idnfc;
        wv.loadUrl(url);
    }

}
