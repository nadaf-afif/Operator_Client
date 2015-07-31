package app.operatorclient.xtxt.Requestmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.operatorclient.xtxt.LoginActivity;

/**
 * Created by yogi on 11/7/15.
 */
public class LogoutAsynctask extends AsyncTask<String, Void, String> implements RequestManger.Constantas {
    Context context;

    public LogoutAsynctask(Context context) {

        this.context = context;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected String doInBackground(String... params) {

        String response = "";

        JSONObject object = new JSONObject();
        try {
            Map<String, String> map = new HashMap<String, String>();
            String sessionid = context.getSharedPreferences(RequestManger.PREFERENCES, Context.MODE_PRIVATE).getString(RequestManger.Constantas.SESSIONID, "");
            map.put(RequestManger.APIKEY, sessionid);
            map.put(RequestManger.REQUESTERKEY, RequestManger.REQUESTERADMIN);

            response = RequestManger.postHttpRequestWithHeader(object, map, RequestManger.HOST + "logout");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        Utils.clearPreferences(context);

        Activity activity = (Activity) context;
        activity.setResult(Activity.RESULT_OK);
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

}
