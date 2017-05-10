package com.degacth.alexander.puzla2;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;

import com.degacth.alexander.puzla2.drawer.GameOverDrawer;

public class GameOverActivity extends Activity {
    GameOverDrawer drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        drawer = new GameOverDrawer(this, (SurfaceView) findViewById(R.id.gameOverSurface));

        findViewById(R.id.gameOverSurface).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        drawer.draw();
    }
}
