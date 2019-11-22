package com.example.uav_client.Network;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.uav_client.Application.SApplication;
import com.example.uav_client.Application.SysApplication;
import com.example.uav_client.Data.Common.ReceiveBody;
import com.example.uav_client.Data.Common.RequestBuildUtil;
import com.example.uav_client.Data.Main.DataListSource;
import com.example.uav_client.R;
import com.example.uav_client.Utils.AppExecutors;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;

public class Consumer {
    private static List<Consumer> consumerPool = new ArrayList<>();
    private static DataListSource.getDataCallBack observerMap = null;
    private static DataListSource.getDataCallBack observerMaponLine = null;

    int requestCode;

    DataListSource.getDataCallBack observer;
    static AppExecutors appExecutors;

    public Consumer(int requestCode, DataListSource.getDataCallBack observer, AppExecutors appExecutors) {
        this.requestCode = requestCode;
        this.observer = observer;
        if (this.appExecutors == null) {
            this.appExecutors = appExecutors;
        }
        enqueue(this);
    }

    public int getRequestCode() {
        return requestCode;
    }

    public static void enqueue(Consumer consumer) {
        if (consumerPool.contains(consumer)) {
            consumerPool.set(consumerPool.indexOf(consumer), consumer);
        } else {
            consumerPool.add(consumer);
        }
    }

    public static void addObserverMap(DataListSource.getDataCallBack observerMap) {
        Consumer.observerMap = observerMap;
    }

    public static void addUnLineObserverMap(DataListSource.getDataCallBack observerMap) {
        Consumer.observerMaponLine = observerMap;
    }

    public static Consumer getConsumer(int requestCode) {
        for (Consumer consumer : consumerPool) {
            if (consumer.requestCode == requestCode) {
                return consumer;
            }
        }
        return null;
    }

    static byte[] temSave = new byte[0];

    public static void back(final byte[] bytes, String s) {
        if (RequestBuildUtil.fourBytesToInt(RequestBuildUtil.nigetPartByteArray(bytes, 0, 3)) != 0xEEEEEEEE) {
            if (temSave.length != 0) {
                BackToMain(RequestBuildUtil.mergeData(temSave, bytes), 14);
                return;
            }
        }
        int requestCode = RequestBuildUtil.unPackrequestCode(bytes, 8);
        if (requestCode == 1) {
            SysApplication.setAlarmArea(ReceiveBody.getAlarm(ReceiveBody.initialParse(RequestBuildUtil.unPackString(bytes), ";")));
            if (observerMap != null) {
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        byte[] b = new byte[0];
                        observerMap.dataGet(b);
                    }
                });
            }
        } else if (requestCode == 2) {
            if (observerMap != null) {
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        observerMap.dataGet(bytes);
                    }
                });
            }

            final List<String> lis = ReceiveBody.initialParse(RequestBuildUtil.unPackString(bytes), "|");
            if (Integer.parseInt(lis.get(lis.size() - 1)) == 1) {
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        popWindow(SApplication.activity1, lis);
                    }
                });
            }
            Log.d("datacome1", s);

        } else if (requestCode == RequestBuildUtil.getSEARCH_UAV_RESULT()) {
            if (temSave.length == 0) {
                if (RequestBuildUtil.getDataLength(bytes) > (bytes.length - 4)) {
                    temSave = new byte[bytes.length];
                    System.arraycopy(bytes, 0, temSave, 0, bytes.length);
                    temSave = bytes;
                } else {
                    BackToMain(bytes, requestCode);
                }
            } else {

            }
        } else {
            BackToMain(bytes, requestCode);
        }
    }

    private static void BackToMain(final byte[] bytes, int requestCode) {
        final Consumer consumer = getConsumer(requestCode - 1);
        if (consumer != null) {
            appExecutors.mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    consumer.observer.dataGet(bytes);
                }
            });
            consumerPool.remove(consumer);
        } else {
//            consumer.observer.error();
        }
    }

    static View popupView;
    static PopupWindow newWindow;
    static TextView textView;

    public static void popWindow(Activity activity, List<String> list) {
        if (!newWindow.isShowing()) {
            popupView = activity.getLayoutInflater().inflate(R.layout.alarm_layout, null);
            popupView.setPadding(50, 0, 50, 0);
            newWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            newWindow.setWidth((int) activity.getResources().getDimension(R.dimen.dp_280));
            newWindow.setAnimationStyle(R.style.popup_window_anim);
            newWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
            newWindow.setFocusable(true);
            newWindow.setOutsideTouchable(true);
            newWindow.update();
            newWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER_VERTICAL, 0, 0);
            textView = popupView.findViewById(R.id.alarm_list);
        }
        if (textView != null) {
            textView.setText(list.toString());
        }
        MediaPlayer mediaPlayer = MediaPlayer.create(activity, R.raw.music);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Consumer) {
            if (this.requestCode == ((Consumer) obj).getRequestCode()) {
                return true;
            }
        }
        return false;
    }

    public static void UnonLine(final String s) {
        appExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date data = null;
                if (observerMaponLine != null) {
                    List<String> list = ReceiveBody.initialParse(s, ";");
                    for (final String s1 : list) {
                        if (data == null) {

                            List<String> list1 = ReceiveBody.initialParse(s1, "|");
                            try {
                                data = df.parse(list1.get(5));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            appExecutors.mainThread().execute(new Runnable() {
                                @Override
                                public void run() {
                                    observerMaponLine.dataGet(s1.getBytes());
                                }
                            });
                        } else {
                            List<String> list1 = ReceiveBody.initialParse(s1, "|");
                            try {
                                Date d = df.parse(list1.get(5));
                                long diff = d.getTime() - data.getTime();
                                data = d;
                                Thread.sleep(diff);
                                appExecutors.mainThread().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        observerMaponLine.dataGet(s1.getBytes());
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("xiao","时间解析错误");
                            }
                        }
                    }
                }
            }
        });
    }

}
