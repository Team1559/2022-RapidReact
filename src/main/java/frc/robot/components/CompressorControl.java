package frc.robot.components;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.PneumaticHub;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import frc.robot.OperatorInterface;
import frc.robot.Wiring;

/**
 * Controls the compressor
 */
public class CompressorControl {
    public PneumaticHub ph;
    public Compressor airCompressor;
    public boolean useCompr2essor = true;
    public boolean isCompressorOn = false;
    private boolean disable = true;
    private boolean press = false;
    private OperatorInterface oi;

    /**
     * Creates a compressor object
     * 
     * @param oi The OpertorInterface object to allow for the compressor to be
     *           toggled in test mode
     */
    public CompressorControl(OperatorInterface oi) {
        ph = new PneumaticHub(Wiring.PNEUMATICS_HUB);
        airCompressor = new Compressor(Wiring.PNEUMATICS_HUB, PneumaticsModuleType.REVPH);
        this.oi = oi;
    }

    /**
     * Enables the compressor
     */
    public void enable() {
        airCompressor.enableDigital();
        isCompressorOn = true;
    }

    /**
     * Disables the compressor
     */
    public void disable() {
        airCompressor.disable();
        isCompressorOn = false;
    }

    /**
     * Call during test periodic to allow the compressor to be toggled on or off
     * during test mode
     */
    public void testPeriodic() {

        if (oi.compressorToggle()) {
            if (!press)
                disable = !disable;
            press = true;
        } else
            press = false;

        if (disable) {
            disable();
        } else {
            enable();
        }
    }
}