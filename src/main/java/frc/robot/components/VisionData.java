package frc.robot.components;

public class VisionData {

    public double hx;
    public double hy;
    public double hr;
    public double bx;
    public double by;
    public double br;
    public int hoopStatus;
    public int ballStatus;
    public boolean waitForOtherRobot;

    /**
     * Returns whether or not the vision system sees the hoop
     * @return Whether or not the vision system sees the hoop
     */
    public boolean isHoopValid() {
        if (hoopStatus != 1) {
            return false;
        }

        else {
            return true;
        }
    }
    /**
     * Returns whether or not the vision system sees a ball of the correct color
     * @return Whether or not the vision system sees a ball of the correct color
     */
    public boolean isBallValid() {
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
    public void Print() {
        System.out.printf("%3.1f %3.1f %3.1f %3.1f %3.1f %3.1f %d %d\n", hx, hy, hr, bx, by, br, hoopStatus,
                ballStatus);
    }
}
