package app.operatorclient.xtxt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.operatorclient.xtxt.Requestmanager.GPSTracker;
import app.operatorclient.xtxt.Requestmanager.Persona;
import app.operatorclient.xtxt.Requestmanager.RequestManger;
import app.operatorclient.xtxt.Requestmanager.Utils;

/**
 * Created by kiran on 29/7/15.
 */
public class PersonaListActivity extends Activity implements RequestManger.Constantas {

    ImageButton back;
    GridView gridview;
    ImageAdapter adapter;
    SharedPreferences prefs;
    String customerId;
    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalist);

        prefs = getSharedPreferences(RequestManger.PREFERENCES, Context.MODE_PRIVATE);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            customerId = extras.getString(CUSTOMERID);
        }

        back = (ImageButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        gridview = (GridView) findViewById(R.id.gridview);


        if (RequestManger.isConnectedToInternet(PersonaListActivity.this)) {
            new GetPersonaListAsynctask().execute();
        } else {
            Toast.makeText(PersonaListActivity.this, "Please check Internet Connection.", Toast.LENGTH_LONG).show();
        }

        getGPSLocation();

    }

    class GetPersonaListAsynctask extends AsyncTask<String, Void, String> implements RequestManger.Constantas {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(PersonaListActivity.this);
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

                response = RequestManger.getHttpRequestWithHeader(map, RequestManger.HOST + "personas_list?customer_id=" + customerId);


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

                    JSONArray personaArray = responseJSON.getJSONArray(DATA);

                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Persona>>() {
                    }.getType();

                    List<Persona> personas = gson.fromJson(personaArray.toString(), listType);

                    personas = sortData(personas);

                    adapter = new ImageAdapter(personas);
                    gridview.setAdapter(adapter);

                } else {
                    JSONObject dataJSON = responseJSON.getJSONObject(DATA);
                    String message = dataJSON.getString(MESSAGE);
                    Toast.makeText(PersonaListActivity.this, message, Toast.LENGTH_LONG).show();


                    Utils.clearPreferences(PersonaListActivity.this);
                    setResult(Activity.RESULT_OK);
                    Intent intent = new Intent(PersonaListActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(PersonaListActivity.this, "Unable to get data.", Toast.LENGTH_LONG).show();
            }

        }

    }

    public class ImageAdapter extends BaseAdapter {

        private List<Persona> personas;
        private LayoutInflater inflater = null;

        public ImageAdapter(List<Persona> personas) {
            this.personas = personas;
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return personas.size();
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

                vi = inflater.inflate(R.layout.gridview_persona, null);

                holder = new ViewHolder();
                holder.nameTextView = (TextView) vi.findViewById(R.id.name);
                holder.pic = (ImageView) vi.findViewById(R.id.pic);

                vi.setTag(holder);
            } else
                holder = (ViewHolder) vi.getTag();


            final Persona persona = personas.get(position);

            holder.nameTextView.setText(titleLetter(persona.getName()));
            Picasso.with(PersonaListActivity.this)
                    .load(persona.getProfile_pic_100())
                    .placeholder(R.drawable.profilepic)
                    .into(holder.pic);

            vi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });

            return vi;

        }

    }

    static class ViewHolder {

        public TextView nameTextView;
        public ImageView pic;

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

    public void getGPSLocation() {
        gps = new GPSTracker(PersonaListActivity.this);

        // check if GPS enabled
        if (gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            Log.d("LAT-LONG", latitude + "  " + longitude);

            if (latitude != 0 & longitude != 0) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(LATITUDE, latitude + "");
                editor.putString(LONGITUDE, longitude + "");
                editor.apply();
            }

        } else {
            gps.showSettingsAlert();
        }
    }

    private List<Persona> sortData(List<Persona> personas) {

        List<Persona> retPersonas = new ArrayList<>();

        String lngt = prefs.getString(LONGITUDE, "");
        String lat = prefs.getString(LATITUDE, "");

        if (!TextUtils.isEmpty(lngt) && !TextUtils.isEmpty(lat)) {

            Location selfLocation, location;

            selfLocation = new Location("");
            selfLocation.setLatitude(Double.parseDouble(lat));
            selfLocation.setLongitude(Double.parseDouble(lngt));

            for (Persona prsn : personas) {
                location = new Location("");

                String perlat = prsn.getLatitude();
                String perlong = prsn.getLongitude();

                if (!TextUtils.isEmpty(perlat) && !TextUtils.isEmpty(perlong)) {
                    location.setLatitude(Double.parseDouble(perlat));
                    location.setLongitude(Double.parseDouble(perlong));

                    double distanceInMeters = selfLocation.distanceTo(location);
                    prsn.setDist(distanceInMeters);
                }

                retPersonas.add(prsn);

            }

            Collections.sort(retPersonas, new MyDistComp());

            return retPersonas;

        }
        Collections.sort(personas, new MyAlphaComp());

        return personas;
    }

    class MyDistComp implements Comparator<Persona> {

        @Override
        public int compare(Persona e1, Persona e2) {
            if (e1.getDist() < e2.getDist()) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    class MyAlphaComp implements Comparator<Persona> {

        @Override
        public int compare(Persona e1, Persona e2) {
            return e1.name.compareToIgnoreCase(e2.name);
        }
    }
}
