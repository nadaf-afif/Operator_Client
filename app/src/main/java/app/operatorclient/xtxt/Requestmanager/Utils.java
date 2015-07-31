package app.operatorclient.xtxt.Requestmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by mac on 04/07/15.
 */
public class Utils {

    public static String getAndroidVer() {
        String myVersion = "";
        try {
            myVersion = android.os.Build.VERSION.RELEASE;
        } catch (Exception e) {

        }
        return myVersion;
    }

    public static String getAppVer(Context ctx) {
        String version = "";
        try {
            PackageManager manager = ctx.getPackageManager();
            PackageInfo info = manager.getPackageInfo(
                    ctx.getPackageName(), 0);
            version = info.versionName;
        } catch (Exception e) {

        }
        return version;
    }

    public static void clearPreferences(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(RequestManger.PREFERENCES, Context.MODE_PRIVATE);
        prefs.edit().clear().commit();
    }


    public static int dateDiffVal(String currenttime, String datestring) {
        Date date = getDate(datestring);
        Date currentDate = getDate(currenttime);


        long oldMillis = date.getTime();
        long currMillis = currentDate.getTime();
        long diff = java.lang.Math.abs(currMillis - oldMillis);

        int ret = (int) (diff / 1000);

        return ret;
    }

    public static String getLocalTime(String timeString, String timezone) {
        String parsedate = "";
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            Date date = formatter.parse(timeString);
            TimeZone tz = TimeZone.getDefault();

            SimpleDateFormat sdfAmerica = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            TimeZone tzInAmerica = TimeZone.getTimeZone(timezone);
            sdfAmerica.setTimeZone(tzInAmerica);

            parsedate = sdfAmerica.format(date);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return parsedate;
    }

    public static String dateDiff(String currenttime, String datestring) {

        Date date = getDate(datestring);
        Date currentDate = getDate(currenttime);


        long oldMillis = date.getTime();
        long currMillis = currentDate.getTime();
        String msg = null;
        long diff = java.lang.Math.abs(currMillis - oldMillis);
        long temp = diff / 60000;
        long seconds = diff / 1000;

        if (temp >= 60) {
            temp = temp / 60;
            if (temp >= 24) {
                temp = temp / 24;
                if (temp > 30) {
                    temp = temp / 30;
                    if (temp > 12) {
                        temp = temp / 12;
                        if (temp == 1) {
                            msg = temp + " year ago";
                        } else {
                            msg = temp + " years ago";
                        }
                    } else {
                        if (temp == 1) {
                            msg = temp + " month ago";
                        } else {
                            msg = temp + " months ago";
                        }
                    }
                } else {
                    if (temp == 1) {
                        msg = temp + " day ago";
                    } else {
                        msg = temp + " days ago";
                    }
                }
            } else {
//                if (temp == 1) {
//                    msg = temp + " hr ago";
//                } else {
//                    msg = temp + " hrs ago";
//                }
                msg = String.format("%02dm %02ds", seconds / 60, seconds % 60);
            }
        } else {
//            if (temp == 1) {
//                msg = temp + " mn ago";
//            } else {
//                msg = temp + " mns ago";
//            }
            msg = String.format("%02dm %02ds", seconds / 60, seconds % 60);
        }
        return msg;
    }

    public static Date getDate(String date) {
        SimpleDateFormat inputFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        Date parsed = null;
        try {
            parsed = inputFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parsed;
    }


    public static Date getDOBDate(String date) {
        SimpleDateFormat inputFormat = new SimpleDateFormat(
                "yyyy-MM-dd");
        Date parsed = null;
        try {
            parsed = inputFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parsed;
    }

    public static ArrayList<Message> parseMsg(JSONArray messagesArray) {
        ArrayList<Message> msgArrayList = new ArrayList<>();
        Message msg;
        try {
            for (int i = 0; i < messagesArray.length(); i++) {
                JSONObject jsonobject = (JSONObject) messagesArray.get(i);
                String message_id = jsonobject.getString("message_id");
                String message = jsonobject.getString("message");
                String created = jsonobject.getString("created");
                String type = jsonobject.getString("type");
                String status = jsonobject.getString("status");

                JSONObject visual_content = null;
                Object visOBJ = jsonobject.get("visual_content");
                if (visOBJ instanceof JSONObject) {
                    visual_content = (JSONObject) visOBJ;
                }
                msg = new Message(message_id, message, created, type, status, visual_content);

                msgArrayList.add(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return msgArrayList;
    }

}
