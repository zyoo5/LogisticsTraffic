package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.JsonUser;
import com.bt.zhangzy.network.entity.ResponseLogin;
import com.bt.zhangzy.tools.Tools;

/**
 * Created by ZhangZy on 2015/6/9.
 */
public class LoginActivity extends BaseActivity {

    EditText username;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.activity_login);
        username = (EditText) findViewById(R.id.login_username_ed);
//        if (!TextUtils.isEmpty(User.getInstance().getUserName())) {
//            username.setText(User.getInstance().getUserName());
//        }
        if (!TextUtils.isEmpty(User.getInstance().getPhoneNum())) {
            username.setText(User.getInstance().getPhoneNum());
        }

        if (AppParams.DEBUG && User.getInstance().isSave()) {
            setTextView(R.id.login_password_ed, User.getInstance().getPassword());
        }

        password = (EditText) findViewById(R.id.login_password_ed);
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //密码每次点击都重新输入
                    password.getText().clear();
                }
            }
        });
        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                    case EditorInfo.IME_ACTION_GO:
                        onClick_Login(v);

                        break;
                }
                return false;
            }
        });

        CheckBox checkBox = (CheckBox) findViewById(R.id.login_remember_ck);
        checkBox.setChecked(User.getInstance().isSave());
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                User.getInstance().setIsSave(isChecked);
                if (isChecked) {
                    if (!TextUtils.isEmpty(username.getText()))
                        User.getInstance().setPhoneNum(username.getText().toString());
                }
            }
        });
    }


    //跳转到注册页面
    public void onClick_Register(View view) {
        startActivity(RegisterActivity.class);
        finish();
    }

    public void onClick_Forget(View view) {
        startActivity(ForgetActivity.class);
        finish();
    }

    //进行登录操作
    public void onClick_Login(View view) {
//        loginSusses();
//        EditText username = (EditText) findViewById(R.id.login_username_ed);
        EditText password = (EditText) findViewById(R.id.login_password_ed);
        String nameStr, passwordStr;
        nameStr = username.getText().toString();
        passwordStr = password.getText().toString();
        if (nameStr.isEmpty() || passwordStr.isEmpty()) {
            showToast("请填写用户名和密码");
            return;
        }

        if (!Tools.IsPhoneNum(nameStr)) {
            showToast("用户名格式错误");
            return;
        }

        if (User.getInstance().isSave() && passwordStr.equals(User.getInstance().getPassword())) {

        } else {
            passwordStr = Tools.MD5(passwordStr);
        }
        request_Login(nameStr, passwordStr);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onClick_Back(null);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick_Back(View view) {
//        super.onClick_Back(view);
        startActivity(HomeActivity.class);
        finish();
    }

    private void loginSusses() {
//        Toast.makeText(this, "登录成功", Toast.LENGTH_LONG).show();
//        User.getInstance().setLogin(true);
//        User.getInstance().setUserType(Type.EnterpriseType);
//        startActivity(new Intent(LoginActivity.this,UserActivity.class));
        //登录成功后保存一下信息；
        getApp().saveUser();
        getApp().setAliasAndTag();

        Bundle bundle = getIntent().getExtras();
        startActivity(HomeActivity.class, bundle);
        finish();

    }

    // 发起登录请求
    private void request_Login(String username, String password) {

        JsonUser user = new JsonUser();
//        user.setName(username);
        user.setPhoneNumber(username);
        user.setPassword(password);
        if (User.getInstance().isSave()) {
            User.getInstance().setPassword(password);
        }

        HttpHelper.getInstance().post(AppURL.Login, user, new JsonCallback() {
            @Override
            public void onFailed(String str) {
                showToast("用户登录失败：" + str);
            }

            @Override
            public void onSuccess(String msg, String jsonstr) {
                if (TextUtils.isEmpty(jsonstr)) {
                    showToast("用户登录失败：" + msg);
                    return;
                }
                ResponseLogin json = ParseJson_Object(jsonstr, ResponseLogin.class);
                User.getInstance().setLoginResponse(json);
//                JsonUser jsonUser = ParseJson_Object(jsonstr, JsonUser.class);
                if (AppParams.DRIVER_APP) {
                    if (User.getInstance().getUserType() != Type.DriverType) {
                        showToast("用户类型错误");
                        return;
                    }
                } else {
                    if (User.getInstance().getUserType() == Type.DriverType) {
                        showToast("用户类型错误");
                        return;
                    }
                }
                showToast("用户登录成功");

                loginSusses();
            }

        });

    }


}
