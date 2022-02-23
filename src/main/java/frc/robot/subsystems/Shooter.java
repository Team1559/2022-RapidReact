package frc.robot.subsystems;

import frc.robot.OperatorInterface;
import frc.robot.components.MachineLearning;
import frc.robot.subsystems.VisionControl.shooterState;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.*;
import frc.robot.*;
import com.revrobotics.*;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;

public class Shooter {

    private OperatorInterface oi;

    private SupplyCurrentLimitConfiguration shooterLimit = new SupplyCurrentLimitConfiguration(true, 20, 20, 0);
    private final int TIMEOUT = 0;
    private final double cLR = 0.1;
    private MachineLearning ml = new MachineLearning();

    private double shooter_kF = 0.045;
    private double shooter_kP = 0.4;
    private double shooter_kD = 0;
    private double shooter_kI = 0.000;
    public double shooterRpms = 10750;
    public double feederSpeed = 0.2;
    public double intakeSpeed = 0.4;

    private TalonFX shooter;
    private CANSparkMax feeder;
    private Solenoid lowerIntake;
    private TalonSRX intake;
    private VisionControl vc;

    // States for gatherer
    public static final int gathererUp = 0;
    public static final int gathererDown = 1;
    public static final int holding = 2;

    public int gathererState = gathererUp;

    public Shooter(OperatorInterface operatorinterface) {
        oi = operatorinterface;
        if (FeatureFlags.doVision && FeatureFlags.visionInitialized) {
            this.vc = Robot.vc;
        }

        // MotorController Config
        shooter = new TalonFX(Wiring.shooterMotor);
        intake = new TalonSRX(Wiring.intake);
        feeder = new CANSparkMax(Wiring.feederMotor, MotorType.kBrushless);

        // Invert motors
        feeder.setInverted(true);
        shooter.setInverted(true);

        // Note about pneumatics :
        // PneumaticsModuleType.CTREPCM for ctre stuff
        // PneumaticsModuleType.REVPH for rev stuff
        if (FeatureFlags.doCompressor && FeatureFlags.compressorInitialized) {
            lowerIntake = new Solenoid(PneumaticsModuleType.REVPH, Wiring.lowerIntake);
            lowerIntake.set(false);
        }

        // Set motors to 0
        feeder.set(0);
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
        shooter.configPeakOutputForward(+1, TIMEOUT);
        shooter.configPeakOutputReverse(-1, TIMEOUT);
        shooter.setNeutralMode(NeutralMode.Coast);
        shooter.configSupplyCurrentLimit(shooterLimit, TIMEOUT);
        ml.setDirectory("Shooter");
        ml.createfile("shooterRPMS");

        try {
            shooterRpms = Double.parseDouble(ml.readFile());
        }

        catch (NullPointerException e) {
            // e.printStackTrace();
        }

        catch (NumberFormatException e) {
            // e.printStackTrace();
        }

    }

    public void main() {
        // Control for FlyWheel
        ShooterMain();
        feederMain();

        // Control for lowering intake
        gathererMain();
    }

    public void gathererMain() {
        if (FeatureFlags.doCompressor && FeatureFlags.compressorInitialized) {
            switch (gathererState) {
                case gathererUp:
                    if (oi.manualIntakeButton()) {
                        lowerIntake();
                        startIntake(intakeSpeed);
                    }

                    else {
                        stopIntake();
                    }
                    break;
                case gathererDown:
                    if (!oi.manualIntakeButton()) {
                        stopIntake();
                    }

                    else {
                        startIntake(intakeSpeed);
                    }
                    break;
                case holding:
                    if (oi.manualIntakeButton()) {
                        stopIntake();
                        raiseIntake();
                    }

                    else {
                        stopIntake();
                    }
                    break;
            }
        }
    }

    public void ShooterMain() {
        System.out.println(shooterRpms);
        updateManualRPMS();

        if (oi.runFlyWheelButtonManual()) {
            if (checkVision()) {
                vc.shooterstate = shooterState.ALIGN;
            }

            startShooter(shooterRpms);
        }

        else if (oi.autoShootButton()) {
            if (checkVision()) {
                autoShoot();
            }

            else {
                if (checkVision()) {
                    vc.shooterstate = shooterState.ALIGN;
                }
                startShooter(shooterRpms);
            }
        }

        else {
            if (checkVision()) {
                vc.shooterstate = shooterState.ALIGN;
            }

            stopShooter();
        }
    }

    public void feederMain() {
        if (oi.shootButton()) {
            startFeeder(feederSpeed);
            startIntake(-intakeSpeed);
        }

        else if (oi.reverseIntake()) {
            startFeeder(-feederSpeed);
        }

        else {
            stopFeeder();
        }
    }

    public void startFeeder(double speed) {
        feeder.set(speed);
    }

    public void stopFeeder() {
        feeder.set(0);
    }

    public void startShooter(double rpms) {
        shooter.set(TalonFXControlMode.Velocity, rpms);
    }

    public double getShooterRpms() {
        return shooter.getSelectedSensorVelocity() / 2048 * 10 / 60;
    }

    public void stopShooter() {
        shooter.set(TalonFXControlMode.PercentOutput, 0);
    }

    public void lowerIntake() {
        lowerIntake.set(true);
        gathererState = gathererDown;
    }

    public void raiseIntake() {
        lowerIntake.set(false);
        gathererState = gathererUp;
    }

    public void startIntake(double speed) {
        intake.set(TalonSRXControlMode.PercentOutput, speed);
    }

    public void stopIntake() {
        intake.set(TalonSRXControlMode.PercentOutput, 0);
        gathererState = holding;
    }

    public boolean checkVision() {
        if (FeatureFlags.doVision && FeatureFlags.visionInitialized) {
            return vc.isHoopValid();
        }
        return false;
    }

    public void autoShoot() {
        vc.autoShoot();
    }

    private void updateManualRPMS() {
        double rpmOld = shooterRpms;
        if (oi.copilot.getDPadPress(oi.DPadUp)) {
            System.out.println("increasing");
            shooterRpms += 100;
        }

        else if (oi.copilot.getDPadPress(oi.DPadDown)) {
            System.out.println("decrease");
            shooterRpms -= 100;
        }

        if (shooterRpms < 0) {
            shooterRpms = 0;
        }

        else if (shooterRpms > 15000) {
            shooterRpms = 15000;
        }

        if (rpmOld != shooterRpms) {
            ml.write(Double.toString(shooterRpms));
        }
    }
}
