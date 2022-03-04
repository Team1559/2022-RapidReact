package frc.robot.subsystems;

import java.rmi.registry.LocateRegistry;

import frc.robot.*;
import frc.robot.components.*;

@SuppressWarnings("unused")
public class Auto {

    private int stepNumber = 0;
    private int stepCounter = 0;

    private double leftTarget;
    private double rightTarget;

    private static final int WAIT = 0;
    private static final int DRIVE = 1;
    private static final int TURN = 2;
    private static final int START_GATHERER = 3;
    private static final int STOP_GATHERER = 4;
    private static final int START_FLYWHEEL = 5;
    private static final int STOP_FLYWHEEL = 6;
    private static final int SHOOT = 7;
    private static final int DRIVE_BALL = 8;
    private static final int DRIVE_HOOP = 9;
    private static final int ALIGN_HOOP = 10;

    private static final int FEEDER_CYCLES = 75;

    private static final int MAX_TURN_SECONDS = 3;
    private static final int MAX_BALL_SECONDS = 5;

    static final int HOOP_ERROR_INCHES = 3;
    static final int HOOP_ERROR_DEGREES = 1;

    private Robot robot;

    private VisionData vData;

    private int[][] steps;

    // No Auto
    public static final int[][] noAuto = {
    };

    // Start gatherer, drive X feet, stop gatherer, start flywheel at known RPM,
    // turn 180, shoot, stop flywheel
    public static final int[][] basicAutoSteps = {
            { WAIT, 50 },
            { START_GATHERER },
            { DRIVE, 60 },
            { STOP_GATHERER },
            { START_FLYWHEEL, 2000 },
            { TURN, 180 },
            { SHOOT },
            { STOP_FLYWHEEL },
    };

    public static final int[][] basicVisionAuto = {
            { WAIT, 50 },
            { DRIVE_BALL, 48 },
            { START_GATHERER },
            { DRIVE, 48 + 3 },
            { WAIT, 30 },
            { STOP_GATHERER },
            { TURN, 180 },
            { START_FLYWHEEL, 0 },
            { ALIGN_HOOP },
            { WAIT, 50 },
            { SHOOT },
            { STOP_FLYWHEEL }
    };

    public static final int[][] minAuto = {
            { DRIVE, 24 },
    };
    /*
     * Drive to ball 71”
     * Wait?
     * Turn 180 degrees
     * Align to target
     * Shoot
     * 
     * Turn left 100 degrees
     * Drive 180”
     * Drive to ball 82”
     * Wait?
     * Turn right 150 degrees
     * Drive to hoop (until 8 ft away)
     * Shoot
     */

    public static final int[][] leftBallAuto = {
            // Get first ball (71" away)
            { DRIVE_BALL, 48 },
            { START_GATHERER },
            // make sure we get the ball by going a few extra inches and waiting a few ticks
            { DRIVE, 48 + 3 },
            { WAIT, 30 },
            { STOP_GATHERER },
            // Shoot first two balls
            { TURN, 180 },
            { START_FLYWHEEL, 0 },
            { ALIGN_HOOP },
            { WAIT, 50 },
            { SHOOT },
            { STOP_FLYWHEEL },

            // Get third ball (82" away)
            { TURN, -100 },
            { DRIVE_BALL, 48 },
            { START_GATHERER },
            // make sure we get the ball by going a few extra inches
            { DRIVE, 48 + 3 },
            // wait 2 sec to give the human time to give us the fourth ball
            { WAIT, 2 * 50 },
            { STOP_GATHERER },
            // Shoot third and fourth balls
            { TURN, 150 },
            { DRIVE_HOOP, 8 },
            { START_FLYWHEEL, 0 },
            { WAIT, 50 },
            { SHOOT },
            { STOP_FLYWHEEL }
    };

    /*
     * Drive to ball 55”
     * Wait?
     * Turn 180 degrees
     * Align to target
     * Shoot
     * 
     * Turn right 103 degrees
     * Drive 172”
     * Drive to ball 84”
     * Wait for human player ball
     * Turn left 150 degrees
     * Drive to hoop (until 8 ft away)
     * Shoot
     */
    public static final int[][] rightBallAuto = {
            // Get 1st ball
            { DRIVE_BALL, 48 },
            // make sure we get the ball by going a few extra inches and waiting a few ticks
            { DRIVE, 48 + 3 },
            { WAIT, 30 },
            { STOP_GATHERER },
            // Shoot 1st two balls
            { TURN, 180 },
            { START_FLYWHEEL, 0 },
            { ALIGN_HOOP },
            { WAIT, 50 },
            { SHOOT },
            { STOP_FLYWHEEL },

            // Get third ball
            { TURN, 103 },
            { DRIVE, 172 },
            { DRIVE_BALL, 48 },
            { START_GATHERER },
            { DRIVE, 48 + 3 },
            // wait 2 sec to give the human time to give us the fourth ball
            { WAIT, 2 * 50 },
            { STOP_GATHERER },
            // Shoot third and fourth balls
            { TURN, -150 },
            { DRIVE_HOOP, 8 },
            { START_FLYWHEEL, 0 },
            { WAIT, 50 },
            { SHOOT },
            { STOP_FLYWHEEL }
    };

    /*
     * Drive to ball 55”
     * Wait?
     * Turn 180 degrees
     * Align to target
     * Shoot
     * 
     * Turn left 168 degrees
     * Drive 80”
     * Drive to ball 83”
     * Wait for human player ball
     * Turn right 168 degrees
     * Drive to hoop (until 8 ft away)
     * Shoot
     */
    public static final int[][] midBallAuto = {
            // Get 1st ball
            { DRIVE_BALL, 48 },
            { START_GATHERER },
            { DRIVE, 48 + 3 },
            { WAIT, 30 },
            { STOP_GATHERER },
            // Shoot 1st 2 balls
            { TURN, 180 },
            { ALIGN_HOOP },
            { START_FLYWHEEL, 0 },
            { WAIT, 50 },
            { SHOOT },
            { STOP_FLYWHEEL },
            // Get terminal and human player ball
            { TURN, -168 },
            { DRIVE, 80 },
            { DRIVE_BALL, 48 },
            { START_GATHERER },
            { DRIVE, 48 + 3 },
            { WAIT, 2 * 50 },
            { STOP_GATHERER },
            { TURN, 168 },
            { DRIVE_HOOP, 8 },
            { START_FLYWHEEL, 0 },
            { WAIT, 50 },
            { SHOOT },
            { STOP_FLYWHEEL }
    };

    public Auto(Robot robot) {
        this(robot, basicAutoSteps);
    }

    public Auto(Robot robot, int[][] steps) {
        this.robot = robot;
        this.steps = steps;
    }

    public void main() {
        if (stepNumber >= steps.length) {
            return;
        }
        int[] step = steps[stepNumber];
        int type = (int) step[0];
        int value = 0;

        if (step.length > 1) {
            value = step[1];
        }
        stepCounter++;

        switch (type) {
            case WAIT:
                Wait((int) value);
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
            case ALIGN_HOOP:
                AlignHoop();
                break;
        }
    }

    private void Done() {
        stepNumber++;
        stepCounter = 0;
    }

    private void Fail(String errorMessage) {
        stepNumber = steps.length;
        System.err.println("AUTO FAILED: " + errorMessage);
    }

    private void Wait(int cycles) {
        System.out.println("Wait: " + stepCounter + "/" + cycles);
        if (stepCounter >= cycles)
            Done();
    }

    private void Drive(int inches) {
        robot.chassis.updateEncoders();
        double revs = robot.chassis.inchesToRevolutions(inches);
        if (stepCounter == 1) {
            // establish setpoints for end of travel
            leftTarget = robot.chassis.flep + revs;
            rightTarget = robot.chassis.frep + revs;


        }
        double remaining = (rightTarget - robot.chassis.frep);
        System.out.println("remaining: " + remaining);
        double driveValue = remaining * Chassis.CHASSIS_GEAR_RATIO * 1.0 / 20;
        System.out.println("Drive value: " + driveValue);
        System.out.println("Encoder position: " + robot.chassis.frep);
        if (stepCounter <= 100) {
            robot.chassis.drive(-driveValue, 0, false);
        } else {
            robot.chassis.drive(0, 0, false);
        }
        double done = revs - remaining;
        System.out.println("Done:" + done);
        int inchesDone = (int) robot.chassis.revolutionsToInches(done);
        System.out.println("Drive: " + inchesDone + "/" + inches);

        if (Math.abs(inchesDone - inches) < 0.5)
            Done();
    }

    private void Turn(int degrees) {
        robot.chassis.drive(0, robot.chassis.degreesToZRotation(degrees));
        if (Math.abs(degrees - robot.chassis.imu.yaw) % 360 < 1.5) {
            robot.chassis.imu.zeroYaw();
            Done();
        } else if (stepCounter > 50 * MAX_TURN_SECONDS)
            Fail("Turned for too long");
    }

    private void StartGatherer() {
        robot.shooter.gathererState = Shooter.gathererDown;
        robot.shooter.disableManual = true;
        Done();
    }

    private void StopGatherer() {
        robot.shooter.gathererState = Shooter.holding;
        robot.shooter.disableManual = false;
        Done();
    }

    private void StartFlywheel(double rpm) {
        if (rpm == 0)
            rpm = robot.shooter.calculateShooterRPMS(Robot.vc.hoopx);
        robot.shooter.startShooter(rpm);
        Done();
    }

    private void StopFlywheel() {
        robot.shooter.stopShooter();
        Done();
    }

    private void Shoot() {
        robot.shooter.startFeeder(robot.shooter.feederSpeed);
        if (stepCounter >= FEEDER_CYCLES) {
            robot.shooter.holdFeeder();
            Done();
        }
    }

    private void DriveBall(int desiredDistanceFromBall) { // in inches
        double positionError = desiredDistanceFromBall - Robot.vc.ballx * 12;
        double ySpeed = positionError * 0.01;

        if (!Robot.vc.trackBall(ySpeed))
            Fail("No ball found");
        if (Robot.vc.ballx < desiredDistanceFromBall && Robot.vc.ballx != 0)
            Done();
        if (stepCounter > MAX_BALL_SECONDS * 50)
            Fail("Took too long");
    }

    private void DriveHoop(int desiredDistanceFromTarget) { // in inches
        double positionError = desiredDistanceFromTarget - Robot.vc.hoopx * 12;
        double ySpeed = positionError * 0.01;

        if (!Robot.vc.trackHoop(ySpeed))
            Fail("No hoop found");
        else if (positionError < HOOP_ERROR_INCHES && Math.abs(Robot.vc.hoopr) < HOOP_ERROR_DEGREES)
            Done();
    }

    private void AlignHoop() {
        if (!Robot.vc.trackHoop(0))
            Fail("No hoop found");
        else if (Math.abs(Robot.vc.hoopr) < HOOP_ERROR_DEGREES)
            Done();
    }
}
