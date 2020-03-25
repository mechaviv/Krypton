/*
 * This file is part of OrionAlpha, a MapleStory Emulator Project.
 * Copyright (C) 2018 Eric Smith <notericsoft@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Eric
 */
public class Utilities {

    /**
     * Returns the specified 32-bit signed integer value as an array of bytes.
     *
     * @param val The value to convert
     * @return An array of bytes with length of 4
     */
    public static byte[] getBytes(int val) {
        byte[] arr = new byte[Integer.BYTES];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (byte) ((val >> (8 * i)) & 0xFF);
        }
        return arr;
    }

    /**
     * Converts a standard byte-array into a readable hex string AoB.
     *
     * @param buf The buffer containing an array of bytes
     * @return A readable hex string
     */
    public static String toHexString(byte[] buf) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < buf.length; i++) {
            int b = buf[i] & 0xFF;
            if (b < 0x10)
                str.append('0');
            str.append(Integer.toHexString(b).toUpperCase());
            str.append(' ');
        }
        str.deleteCharAt(str.length() - 1);
        return str.toString();
    }

    /**
     * Converts an IP from a string to an integer.
     * e.g "127.0.0.1" -> 16777343
     *
     * @param addr The string representation of the IP
     *
     * @return The value of the IP as an integer
     */
    public static final int netIPToInt32(String addr) {
        if (addr.length() < "0.0.0.0".length()) {
            return 0;
        }
        Long netaddr = 0L;
        String[] ip = addr.split("\\.");

        for (int i = 0; i < 4; i++) {
            netaddr += Long.parseLong(ip[i]) << (i << 3);
        }
        return netaddr.intValue();
    }

    /**
     * Converts an IP from an integer representation into a string.
     * e.g 16777343 -> "127.0.0.1"
     *
     * @param netaddr The integer representation of the IP
     *
     * @return The string representation of the IP
     */
    public static final String netIPToString(long netaddr) {
        return String.format("%d.%d.%d.%d", (netaddr & 0xFF), ((netaddr >> 8) & 0xFF), ((netaddr >> 16) & 0xFF), ((netaddr >> 24) & 0xFF));
    }

    //Thanks odin
    /**
     * Joins an array of strings starting from string <code>start</code> with a space.
     *
     * @param arr The array of strings to join.
     * @param start Starting from which string.
     * @return The joined strings.
     */
    public static String joinStringFrom(String[] arr, int start){
        return joinStringFrom(arr, start, " ");
    }

    /**
     * Joins an array of strings starting from string <code>start</code> with <code>sep</code> as a seperator.
     *
     * @param arr The array of strings to join.
     * @param start Starting from which string.
     * @return The joined strings.
     */
    public static String joinStringFrom(String[] arr, int start, String sep){
        StringBuilder builder = new StringBuilder();
        for(int i = start; i < arr.length; i++){
            builder.append(arr[i]);
            if(i != arr.length - 1){
                builder.append(sep);
            }
        }
        return builder.toString();
    }

    public static FileTime getFileTimeFromStringDate(String date) {
        if (date != null && !date.isEmpty() && date.length() == 14) {
            int year = Integer.valueOf(date.substring(0, 4));
            int month = Integer.valueOf(date.substring(4, 6));
            int day = Integer.valueOf(date.substring(6, 8));
            int hour = Integer.valueOf(date.substring(8, 10));
            int minute = Integer.valueOf(date.substring(10, 12));
            int second = Integer.valueOf(date.substring(12, 14));
            SystemTime st = new SystemTime();
            st.setYear(year);
            st.setMonth(month);
            st.setDay(day);
            st.setHour(hour);
            st.setMinute(minute);
            st.setSecond(second);

            st.setMilliseconds(0);
            st.setDayOfWeek(0);

            FileTime ft = st.systemTimeToFileTime();
            return ft;
        }
        return FileTime.END;
    }

    /**
     * Checks if a String is a number ((negative) natural or decimal).
     * @param string The String to check
     * @return Whether or not the String is a number
     */
    public static boolean isNumber(String string) {
        return string != null && string.matches("-?\\d+(\\.\\d+)?");
    }

    public static void getRandomUniqueArray(List<Integer> shuffles, int start, int range, int count) {
        shuffles.clear();

        List<Integer> tempList = new ArrayList<>();
        if (range != 0) {
            for (int i = 0; i < range; i++) {
                tempList.add(i + start);
            }
        }
        for (int i = 0; i < count; i++) {
            int rand = Math.abs(Rand32.genRandom().intValue());

            int size = tempList.size();
            if (size != 0) {
                rand = rand % size;
            }
            shuffles.add(i, tempList.get(rand));
            tempList.remove(rand);
        }
        tempList.clear();
    }
}
