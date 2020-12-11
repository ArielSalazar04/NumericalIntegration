public class IllegalTableContentsException extends Exception {
    public IllegalTableContentsException(){
        super("Table contains non-parsable data.");
    }

}
