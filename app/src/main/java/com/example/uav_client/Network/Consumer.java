package com.example.uav_client.Network;

import com.example.uav_client.Data.Common.RequestBuildUtil;
import com.example.uav_client.Data.Main.DataListSource;
import com.example.uav_client.Utils.AppExecutors;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class Consumer {
    private static List<Consumer> consumerPool = new ArrayList<>();

    public int getRequestCode() {
        return requestCode;
    }

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

    public static void enqueue(Consumer consumer) {
        if (consumerPool.contains(consumer)) {
            consumerPool.set(consumerPool.indexOf(consumer), consumer);
        } else {
            consumerPool.add(consumer);
        }
    }

    public static Consumer getConsumer(int requestCode) {
        for (Consumer consumer : consumerPool) {
            if (consumer.requestCode == requestCode) {
                return consumer;
            }
        }
        return null;
    }

    public static void back(final byte[] bytes) {
        int requestCode = RequestBuildUtil.unPackrequestCode(bytes,8);
        final Consumer consumer = getConsumer(requestCode-1);
        if (consumer != null) {
            appExecutors.mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    consumer.observer.dataGet(bytes);
                }
            });
            if (requestCode != 2) {
                consumerPool.remove(consumer);
            }
        } else {
            consumer.observer.error();
        }
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
}
