package com.degacth.alexander.puzla2.drawer;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.SurfaceView;

/**
 * Created by alexander on 14/04/16.
 */
public class SelectDrawer extends Drawer {

    Bitmap bitmap;
    private int tilesCount;

    public SelectDrawer(SurfaceView surfaceView) {
        super(surfaceView);
    }


    synchronized protected void drawImage() {
        canvas = holder.lockCanvas();
        paint.setARGB(255, 255, 255, 255);
        canvas.drawColor(Color.BLACK);

        if (bitmap != null) {
            bitmap = getThumbnail(bitmap, canvas.getWidth(), canvas.getHeight());
            canvas.drawBitmap(bitmap, 0, 0, paint);
        }

        drawGrid(canvas, tilesCount);
        holder.unlockCanvasAndPost(canvas);
    }

    public void draw(Bitmap bitmap) {
        this.bitmap = bitmap;
        super.draw();
    }

    public int getTilesCount() {
        return tilesCount;
    }

    public void setTilesCount(int tilesCount) {
        this.tilesCount = tilesCount;
    }
}
