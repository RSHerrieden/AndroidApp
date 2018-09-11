/**
 * RS-Herrieden
 * Copyright (C) 2018  Noah Seefried and Moritz Fromm

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.seefried.rsh.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.seefried.rsh.R;


public class Home_TwoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
        SwipeRefreshLayout swipeLayout;

    String url = "https://rsh.noah-seefried.de/v2.0/notifications";
    // needed for popup dialog, disabled
    // ProgressDialog dialog;
    GridView NotificationGridView;

    public Home_TwoFragment() {
        // Empty
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout
        View view = inflater.inflate(R.layout.fragment_home_two, container, false);
        // Find ListView
        NotificationGridView = (GridView)view.findViewById(R.id.NotificationGridView);

        // Popup dialog, disabled
        // dialog = new ProgressDialog(getActivity());
        // dialog.setMessage("Laden...");
        // dialog.show();

        // SwipeRefresh
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(android.R.color.black);

        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String string) {
                parseJsonData(string);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getActivity().getApplicationContext(), "Es ist ein Fehler aufgetreten!", Toast.LENGTH_LONG).show();

                List<String> outputerror = new ArrayList<>();
                outputerror.add("Keine Internetverbindung vorhanden!");

                Home_TwoFragment_Adapter adapter = new Home_TwoFragment_Adapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, outputerror);
                NotificationGridView.setAdapter(adapter);
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(getActivity());
        rQueue.add(request);

        return view;
    }

    @Override
    public void onRefresh() {
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    void parseJsonData(String JSON) {
        try {
            List<String> output = new ArrayList<>();

            JSONObject object = new JSONObject(JSON);

            JSONObject meta = object.getJSONObject("meta");
            String apiDate = meta.getString("date");

            SimpleDateFormat date_day_format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);
            Date dt1 = date_day_format1.parse(apiDate);
            date_day_format1.applyPattern("dd.MM.yyyy");
            String date = date_day_format1.format(dt1);
            DateFormat date_day_format2 = new SimpleDateFormat("EEEE", Locale.GERMANY);
            String date_day = date_day_format2.format(dt1);

            output.add("Mitteilungen für " + date_day + ", " + date);

            String amount = object.getString("amount");

            if (amount.equals("0")) {
                output.add("");
                output.add("Heute keine Mitteilung");
            } else {

                JSONArray notificationsArray = object.getJSONArray("notifications");

                for (int i = 0; i < notificationsArray.length(); ++i) {
                    String value = notificationsArray.getString(i);
                    output.add(value);
                }
            }

            Home_TwoFragment_Adapter adapter = new Home_TwoFragment_Adapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, output);
            NotificationGridView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Close popup dialog, disabled
        // dialog.dismiss();
    }

}
