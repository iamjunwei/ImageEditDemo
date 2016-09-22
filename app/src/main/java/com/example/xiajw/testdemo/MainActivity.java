package com.example.xiajw.testdemo;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private DemoImageView2 imageView;
    private SeekBar seekBar;
    private Button button, btnSave, btnRevert;
    private Button btnRatio1_1, btnRatio4_3, btnRatio3_4, btnRatio9_16, btnRatio16_9, btnRatioOriginal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.btn_mirror);
        button.setEnabled(false);
        btnRevert = (Button) findViewById(R.id.btn_revert);
        btnRevert.setEnabled(false);
        btnRatio1_1 = (Button) findViewById(R.id.btn_ratio_1_1);
        btnRatio1_1.setEnabled(false);
        btnRatio3_4 = (Button) findViewById(R.id.btn_ratio_3_4);
        btnRatio3_4.setEnabled(false);
        btnRatio4_3 = (Button) findViewById(R.id.btn_ratio_4_3);
        btnRatio4_3.setEnabled(false);
        btnRatio9_16 = (Button) findViewById(R.id.btn_ratio_9_16);
        btnRatio9_16.setEnabled(false);
        btnRatio16_9 = (Button) findViewById(R.id.btn_ratio_16_9);
        btnRatio16_9.setEnabled(false);
        btnRatioOriginal = (Button) findViewById(R.id.btn_ratio_origin);
        btnRatioOriginal.setEnabled(false);
        btnSave = (Button) findViewById(R.id.btn_save);
        btnSave.setEnabled(false);
        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        seekBar.setProgress(50);
        seekBar.setMax(100);
        seekBar.setEnabled(false);
        imageView = (DemoImageView2) findViewById(R.id.demo_image);
//        imageView.setOnSaveListener(new DemoImageView.OnSaveListener() {
//            @Override
//            public void onSave() {
//                seekBar.setProgress(50);
//            }
//        });
        setBitmap();
    }

    private void setBitmap() {
        AsyncTask<Void, Void, Bitmap> loadImageTask = new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                AssetManager am = getResources().getAssets();
                try {
                    InputStream is = am.open("Lighthouse.jpg");
                    BitmapFactory.decodeStream(is, null, options);
                    int w = options.outWidth;
                    int h = options.outHeight;
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = Math.max(Math.min(w / metrics.widthPixels, h / metrics.heightPixels), 1);
                    return BitmapFactory.decodeStream(is, null, options);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(final Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if (bitmap == null) return;
                imageView.initOriginBit(bitmap);
                imageView.initBitmap(bitmap, ((float)bitmap.getWidth()) / bitmap.getHeight());
                btnRevert.setEnabled(true);
                btnRevert.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageView.initBitmap(bitmap, (float)(bitmap.getWidth()) / bitmap.getHeight());
                        seekBar.setProgress(50);
                    }
                });
                btnRatio1_1.setEnabled(true);
                btnRatio1_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageView.initBitmap(bitmap, 1f);
                        seekBar.setProgress(50);
                    }
                });
                btnRatio3_4.setEnabled(true);
                btnRatio3_4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageView.initBitmap(bitmap, 0.75f);
                        seekBar.setProgress(50);
                    }
                });
                btnRatio4_3.setEnabled(true);
                btnRatio4_3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageView.initBitmap(bitmap, 4f / 3);
                        seekBar.setProgress(50);
                    }
                });
                btnRatio16_9.setEnabled(true);
                btnRatio16_9.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageView.initBitmap(bitmap, 16f / 9);
                        seekBar.setProgress(50);
                    }
                });
                btnRatio9_16.setEnabled(true);
                btnRatio9_16.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageView.initBitmap(bitmap, 9f / 16);
                        seekBar.setProgress(50);
                    }
                });
                btnRatioOriginal.setEnabled(true);
                btnRatioOriginal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageView.initBitmap(bitmap, (float)(bitmap.getWidth()) / bitmap.getHeight());
                        seekBar.setProgress(50);
                    }
                });
                btnSave.setEnabled(true);
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageView.save();
                    }
                });
                button.setEnabled(true);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageView.mirror();
                    }
                });
                seekBar.setEnabled(true);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        imageView.setRotate((progress - 50) / 50f * 45);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            }
        };

        loadImageTask.execute();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageView.clearBitmap();
    }
}
