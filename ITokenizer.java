import java.io.FileNotFoundException;
import java.io.IOException;

public interface ITokenizer {
	/**
	 * Opens a file for tokenizing.
	 */
	void open(String fileName) throws IOException, TokenizerException;
	
	/**
	 * Returns the current token in the stream.
	 */
	Lexeme current();

	/**
	 * Moves current to the next token in the stream.
	 */
	void moveNext() throws IOException, TokenizerException;

	/**
	 * Closes the file and releases any system resources associated with it.
	 */
	public void close() throws IOException ;
}