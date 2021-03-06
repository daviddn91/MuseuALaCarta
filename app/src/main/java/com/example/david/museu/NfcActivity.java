package com.example.david.museu;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;


public class NfcActivity extends ActionBarActivity {

    private NfcAdapter mAdapter;
    private TextView tv;
    SQLiteDatabase db;
    String idnfc = null;
    WebView wv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        db=openOrCreateDatabase("Database", Context.MODE_PRIVATE, null);
        Cursor c=db.rawQuery("SELECT id FROM consulta_nfc",null);
        if(c.moveToNext()) {
            idnfc = c.getString(0).toString();
        }
        wv = (WebView) findViewById(R.id.webViewNFC);
        //Enable Javascript
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebChromeClient(new WebChromeClient());
        //Inject WebAppInterface methods into Web page by having Interface name 'Android'
        wv.addJavascriptInterface(new WebAppInterface(this), "Android");
        //Load URL inside WebView
        String url = "https://museumalacarte-bustawin.c9.io/artworks?beacon=";
        url = url + idnfc;
        wv.clearCache(true);
        wv.loadUrl(url);
    }



    public class WebAppInterface {
        Context mContext;
        SQLiteDatabase db;
        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
            db=openOrCreateDatabase("Database", Context.MODE_PRIVATE, null);
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public boolean setFavorite(int id) {
            //Toast.makeText(mContext, "Entra a la funcio d'afegir a preferit l'obra "+id, Toast.LENGTH_LONG).show();
            boolean setFavorite = false;
            boolean existeixObra = false;
            Cursor c=db.rawQuery("SELECT id FROM obres_preferides WHERE id = '"+id+"'",null);
            while(c.moveToNext()) {
                if (c.getString(0).toString().equals(String.valueOf(id))) {
                    existeixObra = true;
                };
            }
            if (!existeixObra) {
                //Toast.makeText(mContext, "Arriba a inserir l'obra a BD "+id, Toast.LENGTH_LONG).show();
                db.execSQL("INSERT into obres_preferides(id) values('" + id + "')");
                setFavorite = true;
            }
            return setFavorite;
        }
        @JavascriptInterface
        public boolean unsetFavorite(int id) {
            //Toast.makeText(mContext, "Entra a la funcio d'esborrar preferit l'obra "+id, Toast.LENGTH_LONG).show();
            boolean unsetFavorite = false;
            Cursor c=db.rawQuery("SELECT id FROM obres_preferides WHERE id = '"+id+"'",null);
            while(c.moveToNext()) {
                if (c.getString(0).toString().equals(String.valueOf(id))) {
                    //Toast.makeText(mContext, "Arriba a borrar l'obra a BD"+id, Toast.LENGTH_LONG).show();
                    db.execSQL("DELETE FROM obres_preferides WHERE id = '" + id + "'");
                };
            }
            return unsetFavorite;
        }
        @JavascriptInterface
        public boolean isFavorite(int id) {
            //Toast.makeText(mContext, "Entra a la funcio isFavorite amb id: " + id, Toast.LENGTH_LONG).show();
            boolean isFavorite = false;
            Cursor c=db.rawQuery("SELECT id FROM obres_preferides WHERE id = '"+id+"'",null);
            if (c.getCount() > 0) {
                isFavorite = true;
                //Toast.makeText(mContext, "Entra a la funcio isFavorite = true " + id, Toast.LENGTH_LONG).show();

                wv.post(new Runnable() {
                    @Override
                    public void run() {
                        wv.evaluateJavascript("favorited(1);", null);
                    }
                });

            }
            return isFavorite;
        }
    }
}
