package frc.robot;

public class OperatorInterface {
    private static final int PILOT_PORT = 0;
    private static final int COPILOT_PORT = 1;
    public final DTXboxController pilot;
    public final DTXboxController copilot;
    public final int DPadRight = 90;
    public final int DPadUp = 0;
    public final int DPadLeft = 270;
    public final int DPadDown = 180;

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
        return pilot.getLeftBumper();
    }

    public boolean reverseIntake(){
        return copilot.getLeftTriggerAxis() > 0.5;
    }

    // Climber

}
