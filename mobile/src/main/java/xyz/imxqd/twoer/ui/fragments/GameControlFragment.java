package xyz.imxqd.twoer.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import xyz.imxqd.twoer.R;
import xyz.imxqd.twoer.game.FiveChessPanel;

/**
 * Created by imxqd on 17-4-4.
 */

public class GameControlFragment extends BaseFragment implements View.OnClickListener {
    public static final int ACTION_UNDO = 1;
    public static final int ACTION_RESTART = 2;

    private OnButtonClickCallback mCallback;
    private TextView tvStatus;
    private ImageView ivIcon;
    private Button btnUndo;
    private Button btnRestart;
    private int mType;

    public static GameControlFragment newInstance() {
        return new GameControlFragment();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("type", mType);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mType = savedInstanceState.getInt("type");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof  OnButtonClickCallback) {
            mCallback = (OnButtonClickCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnButtonClickCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public void setYourPieceType(@FiveChessPanel.PieceType int type) {
        mType = type;
    }

    public void setCurrentPieceType(@FiveChessPanel.PieceType int type) {
        if (mType == type) {
            tvStatus.setText(R.string.game_control_me);
        } else {
            tvStatus.setText(R.string.game_control_ta);
        }
        if(type == FiveChessPanel.TYPE_WHITE) {
            ivIcon.setImageResource(R.drawable.stone_white);
        } else if (type == FiveChessPanel.TYPE_BLACK) {
            ivIcon.setImageResource(R.drawable.stone_black);
        }
    }

    @Override
    protected void findViews() {
        tvStatus = f(R.id.game_tv_status);
        ivIcon = f(R.id.game_iv_status_icon);
        btnUndo = f(R.id.game_btn_undo);
        btnRestart = f(R.id.game_btn_restart);
    }

    @Override
    protected void setupEvents() {
        btnUndo.setOnClickListener(this);
        btnRestart.setOnClickListener(this);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_game_control;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.game_btn_restart:
                if (mCallback != null) {
                    mCallback.onGameButtonClick(ACTION_RESTART);
                }
                break;
            case R.id.game_btn_undo:
                if (mCallback != null) {
                    mCallback.onGameButtonClick(ACTION_UNDO);
                }
                break;
        }
    }

    public interface OnButtonClickCallback {
        void onGameButtonClick(int actionType);
    }
}
