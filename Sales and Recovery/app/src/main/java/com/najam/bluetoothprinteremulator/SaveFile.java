package com.najam.bluetoothprinteremulator;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.Context.CONTEXT_IGNORE_SECURITY;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by najam on 08-04-2017.
 */

public class SaveFile {


    private static Context context;
    private String path;
    public SaveFile(Context c) {
        context = c;
    }

    public void save(String data)
    {
        System.gc();
        path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Json/newfile.json";
        File file = new File (path);
        FileOutputStream fos;
        try
        {
            fos = new FileOutputStream(file);
            fos.write(data.getBytes());
            Log.i("Write", "Write Complete");
            fos.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}
