package com.example.z.aplite;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private String filename;
    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.APnumberenter).setOnClickListener(this);
        findViewById(R.id.AP0).setOnClickListener(this);
        findViewById(R.id.AP5).setOnClickListener(this);
        findViewById(R.id.AP10).setOnClickListener(this);
        findViewById(R.id.AP15).setOnClickListener(this);
        findViewById(R.id.AP20).setOnClickListener(this);
        findViewById(R.id.AP25).setOnClickListener(this);
        findViewById(R.id.AP30).setOnClickListener(this);
        findViewById(R.id.AP35).setOnClickListener(this);
        findViewById(R.id.SQ0).setOnClickListener(this);
        findViewById(R.id.SQ1).setOnClickListener(this);
        findViewById(R.id.SQ2).setOnClickListener(this);
        findViewById(R.id.SQ3).setOnClickListener(this);
        findViewById(R.id.SQ4).setOnClickListener(this);
        findViewById(R.id.Refreshall).setOnClickListener(this);
        findViewById(R.id.Close).setOnClickListener(this);

        CheckBox fgo = findViewById(R.id.fgo);
        CheckBox sq = findViewById(R.id.sq);

        fgo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                setfgo(isChecked);
            }
        });

        sq.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                setsq(isChecked);
            }
        });

        long nowmill = System.currentTimeMillis();

        SharedPreferences sp = getSharedPreferences("configuration", Context.MODE_PRIVATE);
        boolean isfirst = sp.getBoolean("isfirst", true);
        if (isfirst) {
            SharedPreferences.Editor ed = sp.edit();

            ed.putBoolean("isfirst",false);

            ed.putInt("CurAP", 0);
            ed.putInt("CurSQ", 0);
            ed.putBoolean("fgo", true);
            ed.putBoolean("sq", true);
            ed.putLong("APtime", nowmill);
            ed.putLong("SQtime", nowmill);
            ed.commit();
        }

        refresh();

        mTimer = new Timer();
        setTimerTask();

    }

    private void setTimerTask() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                doActionHandler.sendMessage(message);
            }
        }, 500, 10000/* 表示1000毫秒之後，每隔1000毫秒執行一次 */);
    }

    private Handler doActionHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int msgId = msg.what;
            switch (msgId) {
                case 1:
                    refresh();                    // do some action
                    break;
                default:
                    break;
            }
        }
    };

    protected void onDestroy() {
        super.onDestroy();
        // cancel timer
        mTimer.cancel();
    }

    public void onClick(View v) {
        switch (v.getId()) {
                case R.id.AP0:
                    setAPto(0);
                    break;
                case R.id.AP5:
                    setAPto(5);
                    break;
                case R.id.AP10:
                    setAPto(10);
                    break;
                case R.id.AP15:
                    setAPto(15);
                    break;
                case R.id.AP20:
                    setAPto(20);
                    break;
                case R.id.AP25:
                    setAPto(25);
                    break;
                case R.id.AP30:
                    setAPto(30);
                    break;
                case R.id.AP35:
                    setAPto(35);
                    break;
                case R.id.SQ0:
                    setSQto(0);
                    break;
                case R.id.SQ1:
                    setSQto(1);
                    break;
                case R.id.SQ2:
                    setSQto(2);
                    break;
                case R.id.SQ3:
                    setSQto(3);
                    break;
                case R.id.SQ4:
                    setSQto(4);
                    break;
                case R.id.APnumberenter:
                    String target = ((EditText) findViewById(R.id.APnumber)).getText().toString();
                    if (!target.equals("")) {
                        int apto = Integer.parseInt(target);
                        if (apto >= 0 && apto <= 138) {
                            setAPto(apto);
                            ((EditText) findViewById(R.id.APnumber)).setText("");
                        }
                    }
                    break;
                case R.id.Refreshall:
                    refresh();
                    break;
                case R.id.Close:
                    System.exit(0);
                    break;

                default:
                    break;
        }
    }

    private void setAPto(int target){
        SharedPreferences sp = getSharedPreferences("configuration", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt("CurAP",target);
        ed.putLong("APtime",System.currentTimeMillis());
        ed.commit();
        refresh();
    }

    private void setSQto(int target){
        SharedPreferences sp = getSharedPreferences("configuration", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt("CurSQ",target);
        ed.putLong("SQtime",System.currentTimeMillis());
        ed.commit();
        refresh();
    }

    private void refresh(){

        SharedPreferences sp = getSharedPreferences("configuration", Context.MODE_PRIVATE);

        long nowmill = System.currentTimeMillis();

        long APsecondspassed = (nowmill - sp.getLong("APtime",0))/1000;
        long SQsecondspassed = (nowmill - sp.getLong("SQtime",0))/1000;

        long APadded = APsecondspassed / 300;
        long SQadded = SQsecondspassed / 7200;
        int newAP = sp.getInt("CurAP",0) + (int)APadded;
        if (newAP > 138) newAP = 138;
        int newSQ = sp.getInt("CurSQ",0) + (int)SQadded;
        if (newSQ > 6) newSQ = 6;

        int secondstonextAP = 300 - (int)(APsecondspassed % 300);
        int secondstonextSQ = 7200 - (int)(SQsecondspassed % 7200);
        int APleft = 137 - newAP;
        int SQleft = 5 - newSQ;

        int secondstofullAP = APleft * 300 + secondstonextAP;
        int secondstofullSQ = SQleft * 7200 + secondstonextSQ;

        long fullAPmill = System.currentTimeMillis() + secondstofullAP * 1000;
        long fullSQmill = System.currentTimeMillis() + secondstofullSQ * 1000;

        Date fullAPdate = new Date(fullAPmill);
        Date fullSQdate = new Date(fullSQmill);

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm满");

        String fullAPstr = formatter.format(fullAPdate);
        String fullSQstr = formatter.format(fullSQdate);

        SimpleDateFormat titleformatter = new SimpleDateFormat("现在HH:mm");
        Date nowdate = new Date(nowmill);
        String title = titleformatter.format(nowdate);
        setTitle(title);

        ((TextView) findViewById(R.id.APshow)).setText(newAP+"");
        ((TextView) findViewById(R.id.SQshow)).setText(newSQ+"");
        ((TextView) findViewById(R.id.APfulltime)).setText(fullAPstr);
        ((TextView) findViewById(R.id.SQfulltime)).setText(fullSQstr);

        ((CheckBox) findViewById(R.id.fgo)).setChecked(sp.getBoolean("fgo",true));
        ((CheckBox) findViewById(R.id.sq)).setChecked(sp.getBoolean("sq",true));

    }

    private void setfgo(boolean onoff){
        int vis;
        if (onoff) vis = View.VISIBLE; else vis = View.INVISIBLE;
        findViewById(R.id.textView).setVisibility(vis);
        findViewById(R.id.APshow).setVisibility(vis);
        findViewById(R.id.APfulltime).setVisibility(vis);
        findViewById(R.id.textView4).setVisibility(vis);
        findViewById(R.id.APnumber).setVisibility(vis);
        findViewById(R.id.APnumberenter).setVisibility(vis);
        findViewById(R.id.AP0).setVisibility(vis);
        findViewById(R.id.AP5).setVisibility(vis);
        findViewById(R.id.AP10).setVisibility(vis);
        findViewById(R.id.AP15).setVisibility(vis);
        findViewById(R.id.AP20).setVisibility(vis);
        findViewById(R.id.AP25).setVisibility(vis);
        findViewById(R.id.AP30).setVisibility(vis);
        findViewById(R.id.AP35).setVisibility(vis);
        SharedPreferences sp = getSharedPreferences("configuration", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("fgo",onoff);
        ed.commit();
    }

    private void setsq(boolean onoff){
        int vis;
        if (onoff) vis = View.VISIBLE; else vis = View.INVISIBLE;
        findViewById(R.id.textView5).setVisibility(vis);
        findViewById(R.id.SQshow).setVisibility(vis);
        findViewById(R.id.SQfulltime).setVisibility(vis);
        findViewById(R.id.textView8).setVisibility(vis);
        findViewById(R.id.SQ0).setVisibility(vis);
        findViewById(R.id.SQ1).setVisibility(vis);
        findViewById(R.id.SQ2).setVisibility(vis);
        findViewById(R.id.SQ3).setVisibility(vis);
        findViewById(R.id.SQ4).setVisibility(vis);
        SharedPreferences sp = getSharedPreferences("configuration", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("sq",onoff);
        ed.commit();
    }


}
