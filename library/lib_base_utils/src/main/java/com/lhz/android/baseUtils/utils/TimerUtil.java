package com.lhz.android.baseUtils.utils;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 时间刷新器（计时器）
 *
 * @use TimerUtil.getInstance().addTimeRefreshListener(...);
 * 在使用TimeRefresher的Context被销毁前TimeRefresher.getInstance().removeTimeRefreshListener(...);
 * 在应用退出前TimeRefresher.getInstance().finish();
 */
public class TimerUtil {
    private static final String TAG = "TimerUtil";

    /**
     * 回调接口
     */
    public interface OnTimeRefreshListener {

        void onTimerStart();

        void onTimerRefresh();

        void onTimerStop();
    }

    private Map<String, TimeHolder> refreshMap;

    private TimerUtil() {
        refreshMap = new HashMap<String, TimeHolder>();
    }

    private static TimerUtil timerUtil;

    public static TimerUtil getInstance() {
        if (timerUtil == null) {
            timerUtil = new TimerUtil();
        }

        return timerUtil;
    }

    public boolean isContain(String tag) {
        return tag != null && refreshMap != null && refreshMap.containsKey(tag);
    }

    public synchronized void addTimeRefreshListener(String tag, OnTimeRefreshListener listener) {
        addTimeRefreshListener(tag, 1000, listener);
    }

    public synchronized void addTimeRefreshListener(String tag, long duration, OnTimeRefreshListener listener) {
        if (duration > 0 && tag != null && listener != null) {
            Log.d(TAG, "\n addTimeRefreshListener  duration > 0 && tag = " + tag + " && listener != null >>");
            if (refreshMap.containsKey(tag) && refreshMap.get(tag) != null) {
                refreshMap.get(tag).startTimer();
                Log.d(TAG, "refreshMap.containsKey(tag) && refreshMap.get(tag) != null >>  refreshMap.get(tag).startTimer();");
            } else {
                refreshMap.put(tag, new TimeHolder(duration, listener));
                Log.d(TAG, "addTimeRefreshListener  added tag = ");
            }
        }
        Log.d(TAG, "addTimeRefreshListener  refreshMap.size() = " + refreshMap.size() + "\n");
    }

    /**
     * @param tag
     */
    public synchronized void startTimeRefreshListener(String tag) {
        TimeHolder holder = refreshMap.get("" + tag);
        if (holder != null) {
            holder.startTimer();
            Log.d(TAG, "startTimeRefreshListener started tag = " + tag);
        }
    }

    /**
     * @param tag
     */
    public synchronized void stopTimeRefreshListener(String tag) {
        TimeHolder holder = refreshMap.get("" + tag);
        if (holder != null) {
            holder.stopTimer();
            Log.d(TAG, "stopTimeRefreshListener stopped tag = " + tag);
        }
    }

    /**
     * @param tag
     */
    public synchronized void removeTimeRefreshListener(String tag) {
        TimeHolder holder = refreshMap.get("" + tag);
        if (holder != null) {
            holder.stopTimer();
            holder = null;
        }
        refreshMap.remove("" + tag);
        Log.d(TAG, "removeTimeRefreshListener removed tag = " + tag);
    }

    /**
     * 完全结束Timer进程
     */
    public void finish() {
        timerUtil = null;
        if (refreshMap == null || refreshMap.size() <= 0) {
            Log.d(TAG, "finish  refreshMap == null || refreshMap.size() <= 0 >> return;");
            return;
        }
        Set<String> tagSet = refreshMap.keySet();
        if (tagSet != null) {
            for (String tag : tagSet) {
                removeTimeRefreshListener(tag);
            }
        }
        refreshMap = null;
        Log.d(TAG, "\n finish  finished \n");
    }


    /**
     * 计时器holder
     */
    @SuppressLint("HandlerLeak")
    public class TimeHolder {

        long duration = 1000;
        OnTimeRefreshListener listener;

        public TimeHolder(OnTimeRefreshListener listener) {
            this(1000, listener);
        }

        public TimeHolder(long duration, OnTimeRefreshListener listener) {
            this.duration = duration;
            this.listener = listener;
            startTimer();
        }


        Timer timer;
        TimerTask task;

        /**
         * 启动计时器
         */
        public void startTimer() {
            if (listener == null) {
                Log.e(TAG, "startTimer  listener == null >> return;");
                return;
            }

            //		Log.w(TAG, "startTimer<<<<<<<< " + DateUtil.getTime(System.currentTimeMillis()));
            stopTimer();

            listener.onTimerStart();
            if (timer == null) {
                timer = new Timer(true);
            }
            if (task == null) {
                task = new TimerTask() {
                    public void run() {
                        handler.sendEmptyMessage(0);
                    }
                };
            }
            timer.schedule(task, 0, duration); //延时0ms后执行，1000ms执行一次
            //		Log.w(TAG, "startTimer >>>>>>> " + DateUtil.getTime(System.currentTimeMillis()));
        }

        /**
         * 停止计时器
         */
        public void stopTimer() {
            if (listener != null) {
                listener.onTimerStop();
            }
            //		Log.w(TAG, "stopTimer<<<<<<<< " + DateUtil.getTime(System.currentTimeMillis()));
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            if (task != null) {
                task.cancel();
                task = null;
            }
            //		Log.w(TAG, "stopTimer>>>>>>> " + DateUtil.getTime(System.currentTimeMillis()));
        }

        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                listener.onTimerRefresh();
            }
        };

    }

}
