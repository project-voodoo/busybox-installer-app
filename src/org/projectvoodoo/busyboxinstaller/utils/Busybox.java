
package org.projectvoodoo.busyboxinstaller.utils;

import java.util.ArrayList;

public class Busybox {

    private ArrayList<String> listApplets(String busyboxPath) {

        ArrayList<String> list = new ArrayList<String>();

        for (String line : Utils.runCommand(busyboxPath)) {

        }

        return list;
    }

}
