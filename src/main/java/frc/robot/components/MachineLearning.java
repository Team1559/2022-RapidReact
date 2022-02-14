package frc.robot.components;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;

public class MachineLearning {
    // private Chassis drivetrain;
    private ArrayList<Double> forwardSpeed = new ArrayList<Double>();
    private ArrayList<Double> sideSpeed = new ArrayList<Double>();
    private ArrayList<Double> frontLeftEncoderPosition = new ArrayList<Double>();
    private ArrayList<Double> frontRightEncoderPosition = new ArrayList<Double>();
    private ArrayList<Double> backLeftEncoderPosition = new ArrayList<Double>();
    private ArrayList<Double> backRightEncoderPosition = new ArrayList<Double>();
    private String filename;
    private final int MAX_SIZE = 1000;//Should only be 750 in length for 15 seconds

    public void executeCmd(String cmd) {
        String[] list = cmd.split(" ");
        if(list.length == 1) {
            executeCmds(list[0]);
        }
        else if(list.length == 2) {
            executeCmds(list[0], list[1]);
        }
        else if(list.length == 3) {
            executeCmds(list[0], list[1], list[2]);
        }
        else if(list.length == 4) {
            executeCmds(list[0], list[1], list[2], list[3]);
        }
    }

    private void executeCmds(String cmd) {
        executeCmds(cmd, "");
    }

    private void executeCmds(String cmd, String arg1) {
        executeCmds(cmd, arg1, "");
    }
    private void executeCmds(String cmd, String arg1, String arg2) {
        executeCmds(cmd, arg1, arg2, "");
    }
    
    @SuppressWarnings("unused")
    private void executeCmds(String cmd, String arg1, String arg2, String arg3) {
        String[] command =  {"sudo", cmd, arg1, arg2, arg3};
        ProcessBuilder builder = new ProcessBuilder(command);
        builder = builder.directory(new File("/"));
        try {
            Process p = builder.start();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    // private int counter = 0;
    public MachineLearning(boolean record, String filename) {
        this.filename = filename + ".txt";
        forwardSpeed.clear();
        sideSpeed.clear();
        frontLeftEncoderPosition.clear();
        frontRightEncoderPosition.clear();
        backLeftEncoderPosition.clear();
        backRightEncoderPosition.clear();
        if(record) {
        executeCmd("mkdir /paths");
        executeCmd("chmod a=rwx /paths");
        try {
            File myObj = new File("/paths/" + filename);
            if (myObj.createNewFile())  {
                System.out.println("File created: " + myObj.getName());
            } else  {
                System.out.println("File already exists.");
            }
            } catch (IOException e)  {
            System.out.println("An error occurred.");
            e.printStackTrace();
            }
        }
    }
    public void init(){
        forwardSpeed.clear();
        sideSpeed.clear();
        frontLeftEncoderPosition.clear();
        frontRightEncoderPosition.clear();
        backLeftEncoderPosition.clear();
        backRightEncoderPosition.clear();
    }

    public void periodic(Double _forwardSpeed, Double _sideSpeed, Double flep, Double frep, Double blep, Double brep) {
        if(forwardSpeed.size() <= MAX_SIZE) {
            forwardSpeed.add(_forwardSpeed);
            sideSpeed.add(_sideSpeed);
            frontLeftEncoderPosition.add(flep);
            frontRightEncoderPosition.add(frep);
            backLeftEncoderPosition.add(blep);
            backRightEncoderPosition.add(brep);
        }
        else{
            System.out.println("Max recording size has been reached");
        }
    }

    public void write()  {
        try {
            FileWriter myWriter = new FileWriter("/paths/"+ filename);
            String out = "";
            for(int i = 0; i < forwardSpeed.size(); i++) {
                out += (forwardSpeed.get(i) + " " + sideSpeed.get(i) + " " + frontLeftEncoderPosition.get(i) + " " + frontRightEncoderPosition.get(i) + " " + backLeftEncoderPosition.get(i) + " " + backRightEncoderPosition.get(i) + " \n");
            }
            myWriter.write(out);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } 
        catch (IOException e)  {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public double interpolate(double counter, double[] value) {
        int intCounter = (int)counter;
        double percent = (counter -intCounter); 
        if(intCounter < value.length-1) {
            double interpolatedValue = (value[intCounter] + (percent * (value[intCounter+1] - value[intCounter])));
            return interpolatedValue;
        }
        else { 
            return value[value.length-1];
        }
    }
}