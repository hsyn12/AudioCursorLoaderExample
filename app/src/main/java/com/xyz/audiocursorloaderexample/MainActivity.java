package com.xyz.audiocursorloaderexample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {


    final private String tag = "MainActivity";
    final private int LOADER_ID = 33;


    String[] cols = {

            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE
    };


    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        run();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        CursorLoader cursorLoader = new CursorLoader(

                getApplicationContext(),
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                cols,
                null,
                null,
                cols[0] + " ASC");


        return cursorLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

        recyclerView.setAdapter(new RecyclerViewAdapter(data));
        
/*
        int nameColumn     = data.getColumnIndex(cols[0]);
        int durationColumn = data.getColumnIndex(cols[1]);
        int sizeColumn     = data.getColumnIndex(cols[2]);

        while (data.moveToNext()) {
            
            String  name     = data.getString(nameColumn),
                    duration = data.getString(durationColumn),
                    size     = data.getString(sizeColumn);
            
            

            String message = String.format(
                    
                        "name       : %s\n" + 
                        "duration   : %s\n" +
                        "size       : %s\n",

                        name, duration, size);
            
            
            Log.i(tag, message);
        }*/


    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        recyclerView.setAdapter(null);
    }


    public void run() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

            runWithPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    this::initLoader, 3);

        }

    }


    int requestCode;
    PermissionCallback permissionCallback;

    public void runWithPermission(@NonNull String permission, @NonNull PermissionCallback permissionCallback, int permissionRequestCode) {

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {

            Log.i(tag, "izne gerek yok");
            permissionCallback.run();
        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                this.permissionCallback = permissionCallback;
                requestCode = permissionRequestCode;

                requestPermissions(new String[]{permission}, permissionRequestCode);

            } else {

                Log.i(tag, "istenen izinde sorun var");
            }


        }

    }

    private void initLoader() {

        Log.i(tag, "loader init");
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @FunctionalInterface
    interface PermissionCallback {
        void run();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        if (requestCode == this.requestCode) {

            permissionCallback.run();

        }


    }


    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        Cursor cursor;

        RecyclerViewAdapter(Cursor cursor) {

            this.cursor = cursor;
            cursor.moveToNext();

        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.music_card, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            cursor.moveToPosition(position);
            
            holder.name.setText(cursor.getString(cursor.getColumnIndex(cols[0])));
            holder.duration.setText(formateMilliSeccond(cursor.getLong(cursor.getColumnIndex(cols[1]))));
            holder.size.setText(String.format("%.2f MB", Float.valueOf(cursor.getString(cursor.getColumnIndex(cols[2]))) / (1024 * 1024)));
        }

        @Override
        public int getItemCount() {
            return cursor.getCount();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView name, duration, size;

            ViewHolder(View itemView) {
                super(itemView);

                name        = itemView.findViewById(R.id.textViewName);
                duration    = itemView.findViewById(R.id.textViewDuration);
                size        = itemView.findViewById(R.id.textViewSize);
            }
        }


    }


    String formateMilliSeccond(long milliseconds) {

        String finalTimerString = "";
        String secondsString;

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        }
        else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        //      return  String.format("%02d Min, %02d Sec",
        //                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
        //                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
        //                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));

        // return timer string
        return finalTimerString;
    }


}