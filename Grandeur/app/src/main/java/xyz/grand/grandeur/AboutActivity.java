package xyz.grand.grandeur;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    private PackageInfo packageInfo;
    private int appVersion;
    private String appVersionName;
    private TextView showAppVersion;
    private Button btnGoBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        showAppVersion = (TextView) findViewById(R.id.show_app_version);
        btnGoBack = (Button) findViewById(R.id.btn_about_go_back);

        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersion = packageInfo.versionCode;
            appVersionName = packageInfo.versionName;
            showAppVersion.setText(appVersionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
