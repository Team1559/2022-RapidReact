package frc.robot.subsystems;

import frc.robot.OperatorInterface;
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.*;

import frc.robot.*;
import com.revrobotics.*;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DriverStation;
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
    public int ticCounter = 0;

    private double encoderTics = 0;

    // TODO: Tune these
    private final double feeder_kF = 0.00;
    private final double feeder_kP = 0.06;
    private final double feeder_kD = 0.2;
    private final double feeder_kI = 0.0003;
    private final double feeder_kiz = 0.0;
    private final double feeder_kiM = 0.1;

    public double feederSpeed = 1.6;

    public double intakeSpeed = 1; // 0.4;

    public static final double SHOOTER_DISTANCE_FROM_CAMERA = 3.5;
    public static final double DEFAULT_RPMS = 2150; // 4 ft from front of robot to face of target
    private final boolean TESTING = true;

    private TalonFX shooter;
    private CANSparkMax feeder;
    private Solenoid lowerIntake;
    private TalonSRX intake;
    private VisionControl vc;
    private Chassis chassis;
    private RelativeEncoder feederEncoder;
    private SparkMaxPIDController feederPid;
    private Robot robot;

    public boolean RESET_ENCODER = true;
    public boolean disableManual = false;

    // States for gatherer
    public static final int gathererUp = 0;
    public static final int gathererDown = 1;
    public static final int holding = 2;
    public static final int upRun = 3;

    public boolean gatherLock = false;

    public int gathererState = gathererUp;

    public int lastState = holding;

    public Shooter(OperatorInterface operatorinterface, Chassis chassis, Robot robot) {
        oi = operatorinterface;
        this.chassis = chassis;
        this.robot = robot;

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
        feederPid.setP(feeder_kP, 0);
        feederPid.setI(feeder_kI, 0);
        feederPid.setD(feeder_kD, 0);
        feederPid.setFF(feeder_kF, 0);
        feederPid.setIZone(feeder_kiz, 0);
        feederPid.setIMaxAccum(feeder_kiM, 0);

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

    public void initVision() {
        this.vc = robot.vc;
    }

    public void main() {
        // Control for FlyWheel
        ShooterMain();
        feederMain();

        // Control for lowering intake
        gathererMain();
        gathererState();
    }

    public void zeroFeeder() {
        feederEncoder.setPosition(0);
        encoderTics = 0;
    }

    public void gathererMain() {
        if (!disableManual) {
            if (FeatureFlags.doCompressor && FeatureFlags.compressorInitialized) {
                switch (gathererState) {
                    case gathererUp:
                        if (oi.manualIntakeButtonPress()) { // Lower intake if button pressed else stop the intakes
                            gathererState = gathererDown;
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
                        break;
                }
            }
        } else {
        }
    }

    public void gathererState() {
        if (!disableManual) {
            lastState = gathererState;
        }
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

            case upRun:
                startIntake(intakeSpeed);
                raiseIntake();
                break;
        }
    }

    public void ShooterMain() {
        SmartDashboard.putNumber("Actual shotoer", getShooterRpms());
        if (oi.runFlyWheelButtonManual()) {
            // oi.copilot.startRumble(0);

            startShooter(getDefaultShooterRpm()); // Assume distance is 8 ft in manual mode
        } else if (oi.autoSteerToHoopButton()) {
            if (checkDependencies()) {
                SmartDashboard.putNumber("hoopx", vc.hoopx);
                SmartDashboard.putNumber("Shooter setpt",
                        calculateShooterRPMS(vc.hoopx + SHOOTER_DISTANCE_FROM_CAMERA + 2));
                startShooter(calculateShooterRPMS(vc.hoopx + SHOOTER_DISTANCE_FROM_CAMERA + 2));
            } else if (TESTING) {
                startShooter(getDefaultShooterRpm());
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
            startFeeder(feederSpeed, oi.shootButtonPress());
            // if (Math.abs( () - calculateShooterRPMS(DEFAULT_DISTANCE)) <
            // vc.shooterThreshold) {
            // disableManual = true;
            // startFeeder(feederSpeed); // flywheel rpm check ^
            // }
        } else if (oi.autoShootButton() && checkDependencies()) { // Shoot when ready
            if (Math.abs(vc.hoopr) <= VisionControl.hoopChassisThreshold) { // Angle check
                System.out.println("Angle check passed");
                if (vc.hoopx <= VisionControl.maxHoopDistance) { // distance check
                    System.out.println("Distance passed");
                    if (Math.abs(chassis.rpmToFps(chassis.getFrontAverageWheelRPM())) < 2) {
                        System.out.println("Speed passed");
                        if (Math.abs(Math.abs(getShooterRpms()) -
                                Math.abs(calculateShooterRPMS(vc.hoopx + SHOOTER_DISTANCE_FROM_CAMERA
                                        + 2))) < VisionControl.shooterThreshold) {
                            System.out.println("RPM check " + disableManual);
                            startFeeder(feederSpeed, !disableManual); // flywheel rpm check ^
                            disableManual = true;
                        }
                    }
                }
            }
        } else if (oi.reverseIntake()) {
            disableManual = false;
            // startFeeder(-feederSpeed, oi.shootButtonPress());
        } else if (!oi.autoCollectButton()) {
            if (disableManual) {
                gathererState = lastState;
                disableManual = false;
            }
            holdFeeder();
        }
    }

    public void startFeeder(double setpoint, boolean setNewTarget) {
        RESET_ENCODER = true;
        if (setNewTarget) {
            feederEncoder.setPosition(0);
            feederPid.setReference(setpoint, ControlType.kPosition);
        }
        if (!gatherLock) {
            if (disableManual && gathererState == holding) {
                gathererState = gathererDown;
            } else if (disableManual) {
                gathererState = upRun;
            }
            gatherLock = true;
        }
    }

    public void holdFeeder() {
        if (RESET_ENCODER) {
            feederEncoder.setPosition(0);
            encoderTics = 0;
            RESET_ENCODER = false;
        }
        if (disableManual && !oi.autoCollectButton() && DriverStation.isTeleop()) {
            gathererState = lastState;
            disableManual = false;
        }
        // if (ticCounter % 10 == 0 && (feederEncoder.getPosition() - encoderTics <
        // 2.0)) {
        // encoderTics -= 0.2;
        // }
        gatherLock = false;
        feederPid.setReference(encoderTics, ControlType.kPosition);
    }

    public void stopFeeder() {
        gathererState = gathererUp;
        feederPid.setReference(0, ControlType.kDutyCycle);
    }

    // Get and Set shooter states
    public void startShooter(double rpms) {
        if (rpms > 2700)
            rpms = 2700;
        else if (rpms < 2000)
            rpms = 2050;
        shooter.set(TalonFXControlMode.Velocity, rpms / 10 / 60 * 2048);
        // shooter.set(TalonFXControlMode.Velocity, 6000 / 10 / 60 * 2048);
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

    public double getRpmError() {
        return shooter.getClosedLoopError() * 10 / 2048 * 60;
    }

    public double getDefaultShooterRpm() {
        // return DEFAULT_RPMS;
        return SmartDashboard.getNumber("Shooter RPM", DEFAULT_RPMS);
    }

    public double calculateShooterRPMS(double distance) {
        // RPM vs. distance fit from
        // https://docs.google.com/spreadsheets/d/1l1Nxlk29b2KL5FwVklSFhfuychKHfztRSNRqPUuQUIs/edit#gid=1365511344
        // return 4476 + 158 * distance;
        return 1750 + 20.2 * distance + 1.85 * Math.pow(distance, 2);
        // return getDefaultShooterRpm();
    }

    public void disable() {
        raiseIntake();
        stopShooter();
        stopIntake();
        stopFeeder();
    }
}
