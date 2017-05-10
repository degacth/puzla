package com.degacth.alexander.puzla2.drawer;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by alexander on 20/04/16.
 */
public class Firework {
    Paint paint;
    Random random;
    ArrayList<Dot> dots = new ArrayList<>();

    public Firework() {
        paint = new Paint();
        random = new Random();
    }

    class Dot {
        int cx, cy;
        int radius;

        Dot(int cx, int cy, int radius) {
            this.cx = cx;
            this.cy = cy;
            this.radius = radius;
        }
    }

    public void draw(Canvas canvas) {
        int maxWidth, maxHeight, paddingX, paddingY;
        maxWidth = canvas.getWidth();
        maxHeight = canvas.getHeight();
        paddingX = maxWidth / 40;
        paddingY = (maxHeight - maxWidth) / 2 + paddingX;

        for (int i = 0; i < 50; i++)
            dots.add(new Dot(
                    random.nextInt(maxWidth),
                    getRandomExclude(new int[]{0, paddingY}, new int[]{maxHeight - paddingY, maxHeight}),
                    random.nextInt(maxWidth / 15)+paddingX));

        for (Dot dot : dots) {
            paint.setARGB(random.nextInt(255), random.nextInt(255), random.nextInt(255), random.nextInt(255));
            canvas.drawCircle(dot.cx, dot.cy, dot.radius, paint);
        }
    }

    public int getRandomExclude(int[]... pairs) {
        try {
            int[] pair = pairs[random.nextInt(pairs.length)];
            return random.nextInt(pair[1] - pair[0]) + pair[0];
        } catch (IndexOutOfBoundsException e) {
            return 0;
        }
    }
}
