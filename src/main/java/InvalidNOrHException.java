public class InvalidNOrHException extends Exception {
    public InvalidNOrHException(){
        super("Invalid values for N or H.\n- N must be an integer\n- H must be an integer, decimal, or a fraction\n- N and H must be positive");
    }
}
