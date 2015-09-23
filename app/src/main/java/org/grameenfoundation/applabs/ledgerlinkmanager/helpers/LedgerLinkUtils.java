package org.grameenfoundation.applabs.ledgerlinkmanager.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
public class LedgerLinkUtils {


    public LedgerLinkUtils() {
    }

    public final boolean isInternetOn(Context context) {

        /** get Connectivity Manager object to check connection*/
        ConnectivityManager connection = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

        /** Check for network connections*/
        if (connection.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connection.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connection.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connection.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
            return true;

        } else if (
                connection.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connection.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
            return false;
        }
        return false;
    }
}
