package com.pagatodoholdings.posandroid.utils;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class UtilsPrinter {
    // UNICODE 0x23 = #
    protected static final byte[] UNICODE_TEXT = new byte[] {0x23, 0x23, 0x23,
            0x23, 0x23, 0x23,0x23, 0x23, 0x23,0x23, 0x23, 0x23,0x23, 0x23, 0x23,
            0x23, 0x23, 0x23,0x23, 0x23, 0x23,0x23, 0x23, 0x23,0x23, 0x23, 0x23,
            0x23, 0x23, 0x23};

    private static final String HEX_STR = "0123456789ABCDEF";
    private static final String[] BINARY_ARRAY = { "0000", "0001", "0010", "0011",
            "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011",
            "1100", "1101", "1110", "1111" };

    private static final String CERO = "0";
    private static final String UNO = "1";

    public  byte[] decodeBitmap(final Bitmap bmp){
        final int bmpWidth = bmp.getWidth();
        final int bmpHeight = bmp.getHeight();



        List<String> bmpHexList;

        List<String> list;//binaryString list



        final int zeroCount = bmpWidth % 8;

        final StringBuilder zeroStr = new StringBuilder();
        if (zeroCount > 0) {

            for (int i = 0; i < (8 - zeroCount); i++) {
                zeroStr.append(CERO);
            }
        }



        list = getColorList(bmpHeight, bmpWidth,bmp,zeroCount,zeroStr);
        bmpHexList = binaryListToHexStringList(list );

        final StringBuilder widthHexString = new StringBuilder();
        widthHexString.append(Integer
                .toHexString(bmpWidth % 8 == 0 ? bmpWidth / 8
                        : bmpWidth / 8 + 1));
        if (widthHexString.length() > 2) {
            Log.e("decodeBitmap error", " width is too large");
            return new byte[0];
        } else if (widthHexString.length() == 1) {
            widthHexString.append(CERO);
            widthHexString.append(widthHexString);
        }
        final String doublecero="00";
        widthHexString.append(doublecero);

        final StringBuilder heightHexString = new StringBuilder();
        heightHexString.append(Integer.toHexString(bmpHeight));
        if (heightHexString.length() > 2) {
            Log.e("decodeBitmap error", " height is too large");
            return new byte[0];
        } else if (heightHexString.length() == 1) {
            heightHexString.append(CERO);
            heightHexString.append(heightHexString);
        }
        heightHexString.append(doublecero);

        final List<String> commandList = new ArrayList<>();
        final String commandHexString = "1D763000";
        commandList.add(commandHexString+widthHexString+heightHexString);
        commandList.addAll(bmpHexList);

        return hexList2Byte(commandList);
    }

    public List<String> getColorList(final int bmpHeight,final int bmpWidth, final Bitmap bmp, final int zeroCount, final StringBuilder zeroStr){

        final List<String> list = new ArrayList<>();
        for (int i = 0; i < bmpHeight; i++) {
            final   StringBuilder sbuffer = getBuffer();
            for (int j = 0; j < bmpWidth; j++) {
                final int color = bmp.getPixel(j, i);

                final int rcolor = (color >> 16) & 0xff;
                final int gcolor = (color >> 8) & 0xff;
                final int bcolor = color & 0xff;

                // if color close to white，bit='0', else bit='1'
                if (rcolor > 160 && gcolor > 160 && bcolor > 160) {
                    sbuffer.append(CERO);
                } else {
                    sbuffer.append(UNO);
                }
            }
            if (zeroCount > 0) {
                sbuffer.append(zeroStr);
            }
            list.add(sbuffer.toString());
        }

        return list;
    }

    public  List<String> binaryListToHexStringList(final List<String> list) {
        final List<String> hexList = new ArrayList<>();
        for (final String binaryStr : list) {
            final StringBuilder sbuffer = getBuffer();
            for (int i = 0; i < binaryStr.length(); i += 8) {
                final String str = binaryStr.substring(i, i + 8);

                final String hexString = myBinaryStrToHexString(str);
                sbuffer.append(hexString);
            }
            hexList.add(sbuffer.toString());
        }
        return hexList;

    }

    public  StringBuilder getBuffer(){
        return new StringBuilder();
    }

    public  String myBinaryStrToHexString(final String binaryStr) {
        final StringBuilder hex = new StringBuilder();
        final String f4byte = binaryStr.substring(0, 4);
        final String b4byte = binaryStr.substring(4, 8);
        for ( int i = 0; i < BINARY_ARRAY.length; i++) {
            if (f4byte.equals(BINARY_ARRAY[i])) {
                hex.append(HEX_STR.substring(i, i + 1));
            }
        }
        for ( int i = 0; i < BINARY_ARRAY.length; i++) {
            if (b4byte.equals(BINARY_ARRAY[i])) {
                hex.append(HEX_STR.substring(i, i + 1));
            }
        }

        return hex.toString();
    }

    public  byte[] hexList2Byte(final List<String> list) {
        final List<byte[]> commandList = new ArrayList<>();

        for (final String hexStr : list) {
            commandList.add(hexStringToBytes(hexStr));
        }
        return  sysCopy(commandList);

    }

    public  byte[] hexStringToBytes(final String hexString) {
        if (hexString == null || hexString.equals("")) {
            return new byte[0];
        }
        final String hexupp =  hexString.toUpperCase(Locale.ENGLISH);
        final int length = hexupp.length() / 2;
        final char[] hexChars = hexupp.toCharArray();
        byte[] dbytes = new byte[length];
        for (int i = 0; i < length; i++) {
            final int pos = i * 2;
            dbytes[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]) & 0xff);
        }
        return dbytes;
    }

    public  byte[] sysCopy(final List<byte[]> srcArrays) {
        int len = 0;
        for (final byte[] srcArray : srcArrays) {
            len += srcArray.length;
        }
        final byte[] destArray = new byte[len];
        int destLen = 0;
        for (final byte[] srcArray : srcArrays) {
            System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
            destLen += srcArray.length;
        }
        return destArray;
    }

    private  byte charToByte(final char cbyte) {
        return (byte) HEX_STR.indexOf(cbyte);
    }
}
