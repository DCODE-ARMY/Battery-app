package com.example.darshan.battery1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import static android.widget.Toast.*;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NotificationReciever extends BroadcastReceiver {
   // MainActivity a=new MainActivity();
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent)
    {   // getting the intent sent when the Stop button is pressed
        // putting toast to make sure that the intent is received
        String a=intent.getStringExtra("actionstop");
        Toast.makeText(context,a,Toast.LENGTH_SHORT).show();
        try {
            // performing the required task once the stop button is pressed
            //calling methods from activity from MainActivity
            //to see how it works see line 134 in MainActivity
                MainActivity.getInstance().stop1();
                MainActivity.getInstance().button();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }




    }
}
