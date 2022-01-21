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
}
