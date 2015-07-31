package app.operatorclient.xtxt;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import app.operatorclient.xtxt.Requestmanager.RequestManger;

/**
 * Created by kiran on 30/7/15.
 */
public class PersonaDetailsActivity extends Activity implements RequestManger.Constantas {

    ImageButton back;
    ImageView pic;
    TextView nameTitle, nameTextView, cityTextView, stsTextView, ageTextview;
    SharedPreferences prefs;
    String personaId;
    JSONObject dataJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personadata);

        prefs = getSharedPreferences(RequestManger.PREFERENCES, Context.MODE_PRIVATE);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String data = extras.getString(DATA);
            personaId = extras.getString(PERSONAID);
            try {
                dataJSON = new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        back = (ImageButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pic = (ImageView) findViewById(R.id.pic);
        nameTitle = (TextView) findViewById(R.id.nameTitle);
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        cityTextView = (TextView) findViewById(R.id.cityTextView);
        stsTextView = (TextView) findViewById(R.id.stsTextView);
        ageTextview = (TextView) findViewById(R.id.ageTextview);


        try {
            nameTitle.setText(dataJSON.getString(NAME));
            nameTextView.setText(dataJSON.getString(NAME));

            cityTextView.setText(dataJSON.getString(AREA) + "," + dataJSON.getString(TOWN));
            stsTextView.setText(dataJSON.getString(STS));
            ageTextview.setText(dataJSON.getString(AGE));


            Picasso.with(PersonaDetailsActivity.this)
                    .load(dataJSON.getString("profile_pic_100"))
                    .placeholder(R.drawable.profilepic)
                    .into(pic);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
