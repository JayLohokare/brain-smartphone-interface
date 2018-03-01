package com.example.jaylo.bcismartphonecontrol.bcibridge;


import android.view.accessibility.AccessibilityNodeInfo;

import com.example.jaylo.bcismartphonecontrol.service.MyAccessibilityService;

import java.util.List;
import java.util.Map;

/**
 * This class is a bridge between android and BCI.
 * The BCI will call a method from this class to pass a number. The class then finds a element
 * corresponding to that number and invokes it's action.
 */
public class AndroidBCIConnector {

    public boolean sendOverlayNumber(int num) {
        Map<Integer, AccessibilityNodeInfo> buttonsMap = MyAccessibilityService.getButtonsMap();
        if(buttonsMap == null || !buttonsMap.containsKey(num)) {
            return false;
        }

        AccessibilityNodeInfo node = buttonsMap.get(num);
        List<AccessibilityNodeInfo.AccessibilityAction> actionList = node.getActionList();
        for(AccessibilityNodeInfo.AccessibilityAction action : actionList) {
            System.out.println(action.toString());
        }

        return true;
    }
}
