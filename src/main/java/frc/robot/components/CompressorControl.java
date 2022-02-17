package frc.robot.components;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import frc.robot.Wiring;

public class CompressorControl {
    public Compressor airCompressor;
    public boolean    useCompressor  = true;
    public boolean    isCompressorOn = false;

    public CompressorControl() {
        airCompressor = new Compressor(PneumaticsModuleType.REVPH);
    }

    public void run() {
        airCompressor.enableDigital();
        isCompressorOn = true;
    }

    public void disable() {
        airCompressor.disable();
        isCompressorOn = false;
    }
}