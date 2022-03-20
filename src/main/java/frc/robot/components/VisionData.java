package frc.robot.components;

/**
 * Holds all the data for the vision system
 */
public class VisionData {
    public static double hx = 0;
    public static double hy = 0;
    public static double hr = 0;
    public static double bx = 0;
    public static double by = 0;
    public static double br = 0;
    public static int hoopStatus = 2;
    public static int ballStatus = 2;
    public static boolean waitForOtherRobot = false;

    /**
     * Returns whether or not the vision system sees the hoop
     * 
     * @return Whether or not the vision system sees the hoop
     */
    public static boolean isHoopValid() {
        if (hoopStatus != 1) {
            return false;
        }

        else {
            return true;
        }
    }

    /**
     * Returns whether or not the vision system sees a ball of the correct color
     * 
     * @return Whether or not the vision system sees a ball of the correct color
     */
    public static boolean isBallValid() {
        if (ballStatus != 1) {
            return false;
        }

        else {
            return true;
        }
    }

    /**
     * Sets all values to their defaults
     */
    public static void restoreDefaults() {
        hx = 0;
        hy = 0;
        hr = 0;
        bx = 0;
        by = 0;
        br = 0;
        hoopStatus = 2;
        ballStatus = 2;
        waitForOtherRobot = false;
    }

    /**
     * Prints the current values
     */
    public static void Print() {
        System.out.printf("%3.1f %3.1f %3.1f %3.1f %3.1f %3.1f %d %d\n", hx, hy, hr, bx, by, br, hoopStatus,
                ballStatus);
    }
}
