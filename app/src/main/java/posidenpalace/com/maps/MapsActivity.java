package posidenpalace.com.maps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Reciver reciver = new Reciver();
    IntentFilter filter = new IntentFilter();
    Location current;
    EditText lat;
    EditText lng;
    EditText street;
    EditText city;
    EditText state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        current = getIntent().getParcelableExtra("location");

        lat = (EditText) findViewById(R.id.etMLat);
        lng = (EditText) findViewById(R.id.etMLong);
        city = (EditText) findViewById(R.id.etMCity);
        street = (EditText) findViewById(R.id.etMStreet);
        state= (EditText) findViewById(R.id.etMState);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.style_json));
        // Add a marker in Sydney and move the camera
        LatLng now = new LatLng(current.getLatitude(), current.getLongitude());
        mMap.addMarker(new MarkerOptions().position(now).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(now));
    }

    public void byAddress(View view) {
        String address = street.getText().toString() +" "+
                city.getText().toString() + " " +
                state.getText().toString();
        Intent find = new Intent(this,GeoService.class);
        find.putExtra("address", address);
        startService(find);
    }

    public void findAddress(View view) {
        double[] latlng = new double[]{Double.parseDouble(lat.getText().toString()), Double.parseDouble(lng.getText().toString())};
        Intent find = new Intent(this,GeoService.class);
        find.putExtra("latlng", latlng);
        startService(find);
    }

    @Override
    protected void onStart() {
        super.onStart();
        filter.addAction("map");
        registerReceiver(reciver,filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(reciver);
    }

    class Reciver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            Address a = bundle.getParcelable("map");
            LatLng changed = new LatLng(a.getLatitude(),a.getLongitude());
            mMap.addMarker(new MarkerOptions().position(changed).title(a.getLocality()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(changed));
        }
    }
}
