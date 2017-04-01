package xyz.imxqd.ta.ui.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import xyz.imxqd.ta.R;
import xyz.imxqd.ta.im.Client;
import xyz.imxqd.ta.model.IMessage;
import xyz.imxqd.ta.model.TextMessage;
import xyz.imxqd.ta.model.VoiceMessage;
import xyz.imxqd.ta.service.MessageService;
import xyz.imxqd.ta.ui.fragments.SoundRecordFragment;
import xyz.imxqd.ta.utils.UserSettings;

import static xyz.imxqd.ta.Constants.SETTING_TARGET_ID;

public class MainActivity extends AppCompatActivity implements SoundRecordFragment.RecordCallback, ServiceConnection {

    private static final String TAG = "MainActivity";

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = (TextView) findViewById(R.id.text);
        editText = (EditText) findViewById(R.id.et_user_id);
        String id = UserSettings.readString(SETTING_TARGET_ID);
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
        String id = editText.getText().toString();
        UserSettings.save(SETTING_TARGET_ID, id);
        IMessage msg = TextMessage.obtain("Test...", id);
        Client.sendMessage(msg);
    }

    @Override
    public void onRecordingCancel() {

    }

    @Override
    public void onRecordingSend(String filename) {
        IMessage msg = VoiceMessage.obtain(filename, editText.getText().toString());
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
