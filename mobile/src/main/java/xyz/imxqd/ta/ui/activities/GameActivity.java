package xyz.imxqd.ta.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import xyz.imxqd.ta.R;
import xyz.imxqd.ta.game.AI;
import xyz.imxqd.ta.game.FiveChessPanel;
import xyz.imxqd.ta.game.OnGameStatusChangeListener;
import xyz.imxqd.ta.game.RobotAI;
import xyz.imxqd.ta.game.RobotAI2;
import xyz.imxqd.ta.im.model.TShockMessage;
import xyz.imxqd.ta.im.model.TVoiceMessage;
import xyz.imxqd.ta.ui.fragments.GameControlFragment;
import xyz.imxqd.ta.ui.fragments.ShockRecordFragment;
import xyz.imxqd.ta.ui.fragments.SoundRecordFragment;
import xyz.imxqd.ta.utils.UserSettings;

import static xyz.imxqd.ta.game.FiveChessPanel.TYPE_BLACK;
import static xyz.imxqd.ta.game.FiveChessPanel.TYPE_WHITE;

public class GameActivity extends AppCompatActivity implements SoundRecordFragment.RecordCallback,
        ShockRecordFragment.RecordCallback{

    private static final String TAG = "GameActivity";

    private FiveChessPanel mGamePanel;
    private ViewPager mViewPager;
    private AI mAi;

    private Handler mHander = new Handler(new Handler.Callback() {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        int timeout = UserSettings.getAILevel(this);
        if (timeout == 0) {
            mAi = new RobotAI(15);
        } else {
            mAi = new RobotAI2(15, timeout);
        }
        Log.d(TAG, "onCreate: " + timeout);
        mAi.reset();

        mGamePanel = (FiveChessPanel) findViewById(R.id.id_wuziqi);
        mGamePanel.setTouchDisable(TYPE_WHITE);
        mGamePanel.setOnGameStatusChangeListener(new OnGameStatusChangeListener() {
            @Override
            public void onPlacePiece(final int type, Point point) {
                mAi.initBoard(mGamePanel.getWhitePieces(), mGamePanel.getBlackPieces());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Point p = mAi.nextBest();
                        Log.d(TAG, "run: " + p);
                        if (type == FiveChessPanel.TYPE_BLACK) {
                            mHander.sendMessage(mHander.obtainMessage(TYPE_WHITE, p.x, p.y));
                        } else {
                            mHander.sendMessage(mHander.obtainMessage(FiveChessPanel.TYPE_BLACK, p.x, p.y));
                        }
                    }
                }).start();


            }

            @Override
            public void onGameOver(int gameWinResult) {

            }
        });

        mViewPager = (ViewPager) findViewById(R.id.game_view_pager);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return GameControlFragment.newInstance();
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
}
