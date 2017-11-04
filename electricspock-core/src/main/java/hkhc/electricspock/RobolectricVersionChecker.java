/*
 * Copyright 2017 Herman Cheung
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package hkhc.electricspock;

import java.util.Properties;

/**
 * Created by herman on 23/9/2017.
 */

public class RobolectricVersionChecker {

    private String versionFile = "robolectric-version.properties";
    private String versionKey = "robolectric.version";

    // *** update this for version upgrade
    private String[] acceptedVersions = new String[] {"3.3","3.4", "3.5"};

    public RobolectricVersionChecker() {}

    public RobolectricVersionChecker(String versionFile) {
        this.versionFile = versionFile;
    }

    public RobolectricVersionChecker(String versionFile, String versionKey) {
        this.versionFile = versionFile;
        this.versionKey = versionKey;
    }

    public String[] getAcceptedVersions() {
        return acceptedVersions;
    }

    public void setAcceptedVersions(String[] acceptedVersions) {
        this.acceptedVersions = acceptedVersions;
    }

    public String getCurrentRobolectricVersion() {

        try {

            Properties prop = new Properties();
            prop.load(getClass().getClassLoader().getResourceAsStream(versionFile));
            return prop.getProperty(versionKey);
        }
        catch (Throwable t) {
            return "Unknown";
        }


    }

    public boolean isVersion(String version, String prefix) {

        if (version==null) return false;

        return version.equals(prefix) ||
                version.indexOf(prefix+".")==0 ||
                version.indexOf(prefix+"-")==0;

    }

    // return true if any of prefixes is matached.
    public boolean isVersion(String version, String[] prefixes) {
        for(String p : prefixes) {
            if (isVersion(version, p)) return true;
        }
        return false;
    }

    public void checkRobolectricVersion(String ver) {

        if (!isVersion(ver, acceptedVersions))
            throw new RuntimeException(
                    "This version of ElectricSpock supports Robolectric 3.3 or 3.4 only. "
                            +"Version "+ver+" is detected.");
    }

    public void checkRobolectricVersion() {

        String ver = getCurrentRobolectricVersion();
        checkRobolectricVersion(ver);

    }

}
