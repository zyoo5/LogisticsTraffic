package com.bt.zhangzy.logisticstraffic.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.d.R;

/**
 * 对话框整理类，对一些通用设置做封装
 * Created by ZhangZy on 2015/6/15.
 */
public class BaseDialog extends Dialog implements View.OnClickListener {
    public interface DialogClickListener {
        void onClick(BaseDialog dialog, View view);
    }

    /**
     * @param context 需要activity 用于计算屏幕大小
     */
    public BaseDialog(Activity context) {
        super(context, R.style.app_dialog_style);
        setContentView(R.layout.base_dialog);
        /*
         * 将对话框的大小按屏幕大小的百分比设置
         */
        Window dialogWindow = getWindow();
        WindowManager m = context.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        //高度不设置，由布局自适应
//        p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65
        dialogWindow.setAttributes(p);
    }

    protected BaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    /**
     * 设置dialog的现实内容
     *
     * @param layoutID 布局文件的id值
     * @return
     */
    public BaseDialog setView(int layoutID) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.dialog_content_ly);
        View view = LayoutInflater.from(getContext()).inflate(layoutID, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayout.addView(view);
        return this;
    }

    /**
     * 设置dialog中的onClick事件 默认对dialog的隐藏做处理
     *
     * @param id       view的id
     * @param listener 监听事件  可为null
     * @return baseDialog
     */
    public BaseDialog setOnClickListener(int id, View.OnClickListener listener) {
        return setOnClickObj(id, listener);
    }

    /**
     * 设置dialog中的onClick事件
     *
     * @param id       view的id
     * @param listener 监听事件  可为null 回传dialog对象 view对象
     * @return baseDialog
     */
    public BaseDialog setOnClickListenerForDialog(int id, DialogClickListener listener) {
        return setOnClickObj(id, listener);
    }


    private BaseDialog setOnClickObj(int id, Object listener) {
        View view = findViewById(id);
        setOnClickObj(view, listener);
        return this;
    }

    public void setOnClickObj(View view, Object listener) {
        if (view != null) {
            view.setOnClickListener(this);
            view.setTag(listener);
        }
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            if (v.getTag() != null) {
                if (v.getTag() instanceof View.OnClickListener) {
                    View.OnClickListener listener = (View.OnClickListener) v.getTag();
                    listener.onClick(v);
                } else if (v.getTag() instanceof DialogClickListener) {
                    DialogClickListener listener = (DialogClickListener) v.getTag();
                    listener.onClick(this, v);
                    return;
                }
            }
            dismiss();
        }
    }

    /**
     * 设置 dialog 中的TextView内容
     *
     * @param id
     * @param num
     * @return
     */
    public BaseDialog setTextView(int id, String num) {
        TextView textView = (TextView) findViewById(id);
        textView.setText(num);
        return this;
    }










}
