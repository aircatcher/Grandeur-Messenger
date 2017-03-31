package xyz.grand.grandeur;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import static xyz.grand.grandeur.R.id.button;

public class PostTimeline extends AppCompatActivity
{
    private static int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_timeline);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TextView textURL = (TextView) findViewById(R.id.timeline_url);
        Button buttonAddURL = (Button) findViewById(R.id.button_add_url);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_create_post);

        // Show Popup Alert
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        final EditText popupAddURL = new EditText(this);
        popupAddURL.setHint("Enter a URL here ...");
        popupAddURL.setPadding(5, 5, 5, 5);
        alertDialogBuilder.setView(popupAddURL);

        alertDialogBuilder.setCancelable(false).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id) {}
        });
        alertDialogBuilder.setCancelable(false).setPositiveButton("Confirm", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id) { textURL.setText((CharSequence) popupAddURL); }
        });

        // Add URL to the timeline post
        buttonAddURL.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        // Add the post to timeline
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                textURL.setText();
            }
        });
    }

}
