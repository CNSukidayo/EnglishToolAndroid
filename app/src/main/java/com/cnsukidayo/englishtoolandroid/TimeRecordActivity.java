package com.cnsukidayo.englishtoolandroid;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.cnsukidayo.englishtoolandroid.context.EnglishToolProperties;
import com.cnsukidayo.englishtoolandroid.core.entitys.TimeRecord;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public class TimeRecordActivity extends AppCompatActivity {

    private RelativeLayout passTimeView;
    private RelativeLayout timeSettingView;
    private EditText articleTime;
    private EditText cautiousReadTime;
    private EditText doubleCautiousReadTime;
    private EditText translateTime;
    private EditText matchParagraphTime;
    private EditText matchWordTime;
    private Button startTime;
    private Button saveSetting;

    // 单选框组合
    private RadioGroup radioGroup;

    private TextView timeView;
    private TextView another;
    private Button failThis;

    private Gson gson = new Gson();
    private TimeRecord timeRecord;
    private LocalTime localTime;
    private Timer timer;

    private volatile boolean flag = true;
    private volatile boolean chooseDouble = false;
    private volatile boolean mock = false;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        public boolean mode = false;

        @SuppressLint("ResourceAsColor")
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                if (mock) {
                    localTime = localTime.plusSeconds(150);
                    if (localTime.getSecond() == 0) {
                        timeView.setText(localTime.toString() + ":00");
                    } else {
                        timeView.setText(localTime.toString());
                    }
                    if (localTime.isAfter(LocalTime.of(0, 0, 0)) && localTime.isBefore(LocalTime.of(0, 30, 0))) {
                        another.setText("Part One.");
                    } else if (localTime.isAfter(LocalTime.of(0, 29, 59)) && localTime.isBefore(LocalTime.of(0, 55, 0))) {
                        passTimeView.setBackground(getResources().getDrawable(R.drawable.background_yellow));
                        another.setText("Part Two.");
                    } else if (localTime.isAfter(LocalTime.of(0, 54, 59)) && localTime.isBefore(LocalTime.of(2, 05, 0))) {
                        another.setText("Part Three.");
                        passTimeView.setBackground(getResources().getDrawable(R.drawable.background_pink));
                    } else {
                        another.setText("NED!");
                        passTimeView.setBackground(getResources().getDrawable(R.drawable.background_red));
                        flag = false;
                        timer.cancel();
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                } else {
                    if ((localTime.getMinute() != 0 || localTime.getSecond() != 0) && !mode) {
                        localTime = localTime.minusSeconds(1);
                        timeView.setText(localTime.toString());
                    } else {
                        if (!mode) {
                            passTimeView.setBackground(getResources().getDrawable(R.drawable.background_red));
                        }
                        mode = true;
                        localTime = localTime.plusSeconds(1);
                        timeView.setText(localTime.toString());
                    }
                }
            } else if (msg.what == 1) {
                mode = false;
            } else if (msg.what == 2) {
                another.setText(localTime.toString());
            }
        }

    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_time_record);

        articleTime = findViewById(R.id.articleTime);
        cautiousReadTime = findViewById(R.id.cautiousReadTime);
        doubleCautiousReadTime = findViewById(R.id.doubleCautiousReadTime);
        translateTime = findViewById(R.id.translateTime);
        matchParagraphTime = findViewById(R.id.matchParagraphTime);
        matchWordTime = findViewById(R.id.matchWordTime);
        startTime = findViewById(R.id.startTime);
        saveSetting = findViewById(R.id.saveSetting);
        radioGroup = findViewById(R.id.radioGroup);
        passTimeView = findViewById(R.id.passTimeView);
        timeView = findViewById(R.id.timeView);
        failThis = findViewById(R.id.failThis);
        timeSettingView = findViewById(R.id.timeSettingView);
        another = findViewById(R.id.another);
        // 先读取到TimeRecord
        this.timeRecord = getTimeRecord();
        articleTime.setText(timeRecord.getArticle());
        cautiousReadTime.setText(timeRecord.getCautiousReadTime());
        doubleCautiousReadTime.setText(timeRecord.getDoubleCautiousReadTime());
        translateTime.setText(timeRecord.getTranslateTime());
        matchParagraphTime.setText(timeRecord.getMatchParagraphTime());
        matchWordTime.setText(timeRecord.getMatchWordTime());

        saveSetting.setOnClickListener(v -> {
            timeRecord.setArticle(articleTime.getText().toString());
            timeRecord.setCautiousReadTime(cautiousReadTime.getText().toString());
            timeRecord.setDoubleCautiousReadTime(doubleCautiousReadTime.getText().toString());
            timeRecord.setTranslateTime(translateTime.getText().toString());
            timeRecord.setMatchParagraphTime(matchParagraphTime.getText().toString());
            timeRecord.setMatchWordTime(matchWordTime.getText().toString());
            String absolutePath = EnglishToolProperties.internalEntireEnglishSourcePath + EnglishToolProperties.timeRecord;
            try (FileOutputStream writer = new FileOutputStream(absolutePath)) {
                String result = gson.toJson(timeRecord);
                writer.write(result.getBytes(StandardCharsets.UTF_8));
                writer.flush();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

        });

        // 开始计时
        startTime.setOnClickListener(v -> {
            String result = null;
            chooseDouble = false;
            mock = false;
            switch (radioGroup.getCheckedRadioButtonId()) {
                case R.id.articleButton:
                    result = articleTime.getText().toString();
                    break;
                case R.id.cautiousButton:
                    result = cautiousReadTime.getText().toString();
                    break;
                case R.id.doubleCautiousButton:
                    result = doubleCautiousReadTime.getText().toString();
                    chooseDouble = true;
                    break;
                case R.id.translateButton:
                    result = translateTime.getText().toString();
                    break;
                case R.id.matchParagraphButton:
                    result = matchParagraphTime.getText().toString();
                    break;
                case R.id.matchWordButton:
                    result = matchWordTime.getText().toString();
                    break;
                case R.id.mockExamineButton:
                    mock = true;
                    result = "00:00";
                    break;
            }
            this.localTime = LocalTime.parse("00:" + result, DateTimeFormatter.ISO_LOCAL_TIME);
            startTimeHandle();
        });

        // 结束
        failThis.setOnClickListener(v -> {
            if (flag) {
                if (chooseDouble) {
                    Message message = new Message();
                    message.what = 2;
                    handler.sendMessage(message);
                    chooseDouble = false;
                } else {
                    flag = false;
                    timer.cancel();
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            } else {
                flag = true;
                passTimeView.setVisibility(View.GONE);
                timeSettingView.setVisibility(View.VISIBLE);
            }
        });
    }

    private TimeRecord getTimeRecord() {
        String absolutePath = EnglishToolProperties.internalEntireEnglishSourcePath + EnglishToolProperties.timeRecord;
        File includeFile = new File(absolutePath);
        TimeRecord result = null;
        if (includeFile.exists()) {
            try {
                result = gson.fromJson(new FileReader(includeFile), TimeRecord.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return result;
        }
        return new TimeRecord();


    }

    @SuppressLint("ResourceAsColor")
    private void startTimeHandle() {
        passTimeView.setVisibility(View.VISIBLE);
        timeSettingView.setVisibility(View.GONE);
        passTimeView.setBackground(getResources().getDrawable(R.drawable.background_green));
        another.setText("");
        timeView.setText(localTime.toString());
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 0;
                handler.sendMessage(message);
            }
        }, 0, 1000);
    }

}
