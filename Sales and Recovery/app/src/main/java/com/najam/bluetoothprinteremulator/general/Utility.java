package com.najam.bluetoothprinteremulator.general;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.najam.bluetoothprinteremulator.R;

import static android.content.Context.MODE_PRIVATE;
import static com.najam.bluetoothprinteremulator.general.Constants.BT_ADDRESS;
import static com.najam.bluetoothprinteremulator.general.Constants.BT_DEVICE_DATA;
import static com.najam.bluetoothprinteremulator.general.Constants.PRINT_BALANCE_DATA;
import static com.najam.bluetoothprinteremulator.general.Constants.TO_PRINT_BALANCE;
import static com.najam.bluetoothprinteremulator.general.Constants.agentCodePrefsTitle;
import static com.najam.bluetoothprinteremulator.general.Constants.urlAndCodePrefsName;
import static com.najam.bluetoothprinteremulator.general.Constants.urlPrefsTitle;

/**
 * Created on 1/2/2019.
 *
 * @author Uwais Alam
 */
public class Utility {
    public static Context context;
    private static Utility utility;
    private static ProgressDialog pDialog = null;

    private Utility() {

    }

    public static Utility getInstance() {
        if (utility == null) {
            utility = new Utility();
        }
        return utility;
    }

    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s).substring(0, n - 1);
    }

    public static String padLeft(String s, int n) {

        return new StringBuffer(new StringBuffer(String.format("%1$" + n + "s", s)).reverse().toString().substring(0, n - 1)).reverse().toString();
    }

    public static void saveAgentInfoToPrefs(String url, String agentCode, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(urlAndCodePrefsName, MODE_PRIVATE).edit();
        editor.putString(urlPrefsTitle, url);
        editor.putString(agentCodePrefsTitle, agentCode);
        editor.apply();
    }

    public static String getAgentCode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(urlAndCodePrefsName, MODE_PRIVATE);
        return prefs.getString(agentCodePrefsTitle, "");
    }

    public static String getBaseUrl(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(urlAndCodePrefsName, MODE_PRIVATE);
        return prefs.getString(urlPrefsTitle, "");
    }

    public static void showProgressbar(Context context, String message) {
        try {
            if (pDialog != null) {
                if (!pDialog.isShowing()) {
                    pDialog = new ProgressDialog(context);
                    pDialog.setMessage(message);
                    pDialog.setIndeterminate(true);
                    pDialog.setCancelable(false);
                    pDialog.show();
                }
            } else {
                pDialog = new ProgressDialog(context);
                pDialog.setMessage(message);
                pDialog.setIndeterminate(true);
                pDialog.setCancelable(false);
                pDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideProgressbar() {
        try {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public static void AlertDialogMessage(Context context, String message) {
        try {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dlg_message);

            TextView msg = dialog.findViewById(R.id.body);
            msg.setText(message);

            View ok = dialog.findViewById(R.id.ok_click_wrapper);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            if (!dialog.isShowing()) {
                dialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void AlertDialogMessageWithCallbacks(Context context, String message, final DialogCallBacks callBacks) {
        try {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dlg_callback_message);

            TextView msg = dialog.findViewById(R.id.body);
            msg.setText(message);

            View ok = dialog.findViewById(R.id.ok_click_wrapper);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    callBacks.OnOkPressed();
                }
            });

            View cancel = dialog.findViewById(R.id.cancel_click_wrapper);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            if (!dialog.isShowing()) {
                dialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean IS_INTERNET_AVAILABLE(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = null;
        if (cm != null) {
            info = cm.getActiveNetworkInfo();
        }
        return info != null && info.isConnected();
    }

    public static double getValidDouble(String value) {
        double qty = 1;
        try {
            qty = Double.parseDouble(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return qty;
    }

    public static double getDouble(String value) {
        double qty = 0;
        try {
            qty = Double.parseDouble(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return qty;
    }

    public static double getDiscountDouble(String value) {
        double qty = 0;
        if (!value.equals("")) {
            try {
                qty = Double.parseDouble(value) / 100;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return qty;
    }

    public static String getJSONFileName() {
        return "SalesAndRecoveryData.json";
    }

    public static String getNewJSONFileName() {
        return "NewSalesAndRecoveryData.json";
    }

    public static String getAppDirectoy() {
        return "SalesAndRecovery";
    }

    public static void showToast(String s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }

    public static String getBluetoothDevice() {
        SharedPreferences prefs = context.getSharedPreferences(BT_DEVICE_DATA, MODE_PRIVATE);
        return prefs.getString(BT_ADDRESS, "");
    }

    public static void saveBluetoothDevice(String address, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(BT_DEVICE_DATA, MODE_PRIVATE).edit();
        editor.putString(BT_ADDRESS, address);
        editor.apply();
    }

    public static void savePrintBalancePreference(boolean b) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PRINT_BALANCE_DATA, MODE_PRIVATE).edit();
        editor.putBoolean(TO_PRINT_BALANCE, b);
        editor.apply();
    }

    public static boolean getPrintBalancePreference() {
        SharedPreferences prefs = context.getSharedPreferences(PRINT_BALANCE_DATA, MODE_PRIVATE);
        return prefs.getBoolean(TO_PRINT_BALANCE, false);
    }

    public static String addLeadingZores(int number) {
        if (number < 10) {
            return "00000" + number;
        } else if (number < 100) {
            return "0000" + number;
        } else if (number < 1000) {
            return "000" + number;
        } else if (number < 10000) {
            return "00" + number;
        } else if (number < 100000) {
            return "0" + number;
        } else {
            return "" + number;
        }
    }

    public interface DialogCallBacks {
        void OnOkPressed();
    }
}
