package me.syeds.ahead.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.syeds.ahead.R;
import me.syeds.ahead.util.FakeBuyer;
import me.syeds.ahead.util.Order;
import me.syeds.ahead.util.TrackListener;
/**
 * Created by syedhaider on 9/20/15.
 */


public class OrderActivity extends Fragment implements TrackListener{

    public static final String EXTRA_ORDER = OrderActivity.class.getName() + ".ORDER";
    
    private FakeBuyer buyer;

    private TextView numberTxtView;
    private TextView desctiptionTxtView;
    private TextView arrivalTimeTxtView;
    private TextView distanceTxtView;
    private ImageView imageView;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_order_details, container, false);
        numberTxtView = (TextView)rootView.findViewById(R.id.txt_number);
        desctiptionTxtView = (TextView)rootView.findViewById(R.id.txt_order_desctiption);
        arrivalTimeTxtView = (TextView)rootView.findViewById(R.id.txt_arrival_time);
        distanceTxtView = (TextView)rootView.findViewById(R.id.txt_distance);
        imageView = (ImageView)rootView.findViewById(R.id.order_image);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        
        if (buyer != null) {
            
            updateLocation(buyer);
            
            buyer.addTrackListener(this);
            Order order = buyer.getOrder();
            
            if (order != null) {
                numberTxtView.setText(String.valueOf(order.getNumber()));
                desctiptionTxtView.setText(order.getDescription());
                //                arrivalTimeTxtView.setText("");
                //                distanceTxtView.setText("");
                imageView.setImageDrawable(order.getImage());
            }
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        
        if (buyer != null) {
            buyer.removeTrackListener(this);
        }
    }
    
    public FakeBuyer getBuyer() {
        return buyer;
    }
    
    public void setBuyer(FakeBuyer buyer) {
        this.buyer = buyer;
    }
    
    @Override
    public void updateLocation(FakeBuyer buyer) {
        
        if (buyer.getTrack() != null && !buyer.getTrack().isEmpty()) {
            
            /*
             //TODO: fix calculate expacted arrival time, its version just for demo
             // demo calculate speed
             List<Location> track = buyer.getTrack();
             Location currentLocation = buyer.getCurrentLocation();
             int currentPoint = track.indexOf(currentLocation);
             int points = track.size() - currentPoint;
             int addedSeconds = points * 2;
             Calendar now = GregorianCalendar.getInstance();
             now.add(Calendar.SECOND, addedSeconds);
             
             final Date arrivalTime = now.getTime();
             */
            
            /*
             // calculate distance
             List<Pair<Integer, Integer>> distanceList = buyer.getDistances();
             int accumPoint = 0;
             int completedDistance = 0;
             int totalDistance = 0;
             
             for (int i = 0; i < distanceList.size(); i++) {
             Pair<Integer, Integer> item = distanceList.get(i);
             totalDistance += item.second;
             accumPoint += item.first;
             if (currentPoint > accumPoint) {
             completedDistance += item.second;
             } else if (currentPoint > (accumPoint - item.first)) {
             // calculate distance beetwen points in one step
             int subDistance = item.second / item.first;
             int previousStepPoints = accumPoint - item.first;
             for (int j = 1; j < item.first; j++) {
             if (currentPoint > previousStepPoints + j) {
             completedDistance += subDistance;
             }
             }
             }
             }
             
             final int distance = totalDistance - completedDistance;
             */
            
            final Date arrivalTime  = buyer.getExpectedArrivalTime();
            final int distance = buyer.getRemainingDistance();
            
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss aa");
                    arrivalTimeTxtView.setText("Expected arrival time: " + df.format(arrivalTime));
                    distanceTxtView.setText("Distance: " + distance + " feets");
                }
            });
        }
        
    }
    
}
