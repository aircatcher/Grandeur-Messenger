package xyz.grand.grandeur.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;

import xyz.grand.grandeur.MainActivity;
import xyz.grand.grandeur.R;

public class PinLockSetupActivity extends AppCompatActivity
{
    private Pinview mPinView;
    private Toolbar toolbar;
    public static int pinLockLength;
    public static String pinLockValue;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_lock_setup);
        mPinView = (Pinview) findViewById(R.id.pin_lock_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar_pin_lock_setup);

        Toast.makeText(getApplicationContext(), "PIN Lock is currently not working just yet", Toast.LENGTH_LONG).show();

        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        mPinView.setPinViewEventListener(new Pinview.PinViewEventListener()
        {
            @Override
            public void onDataEntered(Pinview pinview, boolean b)
            {
                if(pinview.getPinLength() == 4)
                {
                    pinLockLength = pinview.getPinLength();
                    pinLockValue = pinview.getValue();

                    Intent pl_send = new Intent(PinLockSetupActivity.this, SettingsActivity.class);
                    Intent pl_sendToMain = new Intent(PinLockSetupActivity.this, MainActivity.class);
                    pl_send.putExtra("pinLockValue", pinLockValue);
                    pl_sendToMain.putExtra("pinLockValue", pinLockValue);

                    finish();
                }
            }
        });
    }
}
