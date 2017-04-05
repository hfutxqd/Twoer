package xyz.imxqd.ta.ui.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.stephentuso.welcome.BasicPage;
import com.stephentuso.welcome.FragmentWelcomePage;
import com.stephentuso.welcome.TitlePage;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeConfiguration;

import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import xyz.imxqd.ta.Constants;
import xyz.imxqd.ta.R;
import xyz.imxqd.ta.im.Client;
import xyz.imxqd.ta.im.model.TBindMessage;
import xyz.imxqd.ta.qrcode.QrCodeActivity;
import xyz.imxqd.ta.qrcode.utils.QrUtils;
import xyz.imxqd.ta.service.MessageService;
import xyz.imxqd.ta.ui.fragments.BindFragment;
import xyz.imxqd.ta.utils.UserSettings;

/**
 * Created by imxqd on 17-4-2.
 */

public class FirstActivity extends WelcomeActivity implements IRongCallback.ISendMessageCallback, ServiceConnection, MessageService.BindCallback {
    private static final String TAG = "FirstActivity";

    BindFragment fragment;

    private TBindMessage mBindMessage;
    private Bitmap bitmap;
    private String mId;

    private boolean isServiceConnected = false;
    private MessageService.MessageBinder mMessageService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindService(new Intent(this, MessageService.class), this, BIND_AUTO_CREATE);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UserSettings.readString(Constants.SETTING_TARGET_ID) != null
                && UserSettings.readBoolean(Constants.SETTING_ACCEPTED)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (isServiceConnected) {
            unbindService(this);
            isServiceConnected = false;
        }
        super.onDestroy();
    }

    @Override
    protected WelcomeConfiguration configuration() {

        mId = UserSettings.getUserId(this);
        bitmap = QrUtils.generateQRCode(mId);
        fragment = BindFragment.newInstance(bitmap);
        FragmentWelcomePage page = new FragmentWelcomePage() {
            @Override
            protected Fragment fragment() {
                return fragment;
            }
        };

        return new WelcomeConfiguration.Builder(this)
                .page(new TitlePage(R.mipmap.ic_launcher_round,
                        getString(R.string.fisrt_title_1))
                )
                .page(new BasicPage(R.mipmap.ic_launcher_round,
                        getString(R.string.fisrt_title_2),
                        getString(R.string.fisrt_des_2))
                )
                .bottomLayout(WelcomeConfiguration.BottomLayout.BUTTON_BAR_SINGLE)
                .page(page)
                .showPrevButton(true)
                .showNextButton(true)
                .canSkip(false)
                .useCustomDoneButton(true)
                .swipeToDismiss(false)
                .build();
    }

    @Override
    protected void onButtonBarFirstPressed() {
        startActivityForResult(new Intent(this, QrCodeActivity.class), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String targetId = data.getStringExtra(QrCodeActivity.RESULT_KEY);
            UserSettings.save(Constants.SETTING_TARGET_ID, targetId);
            Client.setTargetId(targetId);
            mBindMessage = TBindMessage.obtain(targetId, UserSettings.getUserId(this));
            Client.sendMessage(mBindMessage, this);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttached(Message message) {

    }

    @Override
    public void onSuccess(Message message) {
        Toast.makeText(this, R.string.bind_message_success, Toast.LENGTH_LONG).show();
        fragment.setText(getString(R.string.fisrt_bind_code, mBindMessage.getRandomCode()));

        viewPager.setCurrentItem(2);
    }

    @Override
    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
        Log.e(TAG, "send bind message error : " + errorCode);
        Toast.makeText(this, R.string.bind_message_error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        isServiceConnected = true;
        mMessageService = (MessageService.MessageBinder) service;
        mMessageService.setBindCallback(this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        isServiceConnected = false;
    }

    @Override
    public void onAccept() {
        Toast.makeText(this, R.string.bind_message_accept, Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
