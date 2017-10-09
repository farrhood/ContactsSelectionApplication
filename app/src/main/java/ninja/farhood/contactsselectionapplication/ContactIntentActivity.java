package ninja.farhood.contactsselectionapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ContactIntentActivity extends AppCompatActivity {

    private List<ContactObject> contactsList;

    private int RUNTIME_PERMISSION_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate your view
        setContentView(R.layout.activity_contact_intent);
        ListView intentListView = (ListView) findViewById(R.id.listView1);

        contactsList = new ArrayList<>();

        contactsList.add(new ContactObject("Android One", "111-1111-1111", "www.androidATC.com"));

        contactsList.add(new ContactObject("Android Two", "222-2222-2222", "www.androidATC.com"));

        contactsList.add(new ContactObject("Android Three", "333-3333-3333", "www.androidATC.com"));

        contactsList.add(new ContactObject("Android Four", "444-4444-4444", "www.androidATC.com"));

        List<String> listName = new ArrayList<>();

        for(int i = 0 ; i < contactsList.size() ; i++) {
            listName.add(contactsList.get(i).getName());
        }

        // Initialize the ArrayAdapter object
        ArrayAdapter<String> adapter = new ArrayAdapter<>(ContactIntentActivity.this, android.R.layout.simple_list_item_1, listName);

        intentListView.setAdapter(adapter);

        intentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(ContactIntentActivity.this, ContactPageActivity.class);

                i.putExtra("Object", contactsList.get(position));
                startActivityForResult(i, 0);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data == null) {
            return;
        }

        Bundle resultData = data.getExtras();
        String value = resultData.getString("value");
        switch (resultCode) {

            case Constants.PHONE:
                // Implicit intent to make a call
                makeCall(value);

                break;

            case Constants.WEBSITE:
                // Implicit intent to visit website
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + value)));

                break;

        }

    }

    // We are calling this method to check the permission status

    private void makeCall(String value) {
        // Getting the permission status
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

        // If permission is granted return true
        if (result == PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + value)));
        } else {
            requestCallPermission();
        }
    }

    // Requesting permission

    private void requestCallPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {

            // If the user has denied the permission previously your code will come to this block
            // Explaining why you need this permission
            explainPermissionDialog();
        }

        // Ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, RUNTIME_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        // Checking the request code of our request
        if (requestCode == RUNTIME_PERMISSION_CODE) {

            // If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Displaying a toast
                Toast.makeText(this, "Permission granted you can now make the phone call;", Toast.LENGTH_LONG).show();
            } else {
                // Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops, you just denied the permission.", Toast.LENGTH_LONG).show();
            }

        }

    }

    private void explainPermissionDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // Set title
        alertDialogBuilder.setTitle("Call Permission Required");

        // Set dialog message
        alertDialogBuilder.setMessage("This application requires call permission to make phone calls. Please grant the permission.")
                .setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
               dialog.dismiss();
           }
        });

        // Create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // Show it
        alertDialog.show();

    }

}
