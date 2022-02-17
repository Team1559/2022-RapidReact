package frc.robot;

public class OperatorInterface {
    private static final int PILOT_PORT   = 0;
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
    // Use this class to label each button so we don't accidentally assign the same button to 2 different functions
    
    public boolean autoShootButton() {
        return pilot.getXButton();
    }

    public boolean autoCollectButton() {
        return pilot.getAButton();
    }
    //DriveTrain



    //Shooter/Intake

    //Going to have to switch these to co-pilot at some point
    //Currently thinking that the co pilot controls intake and flywheel with one button
    // And pilot shoots(runs feeder motor) At the moment, going to use seperate buttons for testing purposes
    public boolean runFlyWheelButtonManual(){
        return pilot.getBButton();
    }

    public boolean runFlyWheelButton(){
        return pilot.getLeftBumper();
    }

    public boolean shootButton(){
        return pilot.getRightBumper();
    }

    public boolean lowerIntakeButton(){
        if(pilot.getLeftTriggerAxis() > .5)
            return true;
        else
            return false;
    }

    public boolean intakeButton(){
        if(pilot.getRightTriggerAxis() > .5)
            return true;
        else
            return false;
    }

    //Climber


    

}
