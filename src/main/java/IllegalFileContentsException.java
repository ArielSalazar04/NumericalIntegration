public class IllegalFileContentsException extends Exception {
    public IllegalFileContentsException(){
        super("File must contain parsable, real numbers. \nEach row must have 2 numbers separated by 1 comma.");
    }
}
