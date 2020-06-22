package com.example.darshan.battery1;

import android.Manifest;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashMap;

public class
Main2Activity extends AppCompatActivity {
    ListView listView; // listview to display songs in UI
    String SongPath,SongTitle;
    Runnable runnable;
    ArrayAdapter<String> adapter;
    ArrayList<HashMap<String,String>> song_arrayList;
    ArrayList<String> song_title=new ArrayList<>();   // contains all the song list
    Handler handler= new Handler();
    Uri song_uri;
    /* sharedPreference is used to store values locally in the device and it is visible only within this app and SharedPreferences.Editor  is used
    to modify data in it */
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        listView = findViewById(R.id.list);
        // getting permission to read internal storage
        if(ContextCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(Main2Activity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},5);
        }
       // showSongs();

        // retrieving the saved song path for further usage
        sharedPreferences=getSharedPreferences("Savepoint1",MODE_PRIVATE);
        SongPath=sharedPreferences.getString("SongPath","Not Selected"); // returns not selected initially
    }


    public void showSongs(View view)

    {
        song_arrayList = new ArrayList<>(); // array list to display  list of songs obtained from thread
        getSong(); // method calling to get and display songs
        new Thread(runnable).start(); //starting the worker thread defined in getSongs()

        /* There is only one main thread in which our app performs its function . we could have performed the task in the main thread itself but
        * it results in lagging and sometimes it causes the app to crash because of overload(high ram usage etc . So it is good practice to do heavy works in
        * separate threads and these separate threads are called worker threads */

        // getting song's path and its title
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(Main2Activity.this, position+ "  " + id, Toast.LENGTH_SHORT).show();
                //song_arrayList is a hashmap arraylist which contains path and title in it
                 SongPath =song_arrayList.get(position).get("path");
                 SongTitle=song_arrayList.get(position).get("title");

            }
        });
    }


    public void getSong() {
        song_arrayList = new ArrayList<>();// hashmap arraylist to store song's path and title
        Toast.makeText(this," checking  ",Toast.LENGTH_SHORT).show();
        // defining heavy tasks in worker thread
        runnable =new Runnable() {
            @Override
            public void run() {
                ContentResolver contentResolver=getContentResolver();
                song_uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//uri of whole song list  (as single uri)

                //now the uri is unpacked and stored in cursor so that we have access to each songs
                Cursor cursor = contentResolver.query(song_uri,null,null,null,null);

                if (cursor !=null && cursor.moveToFirst())
                {
                    /* imagine cursor as a database table . Every property(Title,Author,Data(path), Runtime) is stored in separate in separate column .
                    * so we are getting its columnindex to retrieve information   */
                    int songTitle=cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);  // returns the  integer position of the title of the song
                    int songpath=cursor.getColumnIndex(MediaStore.Audio.Media.DATA);   //returns the integer position of the path of the song
                    do {
                        String title=cursor.getString(songTitle); // cursor.getString(get the string at the given position)
                        String path=cursor.getString(songpath);

                        HashMap<String,String> hashMap=new HashMap<>(); // used to store key,value pairs
                        hashMap.put("title",title);
                        hashMap.put("path",path);
                        song_title.add(title);
                        song_arrayList.add(hashMap); // the value is stored like title:path
                    }while(cursor.moveToNext());
                }
                assert cursor != null;
                cursor.close();

                // linking worker thread to main thread
                /*after the task is completed in worker thread the result should be given to main thread for further use. No UI task can be done in worker
                * thread . So it is must to link your worker thread to main thread */
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Main2Activity.this, "main thread", Toast.LENGTH_SHORT).show();

                        adapter= new ArrayAdapter<>(Main2Activity.this,android.R.layout.simple_list_item_1,song_title);  // updating the array list with the song title
                        listView.setAdapter(adapter);
                    }
                });// end of linker thread
            }// end of void run
        };//end of worker thread

    }//end of method a()

    // this function executes when next button is clicked
    public void next(View view){
        onBackPressed();

    }
/* onBackPressed : when back button is pressed the following function runs . */
    @Override
    public void onBackPressed() {
        editor=sharedPreferences.edit();
        editor.putString("SongPath",SongPath);
        editor.putString("SongTitle",SongTitle);
        editor.apply();
        MainActivity.getInstance().text(); // text() is defined in MainActivity which displays the song's path and title
        super.onBackPressed(); // back pressed works only at this point(ie when super.onBackpressed() is called)
        // writing code after this wont execute because you  are navigated to other activity and this activity dies once we are navigated to other activity

    }
}
