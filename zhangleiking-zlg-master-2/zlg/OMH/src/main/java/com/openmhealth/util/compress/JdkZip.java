package com.openmhealth.util.compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 */
public class JdkZip implements Zip {

    public  byte[] zip(byte[] bytes) {
        if(bytes != null && bytes.length > 0) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                //GZIPOutputStream用于压缩
                GZIPOutputStream gos = new GZIPOutputStream(bos);
                gos.write(bytes);
                gos.close();
                return bos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new byte[0];
    }

    public byte[] unzip(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try {
            GZIPInputStream gis = new GZIPInputStream(bis);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int read = gis.read(buffer);
            while(read > 0) {
                bos.write(buffer, 0, read);
                read = gis.read(buffer);
            }
            gis.close();
            bos.close();
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
