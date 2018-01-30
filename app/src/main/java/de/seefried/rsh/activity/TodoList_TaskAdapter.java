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

package de.seefried.rsh.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;


import de.seefried.rsh.R;
import de.seefried.rsh.data.TaskData;
import de.seefried.rsh.data.TaskDataDBHelper;

public class TodoList_TaskAdapter extends CursorAdapter {

    @SuppressLint("StaticFieldLeak")
    private static Context context;
    TaskDataDBHelper helper;

    public TodoList_TaskAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        TodoList_TaskAdapter.context = context;
        helper = new TaskDataDBHelper((TodoListActivity) context);
    }

    // newView to inflate a new view and return it
    // Don't bind any data to view at this point
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_todolist_item_task, parent, false);
    }

    // bindView method to bind all data to given view
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find Views to populate inflated template
        TextView textView = (TextView) view.findViewById(R.id.list_item_task_textview);
        Button done_button = (Button) view.findViewById(R.id.list_item_task_done_button);

        // Extract properties from cursor
        final String id = cursor.getString(TodoListActivity.COL_TASK_ID);
        final String task = cursor.getString(TodoListActivity.COL_TASK_NAME);

        // Populate views with extracted properties
        textView.setText(task);
        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create SQL command for deleting particular ID.
                String sql = String.format("DELETE FROM %s WHERE %s = '%s'",
                        TaskData.TaskEntry.TABLE_NAME,
                        TaskData.TaskEntry._ID,
                        id);
                SQLiteDatabase sqlDB = helper.getWritableDatabase();
                // Execute delete command
                sqlDB.execSQL(sql);
                notifyDataSetChanged();

                // Query database for updated data
                Cursor cursor = sqlDB.query(TaskData.TaskEntry.TABLE_NAME,
                        new String[]{TaskData.TaskEntry._ID, TaskData.TaskEntry.COLUMN_TASK},
                        null,null,null,null,null);
                // Instance method with TodoList_TaskAdapter [so no need to use adapter.swapCursor()]
                swapCursor(cursor);
            }
        });
    }
}