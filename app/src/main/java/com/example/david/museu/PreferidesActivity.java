package com.example.david.museu;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;


public class PreferidesActivity extends ActionBarActivity {

    SQLiteDatabase db;
    ArrayList<String> ids = new ArrayList();
    WebView wv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferides);
        db=openOrCreateDatabase("Database", Context.MODE_PRIVATE, null);
        Cursor c=db.rawQuery("SELECT id FROM obres_preferides",null);
        while(c.moveToNext()) {
            ids.add(c.getString(0).toString());
        }
        wv = (WebView) findViewById(R.id.webView);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebChromeClient(new WebChromeClient());
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
            Toast.makeText(mContext, "Entra a la funcio d'afegir a preferit l'obra "+id, Toast.LENGTH_LONG).show();
            boolean setFavorite = false;
            boolean existeixObra = false;
            Cursor c=db.rawQuery("SELECT id FROM obres_preferides WHERE id = '"+id+"'",null);
            while(c.moveToNext()) {
                if (c.getString(0).toString().equals(String.valueOf(id))) {
                    existeixObra = true;
                };
            }
            if (!existeixObra) {
                Toast.makeText(mContext, "Arriba a inserir l'obra a BD "+id, Toast.LENGTH_LONG).show();
                db.execSQL("INSERT into obres_preferides(id) values('" + id + "')");
                setFavorite = true;
            }
            return setFavorite;
        }
        @JavascriptInterface
        public boolean unsetFavorite(int id) {
            Toast.makeText(mContext, "Entra a la funcio d'esborrar preferit l'obra "+id, Toast.LENGTH_LONG).show();
            boolean unsetFavorite = false;
            Cursor c=db.rawQuery("SELECT id FROM obres_preferides WHERE id = '"+id+"'",null);
            while(c.moveToNext()) {
                if (c.getString(0).toString().equals(String.valueOf(id))) {
                    Toast.makeText(mContext, "Arriba a borrar l'obra a BD"+id, Toast.LENGTH_LONG).show();
                    db.execSQL("DELETE FROM obres_preferides WHERE id = '" + id + "'");
                };
            }
            return unsetFavorite;
        }
        @JavascriptInterface
        public boolean isFavorite(int id) {
            Toast.makeText(mContext, "Entra a la funcio isFavorite amb id: " + id, Toast.LENGTH_LONG).show();
            boolean isFavorite = false;
            Cursor c=db.rawQuery("SELECT id FROM obres_preferides WHERE id = '"+id+"'",null);
            if (c.getCount() > 0) {
                isFavorite = true;
                Toast.makeText(mContext, "Entra a la funcio isFavorite = true " + id, Toast.LENGTH_LONG).show();
                wv.loadUrl("javascript:favorited(1);");
            }
            return isFavorite;
        }
    }
    // Exemple: https://museumalacarte-bustawin.c9.io/artworks?id[]=1&id[]=2
}
