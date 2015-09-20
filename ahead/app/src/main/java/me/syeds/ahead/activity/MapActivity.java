package me.syeds.ahead.activity;

import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import me.syeds.ahead.R;
import me.syeds.ahead.util.FakeBuyer;
import me.syeds.ahead.util.FakeOrderStorage;
import me.syeds.ahead.util.InterviewServerTask;
import me.syeds.ahead.util.LocationGenerator;
import me.syeds.ahead.util.NotificationListener;
import me.syeds.ahead.util.NotificationReceiver;
import me.syeds.ahead.util.Order;
import me.syeds.ahead.util.TrackListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

/**
 * Created by syedhaider on 9/20/15.
 */

public class MapActivity extends FragmentActivity implements TrackListener, OnMapReadyCallback , GoogleMap.OnInfoWindowClickListener, NotificationListener{

    public static final String EXTRA_EVENT = MapActivity.class.getName() + ".EVENT";
    public static final String EXTRA_ORDER_NUMBER = MapActivity.class.getName() + ".ORDER_NUMBER";
    public static final String EXTRA_ADDRESS =      MapActivity.class.getName() + ".ADDRESS";

    public static final int INVENTORY_EVENT = 0;
    public static final int COME_OUT_EVENT = 1;

    private Timer requestTimer;

    private GoogleMap mMap;
    private Map<FakeBuyer, Marker> mapOrderMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mapOrderMarker = new HashMap<>();
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        NotificationReceiver.setListener(this);

        requestTimer = new Timer();
        //--------- LINE FOR EMULATE COLOVER INTERACTION---------------------
        requestTimer.schedule(new InterviewServerTask("http://ahead.appswireless.com/check.php", this), 2000, 3000);
        //--------------------------------------------------------------------
    }

    @Override
    protected void onPause() {
        super.onPause();
        requestTimer.cancel();
        NotificationReceiver.setListener(null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setNewOrderOnMap(intent);
    }

    public void setNewOrderOnMap(Intent intent) {

        int event = intent.getIntExtra(EXTRA_EVENT, -1);
        int orderNumber = intent.getIntExtra(EXTRA_ORDER_NUMBER, 0);
        String startAddress = intent.getStringExtra(EXTRA_ADDRESS);
        if (event == INVENTORY_EVENT) {
            //Toast.makeText(this, "SUCCESS: inventory notification", Toast.LENGTH_LONG).show();

//            Order order = createOrderOnMap(orderNumber, startAddress, false);
//            Intent orderDetailsIntent = new Intent(this, OrderActivity.class);
//            orderDetailsIntent.putExtra(OrderActivity.EXTRA_ORDER, order);
//            startActivity(orderDetailsIntent);
//            OrderActivity fragment = new OrderActivity();
//            fragment.setOrder(order);
//            FragmentManager fm = getSupportFragmentManager();
//            fm.beginTransaction().add(R.id.layout_root, fragment).addToBackStack("details").commit();
            createOrderOnMap(orderNumber, startAddress, false);
        } else if (event == COME_OUT_EVENT) {
            //Toast.makeText(this, "SUCCESS: inventory notification", Toast.LENGTH_LONG).show();


            FakeBuyer buyer = null;
            for (FakeBuyer tempBuyer :mapOrderMarker.keySet()) {
                if (tempBuyer.getOrder().getNumber() == orderNumber) {
                    buyer = tempBuyer;
                    break;
                }
            }

            if (buyer != null) {
                buyer.startTrack(2000);
            } else {
                createOrderOnMap(orderNumber, startAddress, true);
            }
        }
    }

    private Order createOrderOnMap(int orderNumber, String address, boolean isMove) {
        Order order = FakeOrderStorage.getOrder(orderNumber);
        FakeBuyer fakeBuyer = new FakeBuyer();
        fakeBuyer.addTrackListener(this);
        fakeBuyer.setOrder(order);

        if (!isMove) {
            OrderActivity fragment = new OrderActivity();
            fragment.setBuyer(fakeBuyer);
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().add(R.id.layout_root, fragment).addToBackStack("details").commit();
        }

        LocationGenerator fakeRoute = new LocationGenerator(this,
                fakeBuyer,
                address,
                "420 22nd Street, San Francisco, CA 94107, USA");
        fakeRoute.execute(isMove);
        return order;
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
            mapFragment.getMapAsync(this);
            mMap = mapFragment.getMap();

        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        for(Map.Entry<FakeBuyer, Marker> item : mapOrderMarker.entrySet()) {
            if (item.getValue().getPosition().equals(marker.getPosition())) {
                OrderActivity fragment = new OrderActivity();
                fragment.setBuyer(item.getKey());
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction().add(R.id.layout_root, fragment).addToBackStack("details").commit();
            }
        }
    }

    @Override
    public void updateLocation(final FakeBuyer buyer) {

        Date arrivalTime  = buyer.getExpectedArrivalTime();
        int distance = buyer.getRemainingDistance();
        SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss aa");
        final String snippetText = "Dist: " + distance + " feets ("  + df.format(arrivalTime) + ")";


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Location location = buyer.getCurrentLocation();
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                Marker marker = mapOrderMarker.get(buyer);
                if (marker == null) {
                    Order order = buyer.getOrder();
                    marker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title("Order №" + order.getNumber())
                            .snippet(snippetText)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.user))
                            .visible(true));

                    mapOrderMarker.put(buyer, marker);
                    mMap.setOnInfoWindowClickListener(MapActivity.this);
                } else {
                    marker.setPosition(latLng);
                    marker.setSnippet(snippetText);
                    if (marker.isInfoWindowShown()) {
                        marker.hideInfoWindow();
                        marker.showInfoWindow();
                    }
                }
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        if (map != null) {
            LatLng latLng = new LatLng(37.758632, -122.384272);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
            map.animateCamera(cameraUpdate);

            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Store")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.store))
                    .visible(true));

        }
    }

}
