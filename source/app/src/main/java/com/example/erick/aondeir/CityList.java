package com.example.erick.aondeir;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Erick , Caroline , Isabela.
 */

public class CityList {
    private static List<City> cities = new ArrayList<City>();

    public static void makeCityList(){

        cities.add(new City("São josé dos Campos", "-23.17944,-45.88694", "10"));
        cities.add(new City("Gramado", "-29.3746,-50.8764", "25"));
        cities.add(new City("Ubatuba", "-23.43389,-45.07111", "50"));
        cities.add(new City("Florianópolis", "-27.593500,-48.558540", "75"));
        cities.add(new City("São Paulo", "-23.5489,-46.6388 23", "100"));
    }
    public List returnCitiesName(){
        List<String> found = new LinkedList<String>();
        makeCityList();
        for(City city: cities){
                found.add(city.getCityName());
        }
        return found;
    }
    public String returnGeoLocByCityName(String cityName){
        String found = new String();

        for(int i = 0; i < cities.size(); i++){
            if(cities.get(i).getCityName().equals(cityName)){
                found = cities.get(i).getGeoLocation();
            }
        }
        return found;
    }

    public List returnRanges(){
        List<String> found = new LinkedList<String>();
        makeCityList();
        for(City city: cities){
            found.add(city.getRange());
        }
        return found;
    }
}