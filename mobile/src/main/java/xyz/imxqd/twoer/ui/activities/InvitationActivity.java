package xyz.imxqd.twoer.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import xyz.imxqd.twoer.Constants;
import xyz.imxqd.twoer.R;
import xyz.imxqd.twoer.im.Client;
import xyz.imxqd.twoer.im.model.TBindMessage;
import xyz.imxqd.twoer.utils.UserSettings;

public class InvitationActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String ARG_SENDER_ID = "sender_id";
    public static final String ARG_RANDOM_CODE = "random_code";

    private TextView tvText;

    private TextView tvConfirm;
    private TextView tvCancel;

    private String mSendId;
    private String mRandomCode;

    public static void start(String senderId, String randomCode, Context c) {
        Intent intent = new Intent(c, InvitationActivity.class);
        intent.putExtra(InvitationActivity.ARG_SENDER_ID, senderId);
        intent.putExtra(InvitationActivity.ARG_RANDOM_CODE, randomCode);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);
        mSendId = getIntent().getStringExtra(ARG_SENDER_ID);
        mRandomCode = getIntent().getStringExtra(ARG_RANDOM_CODE);
        tvText = (TextView) findViewById(R.id.invitation_text);
        tvConfirm = (TextView) findViewById(R.id.tv_confirm_panel);
        tvCancel = (TextView) findViewById(R.id.tv_cancel_panel);
        if (mRandomCode != null) {
            tvText.setText(getString(R.string.invitation_text, mRandomCode));
        }

        tvConfirm.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confirm_panel:
                UserSettings.save(Constants.SETTING_TARGET_ID, mSendId);
                UserSettings.save(Constants.SETTING_ACCEPTED, true);
                TBindMessage message = TBindMessage.obtain(mSendId, UserSettings.getUserId(this));
                Client.sendMessage(message);
                break;
            case R.id.tv_cancel_panel:
                break;
        }
        finish();
    }
}
