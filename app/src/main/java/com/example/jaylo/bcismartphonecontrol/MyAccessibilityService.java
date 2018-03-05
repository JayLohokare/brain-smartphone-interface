package com.example.jaylo.bcismartphonecontrol;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.os.IBinder;

import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
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

public class MyAccessibilityService extends AccessibilityService implements View.OnTouchListener, View.OnClickListener {


    private static Map<Integer, AccessibilityNodeInfo> buttonsMap;
    private static int buttonId;

    private View topLeftView;

    private Button overlayedButton;
    private float offsetX;
    private float offsetY;
    private int originalXPos;
    private int originalYPos;
    private boolean moving;
    private WindowManager wm;


    @Override
    public void onServiceConnected()
    {
        Log.d("Service notification", "Accessibility service started and connected");
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
        /*wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        overlayedButton = new Button(this);
        overlayedButton.setText("Overlay button");
        overlayedButton.setOnTouchListener(this);
        overlayedButton.setAlpha(1.0f);
        overlayedButton.setBackgroundColor(0x55fe4444);
        overlayedButton.setOnClickListener(this);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.LEFT | Gravity.CENTER;
        params.x = 0;
        params.y = 0;
        wm.addView(overlayedButton, params);

        topLeftView = new View(this);
        WindowManager.LayoutParams topLeftParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);
        topLeftParams.gravity = Gravity.LEFT | Gravity.TOP;
        topLeftParams.x = 0;
        topLeftParams.y = 0;
        topLeftParams.width = 0;
        topLeftParams.height = 0;
        wm.addView(topLeftView, topLeftParams);*/
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        Log.d("Event", "Recorded a new event");

        findTextAndClick(this);

        AccessibilityNodeInfo source = event.getSource();
        if (source == null) {
            return;
        }
        Log.d("Source of event:", source.toString());
    }


    @Override
    public void onInterrupt() {

    }


    public static void findTextAndClick(AccessibilityService accessibilityService) {

        buttonsMap = new HashMap<>();

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
            if(current.isClickable() && current.getClassName().toString().indexOf("Button") != -1) {
                Log.d("Found Button", current.toString());
                buttonsMap.put(buttonId++, current);

                Rect nodePosition = new Rect();
                current.getBoundsInScreen(nodePosition);
                Log.d("Button found","Button window" + nodePosition.toString());

            }
            int childrenCount = current.getChildCount();
            for(int i = 0; i < childrenCount; i++) {
                stack.push(current.getChild(i));
            }

        }

        for(int i : buttonsMap.keySet()) {


            Log.d("Map Contents", "key = " + i + ", value = " + buttonsMap.get(i).toString());

        }
        Log.d("Map Contents", buttonsMap.toString());

    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, "Overlay button click event", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}