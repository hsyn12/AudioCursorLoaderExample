package com.xyz.audiocursorloaderexample;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    
        String[] cols = {
            
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.TITLE
        };
    
    
        CursorLoader cursorLoader = new CursorLoader(
            
                getApplicationContext(),
                MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
                cols,
                null,
                null,
                null);
    
    
        return cursorLoader;
    }
    
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        
        
    }
    
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        
    }
}