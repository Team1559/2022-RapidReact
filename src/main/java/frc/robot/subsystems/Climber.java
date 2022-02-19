package frc.robot.subsystems;

import frc.robot.OperatorInterface;
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.*;
import frc.robot.*;
import com.revrobotics.*;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;

@SuppressWarnings("unused")

public class Climber {

    private OperatorInterface oi;

    private SupplyCurrentLimitConfiguration climberLimit = new SupplyCurrentLimitConfiguration(true, 20, 20, 0);
    private final int TIMEOUT = 0;
    private final double cLR = 0.1;

    private double climber_kF = 0.045;
    private double climber_kP = 0.4;
    private double climber_kD = 0;
    private double climber_kI = 0.000;
    private double climberRpms = 7500;

    private TalonFX climber;
//    private Solenoid ;
    private VisionControl vc;


    public Climber(OperatorInterface operatorinterface) {
        oi = operatorinterface;
        this.vc = Robot.vc;

        // MotorController Config

        climber = new TalonFX(Wiring.climberMotor);

        // climber Velocity mode configs
        climber.set(0.0, TalonFXControlMode.PercentOutput)
        climber.configClosedloopRamp(cLR, TIMEOUT);
        climber.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor); // climber.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
        climber.config_kF(0, climber_kF);
        climber.config_kP(0, climber_kP);
        climber.config_kD(0, climber_kD);
        climber.config_kI(0, climber_kI);
        climber.configNominalOutputForward(0, TIMEOUT);
        climber.configNominalOutputReverse(0, TIMEOUT);
        climber.configPeakOutputForward(+1, TIMEOUT);
        climber.configPeakOutputReverse(-1, TIMEOUT);
        climber.setNeutralMode(NeutralMode.Brake);
        climber.configSupplyCurrentLimit(climberLimit);

    }

    public void runclimber() {
        // System.out.println(climber.getSelectedSensorVelocity()); PRINTS FOR VELOCITY
        // CONTROL

        // Control for FlyWheel
        if (oi.DPadUp()) {
            startclimber(climberRpms);
        } else {
            stopclimber();
        }

        // Control for lowering intake
        if (oi.manualIntakeButton() && state == gathererUp) {
            lowerIntake();
            startIntake();
        } else if (!oi.manualIntakeButton() && state == gathererDown) {
            stopIntake();
        } else if (oi.manualIntakeButton() && state == holding) {
            stopIntake();
            raiseIntake();
        }

    }

    public void startclimber(double rpms) {
        climber.set(1.0, TalonFXControlMode.PercentOutput);
    }

    public void stopclimber() {
        climber.set(TalonFXControlMode.PercentOutput, 0);
    }

    public void lowerIntake() {
        lowerIntake.set(true);
    }

    public void raiseIntake() {
        lowerIntake.set(false);
    }
}
