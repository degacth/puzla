package com.degacth.alexander.puzla2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import com.degacth.alexander.puzla2.drawer.SelectDrawer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class SelectActivity extends Activity {

    private static final int IMAGE_REQUEST = 1;
    private static final int PHOTO_REQUEST = 2;
    private static final String TILES_DIR = "tiles";
    private int xSize;
    private Bitmap bitmap;
    private SelectDrawer drawer;
    private Random rand = new Random();
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        final Display display = getWindowManager().getDefaultDisplay();
        final Point displaySize = new Point();
        display.getSize(displaySize);
        xSize = displaySize.x;

        findViewById(R.id.buttonImages).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, IMAGE_REQUEST);
            }
        });

        findViewById(R.id.buttonRotate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmap == null) return;
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                new Thread(new BitmapSaver(bitmap)).start();
            }
        });

        findViewById(R.id.buttonUpdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRandomImage();
            }
        });

        findViewById(R.id.buttonGetPhoto).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    try {
                        File photoFile = Utils.getFile();
                        mCurrentPhotoPath = photoFile.getAbsolutePath();
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(intent, PHOTO_REQUEST);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(SelectActivity.this, "Error create photo file", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        findViewById(R.id.buttonPlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmap == null) {
                    Toast.makeText(SelectActivity.this, "Choose image", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(SelectActivity.this, GameActivity.class);
                intent.putExtra("tilesCount", drawer.getTilesCount());
                intent.putExtra("size", displaySize.x);
                System.gc();
                startActivity(intent);
            }
        });

        drawer = new SelectDrawer((SurfaceView) findViewById(R.id.selectSurfaceView));
        drawer.setTilesCount(3);
        setTilesButton();
        setRandomImage();
    }

    private void setRandomImage() {
        try {
            int imagesCount = getResources().getAssets().list(TILES_DIR).length;
            if (imagesCount > 0) {
                int num = rand.nextInt(imagesCount) + 1;
                final String filename = num + ".jpg";
                InputStream is = getResources().getAssets().open(TILES_DIR + "/" + filename);
                new Thread(new BitmapSaver(Utils.getBitmapByStream(xSize, new Utils.StreamResolver() {

                    @Override
                    public void preResolve(BitmapFactory.Options options) throws IOException {
                        InputStream is = getResources().getAssets().open(TILES_DIR + "/" + filename);
                        BitmapFactory.decodeStream(is, null, options);
                        is.close();

                    }

                    @Override
                    public Bitmap postResolve(BitmapFactory.Options options) throws IOException {
                        InputStream is = getResources().getAssets().open(TILES_DIR + "/" + filename);
                        Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
                        is.close();
                        return bitmap;
                    }
                }))).start();
                is.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setTilesButton() {
        GridLayout tilesLayout = (GridLayout) findViewById(R.id.tilesButtonLayout);

        float scale = getResources().getDisplayMetrics().density;
        int buttonSize = (int) (70 * scale + 0.5f);

        for (int i = 2; i < 11; i++) {
            Button button = new Button(this);
            button.setWidth(buttonSize);
            button.setHeight(buttonSize);
            button.setText(String.valueOf(i));
            tilesLayout.addView(button);

            button.setOnClickListener(new TilesCountClickListener(drawer, i));
        }
    }

    class TilesCountClickListener implements View.OnClickListener {

        private int tilesCount;
        private SelectDrawer drawer;

        TilesCountClickListener(SelectDrawer _drawer, int _tilesCount) {
            tilesCount = _tilesCount;
            drawer = _drawer;
        }

        @Override
        public void onClick(View view) {
            drawer.setTilesCount(tilesCount);
            drawer.draw(bitmap);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK) {

            if (bitmap != null) bitmap.recycle();
            bitmap = Utils.getBitmapByStream(xSize, new Utils.StreamResolver() {

                @Override
                public void preResolve(BitmapFactory.Options options) throws IOException {
                    BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()), null, options);
                }

                @Override
                public Bitmap postResolve(BitmapFactory.Options options) throws IOException {
                    return BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()), null, options);
                }
            });
            new Thread(new BitmapSaver(bitmap)).start();
        }


        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK) {
            Utils.galleryAddPic(mCurrentPhotoPath, this);

            new Thread(new BitmapSaver(Utils.getBitmapByStream(xSize, new Utils.StreamResolver() {

                @Override
                public void preResolve(BitmapFactory.Options options) throws IOException {
                    BitmapFactory.decodeFile(mCurrentPhotoPath, options);
                }

                @Override
                public Bitmap postResolve(BitmapFactory.Options options) throws IOException {
                    return BitmapFactory.decodeFile(mCurrentPhotoPath, options);
                }
            }))).start();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        System.gc();
        super.onResume();
        drawer.draw(bitmap);
    }

    public void imageChanged() {
        System.gc();
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 3;
            bitmap = BitmapFactory.decodeStream(openFileInput(GameActivity.TILE_IMAGE_FILE));
        } catch (FileNotFoundException e) {}

        drawer.draw(bitmap);
    }

    class BitmapSaver implements Runnable {
        private Bitmap bitmap;

        public BitmapSaver(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        @Override
        public void run() {
            saveBitmap();
        }

        private void saveBitmap() {
            if (bitmap == null) return;

            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, xSize, xSize);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                FileOutputStream outputStream = openFileOutput(GameActivity.TILE_IMAGE_FILE, Context.MODE_PRIVATE);
                outputStream.write(stream.toByteArray());
                outputStream.close();

                SelectActivity.this.imageChanged();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(SelectActivity.this, "Error file save", Toast.LENGTH_LONG).show();
            }
        }
    }
}
