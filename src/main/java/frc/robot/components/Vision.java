package frc.robot.components;

public class Vision {
    UDPClient client;

    private VisionData VData;
    double hoopCameraYOffset = 0;
    double hoopCameraXOffset = 0;// 5.0;
    double hoopCameraROffset = 0;
    double ballCameraYOffset = 0;
    double ballCameraXOffset = 0;
    double ballCameraROffset = 0;

    public Vision() {
        client = new UDPClient();
        VData = new VisionData();
        VData.hoopStatus = 0;
        VData.ballStatus = 0;
    }

    public void VisionInit() {
        // client.run();
    }

    public void update() {
        try {
            VisionData NewData = new VisionData();
            NewData.hoopStatus = 2;
            NewData.ballStatus = 2;

            String in = client.get();
            System.out.println(in);

            if (in != null) {
                String[] parameters = in.split(" ");

                if (parameters.length >= 9) {
                    NewData.ballStatus = Integer.parseInt(parameters[6]);
                    NewData.hoopStatus = Integer.parseInt(parameters[7]);
                    if(NewData.ballStatus == 1){
                        NewData.br = -(Double.parseDouble(parameters[3]) - ballCameraXOffset);
                        NewData.by = Double.parseDouble(parameters[4]) - ballCameraYOffset;
                        NewData.bx = Double.parseDouble(parameters[5]) - ballCameraYOffset;
                    }
                    if(NewData.hoopStatus == 1){
                        NewData.hx = -(Double.parseDouble(parameters[0]) - hoopCameraXOffset);
                        NewData.hy = Double.parseDouble(parameters[1]) - hoopCameraYOffset;
                        NewData.hr = Double.parseDouble(parameters[2]) - hoopCameraROffset;
                    }
                    if (Integer.parseInt(parameters[8]) == 1) {
                        NewData.waitForOtherRobot = true;
                    } else {
                        NewData.waitForOtherRobot = false;
                    }

                }
            }
            VData = NewData;
        } catch (NumberFormatException | NullPointerException e) {
            // System.err.println(e.toString());
        }
    }

    public VisionData getData() {
        update();
        return VData;
    }

    private static Vision instance;

    public static Vision getInstance() {
        if (instance == null) {
            instance = new Vision();
        }

        return instance;
    }

}
