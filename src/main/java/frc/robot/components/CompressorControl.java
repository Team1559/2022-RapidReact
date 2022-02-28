package frc.robot.components;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.PneumaticHub;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import frc.robot.Wiring;

public class CompressorControl {
    public PneumaticHub ph;
    public Compressor airCompressor;
    public boolean useCompressor = true;
    public boolean isCompressorOn = false;

    public CompressorControl() {
        ph = new PneumaticHub(Wiring.PNEUMATICS_HUB);
        airCompressor = new Compressor(16, PneumaticsModuleType.REVPH);
    }

    public void enable() {
        airCompressor.enableDigital();
        isCompressorOn = true;
    }

    public void disable() {
        airCompressor.disable();
        isCompressorOn = false;
    }
}