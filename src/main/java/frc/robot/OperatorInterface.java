package frc.robot;

public class OperatorInterface {
    private static final int PILOT_PORT   = 0;
    private static final int COPILOT_PORT = 1;

    public final DTXboxController pilot;
    public final DTXboxController copilot;

    public OperatorInterface() {
        pilot = new DTXboxController(PILOT_PORT);
        copilot = new DTXboxController(COPILOT_PORT);
    }
    // Use this class to label each button sowe don't accidentily assign the same button to 2 different functions
    
    public boolean autoShootButton(){
        return pilot.getXButton();
    }
    public boolean autoCollectButton(){
        return pilot.getAButton();
    }
}
