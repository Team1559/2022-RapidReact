package frc.robot;

import frc.robot.subsystems.*;
import frc.robot.components.*;

@SuppressWarnings("unused")
public class Auto {

    private int stepNumber = 0;
    private int stepCounter = 0;

    double leftTarget;
    double rightTarget;

    static final int WAIT = 0;
    static final int DRIVE = 1;
    static final int TURN = 2;
    static final int START_GATHERER = 3;
    static final int STOP_GATHERER = 4;
    static final int START_FLYWHEEL = 5;
    static final int STOP_FLYWHEEL = 6;
    static final int SHOOT = 7;
    static final int DRIVE_BALL = 8;
    static final int DRIVE_HOOP = 9;

    static final int FEEDER_CYCLES = 75;

    static final int MAX_TURN_SECONDS = 3;

    private OperatorInterface oi;
    private Shooter shooter;
    private Chassis chassis;

    private VisionData vData;

    private int[][] steps;
    // Start gatherer, drive X feet, stop gatherer, start flywheel at known RPM,
    // turn 180, shoot, stop flywheel
    public static int[][] basicAutoSteps = {
        { WAIT, 50 },
        { START_GATHERER },
        { DRIVE, 6 },
        { STOP_GATHERER },
        { START_FLYWHEEL, 2000 },
        { TURN, 180 },
        { SHOOT },
        { STOP_FLYWHEEL },
    };

    public Auto(){
        this(basicAutoSteps);
    }

    public Auto(int[][] steps) {
        this.steps = steps;
    }

    public void periodic() {
        if (stepNumber >= steps.length) {
            return;
        }
        int[] step = steps[stepNumber];
        int type = step[0];
        int value = 0;

        if (step.length > 1) {
            value = step[1];
        }
        stepCounter++;

        switch (type) {
            case WAIT:
                Wait(value);
                break;
            case DRIVE:
                Drive(value);
                break;
            case TURN:
                Turn(value);
                break;
            case START_GATHERER:
                StartGatherer();
                break;
            case STOP_GATHERER:
                StopGatherer();
                break;
            case START_FLYWHEEL:
                StartFlywheel(value);
                break;
            case STOP_FLYWHEEL:
                StopFlywheel();
                break;
            case SHOOT:
                Shoot();
                break;
            case DRIVE_BALL:
                DriveBall(value);
                break;
            case DRIVE_HOOP:
                DriveHoop(value);
                break;
        }
    }

    private void Done() {
        stepNumber++;
        stepCounter = 0;
    }
    
    private void Fail(String errorMessage) {
        stepNumber = steps.length;
        System.out.println("AUTO FAILED: " + errorMessage);
    }

    private void Wait(int cycles) {
        System.out.println("Wait: " + stepCounter + "/" + cycles);
        if (stepCounter >= cycles)
            Done();
    }

    private void Drive(int inches) {
        chassis.updateEncoders();
        double revs = chassis.inchesToRotations(inches);
        if (stepCounter == 1) {
            // establish setpoints for end of travel
            leftTarget = chassis.flep + revs;
            rightTarget = chassis.frep + revs;
            chassis.setPositionMode();
            chassis.setPositionTarget(leftTarget, rightTarget);
        }
        double remaining = leftTarget - chassis.flep;
        double done = revs - remaining;
        int inchesDone = (int) chassis.rotationsToInches(done);
        System.out.println("Drive: " + inchesDone + "/" + inches);

        if (remaining < 0.5)
            Done();
    }

    private void Turn(int degrees) {
        chassis.setVelocityMode();
        chassis.drive(0, chassis.degreesToZRotation(degrees));
        if(Math.abs(degrees - chassis.imu.yaw) < 1.5)
            Done();
        else if (stepCounter > 50 * MAX_TURN_SECONDS)
            Fail("Turned for too long");
    }

    private void StartGatherer() {
        shooter.lowerIntake();
        shooter.startIntake(shooter.intakeSpeed);
        Done();
    }

    private void StopGatherer() {
        shooter.stopIntake();
        Done();
    }

    private void StartFlywheel(int rpm) {
        shooter.startShooter(rpm);
        Done();
    }

    private void StopFlywheel() {
        shooter.stopShooter();
        Done();
    }

    private void Shoot() {
        shooter.startFeeder(shooter.feederSpeed);
        if (stepCounter >= FEEDER_CYCLES)
            Done();
    }

    private void DriveBall(int inchesToBall) {
        double ySpeed = Robot.vc.ballx * 0.4; // FIXME: I have no idea what this proportion should be (pid controller?)
        if(!Robot.vc.trackBall(ySpeed)) // FIXME: This check could lead to false fails
            Fail("No ball found");
    }

    private void DriveHoop(int desiredDistanceFromTarget) { // 8 ft
        if(stepCounter == 1)
            chassis.setVelocityMode();
        double ySpeed = Robot.vc.hoopx * 0.4; // FIXME: I have no idea what this proportion should be (pid controller?)
        if(!Robot.vc.trackHoop(ySpeed))
            Fail("No hoop found");
        else if(Robot.vc.hoopx < 0.5) // <-- in ft
            Done();
    }
}
