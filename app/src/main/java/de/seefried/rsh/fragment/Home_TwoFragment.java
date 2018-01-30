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

import java.util.ArrayList;
import java.util.List;

import de.seefried.rsh.R;


public class Home_TwoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
        SwipeRefreshLayout swipeLayout;

    String url = "https://rsh.noah-seefried.de/v1.0/index.php";
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
                // Popup error dialog, disabled
                Toast.makeText(getActivity().getApplicationContext(), "Es ist ein Fehler aufgetreten!", Toast.LENGTH_LONG).show();
                // dialog.dismiss();
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
            JSONArray notificationsArray = object.getJSONArray("notifications");

            for(int i = 0; i < notificationsArray.length(); ++i) {
                JSONObject JsonNotifications  = notificationsArray.getJSONObject(i);
                String content = JsonNotifications.getString("content");
                output.add(content);
            }

            Home_TwoFragment_Adapter adapter = new Home_TwoFragment_Adapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, output);
            NotificationGridView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Close popup dialog, disabled
        // dialog.dismiss();
    }

}
