package me.syeds.ahead.util;

import android.app.Activity;
import android.content.Intent;
import android.util.Pair;
import android.widget.Toast;

import me.syeds.ahead.DemoApplication;
import me.syeds.ahead.activity.MapActivity;
import com.clover.sdk.v1.app.AppNotification;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.TimerTask;
/**
 * Created by syedhaider on 9/20/15.
 */
public class InterviewServerTask extends TimerTask {

    private final static String INTENTION_ACTION = "0";
    private final static String COME_OUT_ACTION = "1";
    private final static String EVENT = "event";
    private final static String DATA = "data";
    private final static String ORDER_NUMBER = "order";
    private final static String ADDRESS = "address";

    private String lastResponse;
    private String url;
    private MapActivity activity;
    private Intent intent;

    public InterviewServerTask(String url, MapActivity activity) {
        this.url = url;
        this.activity = activity;
    }

    @Override
    public void run() {
        String newResponse = getResponseFromUrl(url);
        if (lastResponse == null || !lastResponse.equals(newResponse)) {
            lastResponse = newResponse;
            try {
                String eventStr, dataStr;
                JSONObject json = new JSONObject(lastResponse);
                eventStr = json.getString(EVENT);
                dataStr = json.getString(DATA);
                Pair<Integer, String> data = parseNotification(dataStr);

                if (data != null) {
                    intent = null;
                    if (eventStr.equals(INTENTION_ACTION)) {
                        intent = new Intent(DemoApplication.getAppContext(), MapActivity.class);
                        intent.putExtra(MapActivity.EXTRA_EVENT, MapActivity.INVENTORY_EVENT);

                        //sorry for this uglycode, its fast fix instead push notification from clover

                    } else if (eventStr.equals(COME_OUT_ACTION)) {
                        intent = new Intent(DemoApplication.getAppContext(), MapActivity.class);
                        intent.putExtra(MapActivity.EXTRA_EVENT, MapActivity.COME_OUT_EVENT);

                        //sorry for this uglycode, its fast fix instead push notification from clover

                    } else {
                        showError("Undeclared notification event");
                    }

                    if (intent != null) {
                        intent.putExtra(MapActivity.EXTRA_ORDER_NUMBER, data.first);
                        intent.putExtra(MapActivity.EXTRA_ADDRESS, data.second);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                activity.setNewOrderOnMap(intent);
                            }
                        });

                    }
                }

            } catch (JSONException ex) {
                ex.printStackTrace();
            }



        }
    }

    public String getResponseFromUrl(String url) {
        String content = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            content = EntityUtils.toString(httpEntity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    private Pair<Integer, String> parseNotification(String  data) {
        if (data == null) return null;

        try {
            String address;
            Integer orderNumber;

            JSONObject jsonData = new JSONObject(data);
            if (jsonData.has(ORDER_NUMBER)) {
                orderNumber = jsonData.getInt(ORDER_NUMBER);
            } else {
                showError("Notification don;t have order number");
                return null;
            }

            if (jsonData.has(ADDRESS)) {
                address = jsonData.getString(ADDRESS);
            } else {
                showError("Notification don't have address");
                return null;
            }

            return Pair.create(orderNumber, address);
        } catch (JSONException e) {
            e.printStackTrace();
            showError(e.getMessage());
            return null;
        }
    }

    private void showError(String message) {
        //Toast.makeText(DemoApplication.getAppContext(), message, Toast.LENGTH_LONG).show();
    }
}

