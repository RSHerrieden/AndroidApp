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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
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


public class Home_OneFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    String url = "https://rsh.noah-seefried.de/v1.0/index.php";

    // ProgressDialog dialog;  // needed for popup dialog, disabled

    GridView PlanGrid;
    GridView LastUpdateGrid;
    SwipeRefreshLayout swipeLayout;
    LinearLayout parentPanel;
    TextView testview;

    public Home_OneFragment() {
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
        View view = inflater.inflate(R.layout.fragment_home_one, container, false);
        // Find GridView
        PlanGrid = (GridView)view.findViewById(R.id.PlanGridView);
        LastUpdateGrid = (GridView)view.findViewById(R.id.LastUpdateGridView);
        swipeLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_container);
        parentPanel = (LinearLayout)view.findViewById(R.id.parentPanel);

        // Popup dialog, disabled
        // dialog = new ProgressDialog(getActivity());
        // dialog.setMessage("Laden...");
        // dialog.show();

        swipeLayout.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (parentPanel.getScrollY() == 0) {
                    swipeLayout.setEnabled(true);
                } else {
                    swipeLayout.setEnabled(false);
                }
            }
        });

        // SwipeRefresh
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(android.R.color.black);

        // read out selected schoolclass in ListPreference
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String pref_schoolclass = sp.getString("key_schoolclass", "");

        // check selected schoolclass; only for debugging
        // testview = (TextView)view.findViewById(R.id.viewBox);
        // testview.setText(pref_schoolclass);


        // request data from server
        StringRequest request = new StringRequest(url + "?class=" + pref_schoolclass, new Response.Listener<String>() {
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
            List<String> outputplan = new ArrayList<>();
            List<String> outputdate = new ArrayList<>();

            JSONObject object = new JSONObject(JSON);

            JSONObject amount = object.getJSONObject("amount");
            String amount_replacements = amount.getString("replacements");

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String pref_schoolclass = sp.getString("key_schoolclass", "");
            String format_pref_schoolclass = pref_schoolclass.replace("%25", "");

            // date
            JSONObject datefordata = object.getJSONObject("date");
            String date = datefordata.getString("string");

            // week
            String week = object.getString("week");

            SimpleDateFormat date_day_format1 = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
            Date dt1 = date_day_format1.parse(date);
            DateFormat date_day_format2 = new SimpleDateFormat("EE", Locale.GERMANY);
            String date_day = date_day_format2.format(dt1);

            outputdate.add("Vertretungen für " + format_pref_schoolclass + ", " + date_day + " " + date + "      Woche: " + week);

            if (amount_replacements.equals("0")) {
                // output one free line
                outputplan.add("");
                outputplan.add("");
                outputplan.add("");
                outputplan.add("");
                outputplan.add("");
                // output information if no representations
                if (pref_schoolclass.equals("alle")) {
                    outputplan.add("");
                    outputplan.add("Heute");
                    outputplan.add("keine");
                    outputplan.add("Vertretung");
                    outputplan.add("");
                } else {
                    outputplan.add("Heute");
                    outputplan.add("keine");
                    outputplan.add("Vertretung");
                    outputplan.add("für");
                    outputplan.add(format_pref_schoolclass);
                }
            } else {

                JSONArray planArray = object.getJSONArray("replacements");

                // replacements
                for(int i = 0; i < planArray.length(); ++i) {
                    JSONObject JSONAPI = planArray.getJSONObject(i);
                    String schoolclass = JSONAPI.getString("schoolclass");
                    String schoolsubject = JSONAPI.getString("schoolsubject");
                    String schoolhour = JSONAPI.getString("schoolhour");
                    String schoolroom = JSONAPI.getString("schoolroom");
                    String dropped = JSONAPI.getString("dropped");
                    // String out_test = schoolclass + "   " + representations + "   " + schoolsubject + "   " + schoolhour;

                    String format_schoolhour = schoolhour + ". Stunde";
                    outputplan.add(schoolclass);
                    outputplan.add(format_schoolhour);

                    if (schoolsubject.equals("")) {
                        outputplan.add("➔\t");
                    } else {
                        outputplan.add(schoolsubject);
                    }

                    outputplan.add(schoolroom);

                    // check if dropped
                    if (dropped.equals("0")) {
                        outputplan.add("✓");
                    } else {
                        outputplan.add("×");
                    }
                }
            }

            Home_OneFragment_PlanAdapter adapter = new Home_OneFragment_PlanAdapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, outputplan);
            PlanGrid.setAdapter(adapter);
            Home_OneFragment_DateAdapter dateadapter = new Home_OneFragment_DateAdapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, outputdate);
            LastUpdateGrid.setAdapter(dateadapter);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Close popup dialog, disabled
        // dialog.dismiss();
    }
}