package com.example.david.museu;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.Iterator;


public class PreferidesActivity extends ActionBarActivity {

    SQLiteDatabase db;
    ArrayList<String> ids = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferides);
        db=openOrCreateDatabase("Database", Context.MODE_PRIVATE, null);
        Cursor c=db.rawQuery("SELECT id FROM obres_preferides",null);
        while(c.moveToNext()) {
            ids.add(c.getString(0).toString());
        }
        WebView wv = (WebView) findViewById(R.id.webView);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.addJavascriptInterface(new WebAppInterface(this), "Android");
        String url = "https://museumalacarte-bustawin.c9.io/artworks?";
        Iterator it = ids.iterator();
        Boolean primer = true; // Boolean per afegir la primera part de la url o no (id[]=)
        while (it.hasNext()) {
            if (primer) {
                primer = false;
                url = url + "id[]=" + it.next().toString();
            }
            else {
                url = url + "&id[]=" + it.next().toString();
            }
        }
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

            boolean setFavorite = false;
            boolean existeixObra = false;
            Cursor c=db.rawQuery("SELECT id FROM obres_preferides WHERE id = '"+id+"'",null);
            while(c.moveToNext()) {
                if (c.getString(0).toString().equals(String.valueOf(id))) {
                    existeixObra = true;
                };
            }
            if (!existeixObra) {
                db.execSQL("INSERT into obres_preferides(id) values('" + id + "')");
                setFavorite = true;
            }
            return setFavorite;
        }

        public boolean unsetFavorite(int id) {
            boolean unsetFavorite = false;
            Cursor c=db.rawQuery("SELECT id FROM obres_preferides WHERE id = '"+id+"'",null);
            while(c.moveToNext()) {
                if (c.getString(0).toString().equals(String.valueOf(id))) {
                    db.execSQL("DELETE FROM obres_preferides WHERE id = '" + id + "'");
                };
            }
            return unsetFavorite;
        }
    }
    // Exemple: https://museumalacarte-bustawin.c9.io/artworks?id[]=1&id[]=2
}
