package com.cy.wu.library.fileinspector;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created by wcy on 17/2/23.
 */

public class FileUtil {

    private static final String TAG = FileUtil.class.getSimpleName();

    static boolean copyFile(File toFile, File fromFile) {
        if(!fromFile.exists()){
            return false;
        }
        if (toFile.exists()) {//
            toFile.delete();
            createFile(toFile, true);// create file
        }
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = new FileInputStream(fromFile);
            fos = new FileOutputStream(toFile);
            byte[] buffer = new byte[1024];
            while (is.read(buffer) != -1) {
                fos.write(buffer);
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }finally {
            closeSilence(is);
            closeSilence(fos);
        }
    }

    private static void closeSilence(Closeable is) {
        if(is!=null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static void createFile(File file, boolean isFile) {
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                createFile(file.getParentFile(), false);
            } else {
                if (isFile) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    file.mkdir();
                }
            }
        }
    }

    static String getFileMD5(String fileName) {
        File file = new File(fileName);
        if (!file.isFile()|| !file.exists()) {
            return null;
        }
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return getFileMD5(in);
    }

    static String getFileMD5(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }
        MessageDigest digest = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            while ((len = inputStream.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if(children !=null){
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return dir.delete();
    }

    // If targetLocation does not exist, it will be created.
    static void copyDirectory(File sourceLocation , File targetLocation) throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists() && !targetLocation.mkdirs()) {
                throw new IOException("Cannot create dir " + targetLocation.getAbsolutePath());
            }

            String[] children = sourceLocation.list();
            for (int i=0; i<children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {
            // make sure the directory we plan to store the recording in exists
            File directory = targetLocation.getParentFile();
            if (directory != null && !directory.exists() && !directory.mkdirs()) {
                throw new IOException("Cannot create dir " + directory.getAbsolutePath());
            }

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }
}
