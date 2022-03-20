package frc.robot.components;

/**
 * This class is responciple for reciving data from the jetson and structuring
 * it. It also writes the data to the VisionData class
 */
public class Vision implements Runnable {
    private UDPClient client;
    private static Vision instance;
    private Thread clientThread;
    private double hoopCameraYOffset = 0;
    private double hoopCameraXOffset = 0;
    private double hoopCameraROffset = 0;
    private double ballCameraYOffset = 0;
    private double ballCameraXOffset = 0;
    private double ballCameraROffset = 0;

    /**
     * Creates new vision object
     */
    public Vision() {
        clientThread = new Thread(this);
        client = new UDPClient();
        clientThread.start();
    }

    /**
     * Gets new vision data and parses it
     */
    @Override
    public void run() {
        while (true) {
            try {
                String in = client.getData();
                VisionData.restoreDefaults();

                if (in != null) {
                    // System.out.println(in);
                    String[] parameters = in.split(" ");

                    if (parameters.length >= 9) {
                        VisionData.ballStatus = Integer.parseInt(parameters[7]);
                        VisionData.hoopStatus = Integer.parseInt(parameters[6]);

                        if (VisionData.ballStatus == 1) {
                            VisionData.br = -(Double.parseDouble(parameters[3]) - ballCameraXOffset);
                            VisionData.bx = Double.parseDouble(parameters[5]) - ballCameraYOffset;
                            VisionData.by = Double.parseDouble(parameters[4]) - ballCameraROffset;
                        }

                        if (VisionData.hoopStatus == 1) {
                            VisionData.hx = Double.parseDouble(parameters[0]) - hoopCameraXOffset;
                            VisionData.hy = Double.parseDouble(parameters[2]) - hoopCameraYOffset; // Always 0
                            VisionData.hr = Double.parseDouble(parameters[1]) - hoopCameraROffset;
                        }

                        if (Integer.parseInt(parameters[8]) == 1) {
                            VisionData.waitForOtherRobot = true;
                        }

                        else {
                            VisionData.waitForOtherRobot = false;
                        }
                    }
                }
            } catch (NumberFormatException | NullPointerException e) {
                System.err.println(e.toString());
            }
        }
    }

    /**
     * Gets the most current data
     * 
     * @return The most current data
     */
    public VisionData getData() {
        return new VisionData();
    }

    /**
     * Gets the instance
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
