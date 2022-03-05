package frc.robot;

public class Wiring {
    // Use the public static final int __Variable_name__ =
    // __Motor/solinoid/relay_id__; to decair the output devices id

    // Other
    public static final int PDP = 1;
    public static final int PNEUMATICS_HUB = 2;

    // Drivetrain
    public static final int FLMOTOR = 11;
    public static final int FRMOTOR = 12;
    public static final int BLMOTOR = 13;
    public static final int BRMOTOR = 14;

    // Cargo
    public static final int SHOOTER_MOTOR = 16;
    public static final int FEEDER_MOTOR = 15;

    public static final int INTAKE_SOLENOID = 15;
    public static final int INTAKE_MOTOR = 17;

    // Climber
    public static final int CLIMBER_SOLENOID = 8;
    public static final int climberMotor = 18;

    // Relay ports
    public static final int lSpikeChannel = 0;
    public static final int rSpikeChannel = 1;

    // Digital IO ports
    public static final int upperLimitSwitchInput = 0;
    public static final int lowerLimitSwitchInput = 1;
}
