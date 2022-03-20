package frc.robot;

@SuppressWarnings("unused")
public final class FeatureFlags {

    // Modify these values
    public static boolean doVision = true;
    public static boolean doChassis = true;
    public static boolean doImu = true;
    public static boolean doShooter = true;
    public static boolean doCompressor = true;
    public static boolean doClimber = true;

    // Set these once the subsystem in installed
    public static final boolean VISION_INSTALLED = true;
    public static final boolean CHASSIS_INSTALLED = true;
    public static final boolean IMU_INSTALLED = true;
    public static final boolean SHOOTER_INSTALLED = true;
    public static final boolean COMPRESSOR_INSTALLED = true;
    public static final boolean CLIMBER_INSTALLED = true;

    // Leave these values alone
    public static boolean visionInitialized = false;
    public static boolean chassisInitialized = false;
    public static boolean imuInitialized = false;
    public static boolean shooterInitialized = false;
    public static boolean compressorInitialized = false;
    public static boolean climberInitialized = false;

    /**
     * Updates which subsystems are enabled based on their dependencies
     */
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

            if (CLIMBER_INSTALLED) {
                doShooter = true;
            }

            if (COMPRESSOR_INSTALLED) {
                doCompressor = true;
            }
        }

        if (doClimber && CLIMBER_INSTALLED) {
            doCompressor = true;
            doShooter = true;
        }

        if (doChassis && CHASSIS_INSTALLED) {
            if (IMU_INSTALLED) {
                doImu = true;
            }
        }

        if (doShooter && SHOOTER_INSTALLED) {
            if (COMPRESSOR_INSTALLED) {
                doCompressor = true;
                doChassis = true;
            }
        }

        if (!CHASSIS_INSTALLED) {
            doChassis = false;
        }

        if (!IMU_INSTALLED) {
            doImu = false;
        }

        if (!SHOOTER_INSTALLED || !CHASSIS_INSTALLED) {
            doShooter = false;
        }

        if (!COMPRESSOR_INSTALLED) {
            doCompressor = false;
        }

        if (!VISION_INSTALLED || !CHASSIS_INSTALLED || !IMU_INSTALLED || !SHOOTER_INSTALLED || !COMPRESSOR_INSTALLED) {
            doVision = false;
        }
        if (doClimber && CLIMBER_INSTALLED) {
            if (COMPRESSOR_INSTALLED) {
                doCompressor = true;
            }
        }

    }
}
