package com.example.contentproviderdemo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ContactsActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private TextView textViewContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        dbHelper = new DatabaseHelper(this);
        textViewContacts = findViewById(R.id.textViewContacts);

        Button btnAddContact = findViewById(R.id.btnAddContact);
        btnAddContact.setOnClickListener(v -> addDummyContact());

        loadContacts();
    }

    private void addDummyContact() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, "John Doe");
        values.put(DatabaseHelper.COLUMN_PHONE, "0123456789");

        long newRowId = db.insert(DatabaseHelper.TABLE_NAME, null, values);
        Log.d("DB", "Inserted contact with ID: " + newRowId);

        loadContacts();
    }

    private void loadContacts() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, null);

        StringBuilder contacts = new StringBuilder();
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
            String phone = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PHONE));
            contacts.append(name).append(" - ").append(phone).append("\n");
        }
        cursor.close();
        textViewContacts.setText(contacts.toString());
    }
}
