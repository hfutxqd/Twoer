package xyz.imxqd.advancetoast;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by imxqd on 17-4-9.
 */

public class CompleteToast extends Toast {

    public CompleteToast(Context context) {
        super(context);

    }

    public static CompleteToast make(Context context, int text, int duration) {
        CompleteToast result = new CompleteToast(context);
        LayoutInflater inflate = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.ad_toast_layout, null);
        TextView tv = (TextView) v.findViewById(R.id.ad_toast_message);
        tv.setText(text);
        ImageView iv = (ImageView) v.findViewById(R.id.ad_toast_icon);
        iv.setImageResource(R.drawable.ic_done_white_36dp);
        result.setView(v);
        //获取屏幕高度
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        //Toast的Y坐标是屏幕高度的1/3，不会出现不适配的问题
        result.setGravity(Gravity.TOP, 0, height / 3);
        result.setDuration(duration);
        return result;
    }
}
