package com.example.plugintest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import me.kaede.androidjnisample.NativeBlurProcess;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File dir = getDir("jniLibs" , MODE_PRIVATE);
        File destFile = new File(dir.getAbsolutePath() + File.separator + "libstackblur.so");

        if (copyFileFromAssets(this , "libstackblur.so" , destFile.getAbsolutePath())){
            System.load(destFile.getAbsolutePath());
            NativeBlurProcess.isLoadLibraryOk.set(true);
        }else {
            Toast.makeText(this , "xx" , Toast.LENGTH_SHORT).show();
        }

    }



    public void onDoBlur(View view){
        ImageView imageView = (ImageView) findViewById(R.id.iv_app);
        Bitmap b = BitmapFactory.decodeResource(getResources() , android.R.drawable.sym_def_app_icon);
        Bitmap blur = NativeBlurProcess.blur(b , 20 , false);
        imageView.setImageBitmap(blur);
    }

    /**
     * 把 assets 文件夹下的 so 考到 data/data/packagename/jniLibs 目录下
     * @param context
     * @param fileName
     * @param path
     * @return
     */
    private boolean copyFileFromAssets(Context context , String fileName , String path){
        boolean copyIsFinish = false;

        try {
            InputStream is = context.getAssets().open(fileName);

            File destFile = new File(path);
            if (destFile.exists()){
                destFile.delete();
            }
            destFile.createNewFile();
            OutputStream os = new FileOutputStream(destFile);

            byte[] b = new byte[1024];
            int i = 0;
            while ( (i = is.read(b)) > 0){
                os.write(b , 0 , i);
            }
            os.close();
            is.close();

            copyIsFinish = true;

        } catch (Exception e) {
            e.printStackTrace();
        }


        return copyIsFinish;

    }
}
