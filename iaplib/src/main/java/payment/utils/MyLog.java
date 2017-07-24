package payment.utils;

import android.util.Log;


public class MyLog {
    static boolean DEBUG_MODE = true;// BuildConfig.DEBUG;
    static String LOG_TAG = MyLog.class.getSimpleName() + "(googleiap)";
    static long startTime = System.nanoTime();
    static String lastFileName = "";

    public static boolean isDebuging() {
        return DEBUG_MODE;
    }

    public static void v(String info) {
        log(2, info);
    }

    public static void d(String info) {
        log(3, info);
    }

    public static void i(String info) {
        log(4, info);
    }

    public static void w(String info) {
        log(5, info);
    }

    public static void e(String info) {
        log(6, info);
    }

    public static void startTiming() {
        startTime = System.nanoTime();
        if (DEBUG_MODE) {
            StackTraceElement[] stack = new Throwable().getStackTrace();
            StackTraceElement ste = stack[1];
            String fileName = ste.getFileName();

            Log.d(LOG_TAG, String.format("------%s[%s]%s[%s]", fileName, ste.getMethodName(), ste.getLineNumber(), "StartTiming..."));
        }
    }

    public static float stopTiming() {
        long consumingTime = System.nanoTime() - startTime;
        float timeConsuming = (int) (consumingTime / 1000f / 1000f * 100) / 100f;
        if (DEBUG_MODE) {
            StackTraceElement[] stack = new Throwable().getStackTrace();
            StackTraceElement ste = stack[1];
            String fileName = ste.getFileName();

            Log.d(LOG_TAG, String.format("-------%s[%s]%s Time-consuming %s Milliseconds", fileName, ste.getMethodName(), ste.getLineNumber(), timeConsuming));
        }
        return timeConsuming;
    }


    private static void log(int level, String info) {
        if (DEBUG_MODE) {
            Throwable throwable = new Throwable();
            StackTraceElement[] stack = throwable.getStackTrace();
            if (stack == null || stack.length == 0) {
                return;
            }
            Log.d(LOG_TAG, "-----start-----");
            if (level >= 5) {
                Log.d(LOG_TAG, info, throwable);
            } else {
                if (stack.length > 3) {
                    StackTraceElement ste = stack[2];
                    Log.d(LOG_TAG, String.format(">>>>>at %s(%s:%s)[%s]<<<<<", ste.getClassName(), ste.getFileName(), ste.getLineNumber(), info));
                }
            }
            Log.d(LOG_TAG, "----- end -----");
        }
    }

}
