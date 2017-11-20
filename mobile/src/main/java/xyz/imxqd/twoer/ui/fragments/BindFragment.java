package xyz.imxqd.twoer.ui.fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import xyz.imxqd.twoer.R;


public class BindFragment extends BaseFragment {

    private static final String ARG_BITMAP = "bitmap";

    private String text = null;
    private ImageView imIcon;
    private TextView tvText;

    private Bitmap bitmap;

    public BindFragment() {
        // Required empty public constructor
    }

    public static BindFragment newInstance(Bitmap bitmap) {
        BindFragment fragment = new BindFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_BITMAP, bitmap);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void findViews() {
        imIcon = f(R.id.bind_fragment_icon);
        tvText = f(R.id.bind_fragment_text);
    }

    @Override
    protected void initMember() {
        if (getArguments() != null) {
            bitmap = getArguments().getParcelable(ARG_BITMAP);
        }
    }

    @Override
    protected void initUI() {
        if (bitmap != null) {
            imIcon.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (text != null) {
            setText(text);
        }
    }

    public void setText(String text) {
        this.text = text;
        if (isAdded()) {
            tvText.setText(text);
        }
    }


    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_bind;
    }

}
