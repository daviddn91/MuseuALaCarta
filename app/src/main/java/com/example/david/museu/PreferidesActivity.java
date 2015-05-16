package com.example.david.museu;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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

    // Exemple: https://museumalacarte-bustawin.c9.io/artworks?id[]=1&id[]=2
}
