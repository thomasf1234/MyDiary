package com.abstractx1.mydiary;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by tfisher on 11/12/2016.
 */

public class FileUtils {
    public static byte[] readFileToByteArray(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int n;
        while (-1 != (n = fis.read(buffer)))
            baos.write(buffer, 0, n);

        return baos.toByteArray();
    }
}
