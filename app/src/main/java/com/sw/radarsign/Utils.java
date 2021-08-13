package com.sw.radarsign;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {

    public static boolean onoff = false;
    public static byte tx_tol = 5;
    public static byte max_spd = 99;
    public static byte min_spd  = 5;
    public static byte lmt_spd = 50;

    public static byte flsh_state = 0;
    public static int ShowTime = 20;

    public static int laguage = 1;  //0:英语;1;土耳其语


    public static  int rx_escLen = 0;
    public static byte[] do_enEsc(byte[] msg,int len)
    {
        int i = 0x00;
        int index = 0x00,t_index=0x00;
        byte[] txbuf = new byte[64];
        txbuf[t_index] = 0x02;
        t_index++;

        for(i=0;i<len;i++)
        {
            switch(msg[index])
            {
                case 0x02:
                    txbuf[t_index] = 0x1B;  //27
                    t_index++;
                    txbuf[t_index] = (byte) 0xE7;  //231
                    break;
                case 0x03:
                    txbuf[t_index] = 0x1B;  //27
                    t_index++;
                    txbuf[t_index] = (byte) 0xE8; //232
                    break;
                case 0x1B:
                    txbuf[t_index] = 0x1B; //27
                    t_index++;
                    txbuf[t_index] = 0x00;
                    break;
                default:
                    txbuf[t_index] = msg[index];
                    break;
            }
            index++;
            t_index++;
        }
        txbuf[t_index++] = 0x03;
        byte[] tx_buf = new byte[t_index];
        System.arraycopy(txbuf,0,tx_buf,0,t_index);
        return tx_buf;
    }

    public boolean do_crc_msg(byte[] msg,int   length){
        boolean ret =false;
        int calcCrcVal = 0x0000;
        if(length>=3) {
            CRC_16_1021 crc_16_1021 = new CRC_16_1021();
            calcCrcVal = crc_16_1021.do_crc(msg, length - 3);
            int tmprxval = (msg[length - 3] & 0x000000FF) << 8;
            tmprxval = (msg[length - 2] & 0x000000FF) | tmprxval;

            if (tmprxval == calcCrcVal) {
                ret = true;
            } else {
                ret = false;
            }
        }
        else {
            ret = false;
        }
        return ret;
    }


    public  byte[] do_DeEsc(byte[] buf,int length) {
        byte[] tmpbuf = new byte[64];
        int index = 0x00;
        int pro_index = 0x00;
        do {
            if (buf[index] == 0x1B) {
                index++;
                if (buf[index] == (byte) (0xE7)) {
                    tmpbuf[pro_index] = 0x02;
                } else if (buf[index] == (byte) (0xE8)) {
                    tmpbuf[pro_index] = 0x03;
                } else if (buf[index] == 0x00) {
                    tmpbuf[pro_index] = 0x1B;
                }
            } else {
                tmpbuf[pro_index] = buf[index];
            }
            if (buf[index] == 0x03) {
                rx_escLen = pro_index + 1;
                return tmpbuf;
            }
            index++;
            pro_index++;
        } while (length != 0);
        return tmpbuf;
    }





    public static Bitmap getImageFromAssetsFile(Context context,String fileName) {
        Bitmap image = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            AssetManager am = context.getResources().getAssets();
            try {
                InputStream is = am.open(fileName);
                image = BitmapFactory.decodeStream(is);
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return image;
    }

}
