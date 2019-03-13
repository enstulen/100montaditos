package com.uc3m.a100montaditos;

import android.Manifest;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsViewFragment extends Fragment {

    // This section is used to make sure the user have the required Play Service on his phone
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    public boolean isServiceOK() {
        Log.d(TAG, "isServiceOK : checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this.getActivity());
        if (available == ConnectionResult.SUCCESS) {
            // It's all good the user can use the app
            Log.d(TAG, "isServiceOK : Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            // An  error occured it is possible to resolve it
            Log.d(TAG, "isServiceOK : You have an error but we can fix it ;) ");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this.getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this.getActivity(), "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;

    }

    MapView mMapView;
    EditText mSearchText;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);


        mMapView.onResume(); // needed to get the map to display immediately
        mSearchText = (EditText) rootView.findViewById(R.id.search); //Get the searchBar

        init(); //launch the map search feature


        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                googleMap.setMyLocationEnabled(true);


                    System.out.println("get into marker");
                    // For dropping a marker at a point on the Map
                    LatLng sydney = new LatLng(-34, 151);
                    googleMap.addMarker(new MarkerOptions()
                            .position(sydney)
                            .title("Marker Title")
                            .snippet("Marker in Sidney")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            .draggable(false)
                            .visible(true));

                    // For zooming automatically to the location of the marker
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            });

            return rootView;
        }


        private void init(){
        Log.d(TAG, "init:initializing");
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId== EditorInfo.IME_ACTION_SEARCH || actionId== EditorInfo.IME_ACTION_DONE
                || actionId==KeyEvent.ACTION_DOWN || actionId== KeyEvent.KEYCODE_ENTER){
                    //execute the searching method
                    geoLocate();
                }
                return false;
            }
        });
        }

        private void geoLocate(){
            Log.d(TAG, "geoLocate : geolocating");
            String searchString = mSearchText.getText().toString();
            searchString=searchString;
            Geocoder geocoder = new Geocoder(getActivity());
            List<Address> list = new ArrayList<>();



            try {
                list=geocoder.getFromLocationName(searchString,10);
            }catch (IOException e){
                Log.d(TAG, "geoLocate : catch IOException: "+e.getMessage());
            }
            if (list.size() > 0){
                //Address address = list.get(0);
                //Log.d(TAG, "geoLocate : found a place: "+address.toString());
                for(int i = 0;i<list.size();i++)
                {
                    LatLng latLng = new LatLng(list.get(i).getLatitude() , list.get(i).getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(searchString);
                    googleMap.addMarker(markerOptions);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                }
            }
            googleMap.clear();
            EditText tf_location =  getActivity().findViewById(R.id.search);
            String location = tf_location.getText().toString();
            String school = "100 montaditos";
            double latitude,longitude;
            latitude = list.get(0).getLatitude();
            longitude = list.get(0).getLongitude();
            Object dataTransfer[] = new Object[2];
            GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
            String url = getUrl(latitude, longitude, school);

            dataTransfer[0] = googleMap;
            dataTransfer[1] = url;

            getNearbyPlacesData.execute(dataTransfer);
            Toast.makeText(getActivity(), "Showing Nearby Schools", Toast.LENGTH_SHORT).show();
        }


    private String getUrl(double latitude , double longitude , String nearbyPlace)
    {
        // https://maps.googleapis.com/maps/api/place/textsearch/xml?query=restaurants+in+Sydney&key=AIzaSyAsigS5hYBlU0gr9wakZmaGrrKOe3RMwNA
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+2000);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+"AIzaSyAsigS5hYBlU0gr9wakZmaGrrKOe3RMwNA");

        Log.d("MapsActivity", "url = "+googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }

        @Override
        public void onResume() {
            super.onResume();
            mMapView.onResume();
        }

        @Override
        public void onPause() {
            super.onPause();
            mMapView.onPause();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mMapView.onDestroy();
        }

        @Override
        public void onLowMemory() {
            super.onLowMemory();
            mMapView.onLowMemory();
        }
    }




