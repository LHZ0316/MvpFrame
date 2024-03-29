package com.lhz.android.baseUtils.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

/**
 * author : lhz
 * desc   : 自定义验证码倒计时
 */
public class SendButton extends AppCompatTextView {

    private SendClickListener sendClickListener;
    private String normalText = "发送验证码", waitText = "%ds 后重发";
    private int countdown = 0, waitTime = 60;
    private boolean isAttached, isWait;

    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            countdown -= 1;
            if (countdown <= 0) {
                setClickable(true);
                setText(normalText);
                isWait = false;
            } else {
                getHandler().postDelayed(this, 1000);
                setCountDownText(countdown);
            }
        }
    };

    public SendButton(Context context) {
        this(context, null);
    }

    public SendButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SendButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void setCountDownText(int time) {
        setText(String.format(waitText, time));
    }

    @Override
    public void setOnClickListener(OnClickListener listener) {
        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isWait) {
                    if (sendClickListener != null) {
                        if (isMobile(sendClickListener.onGetVerifyPhone())) {
                            setClickable(false);
                            countdown = waitTime;
                            getHandler().post(runnable);
                            sendClickListener.onSendVerificationCode();
                            isWait = true;
                        } else {
                            Toast.makeText(getContext(), "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isAttached) {
            isAttached = true;
            setText(normalText);
        }
    }

    public static boolean isMobile(String mobileNums) {
        /**
         * 判断字符串是否符合手机号码格式
         * 移动号段: 134,135,136,137,138,139,147,150,151,152,157,158,159,170,178,182,183,184,187,188
         * 联通号段: 130,131,132,145,155,156,170,171,175,176,185,186
         * 电信号段: 133,149,153,170,173,177,180,181,189
         */
        String telRegex = "^((13[0-9])|(14[5,7,9])|(15[^4])|(18[0-9])|(17[0,1,3,5,6,7,8]))\\d{8}$";
        if (TextUtils.isEmpty(mobileNums)) {
            return false;
        } else {
            return mobileNums.matches(telRegex);
        }
    }

    public void setOnSendClickListener(SendClickListener listener) {
        //设置点击监听，否则点击不会触发事件
        setOnClickListener(null);
        this.sendClickListener = listener;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (isAttached) {
            isAttached = false;
            isWait = false;
            getHandler().removeCallbacks(runnable);
        }
    }

    public interface SendClickListener {
        String onGetVerifyPhone();

        void onSendVerificationCode();
    }
}
