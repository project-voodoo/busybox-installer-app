
package org.projectvoodoo.busyboxinstaller.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.widget.Toast;

public class Utils {

    private static final String TAG = "Voodoo BusyboxInstaller Utils";
    public static final String scriptFileName = "commands.sh";

    public static final ArrayList<String> runCommand(String command) {

        ArrayList<String> lines = new ArrayList<String>();

        try {
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while ((line = in.readLine()) != null)
                lines.add(line);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error when running: " + command);
        }

        return lines;
    }

    public static ArrayList<String> runScript(Context context, String content, Boolean withSu) {

        ArrayList<String> output = new ArrayList<String>();
        String command;
        Process process;

        String scriptFullPath = context.getFileStreamPath(scriptFileName).getAbsolutePath();

        try {
            FileOutputStream fos = context.openFileOutput(scriptFileName, Context.MODE_PRIVATE);
            fos.write(content.getBytes());
            fos.close();

            command = "chmod 700 " + context.getFileStreamPath(scriptFileName);
            Runtime.getRuntime().exec(command).waitFor();

            // set executable permissions
            command = scriptFullPath;
            if (withSu)
                command = "su -c " + command;
            else
                command = "/system/bin/sh -c " + command;
            process = Runtime.getRuntime().exec(command);
            process.waitFor();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = in.readLine()) != null)
                output.add(line);

            // delete script file
            new File(scriptFullPath).delete();

        } catch (Exception e) {
            Log.d(TAG, "Unable to run script: " + scriptFullPath);

            String errorText = "Unable to run internal script";
            if (withSu)
                errorText += " with superuser";
            e.printStackTrace();
            try {
                Toast.makeText(context, errorText, Toast.LENGTH_SHORT).show();
            } catch (Exception e1) {
            }

        }

        return output;
    }

    public static final Boolean canGainSu(Context context) {

        String suTestScript = "#!/system/bin/sh\necho ";
        String suTestScriptValid = "SuPermsOkay";

        ArrayList<String> output;
        output = runScript(context, suTestScript + suTestScriptValid, true);
        try {
            if (output.get(0).trim().equals(suTestScriptValid)) {
                Log.d(TAG, "Superuser command auth confirmed");
                return true;
            }

        } catch (Exception e) {
        }
        Log.d(TAG, "Superuser command auth refused");
        return false;

    }

    public static String copyFromAssets(Context context, String source, String destination)
            throws IOException {

        // read file from the apk
        InputStream is = context.getAssets().open(source);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();

        // write files in app private storage
        FileOutputStream output = context.openFileOutput(destination, Context.MODE_PRIVATE);
        output.write(buffer);
        output.close();

        Log.d(TAG, source + " asset copied to " + destination);

        return context.getFileStreamPath(destination).getAbsolutePath();
    }

    public static String getPackageVersion(Context context) {

        String versionText = "";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(),
                    PackageManager.GET_META_DATA);
            versionText = pInfo.versionName;
        } catch (NameNotFoundException e) {
            Log.e(TAG, "Unable to get package version name");
            e.printStackTrace();
        }
        return versionText;

    }

}
