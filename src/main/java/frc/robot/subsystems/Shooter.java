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
    private double shooterRpms = 75000;
    private double feederSpeed = 0.2;
    private double intakeSpeed = 0.4;

    private TalonFX shooter;
    private CANSparkMax feeder;
    private Solenoid lowerIntake;
    private TalonSRX intake;
    private VisionControl vc;

    // States for gatherer
    public static final int gathererUp = 0;
    public static final int gathererDown = 1;
    public static final int holding = 3;

    public int gathererState = gathererUp;

    public Shooter(OperatorInterface operatorinterface) {
        oi = operatorinterface;
        if (FeatureFlags.doVision && FeatureFlags.visionInitialized) {
            this.vc = Robot.vc;
        }

        // MotorController Config

        shooter = new TalonFX(Wiring.shooterMotor);
        feeder = new CANSparkMax(Wiring.feederMotor, MotorType.kBrushless);
        lowerIntake = new Solenoid(PneumaticsModuleType.REVPH, Wiring.lowerIntake);
        intake = new TalonSRX(Wiring.intake);

        // Note about pneumatics :

        // PneumaticsModuleType.CTREPCM for ctre stuff
        // PneumaticsModuleType.REVPH for rev stuff

        // Set all motors to 0 and pistons up
        feeder.set(0);
        lowerIntake.set(false);
        intake.set(TalonSRXControlMode.PercentOutput, 0);

        // Shooter Velocity mode configs
        shooter.configClosedloopRamp(cLR, TIMEOUT);
        shooter.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor); // shooter.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
        shooter.config_kF(0, shooter_kF);
        shooter.config_kP(0, shooter_kP);
        shooter.config_kD(0, shooter_kD);
        shooter.config_kI(0, shooter_kI);
        shooter.configNominalOutputForward(0, TIMEOUT);
        shooter.configNominalOutputReverse(0, TIMEOUT);
        shooter.configPeakOutputForward(+1, TIMEOUT);
        shooter.configPeakOutputReverse(-1, TIMEOUT);
        shooter.setNeutralMode(NeutralMode.Coast);
        shooter.configSupplyCurrentLimit(shooterLimit);

        ml.createfile("shooterRPMS");

        try {
            shooterRpms = Double.parseDouble(ml.readFile());
        }

        catch (NullPointerException e) {
            e.printStackTrace();
        }

        catch (NumberFormatException e) {
            e.printStackTrace();
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
        switch (gathererState) {
            case gathererUp:
                if (oi.manualIntakeButton()) {
                    lowerIntake();
                    startIntake();
                }
                break;
            case gathererDown:
                if (!oi.manualIntakeButton()) {
                    stopIntake();
                }
                break;
            case holding:
                if (oi.manualIntakeButton()) {
                    stopIntake();
                    raiseIntake();
                }
                break;
        }
    }

    public void ShooterMain() {
        if (oi.runFlyWheelButtonManual()) {
            if(checkVision()){
                vc.shooterstate = shooterState.ALIGN;
            }
            
            updateManualRPMS();
            startShooter(shooterRpms);
        }

        else if (oi.autoShootButton()) {
            if (checkVision()) {
                autoShoot();
            }

            else {
                if(checkVision()){
                    vc.shooterstate = shooterState.ALIGN;
                }
                updateManualRPMS();
                startShooter(shooterRpms);
            }
        }

        else {
            if(checkVision()){
                vc.shooterstate = shooterState.ALIGN;
            }

            stopShooter();
        }
    }

    public void feederMain() {
        if (oi.shootButton()) {
            startFeeder();
        }

        else {
            stopFeeder();
        }
    }

    public void startFeeder() {
        feeder.set(feederSpeed);
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
        gathererState = gathererDown;
        lowerIntake.set(true);
    }

    public void raiseIntake() {
        gathererState = gathererUp;
        lowerIntake.set(false);
    }

    public void startIntake() {
        intake.set(TalonSRXControlMode.PercentOutput, intakeSpeed);
    }

    public void stopIntake() {
        gathererState = holding;
        intake.set(TalonSRXControlMode.PercentOutput, 0);
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
        if (oi.copilot.getDPadPress(oi.DPadUp)) {
            shooterRpms += 100;
        }

        else if (oi.copilot.getDPadPress(oi.DPadDown)) {
            shooterRpms -= 100;
        }

        if (shooterRpms < 0) {
            shooterRpms = 0;
        }

        else if (shooterRpms > 15000) {
            shooterRpms = 15000;
        }

        ml.write(Double.toString(shooterRpms));
    }
}
