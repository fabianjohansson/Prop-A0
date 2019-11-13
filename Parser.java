import java.io.IOException;

/**
 * TODO!
 * <p>
 * ID Node behövs, lika som noun Node existerar i exempel.
 * Int Node behövs, lika som verb Node existerar i exempel.
 */
public class Parser implements IParser {

    private static final String TOKENIZERMESSAGE = "Invalid Symbol";
    private Tokenizer tokenizer = null;

    @Override
    public void open(String fileName) throws IOException, TokenizerException {

        tokenizer = new Tokenizer();
        tokenizer.open(fileName);
        tokenizer.moveNext();

    }

    @Override
    public INode parse() throws IOException, TokenizerException, ParserException {

        if (tokenizer == null) {
            throw new IOException("no file open! ");

        }
        return new BlockNode(tokenizer);

    }

    @Override
    public void close() throws IOException {

        if (tokenizer != null) {
            tokenizer.close();
        }
    }

    /**
     * la till if tok inte null eftersom ett blockstatement enligt grammatiken kan repeteras oändligt antal gånger
     * men äve noll gånger så är det teoretiskt möjligt att vi får en lexeme som är null.
     * <p>
     * ändrade ifrån private inode s till private statementsnode s = null //inode är ett interface varför ska vi skapa det?
     * ädrade från private blockode bn till priavte blocknode bn = null
     */
    private class BlockNode implements INode {

        private StatementsNode s = null;


        public BlockNode(Tokenizer tok) throws IOException, TokenizerException {
            System.out.println("BlockNode");
            if (tok.current().token() == Token.LEFT_CURLY) {
                System.out.println(tok.current());
                tok.moveNext();
                s = new StatementsNode(tok);

                if (tok.current().token() == Token.RIGHT_CURLY) {
                    tok.moveNext();
                    if(tok.current().token() != Token.EOF){
                        throw new TokenizerException(TOKENIZERMESSAGE);
                    }
                }else{
                    throw new TokenizerException(TOKENIZERMESSAGE);
                }
            }else{
                throw new TokenizerException(TOKENIZERMESSAGE);
            }
        }

        @Override
        public Object evaluate(Object[] args) throws Exception {
            return null;
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {

            //recursive genom alla
        }
    }

    /**
     * eftersom ett statement kan innehålla ett en assign och en statement, eller inget måste detta hanteras.
     * men eftesom det kommer från blockstatement bara om det inte är null är det redan hanterat.
     */
    private class StatementsNode implements INode {

        private AssignmentNode aN = null;
        private StatementsNode sN = null;

        public StatementsNode(Tokenizer tok) throws TokenizerException, IOException {
            System.out.println("StatementNode");
            if (tok.current().token() == Token.IDENT) {
                aN = new AssignmentNode(tok);
                sN = new StatementsNode(tok);
            }else{
                throw new TokenizerException(TOKENIZERMESSAGE);
            }
        }

        @Override
        public Object evaluate(Object[] args) throws Exception {
            return null;
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {

        }
    }

    /**
     * Eftersom ett assignment består av ett ID ett = ett EXPR och ett ;
     * eftersom  = och ; är slut så sparar vi dom i Lex om dom stämmer
     * sen går vi vidare
     * <p>
     * kan behövas en else sats för att gå vidare.
     */
    private class AssignmentNode implements INode {

        private ExpressionNode eN = null;


        public AssignmentNode(Tokenizer tok) throws TokenizerException, IOException {
            System.out.println("AssignmentNode");
            if (tok.current().token() == Token.IDENT) {
                System.out.println(tok.current());
                tok.moveNext();
                if (tok.current().token() == Token.ASSIGN_OP) {
                    System.out.println(tok.current());
                    tok.moveNext();

                    eN = new ExpressionNode(tok);
                    if (tok.current().token() == Token.SEMICOLON) {
                        System.out.println(tok.current());
                        tok.moveNext();

                    }else{
                        throw new TokenizerException(TOKENIZERMESSAGE);
                    }
                } else {
                    throw new TokenizerException(TOKENIZERMESSAGE);

                }
            } else {
                throw new TokenizerException(TOKENIZERMESSAGE);
            }

        }

        @Override
        public Object evaluate(Object[] args) throws Exception {
            return null;
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {

        }
    }

    private class ExpressionNode implements INode {

        TermNode tM = null;

        public ExpressionNode(Tokenizer tok) {

            if (tok.current().token() == Token.IDENT | tok.current().token() == Token.INT_LIT |
                    tok.current().token() == Token.LEFT_PAREN) {
                System.out.println(tok.current());
                tM = new TermNode();
            }


        }

        @Override
        public Object evaluate(Object[] args) throws Exception {
            return null;
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {

        }
    }

    private class TermNode implements INode {

        @Override
        public Object evaluate(Object[] args) throws Exception {
            return null;
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {

        }
    }

    private class FactorNode implements INode {

        @Override
        public Object evaluate(Object[] args) throws Exception {
            return null;
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {

        }
    }
}
