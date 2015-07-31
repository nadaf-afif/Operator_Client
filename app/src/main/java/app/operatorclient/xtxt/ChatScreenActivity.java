package app.operatorclient.xtxt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.operatorclient.xtxt.Requestmanager.CircularImageView;
import app.operatorclient.xtxt.Requestmanager.LogoutAsynctask;
import app.operatorclient.xtxt.Requestmanager.Message;
import app.operatorclient.xtxt.Requestmanager.RequestManger;
import app.operatorclient.xtxt.Requestmanager.Utils;

/**
 * Created by kiran on 21/7/15.
 */
public class ChatScreenActivity extends Activity implements RequestManger.Constantas {

    ImageButton logout, changePersona, media, send;
    ListView listview;
    CustomAdapter adapter;
    CircularImageView customerPic, personaPic;
    TextView customerName, personaName, loadmore;
    EditText messageEdittext;
    LinearLayout sendLinearlayout;
    SharedPreferences prefs;
    JSONObject dataJSON;
    JSONObject personaJSON;
    String customerId, timezone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatscreen);

        prefs = getSharedPreferences(RequestManger.PREFERENCES, Context.MODE_PRIVATE);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String data = extras.getString(DATA);
            customerId = extras.getString(CUSTOMERID);
            try {
                dataJSON = new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        logout = (ImageButton) findViewById(R.id.logout);
        changePersona = (ImageButton) findViewById(R.id.changePersona);
        customerPic = (CircularImageView) findViewById(R.id.customerPic);
        personaPic = (CircularImageView) findViewById(R.id.personaPic);
        customerName = (TextView) findViewById(R.id.customerName);
        personaName = (TextView) findViewById(R.id.personaName);
        listview = (ListView) findViewById(R.id.listview);
        sendLinearlayout = (LinearLayout) findViewById(R.id.sendLinearlayout);

        media = (ImageButton) findViewById(R.id.media);
        send = (ImageButton) findViewById(R.id.send);
        messageEdittext = (EditText) findViewById(R.id.messageEdittext);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatScreenActivity.this);
                builder.setMessage("Are you sure you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new LogoutAsynctask(ChatScreenActivity.this).execute();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        changePersona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatScreenActivity.this, PersonaListActivity.class);
                intent.putExtra(CUSTOMERID, customerId);
                startActivity(intent);
            }
        });

        LayoutInflater inflater = getLayoutInflater();
        View header = inflater.inflate(R.layout.more_messages, listview, false);
        listview.addHeaderView(header, null, false);
        loadmore = (TextView) header.findViewById(R.id.loadMore);


        try {
            JSONObject customerJSON = dataJSON.getJSONObject(CUSTOMER);
            customerName.setText(titleLetter(customerJSON.getString(NAME)));

            Picasso.with(ChatScreenActivity.this)
                    .load(customerJSON.getString(PROFILEPIC))
                    .placeholder(R.drawable.profilepic)
                    .into(customerPic);
            timezone = customerJSON.getString(TIMEZONE);

            if (dataJSON.get(PERSONA) instanceof JSONObject) {
                personaJSON = dataJSON.getJSONObject(PERSONA);
                personaName.setText(titleLetter(personaJSON.getString(NAME)));

                Picasso.with(ChatScreenActivity.this)
                        .load(personaJSON.getString(PROFILEPIC))
                        .placeholder(R.drawable.profilepic)
                        .into(personaPic);
            } else if (dataJSON.get(PERSONA) instanceof JSONArray) {
                sendLinearlayout.setVisibility(View.GONE);
            }

            boolean isSendSMS = dataJSON.getBoolean(SENDSMS);
            if (!isSendSMS) {
                sendLinearlayout.setVisibility(View.GONE);
            }

            String replyop = dataJSON.getString(REPLTOP);
            if (!replyop.equalsIgnoreCase("null")) {
                sendLinearlayout.setVisibility(View.GONE);

                AlertDialog.Builder builder = new AlertDialog.Builder(ChatScreenActivity.this);
                builder.setMessage(replyop + " is already replying.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }

            try {
                String customerDOB = customerJSON.getString(DOB);

                Date date = Utils.getDOBDate(customerDOB);
                Date currentDate = new Date();

                Calendar calDOB = Calendar.getInstance();
                calDOB.setTime(date);
                Calendar cal = Calendar.getInstance();
                cal.setTime(currentDate);

                int diff = cal.get(Calendar.YEAR) - calDOB.get(Calendar.YEAR);
                if (diff < 18) {
                    media.setVisibility(View.GONE);
                }
                if (customerDOB.contains("0000")) {
                    media.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                media.setVisibility(View.GONE);
            }

            String color = customerJSON.getString(COLOR);
            listview.setBackgroundColor(Color.parseColor("#" + color));

            JSONArray messagesArray = dataJSON.getJSONArray(MESSAGES);

            ArrayList<Message> msgArrayList = Utils.parseMsg(messagesArray);
            adapter = new CustomAdapter(msgArrayList);
            listview.setAdapter(adapter);
            listview.setSelection(msgArrayList.size() - 1);

            boolean moreMessages = dataJSON.getBoolean(MORE_MESSAGES);
            if (moreMessages) {
                loadmore.setVisibility(View.VISIBLE);

                loadmore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (adapter != null) {
                            try {
                                String msgid = adapter.getLastMessageid();
                                new MoreChatAsynctask().execute(customerId, msgid, personaJSON.getString(ID));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

            } else {
                loadmore.setVisibility(View.GONE);
            }

            customerPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new CustomerDataAsynctask().execute(customerId);
                }
            });

            personaPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        new PersonaDataAsynctask().execute(personaJSON.getString(ID));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private String titleLetter(String source) {

        try {
            String[] arr = source.split(" ");
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < arr.length; i++) {
                sb.append(Character.toUpperCase(arr[i].charAt(0)))
                        .append(arr[i].substring(1).toLowerCase()).append(" ");
            }
            return sb.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return source;
    }

    public class CustomAdapter extends BaseAdapter {

        private List<Message> data;
        private LayoutInflater inflater = null;
        String currenttime;

        public CustomAdapter(List<Message> data) {

            this.data = data;
            currenttime = prefs.getString(CURRENTTIME, "");
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        public List<Message> getdataArray() {
            return data;
        }

        public String getLastMessageid() {
            Message temp = data.get(0);
            return temp.getMessage_id();
        }

        public int getCount() {

            return data.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }


        public View getView(int position, View convertView, ViewGroup parent) {

            View vi = convertView;
            ViewHolder holder;

            if (convertView == null) {

                vi = inflater.inflate(R.layout.listitem_msg, null);

                holder = new ViewHolder();

                holder.received = (RelativeLayout) vi.findViewById(R.id.received);
                holder.sent = (RelativeLayout) vi.findViewById(R.id.sent);
                holder.recMsg = (TextView) vi.findViewById(R.id.msg);
                holder.recTime = (TextView) vi.findViewById(R.id.recTime);
                holder.sentMsg = (TextView) vi.findViewById(R.id.sentMsg);
                holder.sentTime = (TextView) vi.findViewById(R.id.sentTime);

                vi.setTag(holder);
            } else
                holder = (ViewHolder) vi.getTag();


            final Message message = data.get(position);
            if (message.getType().equalsIgnoreCase("mo")) {
                holder.received.setVisibility(View.VISIBLE);
                holder.sent.setVisibility(View.GONE);
                holder.recMsg.setText(message.getMessage());
                holder.recTime.setText(Utils.getLocalTime(message.getCreated(), timezone));
            } else {
                holder.received.setVisibility(View.GONE);
                holder.sent.setVisibility(View.VISIBLE);
                holder.sentMsg.setText(message.getMessage());
                holder.sentTime.setText(Utils.getLocalTime(message.getCreated(), timezone));
            }


            return vi;
        }

    }


    static class ViewHolder {
        public RelativeLayout received, sent;
        public TextView recMsg, recTime, sentMsg, sentTime;

    }

    class MoreChatAsynctask extends AsyncTask<String, Void, String> implements RequestManger.Constantas {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ChatScreenActivity.this);
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
                String authkey = prefs.getString(AUTHKEY, "");
                map.put(RequestManger.APIKEY, authkey);
                map.put(RequestManger.REQUESTERKEY, RequestManger.REQUESTEROPERATOR);

                String url = "message_history?customer_id=" + params[0] + "&message_id=" + params[1] + "&persona_id=" + params[2];

                response = RequestManger.getHttpRequestWithHeader(map, RequestManger.HOST + url);


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

                    JSONArray messageArray = dataJSON.getJSONArray(MESSAGES);
                    ArrayList<Message> msgArrayList = Utils.parseMsg(messageArray);
                    int position = msgArrayList.size();
                    msgArrayList.addAll(adapter.getdataArray());

                    adapter = new CustomAdapter(msgArrayList);
                    listview.setAdapter(adapter);
                    listview.setSelection(position);

                    boolean moreMessages = dataJSON.getBoolean(MORE_MESSAGES);
                    if (moreMessages) {
                        loadmore.setVisibility(View.VISIBLE);
                    } else {
                        loadmore.setVisibility(View.GONE);
                    }

                } else {
                    JSONObject dataJSON = responseJSON.getJSONObject(DATA);
                    String message = dataJSON.getString(MESSAGE);
                    Toast.makeText(ChatScreenActivity.this, message, Toast.LENGTH_LONG).show();


                    Utils.clearPreferences(ChatScreenActivity.this);
                    setResult(Activity.RESULT_OK);
                    Intent intent = new Intent(ChatScreenActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(ChatScreenActivity.this, "Unable to get data.", Toast.LENGTH_LONG).show();
            }

        }

    }

    class CustomerDataAsynctask extends AsyncTask<String, Void, String> implements RequestManger.Constantas {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ChatScreenActivity.this);
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
                String authkey = prefs.getString(AUTHKEY, "");
                map.put(RequestManger.APIKEY, authkey);
                map.put(RequestManger.REQUESTERKEY, RequestManger.REQUESTEROPERATOR);

                response = RequestManger.getHttpRequestWithHeader(map, RequestManger.HOST + "customer_details/" + params[0]);


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
                    Intent intent = new Intent(ChatScreenActivity.this, CustomerDetailsActivity.class);
                    intent.putExtra(DATA, dataJSON.toString());
                    intent.putExtra(CUSTOMERID, customerId);
                    startActivity(intent);
                } else {
                    JSONObject dataJSON = responseJSON.getJSONObject(DATA);
                    String message = dataJSON.getString(MESSAGE);
                    Toast.makeText(ChatScreenActivity.this, message, Toast.LENGTH_LONG).show();


                    Utils.clearPreferences(ChatScreenActivity.this);
                    setResult(Activity.RESULT_OK);
                    Intent intent = new Intent(ChatScreenActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(ChatScreenActivity.this, "Unable to get data.", Toast.LENGTH_LONG).show();
            }

        }

    }

    class PersonaDataAsynctask extends AsyncTask<String, Void, String> implements RequestManger.Constantas {
        ProgressDialog progressDialog;
        String personaid;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ChatScreenActivity.this);
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
                String authkey = prefs.getString(AUTHKEY, "");
                map.put(RequestManger.APIKEY, authkey);
                map.put(RequestManger.REQUESTERKEY, RequestManger.REQUESTEROPERATOR);

                personaid = params[0];

                response = RequestManger.getHttpRequestWithHeader(map, RequestManger.HOST + "persona_details/" + params[0]);


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
                    Intent intent = new Intent(ChatScreenActivity.this, PersonaDetailsActivity.class);
                    intent.putExtra(DATA, dataJSON.toString());
                    intent.putExtra(PERSONAID, personaid);
                    startActivity(intent);
                } else {
                    JSONObject dataJSON = responseJSON.getJSONObject(DATA);
                    String message = dataJSON.getString(MESSAGE);
                    Toast.makeText(ChatScreenActivity.this, message, Toast.LENGTH_LONG).show();


                    Utils.clearPreferences(ChatScreenActivity.this);
                    setResult(Activity.RESULT_OK);
                    Intent intent = new Intent(ChatScreenActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(ChatScreenActivity.this, "Unable to get data.", Toast.LENGTH_LONG).show();
            }

        }

    }


}
