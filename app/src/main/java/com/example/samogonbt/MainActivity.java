package com.example.samogonbt;

import android.annotation.SuppressLint;
//import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.UUID;

import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {
    /*#########################################ОТЛАДКА###################################################*/
    final static String TAG = "LifeCycle";

    /*###############################ИНТЕРФЕЙС - ПЕРЕМЕННЫЕ ФУНКЦИИ И МЕТОДЫ#############################*/
    /*ПЕРЕМЕННЫЕ*/
    //Иконки
    public ImageView ImVwAlarm;
    //Кнопки
    public Button ButCt, ButCls;
    public Button ButKup, ButKdn,ButUup, ButUdn, ButSend;
    public boolean ButConnectBool=false;

    public int ValKdim, ValUst;

    //CompoundButton
    CompoundButton TogHeat, TogGon, TogPump;
    //Строковые для отображения
/*
    String StrTimeMP1="Время ",
            StrTimeMP2=":",
            StrDMP1="Дата ",
            StrDMP2=".",
            StrTMP1="Температура снаружи: ",
            StrTMP2="*С",
            StrPMP1="Давление: ",
            StrPMP2=" мм.рт.ст.",
            StrAMP1="Высота: ",
            StrAMP2=" м.",
            StrWMP1="Уровень воды: ",
            StrWMP2="%";
*/
    TextView TxtAlarm;

    TextView TxtUstavka, TxtKdim, TxtHeatVal, TxtColdTemp,TxtDefTemp,TxtResTemp,TxtTwa;

    TextView TxtUstVal, TxtKdimVal;


    /*ФУНКЦИИ*/
    //Функция: Формирование строки
    public void SetDispText(TextView NameVar, String First, String Var, String Second){
        String result;
        result=First + Var + Second;
        NameVar.setText(result);
    }

    //Функция: Установка переключателя
  /*  void SetToggleSwitch(CompoundButton name, String Value){
        if ("ON".equals(Value)){
            name.setChecked(true);
            //name.setCh
        }
        else {
            name.setChecked(false);
        }
    }
*/
    //Функция: получения значений по умолчанию
    /*public String loadVar(String name){
        sPref = getPreferences(MODE_PRIVATE);
        return sPref.getString(name,"");
    }
*/
    //Функция: полуения значений с аппарата
    public void InputRef (String InputVarName, String InputVarVal) {
        switch (InputVarName){
            case "j":
                if (InputVarVal.equals("1")) {
                    ALARM(true);
                }
                else {
                    ALARM(false);
                }
                break;

            case "a":
                SetDispText(TxtUstavka, "Уставка нагрев- ", InputVarVal, " *С");
                break;

            case "b":
                SetDispText(TxtKdim, "Коэф. - ", InputVarVal, "");
                break;

            case "d":
                SetDispText(TxtHeatVal, "Нагрев - ", InputVarVal, " %");
                break;

            case "f":
                SetDispText(TxtColdTemp, "Темп. воды - ", InputVarVal, " *С");
                break;

            case "g":
                SetDispText(TxtDefTemp, "Темп. дефлегматора - ", InputVarVal, " *С");
                break;

            case "h":
                SetDispText(TxtResTemp, "Темп. резерв - ", InputVarVal, " *С");
                break;

            case "i":
                SetDispText(TxtTwa, "Уставка охлаждение - ", InputVarVal, " *С");
                break;

            case "e":
                if (InputVarVal.equals("1")){
                    TogPump.setChecked(true);
                }
                else {
                    TogPump.setChecked(false);
                }
                break;

            case "c":
                if (InputVarVal.equals("1")){
                    TogHeat.setChecked(true);
                }
                else if (InputVarVal.equals("2")){
                    TogGon.setChecked(true);
                }
                else {
                    TogHeat.setChecked(false);
                    TogGon.setChecked(false);
                }
                break;

            default:
                break;
        }

    }

    /*МЕТОДЫ*/
    //Старт интерфейса
    @SuppressLint("SetTextI18n")
    private void IntStart() {
        Log.d(TAG, "IntStart");
        //Текстовые (+ИКОНКА АВАРИЯ)
        TxtUstavka = findViewById(R.id.textUstavka);
        TxtUstavka.setText("Уставка---*С");
        TxtKdim = findViewById(R.id.textKdim);
        TxtKdim.setText("Коэф---");
        TxtHeatVal = findViewById(R.id.textHeatVal);
        TxtHeatVal.setText("Нагрев---%");
        TxtColdTemp = findViewById(R.id.textColdTemp);
        TxtColdTemp.setText("Темп. воды---*С");
        TxtDefTemp = findViewById(R.id.textDefTemp);
        TxtDefTemp.setText("Темп. дефлегматора---*С");
        TxtResTemp = findViewById(R.id.textResTemp);
        TxtResTemp.setText("Темп. резерв---*С");
        TxtTwa = findViewById(R.id.textTwa);
        TxtTwa.setText("Уставка воды---*С");
        TxtUstVal = findViewById(R.id.TextButUst);
        //TxtUstVal.setText(ValUst);
        TxtKdimVal=findViewById(R.id.TextButKpid);
        //TxtKdimVal.setText(ValKdim);
        //(+ИКОНКА АВАРИЯ)
        TxtAlarm = findViewById(R.id.TextAlarm);
        ImVwAlarm = findViewById(R.id.ImageAlarm);
        //Log.d(TAG, "Текстовые");
        //Кнопки
        ButCls = findViewById(R.id.ButClose);
        ButCt = findViewById(R.id.ButConnect);
        ButKup = findViewById(R.id.ButKup);
        ButKdn = findViewById(R.id.ButKdn);
        ButUup = findViewById(R.id.ButUup);
        ButUdn = findViewById(R.id.ButUdn);
        ButSend = findViewById(R.id.ButSend);
        ButCls.setOnClickListener(OclBtn);
        ButCt.setOnClickListener(OclBtn);
        ButKup.setOnClickListener(OclBtn);
        ButKdn.setOnClickListener(OclBtn);
        ButUup.setOnClickListener(OclBtn);
        ButUdn.setOnClickListener(OclBtn);
        ButSend.setOnClickListener(OclBtn);
        //Log.d(TAG, "Кнопки");
        //Выключатели
        TogHeat=findViewById(R.id.TogButHeat);
        TogGon=findViewById(R.id.TogButGon);
        TogPump=findViewById(R.id.TogButPump);
        //Log.d(TAG, "Выключатели");
        //Назначение обработчиков
        //Видимость
        TxtAlarm.setVisibility(View.GONE);
        ImVwAlarm.setVisibility(View.GONE);
        //Log.d(TAG, "обработчиков Видимость");
        //Кнопки
        //Выключатели
        TogHeat.setOnCheckedChangeListener(TogHeatList);
        TogGon.setOnCheckedChangeListener(TogGonList);
        TogPump.setOnCheckedChangeListener(TogPumpList);
        ValKdim=35;
        ValUst=85;
        TxtKdimVal.setText(Integer.toString(ValKdim));
        TxtUstVal.setText(Integer.toString(ValUst));
        //Log.d(TAG, "обработчиков Выключатели");
    }

    //Обновление интерфейса
/*
    private void IntRepeat(){
        //Текстовые
        //Кнопки
        //Выключатели
        SetToggleSwitch(TogHeat,loadVar("LtSwAll"));
    }*/

    //ALARM
    public void ALARM(boolean ON) {
        if (ON){
                TxtAlarm.setVisibility(View.VISIBLE);
                ImVwAlarm.setVisibility(View.VISIBLE);
            }
        else {
            TxtAlarm.setVisibility(View.GONE);
            ImVwAlarm.setVisibility(View.GONE);
        }
    }

    //ОБРАБОТЧИКИ СОБЫТИЙ
    //Кнопки
    Button.OnClickListener OclBtn = new View.OnClickListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ButKup:
                    ValKdim=ValKdim+1;
                    TxtKdimVal.setText(Integer.toString(ValKdim));
                    break;
                case R.id.ButKdn:
                    ValKdim=ValKdim-1;
                    TxtKdimVal.setText(Integer.toString(ValKdim));
                    break;
                case R.id.ButUup:
                    ValUst=ValUst+1;
                    TxtUstVal.setText(Integer.toString(ValUst));
                    break;
                case R.id.ButUdn:
                    ValUst=ValUst-1;
                    TxtUstVal.setText(Integer.toString(ValUst));
                    break;
                case R.id.ButSend:
                    String Send;
                    Send= String.valueOf(ValKdim);
                    BtSend("b."+Send+";");
                    Send= String.valueOf(ValUst);
                    BtSend("a."+Send+";");
                    break;
                case R.id.ButClose:
                    BtClose();
                    onDestroy();
                    finish();
                    break;
                case R.id.ButConnect:
                    //ЗАПУСК BLUETOOTH
                    if (!ButConnectBool){
                        BtStart();
                        if (BtOn) {
                            Log.d(TAG, "BT:ЗАПУСК");
                            ButCt.setText("Отсоединить");
                            BtSend("k.1;");
                            ButConnectBool=true;
                        }
                        else {
                            Log.d(TAG, "BT:Ошибка");
                            ButConnectBool=false;
                        }
                    }
                    else {
                        BtClose();
                        ButCt.setText("Соединить");
                        ButConnectBool=false;
                        BtOn=false;
                    }
                    break;
                default:
                    break;
            }
        }
    };
    /*

    int Ustavka = int(EEPROM.read(1)); //Уставка температура поддержания
    int Kdim = int(EEPROM.read(2));    //Коэффициент

    int Mode = 0; //Режим 1 - Нагрев, 2 - Перегонка, 0 - Выкл

    int HeatVal = 0; //Значение в процентах на экран

    bool PumpOn = false;

    bool AlarmSw = false;

    int ColdTemp, DefTemp, ResTemp;

    int Twa = int(EEPROM.read(3));

        */
    //Переключатели
    ToggleButton.OnCheckedChangeListener TogHeatList = new ToggleButton.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(CompoundButton ToggleButton, boolean isChecked) {
            if (isChecked) {
                if (BtOn){
                    BtSend("c.1;");
                    TogGon.setChecked(false);
                }
            }
            else
            {
                if (BtOn) {
                    BtSend("c.0;");
                }
            }
        }
    };

    ToggleButton.OnCheckedChangeListener TogGonList = new ToggleButton.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(CompoundButton ToggleButton, boolean isChecked) {
            if (isChecked) {
                if (BtOn) {
                    BtSend("c.2;");
                    TogHeat.setChecked(false);
                }
            }
            else
            {
                if (BtOn) {
                    BtSend("c.0;");
                }

            }
        }
    };

    ToggleButton.OnCheckedChangeListener TogPumpList = new ToggleButton.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(CompoundButton ToggleButton, boolean isChecked) {
            if (isChecked) {
                if (BtOn) {
                    BtSend("e.1;");
                }
            }
            else
            {
                if (BtOn) {
                    BtSend("e.0;");
                }
            }
        }
    };

    /*###############################ОСНОВНОЕ ТЕЛО#############################*/
    //СТАРТ
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();
        Log.d(TAG,"MainActivity.onCreate");
        //СТАРТ ИНТЕРФЕЙС
        IntStart();
    }
    //РАБОТА
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"MainActivity.onResume");
    }
    //СТОПЭ
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG,"MainActivity.onStop");
    }

    //УБЕЙСЯ

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"MainActivity.onDestroy");
    }
    @Override
    public void onRestart(){
        super.onRestart();

        Log.d(TAG,"MainActivity.onRestart");
    }


    /*###########################БЛЮПУП - ПЕРЕМЕННЫЕ ФУНКЦИИ И МЕТОДЫ#############################*/
    //МАСКА ПРИЕМА: (УС-ВО):(ИМЯ ПЕРЕМЕННОЙ).(ЗНАЧЕНИЕ)
    //МАСКА ОТПРАВКИ (УС-ВО):(ИМЯ ПЕРЕМЕННОЙ).(ЗНАЧЕНИЕ)

    /*ПЕРЕМЕННЫЕ*/
    public boolean BtOn=false;
    //public static SharedPreferences sPref;
    private static final int REQUEST_ENABLE_BT = 1;
    public BluetoothAdapter btAdapter;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static BluetoothThread BtThread = null;
    public String macAddress = "20:16:09:26:72:11";

    //Прием
    private static final int RECIVE_MESSAGE = 1;
    Handler BtHandler;
    String msg_IN;

    /*МЕТОДЫ*/
    //Сохранение прирятой переменной
    /*private void saveVar(String name, String Value){
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editing = sPref.edit();
        editing.putString(name,Value);
        editing.apply();
    }
*/
    /*############################СТАРТ####################################*/
    @SuppressLint("HandlerLeak")
    public void BtStart() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            Log.d(TAG,"***Нет БЛЮПУПА***");
        }
        else {
            if (btAdapter.isEnabled()) {
                BtDrive(macAddress);
            } else {
                //Включить bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                BtDrive(macAddress);
            }
        }
    }
    /*############################РАБОТА####################################*/
    @SuppressLint("HandlerLeak")
    public void BtDrive(String MacAdress) {
        BluetoothDevice device = btAdapter.getRemoteDevice(MacAdress);
        BluetoothSocket btSocket;
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            Log.d(TAG,"***Создан сокет: "+btSocket+"***");
        } catch (IOException e) {
            Log.d(TAG,"***Не могу создать сокет***");
            BtOn=false;
            return;
        }
        btAdapter.cancelDiscovery();
        try {
            btSocket.connect();
        } catch (IOException e) {
            Log.d(TAG,"***Не коннектится***");
            BtOn=false;
            return;
        }
        Log.d(TAG,"***Соединился***");
        BtThread = new BluetoothThread(btSocket);
        BtThread.start();
        Log.d(TAG,"***BtThread.start***");
        BtHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                //txtArduino.setText(msg_exemple);
                Log.d(TAG,"***"+msg_IN+"***");
                char[] InMsg = msg_IN.toCharArray();
                //Log.d(TAG,InMsg.toString());
                StringBuilder MSG = new StringBuilder();
                String Name = "";
                String Value;
                int Lenght = InMsg.length;
                for (int i = 0; Lenght>i; i++){
                    char b = InMsg[i];
                    if(b=='.'){
                        Name = MSG.toString();
                        MSG = new StringBuilder();
                        b = 0;
                    }
                    else if (b==';'){
                        Value = MSG.toString();
                        MSG = new StringBuilder();
                        InputRef (Name, Value);
                        //saveVar(Name,Value);
                        Log.d(TAG,"***IN:"+Name+' '+Value+"***");
                    }
                    else {
                        MSG.append(b);
                    }
                }
            }
        };
        BtOn=true;
    }
    /*############################ПОСЫЛКА##################################*/
    public static void BtSend(String message) {
        Log.d(TAG,message);
        byte[] msgBuffer = message.getBytes();
        try {
            OutStream.write(msgBuffer);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e){
                Log.d(TAG,"***Ошибка приостановки потока***");
            }

        } catch (IOException e) {
            Log.d(TAG,"***Bt неисправен***");
        }

    }

    /*############################ВЫКЛЮЧИТЬ BLUETOOTH##################################*/
    public void BtClose(){
        if (BtThread.status_OutStream() != null) {
            BtSend("OutS.1;");
            try {
                MyBtSocket.close();
            } catch (IOException e) {
                Log.d(TAG,"***Не могу закрыть сокет***");
                return;
            }
        }

        Log.d(TAG,"***Cокет закрыт***");
    }

    /*ОТДЕЛЬНЫЙ ПОТОК ДЛЯ ПЕРЕДАЧИ/ПРИЕМА*/
    public BluetoothSocket MyBtSocket;
    public static OutputStream OutStream;
    public InputStream InStream;
    public class BluetoothThread extends Thread {

        BluetoothThread(BluetoothSocket socket) {
            MyBtSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException ignored) {
            }
            InStream = tmpIn;
            OutStream = tmpOut;
        }
        Object status_OutStream() {
            if (OutStream == null) {
                return null;
            } else {
                return OutStream;
            }
        }
        public void run() {
            while (true){
                try {
                    int bytes = InStream.available();
                    if (bytes == 0) {
                        try {
                            Thread.sleep(350);
                        } catch (InterruptedException e){
                            Log.d(TAG,"***Ошибка приостановки потока***");
                        }
                        continue;
                    }
                    byte[] buffer = new byte[bytes];
                    bytes = InStream.read(buffer);
                    msg_IN = new String(buffer);
                    BtHandler.obtainMessage(RECIVE_MESSAGE,bytes,-1,buffer).sendToTarget();
                }catch (IOException e){
                    Log.d(TAG,"***Ошибка буфера***");
                    break;
                }
            }
        }

    }

}
