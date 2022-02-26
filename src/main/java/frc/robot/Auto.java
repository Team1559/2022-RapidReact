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
    static final int MAX_BALL_SECONDS = 5;

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
    public static int[][] basicVisionAuto = {
        { WAIT, 50 },
        { DRIVE_BALL, 12 },
        { START_GATHERER },
        { DRIVE, 14 },
        { WAIT, 10 },
        { STOP_GATHERER },
        { TURN, 180 },
        { DRIVE_HOOP, -1 },
        { START_FLYWHEEL, 9001 },
        { SHOOT },
        { STOP_FLYWHEEL }
    };

    public static int[][] minAuto = {
        { DRIVE, 96 },
    };
/*
    Drive to ball 71”
    Wait?
    Turn 180 degrees
    Align to target
    Shoot

    Turn left 100 degrees
    Drive 180”
    Drive to ball 82”
    Wait?
    Turn right 150 degrees
    Drive to hoop (until 8 ft away)
    Shoot
*/

    public static int[][] leftBallAuto = {
        { WAIT, 50 },
        // Get first ball (71" away)
        { DRIVE, 71-48 },
        { DRIVE_BALL, 12 },
        { START_GATHERER },
        { DRIVE, 14 },
        { WAIT, 10 },
        { STOP_GATHERER },
        // Shoot first ball
        { TURN, 180 },
        { DRIVE_HOOP, -1 },
        { WAIT, 10 },
        { START_FLYWHEEL, 9001 },
        { WAIT, 50 },
        { SHOOT },
        { STOP_FLYWHEEL },

        // Get second ball (82" away)
        { TURN, -100 },
        { DRIVE, 180+(82-48) },
        { DRIVE_BALL, 12 },
        { START_GATHERER },
        { DRIVE, 14 },
        { WAIT, 10 },
        { STOP_GATHERER },
        // Shoot second ball
        { TURN, 150 },
        { DRIVE_HOOP, 8 },
        { WAIT, 10 },
        { START_FLYWHEEL, 9001 },
        { WAIT, 50 },
        { SHOOT },
        { STOP_FLYWHEEL }
    };

/*
    Drive to ball 55”
    Wait?
    Turn 180 degrees
    Align to target
    Shoot

    Turn right 103 degrees
    Drive 172”
    Drive to ball 84”
    Wait for human player ball
    Turn left 150 degrees
    Drive to hoop (until 8 ft away)
    Shoot
*/
    public static int[][] rightBallAuto = {
        // Get 1st ball
        { DRIVE_BALL, 12 },
        { START_GATHERER },
        { DRIVE, 14 },
        { WAIT, 10 },
        { STOP_GATHERER },
        // Shoot 1st ball
        { TURN, 180 },
        { DRIVE_HOOP, -1 },
        { WAIT, 10 },
        { START_FLYWHEEL, 9001 },
        { WAIT, 50 },
        { SHOOT },
        { SHOOT },
        { STOP_FLYWHEEL },

        // Get 2nd ball
        { TURN, 103 },
        { DRIVE, 172 },
        { DRIVE_BALL, 12 },
        { START_GATHERER },
        { DRIVE, 14 },
        { WAIT, 10+50*2 },
        { STOP_GATHERER },
        // Shoot 2nd ball
        { TURN, -150 },
        { DRIVE_HOOP, 8 },
        { WAIT, 10 },
        { START_FLYWHEEL, 9001 },
        { WAIT, 50 },
        { SHOOT },
        { STOP_FLYWHEEL }
    };

/*
    Drive to ball 55”
    Wait?
    Turn 180 degrees
    Align to target
    Shoot

    Turn left 168.5 degrees
    Drive 80”
    Drive to ball 83”
    Wait for human player ball
    Turn right 168.5 degrees
    Drive to hoop (until 8 ft away)
    Shoot
*/
    public static double[][] midBallAuto = {
        // Get 1st ball
        { DRIVE_BALL, 12 },
        { START_GATHERER },
        { DRIVE, 14 },
        { WAIT, 10 },
        { STOP_GATHERER },
        // Shoot 1st 2 balls
        { TURN, 180 },
        { DRIVE_HOOP, -1 },
        { SHOOT },
        // GET terminal and human player ball
        { TURN, -168.5 },
        { DRIVE, 80 },
        { DRIVE_BALL, 12},
        { START_GATHERER },
        { DRIVE, 14 },
        { WAIT, 10+50*2 },
        { STOP_GATHERER },
        { TURN, 168.5 },
        { DRIVE_HOOP, 8 },
        { SHOOT }
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
        if(Math.abs(degrees - chassis.imu.yaw) % 360 < 1.5){
            chassis.imu.zeroYaw();
            Done();
        } else if (stepCounter > 50 * MAX_TURN_SECONDS)
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

    private void StartFlywheel(double rpm) {
        if(rpm == 9001)
            rpm = shooter.calculateShooterRPMS(Robot.vc.hoopx);
        shooter.startShooter(rpm);
        Done();
    }

    private void StopFlywheel() {
        shooter.stopShooter();
        Done();
    }

    private void Shoot() {
        shooter.startFeeder(shooter.feederSpeed);
        if (stepCounter >= FEEDER_CYCLES){
            shooter.stopFeeder();
            Done();
        }
    }

    private void DriveBall(int desiredDistanceFromBall) { // in inches
        double ySpeed = Robot.vc.ballx * 12 /* ft -> in */ * 0.4; // FIXME: I have no idea what this proportion should be (pid controller?)
        if(!Robot.vc.trackBall(ySpeed))
            Fail("No ball found");
        if(Robot.vc.ballx < desiredDistanceFromBall && Robot.vc.ballx != 0)
            Done();
        if(stepCounter > MAX_BALL_SECONDS * 50)
            Fail("Took too long");
    }

    private void DriveHoop(int desiredDistanceFromTarget) { // 8 ft
        if(stepCounter == 1)
            chassis.setVelocityMode();
        double ySpeed = Robot.vc.hoopx * 0.4;
        if(!Robot.vc.trackHoop(desiredDistanceFromTarget > 0 ? ySpeed : 0))
            Fail("No hoop found");
        else if(Robot.vc.hoopx < 0.5) // <-- in ft
            Done();
    }
}
