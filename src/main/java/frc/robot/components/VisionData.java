package frc.robot.components;

public class VisionData {
    
    public double x;
    public double y;
    public double r;
    public Integer status;

    public boolean IsValid(){
        if(status!=1){
            return false;
        }
        else
            return true;
    }

    public void Print() {
        // System.out.printf("%3.1f %3.1f  %3.1f  %d\n", x, y, r, status);
    }
}