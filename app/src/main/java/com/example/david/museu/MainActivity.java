package com.example.david.museu;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {

    SQLiteDatabase db;
    NfcAdapter nfcAdapter;
    EditText txtTagContent;
    Boolean teNfc = false;

    ToggleButton tglReadWrite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        db = openOrCreateDatabase("Database", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS obres_preferides(id VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS consulta_nfc(id VARCHAR);");
        db.execSQL("INSERT into obres_preferides(id) values('1')");


        // Boton 2
        Button boton2 = (Button) findViewById(R.id.bPreferides);
        boton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor c=db.rawQuery("SELECT id FROM obres_preferides",null);
                if (c.getCount() > 0) {
                    Intent nuevoform = new Intent(MainActivity.this, PreferidesActivity.class);
                    startActivity(nuevoform);
                }
                else {
                    // Aqui popup que diga que no hay preferidas
                    senseObresPreferides();
                }
            }
        });

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        tglReadWrite = (ToggleButton) findViewById(R.id.tglReadWrite);
        txtTagContent = (EditText) findViewById(R.id.txtTagContent);

        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            teNfc = true;
            Toast.makeText(this, "NFC disponible :)", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "NFC no disponible :(", Toast.LENGTH_LONG).show();
        }
    }

    private void senseObresPreferides() {
            Toast.makeText(this, "No tens cap obra preferida", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        //Toast.makeText(this, "NFC intent recibido!", Toast.LENGTH_LONG).show();
        super.onNewIntent(intent);

        if (intent.hasExtra((NfcAdapter.EXTRA_TAG))) {
            //Toast.makeText(this, "NfcIntent!", Toast.LENGTH_SHORT).show();

            if (tglReadWrite.isChecked())
            {
                Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

                if (parcelables != null && parcelables.length > 0)
                {
                    consultaObraPerNFC((NdefMessage) parcelables[0]);
                    //readTextFromMessage((NdefMessage) parcelables[0]);
                } else {
                    Toast.makeText(this, "No NDEF messages found!", Toast.LENGTH_SHORT).show();
                }


            }else {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                NdefMessage ndefMessage = createNdefMessage(txtTagContent.getText()+"");

                writeNdefMessage(tag, ndefMessage);
            }
        }
    }

    private void readTextFromMessage(NdefMessage ndefMessage) {
        NdefRecord[] ndefRecords = ndefMessage.getRecords();

        if (ndefRecords != null && ndefRecords.length > 0) {

            NdefRecord ndefRecord = ndefRecords[0];

            String tagContent = getTextFromNdefRecord(ndefRecord);

            txtTagContent.setText(tagContent);

        } else {
            Toast.makeText(this, "No NDEF records found!", Toast.LENGTH_SHORT).show();
        }
    }

    private void consultaObraPerNFC(NdefMessage ndefMessage) {
        NdefRecord[] ndefRecords = ndefMessage.getRecords();

        if (ndefRecords != null && ndefRecords.length > 0) {

            NdefRecord ndefRecord = ndefRecords[0];

            String tagContent = getTextFromNdefRecord(ndefRecord);
            db.execSQL("DELETE FROM consulta_nfc");
            db.execSQL("INSERT into consulta_nfc(id) values('"+tagContent+"')");
            Intent nuevoform = new Intent(MainActivity.this, NfcActivity.class);
            startActivity(nuevoform);
            //txtTagContent.setText(tagContent);

        } else {
            Toast.makeText(this, "No NDEF records found!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        enableForegroundDispatchSystem();
    }

    @Override
    protected void onPause() {
        super.onPause();

        disableForegroundDispatchSystem();
    }

    private void enableForegroundDispatchSystem() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[]{};
        if (teNfc)
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null); //@todo nfc
    }

    private void disableForegroundDispatchSystem() {
        if (teNfc)
        nfcAdapter.disableForegroundDispatch(this); //@todo nfc
    }

    private void formatTag(Tag tag, NdefMessage ndefMessage) {
        try {

            NdefFormatable ndefFormatable = NdefFormatable.get(tag);

            if (ndefFormatable == null) {
                Toast.makeText(this, "Tag is not ndef formatable!", Toast.LENGTH_SHORT).show();
                return;
            }

            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();

            Toast.makeText(this, "Tag writen!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e("formatTag", e.getMessage());
        }
    }

    private void writeNdefMessage(Tag tag, NdefMessage ndefMessage) {

        try {

            if (tag == null) {
                Toast.makeText(this, "Tag object cannot be null", Toast.LENGTH_SHORT).show();
                return;
            }

            Ndef ndef = Ndef.get(tag);

            if (ndef == null) {
                // format tag with the ndef formate and writes the message
                formatTag(tag, ndefMessage);
            } else {
                ndef.connect();
                if (!ndef.isWritable()) {
                    Toast.makeText(this, "Tag is not writable", Toast.LENGTH_SHORT).show();
                    ndef.close();
                    return;
                }

                ndef.writeNdefMessage(ndefMessage);
                ndef.close();

                Toast.makeText(this, "Tag writen!", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e("writeNdefMessage", e.getMessage());
        }

    }

    private NdefRecord createTextRecord(String content) {
        try {
            byte[] language;
            language = Locale.getDefault().getLanguage().getBytes("UTF-8");

            final byte[] text = content.getBytes("UTF-8");
            final int languageSize = language.length;
            final int textLength = text.length;
            final ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + languageSize + textLength);

            payload.write((byte) (languageSize & 0x1F));
            payload.write(language, 0, languageSize);
            payload.write(text, 0, textLength);

            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());

        } catch (UnsupportedEncodingException e) {
            Log.e("createTextRecord", e.getMessage());
        }
        return null;
    }

    private NdefMessage createNdefMessage(String content) {

        NdefRecord ndefRecord = createTextRecord(content);
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ndefRecord});

        return ndefMessage;
    }

    public void tglReadWriteOnClick(View view) {
        txtTagContent.setText("");
    }

    public String getTextFromNdefRecord(NdefRecord ndefRecord)
    {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1, payload.length - languageSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        return tagContent;
    }
}
