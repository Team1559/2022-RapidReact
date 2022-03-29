package frc.robot.components;

public class Vision {
    private UDPClient client;

    public int ballStatus = 2;
    public int hoopStatus = 2;
    public double hx;
    public double hy;
    public double hr;
    public double bx;
    public double by;
    public double br;

    public Vision() {
    }

    public void update() {
        try {
            this.hoopStatus = 2;
            this.ballStatus = 2;
            String in = client.get();

            if (in != null) {
                // System.out.println(in);
                String[] parameters = in.split(" ");

                if (parameters.length >= 9) {
                    this.ballStatus = Integer.parseInt(parameters[7]);
                    this.hoopStatus = Integer.parseInt(parameters[6]);

                    if (this.ballStatus == 1) {
                        this.br = -Double.parseDouble(parameters[3]);
                        this.bx = Double.parseDouble(parameters[5]);
                        this.by = Double.parseDouble(parameters[4]);
                    }

                    if (this.hoopStatus == 1) {
                        this.hx = Double.parseDouble(parameters[0]);
                        this.hy = Double.parseDouble(parameters[2]); // Always 0
                        this.hr = Double.parseDouble(parameters[1]);
                    }
                }
            }
        } catch (NumberFormatException | NullPointerException e) {
            e.printStackTrace(System.err);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public boolean isHoopValid(){
        return this.hoopStatus == 1;
    }

    public boolean isBallValid(){
        return this.ballStatus == 1;
    }
}
