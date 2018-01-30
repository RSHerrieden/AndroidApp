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
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import de.seefried.rsh.R;
import de.seefried.rsh.data.TaskData;
import de.seefried.rsh.data.TaskDataDBHelper;

public class TodoListActivity extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnClickListener {

    TodoList_TaskAdapter mTaskAdapter;
    Toolbar toolbar;

    // TASKS_COLUMNS
    static final int COL_TASK_ID = 0;
    static final int COL_TASK_NAME = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todolist);

        // Find  ListView
        ListView listView = (ListView) findViewById(R.id.list_task);
        // Find FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        // Check if clicked
        fab.setOnClickListener(this);

        // DBHelper to read from database
        TaskDataDBHelper helper = new TaskDataDBHelper(this);
        SQLiteDatabase sqlDB = helper.getReadableDatabase();

        // Query database to get data
        @SuppressLint("Recycle") Cursor cursor = sqlDB.query(TaskData.TaskEntry.TABLE_NAME,
                new String[]{TaskData.TaskEntry._ID, TaskData.TaskEntry.COLUMN_TASK},
                null, null, null, null, null);

        //Create new TodoList_TaskAdapter and bind it to ListView
        mTaskAdapter = new TodoList_TaskAdapter(this, cursor);
        listView.setAdapter(mTaskAdapter);

        // import Toolbar and goback arrow
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    // goback to last Fragment
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle( Html.fromHtml("<font color='#000000'>Neue Aufgabe</font>"));
        final EditText inputField = new EditText(this);
        inputField.setTextColor(Color.parseColor("#000000"));
        builder.setView(inputField);
        builder.setPositiveButton("Hinzufügen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Get user input
                String inputTask = inputField.getText().toString();

                // Get DBHelper to write to database
                TaskDataDBHelper helper1 = new TaskDataDBHelper(TodoListActivity.this);
                SQLiteDatabase db = helper1.getWritableDatabase();

                // Put in the values within a ContentValues
                ContentValues values = new ContentValues();
                values.clear();
                values.put(TaskData.TaskEntry.COLUMN_TASK, inputTask);

                // Insert the values into Table for Tasks
                db.insertWithOnConflict(
                        TaskData.TaskEntry.TABLE_NAME,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_IGNORE);

                // Query database again to get updated data
                Cursor cursor = db.query(TaskData.TaskEntry.TABLE_NAME,
                        new String[]{TaskData.TaskEntry._ID, TaskData.TaskEntry.COLUMN_TASK},
                        null, null, null, null, null);

                // Swap old data with new data for display
                mTaskAdapter.swapCursor(cursor);
            }
        });
        builder.setNegativeButton("Abbrechen", null);
        builder.create().show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }
}
