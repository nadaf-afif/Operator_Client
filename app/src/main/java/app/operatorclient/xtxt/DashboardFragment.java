package app.operatorclient.xtxt;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import app.operatorclient.xtxt.Requestmanager.RequestManger;

/**
 * Created by yogi on 4/7/15.
 */
public class DashboardFragment extends Fragment {

    View rootView;
    TextView modTextview, chatTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        modTextview = (TextView) rootView.findViewById(R.id.modTextview);
        chatTextView = (TextView) rootView.findViewById(R.id.chatTextView);

        SharedPreferences prefs = getActivity().getSharedPreferences(RequestManger.PREFERENCES, Context.MODE_PRIVATE);
        modTextview.setText(prefs.getString(RequestManger.Constantas.MESSAGEOFTHEDAY, ""));

        chatTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (RequestManger.isConnectedToInternet(getActivity())) {
                    ((MainActivity) getActivity()).startSession();
                } else {
                    Toast.makeText(getActivity(), "Please check Internet Connection.", Toast.LENGTH_LONG).show();
                }

            }
        });

        return rootView;
    }

}
