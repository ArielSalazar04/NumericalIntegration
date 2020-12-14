public class InsufficientDataException extends Exception{
    public InsufficientDataException(){
        super("Please enter at least 3 data points.");
    }
}
