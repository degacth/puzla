package com.degacth.alexander.puzla2.drawer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.SurfaceView;

import com.degacth.alexander.puzla2.GameActivity;

import java.io.FileNotFoundException;
import java.util.Random;

/**
 * Created by alexander on 19/04/16.
 */
public class GameOverDrawer extends Drawer {

    Bitmap bitmap;

    public GameOverDrawer(Context context, SurfaceView surfaceView) {
        super(surfaceView);
        try {
            bitmap = BitmapFactory.decodeStream(context.openFileInput(GameActivity.TILE_IMAGE_FILE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void drawImage() {
        Firework firework = new Firework();
        Random random = new Random();

        canvas = holder.lockCanvas();
        canvas.drawColor(Color.WHITE);
        int margin = (canvas.getHeight() - canvas.getWidth()) / 2;
        int step = canvas.getHeight() / 50;
        for (int i = 0 - step; i <= canvas.getHeight() / step; i++) {
            paint.setARGB(255, random.nextInt(255), random.nextInt(255), random.nextInt(255));
            paint.setStrokeWidth((float) (step / 1.3));
            canvas.drawLine(0 - step, i * step, canvas.getWidth() + step, i * step + step * 10, paint);
        }
        paint.setARGB(255, 0, 0, 0);
        canvas.drawBitmap(bitmap, 0, margin, paint);
        firework.draw(canvas);
        holder.unlockCanvasAndPost(canvas);
    }
}
