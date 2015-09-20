package me.syeds.ahead.util;

import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.widget.Toast;

import com.clover.sdk.v1.app.AppNotification;
import com.clover.sdk.v1.app.AppNotificationReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import me.syeds.ahead.DemoApplication;
import me.syeds.ahead.activity.MapActivity;

/**
 * Created by syedhaider on 9/20/15.
 */
public class NotificationReceiver extends AppNotificationReceiver {

    private final static String INTENTION_ACTION = "0";
    private final static String COME_OUT_ACTION = "1";
    private final static String ORDER_NUMBER = "order";
    private final static String ADDRESS = "address";

    private static NotificationListener listener;

    public static void setListener(NotificationListener listener) {
        NotificationReceiver.listener = listener;
    }


    @Override
    public void onReceive(Context context, AppNotification appNotification) {

        Pair<Integer, String> data = parseNotification(appNotification);
        if (data != null) {
            Intent intent = null;
            if (appNotification.appEvent.equals(INTENTION_ACTION)) {
                intent = new Intent(context, MapActivity.class);
                intent.putExtra(MapActivity.EXTRA_EVENT, MapActivity.INVENTORY_EVENT);
            } else if (appNotification.appEvent.equals(COME_OUT_ACTION)) {
                intent = new Intent(context, MapActivity.class);
                intent.putExtra(MapActivity.EXTRA_EVENT, MapActivity.COME_OUT_EVENT);
            } else {
                showError("Undeclared notification event");
            }

            if (intent != null) {
                intent.putExtra(MapActivity.EXTRA_ORDER_NUMBER, data.first);
                intent.putExtra(MapActivity.EXTRA_ADDRESS, data.second);
                if (listener == null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {
                    listener.setNewOrderOnMap(intent);
                }
            }
        }

    }

    private Pair<Integer, String> parseNotification(AppNotification notification) {
        try {
            String address;
            Integer orderNumber;
            JSONObject json = new JSONObject(notification.payload);

            if (json.has(ORDER_NUMBER)) {
                orderNumber = json.getInt(ORDER_NUMBER);
            } else {
                showError("Notification don;t have order number");
                return null;
            }

            if (json.has(ADDRESS)) {
                address = json.getString(ADDRESS);
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
        Toast.makeText(DemoApplication.getAppContext(), message, Toast.LENGTH_LONG).show();
    }
}
