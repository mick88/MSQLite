package com.michaldabski.msqlite;

import android.app.Application;

import org.junit.runners.model.InitializationError;
import org.robolectric.AndroidManifest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.res.Fs;

/**
 * Created by Michal on 26/07/2014.
 */
public class MsqliteTestRunner extends RobolectricTestRunner
{
    public MsqliteTestRunner(Class<?> testClass) throws InitializationError
    {
        super(testClass);
    }

    @Override protected AndroidManifest getAppManifest(Config config) {
        String myAppPath = Application.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String manifestPath = myAppPath + "../../../src/main/AndroidManifest.xml";
        String resPath = myAppPath + "../../../src/main/res";
        String assetPath = myAppPath + "../../../src/main/assets";
        return createAppManifest(Fs.fileFromPath(manifestPath), Fs.fileFromPath(resPath), Fs.fileFromPath(assetPath));
    }
}