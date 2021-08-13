package com.sw.radarsign;

import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Locale;

public class TcpClient {

    public static Socket socket = null;
    String ip = null;
    int port = 5168;
    public static OutputStream outputStream = null;        //输出流
    public static InputStream inputStream=null;            //接收流
    public static boolean txFlg = false;
    public static boolean sendOk = false;
    public static boolean conOK = false;
    public static boolean interupRx = false;

    //设置服务器IP port
    public void TcpClientInit(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    public void CloseAll(){
        try {
            txFlg = false;
            sendOk = false;
            conOK = false;
            interupRx = true;
            if(inputStream != null) {
                inputStream.close();
            }
            if(outputStream != null) {
                outputStream.close();
            }
            if(socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public class RxTcpClient extends Thread {
        public void run(){
            try {
                socket = new Socket(ip, port);        //访问指定的ip地址:8080
                conOK = true;
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                conOK = false;
                e.printStackTrace();
            }
            while (conOK) {
                readMsg();
            }
        }
    }


    public class TxTcpClient extends Thread {
        public void run(){
            while (true){
                if(txFlg) {
                    txFlg = false;
                    //MainActivity.getMainActivity().send_tv.setVisibility(View.VISIBLE);
                    byte [] vldmsg = {0x32,0x35,0x30,0x30,0x30,0x30,0x30,0x30,0x35,0x36,0x30,0x30,0x39,0x39,0x30,0x7A,(byte)0x93,0x30,0x30};
                    if(Utils.onoff){
                        vldmsg[4] = 0x31;
                    }
                    byte[] tmpval = new byte[3];
                    String tmpstr = String.format(Locale.getDefault(),"%03d",Utils.min_spd);
                    tmpval = tmpstr.getBytes();
                    System.arraycopy(tmpval,0,vldmsg,5,3);

                    tmpstr = String.format(Locale.getDefault(),"%03d",Utils.lmt_spd);
                    tmpval = tmpstr.getBytes();
                    System.arraycopy(tmpval,0,vldmsg,8,3);

                    tmpstr = String.format(Locale.getDefault(),"%03d",Utils.max_spd);
                    tmpval = tmpstr.getBytes();
                    System.arraycopy(tmpval,0,vldmsg,11,3);
                    vldmsg[14] = (byte) (Utils.flsh_state+0x30);
                    vldmsg[15] = (byte) (Utils.tx_tol+0x30);

                    int tmplen = 16;
                    CRC_16_1021 crc_16_1021 = new CRC_16_1021();
                    int calcCrcVal = crc_16_1021.do_crc(vldmsg,16);
                    vldmsg[tmplen++] = (byte) ((calcCrcVal>>8)&0xFF);
                    vldmsg[tmplen++] = (byte) (calcCrcVal&0xFF);
                    byte[] txmsg = Utils.do_enEsc(vldmsg,tmplen);

                    sendMsg(outputStream, txmsg);
                    //sendMsg(outputStream, "1234132594169");
                    sendOk = true;
                    Log.v("发送次数", "发送成功：");
                    txFlg = false;

                    //Toast.makeText(MainActivity.getMainActivity(),"Send Successfully!" ,Toast.LENGTH_SHORT).show();
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }


    /**
     * 发送信息的方法
     * @param msg
     */
    public void sendMsg(OutputStream outputStream, String msg){
        try{
            //msg+="#";//以#来区分是一条消息
            outputStream.write(msg.getBytes());
            outputStream.flush();
            //Log.v("AndroidChat", "发送成功："+msg);
        }catch(Exception e){
            //Log.v("AndroidChat", "发送失败："+msg+"error:"+e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 发送信息的方法
     * @param msg
     */
    public void sendMsg(OutputStream outputStream, byte[] msg){
        try{
            //msg+="#";//以#来区分是一条消息
            outputStream.write(msg);
            outputStream.flush();
            //Log.v("AndroidChat", "发送成功："+msg);
        }catch(Exception e){
            //Log.v("AndroidChat", "发送失败："+msg+"error:"+e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * 接收信息的方法
     * @return
     */
    private String readMsg(){
        try{
            byte[] buf = new byte[1024];
            int t=inputStream.read(buf);
            /*
            while(t!='#'){
                Log.v("AndroidChat", "接收到一个字节："+t);
                stb.append((char)t);
                t=inputStream.read();
            }
            */

            String inMsg= new String(buf);
            //Log.v("AndroidChat", "接收到一条消息："+inMsg);
            return inMsg;
        }catch(Exception e){
            //Log.v("AndroidChat", "接收消息出错："+e.getMessage());
            e.printStackTrace();
        }
        return "error";
    }

}
