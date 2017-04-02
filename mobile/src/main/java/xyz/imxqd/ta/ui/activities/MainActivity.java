package xyz.imxqd.ta.ui.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import xyz.imxqd.ta.R;
import xyz.imxqd.ta.im.Client;
import xyz.imxqd.ta.model.ITMessage;
import xyz.imxqd.ta.model.TShockMessage;
import xyz.imxqd.ta.model.TTextTMessage;
import xyz.imxqd.ta.model.TVoiceMessage;
import xyz.imxqd.ta.service.MessageService;
import xyz.imxqd.ta.ui.fragments.ShockRecordFragment;
import xyz.imxqd.ta.ui.fragments.SoundRecordFragment;
import xyz.imxqd.ta.utils.Shocker;
import xyz.imxqd.ta.utils.UserSettings;

import static xyz.imxqd.ta.Constants.SETTING_TARGET_ID;

public class MainActivity extends AppCompatActivity implements SoundRecordFragment.RecordCallback,
        ServiceConnection, ShockRecordFragment.RecordCallback{

    private static final String TAG = "MainActivity";

    private EditText editText;

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = (TextView) findViewById(R.id.text);
        editText = (EditText) findViewById(R.id.et_user_id);
        id = UserSettings.readString(SETTING_TARGET_ID);
        if (id != null) {
            editText.setText(id);
        }
        tv.setText(Client.getUserId());
    }

    boolean isServiceConnected = false;
    @Override
    protected void onStart() {
        super.onStart();
        if (!isServiceConnected) {
            bindService(new Intent(this, MessageService.class), this, BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop() {
        if (isServiceConnected) {
            unbindService(this);
            isServiceConnected = false;
        }

        super.onStop();
    }

    public void onSendBtnClick(View view) {
        id = editText.getText().toString();
        UserSettings.save(SETTING_TARGET_ID, id);
        ITMessage msg = TTextTMessage.obtain("Test...", id);
        Client.sendMessage(msg);
    }

    @Override
    public void onSoundRecordingCancel() {

    }

    @Override
    public void onShockRecordingCancel() {

    }

    @Override
    public void onShockRecordingSend(TShockMessage msg) {
        Client.sendMessage(msg);
    }

    @Override
    public void onSoundRecordingSend(TVoiceMessage msg) {
        Client.sendMessage(msg);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        isServiceConnected = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        isServiceConnected = false;
    }

}
