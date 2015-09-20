package me.syeds.ahead.util;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Pair;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by syedhaider on 9/20/15.
 */

public class LocationGenerator extends AsyncTask<Boolean, Integer, Boolean>{

    private static final String TOAST_MSG = "Calculate course of buyer";
    private static final String TOAST_ERR_MAJ = "Impossible calculate course";

    private Context context;
    private FakeBuyer buyer;
    private String editDepart;
    private String editArrivee;
    private final List<Location> route;
    private List<Pair<Integer, Integer>>routeDistance;
    private boolean isStart;

    public LocationGenerator(final Context context, final FakeBuyer buyer, final String editDepart, final String editArrivee)
    {
        this.route = new ArrayList<>();
        this.routeDistance = new ArrayList<>();
        this.context = context;
        this.buyer = buyer;
        this.editDepart = editDepart;
        this.editArrivee = editArrivee;
    }

    @Override
    protected void onPreExecute()
    {
        Toast.makeText(context, TOAST_MSG, Toast.LENGTH_LONG).show();
    }

    @Override
    protected Boolean doInBackground(Boolean... params)
    {
        if (params != null) {
            isStart = params[0];
        }

        try
        {
            final StringBuilder url = new StringBuilder("http://maps.googleapis.com/maps/api/directions/xml?sensor=false&language=en");
            url.append("&origin=");
            url.append(editDepart.replace(' ', '+'));
            url.append("&destination=");
            url.append(editArrivee.replace(' ', '+'));

            final InputStream stream = new URL(url.toString()).openStream();

            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setIgnoringComments(true);

            final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            final Document document = documentBuilder.parse(stream);
            document.getDocumentElement().normalize();

            final String status = document.getElementsByTagName("status").item(0).getTextContent();
            if(!"OK".equals(status))
            {
                return false;
            }

            final Element elementLeg = (Element) document.getElementsByTagName("leg").item(0);
            final NodeList nodeListStep = elementLeg.getElementsByTagName("step");
            final int length = nodeListStep.getLength();

            for(int i=0; i<length; i++)
            {
                final Node nodeStep = nodeListStep.item(i);
                if(nodeStep.getNodeType() == Node.ELEMENT_NODE)
                {
                    final Element elementStep = (Element) nodeStep;

                    int countPoint = decodePolylines(elementStep.getElementsByTagName("points").item(0).getTextContent());
                    Element distanceElm = (Element) elementStep.getElementsByTagName("distance").item(0);
                    String distanceStr = distanceElm.getElementsByTagName("value").item(0).getTextContent();
                    int distance = Integer.parseInt(distanceStr);
                    Pair<Integer, Integer> distancePair = Pair.create(countPoint, distance);
                    routeDistance.add(distancePair);

                }
            }
            return true;
        }
        catch(final Exception e)
        {
            return false;
        }
    }

    private void decodeTime(final String time) {

    }


    /** METHODE QUI DECODE LES POINTS EN LAT-LONG**/
    private int decodePolylines(final String encodedPoints)
    {
        int index = 0;
        int lat = 0, lng = 0;

        int countPoints = 0;

        while (index < encodedPoints.length())
        {
            int b, shift = 0, result = 0;

            do
            {
                b = encodedPoints.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;

            do
            {
                b = encodedPoints.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            //new LatLng((double)lat/1E5, (double)lng/1E5)
            Location location = new Location("");
            location.setLatitude((double)lat/1E5);
            location.setLongitude((double)lng/1E5);
            route.add(location);
            countPoints++;
        }

        return countPoints;
    }

    @Override
    protected void onPostExecute(final Boolean result)
    {
        if(!result)
        {
            Toast.makeText(context, TOAST_ERR_MAJ, Toast.LENGTH_SHORT).show();
        }
        else
        {
            buyer.setDistances(routeDistance);
            buyer.setTrack(route);
            if (isStart) {
                buyer.startTrack(2000);
            } else {
                buyer.showCurrentPosition();
            }
        }
    }
}

