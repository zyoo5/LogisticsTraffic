package com.bt.zhangzy.logisticstraffic.view;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.tools.ContextTools;

/**
 * Created by ZhangZy on 2016-3-29.
 */
public class CallPhoneDialog extends BaseDialog {
    Context context;
    String phoneNum;
    int companyId;

    public CallPhoneDialog(Activity context) {
        super(context);
        this.context = context;
        setView(R.layout.dialog_call_phone);
//        confirmBt = (Button) findViewById(R.id.dialog_btn_yes);
        findViewById(R.id.dialog_btn_no).setOnClickListener(this);
        setTextView(R.id.dialog_phone_info_tx, String.format(context.getString(R.string.call_phone_tx), context.getString(R.string.app_name)));
    }

    public CallPhoneDialog(Activity context, final String phoneNum1, final String phoneNum2) {
        super(context);
        this.context = context;
        setView(R.layout.dialog_call_phone_list);
        Button button1 = (Button) findViewById(R.id.dialog_phone_num_1);
        Button button2 = (Button) findViewById(R.id.dialog_phone_num_2);
//        button1.setOnClickListener(this);
//        button2.setOnClickListener(this);
        button1.setText(phoneNum1);
        button2.setText(phoneNum2);
        setOnClickListener(R.id.dialog_phone_num_1, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContextTools.CallPhone(CallPhoneDialog.this.context, phoneNum1);
            }
        });
        setOnClickListener(R.id.dialog_phone_num_2, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContextTools.CallPhone(CallPhoneDialog.this.context, phoneNum2);
            }
        });
    }

    public CallPhoneDialog setCompanyId(int companyId) {
        this.companyId = companyId;
        return this;
    }

    public CallPhoneDialog setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
        setTextView(R.id.dialog_phone_num, phoneNum);
        setOnClickListener(R.id.dialog_btn_yes, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.dialog_btn_yes) {
//                    getApp().callPhone(phoneNum);
                    ContextTools.CallPhone(context, CallPhoneDialog.this.phoneNum);
                    if (companyId > 0) {
                        requestUploadCallNumber(companyId);
                    }
                }
            }
        });
        return this;
    }

    public CallPhoneDialog setInfoMessage(String msg) {
        setTextView(R.id.dialog_phone_info_tx, msg);
        return this;
    }

    private void requestUploadCallNumber(int companyId) {
        HttpHelper.getInstance().get(AppURL.GetUploadCallNum, new String[]{"companyId=" + companyId}, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {

            }

            @Override
            public void onFailed(String str) {

            }
        });
    }

}
