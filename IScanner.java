
import java.io.FileNotFoundException;
import java.io.IOException;

public interface IScanner {
	/**
	 * Opens a file for scanning.
	 */
	void open(String fileName) throws FileNotFoundException;
	
	/**
	 * Returns the current character in the stream.
	 */
	char current();

	/**
	 * Moves current to the next character in the stream.
	 */
	void moveNext() throws IOException;

	/**
	 * Closes the file and releases any system resources associated with it.
	 */
	public void close() throws IOException ;
}