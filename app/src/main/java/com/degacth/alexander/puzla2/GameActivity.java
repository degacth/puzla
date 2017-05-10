package com.degacth.alexander.puzla2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Toast;

import com.degacth.alexander.puzla2.drawer.GameDrawer;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

public class GameActivity extends Activity implements GameOverListener {

    private GameDrawer drawer;
    final public static String TILE_IMAGE_FILE = "tileImageFile.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Bundle data = getIntent().getExtras();
        int size = data.getInt("size");
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(openFileInput(TILE_IMAGE_FILE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        SurfaceView surface = (SurfaceView) findViewById(R.id.gameSurface);
        int tilesCount = data.getInt("tilesCount");
        surface.getHolder().setFixedSize(size, size);
        drawer = new GameDrawer(surface, bitmap, size, tilesCount);
        drawer.addGameOverListener(this);

        if (savedInstanceState != null) {
            drawer.setTilesPositions(savedInstanceState.getIntegerArrayList("tilesPositions"));
        } else {
            drawer.setTilesPositions(getRandom(tilesCount));
        }

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("6CFEAA8EC4686B9E8119AF1BF4F11AC1")
                .build();
        mAdView.loadAd(adRequest);

    }

    private ArrayList<Integer> getRandom(int tilesCount) {
        ArrayList<Integer> ints = new ArrayList<>();
        for (int i = 0; i < tilesCount * tilesCount; i++) ints.add(i);
        Collections.shuffle(ints);
        return ints;
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        state.putIntegerArrayList("tilesPositions", drawer.getTilesPositions());
    }

    @Override
    protected void onResume() {
        super.onResume();
        drawer.draw();
    }

    @Override
    public void gameOver() {
        Intent intent = new Intent(this, GameOverActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        drawer.gameStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}
