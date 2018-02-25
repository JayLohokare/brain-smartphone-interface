package com.example.jaylo.bcismartphonecontrol;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityRecord;
import android.widget.Button;
import android.widget.Toast;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MyAccessibilityService extends AccessibilityService {

    private static Map<Integer, AccessibilityNodeInfo> buttonsMap = new HashMap<>();
    private static int buttonId;

    @Override
    public void onServiceConnected()
    {
        Log.d("Connected UAU", "***** onServiceConnected");


        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.notificationTimeout = 100;
        info.feedbackType = AccessibilityEvent.TYPES_ALL_MASK;
        setServiceInfo(info);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        Intent serviceIntent = new Intent(this.getApplicationContext(), MyAccessibilityService.class);
        startService(serviceIntent);



    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Craeted", "Yay");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        Log.d("ERROORRR", "DATAAA");

        findTextAndClick(this);

        AccessibilityNodeInfo source = event.getSource();
        if (source == null) {
            return;
        }
        Log.d("Source", source.toString());
    }


    @Override
    public void onInterrupt() {

    }


    public static void findTextAndClick(AccessibilityService accessibilityService) {

        AccessibilityNodeInfo accessibilityNodeInfo = accessibilityService.getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return;
        }

        int actions = accessibilityNodeInfo.getActions();
        Log.d("Actions count",Integer.toString(actions));
        Log.d("Action List", accessibilityNodeInfo.getActionList().toString() );

        Log.d("Finding Clickable items", "Here they are");

        List<AccessibilityNodeInfo> buttons = accessibilityNodeInfo.findAccessibilityNodeInfosByText("Button");
        for (int i = 0; i < buttons.size(); i++) {
            Log.d("Button: ", i + buttons.get(i).toString());
        }

        /*getClassName*/

       /* accessibilityNodeInfo.getChild() */

     /*   accessibilityNodeInfo.getChildCount()*/

        Deque<AccessibilityNodeInfo> stack = new LinkedList<>();

        stack.push(accessibilityNodeInfo);
        while (!stack.isEmpty()) {
            AccessibilityNodeInfo current = stack.pop();
            if(current == null) {
                continue;
            }
            Log.d("Class is:", current.getClassName().toString());
            if(current.getClassName().toString().indexOf("Button") != -1) {
                Log.d("Found Button", current.toString());
                buttonsMap.put(buttonId++, current);
            }
            int childrenCount = current.getChildCount();
            for(int i = 0; i < childrenCount; i++) {
                stack.push(current.getChild(i));
            }

        }

        for(int i : buttonsMap.keySet()) {
            Log.d("Map Contents", "key = " + i + ", value = " + buttonsMap.get(i).toString());
        }
        //Log.d("Map Contents", buttonsMap.toString());

    }
}