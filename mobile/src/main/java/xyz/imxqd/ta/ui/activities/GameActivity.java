package xyz.imxqd.ta.ui.activities;

import android.content.DialogInterface;
import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;

import xyz.imxqd.ta.R;
import xyz.imxqd.ta.game.OnGameStatusChangeListener;
import xyz.imxqd.ta.game.WuziqiPanel;
import xyz.imxqd.ta.im.model.TShockMessage;
import xyz.imxqd.ta.im.model.TVoiceMessage;
import xyz.imxqd.ta.ui.fragments.GameControlFragment;
import xyz.imxqd.ta.ui.fragments.ShockRecordFragment;
import xyz.imxqd.ta.ui.fragments.SoundRecordFragment;

public class GameActivity extends AppCompatActivity implements SoundRecordFragment.RecordCallback,
        ShockRecordFragment.RecordCallback{

    private static final String TAG = "GameActivity";

    private WuziqiPanel mGamePanel;
    private AlertDialog.Builder alertBuilder;
    private AlertDialog alertDialog;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //游戏结束时弹出对话框
        alertBuilder = new AlertDialog.Builder(GameActivity.this);
        alertBuilder.setPositiveButton(R.string.title_game_again, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mGamePanel.restartGame();
            }
        });
        alertBuilder.setNegativeButton(R.string.title_game_exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GameActivity.this.finish();
            }
        });
        alertBuilder.setCancelable(false);
        alertBuilder.setTitle(R.string.title_game_over);

        mGamePanel = (WuziqiPanel) findViewById(R.id.id_wuziqi);
        mGamePanel.setOnGameStatusChangeListener(new OnGameStatusChangeListener() {
            @Override
            public void onPlacePiece(int type, Point point) {
                Log.d(TAG, "onPlacePiece: " + type);
                Log.d(TAG, "onPlacePiece: " + point.toString());
            }

            @Override
            public void onGameOver(int gameWinResult) {
                switch (gameWinResult) {
                    case WuziqiPanel.WHITE_WIN:
                        alertBuilder.setMessage(R.string.message_game_white_win);
                        break;
                    case WuziqiPanel.BLACK_WIN:
                        alertBuilder.setMessage(R.string.message_game_black_win);
                        break;
                    case WuziqiPanel.NO_WIN:
                        alertBuilder.setMessage(R.string.message_game_no_win);
                        break;
                }
                alertDialog = alertBuilder.create();
                alertDialog.show();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
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
