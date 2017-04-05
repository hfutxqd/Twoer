package xyz.imxqd.ta.ui.fragments;

import xyz.imxqd.ta.R;

/**
 * Created by imxqd on 17-4-4.
 */

public class GameControlFragment extends BaseFragment {

    public static GameControlFragment newInstance() {
        return new GameControlFragment();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_game_control;
    }
}
