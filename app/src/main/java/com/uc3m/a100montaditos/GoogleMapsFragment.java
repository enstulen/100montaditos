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
    //Api Key for google places
    final String GOOGLE_KEY = "AIzaSyCbkQQ_VTCL1UGJU2mixsdKdTnfyjHvZiU";

    Double latitude;
    Double longitude;

    final String radius = "100000";
    final String type = "restaurants";

    GoogleMap mMap;
    MapView mMapView;

    //widgets for search
    private EditText mSearchText;

    private final int REQUEST_PERMISSION_ACCESS_FINE_LOCATION = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //request permission of the user for accessing location
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);

        //instanciate the mapView
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(this);
        //instanciate the search
        mSearchText=(EditText) rootView.findViewById(R.id.search);
        //Method running the search job
        init();
        return rootView;
    }

    /**
     * Init() method that launches geoLocate method onclick ENTER
     */
    private void init(){
        Log.d(TAG, "init : initializing");
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //We want to perform search one the user has clicked ENTER
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

    /**
     * Geolocate accordingly to the {@code String} contained in
     * {@code mSearchText} it involves Geocoder to fetch {@code longitude}
     * and {@code latitude} from places
     *
     *
     *
     * @param
     */

    private void geoLocate(){
        Log.d(TAG, "geoLocate : Geolocating user");
        //retrieve string from
        String searchstring = mSearchText.getText().toString();
        Geocoder geocoder= new Geocoder(getContext());
        List<Address> list = new ArrayList<>();
        try{
            list=geocoder.getFromLocationName(searchstring,1);
        }catch (IOException e){
            Log.e(TAG, "geolocate: IOException "+e.getMessage());
        }
        //make sure geocoder has not returned empty search
        if (list.size()>0){
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: found a location "+address.toString());
            longitude = address.getLongitude();
            latitude = address.getLatitude();
            centerMap(latitude, longitude);
            GooglePlaces places = new GooglePlaces(new OnFinishedListener() {
                @Override
                void onFinished(ArrayList<GooglePlace> list) {
                    final ArrayList<GooglePlace> finalList = list;
                    //Runs the Json Parsing
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

    }

    /**
     * onMapReady is the method running the job when the map is displayed
     *
     *
     * @param googleMap the map instance of the previously defined GoogleMap class
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        //retrieve settings to display zoom controls and compass
        UiSettings settings = mMap.getUiSettings();
        settings.setZoomControlsEnabled(true);
        settings.setCompassEnabled(true);
        //center the map on current location
        getLocationAndCenterMap();
        //loop for ploting markers on the map
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

    /**
     *  getLocationAndCenterMap is used to retrieve user's location and center the map according
     *  to those parameters
     *
     */
    public void getLocationAndCenterMap() {
        // We first have to make sure the user granted the permission to access his location
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Enables the my-location layer.
            mMap.setMyLocationEnabled(true);
            LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            centerMap(latitude, longitude);
        }
    }

    /**
     * Center the map for a given lat,long couple.
     * zoom is x10
     *
     * @param latitude
     * @param longitude
     */
    public void centerMap(double latitude, double longitude) {

        LatLng position = new LatLng(latitude, longitude);
        CameraUpdate update;
        float zoom = 10;
        update = CameraUpdateFactory.newLatLngZoom(position, zoom);
        mMap.moveCamera(update);

    }

    /**
     * if location permission is granted then call getLocationAndCenterMap
     * @param requestCode
     * @param permissions
     * @param grantResults
     */

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

    /**
     * Creating an AsyncTask This class allows us to perform background operations
     * and publish results on the UI thread without having to manipulate threads or handlers.
     */
    public class GooglePlaces extends AsyncTask<View, Void, ArrayList<GooglePlace>> {
        List<String> listTitle;
        private OnFinishedListener mAfter;

        public GooglePlaces(OnFinishedListener after) {
            mAfter = after;
        }

        /**
         * invoked on the background thread immediately after onPreExecute() finishes executing.
         * This step is used to perform background computation that can take a long time.
         * The parameters of the asynchronous task are passed to this step.
         * The result of the computation must be returned by this step and will be passed back to the last step.
         * @param urls
         * @return
         */
        @Override
        protected ArrayList<GooglePlace> doInBackground(View... urls) {
            ArrayList<GooglePlace> temp;
            //print the call in the console
            System.out.println("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                    + latitude + "," + longitude + "&name=100montaditos" + "&radius=" + radius + "&type=" + type + "&sensor=true&key=" + GOOGLE_KEY);

            // make Call to the url
            temp = makeCall("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                    + latitude + "," + longitude + "&name=100montaditos" + "&radius=" + radius + "&type=" + type + "&sensor=true&key=" + GOOGLE_KEY);

            if (mAfter != null) {
                mAfter.onFinished(temp);
            }
            return temp;

        }

        @Override
        protected void onPreExecute() {
            // if one day a request takes more time we'll start a progress bar here
        }

        @Override
        protected void onPostExecute(ArrayList<GooglePlace> result) {
            // Here we update the user interface
            listTitle = new ArrayList<String>();

            for (int i = 0; i < result.size(); i++) {
                // Put the name, and the location of each restaurants returned by the request in a list
                listTitle.add(i, "Restaurant: " + result.get(i).getName() + "\nLatitude: " + result.get(i).getLatitude() + "\nLongitude:" + result.get(i).getLongitude());
            }


        }
    }

    /**
     * Method doing the checking the URL and doing the JSON Parsing
     * that is converting the json output from the googlePlace API
     * and put those results in and array list of googlePlace.
     *
     * @param stringURL
     * @return ArrayList<GooglePlace> an arraylist of googlePlace
     */
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
                    // search for the string "results"
                    if (name.equals("results")) {
                        // begin an object array
                        jsonReader.beginArray();
                        while (jsonReader.hasNext()) {
                            GooglePlace poi = new GooglePlace();
                            jsonReader.beginObject();
                            // begin an object
                            while (jsonReader.hasNext()) {
                                name = jsonReader.nextName();
                                if (name.equals("name")) {
                                    // if key "name" then keep the value
                                    poi.setName(jsonReader.nextString());
                                    System.out.println("PLACE NAME:" + poi.getName());
                                } else if (name.equals("geometry")) {
                                    // if key "geometry" then begin an object
                                    jsonReader.beginObject();
                                    while (jsonReader.hasNext()) {
                                        name = jsonReader.nextName();
                                        if (name.equals("location")) {
                                            // inside "geometry", if key "location" then begin an object
                                            jsonReader.beginObject();
                                            while (jsonReader.hasNext()) {
                                                name = jsonReader.nextName();
                                                // retrieve the values of "lat" and "long" from this object
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
