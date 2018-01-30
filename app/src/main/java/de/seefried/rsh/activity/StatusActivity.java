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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import de.seefried.rsh.R;


public class StatusActivity extends AppCompatActivity {

    Toolbar toolbar;
    //private WebView web_apistatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        // import Toolbar and goback arrow
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // import API-Status WebView
        //web_apistatus = (WebView) findViewById(R.id.web_apistatus);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Load API-Status
        //web_apistatus.getSettings().setJavaScriptEnabled(false);
        //web_apistatus.setWebViewClient(new WebViewClient());
        //web_apistatus.loadUrl("https://rsh.noah-seefried.de/apitest.php");
    }

    // goback to last Fragment
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}