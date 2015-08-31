package com.example.musicApp;

import android.util.Log;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Nick_dnepr on 17.06.2015.
 */
public class DownloadService {
    public static boolean downloadFile(String url, File outputFile) {

        try {
            URL urlToFile = new URL(url);

            URLConnection connection = urlToFile.openConnection();
//            int contentLength = connection.getContentLength();

            InputStream is = urlToFile.openStream();
            DataInputStream dis = new DataInputStream(is);

            byte[] buffer = new byte[4096];
            int length;
            long total = 0;
            FileOutputStream fos = new FileOutputStream(outputFile);
            while ((length = dis.read(buffer))>0) {
                fos.write(buffer, 0, length);
                total += length;

//                publishProgressInterface.publishProgressDownloading((int) (total * 100 / contentLength));

            }
            fos.flush();
            fos.close();

            dis.close();

        } catch (MalformedURLException mue) {
            Log.e("SYNC getUpdate", "malformed url error", mue);
            return false;
        } catch (IOException ioe) {
            Log.e("SYNC getUpdate", "io error", ioe);
            return false;
        } catch (SecurityException se) {
            Log.e("SYNC getUpdate", "security error", se);
            return false;
        }

        return true;
    }
}
