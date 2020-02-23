package de.seefried.rsh.fragment;

import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RetrieveXmlTask extends AsyncTask {

    URL url;
    String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    String devDate = "2020-02-21";
    String uri = "https://www.realschule-herrieden.de/vpapp/data_" + devDate + ".xml";


ArrayList woche = new ArrayList();
    ArrayList klasse = new ArrayList();
    ArrayList fach = new ArrayList();
    ArrayList stunde = new ArrayList();
    ArrayList raum = new ArrayList();
    ArrayList entfaellt = new ArrayList();

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            url = new URL(uri);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(getInputStream(url), "UTF_8");

            boolean insideItem = false;

            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {

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
                } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                    insideItem = false;
                }

                eventType = xpp.next(); //move to next element
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
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