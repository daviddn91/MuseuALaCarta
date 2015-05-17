package com.example.david.museu;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {

    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button boton = (Button) findViewById(R.id.bServer);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nuevoform = new Intent(MainActivity.this, ServerActivity.class);
                startActivity(nuevoform);
            }
        });
        db=openOrCreateDatabase("Database", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS obres_preferides(id VARCHAR);");
        db.execSQL("INSERT into obres_preferides(id) values('1')");
        Button boton2 = (Button) findViewById(R.id.bPreferides);
        boton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nuevoform = new Intent(MainActivity.this, PreferidesActivity.class);
                startActivity(nuevoform);
            }
        });
    }

}
