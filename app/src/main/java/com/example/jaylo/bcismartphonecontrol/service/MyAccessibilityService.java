package com.example.jaylo.bcismartphonecontrol.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jaylo.bcismartphonecontrol.R;
import com.example.jaylo.bcismartphonecontrol.bcibridge.AndroidBCIConnector;
import com.example.jaylo.bcismartphonecontrol.bcibridge.SampleGattAttributes;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

public class MyAccessibilityService extends AccessibilityService implements View.OnTouchListener, View.OnClickListener {


    private static Map<Integer, AccessibilityNodeInfo> buttonsMap;

    private View topLeftView;

    //private Button overlayedButton,overlayedButton2,overlayedButton3;
    private float offsetX;
    private float offsetY;
    private int originalXPos;
    private int originalYPos;
    private boolean moving;
    private WindowManager wm;
    private RelativeLayout rl;
    private RelativeLayout.LayoutParams param;
    private LayoutInflater inflater;
    private View view;




    public static Map<Integer, AccessibilityNodeInfo> getButtonsMap() {
        return buttonsMap;
    }

    public static Map<Integer, AccessibilityNodeInfo> findTextAndClick(AccessibilityService accessibilityService) {
        buttonsMap = new HashMap<>();
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

        int buttonId = 0;

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

        Log.d("Map Contents", "------------------------------------------------------------------------");
        for (int i : buttonsMap.keySet()) {
            Log.d("Map Contents", "key = " + i + ", value = " + buttonsMap.get(i).getClassName());
        }
        Log.d("Map Contents", "------------------------------------------------------------------------");


        return buttonsMap;
    }


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

        Intent serviceIntent = new Intent(getApplicationContext(), MyAccessibilityService.class);
        startService(serviceIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.relative_layout, null);
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
        /*
        int i=0;
        int childCount = rl.getChildCount();
        View childList[] = new View[childCount];
        while (i<childCount){
            childList[i]=rl.getChildAt(i);
        }

        for(View v : childList){
            rl.remo
        }
        */
        rl.removeAllViews();

        Log.d("Event", "Recorded a new event");
        buttonsMap = findTextAndClick(this);
        Rect r;
        AccessibilityNodeInfo a;
        int width = 300, height = 300;
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


        for (int i : buttonsMap.keySet()) {
            tv = new TextView(this);
            tv.setText("" + i + "");
            tv.setAlpha(1.0f);
            tv.setBackgroundColor(0x55fe4444);
            a = buttonsMap.get(i);
            r = new Rect();
            a.getBoundsInScreen(r);
            r.sort();
            param = new RelativeLayout.LayoutParams(r.width(), r.height());
            param.leftMargin = r.left;
            param.topMargin = r.top;
            // I think these margins are redundant
            //param.rightMargin = r.right;
            //param.bottomMargin = r.bottom;
            rl.addView(tv, param);
        }

        AndroidBCIConnector.sendOverlayNumber(5);

        AccessibilityNodeInfo source = event.getSource();
        if (source == null) {
            return;
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, "Overlay button click event", Toast.LENGTH_SHORT).show();
//        v.getOverlay().getClass().
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        int xpos = (int) e.getX();
        int ypos = (int) e.getY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("DEBUG", "On touch (down)" + String.valueOf(xpos) + String.valueOf(ypos));
            case MotionEvent.ACTION_UP:
                Log.d("DEBUG", "On touch (up)" + String.valueOf(xpos) + String.valueOf(ypos));
            case MotionEvent.ACTION_MOVE:
                Log.d("DEBUG", "On touch (move)" + String.valueOf(xpos) + String.valueOf(ypos));
                break;
        }
        return true;
    }
}