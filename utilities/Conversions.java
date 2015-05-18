/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;


import javafx.util.Duration;

/**
 *
 * @author jmd-m
 */
public class Conversions {
    
    public static String durationToDynamicHrMnSc(Duration d) {
        long left = (long) d.toMillis();
        long hour = left / 3600000;
        left %= 3600000;
        long min = left / 60000;
        left %= 60000;
        long sec = left / 1000;
        String hourStr = (hour>0)?String.format("%02d:", hour):"";
        return String.format("%s%02d:%02d", hourStr, min, sec);
    }
    
    public static String durationToMnSc(Duration d) {
        long left = (long) d.toMillis();
        long hour = left / 3600000;
        left %= 3600000;
        long min = left / 60000;
        left %= 60000;
        long sec = left / 1000;
        return String.format("%02d:%02d", min, sec);
    }

    public static String durationToHrMnSc(Duration d) {
        long left = (long) d.toMillis();
        long hour = left / 3600000;
        left %= 3600000;
        long min = left / 60000;
        left %= 60000;
        long sec = left / 1000;
        return String.format("%02d:%02d:%02d", hour, min, sec);
    }

    public static String durationToHrMnScMi(Duration d) {
        long left = (long) d.toMillis();
        long hour = left / 3600000;
        left %= 3600000;
        long min = left / 60000;
        left %= 60000;
        long sec = left / 1000;
        left %= 1000;
        return String.format("%02d:%02d:%02d:%03d", hour, min, sec, left);
    }

    @Deprecated
    public static String durationFormatted(Duration d) {
        long left = (long) d.toMillis();
        long hour = left / 3600000;
        left %= 3600000;
        long min = left / 60000;
        left %= 60000;
        long sec = left / 1000;
        left %= 1000;
        return String.format("%02d:%02d:%02d:%03d", hour, min, sec, left);
    }

    public static long readUint64(byte[] b, int p) {
        long result = 0;
        result |= readUint32(b, p) << 32;
        result |= readUint32(b, (p + 4));
        return result;
    }

    public static long readUint32(byte[] b, int p) {
        long result = 0;
        result |= readUint8(b, p) << 24;
        result |= readUint8(b, (p + 1)) << 16;
        result |= readUint8(b, (p + 2)) << 8;
        result |= readUint8(b, (p + 3));
        return result;
    }

    public static long readUint8(byte[] b, int p) {
        long result = 0;
        result |= ((b[p]) & 0xFF);
        return result;
    }
}
