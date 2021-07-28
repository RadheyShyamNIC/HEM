package in.nic.hem.mypkg;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class AppPermission extends AppCompatActivity {
    Context context;
    Activity activity;

    public AppPermission(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }
    public boolean  checkPermissions(boolean flag)
    {
        int PERMISSION_ALL = 1;
        boolean returnData = false;
        String[]  PERMISSIONS = {
                android.Manifest.permission.CAMERA
        };
        returnData = hasPermissions(PERMISSIONS);
        if(!returnData && flag){
            ActivityCompat.requestPermissions(activity, PERMISSIONS, PERMISSION_ALL);
            returnData = hasPermissions(PERMISSIONS);
        }
        return returnData;
    }
    public boolean hasPermissions(String... PERMISSIONS) {
        if (context != null && PERMISSIONS != null) {
            for (String permission : PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    public boolean  checkPermissionsFileAccess(boolean flag)
    {
        int PERMISSION_ALL = 1;
        boolean returnData = false;
        String[]  PERMISSIONS = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        returnData = hasPermissionsFileAccess(PERMISSIONS);
        if(!returnData && flag){
            ActivityCompat.requestPermissions(activity, PERMISSIONS, PERMISSION_ALL);
            returnData = hasPermissionsFileAccess(PERMISSIONS);
        }
        return returnData;
    }
    public boolean hasPermissionsFileAccess(String... PERMISSIONS) {
        if (context != null && PERMISSIONS != null) {
            for (String permission : PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
