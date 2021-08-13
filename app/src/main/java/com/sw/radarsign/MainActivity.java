package com.sw.radarsign;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private String TAG = "TcpClient";
    private String[] TolArray = {"0%","3%","5%","7%","10%","12%","15%"};
    private CheckBox checkBox ;
    private EditText minSpd,lmtSpd,maxSpd,ip_tv;
    TcpClient tcpClient;

    RadioGroup overSpd_group;
    RadioButton stdy_radio,fast_radio,slow_radio;
    Spinner spinner,sp_lng;
    String serverIP = "192.168.1.11";
    Button btn1,btn2;
    TextView send_tv ;
    public static int dly_time = 0;
    public static boolean conBnt = false;
    public static String warnTxt;
    public static int color = 0x00FF00;
    public static boolean send_bnt = false;
    public static TextView dev_ip,dev_conf,dev_min,dev_lmt,dev_max,dev_ovrb,dev_tol,dev_lng;
    ImageView imageView ;


    private static MainActivity mainActivity;
    public MainActivity() {
        mainActivity = this;
    }

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        overSpd_group = (RadioGroup)findViewById(R.id.over_id);   //
        stdy_radio = (RadioButton) findViewById(R.id.stdy_radio);
        fast_radio = (RadioButton) findViewById(R.id.fast_radio);
        slow_radio = (RadioButton) findViewById(R.id.slow_radio);
        spinner = findViewById(R.id.tol_spinner);
        checkBox = findViewById(R.id.checkBox);
        minSpd = findViewById(R.id.minspd_tv);
        lmtSpd = findViewById(R.id.lmtspd_tv);
        maxSpd = findViewById(R.id.maxspd_tv);
        ip_tv = findViewById(R.id.ip_tv);

        send_tv = findViewById(R.id.send_tv);
        btn1 = (Button) this.findViewById(R.id.button0);
        btn2 = (Button) this.findViewById(R.id.button);


        sp_lng = findViewById(R.id.lng_sp);
        dev_ip = findViewById(R.id.dev_ip);
        dev_conf = findViewById(R.id.dev_conf);
        dev_min = findViewById(R.id.dev_min);
        dev_lmt = findViewById(R.id.dev_lmt);
        dev_max = findViewById(R.id.dev_max);
        dev_ovrb = findViewById(R.id.dev_ovrb);
        dev_tol = findViewById(R.id.dev_tol);
        dev_lng = findViewById(R.id.dev_lng);

        final String[][] LngArray = {{"English", "Dil", "中文"}};
        sp_lng.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LngArray[0] = getResources().getStringArray(R.array.language);
                String Text = LngArray[0][position];
                Utils.laguage = (byte) id;
                switch (Utils.laguage){
                    case 0:
                        dev_ip.setText("The Device IP:");
                        dev_conf.setText("Display Configuration");
                        dev_min.setText("Minimum Display Speed:");
                        dev_lmt.setText("Limit Speed:");
                        dev_max.setText("Maximum Display Speed:");
                        dev_ovrb.setText("Over Speed Limit Display Behavior:");
                        dev_tol.setText("Tolerance:");
                        dev_lng.setText("Language:");
                        checkBox.setText("Display Enabled");
                        btn1.setText("Connect");
                        stdy_radio.setText("On Steady");
                        fast_radio.setText("Flash Fast");
                        slow_radio.setText("Flash Slow");
                        btn2.setText("Set Config");
                        break;
                    case 1:
                        dev_ip.setText("Cihaz IP'si:");
                        dev_conf.setText("Ekran Yapılandırması");
                        dev_min.setText("Minimum Görüntüleme Hızı:");
                        dev_lmt.setText("Sınır Hız:");
                        dev_max.setText("Maksimum Görüntüleme Hızı:");
                        dev_ovrb.setText("Aşırı Hız Sınırı Görüntüleme Davranışı:");
                        dev_tol.setText("Hata payı:");
                        dev_lng.setText("Dil:");
                        checkBox.setText("Hata payı");
                        btn1.setText("Bağlan");
                        stdy_radio.setText("Sabit");
                        fast_radio.setText("Hızlı Flaş");
                        slow_radio.setText("Flash Yavaş");
                        btn2.setText("Yapılandırmayı Ayarla");
                        break;
                    case 2:
                        dev_ip.setText("设备IP:");
                        dev_conf.setText("显示配置");
                        dev_min.setText("最小显示速度:");
                        dev_lmt.setText("限速值:");
                        dev_max.setText("最大显示速度:");
                        dev_ovrb.setText("超速显示行为:");
                        dev_tol.setText("误差:");
                        dev_lng.setText("语言:");
                        checkBox.setText("开屏");
                        btn1.setText("连接");
                        stdy_radio.setText("长亮");
                        fast_radio.setText("快闪");
                        slow_radio.setText("慢闪");
                        btn2.setText("发送配置");
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        imageView = findViewById(R.id.imageView);
        Bitmap bitMap =  Utils.getImageFromAssetsFile(MainActivity.this,"logo.png");
        imageView.setImageBitmap(bitMap);

        if(ip_tv.getText().toString().length() != 0){
            serverIP = ip_tv.getText().toString();
        }
        minSpd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(minSpd.getText().toString().length() != 0) {
                    String text = minSpd.getText().toString();
                    Utils.min_spd = (byte) Integer.parseInt(text);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        lmtSpd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //String text = lmtSpd.getText().toString();
                //Utils.lmt_spd = (byte) Integer.parseInt(text);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(lmtSpd.getText().toString().length() != 0) {
                    String text = lmtSpd.getText().toString();
                    Utils.lmt_spd = (byte) Integer.parseInt(text);
                }
            }
        });

        maxSpd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(maxSpd.getText().toString().length() != 0) {
                    String text = maxSpd.getText().toString();
                    Utils.max_spd = (byte) Integer.parseInt(text);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ip_tv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(ip_tv.getText().toString().length() != 0) {
                    serverIP = ip_tv.getText().toString();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            }
        });
        overSpd_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == stdy_radio.getId()){
                    Utils.flsh_state = 0;
                }
                else if(checkedId == fast_radio.getId()){
                    Utils.flsh_state = 1;
                }
                else if(checkedId == slow_radio.getId()){
                    Utils.flsh_state = 2;
                }
            }
        });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TolArray = getResources().getStringArray(R.array.tolerance);
                String Text = TolArray[position];
                Utils.tx_tol = (byte) id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tcpClient != null){
                    tcpClient.CloseAll();
                }
                else {
                    tcpClient = new TcpClient();
                }
                int serverPort = 5168;
                tcpClient.TcpClientInit(serverIP, serverPort);
                TcpClient.RxTcpClient rxTcpClient = tcpClient.new RxTcpClient();
                rxTcpClient.start();

                TcpClient.TxTcpClient txTcpClient = tcpClient.new TxTcpClient();
                txTcpClient.start();
                conBnt = true;
                dly_time = 0;
            }
        });





        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Auto-generated method stub

                if(checkBox.isChecked()){
                    Utils.onoff = true;
                }
                else {
                    Utils.onoff = false;
                }
                dly_time = 0;
                send_bnt =  true;
                TcpClient.txFlg = true;
                //send_tv.setVisibility(View.VISIBLE);
            }
        });
        new Thread(new ShowThread()).start();







    }

    private void showSelectedPrice(String text){
        Toast.makeText(MainActivity.this,text ,Toast.LENGTH_SHORT).show();
    }


    @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    send_tv.setText(warnTxt);
                    send_tv.setTextColor(color);
                    send_tv.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    send_tv.setVisibility(View.INVISIBLE);
                    break;
                default:
                    break;
            }
        }
    };

    class ShowThread implements Runnable{

        @Override
        public void run() {
            while (true){
                Message msg = new Message();
                SystemClock.sleep(100);
                if(conBnt){
                    if(dly_time >= Utils.ShowTime){
                        dly_time = 0;
                        conBnt = false;
                        //TcpClient.sendOk = false;
                    }
                    else {
                        dly_time += 1;
                    }

                    if(TcpClient.conOK){
                        color = Color.GREEN;
                        if(Utils.laguage == 0) {
                            warnTxt = "Connected Successfully!";
                        } else if(Utils.laguage == 1) {
                            warnTxt = "Başarıyla Bağlandı!";//"Connect Successfully!";
                        }
                        else if(Utils.laguage == 2){
                            warnTxt = "连接成功!";
                        }
                    }
                    else {
                        color = Color.RED;
                        if(Utils.laguage == 0) {
                            warnTxt = "Please check if the IP is correct!";
                        }else if(Utils.laguage == 1) {
                            warnTxt = "Lütfen IP'nin doğru olup olmadığını kontrol edin!";//"Please check if the IP is correct!";//"Connect Faild!";
                        }
                        else if(Utils.laguage == 2){
                            warnTxt = "请检查IP是否正确!";
                        }
                    }
                    msg.what = 0;

                }
                else if(send_bnt){
                    if(TcpClient.sendOk) {
                        if (dly_time >= Utils.ShowTime) {
                            dly_time = 0;
                            conBnt = false;
                            send_bnt = false;
                            TcpClient.sendOk = false;
                        } else {
                            dly_time += 1;
                        }

                        if (TcpClient.conOK) {
                            color = Color.GREEN;
                            if(Utils.laguage == 0) {
                                warnTxt = "Sent Successfully!";
                            }else if(Utils.laguage == 1){
                                warnTxt = "Başarıyla gönderildi!";//"Send Successfully!";
                            }
                            else if(Utils.laguage == 2){
                                warnTxt = "发送成功!";
                            }
                        } else {
                            color = Color.RED;
                            if(Utils.laguage == 0) {
                                warnTxt = "Sent Faild!";
                            }else if(Utils.laguage == 1){
                                warnTxt = "Gönderilemedi!";
                            }
                            else if(Utils.laguage == 2){
                                warnTxt = "发送失败!";
                            }
                        }
                        msg.what = 0;
                    }
                    else {
                        if (dly_time >= Utils.ShowTime) {
                            dly_time = 0;
                            conBnt = false;
                            send_bnt = false;
                        } else {
                            dly_time += 1;
                        }
                        color = Color.RED;
                        if(Utils.laguage == 0) {
                            warnTxt = "Please Connect First!";
                        }
                        else if(Utils.laguage ==1){
                            warnTxt = "Lütfen Önce Bağlanın!";
                        }
                        else if(Utils.laguage == 2){
                            warnTxt = "请先连接设备!";
                        }
                        msg.what = 0;
                    }
                }
                    /*
                else if(TcpClient.sendOk){
                    if(dly_time >= 10){
                        dly_time = 0;
                        conBnt = false;
                        TcpClient.sendOk = false;
                    }
                    else {
                        dly_time += 1;
                    }

                    if(TcpClient.conOK){
                        color = Color.GREEN;
                        warnTxt = "Send Successfully!";
                    }
                    else {
                        color = Color.RED;
                        warnTxt = "Send Faild!";
                    }
                    msg.what = 0;
                }
                */
                /*
                else if(TcpClient.conOK){
                    if(TcpClient.sendOk) {
                        warnTxt = "Connect Successfully!";
                        color = Color.GREEN;
                        if(dly_time >= 10){
                            dly_time = 0;
                            TcpClient.sendOk = false;
                        }
                        else {
                            dly_time += 1;
                        }
                        SystemClock.sleep(1);
                        msg.what = 0;
                    }
                    else {
                        msg.what = 1;
                    }
                }
                */
                else {
                    msg.what = 1;
                }
                handler.sendMessage(msg);
            }
        }
    }
}



