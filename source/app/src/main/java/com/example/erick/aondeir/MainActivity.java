package com.example.erick.aondeir;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.String;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DocList docs = new DocList();
    private CityList cityList = new CityList();
    private Spinner spnCities;
    private String range = "50";
    private String strGeoLoc = "";
    private Location location;
    private LocationManager locationManager;
    private Address address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        double latitude = 0.0;
        double longitude = 0.0;

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //System.out.println("teste 'Permission!'");
        }else{
            locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        if(location != null){
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            strGeoLoc = latitude + "," + longitude;
        }else{
            strGeoLoc = "-23.17944,-45.88694";
        }

        Bitmap imgAux = null;
        try{
            URL url = new URL("http://www.windowsclub.com.br/wp-content/uploads/2015/09/catraca-livre.jpg");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream =  httpURLConnection.getInputStream();
            imgAux = BitmapFactory.decodeStream(inputStream);
        }catch(IOException e){
            System.out.println("teste: Falhou image_thumbnail download!");
        }
        ImageView img = (ImageView)findViewById(R.id.imgIco);
        img.setImageBitmap(imgAux);

        Date date = new Date();

        spnCities = (Spinner) findViewById(R.id.spCities);
        //Cria um ArrayAdapter usando um padrão de layout da classe R do android, passando o ArrayList nomes
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, cityList.returnRanges());
        ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spnCities.setAdapter(spinnerArrayAdapter);

        spnCities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                range = String.valueOf(spnCities.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void onClickEventSearch(View view) {

        final Spinner spinnerEvent;
        Date date = new Date();
        final List<String> titles = docs.returnAllPost_Title(strGeoLoc, date, range);

            spinnerEvent = (Spinner) findViewById(R.id.chooseEvent);
            //Cria um ArrayAdapter usando um padrão de layout da classe R do android, passando o ArrayList nomes
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, titles);
            ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            spinnerEvent.setAdapter(spinnerArrayAdapter);

        spinnerEvent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                //String title = String.valueOf(spinnerEvent.getSelectedItem());
                String place = docs.returnPlaceByTitle(String.valueOf(spinnerEvent.getSelectedItem()));
                TextView tvPlaceName = (TextView) findViewById(R.id.tvPlaceName);
                tvPlaceName.setText(place);

                String bairro = docs.returnNeighborhoodByTitle(String.valueOf(spinnerEvent.getSelectedItem()));
                TextView tvBairro = (TextView) findViewById(R.id.tvBairro);
                tvBairro.setText(bairro);

                Bitmap imgAux = docs.returnImageIco(String.valueOf(spinnerEvent.getSelectedItem()));
                ImageView img = (ImageView)findViewById(R.id.imgIco);
                img.setImageBitmap(imgAux);

                ImageView iv = (ImageView) findViewById(R.id.imgIco);
                iv.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Uri uri = Uri.parse(docs.returnPost_permalink(String.valueOf(spinnerEvent.getSelectedItem())));
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.setData(Uri.parse(String.valueOf(uri)));
                        startActivity(intent);
                    }
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }
}
