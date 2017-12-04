package com.example.erick.aondeir;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Erick , Caroline , Isabela on 16/09/2017.
 */

public class DocList {

    private List<Doc> docs;

    public void getDocs(String geoLoc, Date date, String range){
        try {
            docs = new ArrayList(new Connection().listDoc(geoLoc, date, range));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> returnAllPost_Title(String geoLoc, Date date, String range){
        List<String> post_titles = new LinkedList<String>();
        // ok!
        getDocs(geoLoc, date, range);
        for(Doc doc:docs){
            post_titles.add(doc.getPost_title());
        }
        return post_titles;
    }

    public String returnPlaceByTitle(String title){
        String found = new String();
        for(Doc doc: docs){
            if(doc.getPost_title().equals(title)) found = doc.getPlace_name();
        }
        return found;
    }

    public String returnNeighborhoodByTitle(String title){
        String found = new String();
        for(Doc doc: docs){
            if(doc.getPost_title().equals(title)) found = doc.getPlace_neighborhood();
        }
        return found;
    }

    public Bitmap returnImageIco(String title){
        Bitmap found = null;
        for(Doc doc: docs){
            if(doc.getPost_title().equals(title)) found = doc.getPost_image_thumbnail();
        }
        return found;
    }

    public String returnPlace_city(String title) {
        String found = new String();
        for (Doc doc: docs){
            if (doc.getPost_title().equals(title)) found = doc.getPlace_city();
        }
        return found;
    }

    public String returnPost_permalink(String title){
        String found = null;
        for(Doc doc: docs){
            if (doc.getPost_title().equals(title)) found = doc.getPost_permalink();
        }
        return found;
    }
}
