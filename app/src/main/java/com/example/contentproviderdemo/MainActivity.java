package com.example.contentproviderdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private TextView txtContacts;
    private EditText edtDeleteContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        txtContacts = findViewById(R.id.txtContacts);
        edtDeleteContact = findViewById(R.id.edtDeleteContact);
    }

    public void btnGetContactPressed(View v) {
        getPhoneContacts();
    }

    private void getPhoneContacts() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 0);
            return;
        }

        ContentResolver contentResolver = getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        if (cursor == null) return;

        StringBuilder contacts = new StringBuilder();
        Log.i("CONTACT_PROVIDER_DEMO", "TOTAL # of Contacts ::: " + cursor.getCount());

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String contactName =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range") String contactNumber =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                contacts.append(contactName).append(" : ").append(contactNumber).append("\n");
                Log.i("CONTACT_PROVIDER_DEMO", "Contact Name ::: " + contactName + " Ph#  :::   " + contactNumber);
            }
        }
        txtContacts.setText(contacts.toString());
        cursor.close();
    }

    public void btnDeleteContactPressed(View v) {
        String contactNameToDelete = edtDeleteContact.getText().toString().trim();
        if (contactNameToDelete.isEmpty()) {
            Toast.makeText(this, "Enter contact name to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, 1);
            return;
        }

        deleteContact(contactNameToDelete);
    }

    //go to contact
    public void goToContacts(View v) {
        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);
    }


    private void deleteContact(String contactName) {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = ContactsContract.RawContacts.CONTENT_URI;

        Cursor cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID},
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?",
                new String[]{contactName},
                null
        );

        if (cursor == null || !cursor.moveToFirst()) {
            Toast.makeText(this, "Contact not found", Toast.LENGTH_SHORT).show();
            return;
        }

        @SuppressLint("Range") String contactId =
                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
        cursor.close();

        int deletedRows = contentResolver.delete(
                uri,
                ContactsContract.RawContacts.CONTACT_ID + " = ?",
                new String[]{contactId}
        );

        if (deletedRows > 0) {
            Toast.makeText(this, "Contact deleted", Toast.LENGTH_SHORT).show();
            getPhoneContacts();
        } else {
            Toast.makeText(this, "Failed to delete contact", Toast.LENGTH_SHORT).show();
        }
    }

}
