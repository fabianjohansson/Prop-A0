import java.io.IOException;

/**
 *
 *
 *                                          TODO!
 *
 * ID Node behövs, lika som noun Node existerar i exempel.
 * Int Node behövs, lika som verb Node existerar i exempel.
 */
public class Parser implements IParser {

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
     *
     * la till if tok inte null eftersom ett blockstatement enligt grammatiken kan repeteras oändligt antal gånger
     * men äve noll gånger så är det teoretiskt möjligt att vi får en lexeme som är null.
     *
     * ändrade ifrån private inode s till private statementsnode s = null //inode är ett interface varför ska vi skapa det?
     * ädrade från private blockode bn till priavte blocknode bn = null
     *
     */
    private class BlockNode implements INode {

        private StatementsNode s = null;
        private BlockNode bN = null;

        public BlockNode(Tokenizer tok) {
            if (tok != null) {
                s = new StatementsNode(tok);
                if (tok.current().token() != Token.EOF) {
                    bN = new BlockNode(tok);
                }
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
     *
     * eftersom ett statement kan innehålla ett en assign och en statement, eller inget måste detta hanteras.
     * men eftesom det kommer från blockstatement bara om det inte är null är det redan hanterat.
     *
     *
     *
     */
    private class StatementsNode implements INode {

        private AssignmentNode aN = null;
        private StatementsNode sN = null;

        public StatementsNode(Tokenizer tok){

            aN = new AssignmentNode(tok);
            sN = new StatementsNode(tok);
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
     *
     * kan behövas en else sats för att gå vidare.
     *
     *
     *
     */
    private class AssignmentNode implements INode {

        private ExpressionNode eN = null;
        private IDNode iN = null;
        Lexeme lex = null;

        public AssignmentNode (Tokenizer tok){

            if (tok.current().token().equals('=')){
                lex = tok.current();
            }else if (tok.current().token().equals(';')){
                lex = tok.current();
            }
                //eN = new ExpressionNode(tok);
                //iN = new IDNode(tok);
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

    private class IDNode implements INode{


        @Override
        public Object evaluate(Object[] args) throws Exception {
            return null;
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {

        }
    }
}
