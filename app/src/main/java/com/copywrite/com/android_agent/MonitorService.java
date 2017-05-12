package com.copywrite.com.android_agent;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import static android.view.accessibility.AccessibilityEvent.*;

/**
 * Created by di on 2017/5/8.
 */

public class MonitorService extends AccessibilityService {
    private static Long lastScan = 0L;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        final int eventType = event.getEventType();

        if (TYPE_WINDOW_CONTENT_CHANGED == eventType ) {
            AccessibilityNodeInfo rootNode = getRootInActiveWindow();

            if (null == rootNode) {
                return;
            }

            List<AccessibilityNodeInfo> scanList = rootNode.findAccessibilityNodeInfosByText("扫码登录");
            if (null != scanList && scanList.size() > 0) {
                AccessibilityNodeInfo parent = scanList.get(scanList.size() - 1);
                if (null != parent) {
                    Long now = new Date().getTime();
                    if (now - lastScan > 5000) {
                        execShellCmd("input touchscreen tap 39 1600");
                        lastScan = now;
                    }
                    return;
                }
            }

            List<AccessibilityNodeInfo> rescanList = rootNode.findAccessibilityNodeInfosByText("手机安全登录");
            if (null != rescanList && rescanList.size() > 0) {
                AccessibilityNodeInfo parent = scanList.get(rescanList.size() - 1);
                if (null == parent) {
                    return;
                }

                execShellCmd("input touchscreen tap 39 1700");
                return;
            }
        }

        if (TYPE_WINDOW_STATE_CHANGED == eventType ) {
            AccessibilityNodeInfo rootNode = getRootInActiveWindow();

            if (null == rootNode) {
                return;
            }

            List<AccessibilityNodeInfo> scanList = rootNode.findAccessibilityNodeInfosByText("扫码登录");
            if (null != scanList && scanList.size() > 0) {
                AccessibilityNodeInfo parent = scanList.get(scanList.size() - 1);
                if (null != parent) {
                    execShellCmd("input touchscreen tap 39 1600");
                    return;
                }
            }

            List<AccessibilityNodeInfo> rescanList = rootNode.findAccessibilityNodeInfosByText("手机安全登录");
            if (null != rescanList && rescanList.size() > 0) {
                AccessibilityNodeInfo parent = scanList.get(rescanList.size() - 1);
                if (null == parent) {
                    return;
                }

                execShellCmd("input touchscreen tap 39 1700");
                return;
            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    /**
     * 执行shell命令
     *
     * @param cmd
     */
    private void execShellCmd(String cmd) {

        try {
            // 申请获取root权限，这一步很重要，不然会没有作用
            Process process = Runtime.getRuntime().exec("su");
            // 获取输出流
            OutputStream outputStream = process.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(
                    outputStream);
            dataOutputStream.writeBytes(cmd);
            dataOutputStream.flush();
            dataOutputStream.close();
            outputStream.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
