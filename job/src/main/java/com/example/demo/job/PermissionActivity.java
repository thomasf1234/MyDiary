package com.example.demo.job;

import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tfisher on 27/10/2016.
 */

public class PermissionActivity extends AppCompatActivity {
    protected Map<Integer,PermissionJob> permissionRequestJobMap;

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (permissionRequestJobMap.containsKey(requestCode)) {
            PermissionJob permissionJob = permissionRequestJobMap.get(requestCode);
            if (permissionJob.hasPermission())
                try {
                    permissionJob.perform();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    public void triggerJob(Job job) throws Exception {
        if(job instanceof PermissionJob) {
            PermissionJob permissionJob = (PermissionJob) job;
            if (permissionJob.hasPermission())
                permissionJob.perform();
            else {
                setPermissionRequestJobMap(permissionJob.getPermissionRequestId(), permissionJob);
                permissionJob.requestPermission();
            }
        } else
            job.perform();
    }

    private void setPermissionRequestJobMap(int permissionRequestId, PermissionJob permissionJob) {
        if (this.permissionRequestJobMap == null)
            this.permissionRequestJobMap = new HashMap<>();
        this.permissionRequestJobMap.put(permissionRequestId, permissionJob);
    }
}
