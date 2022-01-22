package frc.robot.components;

public class VisionData {

    public double hx;
    public double hy;
    public double hr;
    public double bx;
    public double by;
    public double br;

    public Integer hoopStatus;
    public Integer ballStatus;
    public boolean waitForOtherRobot;

    public boolean isHoopValid() {
        if (hoopStatus != 1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isBallValid() {
        if (ballStatus != 1) {
            return false;
        } else {
            return true;
        }
    }

    public void Print() {
        System.out.printf("%3.1f %3.1f %3.1f %3.1f %3.1f %3.1f %d %d\n", hx, hy, hr, bx, by, br, hoopStatus,
                ballStatus);
    }
}
