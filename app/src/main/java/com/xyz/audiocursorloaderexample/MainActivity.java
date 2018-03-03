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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {


    final private String tag             = "MainActivity";
    final private int    LOADER_AUDIO_ID = 33;


    String[] AUDIO_COLUMNS = {

            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DURATION
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


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getApplicationContext(),
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                AUDIO_COLUMNS,
                null,
                null,
                AUDIO_COLUMNS[0] + " ASC");

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

        Log.i(tag, "onLoadFinished : " + data.getCount());


        recyclerView.swapAdapter(new RecyclerViewAdapter(data), true);


    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        recyclerView.swapAdapter(null, true);
    }


    public void run() {


        runWithPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                this::initLoader,
                3);


    }

    private void initLoader() {

        Log.i(tag, "loader init");

        getSupportLoaderManager().initLoader(LOADER_AUDIO_ID, null, this);


    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        Cursor cursor;

        RecyclerViewAdapter(@NonNull Cursor cursor) {

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
            holder.textView1.setText(cursor.getString(cursor.getColumnIndex(AUDIO_COLUMNS[0])));
            holder.textView3.setText(String.format("%.2f MB", Float.valueOf(cursor.getString(cursor.getColumnIndex(AUDIO_COLUMNS[1]))) / (1024 * 1024)));
            holder.textView2.setText(formateMilliSeccond(cursor.getLong(cursor.getColumnIndex(AUDIO_COLUMNS[2]))));

        }

        @Override
        public int getItemCount() {
            return cursor.getCount();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView textView1, textView2, textView3;

            ViewHolder(View itemView) {
                super(itemView);

                textView1 = itemView.findViewById(R.id.textView1);
                textView2 = itemView.findViewById(R.id.textView2);
                textView3 = itemView.findViewById(R.id.textView3);
            }
        }
    }

    @FunctionalInterface
    interface PermissionCallback {
        void run();
    }

    int                requestCode;
    PermissionCallback permissionCallback;

    public void runWithPermission(@NonNull String permission, @NonNull PermissionCallback permissionCallback, int permissionRequestCode) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {

                Log.i(tag, "izin var");
                permissionCallback.run();

            }
            else {

                this.permissionCallback = permissionCallback;
                requestCode = permissionRequestCode;

                requestPermissions(new String[]{permission}, permissionRequestCode);
            }

        }
        else {

            Log.i(tag, "izne gerek yok");
            permissionCallback.run();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        if (requestCode != this.requestCode) return;

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            permissionCallback.run();
        }
        else {

            Log.i(tag, "izin reddedildi");
        }

        this.requestCode = -1;

    }

    @NonNull
    String formateMilliSeccond(long milliseconds) {

        String finalTimerString = "";
        String secondsString;

        // Convert total textView2 into time
        int hours   = (int) (milliseconds / (1000 * 60 * 60));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return super.onOptionsItemSelected(item);
    }


    public static String getDate(String milis) {

        Date date;

        try {

            date = new Date(Long.valueOf(milis));

        }
        catch (NumberFormatException e) {

            return milis;
        }

        return String.format(new Locale("tr"), "%te %<tB %<tY %<tA %<tH:%<tM:%<tS", date);
    }
}