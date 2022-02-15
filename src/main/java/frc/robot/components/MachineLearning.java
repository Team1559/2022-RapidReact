package frc.robot.components;
import java.util.ArrayList;
import java.io.*;

public class MachineLearning {
    private ArrayList<Double> forwardSpeed = new ArrayList<Double>();
    private ArrayList<Double> sideSpeed = new ArrayList<Double>();
    private ArrayList<Double> frontLeftEncoderPosition = new ArrayList<Double>();
    private ArrayList<Double> frontRightEncoderPosition = new ArrayList<Double>();
    private ArrayList<Double> backLeftEncoderPosition = new ArrayList<Double>();
    private ArrayList<Double> backRightEncoderPosition = new ArrayList<Double>();
    private String filename;
    private static String OS = null;
    private final int MAX_SIZE = 2000; //Should only need to be 750 in length for 15 seconds

    public MachineLearning(boolean record, String filename) {
        if(filename.equals("")){
            this.filename = "Default_Path" + ".txt";
        }
        else{
            this.filename = filename + ".txt";
        }
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

                if (myObj.createNewFile()) {
                    System.out.println("File created: " + myObj.getName());
                } 
                
                else  {
                    System.out.println("File already exists.");
                }
            } 
            catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }
    }

    public void init() {
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

        else {
            System.out.println("Max recording size has been reached");
        }
    }

    public void write() {
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

    public static String getOsName() {
        if(OS == null) { 
            OS = System.getProperty("os.name"); 
        }

        return OS;
    }
    
    public static boolean isWindows() {
        return getOsName().startsWith("Windows");
    }

    public static boolean isUnix() {
        return !isWindows();
    }

    /**
    Runs commands on the system starting at the root directory of the os.
    Use //s instead of spaces, so for example making a directory "test 1" is done with executeCmd("mkdir test//s1");
    @param cmd the command you want to run
    @return the output of the command
    */
    private String executeCmd(String cmd) {
        return executeCmd(cmd, "/");
    }

    /**
    Runs commands on the system starting at the specified directory.
    Use //s instead of spaces, so for example making a directory "test 1" is done with executeCmd("mkdir test//s1", "/");
    @param cmd the command you want to run
    @param startingDir the starting directory of the command
    @return the output of the command
    */
    private String executeCmd(String cmd, String startingDir) {
        cmd = cmd.replaceAll("//S", "//s");
        
        if(startingDir == ""){
            startingDir = "/";
        }

        String[] split = cmd.split(" ");
        String[] command = new String[split.length + 1];
        String out = "";

        if(isUnix()) {
            command[0] = "sudo";
        }

        else {
            command[0] = "powershell";
        }

        for(int i = 1; i < command.length; i++) {
            command[i] = split[i - 1].replaceAll("//s"," ");
        }

        ProcessBuilder builder = new ProcessBuilder(command);
        builder = builder.directory(new File(startingDir));

        try {
            Process p = builder.start();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()), 1);
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                out += line + "\n";
            }
        }

        catch(IOException e) {
            e.printStackTrace();
        }

        return out;
    }
}
