package com.kitty.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.kitty.tofuflee.BuildConfig;

public class FJLog {
    static boolean LOG_ENABLE = BuildConfig.DEBUG;
    static String LOG_TAG = "FJLog";
    static long startTime = System.nanoTime();
    static String lastFileName = "";

    public static boolean isDebuging() {
        return BuildConfig.DEBUG;
    }

    public static void i(String info) {
        if (LOG_ENABLE) {
            try {
                final StackTraceElement[] stack = new Throwable().getStackTrace();
                final StackTraceElement ste = stack[1];
                String fileName = ste.getFileName();
                if (TextUtils.isEmpty(fileName) && fileName.equalsIgnoreCase(lastFileName)) {
                    fileName = getBlankString(lastFileName.length());
                } else {
                    lastFileName = fileName;
                }
                android.util.Log.d(LOG_TAG, " ");
                android.util.Log.d(LOG_TAG, " ");
                android.util.Log.d(LOG_TAG, " ");
                android.util.Log.d(LOG_TAG, " ");
                android.util.Log.d(
                        LOG_TAG,
                        String.format("-----------------------------===========[%s][%s]%s[%s]==========----------------------------------", fileName, ste.getMethodName(),
                                ste.getLineNumber(), info));
                android.util.Log.d(LOG_TAG, " ");
                android.util.Log.d(LOG_TAG, " ");
                android.util.Log.d(LOG_TAG, " ");
            } catch (Exception e) {
                android.util.Log.d(LOG_TAG, " ");
                android.util.Log.d(LOG_TAG, " ");
                android.util.Log.d(LOG_TAG, " ");
                android.util.Log.d(LOG_TAG, " ");
                android.util.Log.d(LOG_TAG, "-----------------------------=====================----------------------------------");
                android.util.Log.d(LOG_TAG, " ");
                android.util.Log.d(LOG_TAG, " ");
                android.util.Log.d(LOG_TAG, " ");
            }
        }
    }

    public static void l(String info) {
        if (LOG_ENABLE) {
            try {
                final StackTraceElement[] stack = new Throwable().getStackTrace();
                final StackTraceElement ste = stack[1];
                String fileName = ste.getFileName();
                if (TextUtils.isEmpty(fileName) && fileName.equalsIgnoreCase(lastFileName)) {
                    fileName = getBlankString(lastFileName.length());
                } else {
                    lastFileName = fileName;
                }
                android.util.Log.d(LOG_TAG, String.format("------%s[%s]%s[%s]", fileName, ste.getMethodName(), ste.getLineNumber(), info));
            } catch (Exception e) {
                android.util.Log.d(LOG_TAG, String.format("------[%s]", info));
            }
        }
    }

    private static String getBlankString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    public static void l_stack(String info) {
        if (LOG_ENABLE) {
            try {
                final StackTraceElement[] stack = new Throwable().getStackTrace();
                StackTraceElement ste = stack[1];
                String fileName = ste.getFileName();
                if (TextUtils.isEmpty(fileName) && fileName.equalsIgnoreCase(lastFileName)) {
                    fileName = getBlankString(lastFileName.length());
                } else {
                    lastFileName = fileName;
                }
                android.util.Log.d(LOG_TAG, String.format("------%s[%s]%s[%s]", fileName, ste.getMethodName(), ste.getLineNumber(), info));
                for (int i = 2; i <= (stack.length > 2 ? 2 : 1); i++) {
                    ste = stack[i];
                    fileName = getBlankString((lastFileName + "------ ").length()) + ste.getFileName();
                    android.util.Log.d(LOG_TAG, String.format("%s[%s]%s", fileName, ste.getMethodName(), ste.getLineNumber()));
                }
            } catch (Exception e) {
                android.util.Log.d(LOG_TAG, String.format("------[%s]", info));
            }
        }
    }

    public static void l_stack_all(String info) {
        if (LOG_ENABLE) {
            try {
                final StackTraceElement[] stack = new Throwable().getStackTrace();
                StackTraceElement ste = stack[1];
                String fileName = ste.getFileName();
                if (TextUtils.isEmpty(fileName) && fileName.equalsIgnoreCase(lastFileName)) {
                    fileName = getBlankString(lastFileName.length());
                } else {
                    lastFileName = fileName;
                }
                android.util.Log.d(LOG_TAG, info + " " + String.format("------%s[%s]%s[%s]", fileName, ste.getMethodName(), ste.getLineNumber(), info));
                for (int i = 2; i < stack.length; i++) {
                    ste = stack[i];
                    fileName = getBlankString((lastFileName + "------ ").length()) + ste.getFileName();
                    android.util.Log.d(LOG_TAG, info + " " + String.format("%s[%s]%s", fileName, ste.getMethodName(), ste.getLineNumber()));
                }
                android.util.Log.d(LOG_TAG, info + " " + String.format("------ stack end!!--length:[%s]", stack.length - 1));
            } catch (Exception e) {
                android.util.Log.d(LOG_TAG, String.format("------[%s]", info));
            }
        }
    }

    public static void startTiming(String info) {
        startTime = System.nanoTime();
        if (LOG_ENABLE) {
            try {
                StackTraceElement[] stack = new Throwable().getStackTrace();
                StackTraceElement ste = stack[1];
                String fileName = ste.getFileName();
                if (TextUtils.isEmpty(fileName) && fileName.equalsIgnoreCase(lastFileName)) {
                    fileName = getBlankString(lastFileName.length());
                } else {
                    lastFileName = fileName;
                }
                android.util.Log.d(LOG_TAG, String.format("------%s[%s]%s[%s]", fileName, ste.getMethodName(), ste.getLineNumber(), "StartTiming..." + info));
            } catch (Exception e) {
                android.util.Log.d(LOG_TAG, String.format("------StartTiming..."));
            }
        }
    }

    public static float stopTiming() {
        long consumingTime = System.nanoTime() - startTime;
        float timeConsuming = (int) (consumingTime / 1000f / 1000f * 100) / 100f;
        if (LOG_ENABLE) {
            try {
                StackTraceElement[] stack = new Throwable().getStackTrace();
                StackTraceElement ste = stack[1];
                String fileName = ste.getFileName();
                if (TextUtils.isEmpty(fileName) && fileName.equalsIgnoreCase(lastFileName)) {
                    fileName = getBlankString(lastFileName.length());
                } else {
                    lastFileName = fileName;
                }
                android.util.Log.d(LOG_TAG, String.format("-------%s[%s]%s Time-consuming %s Milliseconds", fileName, ste.getMethodName(), ste.getLineNumber(), timeConsuming));
            } catch (Exception e) {
                android.util.Log.d(LOG_TAG, String.format("------- Time-consuming %s Milliseconds", timeConsuming));
            }
        }
        return timeConsuming;
    }

    public static void e(String info) {
        if (LOG_ENABLE) {
            android.util.Log.e(LOG_TAG, info);
        }
    }

    public static String getDBLog(SQLiteDatabase db, String sql) {
        StringBuilder sb = new StringBuilder();
        sb.append("DBLog: ").append(sql).append("\n");
        Cursor c = null;
        try {
            c = db.rawQuery(sql, null);
            if (!c.moveToFirst()) {
                sb.append("no record.\n");
            } else {
                do {
                    for (int n = 0; n < c.getColumnCount(); n++) {
                        sb.append(c.getColumnName(n)).append("=");
                        try {
                            sb.append(c.getString(n));
                        } catch (Exception e) {
                            sb.append(e.toString()).append("/").append(e.getMessage());
                        }
                        sb.append(", ");
                    }
                    sb.append("\n");
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            sb.append("DBLog Error: ").append(e.toString()).append(" ").append(e.getMessage()).append("\n");
        }
        if (c != null) {
            c.close();
        }
        return sb.toString();
    }

    //    private static String intToIp(int i) {
    //        return (i & 0xFF) + "." + (i >> 8 & 0xFF) + "." + (i >> 16 & 0xFF) + "." + (i >> 24 & 0xFF);
    //    }
    //
    //    private static boolean needUpload = false;
    //    private static boolean isSpecficUserRemoteLog = false;
    //    private static boolean isRondomUserRemoteLog = false;

    //    public static void checkRemoteLogState() {
    //        isSpecficUserRemoteLog = false;
    //        isRondomUserRemoteLog = false;
    //        if (RemoteConfig.getBoolean("RemoteLog", "Enable", false)) {
    //            isRondomUserRemoteLog = Config.getDeviceSharedPreferences().getInt("RLog_UUid", 1) < RemoteConfig.getInt("RemoteLog", "TokenPercentage", 0);
    //            ArrayList<String> uids = RemoteConfig.getArrayList("RemoteLog", "Users");
    //            if (uids != null && !TextUtils.isEmpty(Config.getLoginUserID())) {
    //                for (String uid : uids) {
    //                    if (uid.equals(Config.getLoginUserID())) {
    //                        isSpecficUserRemoteLog = true;
    //                        enableRemoteLog(true, "Users");
    //
    //                        try {
    //                            ConnectivityManager cm = (ConnectivityManager) HSIMApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    //                            IMLog.write("network info:" + cm.getActiveNetworkInfo().getType());
    //                        } catch (Exception e) {
    //                        }
    //
    //                        try {
    //                            WifiManager wifiManager = (WifiManager) HSIMApplication.getContext().getSystemService(Context.WIFI_SERVICE);
    //                            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
    //                            IMLog.write("network ip address:" + intToIp(wifiInfo.getIpAddress()));
    //                        } catch (Exception e) {
    //                        }
    //
    //                        break;
    //                    }
    //                }
    //            }
    //
    //            if (isSpecficUserRemoteLog == false) {
    //                enableRemoteLog(false, "Users");
    //            }
    //        } else {
    //            SharedPreferences deviceSh = Config.getDeviceSharedPreferences();
    //            Editor editor = deviceSh.edit();
    //            editor.putString("Conditions", "");
    //            editor.commit();
    //            IMRemoteLog.setEnable(false);
    //            IMRemoteLog.clear();
    //        }
    //    }
    //
    //    public static void enableRemoteLog(boolean bEnable, String condition) {
    //        String conditions = Config.getDeviceSharedPreferences().getString("Conditions", "");
    //        JSONObject jsonMap;
    //        Map<String, String> conditionMap = new HashMap<String, String>();
    //        try {
    //            jsonMap = new JSONObject(conditions);
    //            Iterator<String> it = jsonMap.keys();
    //            while (it.hasNext()) {
    //                String key = it.next();
    //                conditionMap.put(key, (String) jsonMap.get(key));
    //            }
    //        } catch (JSONException e) {
    //            e.printStackTrace();
    //        }
    //
    //        if (bEnable) {
    //            conditionMap.put(condition, "true");
    //        } else {
    //            conditionMap.remove(condition);
    //        }
    //
    //        if (conditionMap.isEmpty()) {
    //            IMRemoteLog.setEnable(false);
    //            IMRemoteLog.clear();
    //        } else {
    //            IMRemoteLog.setEnable(true);
    //        }
    //
    //        JSONObject json = new JSONObject();
    //        try {
    //            for (String key : conditionMap.keySet()) {
    //                json.put(key, conditionMap.get(key));
    //            }
    //        } catch (Exception e) {
    //        }
    //        SharedPreferences deviceSh = Config.getDeviceSharedPreferences();
    //        Editor editor = deviceSh.edit();
    //        editor.putString("Conditions", json.toString());
    //        editor.commit();
    //    }
    //
    //    public static void uploadRemoteLog() {
    //        if (IMRemoteLog.isEnabled() || needUpload) {
    //            IMRemoteLog.upload();
    //            needUpload = false;
    //        }
    //    }
    //
    //    public static void write(String log) {
    //        HSLog.d("RemoteLog", log);
    //        IMRemoteLog.log(log);
    //    }
    //
    //    public static void hardWrite(String log) {
    //        if (RemoteConfig.getBoolean("RemoteLog", "Enable", false)
    //                && Config.getDeviceSharedPreferences().getInt("RLog_UUid", 1) < RemoteConfig.getInt("RemoteLog", "ErrorPercentage", 0)) {
    //            needUpload = true;
    //            boolean isLogEnable = IMRemoteLog.isEnabled();
    //            if (!isLogEnable) {
    //                IMRemoteLog.setEnable(true);
    //            }
    //            HSLog.d("RemoteLog", log);
    //            IMRemoteLog.log(log);
    //            if (!isLogEnable) {
    //                IMRemoteLog.setEnable(false);
    //            }
    //        }
    //    }
}
