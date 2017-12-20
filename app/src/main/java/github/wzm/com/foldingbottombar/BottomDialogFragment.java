package github.wzm.com.foldingbottombar;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

/**
 * Okline(Hangzhou)co,Ltd<br/>
 * Author: wangzhongming<br/>
 * Email:  wangzhongming@okline.cn</br>
 * Date :  2017/12/13 14:55 </br>
 * Summary:
 */

public class BottomDialogFragment  extends DialogFragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.bottom_dialog_fragment, null);

        return inflate;
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            Window window = dialog.getWindow();
            //指定显示位置
            dialog.getWindow().setGravity(Gravity.BOTTOM);
            //指定显示大小
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //显示消失动画
           // window.setWindowAnimations(R.style.animate_dialog);
            //设置背景透明
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //设置点击外部可以取消对话框
            setCancelable(true);
        }



    }

}
