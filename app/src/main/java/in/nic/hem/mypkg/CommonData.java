package in.nic.hem.mypkg;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class CommonData {
    private static boolean askPermisionRequired;

    public static boolean isAskPermisionRequired() {
        return askPermisionRequired;
    }

    public static void setAskPermisionRequired(boolean askPermisionRequired) {
        CommonData.askPermisionRequired = askPermisionRequired;
    }
    public boolean clearPermissionRequest(Context context, Activity activity)
    {
        AppPermission appPermission = new AppPermission(context,activity);
        boolean flag = true;
        boolean PERMISSIONS = appPermission.checkPermissions(flag);
        if (!PERMISSIONS) {
            PERMISSIONS = appPermission.checkPermissions(false);
            Toast.makeText(context, "GPS and Camera Access Permission Denied.... Give Permision to use this feature", Toast.LENGTH_LONG).show();
        }
        return PERMISSIONS;
    }
}
