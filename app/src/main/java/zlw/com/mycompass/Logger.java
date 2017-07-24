package zlw.com.mycompass;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Android Log的包装类,免去一些麻烦
 */
public class Logger {
    private static final String PRE = "&";
    public static boolean sIsDebug = true;
    public static boolean sIsShow = true;

    private static final String space = "----------------------------------------------------------------------------------------------------";

//    private static boolean LOGV = false;
//    private static boolean LOGD = false;
//    private static boolean LOGI = false;
//    private static boolean LOGW = false;
//    private static boolean LOGE = false;

    public static void v(String tag, String format, Object... args) {
        if (sIsShow) {
            List<String> list = buildMessage(format, args);
            for (int i = 0; i < list.size(); i++) {
                String msg = list.get(i);
                Log.v(formartLength(PRE + tag, 28), (i > 0 ? "    " : "") + msg);
            }
        }
    }

    public static void v(Throwable throwable, String tag, String format, Object... args) {
        if (sIsShow) {
            List<String> list = buildMessage(format, args);
            for (int i = 0; i < list.size(); i++) {
                String msg = list.get(i);
                Log.v(formartLength(PRE + tag, 28), (i > 0 ? "    " : "") + msg, throwable);
            }
        }
    }


    public static void d(String tag, String format, Object... args) {
        if (sIsShow) {
            List<String> list = buildMessage(format, args);
            for (int i = 0; i < list.size(); i++) {
                String msg = list.get(i);
                Log.d(formartLength(PRE + tag, 28), (i > 0 ? "    " : "") + msg);
            }
        }
    }

    public static void d(Throwable throwable, String tag, String format, Object... args) {
        if (sIsShow) {
            List<String> list = buildMessage(format, args);
            for (int i = 0; i < list.size(); i++) {
                String msg = list.get(i);
                Log.d(formartLength(PRE + tag, 28), (i > 0 ? "    " : "") + msg, throwable);
            }
        }
    }

    public static void i(String tag, String format, Object... args) {
        if (sIsShow) {
            List<String> list = buildMessage(format, args);
            for (int i = 0; i < list.size(); i++) {
                String msg = list.get(i);
                Log.i(formartLength(PRE + tag, 28), (i > 0 ? "    " : "") + msg);
            }

        }
    }

    public static void i(Throwable throwable, String tag, String format, Object... args) {
        if (sIsShow) {
            List<String> list = buildMessage(format, args);
            for (int i = 0; i < list.size(); i++) {
                String msg = list.get(i);
                Log.i(formartLength(PRE + tag, 28), (i > 0 ? "    " : "") + msg, throwable);
            }
        }
    }

    public static void w(String tag, String format, Object... args) {
        if (sIsShow) {
            List<String> list = buildMessage(format, args);
            for (int i = 0; i < list.size(); i++) {
                String msg = list.get(i);
                Log.w(formartLength(PRE + tag, 28), (i > 0 ? "    " : "") + msg);
            }
        }
    }

    public static void w(Throwable throwable, String tag, String format, Object... args) {
        if (sIsShow) {
            List<String> list = buildMessage(format, args);
            for (int i = 0; i < list.size(); i++) {
                String msg = list.get(i);
                Log.w(formartLength(PRE + tag, 28), (i > 0 ? "    " : "") + msg, throwable);
            }
        }
    }


    public static void e(String tag, String format, Object... args) {
        if (sIsShow) {
            List<String> list = buildMessage(format, args);
            for (int i = 0; i < list.size(); i++) {
                String msg = list.get(i);
                Log.e(formartLength(PRE + tag, 28), (i > 0 ? "    " : "") + msg);
            }
        }
    }

    public static void e(Throwable throwable, String tag, String format, Object... args) {
        if (sIsShow) {
            List<String> list = buildMessage(format, args);
            for (int i = 0; i < list.size(); i++) {
                String msg = list.get(i);
                Log.e(formartLength(PRE + tag, 28), (i > 0 ? "    " : "") + msg, throwable);
            }
        }
    }

    public static void printStackTrace(Throwable e) {
        if (sIsShow) {
            String message = e.getMessage() == null ? "unknown throwable message " : e.getMessage();
            List<String> list = buildMessage(message);
            for (int i = 0; i < list.size(); i++) {
                String msg = list.get(i);
                Log.e(formartLength(PRE, 28), (i > 0 ? "    " : "") + msg);
            }
            e.printStackTrace();
        }
    }

    private static List<String> buildMessage(String format, Object... args) {
        List<String> msgList = new ArrayList<>();

        try {
            String msg = (args == null || args.length == 0) ?
                    (TextUtils.isEmpty(format) ? "--->format null<---" : format)
                    : String.format(Locale.US, format, args);
//            msgList.add(msg);
            if (!sIsShow) {
                return msgList;
            }
            StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();
            String caller = "";
            String callingClass = "";
            String callFile = "";
            int lineNumber = 0;
            for (int i = 2; i < trace.length; i++) {
                Class<?> clazz = trace[i].getClass();
                if (!clazz.equals(Logger.class)) {
                    callingClass = trace[i].getClassName();
                    callingClass = callingClass.substring(callingClass
                            .lastIndexOf('.') + 1);
                    caller = trace[i].getMethodName();
                    callFile = trace[i].getFileName();
                    lineNumber = trace[i].getLineNumber();
                    break;
                }
            }

            String method = String.format(Locale.US, "[%03d] %s.%s(%s:%d)"
                    , Thread.currentThread().getId(), callingClass, caller, callFile, lineNumber);
            String formartMethod = formartLength(method, 93);

            for (int i = 0; i < msg.length(); i += 2048) {
                if (i + 2048 < msg.length()) {
                    String subMsg = msg.substring(i, i + 2048);
                    String logMessage = String.format(Locale.US, "%s> %s", formartMethod, subMsg);
                    msgList.add(logMessage);
                } else {
                    String subMsg = msg.substring(i, msg.length());
                    String logMessage = String.format(Locale.US, "%s> %s", formartMethod, subMsg);
                    msgList.add(logMessage);
                    break;
                }
            }
            return msgList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        msgList.add("----->ERROR LOG STRING<------");
        return msgList;
    }

    private static String formartLength(String src, int len) {
        StringBuilder sb = new StringBuilder();
        if (src.length() >= len) {
            sb.append(src);
        } else {
            sb.append(src);
            sb.append(space.substring(0, len - src.length()));
        }
        return sb.toString();
    }

    public static class XiaoCaiqi {
        long start;

        public XiaoCaiqi() {
            start = SystemClock.elapsedRealtime();
        }

        public long end() {
            return SystemClock.elapsedRealtime() - start;
        }

    }

}

/*
                     ★
                    ／＼
                   ／  ＼
                  ／i⸛  ＼
                 ／｡ i & ＼
                ／ i &⸛&⸛ ＼
               ／⸮⁂    @⸮ ⸛＼
              ／｡⸛  & &｡ ⸮  ＼
             ／ ⸛  ⸮    ｡⸮  ⸛＼
            ／ ⁂｡⸛@  ｡  @⸛｡ & ＼
           ／⁂ @ @   ⸮ ⸛@     &＼
          ／@ ｡ ｡& ⸮@   ⸛ & ｡   ＼
         ／i ⁂⁂ ⸮⸛i  @⸛ ⁂ ⸛     ＼
        ／  ⸛          ⸮｡｡@｡&&    ＼
       ／   @⸮ ｡   ⸛   ⁂      ⸮    ＼
      ／ &⸮｡i  ⸛｡   ｡  & &     i   i＼
     ／⸛ i      @ i    @ @   i& &i⁂@ ＼
    ／⸛@⸛i ⁂    ⁂ @  i  & ｡ ⸮⸮    &   ＼
   ／｡i⸛   ⸮   ⸛     @& ⸮&⸮i⸛｡⁂  ｡@ ｡⸮⸮＼
  ／         &     i  ⸛&@     ｡ ⸛ ⁂⸛  @ ＼
 ／⁂ ⁂     ⁂ i  &  ⸮⸛⁂ &        @｡ ｡     ＼
 ^^^^^^^^^^^^^^^^^^^|  |^^^^^^^^^^^^^^^^^^^
                    |  |


 */