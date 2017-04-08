package xyz.imxqd.ta.ui.fragments;


import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.TextView;

import xyz.imxqd.ta.R;
import xyz.imxqd.ta.im.model.TShockMessage;
import xyz.imxqd.ta.utils.Shocker;

public class ShockRecordFragment extends BaseFragment implements View.OnTouchListener {

    private static final String TAG = "ShockRecordFragment";

    private static final int DISTANCE_Y_SEND_OR_CANCEL = 80;

    private View mBgView;
    private TextView mText;
    private ImageView mIcon;

    private ViewPropertyAnimator animator;
    private boolean isRecording = false;

    private TShockMessage message;
    private long touchTime = 0;

    private RecordCallback mCallback;


    public ShockRecordFragment() {
        // Required empty public constructor
    }

    public static ShockRecordFragment newInstance() {
        return new ShockRecordFragment();
    }


    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_shock_record;
    }

    @Override
    protected void findViews() {
        mBgView = f(R.id.shock_bg_view);
        mText = f(R.id.shock_text);
        mIcon = f(R.id.shock_icon);
    }

    @Override
    protected void initUI() {
        animator = mBgView.animate();
    }

    @Override
    protected void setupEvents() {
        mIcon.setOnTouchListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ShockRecordFragment.RecordCallback) {
            mCallback = (ShockRecordFragment.RecordCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ShockRecordFragment");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int y = (int) event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mBgView.setBackgroundResource(R.drawable.circular_grey_200);
            mText.setText(R.string.shock_release_to_pause);
            if (!isRecording) {
                message = TShockMessage.obtain();
                touchTime = System.currentTimeMillis();
            }
            message.addPoint(System.currentTimeMillis() - touchTime);
            touchTime = System.currentTimeMillis();
            isRecording = true;
            Shocker.shock();
            startAnim();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            mBgView.setBackgroundResource(R.drawable.circular_outline_grey_200);
            message.addPoint(System.currentTimeMillis() - touchTime);
            touchTime = System.currentTimeMillis();
            Shocker.cancal();
            stopAnim();
            mText.setText(R.string.shock_press_to_record);
            if (isWantToCancel(y)) {
                isRecording = false;
                mCallback.onShockRecordingCancel();
            } else if (isWantToSend(y)) {
                isRecording = false;
                mCallback.onShockRecordingSend(message);
            } else {
                isRecording = true;
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (isWantToCancel(y)) {
                mText.setText(R.string.shock_release_to_cancel);
            } else if (isWantToSend(y)) {
                mText.setText(R.string.shock_release_to_send);
            } else {
                mText.setText(R.string.shock_release_to_pause);
            }
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            mBgView.setBackgroundResource(R.drawable.circular_outline_grey_200);
            Shocker.cancal();
            stopAnim();
            mText.setText(R.string.shock_press_to_record);
            isRecording = false;
            mCallback.onShockRecordingCancel();
        }
        return false;
    }

    public boolean isWantToCancel(int y){
        if (y > mIcon.getHeight() + DISTANCE_Y_SEND_OR_CANCEL) {
            return true;
        }
        return false;
    }

    public boolean isWantToSend(int y){
        if (y < -DISTANCE_Y_SEND_OR_CANCEL) {
            return true;
        }
        return false;
    }


    private void startAnim() {
        if (!isRecording) {
            return;
        }
        animator.scaleX(2)
                .scaleY(2);
        animator.setDuration(200);
        animator.start();
    }

    private void stopAnim() {
        animator.scaleX(1).scaleY(1).start();
    }

    public interface RecordCallback {
        void onShockRecordingCancel();
        void onShockRecordingSend(TShockMessage ms);
    }

}
