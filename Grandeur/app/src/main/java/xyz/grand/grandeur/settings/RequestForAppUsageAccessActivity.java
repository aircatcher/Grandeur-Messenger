package xyz.grand.grandeur.settings;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import xyz.grand.grandeur.R;

public class RequestForAppUsageAccessActivity extends AppCompatActivity
{
    private Button buttonProceed, buttonCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_for_app_usage_access);

        if(appUsageAccessEnabled())
        {
            finish();
            startActivity(new Intent(RequestForAppUsageAccessActivity.this, PinLockSetupActivity.class));
        }
        else
        {
            buttonProceed = (Button) findViewById(R.id.button_proceed_enable_usage_access);
            buttonCancel = (Button) findViewById(R.id.button_cancel_enable_usage_access);

            buttonProceed.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                }
            });
            buttonCancel.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    finish();
                }
            });
        }
    }

    private boolean appUsageAccessEnabled()
    {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }
}
