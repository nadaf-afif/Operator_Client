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
public class CustomerDetailsActivity extends Activity implements RequestManger.Constantas {

    ImageButton back;
    ImageView pic;
    TextView nameTitle, nameTextView, cityTextView, dobTextView, ageTextview;
    SharedPreferences prefs;
    String customerId;
    JSONObject dataJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerdata);

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
        dobTextView = (TextView) findViewById(R.id.dobTextView);
        ageTextview = (TextView) findViewById(R.id.ageTextview);


        try {
            nameTitle.setText(dataJSON.getString(NAME));
            nameTextView.setText(dataJSON.getString(NAME));

            cityTextView.setText(dataJSON.getString(TOWN));
            dobTextView.setText(dataJSON.getString(DOB));
            ageTextview.setText(dataJSON.getString(AGE));


            Picasso.with(CustomerDetailsActivity.this)
                    .load(dataJSON.getString("avatars_100"))
                    .placeholder(R.drawable.profilepic)
                    .into(pic);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
