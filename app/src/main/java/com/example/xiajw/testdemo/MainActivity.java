package com.example.xiajw.testdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private DemoImageView2 imageView;
    private SeekBar seekBar;
    private Button button, btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.btn_mirror);
        button.setEnabled(false);
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
                File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File picFile = new File(file.getAbsolutePath(), "Lighthouse.jpg");
                BitmapFactory.decodeFile(picFile.getAbsolutePath(), options);
                int w = options.outWidth;
                int h = options.outHeight;
                options.inJustDecodeBounds = false;
                options.inSampleSize = Math.max(Math.min(w / metrics.widthPixels, h / metrics.heightPixels), 1);
                return BitmapFactory.decodeFile(picFile.getAbsolutePath(), options);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                imageView.setBitmap(bitmap);
                btnSave.setEnabled(true);
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        imageView.save();
                        imageView.setRotate(45);
                    }
                });
                button.setEnabled(true);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        imageView.mirror();
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
