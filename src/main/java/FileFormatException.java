package main.java;

public class FileFormatException extends Exception{
    public FileFormatException(){
        super("Invalid formatting in file.\n- File must have a .txt or .csv file");
    }
}
