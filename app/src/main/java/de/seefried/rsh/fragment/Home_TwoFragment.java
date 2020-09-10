/**
 * RS-Herrieden
 * Copyright (C) 2018  Noah Seefried and Moritz Fromm
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

            String date;
            String date_day;

            String weekday_name = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(System.currentTimeMillis());

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.GERMANY);

            String currentTimeString = timeFormat.format(new Date(System.currentTimeMillis()));
            String timeForNextDayString = "13:30";
            Date currentTime = timeFormat.parse(currentTimeString);
            Date timeForNextDay = timeFormat.parse(timeForNextDayString);

            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
            SimpleDateFormat date_dayFormat = new SimpleDateFormat("EE", Locale.GERMANY);

            Calendar calendar = Calendar.getInstance();
            if (weekday_name.equals("Saturday")) {
                calendar.add(Calendar.DAY_OF_YEAR, 2);
                Date plus2Days = calendar.getTime();
                date = dateFormat.format(plus2Days);
                date_day = "Mo.";
            } else if (weekday_name.equals("Sunday")) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                Date plus1Day = calendar.getTime();
                date = dateFormat.format(plus1Day);
                date_day = "Mo.";
            } else {
                if (currentTime.before(timeForNextDay)) {
                    date = dateFormat.format(new Date());
                    date_day = date_dayFormat.format(new Date());
                } else {
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                    Date plus1Day = calendar.getTime();
                    date = dateFormat.format(plus1Day);
                    date_day = date_dayFormat.format(plus1Day);
                }
            }

            output.add("Mitteilungen für " + date_day + ", " + date);

            if (mitteilung.size() == 0) {
                output.add("");
                output.add("Heute keine Mitteilung");
            } else {
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
