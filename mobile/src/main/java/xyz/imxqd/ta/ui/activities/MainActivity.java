package xyz.imxqd.ta.ui.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import xyz.imxqd.ta.R;
import xyz.imxqd.ta.im.Client;
import xyz.imxqd.ta.im.model.TShockMessage;
import xyz.imxqd.ta.im.model.TVoiceMessage;
import xyz.imxqd.ta.qrcode.QrCodeActivity;
import xyz.imxqd.ta.service.MessageService;
import xyz.imxqd.ta.ui.fragments.ShockRecordFragment;
import xyz.imxqd.ta.ui.fragments.SoundRecordFragment;
import xyz.imxqd.ta.utils.UserSettings;

import static xyz.imxqd.ta.Constants.SETTING_ACCEPTED;
import static xyz.imxqd.ta.Constants.SETTING_TARGET_ID;

public class MainActivity extends AppCompatActivity implements SoundRecordFragment.RecordCallback,
        ServiceConnection, ShockRecordFragment.RecordCallback{

    private static final String TAG = "MainActivity";

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        id = UserSettings.readString(SETTING_TARGET_ID);
        if (id == null || !UserSettings.readBoolean(SETTING_ACCEPTED)) {
            startActivity(new Intent(this, FirstActivity.class));
            finish();
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: " + data.getStringExtra(QrCodeActivity.RESULT_KEY));
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    public void onGameClick(View view) {
        startActivity(new Intent(this, GameActivity.class));
    }
}
