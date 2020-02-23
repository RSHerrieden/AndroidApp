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
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.seefried.rsh.R;


public class Home_TwoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout swipeLayout;

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
        NotificationGridView = view.findViewById(R.id.NotificationGridView);

        // Popup dialog, disabled
        // dialog = new ProgressDialog(getActivity());
        // dialog.setMessage("Laden...");
        // dialog.show();

        // SwipeRefresh
        swipeLayout = view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(android.R.color.black);

        List<String> output = new ArrayList<>();
        try {
            ArrayList<String> mitteilung;

            RetrieveXmlTask getXML = new RetrieveXmlTask();
            getXML.execute();
            mitteilung = getXML.mitteilung();

            SystemClock.sleep(500);


            // skip weekend needs to be added
            String date = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
            String date_day = new SimpleDateFormat("EE", Locale.getDefault()).format(new Date());

            output.add("Mitteilungen für " + date_day + ", " + date);

            String[] mitteilungen;
            mitteilungen = mitteilung.get(0).split("  ");
            if (mitteilungen[0].equals("")) {
                output.add("");
                output.add("Heute keine Mitteilung");
            } else {
                for (int i = 0; i < mitteilungen.length; i++) {
                    output.add(mitteilungen[i]);
                }
            }

            Home_TwoFragment_Adapter adapter = new Home_TwoFragment_Adapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, output);
            NotificationGridView.setAdapter(adapter);

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
