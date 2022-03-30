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

    public boolean isHoopValid() {
        return hoopStatus == 1;
    }

    public boolean isBallValid() {
        return ballStatus == 1;
    }

    public void Print() {
        System.out.printf("%3.1f %3.1f %3.1f %3.1f %3.1f %3.1f %d %d\n", hx, hy, hr, bx, by, br, hoopStatus,
                ballStatus);
    }
}
