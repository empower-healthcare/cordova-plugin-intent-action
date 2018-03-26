package ngocdaothanh.cordova.plugins;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class IntentActionPlugin extends CordovaPlugin {
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("startIntentAction")) {
            String intentAction = args.getString(0);

            if (intentAction.equals("android.settings.APP_NOTIFICATION_SETTINGS")) {
                Intent intent = new Intent();
                Context context = this.cordova.getActivity().getApplicationContext();
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.N_MR1) {
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
                } else if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("app_package", context.getPackageName());
                    intent.putExtra("app_uid", context.getApplicationInfo().uid);
                } else {
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package:" + context.getPackageName()));
                }
                this.cordova.getActivity().startActivity(intent);
            } else {
                Intent intent = new Intent(intentAction);
                if (args.length() == 2) {
                    setOptions(intent, args.getString(1));
                }
                this.cordova.getActivity().startActivity(intent);
            }

            callbackContext.success(intentAction);
            return true;
        }
        return false;
    }

    private void setOptions(Intent intent, String options) throws JSONException {
        JSONObject jsonObject = new JSONObject(options);

        if (jsonObject.has("extras")) {
            JSONObject extras = jsonObject.getJSONObject("extras");
            setExtras(intent, extras);
        }

        if (jsonObject.has("data")) {
            String uriString = jsonObject.getString("data");
            Uri data = Uri.parse(uriString);

            if (jsonObject.has("type")) {
                String type = jsonObject.getString("type");
                intent.setDataAndType(data, type);
            } else {
                intent.setData(data);
            }
        }
    }

    private void setExtras(Intent intent, JSONObject extras) throws JSONException {
        JSONArray names = extras.names();
        for (int i = 0; i < names.length(); i++) {
            String name = names.getString(i);
            Serializable value = (Serializable) extras.get(name);
            intent.putExtra(name, value);
        }
    }
}
