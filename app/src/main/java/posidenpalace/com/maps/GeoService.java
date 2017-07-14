package posidenpalace.com.maps;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GeoService extends IntentService{


    public GeoService() {
        super("GeoService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String address;
        address = intent.getStringExtra("address");
        double[] latlng = intent.getDoubleArrayExtra("latlng");
        if (address != null){
            try {
                HandleString(address);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            try {
                HanleDoubles(latlng[0],latlng[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void HanleDoubles(double la, double lo) throws IOException {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses = geocoder.getFromLocation(la,lo,3);
        Address a = addresses.get(0);
        Bundle bundle = new Bundle();
        bundle.putParcelable("map", a);
        Intent send = new Intent("map");
        send.putExtras(bundle);
        sendBroadcast(send);

    }

    public void HandleString(String s) throws IOException {
        Geocoder geocoder = new Geocoder(this);
        Bundle bundle = new Bundle();
        List<Address>  addresses = geocoder.getFromLocationName(s, 3);
        Address a = addresses.get(0);
        bundle.putParcelable("map", a);
        Intent send = new Intent("map");
        send.putExtras(bundle);
        sendBroadcast(send);
    }
}
