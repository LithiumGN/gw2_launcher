package updater;

import helpers.LauncherHelper;
import frame.CoreFrame;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CoreUpdater implements Runnable {

    private CoreFrame cf;
    private String path;
    private File dll;
    private File old_dll;
    private File disabled_dll;
    private static Logger log = Logger.getLogger(CoreUpdater.class.getName());


    public CoreUpdater(CoreFrame cf, String path) {
        this.cf = cf;
        this.path = path;
        dll = new File(path + "\\bin64\\d3d9.dll");  //dll of ArcDPS or BGDM
        old_dll = new File(path + "\\bin64\\d3d9_old.dll"); //backup dll of ArcDPS or BGDM
        disabled_dll = new File(path + "\\bin64\\d3d9_disabled.dll"); //disabled dll of ArcDPS or BGDM
        LauncherHelper.LogSetup(log, false);
    }

    //run() from interface "Runnable"
    public void run() {
        boolean check = dll.exists();
        File ini = new File(path + "\\bin64\\arcdps.ini"); //settings file of archdps. Needed to make ArcDPS works

        if (check) {
            //System.out.println("d3d9.dll exists");
            changeConfig("arc_only");
            log.log(Level.INFO, "d3d9.dll found");
            LauncherHelper.updateDll(cf, path); //if d3d9.dll exists check if update is needed

        } else if (!check && disabled_dll.exists()) {
            //If d3d9.dll is not detected but a disabled dll is found. Rename "disabled_d3d9.dll" and check for an update
            try {
                log.log(Level.INFO, "d3d9.dll not found but d3d9_disabled exists");
                Files.copy(disabled_dll.toPath(), dll.toPath());
                disabled_dll.delete();
                changeConfig("arc_only");
            } catch (IOException e) {
                e.printStackTrace();
                //Change status and color of JLabel status
                cf.status.setText("- Cannot restore ArcDPS");
                cf.status.setForeground(Color.RED);
            }
            LauncherHelper.updateDll(cf, path); //check for update just in case
        } else if (!check && old_dll.exists()) {
            //If d3d9.dll is not detected but a backup is found. Rename the backup and check for an update
            log.log(Level.INFO, "d3d9.dll not found but d3d9_old exists");
            try {
                Files.copy(old_dll.toPath(), dll.toPath());
                old_dll.delete();
                changeConfig("arc_only");
            } catch (IOException e) {
                e.printStackTrace();
                //Change status and color of JLabel status
                cf.status.setText("- Cannot connect to the update server");
                cf.status.setForeground(Color.RED);
            }

            LauncherHelper.updateDll(cf, path);

        } else {
            log.log(Level.INFO, "d3d9.dll, d3d9_old.dll, d3d9_disabled.dll not found");
            //if there is not d3d9.dll and no backup it means that ArcDPS or BGDM is not installed
            cf.startwith.setEnabled(false);

        }


        if (!ini.exists() && (cf.getMode().equals("arc_only"))) { //If ini file is not detected ask to the user if he would like to restore it with a default version from the website
            int dialogButton = JOptionPane.YES_NO_OPTION;
            log.log(Level.INFO, "archdps.ini not found");
            int dialogResult = JOptionPane.showConfirmDialog(null, "ArcDPS configuration file not found. Would you like to download a default configoration?", "ArcDPS configuration file not detected", dialogButton);
            if (dialogResult == JOptionPane.YES_OPTION) {
                LauncherHelper.downloadINI(cf, path); //Method used to download the .ini
            }

        }


        LauncherHelper.closeLogHandlers(log);
    }


    public static void runWithoutDPS(String path) {
        File dll = new File(path + "\\bin64\\d3d9.dll");
        if (dll.exists()) {
            log.log(Level.INFO, "Disabling dll");
            File old = new File(path + "\\bin64\\d3d9_disabled.dll");
            if (old.exists()) old.delete(); //delete an older disabled dll to prevent an exception
            try {
                Files.copy(dll.toPath(), old.toPath()); //rename d3d9.dll to d3d9_disabled.dll
                dll = new File(path + "\\bin64\\d3d9.dll");
                dll.delete();
            } catch (IOException e) {
                e.printStackTrace();
                log.log(Level.SEVERE, "IOException when disabling dll");
                errorDialog(path);
            }
        }
    }

    //Error dialog needed for static methods
    public static void errorDialog(String path) {
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int dialogResult = JOptionPane.showConfirmDialog(null, "Something went wrong. Check your internet connection. Would you like to run GW2 without ArcDPS?",
                "Updater failed", dialogButton);
        if (dialogResult == JOptionPane.YES_OPTION) {
            CoreUpdater.runWithoutDPS(path);
        }

    }

    public void changeConfig(String mode) {
        Properties prop = new Properties();
        LauncherHelper.loadProp(prop, "gw2_launcher.cfg");
        prop.put("mode", mode);
        LauncherHelper.saveProp(prop, "gw2_launcher.cfg");
    }


}
