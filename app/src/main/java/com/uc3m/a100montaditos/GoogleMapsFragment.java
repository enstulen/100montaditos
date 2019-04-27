package com.uc3m.a100montaditos;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.JsonReader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

class GooglePlace {
    private String name;
    private String latitude;
    private String longitude;

    public GooglePlace() {
        this.name = "";
        this.latitude = "";
        this.longitude = "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

}

public class GoogleMapsFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "log" ;
    private static final Object DEFAULT_ZOOM = 10;
    final String GOOGLE_KEY = "AIzaSyCbkQQ_VTCL1UGJU2mixsdKdTnfyjHvZiU";

    Double latitude;
    Double longitude;

    final String radius = "1000000";
    final String type = "restaurants";

    GoogleMap mMap;
    MapView mMapView;

    //widgets
    private EditText mSearchText;

    private final int REQUEST_PERMISSION_ACCESS_FINE_LOCATION = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(this);
        mSearchText=(EditText) rootView.findViewById(R.id.search);
        init();
        return rootView;
    }

    private void init(){
        Log.d(TAG, "init : initializing");
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId== EditorInfo.IME_ACTION_SEARCH
                        || actionId==EditorInfo.IME_ACTION_DONE
                        || event.getAction()==KeyEvent.ACTION_DOWN
                        || event.getAction()==KeyEvent.KEYCODE_ENTER){
                    //execute our method for searching
                    geoLocate();
                }
                return false;
            }
        });

    }


    private void geoLocate(){
        Log.d(TAG, "geoLocate : Geolocating user");

        String searchstring = mSearchText.getText().toString();
        Geocoder geocoder= new Geocoder(getContext());
        List<Address> list = new ArrayList<>();
        try{
            list=geocoder.getFromLocationName(searchstring,1);
        }catch (IOException e){
            Log.e(TAG, "geolocate: IOException "+e.getMessage());
        }
        if (list.size()>0){
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: found a location "+address.toString());
            //Toast.makeText(this.getActivity(),address.toString(),Toast.LENGTH_SHORT).show();
            longitude = address.getLongitude();
            latitude = address.getLatitude();
            centerMap(latitude, longitude);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        UiSettings settings = mMap.getUiSettings();
        settings.setZoomControlsEnabled(true);
        settings.setCompassEnabled(true);

        getLocationAndCenterMap();

        GooglePlaces places = new GooglePlaces(new OnFinishedListener() {
            @Override
            void onFinished(ArrayList<GooglePlace> list) {
                final ArrayList<GooglePlace> finalList = list;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (GooglePlace gp : finalList) {
                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.valueOf(gp.getLatitude()), Double.valueOf(gp.getLongitude())))
                                    .title(gp.getName()));
                        }
                    }
                });


            }
        });
        places.execute();


    }

    public void getLocationAndCenterMap() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            centerMap(latitude, longitude);
        }
    }

    public void centerMap(double latitude, double longitude) {

        LatLng position = new LatLng(latitude, longitude);
        CameraUpdate update;
        float zoom = 10;
        update = CameraUpdateFactory.newLatLngZoom(position, zoom);
        mMap.moveCamera(update);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_ACCESS_FINE_LOCATION: {
                getLocationAndCenterMap();
                return;
            }

        }
    }

    private abstract class OnFinishedListener {
        abstract void onFinished(ArrayList<GooglePlace> list);
    }

    public class GooglePlaces extends AsyncTask<View, Void, ArrayList<GooglePlace>> {
        List<String> listTitle;
        private OnFinishedListener mAfter;

        public GooglePlaces(OnFinishedListener after) {
            mAfter = after;
        }


        @Override
        protected ArrayList<GooglePlace> doInBackground(View... urls) {
            System.out.println("appel a doInBackground");
            ArrayList<GooglePlace> temp;
            //print the call in the console
            System.out.println("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                    + latitude + "," + longitude + "&name=100montaditos" + "&radius=" + radius + "&type=" + type + "&sensor=true&key=" + GOOGLE_KEY);

            // make Call to the url
            temp = makeCall("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                    + latitude + "," + longitude + "&name=100montaditos" + "&radius=" + radius + "&type=" + type + "&sensor=true&key=" + GOOGLE_KEY);

            if (mAfter != null) {
                //you didnt illustrate what resultString is, you might
                //want this to be the returned value from doInBackground
                mAfter.onFinished(temp);
            }
            return temp;

        }

        @Override
        protected void onPreExecute() {
            // we can start a progress bar here
        }

        @Override
        protected void onPostExecute(ArrayList<GooglePlace> result) {
            // Aquí se actualiza el interfaz de usuario
            listTitle = new ArrayList<String>();

            for (int i = 0; i < result.size(); i++) {
                // make a list of the venus that are loaded in the list.
                // show the name, the category and the city
                listTitle.add(i, "Restaurant: " + result.get(i).getName() + "\nLatitude: " + result.get(i).getLatitude() + "\nLongitude:" + result.get(i).getLongitude());
            }


        }
    }

    public static ArrayList<GooglePlace> makeCall(String stringURL) {

        URL url = null;
        BufferedInputStream is = null;
        JsonReader jsonReader;
        ArrayList<GooglePlace> temp = new ArrayList<GooglePlace>();

        try {
            url = new URL(stringURL);
        } catch (Exception ex) {
            System.out.println("Malformed URL");
        }

        try {
            if (url != null) {
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                is = new BufferedInputStream(urlConnection.getInputStream());

            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.out.println("IOException");
        }

        if (is != null) {
            try {
                jsonReader = new JsonReader(new InputStreamReader(is, "UTF-8"));
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    String name = jsonReader.nextName();
                    // Busca la cadena "results"
                    if (name.equals("results")) {
                        // comienza un array de objetos
                        jsonReader.beginArray();
                        while (jsonReader.hasNext()) {
                            GooglePlace poi = new GooglePlace();
                            jsonReader.beginObject();
                            // comienza un objeto
                            while (jsonReader.hasNext()) {
                                name = jsonReader.nextName();
                                if (name.equals("name")) {
                                    // si clave "name" guarda el valor
                                    poi.setName(jsonReader.nextString());
                                    System.out.println("PLACE NAME:" + poi.getName());
                                } else if (name.equals("geometry")) {
                                    // Si clave "geometry" empieza un objeto
                                    jsonReader.beginObject();
                                    while (jsonReader.hasNext()) {
                                        name = jsonReader.nextName();
                                        if (name.equals("location")) {
                                            // dentro de "geometry", si clave "location" empieza un objeto
                                            jsonReader.beginObject();
                                            while (jsonReader.hasNext()) {
                                                name = jsonReader.nextName();
                                                // se queda con los valores de "lat" y "long" de ese objeto
                                                if (name.equals("lat")) {
                                                    poi.setLatitude(jsonReader.nextString());
                                                    System.out.println("PLACE LATITUDE:" + poi.getLatitude());
                                                } else if (name.equals("lng")) {
                                                    poi.setLongitude(jsonReader.nextString());
                                                    System.out.println("PLACE LONGITUDE:" + poi.getLongitude());
                                                } else {
                                                    jsonReader.skipValue();
                                                }
                                            }
                                            jsonReader.endObject();
                                        } else {
                                            jsonReader.skipValue();
                                        }
                                    }
                                    jsonReader.endObject();
                                } else {
                                    jsonReader.skipValue();
                                }
                            }
                            jsonReader.endObject();
                            temp.add(poi);
                        }
                        jsonReader.endArray();
                    } else {
                        jsonReader.skipValue();
                    }
                }
                jsonReader.endObject();
            } catch (Exception e) {
                System.out.println("Exception");
                return new ArrayList<GooglePlace>();
            }
        }

        return temp;
    }


}
