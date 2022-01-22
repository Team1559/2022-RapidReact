package frc.robot.subsystems;

import frc.robot.OperatorInterface;
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.*;
import frc.robot.*;

public class Shooter {

    private OperatorInterface oi;
    private double shooter_kF = 0;
    private double shooter_kP = .007;
    private double shooter_kD = 0;
    private double shooter_kI = 0.00000;// 1e-6
    private SupplyCurrentLimitConfiguration shooterLimit = new SupplyCurrentLimitConfiguration(true, 100, 20, 1000);
    private TalonFX shooter;
    private double shooterRpms = 98;

    private final int TIMEOUT = 0;
    private final double cLR = 0.1;

    public void init(OperatorInterface operatorinterface) {
        oi = operatorinterface;
        // Shooter Motor Config
        shooter = new TalonFX(Wiring.shooterMotor);

        shooter.set(TalonFXControlMode.PercentOutput, 0);

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

    }

    public void startShooter() {
        shooter.set(TalonFXControlMode.PercentOutput, -0.5);
    }

    public void stopShooter() {
        shooter.set(TalonFXControlMode.PercentOutput, 0);
    }

    public void shoot() {
        if (oi.autoShootButton()) {
            startShooter();
        }
        else{
            stopShooter();
        }
    }
}
