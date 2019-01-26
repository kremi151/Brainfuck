package lu.kremi151.brainfuck;

import android.app.Application;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

import lu.kremi151.brainfuck.enums.EnumCellWidth;

/**
 * Created by michm on 19.10.2016.
 */

public class Brainfuck extends Application {

    private SharedPreferences prefs;
    private static Brainfuck instance;
    private ClipboardManager clipboardManager;
    private File scriptsDir;

    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;
        prefs = this.getSharedPreferences("bf_ide", Context.MODE_PRIVATE);
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        scriptsDir = new File(getFilesDir(), "scripts");
        scriptsDir.mkdirs();
    }

    public static Brainfuck getInstance(){
        return instance;
    }

    public boolean sixteenBitModeEnabled(){
        return prefs.getBoolean("10b", false);
    }

    public void setSixteenBitMode(boolean v){
        prefs.edit().putBoolean("10b", v).apply();
    }

    public boolean isInfiniteLoopDetectionEnabled(){
        return prefs.getBoolean("infi_loop_detect", true);
    }

    public void setInfiniteLoopDetectionEnabled(boolean v){
        prefs.edit().putBoolean("infi_loop_detect", v).apply();
    }

    public boolean isExtendedModeEnabled(){
        return prefs.getBoolean("extended_execution", false);
    }

    public void setExtendedModeEnabled(boolean v){
        prefs.edit().putBoolean("extended_execution", v).apply();
    }

    public boolean useBFKeyboardAsDefault(){
        return prefs.getBoolean("bf_keyboard_default", true);
    }

    public void setUseBFKeyboardAsDefault(boolean v){
        prefs.edit().putBoolean("bf_keyboard_default", v).apply();
    }

    public ClipboardManager getClipboardManager(){
        return clipboardManager;
    }

    public File getScriptsDir(){
        return scriptsDir;
    }
}
