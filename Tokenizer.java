import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
//ID,int,term,expr,assign,
/*
* The tokenizer should call the Scanner to get a stream of characters, and from
that stream of characters create a stream of lexemes/tokens. You should
implement a Tokenizer class, which implements the interface ITokenizer
* */

public class Tokenizer implements ITokenizer {
    private Lexeme current = null;
    private Lexeme next = null;
    private Scanner scanner = null;

    private static final Map<Character, Token> VALID_SYMBOLS;


    static {
        VALID_SYMBOLS = new HashMap<>();


        VALID_SYMBOLS.put('{', Token.LEFT_CURLY);
        VALID_SYMBOLS.put('}', Token.RIGHT_CURLY);
        VALID_SYMBOLS.put('+', Token.ADD_OP);
        VALID_SYMBOLS.put('=', Token.ASSIGN_OP);
        VALID_SYMBOLS.put('/', Token.DIV_OP);
        VALID_SYMBOLS.put(Scanner.EOF, Token.EOF);
        VALID_SYMBOLS.put('*', Token.MULT_OP);
        VALID_SYMBOLS.put('-', Token.SUB_OP);
        VALID_SYMBOLS.put(';', Token.SEMICOLON);
        VALID_SYMBOLS.put(null, Token.NULL);
    }

    public Tokenizer() {

    }

    @Override
    public void open(String fileName) throws IOException, TokenizerException {
        scanner = new Scanner();
        scanner.open(fileName);
        scanner.moveNext();

        // next = extractLexeme();
    }


    @Override
    public Lexeme current() {
        return current;
    }

    @Override
    public void moveNext() throws IOException, TokenizerException {
        if (scanner == null) {
            throw new IOException("no open file. ");
        }
        current = next;
        if (next.token() != Token.EOF) {
            next = extractLexeme();
        }
    }

    @Override
    public void close() throws IOException {

        if (scanner != null) {
            scanner.close();
        }
    }

    private Lexeme extractLexeme() throws TokenizerException, IOException {

        char ch = scanner.current();
        if (VALID_SYMBOLS.containsKey(ch)) {
            return new Lexeme(ch, VALID_SYMBOLS.get(ch));
        } else if (Character.isLetter(ch)) {
            StringBuilder builder = new StringBuilder();
            while (Character.isLetter(scanner.current())) {
                builder.append(scanner.current());
                scanner.moveNext();
            }
            String value = builder.toString();
            return new Lexeme(value, Token.IDENT);
        } else if (Character.isDigit(ch)) {
            StringBuilder builder = new StringBuilder();
            while (Character.isDigit(scanner.current())) {
                builder.append(scanner.current());
                scanner.moveNext();
            }
            String value = builder.toString();
            return new Lexeme(value, Token.INT_LIT);
        } else {
            throw new TokenizerException("invalid character" + ch);
        }
    }
}

