package com.example.david.museu;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.nio.charset.Charset;


public class MainActivity extends ActionBarActivity {

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mTextView = (TextView) findViewById(R.id.tview);

        /*
        mAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "Aquest dispositiu no suporta NFC.", Toast.LENGTH_LONG).show();
            //finish();
            return;
        }

        if (!mAdapter.isEnabled()) {
            mTextView.setText("NFC apagat.");
        } else {
            mTextView.setText("NFC operatiu");
        }
        */

        // Boton 1
        Button boton = (Button) findViewById(R.id.bServer);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nuevoform = new Intent(MainActivity.this, ServerActivity.class);
                startActivity(nuevoform);
            }
        });

        // Crear BD
        db=openOrCreateDatabase("Database", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS obres_preferides(id VARCHAR);");
        db.execSQL("INSERT into obres_preferides(id) values('1')");


        // Boton 2
        Button boton2 = (Button) findViewById(R.id.bPreferides);
        boton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nuevoform = new Intent(MainActivity.this, PreferidesActivity.class);
                startActivity(nuevoform);
            }
        });

        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC disponible!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "NFC no disponible :(", Toast.LENGTH_LONG).show();
        }

    }
}
