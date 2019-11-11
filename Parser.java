import java.io.IOException;

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

        if (tokenizer == null){
            throw new IOException("no file open! ");

        }return new BlockNode(tokenizer);

    }

    @Override
    public void close() throws IOException {

        if (tokenizer != null){
            tokenizer.close();
        }
    }
    private class BlockNode implements INode{

        private INode s;
        private BlockNode bN;
        public BlockNode (Tokenizer tok){
            s = new StatementsNode(tok);
            if (tok.current().token() != Token.EOF) {
                bN = new BlockNode(tok);
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
    private class StatementsNode implements INode{

        @Override
        public Object evaluate(Object[] args) throws Exception {
            return null;
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {

        }
    }
    private class AssignmentNode implements INode{

        if
        @Override
        public Object evaluate(Object[] args) throws Exception {
            return null;
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {

        }
    }
    private class ExpressionNode implements INode{

        @Override
        public Object evaluate(Object[] args) throws Exception {
            return null;
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {

        }
    }
    private class TermNode implements INode{

        @Override
        public Object evaluate(Object[] args) throws Exception {
            return null;
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {

        }
    }
        private class FactorNode implements INode{

            @Override
            public Object evaluate(Object[] args) throws Exception {
                return null;
            }

            @Override
            public void buildString(StringBuilder builder, int tabs) {

            }
        }
}
