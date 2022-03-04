package frc.robot.subsystems;

import frc.robot.OperatorInterface;
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.*;
import frc.robot.*;
import com.revrobotics.*;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shooter {
    private OperatorInterface oi;
    private SupplyCurrentLimitConfiguration shooterLimit = new SupplyCurrentLimitConfiguration(true, 60, 40, 2);
    private final int TIMEOUT = 0;
    private final double cLR = 0.75;

    private final double shooter_kF = 0.045;
    private final double shooter_kP = 0.4;
    private final double shooter_kD = 0;
    private final double shooter_kI = 0.000;
    private final double shooter_kiz = 0.0;

    // TODO: Tune these
    private final double feeder_kF = 0.045;
    private final double feeder_kP = 0.4;
    private final double feeder_kD = 0;
    private final double feeder_kI = 0.000;
    private final double feeder_kiz = 0.0;

    public double shooterRpms = 7500;
    public double feederSpeed = 0.2;
    public double intakeSpeed = 1; // 0.4;
    private final double DEFAULT_DISTANCE = 2500;
    private final boolean TESTING = true;

    private TalonFX shooter;
    private CANSparkMax feeder;
    private Solenoid lowerIntake;
    private TalonSRX intake;
    private VisionControl vc;
    private Chassis chassis;
    private RelativeEncoder feederEncoder;
    private SparkMaxPIDController feederPid;

    private boolean RESET_ENCODER = true;
    public boolean disableManual = false;

    // States for gatherer
    public static final int gathererUp = 0;
    public static final int gathererDown = 1;
    public static final int holding = 2;

    public int gathererState = gathererUp;

    private int lastState = gathererState;

    public Shooter(OperatorInterface operatorinterface, Chassis chassis) {
        oi = operatorinterface;
        if (FeatureFlags.doVision && FeatureFlags.visionInitialized) {
            this.vc = Robot.vc;
            this.chassis = chassis;
        }

        // MotorController Config
        shooter = new TalonFX(Wiring.SHOOTER_MOTOR);
        intake = new TalonSRX(Wiring.INTAKE_MOTOR);
        feeder = new CANSparkMax(Wiring.FEEDER_MOTOR, MotorType.kBrushless);

        // Invert motors
        feeder.setInverted(true);
        shooter.setInverted(true);

        // Note about pneumatics :
        // PneumaticsModuleType.CTREPCM for ctre stuff
        // PneumaticsModuleType.REVPH for rev stuff
        if (FeatureFlags.doCompressor && FeatureFlags.compressorInitialized) {
            lowerIntake = new Solenoid(Wiring.PNEUMATICS_HUB, PneumaticsModuleType.REVPH, Wiring.INTAKE_SOLENOID);
            lowerIntake.set(false);
        }

        // Set motors to 0
        feeder.set(0);
        feederEncoder = feeder.getEncoder();
        feederPid = feeder.getPIDController();
        feederPid.setP(feeder_kP);
        feederPid.setI(feeder_kI);
        feederPid.setD(feeder_kD);
        feederPid.setFF(feeder_kF);
        feederPid.setIZone(feeder_kiz);

        intake.set(TalonSRXControlMode.PercentOutput, 0);

        // Shooter Velocity mode configs
        shooter.configClosedloopRamp(cLR, TIMEOUT);
        shooter.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, 0, TIMEOUT);
        shooter.config_kF(0, shooter_kF, TIMEOUT);
        shooter.config_kP(0, shooter_kP, TIMEOUT);
        shooter.config_kD(0, shooter_kD, TIMEOUT);
        shooter.config_kI(0, shooter_kI, TIMEOUT);
        shooter.configNominalOutputForward(0, TIMEOUT);
        shooter.configNominalOutputReverse(0, TIMEOUT);
        shooter.configPeakOutputForward(1, TIMEOUT);
        shooter.configPeakOutputReverse(-1, TIMEOUT);
        shooter.setNeutralMode(NeutralMode.Coast);
        shooter.config_IntegralZone(0, shooter_kiz, TIMEOUT);
        shooter.configSupplyCurrentLimit(shooterLimit, TIMEOUT);

    }

    public void main() {
        // Control for FlyWheel
        ShooterMain();
        feederMain();

        // Control for lowering intake
        gathererMain();
    }

    public void gathererMain() { // TODO make sure the fix works
        if (!disableManual) {
            if (FeatureFlags.doCompressor && FeatureFlags.compressorInitialized) {
                switch (gathererState) {
                    case gathererUp:
                        if (oi.manualIntakeButtonPress()) { // Lower intake if button pressed else stop the intakes
                            gathererState = gathererDown;
                        }
                        if (oi.climberEnableButton()) {
                            gathererState = holding;
                        }
                        break;
                    case gathererDown:
                        if (!oi.manualIntakeButton()) { // Stop the intake and hold ball when button is released
                            gathererState = holding;
                        }
                        break;
                    case holding:
                        if (oi.raiseIntakeButton()) { // intake when the button is pressed again
                            gathererState = gathererUp;
                        } else if (oi.manualIntakeButton()) {
                            gathererState = gathererDown;
                        }
                        if (oi.climberEnableButton()) {
                            gathererState = holding;
                        }
                        break;
                }
            }
            gathererState();
        }
    }

    public void gathererState() {
        switch (gathererState) {
            case gathererUp:
                stopIntake();
                raiseIntake();
                break;
            case gathererDown:
                lowerIntake();
                startIntake(intakeSpeed);
                break;
            case holding:
                stopIntake();  
                lowerIntake();
                break;
        }
    }

    public void ShooterMain() {
        if (oi.runFlyWheelButtonManual()) {
            oi.copilot.startRumble(-1);
            startShooter(calculateShooterRPMS(DEFAULT_DISTANCE)); // Assume distance is 8 ft in manual mode
            shooterRpms = calculateShooterRPMS(DEFAULT_DISTANCE);
        } else if (oi.autoSteerToHoopButton()) {
            if (checkDependencies()) {
                shooterRpms = calculateShooterRPMS(vc.hoopx);
                startShooter(calculateShooterRPMS(vc.hoopx));
            } else if (TESTING) {
                startShooter(calculateShooterRPMS(DEFAULT_DISTANCE));
            }
        } else {
            oi.copilot.stopRumble();
            stopShooter();
        }
    }

    // FEEDER STUFF
    public void feederMain() {
        if (oi.shootButton()) {
            disableManual = true;
            lastState = gathererState;
            startFeeder(feederSpeed);
        } else if (oi.autoShootButton() && checkDependencies()) { // Shoot when ready
            if (Math.abs(vc.hoopr) <= vc.hoopChassisThreshold) { // Angle check
                if (vc.hoopx <= vc.maxHoopDistance) { // distance check
                    if (oi.pilot.getLeftY() < 0.05
                            && Math.abs(chassis.rpmToFps(chassis.getFrontAverageWheelRPM())) < 2) {
                        // Speed check ^^
                        if (Math.abs(getShooterRpms() - calculateShooterRPMS(vc.hoopx)) < vc.shooterThreshold) {
                            disableManual = true;
                            lastState = gathererState;
                            startFeeder(feederSpeed); // flywheel rpm check ^
                        }
                    }
                }
            }
        } else if (oi.reverseIntake()) {
            startFeeder(-feederSpeed);
        } else {
            if (disableManual) {
                gathererState = lastState;
                disableManual = false;
            }
            holdFeeder();
        }
    }

    public void startFeeder(double speed) {
        RESET_ENCODER = true;
        feederPid.setReference(speed, ControlType.kDutyCycle);
        if (disableManual) {
            gathererState = lastState;
            disableManual = false;

        }

    }

    public void holdFeeder() {
        if (RESET_ENCODER) {
            feederEncoder.setPosition(0);
            RESET_ENCODER = false;
        }
        if (disableManual) {
            gathererState = lastState;
            disableManual = false;
        }
        feederPid.setReference(0, ControlType.kPosition);
    }

    public void stopFeeder() {
        gathererState = gathererUp;
        feederPid.setReference(0, ControlType.kDutyCycle);
    }

    // Get and Set shooter states
    public void startShooter(double rpms) {
        SmartDashboard.putNumber("Shooter RPMs", rpms);
        shooter.set(TalonFXControlMode.Velocity, rpms / 10 / 60 * 2048);
    }

    public void stopShooter() {
        shooter.set(TalonFXControlMode.PercentOutput, 0);
    }

    public double getShooterRpms() {
        return shooter.getSelectedSensorVelocity() * 10 / 2048 * 60;
    }

    // INTAKE STUFF
    // Raise and lower intake
    public void lowerIntake() {
        lowerIntake.set(true);
    }

    public void raiseIntake() {
        lowerIntake.set(false);
    }

    // Start and stop intake
    public void startIntake(double speed) {
        intake.set(TalonSRXControlMode.PercentOutput, speed);
    }

    public void stopIntake() {
        intake.set(TalonSRXControlMode.PercentOutput, 0);
    }

    public boolean checkDependencies() {
        return checkHoopVision() && checkChassis();
    }

    // Validate hoop vision
    public boolean checkHoopVision() {

        return FeatureFlags.doVision && FeatureFlags.visionInitialized && vc.isHoopValid();
    }

    public boolean checkChassis() {
        return FeatureFlags.doChassis && FeatureFlags.chassisInitialized;
    }

    public void autoShoot() {
        vc.autoShoot();
    }

    public double calculateShooterRPMS(double distance) {
        return distance;
        // double shooterRPM = 0;
        // // math
        // shooterRPM = 2000 + 1000 * (distance * 12 - 100) / 120 * 2/3; // TODO: fix
        // this

        // return shooterRPM;
    }

    public void disable() {
        raiseIntake();
        stopShooter();
        stopIntake();
        stopFeeder();
    }
}
