package com.cnsukidayo.englishtoolandroid;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.cnsukidayo.englishtoolandroid.core.enums.Command;
import com.cnsukidayo.englishtoolandroid.core.enums.CommandStatus;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ControllerComputerActivity extends AppCompatActivity {
    private Socket socket;
    private Button keyboardUP;
    private Button keyboardDOWN;
    private Button keyboardLEFT;
    private Button keyboardRIGHT;
    private Button mouseUP;
    private Button mouseDOWN;
    private Button mouseLEFT;
    private Button mouseRIGHT;
    private final Gson gson = new Gson();
    private ExecutorService mExecutorService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_controller_computer);
        mExecutorService = Executors.newCachedThreadPool();
        mExecutorService.execute(() -> {
            try {
                socket = new Socket("192.168.0.105", 8888);
                socket.setSoTimeout(60000);
                Log.d("" + socket.isClosed(), "233");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        keyboardUP = findViewById(R.id.keyboardUP);
        keyboardDOWN = findViewById(R.id.keyboardDOWN);
        keyboardLEFT = findViewById(R.id.keyboardLEFT);
        keyboardRIGHT = findViewById(R.id.keyboardRIGHT);
        mouseUP = findViewById(R.id.mouseUP);
        mouseDOWN = findViewById(R.id.mouseDOWN);
        mouseLEFT = findViewById(R.id.mouseLEFT);
        mouseRIGHT = findViewById(R.id.mouseRIGHT);

        keyboardUP.setOnClickListener(getOnClickListener());
        keyboardDOWN.setOnClickListener(getOnClickListener());
        keyboardLEFT.setOnClickListener(getOnClickListener());
        keyboardRIGHT.setOnClickListener(getOnClickListener());
        mouseUP.setOnClickListener(getOnClickListener());
        mouseDOWN.setOnClickListener(getOnClickListener());
        mouseLEFT.setOnClickListener(getOnClickListener());
        mouseRIGHT.setOnClickListener(getOnClickListener());

    }

    private View.OnClickListener onClickListener = null;

    public View.OnClickListener getOnClickListener() {
        if (onClickListener == null) {
            onClickListener = v -> {
                Command command = new Command();
                switch (v.getId()) {
                    case R.id.keyboardUP:
                        command.setCommandStatus(CommandStatus.UP);
                        break;
                    case R.id.keyboardDOWN:
                        command.setCommandStatus(CommandStatus.DOWN);
                        break;
                    case R.id.keyboardLEFT:
                        command.setCommandStatus(CommandStatus.LEFT);
                        break;
                    case R.id.keyboardRIGHT:
                        command.setCommandStatus(CommandStatus.RIGHT);
                        break;
                    case R.id.mouseUP:
                        command.setCommandStatus(CommandStatus.MOUSEUP);
                        break;
                    case R.id.mouseDOWN:
                        command.setCommandStatus(CommandStatus.MOUSEDOWN);
                        break;
                    case R.id.mouseLEFT:
                        command.setCommandStatus(CommandStatus.MOUSELEFT);
                        break;
                    case R.id.mouseRIGHT:
                        command.setCommandStatus(CommandStatus.MOUSERIGHT);
                        break;
                }
                String out = gson.toJson(command) + "\n";
                mExecutorService.execute(() -> {
                    try {
                        OutputStream outputStream = socket.getOutputStream();
                        outputStream.write(out.getBytes(StandardCharsets.UTF_8));
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            };
        }
        return onClickListener;
    }

}
