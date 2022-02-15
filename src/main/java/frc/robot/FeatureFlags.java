package frc.robot;

public final class FeatureFlags {

    // Modify these values
    public static boolean doVision = false;
    public static boolean doChassis = false;
    public static boolean doImu = false;

    // Leave these values alone
    public static boolean visionInitialized = false;
    public static boolean chassisInitialized  = false;
    public static boolean imuInitialized = false;


    public static void updateDependencies() {
        if(doVision) {
            doChassis= true;
            doImu = true;
        }
        
        if(doChassis) {
            doImu = true;
        }
    }
}
