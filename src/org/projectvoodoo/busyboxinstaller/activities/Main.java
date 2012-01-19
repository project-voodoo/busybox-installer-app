
package org.projectvoodoo.busyboxinstaller.activities;

import java.io.IOException;

import org.projectvoodoo.busyboxinstaller.R;
import org.projectvoodoo.busyboxinstaller.utils.Utils;

import android.app.Activity;
import android.os.Bundle;

public class Main extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        try {
            Utils.copyFromAssets(getApplicationContext(), "busybox", "busybox");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
