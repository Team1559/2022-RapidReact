package frc.robot.components;

/**
 * Holds all the data for the vision system
 */
public class VisionData {

    public static double hx;
    public static double hy;
    public static double hr;
    public static double bx;
    public static double by;
    public static double br;
    public static int hoopStatus;
    public static int ballStatus;
    public static boolean waitForOtherRobot;

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
     * Prints the current values
     */
    public static void Print() {
        System.out.printf("%3.1f %3.1f %3.1f %3.1f %3.1f %3.1f %d %d\n", hx, hy, hr, bx, by, br, hoopStatus,
                ballStatus);
    }
}
