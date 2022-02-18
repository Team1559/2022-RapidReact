package frc.robot;

@SuppressWarnings("unused")
public final class FeatureFlags {

    // Modify these values
    public static boolean doVision = true;
    public static boolean doChassis = false;
    public static boolean doImu = false;
    public static boolean doShooter = false;
    public static boolean doCompressor = false;

    // Set these once the subsystem in installed
    public static final boolean VISION_INSTALLED = true;
    public static final boolean CHASSIS_INSTALLED = true;
    public static final boolean IMU_INSTALLED = true;
    public static final boolean SHOOTER_INSTALLED = false;
    public static final boolean COMPRESSOR_INSTALLED = false;

    // Leave these values alone
    public static boolean visionInitialized = false;
    public static boolean chassisInitialized = false;
    public static boolean imuInitialized = false;
    public static boolean shooterInitalized = false;
    public static boolean compressorInitialized = false;

    public static void updateDependencies() {
        if (doVision && VISION_INSTALLED) {
            if (CHASSIS_INSTALLED) {
                doChassis = true;
            }

            if (IMU_INSTALLED) {
                doImu = true;
            }

            if (SHOOTER_INSTALLED) {
                doShooter = true;
            }

            if (COMPRESSOR_INSTALLED) {
                doCompressor = true;
            }
        }

        if (doChassis && CHASSIS_INSTALLED) {
            if (IMU_INSTALLED) {
                doImu = true;
            }
        }

        if (doShooter && SHOOTER_INSTALLED) {
            if (COMPRESSOR_INSTALLED) {
                doCompressor = true;
            }
        }
    }
}
