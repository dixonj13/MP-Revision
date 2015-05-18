/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;


import java.io.*;
import java.util.*;
import javafx.util.*;

/**
 *
 * @author jmd-m
 */
public class ChapterParser {

    private static List<String> boxes = Arrays.asList("moov", "udta");

    public static Map<String, Duration> parse(File file) throws IOException, FileNotFoundException {
        try (FileInputStream fis = new FileInputStream(file)) {
            Map<String, Duration> chpl = new TreeMap<>();
            find(fis, chpl);
            fis.close();
            return chpl;
        }
    }

    public static Map<String, Duration> parse(String file) throws IOException, FileNotFoundException {
        try (FileInputStream fis = new FileInputStream(file)) {
            Map<String, Duration> chpl = new TreeMap<>();
            find(fis, chpl);
            fis.close();
            return chpl;
        }
    }

    private static Map<String, Duration> find(FileInputStream fis, Map m) throws IOException {
        while (fis.available() > 0) {
            byte[] header = new byte[8];
            fis.read(header);

            long size = Conversions.readUint32(header, 0);
            String type = new String(header, 4, 4, "ISO-8859-1");

            if (boxes.contains(type)) {
                find(fis, m);
            } else {
                if (type.equals("chpl")) {
                    if (fis.skip(8) != 8) {
                        throw new IOException("Invalid number of bytes skipped");
                    }

                    byte[] buffer = new byte[1];
                    fis.read(buffer);
                    long chCount = Conversions.readUint8(buffer, 0);

                    for (int i = 0; i < chCount; i++) {
                        buffer = new byte[8];
                        fis.read(buffer);
                        double time = (double) Conversions.readUint64(buffer, 0) / 10000;
                        buffer = new byte[1];
                        fis.read(buffer);
                        int length = (int) Conversions.readUint8(buffer, 0);
                        buffer = new byte[length];
                        fis.read(buffer);
                        String text = new String(buffer, 0, length, "ISO-8859-1");
                        m.put(text, new Duration(time));
                    }
                    return m;
                } else {
                    if (fis.skip(size - 8) != size - 8) {
                        throw new IOException("Invalid number of bytes skipped");
                    }
                }
            }
        }
        return m;
    }

}
