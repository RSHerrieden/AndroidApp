package de.seefried.rsh.fragment;

import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RetrieveXmlTask extends AsyncTask {

    ArrayList error = new ArrayList();
    ArrayList mitteilung = new ArrayList();
    ArrayList woche = new ArrayList();
    ArrayList klasse = new ArrayList();
    ArrayList fach = new ArrayList();
    ArrayList stunde = new ArrayList();
    ArrayList raum = new ArrayList();
    ArrayList entfaellt = new ArrayList();

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            String date = null;
            URL url;

            String weekday_name = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(System.currentTimeMillis());

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.GERMANY);

            String currentTimeString = timeFormat.format(new Date(System.currentTimeMillis()));
            String timeForNextDayString = "13:30";
            Date currentTime = timeFormat.parse(currentTimeString);
            Date timeForNextDay = timeFormat.parse(timeForNextDayString);

            Calendar calendar = Calendar.getInstance();
            if (weekday_name.equals("Saturday")) {
                calendar.add(Calendar.DAY_OF_YEAR, 2);
                Date plus2Days = calendar.getTime();
                date = dateFormat.format(plus2Days);
            } else if (weekday_name.equals("Sunday")) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                Date plus1Day = calendar.getTime();
                date = dateFormat.format(plus1Day);
            } else {
                if (currentTime.before(timeForNextDay)) {
                    date = dateFormat.format(new Date());
                } else {
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                    Date plus1Day = calendar.getTime();
                    date = dateFormat.format(plus1Day);
                }
            }
            String devDate = "2020-09-12";
            Log.d("DATE", date);
            String uri = "https://www.realschule-herrieden.de/vpapp/data_" + date + ".xml";
            url = new URL(uri);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();
            try {
                xpp.setInput(url.openConnection().getInputStream(), "UTF_8");
            } catch (FileNotFoundException e) {
                // TODO: proper error handling
                error.add("404");
                //e.printStackTrace();
            }

            boolean insideItem = false;

            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {

                    if (xpp.getName().equalsIgnoreCase("SchuelerMitteilung")) {
                        mitteilung.add(xpp.getAttributeValue(null, "Text"));
                    }
                    if (xpp.getName().equalsIgnoreCase("Woche")) {
                        woche.add(xpp.nextText());
                    }

                    if (xpp.getName().equalsIgnoreCase("Vertretung")) {
                        insideItem = true;
                    } else if (xpp.getName().equalsIgnoreCase("Klasse")) {
                        if (insideItem)
                            klasse.add(xpp.nextText()); // extract the klasse
                    } else if (xpp.getName().equalsIgnoreCase("Fach")) {
                        if (insideItem)
                            fach.add(xpp.nextText()); //extract the fach
                    } else if (xpp.getName().equalsIgnoreCase("Std")) {
                        if (insideItem)
                            stunde.add(xpp.nextText()); //extract the Std
                    } else if (xpp.getName().equalsIgnoreCase("Raum")) {
                        if (insideItem)
                            raum.add(xpp.nextText()); //extract the fach
                    } else if (xpp.getName().equalsIgnoreCase("Entfaellt")) {
                        if (insideItem)
                            entfaellt.add(xpp.nextText()); //extract the fach
                    }
                }
                eventType = xpp.next();
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return klasse;
    }

    public InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    public ArrayList error() {
        return error;
    }

    public ArrayList mitteilung() {
        return mitteilung;
    }

    public ArrayList woche() {
        return woche;
    }

    public ArrayList klassen() {
        return klasse;
    }

    public ArrayList<String> fach() {
        return fach;
    }

    public ArrayList<String> stunde() {
        return stunde;
    }

    public ArrayList<String> raum() {
        return raum;
    }

    public ArrayList<String> entfaellt() {
        return entfaellt;
    }
}