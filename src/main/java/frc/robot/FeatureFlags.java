package frc.robot;

public final class FeatureFlags {

    // Modify these values
    public static boolean doVision = true;
    public static boolean doChassis = false;
    public static boolean doImu = false;
    public static boolean doShooter = false;
    public static boolean doCompressor = true;

    // Leave these values alone
    public static boolean visionInitialized = false;
    public static boolean chassisInitialized  = false;
    public static boolean imuInitialized = false;
    public static boolean shooterInitalized = false;
    public static boolean compressorInitialized = false;


    public static void updateDependencies() {
        if(doVision) {
            doChassis= true;
            doImu = true;
            doShooter = true;
            doCompressor = true;
        }
        
        if(doChassis) {
            doImu = true;
        }

        if(doShooter){
            doCompressor = true;
        }
    }
}
