package com.najam.bluetoothprinteremulator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by najam on 01-04-2017.
 */

public class LoadJson {
    private String restoredText;

    public void resetAsset(Context C){
        boolean deleted;
        SharedPreferences prefs = C.getSharedPreferences("AddressPref", MODE_PRIVATE);
        String filepath = prefs.getString("Address", null);

        //String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Json/newfile.json";
            File file = new File(filepath);
            if (file.exists()) {
                deleted = file.delete();
                prefs.edit().putString("Address", null);
            }
    }

    public String loadJSONFromAsset(Context C, String comm) throws IOException {
        SharedPreferences prefs = C.getSharedPreferences("AddressPref", MODE_PRIVATE);
        restoredText = prefs.getString("Address", null);
        if (comm.equals("write")) {
            restoredText = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Json/newfile.json";
        }

        System.gc();
        File yourFile = new File(restoredText);
        if (!yourFile.exists()) {
            yourFile = new File(restoredText);
        }
        FileInputStream stream = new FileInputStream(yourFile);
        String jString = null;
        try {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            jString = Charset.defaultCharset().decode(bb).toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stream.close();
        }
        /*try {
            InputStream is = C.getAssets().open("extract.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }*/
        return jString;
    }
}
