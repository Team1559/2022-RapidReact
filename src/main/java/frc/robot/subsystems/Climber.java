package frc.robot.subsystems;

import frc.robot.OperatorInterface;
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.*;
import com.ctre.phoenix.time.StopWatch;

import frc.robot.*;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Climber {

    private OperatorInterface oi;
    private Shooter shooter;
    private TalonSRX relay = new TalonSRX(Wiring.RELAY_SRX);
    DigitalInput upperLimitSwitch = new DigitalInput(Wiring.upperLimitSwitchInput);
    DigitalInput lowerLimitSwitch = new DigitalInput(Wiring.lowerLimitSwitchInput);
    private boolean wait = true;

    private SupplyCurrentLimitConfiguration climberLimit = new SupplyCurrentLimitConfiguration(true, 80, 90, 0);
    private final int TIMEOUT = 0;
    private final double cLR = 0.1;

    private boolean disable = false;
    private boolean press = false;

    private final double CLIMBER_DOWN_RPMS = 2500;
    private final double CLIMBER_UP_RPMS = 1600;

    private boolean resetEncoder = true;

    // TODO Tune this PID controller, It will need an I value
    private final double kF = 0.015;
    private final double kP = 0.15;
    private final double kD = 0.06;
    private final double kI = 0.000;
    private final double kiz = 0;

    private final double pkF = 0.0;
    private final double pkP = 0.07;
    private final double pkD = 0.06;
    private final double pkI = 0.000;
    private final double pkiz = 0;
    private StopWatch watch = new StopWatch();
    private final double MAX_WAIT = 0.125;
    private boolean disengageSolenoid = false;

    private TalonFX climber;
    private Solenoid climberSolenoid;

    public Climber(OperatorInterface oi, Shooter shooter) {
        this.oi = oi;
        this.shooter = shooter;
        climberSolenoid = new Solenoid(Wiring.PNEUMATICS_HUB, PneumaticsModuleType.REVPH, Wiring.CLIMBER_SOLENOID);

        // MotorController Config

        climber = new TalonFX(Wiring.climberMotor);

        // climber Velocity mode configs
        climber.set(TalonFXControlMode.Velocity, 0.0);
        climber.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, 0, TIMEOUT);
        climber.configClosedloopRamp(cLR, TIMEOUT);
        climber.configNominalOutputForward(0, TIMEOUT);
        climber.configNominalOutputReverse(0, TIMEOUT);
        climber.configPeakOutputForward(+1, TIMEOUT);
        climber.configPeakOutputReverse(-1, TIMEOUT);
        climber.setNeutralMode(NeutralMode.Brake);
        climber.configSupplyCurrentLimit(climberLimit, TIMEOUT);
        climber.config_kF(0, kF, TIMEOUT);
        climber.config_kP(0, kP, TIMEOUT);
        climber.config_kD(0, kD, TIMEOUT);
        climber.config_kI(0, kI, TIMEOUT);
        climber.config_kF(0, kF, TIMEOUT);
        climber.config_IntegralZone(0, kiz, TIMEOUT);

        climber.config_kP(1, pkP, TIMEOUT);
        climber.config_kD(1, pkD, TIMEOUT);
        climber.config_kI(1, pkI, TIMEOUT);
        climber.config_kF(1, pkF, TIMEOUT);
        climber.config_IntegralZone(1, pkiz, TIMEOUT);
        climber.setInverted(true);
        climber.selectProfileSlot(0, 0);

        relay.configFactoryDefault();
        relay.setNeutralMode(NeutralMode.Coast);
    }

    private void solenoidMain() {
        if (disengageSolenoid) {
            relay.set(TalonSRXControlMode.PercentOutput, 1);
        } else {
            relay.set(TalonSRXControlMode.PercentOutput, 0);
        }
    }

    public void disengageSolenoid() {
        disengageSolenoid = true;
        if (wait) {
            watch.start();
        }
        wait = false;
    }

    public void engageSolenoid() {
        wait = true;
        disengageSolenoid = false;
    }

    public void main() {

        solenoidMain();
        climber.setNeutralMode(NeutralMode.Brake);

        SmartDashboard.putBoolean("Upper Limit Switch", upperLimitSwitch.get());
        SmartDashboard.putBoolean("Lower Limit Switch", lowerLimitSwitch.get());
        SmartDashboard.putNumber("Climber rpm", climber.getSelectedSensorVelocity() * 10 * 60 / 2048);

        // Control for Winch
        if (oi.climberEnableButton()) {
            disengageSolenoid(); // disengage the solenoid once enabled
            if (oi.extendClimberPistonsButton()) {
                extendPistons();
            } else if (oi.retractClimberPistonsButton()) {
                retractPistons();
            }
            // Lower limit switch is hit when the robot is up high
            if (oi.climberUpButton() && !LowerLimitHit()) {
                disengageSolenoid(); // disengage the solenoid once enabled

                if (watch.getDuration() >= MAX_WAIT) {
                    raiseRobot();
                }
            } else if (oi.climberDownButton() && !UpperLimitHit()) {
                disengageSolenoid(); // disengage the solenoid once enabled

                if (watch.getDuration() >= MAX_WAIT) {
                    lowerRobot();
                }
            } else {
                holdRobot();
            }
        } else {
            holdRobot();
        }

    }

    private boolean UpperLimitHit() {
        // The magnetic limit switches are active low inputs
        return !upperLimitSwitch.get();
    }

    private boolean LowerLimitHit() {
        // The magnetic limit switches are active low inputs
        return !lowerLimitSwitch.get();
    }

    public void raiseRobot() {
        if (!resetEncoder) {
            climber.selectProfileSlot(0, 0);
            resetEncoder = true;
        }
        climber.set(TalonFXControlMode.Velocity, CLIMBER_UP_RPMS * 2048 / 10 / 60);
    }

    public void lowerRobot() {
        if (!resetEncoder) {
            climber.selectProfileSlot(0, 0);
            resetEncoder = true;
        }
        climber.set(TalonFXControlMode.Velocity, -CLIMBER_DOWN_RPMS * 2048 / 10 / 60);
    }

    public void zeroClimber() {
        climber.setSelectedSensorPosition(0);
    }

    public void holdRobot() {
        oi.copilot.stopRumble();
        if (resetEncoder) {
            climber.setSelectedSensorPosition(0);
            resetEncoder = false;
            climber.selectProfileSlot(1, 0);
        }
        climber.set(TalonFXControlMode.Position, 0);
    }

    public void stopClimber() {
        climber.set(TalonFXControlMode.PercentOutput, 0.0);
    }

    public void extendPistons() {
        shooter.disableManual = true;
        shooter.gathererState = shooter.gathererDown;
        climberSolenoid.set(true);
    }

    public void retractPistons() {
        shooter.disableManual = true;
        shooter.gathererState = shooter.gathererUp;
        climberSolenoid.set(false);
    }

    public void disable() {
        shooter.gathererState = Shooter.gathererUp;
    }

    public void testPeriodic() {
        if (oi.testClimber()) {
            if (!press)
                disable = !disable;
            press = true;
        } else
            press = false;

        if (disable) {
            relay.set(TalonSRXControlMode.PercentOutput, 1);
        } else {
            relay.set(TalonSRXControlMode.PercentOutput, 0);
        }
    }

    public void testInit() {
        stopClimber();
        climber.setNeutralMode(NeutralMode.Coast);
        retractPistons();
    }

}
