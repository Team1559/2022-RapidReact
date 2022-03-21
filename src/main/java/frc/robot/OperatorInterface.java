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

    public boolean autoSteerToHoopButton() { // Rename this to steer to hoop
        return pilot.getBButton();
    }

    public boolean autoCollectButton() {
        return pilot.getAButton();
    }

    public boolean autoCollectButtonRelease() {
        return pilot.getAButtonReleased();
    }

    public boolean compressorToggle() {
        return pilot.getLeftTriggerAxis() > 0.5;
    }

    // DriveTrain
    public boolean slowModeButton() {
        return pilot.getLeftTriggerAxis() > 0.5;
    }

    // Shooter/Intake
    public boolean runFlyWheelButtonManual() {
        return copilot.getRightBumper();
    }

    public boolean runFlyWheelButtonManualPress() {
        return copilot.getRightBumperPressed();
    }

    public boolean shootButton() {
        return pilot.getRightBumper();
    }

    public boolean shootButtonPress() {
        return pilot.getRightBumperPressed();
    }

    public boolean autoShootButton() {
        return copilot.getAButton();
    }

    public boolean manualIntakeButton() {
        return pilot.getLeftBumper();
    }

    public boolean manualIntakeButtonPress() {
        return pilot.getLeftBumperPressed();
    }

    public boolean manualIntakeButtonRelease() {
        return pilot.getLeftBumperReleased();
    }

    public boolean raiseIntakeButton() {
        return pilot.getRightTriggerAxis() > 0.25;
    }

    public boolean reverseIntake() {
        return copilot.getLeftTriggerAxis() > 0.25;
    }

    // Climber
    public boolean climberUpButton() {
        return copilot.getDpad(DPadUp);
    }

    public boolean climberDownButton() {
        return copilot.getDpad(DPadDown);
    }

    public boolean climberEnableButton() {
        return copilot.getRightTriggerAxis() > .25;
    }

    public boolean testClimber() {
        return pilot.getRightTriggerAxis() > .25;
    }

    public boolean extendClimberPistonsButton() {
        return copilot.getXButton();
    }

    public boolean retractClimberPistonsButton() {
        return copilot.getYButton();
    }
}
