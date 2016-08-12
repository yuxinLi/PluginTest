package com.example.plugin_dex;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String outDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    private static final String outDex = outDir + "plugin-dex.dex";
    private static final String outApk = outDir + "plugin-apk.apk";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }



    public void onLoadDexCLick(View v) {
        if (copyFileFromAssetsToSd(this, "plugin-dex.dex", outDex)) {
            Log.e(TAG, "成功复制dex到SD卡");

            final File outDexFile = new File(outDex);
            File dexDir = getDir("dex", MODE_PRIVATE);
            DexClassLoader dexClassLoader = new DexClassLoader(outDexFile.getAbsolutePath() , dexDir.getAbsolutePath() , null, getClassLoader());

            Class<?> loadClass = null;
            try {
                loadClass = dexClassLoader.loadClass("kaede.me.pluginsoucre.Foo");
                Method foo = loadClass.getDeclaredMethod("foo");
                foo.setAccessible(true);
                String s  = (String) foo.invoke(loadClass.newInstance());

                Toast.makeText(this , s , Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void onLoadApkCLick(View v){
        if (copyFileFromAssetsToSd(this ,"plugin-apk.apk" , outApk )){
            Log.e(TAG, "成功复制apk到SD卡");

            File outApkFile = new File(outApk);
            File apkDir = getDir("apk" , MODE_PRIVATE);
            DexClassLoader dexClassLoader = new DexClassLoader(outApkFile.getAbsolutePath() , apkDir.getAbsolutePath() , null, getClassLoader());

            try {
                Class<?> loadClass = dexClassLoader.loadClass("kaede.me.pluginsoucre.MainActivity");
                Object o = loadClass.newInstance();
                Method biu = loadClass.getDeclaredMethod("biu", Context.class, String.class);
                biu.setAccessible(true);

                biu.invoke(o , this , "xxx");


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public static boolean copyFileFromAssetsToSd(Context context, String fileName, String path) {
        boolean copyIsFinish = false;
        try {
            InputStream is = context.getAssets().open(fileName);
            File file = new File(path);
            if (file.exists()){
                file.delete();
            }
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();
            copyIsFinish = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return copyIsFinish;
    }

}
