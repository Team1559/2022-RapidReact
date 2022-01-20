package frc.robot;

/**
 * A proposed new OperatorInterface class to simplify our current code and
 * remove the need to declare the {@link DTButton} and {@link Buttons} classes
 * by using the built-in {@code wpilib} profiles for Xbox controllers
 */
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
