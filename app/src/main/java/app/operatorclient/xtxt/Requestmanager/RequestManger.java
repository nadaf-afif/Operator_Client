package app.operatorclient.xtxt.Requestmanager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by mac on 04/07/15.
 */
@SuppressWarnings("ALL")
public class RequestManger {

    public static final String HOST = "http://stage.operator-api.xtxt.co/api/v1/";

    public static final String APIKEY = "Authorization";
    public static final String APIKEYVALUE = "xTxt123456";
    public static final String REQUESTERKEY = "Requester";
    public static final String REQUESTERVALUE = "xtxt-app-global";
    public static final String REQUESTERADMIN = "xtxt-app-admin";

    public static final String REQUESTEROPERATOR = "xtxt-app-operator";


    public static final String PREFERENCES = "appPreferences";

    public interface Constantas {
        public static final String ERROR = "error";
        public static final String DATA = "data";
        public static final String ID = "id";
        public static final String USERID = "user_id";
        public static final String SESSIONID = "session_id";
        public static final String NAME = "name";
        public static final String MESSAGEOFTHEDAY = "message_of_the_day";
        public static final String MESSAGE = "message";
        public static final String CURRENTTIME = "current_time";

        public static final String MOBILE = "mobile";
        public static final String USERNAME = "username";
        public static final String EMAIL = "email";
        public static final String NAMEATA = "name";
        public static final String PIN = "pin";
        public static final String OPERATORID = "operator_id";
        public static final String TIMEZONETITLE = "timezone_title";
        public static final String TIMEZONENAME = "timezone_name";

        public static final String AUTHKEY = "auth_key";

        public static final String CUSTOMER = "customer";
        public static final String CUSTOMERID = "customer_id";
        public static final String PERSONA = "persona";
        public static final String PERSONAID = "persona_id";
        public static final String PROFILEPIC = "profile_pic_100x100";
        public static final String SENDSMS = "send_sms";
        public static final String REPLTOP = "reply_op";
        public static final String DOB = "date_of_birth";
        public static final String COLOR = "colour";
        public static final String MESSAGES = "messages";
        public static final String MORE_MESSAGES = "more_messages";

        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";

        public static final String TOWN = "town";
        public static final String AREA = "area";
        public static final String AGE = "age";
        public static final String STS = "status";
        public static final String TIMEZONE = "timezone";

    }

    public static String postHttpRequestWithHeader(JSONObject object, Map<String, String> map, String url) {

        String responseString = null;

        try {

            HttpClient hc = new DefaultHttpClient();
            String message;
            message = object.toString();

            HttpPost p = new HttpPost(url);
            Log.d("json", message);
            p.setEntity(new StringEntity(message, "UTF8"));
            p.setHeader("Content-type", "application/json");

            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                p.setHeader(key, value);
            }

            HttpResponse response = hc.execute(p);
            Log.d("Status line", "" + response.getStatusLine().getStatusCode());
            responseString = EntityUtils.toString(response.getEntity());

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("RESPONSE POST", responseString + " ");
        return responseString;

    }


    public static String getHttpRequestWithHeader(Map<String, String> map, String url) {

        String responseString = null;

        try {

            HttpClient hc = new DefaultHttpClient();

            HttpGet p = new HttpGet(url);
            p.setHeader("Content-type", "application/json");

            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                p.setHeader(key, value);
            }

            HttpResponse response = hc.execute(p);
            Log.d("Status line", "" + response.getStatusLine().getStatusCode());
            responseString = EntityUtils.toString(response.getEntity());

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("URL GET", url + " ");
        Log.d("RESPONSE GET", responseString + " ");
        return responseString;

    }


    public static boolean isConnectedToInternet(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
        if (ni != null && ni.isAvailable() && ni.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}
