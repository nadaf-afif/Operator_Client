package app.operatorclient.xtxt;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import app.operatorclient.xtxt.Requestmanager.RequestManger;
import app.operatorclient.xtxt.Requestmanager.Utils;

/**
 * Created by mac on 07/07/15.
 */
public class StataticsFragment extends Fragment {

    View rootView;
    EditText fromEdittext, toEdittext;
    TextView getTextView;
    TextView messages, country1, country2, country1v, country2v, total;
    LinearLayout stats;
    private SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_statatics, container, false);

        prefs = getActivity().getSharedPreferences(RequestManger.PREFERENCES, Context.MODE_PRIVATE);

        fromEdittext = (EditText) rootView.findViewById(R.id.fromEdittext);
        toEdittext = (EditText) rootView.findViewById(R.id.toEdittext);
        getTextView = (TextView) rootView.findViewById(R.id.getTextView);

        messages = (TextView) rootView.findViewById(R.id.messages);
        country1 = (TextView) rootView.findViewById(R.id.country1);
        country2 = (TextView) rootView.findViewById(R.id.country2);
        country1v = (TextView) rootView.findViewById(R.id.country1v);
        country2v = (TextView) rootView.findViewById(R.id.country2v);
        total = (TextView) rootView.findViewById(R.id.total);

        stats = (LinearLayout) rootView.findViewById(R.id.stats);

        final DatePickerDialog.OnDateSetListener fromdatePickerListener = new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int selectedYear,
                                  int selectedMonth, int selectedDay) {

                fromEdittext.setText(selectedYear + "-" + String.format("%02d", selectedMonth) + "-" + String.format("%02d", selectedDay));
            }
        };

        fromEdittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                DatePickerDialog datePicker = new DatePickerDialog(getActivity(), fromdatePickerListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));
                datePicker.setCancelable(false);
                datePicker.setTitle("Select the date");

                datePicker.show();
            }
        });

        final DatePickerDialog.OnDateSetListener todatePickerListener = new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int selectedYear,
                                  int selectedMonth, int selectedDay) {

                toEdittext.setText(selectedYear + "-" + String.format("%02d", selectedMonth) + "-" + String.format("%02d", selectedDay));
            }
        };


        toEdittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                DatePickerDialog datePicker = new DatePickerDialog(getActivity(), todatePickerListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));
                datePicker.setCancelable(false);
                datePicker.setTitle("Select the date");

                datePicker.show();
            }
        });

        getTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String from = fromEdittext.getText().toString();
                String to = toEdittext.getText().toString();

                if (TextUtils.isEmpty(from)) {
                    Toast.makeText(getActivity(), "Please choose from date.", Toast.LENGTH_LONG).show();
                }
                if (TextUtils.isEmpty(to)) {
                    Toast.makeText(getActivity(), "Please choose to date.", Toast.LENGTH_LONG).show();
                }

                new StatsAsynctask().execute(from, to);
            }
        });

        return rootView;
    }

    class StatsAsynctask extends AsyncTask<String, Void, String> implements RequestManger.Constantas {

        ProgressDialog progressDialog;
        String from, to;

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

            JSONObject object = new JSONObject();
            try {

                object.put("from", params[0]);
                object.put("to", params[1]);
                from = params[0];
                to = params[1];


                Map<String, String> map = new HashMap<String, String>();
                String sessionid = prefs.getString(RequestManger.Constantas.SESSIONID, "");
                map.put(RequestManger.APIKEY, sessionid);
                map.put(RequestManger.REQUESTERKEY, RequestManger.REQUESTERADMIN);

                response = RequestManger.postHttpRequestWithHeader(object, map, RequestManger.HOST + "stats");


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

            try {
                JSONObject responseJSON = new JSONObject(result);
                boolean error = responseJSON.getBoolean(ERROR);

                if (!error) {
                    JSONObject dataJSON = responseJSON.getJSONObject(DATA);
                    JSONObject messageJSON = dataJSON.getJSONObject(MESSAGE);

                    Map<String, String> map = new HashMap<String, String>();

                    Iterator<String> iter = messageJSON.keys();
                    while (iter.hasNext()) {
                        String key = iter.next();
                        String value = messageJSON.getString(key);
                        map.put(key, value);
                    }

                    stats.setVisibility(View.VISIBLE);

                    messages.setText("Messages from " + from + " to " + to);
                    total.setText(map.get("Total"));
                    map.remove("Total");

                    int index = 0;
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();

                        if (index == 0) {
                            country1.setText(key);
                            country1v.setText(value);
                        } else {
                            country2.setText(key);
                            country2v.setText(value);
                        }
                        index = index + 1;

                    }

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
