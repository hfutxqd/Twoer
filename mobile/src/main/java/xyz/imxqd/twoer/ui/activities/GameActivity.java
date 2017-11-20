package xyz.imxqd.twoer.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import xyz.imxqd.twoer.R;
import xyz.imxqd.twoer.game.AI;
import xyz.imxqd.twoer.game.FiveChessPanel;
import xyz.imxqd.twoer.game.OnGameStatusChangeListener;
import xyz.imxqd.twoer.game.RobotAI;
import xyz.imxqd.twoer.game.RobotAI2;
import xyz.imxqd.twoer.im.model.TShockMessage;
import xyz.imxqd.twoer.im.model.TVoiceMessage;
import xyz.imxqd.twoer.ui.fragments.GameControlFragment;
import xyz.imxqd.twoer.ui.fragments.ShockRecordFragment;
import xyz.imxqd.twoer.ui.fragments.SoundRecordFragment;
import xyz.imxqd.twoer.utils.UserSettings;

import static xyz.imxqd.twoer.game.FiveChessPanel.TYPE_BLACK;
import static xyz.imxqd.twoer.game.FiveChessPanel.TYPE_WHITE;

public class GameActivity extends AppCompatActivity implements SoundRecordFragment.RecordCallback,
        ShockRecordFragment.RecordCallback, GameControlFragment.OnButtonClickCallback,
        OnGameStatusChangeListener {

    private static final String TAG = "GameActivity";

    private FiveChessPanel mGamePanel;
    private ViewPager mViewPager;
    private AI mAi;

    private GameControlFragment mGameControlFragment;

    private Handler mAIHandler;

    private Handler mUIHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case TYPE_BLACK: {
                    Point p = new Point(msg.arg1, msg.arg2);
                    mGamePanel.addPiece(TYPE_BLACK, p);
                }
                    break;
                case TYPE_WHITE: {
                    Point p = new Point(msg.arg1, msg.arg2);
                    mGamePanel.addPiece(TYPE_WHITE, p);
                }
                    break;
            }
            return true;
        }
    });

    private HandlerThread mAIThread = new HandlerThread("GameAI");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mAIThread.start();
        mAIHandler = new Handler(mAIThread.getLooper());

        mGamePanel = (FiveChessPanel) findViewById(R.id.id_wuziqi);
        mGamePanel.setTouchDisable(TYPE_WHITE);
        mGamePanel.setOnGameStatusChangeListener(this);

        mViewPager = (ViewPager) findViewById(R.id.game_view_pager);
        mGameControlFragment = GameControlFragment.newInstance();
        mGameControlFragment.setYourPieceType(TYPE_BLACK);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return mGameControlFragment;
                    case 1:
                        return SoundRecordFragment.newInstance();
                    case 2:
                        return ShockRecordFragment.newInstance();
                }
                return null;
            }

            @Override
            public int getCount() {
                return 3;
            }
        });
        mViewPager.setOffscreenPageLimit(3);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAIThread.quitSafely();
        mAIHandler = null;
    }

    private void initAI() {
        int timeout = UserSettings.getAILevel(this);
        if (timeout == 0) {
            mAi = new RobotAI(15);
        } else {
            mAi = new RobotAI2(15, timeout);
        }
        mAi.reset();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initAI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_settings){
            startActivity(new Intent(this, GameSettingsActivity.class));
        }
        return true;
    }

    @Override
    public void onShockRecordingCancel() {

    }

    @Override
    public void onShockRecordingSend(TShockMessage ms) {

    }

    @Override
    public void onSoundRecordingCancel() {

    }

    @Override
    public void onSoundRecordingSend(TVoiceMessage msg) {

    }

    @Override
    public void onGameButtonClick(int actionType) {
        if (actionType == GameControlFragment.ACTION_UNDO) {
            mGamePanel.undo();
        } else if (actionType == GameControlFragment.ACTION_RESTART) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.message_game_restart)
                    .setNegativeButton(R.string.btn_cancel, null)
                    .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mGamePanel.restartGame();
                            mAi.reset();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    @Override
    public void onPlacePiece(final int type, Point point) {
        if (type == TYPE_BLACK) {
            mGameControlFragment.setCurrentPieceType(TYPE_WHITE);
            mAi.initBoard(mGamePanel.getWhitePieces(), mGamePanel.getBlackPieces());
            mAIHandler.post(new Runnable() {
                @Override
                public void run() {
                    final Point p = mAi.nextBest();
                    mUIHandler.sendMessage(mUIHandler.obtainMessage(TYPE_WHITE, p.x, p.y));
                }
            });

        } else {
            mGameControlFragment.setCurrentPieceType(TYPE_BLACK);
        }
    }

    @Override
    public void onUndo(@FiveChessPanel.PieceType int type, Point point) {
        mGameControlFragment.setCurrentPieceType(type);
    }

    @Override
    public void onGameOver(int gameWinResult) {

    }

    @Override
    public void onGameRestart() {
        mGameControlFragment.setCurrentPieceType(TYPE_BLACK);
    }
}
