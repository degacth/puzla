package com.degacth.alexander.puzla2.drawer;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import com.degacth.alexander.puzla2.GameOverListener;

import java.util.ArrayList;

/**
 * Created by alexander on 18/04/16.
 */
public class GameDrawer extends Drawer implements View.OnTouchListener {

    private Bitmap bitmap;
    private int tilesCount;
    private int fullTilesCount;
    private int tileSize;
    private ArrayList<Integer> tilesPositions;
    private Bitmap[] tiles;
    private int selectedIndex;
    private int size;
    private ArrayList<GameOverListener> gameOverListeners = new ArrayList<>();

    public GameDrawer(SurfaceView surfaceView, Bitmap bitmap, int size, int tilesCount) {
        super(surfaceView);

        this.bitmap = bitmap;
        this.size = size;
        this.tilesCount = tilesCount;
        this.selectedIndex = -1;
        createGame();
        surface.setOnTouchListener(this);
    }

    private void createGame() {
        tileSize = size / tilesCount;
        fullTilesCount = (int) Math.pow(tilesCount, 2);
        tiles = new Bitmap[fullTilesCount];
        for (int i = 0; i < fullTilesCount; i++)
            tiles[i] = bitmap.createBitmap(bitmap, (i % tilesCount) * tileSize, ((int) Math.floor(i / tilesCount)) * tileSize, tileSize, tileSize);
    }

    @Override
    protected void drawImage() {
        canvas = holder.lockCanvas();
        paint.setARGB(255, 0, 0, 0);
        drawTiles();

        paint.setARGB(255, 255, 255, 255);
        drawGrid(canvas, tilesCount);

        drawSelectedIndex();

        holder.unlockCanvasAndPost(canvas);
        checkGameOver();
    }

    private void drawSelectedIndex() {
        if (!hasSelected()) return;

        Bitmap tile = tiles[tilesPositions.get(selectedIndex)];
        int diff = size / 30;
        Bitmap scaled = Bitmap.createScaledBitmap(tile, diff + tile.getWidth(), diff + tile.getWidth(), false);
        Point coords = getCoordsByIndex(selectedIndex);
        int x = coords.x * tileSize - diff / 2;
        int y = coords.y * tileSize  - diff / 2;
        int padding = size / 180;
        x = Math.min(Math.max(x, padding), (tilesCount - 1) * tileSize - diff - padding);
        y = Math.min(Math.max(y, padding), (tilesCount - 1) * tileSize - diff - padding);
        Rect rect = new Rect(x - padding, y - padding, x + diff + tileSize + padding, y + diff + tileSize + padding);
        canvas.drawRect(rect, paint);
        canvas.drawBitmap(scaled, x, y, paint);
    }

    private void drawTiles() {
        for (int i = 0; i < fullTilesCount; i++)
            canvas.drawBitmap(tiles[tilesPositions.get(i)], (i % tilesCount) * tileSize, ((int) Math.floor(i / tilesCount)) * tileSize, paint);
    }

    public ArrayList<Integer> getTilesPositions() {
        return tilesPositions;
    }

    public void setTilesPositions(ArrayList<Integer> tilesPositions) {
        this.tilesPositions = tilesPositions;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                surfaceDown(x, y);
                view.post(this);
                break;
        }

        return true;
    }

    private void surfaceDown(int x, int y) {
        if (!hasSelected()) {
            setSelectedIndex(x, y);
            return;
        }

        replaceSelected(x, y);
    }

    private void replaceSelected(int x, int y) {
        Point point = transformCoordinates(x, y);
        int newIndex = getIndexByCoords(point.x, point.y);
        int stack = tilesPositions.get(selectedIndex);

        tilesPositions.set(selectedIndex, tilesPositions.get(newIndex));
        tilesPositions.set(newIndex, stack);
        selectedIndex = -1;
    }

    private void checkGameOver() {
        for (int i = 0; i < fullTilesCount; i++) if (i != tilesPositions.get(i)) return;
        for (GameOverListener listener: gameOverListeners) listener.gameOver();
    }

    private void setSelectedIndex(int x, int y) {
        Point point = transformCoordinates(x, y);
        selectedIndex = getIndexByCoords(point.x, point.y);
    }

    private Point transformCoordinates(int x, int y) {
        return new Point(Math.round(x / tileSize), Math.round(y / tileSize));
    }

    private int getIndexByCoords(int x, int y) {
        return x + y * tilesCount;
    }

    private Point getCoordsByIndex(int index) {
        return new Point(index % tilesCount, index / tilesCount);
    }

    private boolean hasSelected() {
        return selectedIndex >= 0;
    }

    public void addGameOverListener(GameOverListener listener) {
        gameOverListeners.add(listener);
    }

    public void gameStop() {
        if (bitmap != null) bitmap.recycle();
        for (Bitmap tile: tiles) tile.recycle();
    }
}
