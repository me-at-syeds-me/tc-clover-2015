package me.syeds.ahead.util;

import android.location.Location;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FakeBuyer{
    
    private Timer trackTimer;
    private List<Location> track;
    private List<Pair<Integer, Integer>> distances;
    private Location currentLocation;
    private int currentPosition;
    private List<TrackListener>listeners;
    private Order order;
    
    public FakeBuyer() {
        track = new ArrayList<Location>();
        listeners = new ArrayList<TrackListener>();
        currentPosition = 0;
    }
    
    public void setTrack(List<Location> track) {
        this.track = track;
    }
    
    public List<Location> getTrack() {
        return this.track;
    }
    
    public void addTrackListener(TrackListener listener) {
        listeners.add(listener);
    }
    
    public void removeTrackListener(TrackListener listener) {
        listeners.remove(listener);
    }
    
    public List<TrackListener> getAllTrackListener() {
        return listeners;
    }
    
    public Location getCurrentLocation() {
        if (currentLocation == null) {
            currentLocation = track.get(currentPosition);
        }
        return currentLocation;
    }
    
    public void startTrack(int speed) {
        trackTimer = new Timer();
        trackTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (track.size() > currentPosition) {
                    currentLocation = track.get(currentPosition);
                    currentPosition++;
                    for (TrackListener listener : listeners) {
                        listener.updateLocation(FakeBuyer.this);
                    }
                } else {
                    trackTimer.cancel();
                }
            }
        },
                            0,
                            speed);
    }
    
    public void stopTrack() {
        if (trackTimer != null) {
            trackTimer.cancel();
        }
        currentPosition = 0;
    }
    
    public void showCurrentPosition() {
        for (TrackListener listener : listeners) {
            listener.updateLocation(FakeBuyer.this);
        }
    }
    
    public Date getExpectedArrivalTime() {
        //TODO: fix calculate expacted arrival time, its version just for demo
        Location currentLocation = getCurrentLocation();
        int currentPoint = track.indexOf(currentLocation);
        int points = track.size() - currentPoint;
        int addedSeconds = points * 2;
        Calendar now = GregorianCalendar.getInstance();
        now.add(Calendar.SECOND, addedSeconds);
        return now.getTime();
        
    }
    
    public int getRemainingDistance() {
        //TODO: fix calculate remaining distance, its version just for demo
        Location currentLocation = getCurrentLocation();
        int currentPoint = track.indexOf(currentLocation);
        int accumPoint = 0;
        int completedDistance = 0;
        int totalDistance = 0;
        
        for (int i = 0; i < distances.size(); i++) {
            Pair<Integer, Integer> item = distances.get(i);
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
        
        return totalDistance - completedDistance;
        
    }
    
    
    public Order getOrder() {
        return order;
    }
    
    public void setOrder(Order order) {
        this.order = order;
    }
    
    public void setDistances(List<Pair<Integer, Integer>> distances) {
        this.distances = distances;
    }
}
