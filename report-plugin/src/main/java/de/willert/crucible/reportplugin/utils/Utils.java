package de.willert.crucible.reportplugin.utils;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by czoeller on 31.08.16.
 */
public class Utils {
    /**
     * Export a resource embedded into a Jar file to the local file path.
     *
     * @param sResource ie.: "/SmartLibrary.dll"
     * @return The path to the exported resource
     * @throws Exception
     */
    static public boolean exportResource(Class clazz, String sResource, File fDest) throws IOException {
        if (sResource == null || fDest == null) return false;
        InputStream sIn = null;
        OutputStream sOut = null;
        File sFile = null;
            fDest.getParentFile().mkdirs();
            sFile = new File(sResource);
        try {

            int nLen = 0;
            sIn = clazz.getClassLoader().getResourceAsStream(sResource);
            if (sIn == null) {
                throw new IOException("Error copying from jar" + "(" + sResource + " to " + fDest.getPath() + ")");
            }
            sOut = new FileOutputStream(fDest);
            byte[] bBuffer = new byte[1024];
            while ((nLen = sIn.read(bBuffer)) > 0)
                sOut.write(bBuffer, 0, nLen);
            sOut.flush();
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
        finally {
            try {
                if (sIn != null)
                    sIn.close();
                if (sOut != null)
                    sOut.close();
            }
            catch (IOException eError) {
                eError.printStackTrace();
            }
        }
        return fDest.exists();
    }
    public static void copyFromJar(Class<?> clazz, final String source, final Path target) throws URISyntaxException, IOException {

    }

    /**
     * Parses string into date.
     * @param string The string to parse
     * @return date
     */
    public static Date stringToDate(String string) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateFormatUtils.ISO_DATE_FORMAT.getPattern());
        Date date = null;
        try {
            date = simpleDateFormat.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
