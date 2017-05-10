package com.degacth.alexander.puzla2.drawer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.ThumbnailUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by alexander on 14/04/16.
 */
abstract class Drawer implements Runnable {
    SurfaceView surface;
    SurfaceHolder holder;
    Paint paint;
    Canvas canvas;

    abstract protected void drawImage();

    Drawer(SurfaceView surfaceView) {
        surface = surfaceView;
        holder = surface.getHolder();
        paint = new Paint();
    }

    protected Bitmap getThumbnail(Bitmap bitmap, int sizeX, int sizeY) {
        return ThumbnailUtils.extractThumbnail(bitmap, sizeX, sizeY);
    }

    protected void drawGrid(Canvas canvas, int grid) {
        drawGrid(canvas, grid, 0);
    }

    protected void drawGrid(Canvas canvas, int grid, int topMargin) {
        int height = canvas.getHeight();
        int tileSize = height / grid;
        int strokeWidth = height / 200;
        paint.setStrokeWidth(strokeWidth);

        for (int i = 1; i < grid; i++) {
            canvas.drawLine(0, i * tileSize + topMargin, height, i * tileSize + topMargin, paint);
            canvas.drawLine(i * tileSize, 0 + topMargin, i * tileSize, height + topMargin, paint);
        }

        for (int i = 0; i < 2; i++) {
            int diff = ((i == 1) ? -1 : 1)*(strokeWidth / 2);
            canvas.drawLine(0, i * height + diff, height, i * height + diff, paint);
            canvas.drawLine(i * height + diff, 0, i * height + diff, height, paint);
        }
    }


    public void draw() {
        surface.post(this);
    }

    @Override
    public void run() {
        if (holder.getSurface().isValid()) drawImage();
        else {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            run();
        }
    }
}
