package org.grameenfoundation.applabs.ledgerlinkmanager.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
public class Utils {


    public Utils() {
    }

    public final boolean isInternetOn(Context context) {

        // get connectivity Manager object
        ConnectivityManager connection = (ConnectivityManager)
                context.getSystemService(context.CONNECTIVITY_SERVICE);

        // check for network connections
        if (connection.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connection.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connection.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connection.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
            return true;

        } else if (
                connection.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connection.getNetworkInfo(1).getState() ==
                                android.net.NetworkInfo.State.DISCONNECTED) {
            return false;
        }
        return false;
    }
}
