package frc.robot.components;

/**
 * This class is responciple for reciving data from the jetson and structuring
 * it it also writes it to the VisionData class
 */
public class Vision implements Runnable {
    private UDPClient client;
    private static Vision instance;
    private VisionData VData;
    private Thread clientThread;

    double hoopCameraYOffset = 0;
    double hoopCameraXOffset = 0;
    double hoopCameraROffset = 0;
    double ballCameraYOffset = 0;
    double ballCameraXOffset = 0;
    double ballCameraROffset = 0;

    /**
     * Creates new vision object
     */
    public Vision() {
        clientThread = new Thread(this);
        client = new UDPClient();
        VData = new VisionData();
        VData.hoopStatus = 0;
        VData.ballStatus = 0;
        clientThread.start();

    }

    /**
     * Gets new vision data and parses it
     */
    @Override
    public void run() {
        try {
            VisionData NewData = new VisionData();
            NewData.hoopStatus = 2;
            NewData.ballStatus = 2;
            String in = client.getData();

            if (in != null) {
                // System.out.println(in);
                String[] parameters = in.split(" ");

                if (parameters.length >= 9) {
                    NewData.ballStatus = Integer.parseInt(parameters[7]);
                    NewData.hoopStatus = Integer.parseInt(parameters[6]);

                    if (NewData.ballStatus == 1) {
                        NewData.br = -(Double.parseDouble(parameters[3]) - ballCameraXOffset);
                        NewData.bx = Double.parseDouble(parameters[5]) - ballCameraYOffset;
                        NewData.by = Double.parseDouble(parameters[4]) - ballCameraYOffset;
                    }

                    if (NewData.hoopStatus == 1) {
                        NewData.hx = Double.parseDouble(parameters[0]) - hoopCameraXOffset;
                        NewData.hy = Double.parseDouble(parameters[2]) - hoopCameraYOffset; // Always 0
                        NewData.hr = Double.parseDouble(parameters[1]) - hoopCameraROffset;
                    }

                    if (Integer.parseInt(parameters[8]) == 1) {
                        NewData.waitForOtherRobot = true;
                    }

                    else {
                        NewData.waitForOtherRobot = false;
                    }
                }
            }
            VData = NewData;
        } catch (NumberFormatException | NullPointerException e) {
            System.err.println(e.toString());
        }
    }

    /**
     * Gets the most current data
     * 
     * @return The most current data
     */
    public VisionData getData() {
        return VData;
    }

    /**
     * Gets the intance
     * 
     * @return The instance
     */
    public static Vision getInstance() {
        if (instance == null) {
            instance = new Vision();
        }

        return instance;
    }
}
