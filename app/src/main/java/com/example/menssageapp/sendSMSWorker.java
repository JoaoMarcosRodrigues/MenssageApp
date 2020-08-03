package com.example.menssageapp;

import android.content.Context;
import android.os.Build;
import android.telephony.SmsManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


public class sendSMSWorker extends Worker {

    public sendSMSWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @NonNull
    @Override
    public Result doWork() {
        String message = getInputData().getString("message");
        String[] numbers = getInputData().getStringArray("contacts");

        try {
            if (numbers.length != 0) {
                for (int j = 0; j < numbers.length; j++) {
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(numbers[j], null, message, null, null);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.success();
    }
}
