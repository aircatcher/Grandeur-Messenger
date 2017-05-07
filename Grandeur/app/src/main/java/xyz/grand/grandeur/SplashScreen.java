package xyz.grand.grandeur;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Ferick Andrew on May 07, 2017.
 */

public class SplashScreen extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(2000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        finish();
    }
}