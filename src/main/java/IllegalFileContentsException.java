public class IllegalFileContentsException extends Exception {
    public IllegalFileContentsException(){
        super("File must contain parsable, real numbers");
    }
}
