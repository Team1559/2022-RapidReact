package frc.robot.components;

import java.io.*;

public class FileLogging {
    private String filename, out, dir;
    private final String DEFAULT_PATH = "/1559data/";

    /**
     * A file mannagment class for creating and reading files
     */
    public FileLogging() {
        out = "";
        filename = "";
        dir = "";
    }

    /**
     * Adds data to be written
     * 
     * @param data New data to be written
     */
    public void addData(String data) {
        out += data;
    }

    /**
     * Sets the directory that the file will be written in and creates the direcory
     * if it doesn't exist (Doesn't create the directory on roborios)
     * 
     * @param dir The directory to be set
     */
    public void setDirectory(String dir) {
        if (new File(dir).mkdir()) {
            System.out.println("Created directory");
        }
        this.dir = DEFAULT_PATH + dir;
    }

    /**
     * Creates a file with the given name
     * 
     * @param name Name of the file to be written
     */
    public void createfile(String name) {
        if (name.equals("")) {
            name = dir + "Default_file.txt";
        }

        else {
            name += ".txt";
        }

        filename += name;

        try {
            File myObj = new File(dir + name);

            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            }

            else {
                System.out.println("File already exists.");
            }
        }

        catch (IOException e) {
            System.out.println("An error occurred.");
            // // e.printStackTrace();
        }
    }

    /**
     * Writes the data stored using the addData() method
     */
    public void write() {
        write(out);
        out = "";
    }

    /**
     * Writes data to the last file created
     * 
     * @param data Data to be written
     */
    public void write(String data) {
        try {
            FileWriter myWriter = new FileWriter(dir + filename);

            myWriter.write(data);
            myWriter.close();
            // System.out.println("Successfully wrote to the file.");
        }

        catch (IOException e) {
            System.out.println("An error occurred.");
            // e.printStackTrace();
        }
    }

    /**
     * Writes data to the designated file
     * 
     * @param filename Name of the file to be written
     * @param data     Data to be written
     */
    public void write(String filename, String data) {
        try {
            FileWriter myWriter = new FileWriter(dir + filename);

            myWriter.write(data);
            myWriter.close();
            // System.out.println("Successfully wrote to the file.");
        }

        catch (IOException e) {
            System.out.println("An error occurred.");
            // e.printStackTrace();
        }
    }

    /**
     * Reads the last file created
     * 
     * @return The contents os the file
     */
    public String readFile() {
        return readFile(filename);
    }

    /**
     * Reads the file with the given name
     * 
     * @param fileName File to be read
     * @return The contents of the file
     */
    public String readFile(String fileName) {
        File file = new File(dir + fileName);
        String fileContent = "";

        try (FileReader fr = new FileReader(file)) {
            char[] chars = new char[(int) file.length()];
            fr.read(chars);

            fileContent = new String(chars);
        }

        catch (IOException e) {
            // e.printStackTrace();
        }

        return fileContent;

    }
}
