package com.example.darshan.battery1;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


public class MainActivity extends AppCompatActivity {
    TextView tet,song,savepath;
    Handler handler;
    Runnable runnable;
    EditText e1,e2;
    CameraManager c;
    int bt,v1=0,v2=0; // v1 and v2 are used to control notification
    boolean a,b,r,f=false,g=false; // f - to control camera(flash) g- to control vibration
    MediaPlayer mp;
    int q,w,k=0,j=0,l=0;  // control variables : for controlling the loops and some execution steps
    ToggleButton button,button2;
    String cameraID;
    Vibrator vibrator;
    NotificationManagerCompat notificationManagerCompat;
    static MainActivity instance;
    Notification builder;

    Uri Myuri;
    Button btop;

    SharedPreferences sharedPreferences;


    String song_path,song_title;





    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance=this;
        savepath=findViewById(R.id.path);
        tet=findViewById(R.id.text1);
        e1=findViewById(R.id.edt1);
        e2=findViewById(R.id.edt2);
        button=findViewById(R.id.tog);
        button2=findViewById(R.id.toggle2);
        btop=findViewById(R.id.show);
        song = findViewById(R.id.songs);


         sharedPreferences=getSharedPreferences("Savepoint1",MODE_PRIVATE);



        //retrieving song path
        song_path=sharedPreferences.getString("SongPath","Not Selected");
        song_title=sharedPreferences.getString("SongTitle","Not Selected");


        //display path
        savepath.setText(song_path);



        // display percentage
        bt = (int) batterylevel(); // method that returns battery level
        String u = Integer.toString(bt) + "%";
        tet.setText(u);


        // checking whether mobile has camera feature or not
        a=getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        //self check permission
        b= ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED;

        //asking permission
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},60);

        c= (CameraManager)getSystemService(CAMERA_SERVICE);

        //vibrator initialization
        vibrator=(Vibrator)getSystemService(VIBRATOR_SERVICE);

        //notification channel initialization and declaration
        notificationManagerCompat = NotificationManagerCompat.from(this);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            NotificationChannel channel= new NotificationChannel("channel1","notify name", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("notify");
            NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }


    }

    // it is defined to access all the methods of this activity from  other activities
    // see its usage in NotificationReceiver class
    public static   MainActivity getInstance()
    {
       return instance;
    }




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public  void start(View view) {

        String start_per = e1.getText().toString();
        String end_per = e2.getText().toString();

        //checking both edit texts for null
            if (TextUtils.isEmpty(start_per) && TextUtils.isEmpty(end_per))
            {
                Toast.makeText(MainActivity.this, "Enter some values", Toast.LENGTH_SHORT).show();
                if (song_path ==null)
                    Toast.makeText(MainActivity.this,"null",Toast.LENGTH_SHORT).show();

                l=1;
            }

            else
                {
                    btop.setEnabled(false);
                    l=0;
                 // giving some fixed values if none is defined in edit texts
                if (TextUtils.isEmpty(start_per))
                    q = 90;
                else
                    q = Integer.parseInt(e1.getText().toString());

                if (TextUtils.isEmpty(end_per))
                    w = 25;
                else
                    w = Integer.parseInt(e2.getText().toString());

                /* if no songs is selected the song in this app will be used . the song is stored in raw folder . this wont be available at the time when you are
                * seeing this*/
                if ( song_path.equals("Not Selected")) {
                    mp = MediaPlayer.create(getApplicationContext(), R.raw.song);
                }

                else
                    {
                    Myuri = Uri.parse(song_path);
                    Toast.makeText(this,"gfff",Toast.LENGTH_SHORT).show();
                    mp = MediaPlayer.create(MainActivity.this, Myuri);

                }

                r = false;
                if (l == 0) {
                    // q and w are the inputs from user
                    if (q > 0 && w > 0 && q <= 100 && w < 100) {
                        /* There is only one main thread in which our app performs its function . we could have performed the task in
                        *the main thread itself but it results in lagging and sometimes it causes the app to crash
                       * because of overload(high ram usage etc . So it is good practice to do heavy works in
                       * separate threads and these separate threads are called worker threads */

                            runnable = new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void run() {
                                handler.postDelayed(runnable, 10000); // it delays the thread execution by 10secs or in other words thread executes every
                                // 10secs
                                bt = (int) batterylevel();
                                String u = Integer.toString(bt) + "%";
                                tet.setText(u);
                                check();
                                if (button.isChecked()) {

                                    f = true;
                                    try {
                                        cameraa();
                                    } catch (CameraAccessException e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (button2.isChecked()) {
                                    g = true;
                                    vib();

                                }
                                if (q == bt && v1 == 0) {
                                    notificationf();
                                    v1 = 1;
                                }
                                if (w == bt && v2 == 0) {
                                    notificationf();
                                    v2 = 1;
                                }

                            }
                        };
                        handler = new Handler();
                        handler.postDelayed(runnable, 0);




                    } else
                        Toast.makeText(this, "enter values > 0", Toast.LENGTH_SHORT).show();
                }
                    l = 1;

            }
               }



    public void notificationf()
    {
        //for default notification
        Intent intent=new Intent(this,MainActivity.class);//the activity to open when the notification ia clicked
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,0);


        // for notification with button
        Intent intent1=new Intent(this,NotificationReciever.class);
        intent1.putExtra("actionstop","check"); //sending intent to NotificationReceiver class
        PendingIntent pendingIntent1=PendingIntent.getBroadcast(this,0,intent1,PendingIntent.FLAG_UPDATE_CURRENT); // it is used to define
        //what happens when STOP button is clicked

        builder=new NotificationCompat.Builder(this,"channel1")
                .setSmallIcon(R.drawable.addr)
                .setContentTitle("BATTERY")
                .setContentText("battery alert1")
                .setContentIntent(pendingIntent)// used to open the activity when notification is clicked
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("the battery has attained desired percentage"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(R.mipmap.ic_launcher,"STOP",pendingIntent1) // defining button and its action
                .build();
        notificationManagerCompat.notify(1,builder);

    }
// for camera
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void cameraa() throws CameraAccessException {
        cameraID=c.getCameraIdList()[0];
        if (f)
        {
            if (bt == q + 1 || bt == q) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    c.setTorchMode(cameraID, true);
                }
            }
            if (bt == w || bt == w - 1)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    c.setTorchMode(cameraID, true);
                }
        }


   }
   // for vibration
   @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
   public void vib()
   {
       if(g)
       {long[] pattern={2000,3000,2000,3000,2000,3000};

           if (bt == q + 5|| bt == q) {
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
               vibrator.vibrate(VibrationEffect.createWaveform(pattern,5));
           }
           }
           if (bt == w || bt == w - 5) {
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                   vibrator.vibrate(VibrationEffect.createWaveform(pattern, 5));
               }
           }
       }
   }

    public void check() // for playing media player
    {
        if(!mp.isPlaying())
        {
            if (bt == q+5 || bt== q )
            {
                if (mp != null)
                {
                    if (j < 1)
                        mp.start();
                }
            }
            if (bt == w || bt== w-5)
            {

                if (mp != null)
                {
                    if (k < 1)
                        mp.start();

                }

            }
        }

    }

  // getting battery satus
    public float batterylevel() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent intent = registerReceiver(null, intentFilter);
        assert intent != null;
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        if (level == -1 && scale == -1) {
            return 50.0f;
        }
        return (float)level /(float) scale*100.0f;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void stop(View view) throws CameraAccessException {
        stop1(); // to check what is currently active and  to stop it
        btop.setEnabled(true);
    }

    // used control variables which i mentioned above to know its status(of flash, vibrator and song) and to make it stop
    @RequiresApi(api = Build.VERSION_CODES.M)

    public  void stop1() throws CameraAccessException {
        if (l == 1) {

            if (mp.isPlaying()) {
                mp.stop();
            }


            if (f) {
                c.setTorchMode(cameraID, false);
                button.setChecked(false);
            }
            if (g) {
                vibrator.cancel();
                button2.setChecked(false);
            }
            e1.setText("");
            e2.setText("");
            r = true;
            l = 0;
            g=false;
            f=false;
            v1=0;
            v2=0;
handler.removeCallbacks(runnable);
        }
    }


    //select songs
    public void Select_songs(View view)
    {
        Intent i= new Intent(MainActivity.this,Main2Activity.class);
        startActivity(i);

        //song.setText(e);
    }
    public void text()
    {

        //retrieving song path
        song_path=sharedPreferences.getString("SongTitle","Not Selected");

        //this will give the path of song when selected
        song_title=sharedPreferences.getString("SongPath","Not Selected");


        //display path
        savepath.setText(song_title);
    }
    public void button()
    {
        btop.setEnabled(true);
    }

}
