package frc.robot;

public class OperatorInterface {
    private static final int PILOT_PORT = 0;
    private static final int COPILOT_PORT = 1;
    public final DTXboxController pilot;
    public final DTXboxController copilot;
    public static final int DPadRight = 0;
    public static final int DPadUp = 90;
    public static final int DPadLeft = 180;
    public static final int DPadDown = 270;

    public OperatorInterface() {
        pilot = new DTXboxController(PILOT_PORT);
        copilot = new DTXboxController(COPILOT_PORT);
    }
    // Use this class to label each button so we don't accidentally assign the same
    // button to 2 different functions

    public boolean autoShootButton() {
        return pilot.getBButton();
    }

    public boolean autoCollectButton() {
        return pilot.getAButton();
    }
    // DriveTrain

    // Shooter/Intake
    public boolean runFlyWheelButtonManual() {
        return copilot.getRightBumper();
    }

    public boolean shootButton() {
        return copilot.getBButton();
    }

    public boolean manualIntakeButton() {
        if (pilot.getLeftBumper()) {
            return true;
        } else {
            return false;
        }
    }

    // Climber

}
