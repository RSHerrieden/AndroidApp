/**
 * RS-Herrieden
 * Copyright (C) 2018  Noah Seefried and Moritz Fromm
 * 
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
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.seefried.rsh.R;


public class Home_OneFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    // ProgressDialog dialog;  // needed for popup dialog, disabled

    GridView PlanGrid;
    GridView LastUpdateGrid;
    SwipeRefreshLayout swipeLayout;
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
        PlanGrid = view.findViewById(R.id.PlanGridView);
        LastUpdateGrid = view.findViewById(R.id.LastUpdateGridView);
        swipeLayout = view.findViewById(R.id.swipe_container);

        // Popup dialog, disabled
        // dialog = new ProgressDialog(getActivity());
        // dialog.setMessage("Laden...");
        // dialog.show();

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
        List<String> outputplan = new ArrayList<>();
        List<String> outputdate = new ArrayList<>();
        try {

            ArrayList<String> woche;
            ArrayList<String> klassen;
            ArrayList<String> faecher;
            ArrayList<String> stunden;
            ArrayList<String> raeume;
            ArrayList<String> entfaellt;

            RetrieveXmlTask getXML = new RetrieveXmlTask();
            getXML.execute();
            woche = getXML.woche();
            klassen = getXML.klassen();
            faecher = getXML.fach();
            stunden = getXML.stunde();
            raeume = getXML.raum();
            entfaellt = getXML.entfaellt();
            Log.wtf("log", String.valueOf(klassen.size()));
            if (klassen.size() > 0) {
                for (int i = 0; i < 13; i++) {
                    Log.wtf("log", String.valueOf(klassen.get(i)));
                }
            }

            SystemClock.sleep(500);

            // skip weekend needs to be added
            String date = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
            String date_day = new SimpleDateFormat("EE", Locale.getDefault()).format(new Date());

            String format_pref_schoolclass = pref_schoolclass.replace("%25", "");
            if (pref_schoolclass.equals("")) {
                // this is a bugfix for the first start, i cannot load the settings before user open it

                outputdate.add("Vertretungen für alle, " + date_day + " " + date + "      Woche: " + woche.get(0));
            } else {

                outputdate.add("Vertretungen für " + format_pref_schoolclass + ", " + date_day + " " + date + "      Woche: " + woche.get(0));
            }

            if (klassen.size() == 0) {
                // output one free line
                outputplan.add("");
                outputplan.add("");
                outputplan.add("");
                outputplan.add("");
                outputplan.add("");
                // output information if no representations
                if (pref_schoolclass.equals("alle")) {
                    outputplan.add("");
                    outputplan.add("Keine");
                    outputplan.add("");
                    outputplan.add("Vertretung");
                    outputplan.add("");
                } else {
                    outputplan.add("Aktuell");
                    outputplan.add("keine");
                    outputplan.add("Vertretung");
                    outputplan.add("für");
                    outputplan.add("dich");
                }
            } else {
                for (int i = 0; i < klassen.size(); i++) {

                    if (pref_schoolclass.equals("alle")) {
                        outputplan.add(klassen.get(i));
                        outputplan.add(stunden.get(i));
                        outputplan.add(faecher.get(i));
                        outputplan.add(raeume.get(i));
                        if (entfaellt.get(i).equals("false")) {
                            outputplan.add("✓");
                        } else {
                            outputplan.add("×");
                        }
                    } else if (klassen.get(i).equals(format_pref_schoolclass)) {
                        outputplan.add(klassen.get(i));
                        outputplan.add(stunden.get(i));
                        outputplan.add(faecher.get(i));
                        outputplan.add(raeume.get(i));
                        if (entfaellt.get(i).equals("false")) {
                            outputplan.add("✓");
                        } else {
                            outputplan.add("×");
                        }
                    }
                }
                if (outputplan.size() == 0) {
                    outputplan.add("Aktuell");
                    outputplan.add("keine");
                    outputplan.add("Vertretung");
                    outputplan.add("für");
                    outputplan.add("dich");
                }

                Home_OneFragment_PlanAdapter adapter = new Home_OneFragment_PlanAdapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, outputplan);
                PlanGrid.setAdapter(adapter);
                Home_OneFragment_DateAdapter dateadapter = new Home_OneFragment_DateAdapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, outputdate);
                LastUpdateGrid.setAdapter(dateadapter);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onRefresh() {
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }
}