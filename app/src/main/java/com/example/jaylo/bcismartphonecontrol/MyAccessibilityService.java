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
import android.support.constraint.solver.widgets.Rectangle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityRecord;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MyAccessibilityService extends AccessibilityService implements View.OnTouchListener, View.OnClickListener {





    private View topLeftView;

    //private Button overlayedButton,overlayedButton2,overlayedButton3;
    private float offsetX;
    private float offsetY;
    private int originalXPos;
    private int originalYPos;
    private boolean moving;
    private WindowManager wm;
    private RelativeLayout rl;
    RelativeLayout.LayoutParams param;

    @Override
    public void onServiceConnected() {
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
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        /*
        overlayedButton = new Button(this);
        overlayedButton.setText("1");
        overlayedButton.setOnTouchListener(this);
        overlayedButton.setAlpha(1.0f);
        overlayedButton.setBackgroundColor(0x55fe4444);
        overlayedButton.setOnClickListener(this);

        overlayedButton2 = new Button(this);
        overlayedButton2.setText("2");
        overlayedButton2.setOnTouchListener(this);
        overlayedButton2.setAlpha(1.0f);
        overlayedButton2.setBackgroundColor(0x55fe4444);
        overlayedButton2.setOnClickListener(this);

        overlayedButton3 = new Button(this);
        overlayedButton3.setText("3");
        overlayedButton3.setOnTouchListener(this);
        overlayedButton3.setAlpha(1.0f);
        overlayedButton3.setBackgroundColor(0x55fe4444);
        overlayedButton3.setOnClickListener(this);
*/
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.relative_layout, null);
        rl = (RelativeLayout) view.findViewById(R.id.relative_layout);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);
        //params.gravity = Gravity.LEFT | Gravity.CENTER;
        params.x = 0;
        params.y = 0;
        wm.addView(rl, params);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        //TODO Need to write the logic for cleaning previous window contents

        Log.d("Event", "Recorded a new event");
        Map<Integer,AccessibilityNodeInfo> map;
        map = findTextAndClick(this);
        Rect r;
        AccessibilityNodeInfo a;
        int width=300,height=300;
        TextView tv;
        /*
        Button[] bArray = {overlayedButton,overlayedButton2,overlayedButton3};

        for(int i=0;i<3;i++) {
            param = new RelativeLayout.LayoutParams( width, height);
            param.leftMargin = i*300;
            param.topMargin = 0;
            rl.addView(bArray[i], param);
        }
        */

        for (int i : map.keySet()) {
            tv = new TextView(this);
            tv.setText(""+i+"");
            tv.setAlpha(1.0f);
            tv.setBackgroundColor(0x55fe4444);
            a = map.get(i);
            r = new Rect();
            a.getBoundsInScreen(r);
            r.sort();
            param = new RelativeLayout.LayoutParams( r.width(), r.height());
            param.leftMargin = r.left;
            param.topMargin = r.top;
            // I think these margins are redundant
            //param.rightMargin = r.right;
            //param.bottomMargin = r.bottom;
            rl.addView(tv,param);
        }

        AccessibilityNodeInfo source = event.getSource();
        if (source == null) {
            return;
        }
    }


    @Override
    public void onInterrupt() {

    }


    public static Map<Integer, AccessibilityNodeInfo> findTextAndClick(AccessibilityService accessibilityService) {
        Map<Integer, AccessibilityNodeInfo> buttonsMap = new HashMap<>();
        AccessibilityNodeInfo accessibilityNodeInfo = accessibilityService.getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return buttonsMap;
        }

        //int actions = accessibilityNodeInfo.getActions();
        //Log.d("Actions count", Integer.toString(actions));
        //Log.d("Action List", accessibilityNodeInfo.getActionList().toString());

        //Log.d("Finding Clickable items", "Here they are");

        /*
        List<AccessibilityNodeInfo> buttons = accessibilityNodeInfo.findAccessibilityNodeInfosByText("Button");
        for (int i = 0; i < buttons.size(); i++) {
            Log.d("Button: ", i + buttons.get(i).toString());
        }*/

        Deque<AccessibilityNodeInfo> stack = new LinkedList<>();

        int buttonId=0;

        stack.push(accessibilityNodeInfo);


        while (!stack.isEmpty()) {
            AccessibilityNodeInfo current = stack.pop();
            if (current == null) {
                continue;
            }
            Log.d("Class is:", current.getClassName().toString());
            //if (current.getClassName().toString().contains("Button")) {
            if (current.isClickable()) {
                    //Log.d("Found Button", current.toString());
                    buttonsMap.put(buttonId++, current);
            }

            int childrenCount = current.getChildCount();
            for (int i = 0; i < childrenCount; i++) {
                stack.push(current.getChild(i));
            }
        }

        for (int i : buttonsMap.keySet()) {
            Log.d("Map Contents", "key = " + i + ", value = " + buttonsMap.get(i).getClassName());
        }

        return buttonsMap;
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