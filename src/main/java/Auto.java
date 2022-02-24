public class Auto {
    private int stepNumber = 0;
    private int stepCounter = 0;

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

    private int[][] steps;

    // Start gatherer, drive X feet, stop gatherer, start flywheel at known RPM,
    // turn 180, shoot, stop flywheel
    public int[][] basicAutoSteps = {
            { WAIT, 50 },
            { START_GATHERER },
            { DRIVE, 6 },
            { STOP_GATHERER },
            { START_FLYWHEEL, 2000 },
            { TURN, 180 },
            { SHOOT },
            { STOP_FLYWHEEL },
    };

    public Auto(int[][] stepsToRun) {
        steps = stepsToRun;
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

    private void Wait(int cycles) {
        if (stepCounter >= cycles) {
            Done();
        }
    }

    private void Drive(int inches) {
        Done();
    }

    private void Turn(int degrees) {
        Done();
    }

    private void StartGatherer() {
        Done();
    }

    private void StopGatherer() {
        Done();
    }

    private void StartFlywheel(int rpm) {
        Done();
    }

    private void StopFlywheel() {
        Done();
    }

    private void Shoot() {
        Done();
    }

    private void DriveBall(int inches) {
        Done();
    }

    private void DriveHoop(int inches) {
        Done();
    }
}
