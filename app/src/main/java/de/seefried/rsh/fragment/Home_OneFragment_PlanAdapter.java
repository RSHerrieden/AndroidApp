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
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import de.seefried.rsh.R;

public class Home_OneFragment_PlanAdapter extends BaseAdapter {

    Context context;
    List<String> output;
    LayoutInflater inflater;
    TextView gridtext;

    public Home_OneFragment_PlanAdapter(Context applicationContext, int simple_list_item_1, List<String> output) {
        this.context = applicationContext;
        this.output = output;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() { return output.size(); }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
    view = inflater.inflate(R.layout.fragment_home_one_gridtext, null);
        gridtext = (TextView) view.findViewById(R.id.gridtext);
        gridtext.setText(output.get(i));
        return view;
    }
}
