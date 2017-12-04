package com.example.erick.aondeir;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Erick , Caroline , Isabela on 10/09/2017.
 */

public class Connection {

    private static final String ns = null;
    private  String inicialUrl = "https://api.catracalivre.com.br/select/?fq={!geofilt%20pt=";
    private  String middleUrl = "%20sfield=place_geolocation%20d=";
    private String finalUrl = "}&q=post_type:event&q=event_datetime:[";
    //private  String geoLocation = "-23.17944,-45.88694";

    public List listDoc(String geoLoc, Date date, String range){

        URL urlConection;
        HttpURLConnection urlConnection = null;
        List<Doc> xmlList = null;

        String auxDate = "";

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        auxDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        auxDate = auxDate + "T00:00:00Z%20TO%20";
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 7);
        auxDate = auxDate + new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        auxDate = auxDate + "T23:59:59Z]";
        //System.out.println("teste calendar: " + auxDate);

        try {
            urlConection = new URL(inicialUrl + geoLoc + middleUrl + range + finalUrl + auxDate);
            urlConnection = (HttpURLConnection) urlConection.openConnection();
            InputStream in = urlConnection.getInputStream();
            xmlList = parse(in);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                urlConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace(); //If you want further info on failure...
            }
        }
        return xmlList;
    }

    public String getCityName(String geoLoc){
        return geoLoc;
    }

    // ****************************PARSING***********************************************

    public List parse(InputStream in) throws XmlPullParserException, IOException {

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            List resultList = readResult(parser);
            return resultList;
        } finally {
            in.close();
        }
    }
    private List readResult(XmlPullParser parser) throws XmlPullParserException, IOException {
        List docs = new ArrayList();
        parser.require(XmlPullParser.START_TAG, ns, "response");
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            String name = parser.getName();
            // Starts by looking for the doc tag
            try{
                if (name.equals("doc")) {
                    docs.add(readDoc(parser));
                }
            }catch (Exception e){}
        }
        return docs;
    }
    private Doc readDoc(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "doc");
        // **************Ok!
        String post_title = null;
        Bitmap post_image_thumbnail = null;
        String place_name = null;
        String place_geolocation = null;
        String place_neighborhood = null;
        String place_city = null;
        String post_permalink = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            String attribute = parser.getAttributeValue(0);
            if (name.equals("str") && attribute.equals("post_title")) {
                post_title = readPost_title(parser);
            }else if (name.equals("str") && attribute.equals("post_image_thumbnail")) {
                post_image_thumbnail = readPost_image_thumbnail(parser);
            }else if (name.equals("arr") && attribute.equals("place_name")) {
                place_name = readPlace_name(parser);
            }else if (name.equals("arr") && attribute.equals("place_geolocation")) {
                place_geolocation = readPlace_geolocation(parser);
            }else if (name.equals("arr") && attribute.equals("place_neighborhood")) {
                place_neighborhood = readPlace_neighborhood(parser);
            }else if (name.equals("str") && attribute.equals("post_permalink")){
                post_permalink = readPost_permalink(parser);
            }else if (name.equals("arr") && attribute.equals("place_city")){
                place_city = readPlace_city(parser);
            }else {
                skip(parser);
            }
        }
        return new Doc(post_title, post_image_thumbnail, place_name, place_geolocation, place_neighborhood, place_city, post_permalink);
    }
    private String readPost_title(XmlPullParser parser) throws IOException, XmlPullParserException {
        String title = "";
        parser.require(XmlPullParser.START_TAG, ns, "str");
        String tag = parser.getName();
        String attribute = parser.getAttributeValue(0);
        if (tag.equals("str")) {
            if (attribute.equals("post_title")){
                title = readText(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "str");
        return title;
    }
    private String readPost_permalink(XmlPullParser parser) throws IOException, XmlPullParserException {
        String title = "";
        parser.require(XmlPullParser.START_TAG, ns, "str");
        String tag = parser.getName();
        String attribute = parser.getAttributeValue(0);
        if (tag.equals("str")) {
            if (attribute.equals("post_permalink")){
                title = readText(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "str");
        return title;
    }
    private Bitmap readPost_image_thumbnail(XmlPullParser parser) throws IOException, XmlPullParserException {
        Bitmap image_thumbnail = null;
        parser.require(XmlPullParser.START_TAG, ns, "str");
        String tag = parser.getName();
        String attribute = parser.getAttributeValue(0);
        if (tag.equals("str")) {
            if (attribute.equals("post_image_thumbnail")){
                try{
                    URL url = new URL(readText(parser));
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream =  httpURLConnection.getInputStream();
                    image_thumbnail = BitmapFactory.decodeStream(inputStream);
                }catch(IOException e){
                    System.out.println("teste: Falhou image_thumbnail download!");
                }
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "str");
        return image_thumbnail;
    }
    private String readPlace_name(XmlPullParser parser) throws IOException, XmlPullParserException {
        String place_name = "";
        parser.require(XmlPullParser.START_TAG, ns, "arr");
        parser.next();
        String tag = parser.getName();
        if (tag.equals("str")) {
            place_name = readText(parser);
            parser.next();
            // *** if parser.next is null is because there are a lot of places... ***
            parser.require(XmlPullParser.END_TAG, ns, "arr");
            return place_name;
        }else {
            parser.next();
            parser.require(XmlPullParser.END_TAG, ns, "arr");
            return "will be implemented...";
        }
    }
    private String readPlace_geolocation(XmlPullParser parser) throws IOException, XmlPullParserException {
        String place_geolocation = "";
        parser.require(XmlPullParser.START_TAG, ns, "arr");
        parser.next();
        String tag = parser.getName();
        if (tag.equals("str")) {
            place_geolocation = readText(parser);
        }
        parser.next();
        parser.require(XmlPullParser.END_TAG, ns, "arr");
        return place_geolocation;
    }
    private String readPlace_neighborhood(XmlPullParser parser) throws IOException, XmlPullParserException {
        String place_neighborhood = "";
        parser.require(XmlPullParser.START_TAG, ns, "arr");
        parser.next();
        String tag = parser.getName();
        if (tag.equals("str")) {
            place_neighborhood = readText(parser);
        }
        parser.next();
        parser.require(XmlPullParser.END_TAG, ns, "arr");
        return place_neighborhood;
    }
    private String readPlace_city(XmlPullParser parser) throws IOException, XmlPullParserException {
        String place_city = "";
        parser.require(XmlPullParser.START_TAG, ns, "arr");
        parser.next();
        String tag = parser.getName();
        if (tag.equals("str")) {
            place_city = readText(parser);
        }
        parser.next();
        parser.require(XmlPullParser.END_TAG, ns, "arr");
        return place_city;
    }
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}