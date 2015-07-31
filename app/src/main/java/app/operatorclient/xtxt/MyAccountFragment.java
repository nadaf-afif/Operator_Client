package app.operatorclient.xtxt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.operatorclient.xtxt.Requestmanager.RequestManger;
import app.operatorclient.xtxt.Requestmanager.Utils;

/**
 * Created by yogi on 8/7/15.
 */
public class MyAccountFragment extends Fragment implements RequestManger.Constantas {

    View rootView;
    TextView nameTextview, usernameTextview, operatoridTextview, pinTextview, emailTextview, mobileTextview, timezoneTextview;
    SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_myaccount, container, false);


        prefs = getActivity().getSharedPreferences(RequestManger.PREFERENCES, Context.MODE_PRIVATE);

        nameTextview = (TextView) rootView.findViewById(R.id.nameTextview);
        usernameTextview = (TextView) rootView.findViewById(R.id.usernameTextview);
        operatoridTextview = (TextView) rootView.findViewById(R.id.operatoridTextview);
        pinTextview = (TextView) rootView.findViewById(R.id.pinTextview);
        emailTextview = (TextView) rootView.findViewById(R.id.emailTextview);
        mobileTextview = (TextView) rootView.findViewById(R.id.mobileTextview);
        timezoneTextview = (TextView) rootView.findViewById(R.id.timezoneTextview);


        String mobile = prefs.getString(MOBILE, "");
        String uid = prefs.getString(USERID, "");
        String username = prefs.getString(USERNAME, "");
        String email = prefs.getString(EMAIL, "");
        String name = prefs.getString(NAMEATA, "");
        String pin = prefs.getString(PIN, "");
        String operatorid = prefs.getString(OPERATORID, "");
        String timezone_title = prefs.getString(TIMEZONETITLE, "");
        String timezone_name = prefs.getString(TIMEZONENAME, "");

        nameTextview.setText(name);
        usernameTextview.setText(username);
        operatoridTextview.setText(operatorid);
        pinTextview.setText(pin);
        emailTextview.setText(email);
        mobileTextview.setText(mobile);
        timezoneTextview.setText(timezone_title);

        new GetDataAsynctask().execute();

        return rootView;
    }

    class GetDataAsynctask extends AsyncTask<String, Void, String> implements RequestManger.Constantas {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String response = "";

            try {
                Map<String, String> map = new HashMap<String, String>();
                String sessionid = prefs.getString(RequestManger.Constantas.SESSIONID, "");
                map.put(RequestManger.APIKEY, sessionid);
                map.put(RequestManger.REQUESTERKEY, RequestManger.REQUESTERADMIN);

                response = RequestManger.getHttpRequestWithHeader(map, RequestManger.HOST + "my_account");


            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (progressDialog != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
            if (!isAdded()) {
                return;
            }

            try {
                JSONObject responseJSON = new JSONObject(result);
                boolean error = responseJSON.getBoolean(ERROR);

                if (!error) {
                    JSONObject dataJSON = responseJSON.getJSONObject(DATA);

                    String mobile = dataJSON.getString(MOBILE);
                    String username = dataJSON.getString(USERNAME);
                    String email = dataJSON.getString(EMAIL);
                    String name = dataJSON.getString(NAMEATA);
                    String pin = dataJSON.getString(PIN);
                    String operatorid = dataJSON.getString(OPERATORID);
                    String timezone_title = dataJSON.getString(TIMEZONETITLE);
                    String timezone_name = dataJSON.getString(TIMEZONENAME);


                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(MOBILE, mobile);
                    editor.putString(USERNAME, username);
                    editor.putString(EMAIL, email);
                    editor.putString(NAMEATA, name);
                    editor.putString(PIN, pin);
                    editor.putString(OPERATORID, operatorid);
                    editor.putString(TIMEZONETITLE, timezone_title);
                    editor.putString(TIMEZONENAME, timezone_name);
                    editor.apply();

                    nameTextview.setText(name);
                    usernameTextview.setText(username);
                    pinTextview.setText(pin);
                    operatoridTextview.setText(operatorid);
                    emailTextview.setText(email);
                    mobileTextview.setText(mobile);
                    timezoneTextview.setText(timezone_title);

                } else {
                    JSONObject dataJSON = responseJSON.getJSONObject(DATA);
                    String message = dataJSON.getString(MESSAGE);
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

                    Utils.clearPreferences(getActivity());
                    Activity activity = getActivity();
                    activity.setResult(Activity.RESULT_OK);
                    Intent intent = new Intent(activity, LoginActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(getActivity(), "Unable to get data.", Toast.LENGTH_LONG).show();
            }

        }

    }
}
