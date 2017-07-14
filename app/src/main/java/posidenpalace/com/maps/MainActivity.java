package posidenpalace.com.maps;

import android.Manifest;
import android.app.IntentService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements android.location.LocationListener{

    private static final int LOCATION = 1;
    private static final String TAG ="test";
    private FusedLocationProviderClient local;
    private Location current;
    private EditText street;
    private EditText city;
    private EditText state;
    private EditText longe;
    private EditText lat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        street = (EditText) findViewById(R.id.etStreet);
        city = (EditText) findViewById(R.id.etCity);
        state = (EditText) findViewById(R.id.etState);
        lat = (EditText) findViewById(R.id.etLat);
        longe = (EditText) findViewById(R.id.etLong);


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate: no premission");

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d(TAG, "onCreate: explain");


            } else {

                // No explanation needed, we can request the permission.
                Log.d(TAG, "onCreate: no explain");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION);

            }
        }

        LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("Loctaion is Not on");
            dialog.setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Leave it off", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }
        getLocation();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: granted" );


                } else {
                    Log.d(TAG, "onRequestPermissionsResult: Denyed ");

                }
                return;
            }



            // other 'case' lines to check for other
            // permissions this app might request
        }

    }

    @SuppressWarnings("MissingPermission")
    private void getLocation() {
        local = LocationServices.getFusedLocationProviderClient(this);
        local.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        current= location;
                        Log.d(TAG, "onSuccess: " + location);
                    }
                });
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: " +location);
        getLocation();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "onStatusChanged: ");
        switch (status)
        {

        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "onProviderEnabled:");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "onProviderDisabled: ");
    }

    public void map(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("location", current);
        startActivity(intent);
    }

    public void rest(View view) {
        String address = street.getText().toString()
                + " " + city.getText().toString()
                + " " + state.getText().toString();

        Log.d(TAG, "geocodeAddress: "+ address);

        OkHttpClient okHttpClient = new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("maps.googleapis.com")
                .addPathSegment("maps")
                .addPathSegment("api")
                .addPathSegment("geocode")
                .addPathSegment("json")
                .addQueryParameter("address", address)
                .addQueryParameter("key", "AIzaSyA-a8S35baJwQTXpzNoQGzqSiL6P3o3D7A")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Log.d(TAG, "onFailure: "+ e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Log.d(TAG, "onResponse: " + response.body().string());
            }
        });

    }

    public void findAddress(View view) {
        String address = lat.getText().toString()
                + " " + longe.getText().toString();

        Log.d(TAG, "geocodeAddress: "+ address);

        OkHttpClient okHttpClient = new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("maps.googleapis.com")
                .addPathSegment("maps")
                .addPathSegment("api")
                .addPathSegment("geocode")
                .addPathSegment("json")
                .addQueryParameter("latlng", address)
                .addQueryParameter("key", "AIzaSyA-a8S35baJwQTXpzNoQGzqSiL6P3o3D7A")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Log.d(TAG, "onFailure: "+ e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Log.d(TAG, "onResponse: " + response.body().string());
            }
        });

    }




}
