import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class Parser implements IParser {

    private static final String PARSE_EXCEPTION_MESSAGE = "Invalid Symbol";
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
    private class ResultNode{
        private String id;
        private double currentValue;

        public ResultNode(String id, double currentValue){
            this.id = id;
            this.currentValue = currentValue;
        }

        public double getCurrentValue() {
            return currentValue;
        }
        public String getId(){
            return id;
        }
    }

    private class BlockNode implements INode {

        private Lexeme leftCurly, rightCurly;
        private StatementsNode s;

        public BlockNode(Tokenizer tok) throws IOException, ParserException, TokenizerException {
            if (tok.current().token() == Token.LEFT_CURLY) {
                leftCurly = tok.current();
                tok.moveNext();
                s = new StatementsNode(tok);
                if (tok.current().token() == Token.RIGHT_CURLY) {
                    rightCurly = tok.current();
                    tok.moveNext();
                    if (tok.current().token() != Token.EOF) {
                        throw new ParserException(PARSE_EXCEPTION_MESSAGE);
                    }
                } else {
                    throw new ParserException(PARSE_EXCEPTION_MESSAGE);
                }
            } else {
                throw new ParserException(PARSE_EXCEPTION_MESSAGE);
            }
        }



        @Override
        public Object evaluate(Object[] args) throws Exception {

            ResultNode[] evalArray = new ResultNode[500];
            if(s != null) {
                return s.evaluate(evalArray);
            }
            return null;
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {

            builder.append("\t".repeat(tabs) + "BlockNode" + "\r\n");
            builder.append("\t".repeat(tabs) + leftCurly + "\r\n");
            tabs++;
            s.buildString(builder, tabs);
            tabs--;
            builder.append("\t".repeat(tabs) + rightCurly + "\r\n");
        }
    }

    private class StatementsNode implements INode {

        private AssignmentNode aN = null;
        private StatementsNode sN = null;
        private Lexeme lex = null;


        public StatementsNode(Tokenizer tok) throws ParserException, IOException, TokenizerException {
            if (tok.current().token() == Token.IDENT) {
                lex = tok.current();
                aN = new AssignmentNode(tok);
                sN = new StatementsNode(tok);
            }
        }

        @Override
        public Object evaluate(Object[] args) throws Exception {
            StringBuilder sb = new StringBuilder();

            if(aN != null){
                ResultNode currentResult = (ResultNode) aN.evaluate(args);
                sb.append(currentResult.getId() + "=" + currentResult.getCurrentValue() + "\n");
                System.out.println("sb " + sb);
                for(int i = 0; i < args.length; i++){
                    if(args[i] == null){
                        args[i] = currentResult;
                        //breaks because array initialized to 300 in blockNode evaluation
                        break;
                    }
                }
                sb.append(sN.evaluate(args));
            }
            return sb;
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {

            builder.append("\t".repeat(tabs) + "StatementNode" + "\r\n");
            tabs++;
            if (lex != null) {
                aN.buildString(builder, tabs);
                sN.buildString(builder, tabs);
            }
        }
    }

    private class AssignmentNode implements INode {
        private ExpressionNode eN;
        private Lexeme identifier, assign, semiColon;

        public AssignmentNode(Tokenizer tok) throws ParserException, IOException, TokenizerException {
            if (tok.current().token() == Token.IDENT) {
                identifier = tok.current();
                tok.moveNext();
                if (tok.current().token() == Token.ASSIGN_OP) {
                    assign = tok.current();
                    tok.moveNext();
                    eN = new ExpressionNode(tok);
                    if (tok.current().token() == Token.SEMICOLON) {
                        semiColon = tok.current();
                        tok.moveNext();
                    } else {
                        throw new ParserException(PARSE_EXCEPTION_MESSAGE);
                    }
                } else {
                    throw new ParserException(PARSE_EXCEPTION_MESSAGE);
                }
            } else {
                throw new ParserException(PARSE_EXCEPTION_MESSAGE);
            }
        }

        @Override
        public Object evaluate(Object[] args) throws Exception {
            /*
            * Behöver nog hantera semikolon här
            * */
                return new ResultNode(identifier.value().toString(), Double.parseDouble(eN.evaluate(args).toString()));

        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {

            builder.append("\t".repeat(tabs) + "AssignmentNode" + "\r\n");
            tabs++;
            builder.append("\t".repeat(tabs) + identifier + "\r\n");
            builder.append("\t".repeat(tabs) + assign + "\r\n");
            eN.buildString(builder, tabs);
            builder.append("\t".repeat(tabs) + semiColon + "\r\n");
        }
    }

    private class ExpressionNode implements INode {
        private ExpressionNode eN = null;
        private TermNode tM;
        private Lexeme addOrSub;

        public ExpressionNode(Tokenizer tok) throws ParserException, IOException, TokenizerException {

            if (tok.current().token() == Token.IDENT || tok.current().token() == Token.INT_LIT
                    || tok.current().token() == Token.LEFT_PAREN) {
                tM = new TermNode(tok);
                if (tok.current().token() == Token.ADD_OP || tok.current().token() == Token.SUB_OP) {
                    addOrSub = tok.current();
                    tok.moveNext();
                    eN = new ExpressionNode(tok);
                }
            } else {
                throw new ParserException(PARSE_EXCEPTION_MESSAGE);
            }
        }

        @Override
        public Object evaluate(Object[] args) throws Exception {
            double termNodeValue = Double.parseDouble(tM.evaluate(args).toString());

            if(eN == null) {
                return termNodeValue;
            }else {
                double exprNodeValue = Double.parseDouble(eN.evaluate(args).toString());
                if (addOrSub.token() == Token.ADD_OP){
                    return termNodeValue + exprNodeValue;
                }else{
                    return termNodeValue - exprNodeValue;
                }
            }
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {

            builder.append("\t".repeat(tabs) + "ExpressionNode" + "\r\n");
            tabs++;

            tM.buildString(builder, tabs);
            if (addOrSub != null) {
                builder.append("\t".repeat(tabs) + addOrSub + "\r\n");
                eN.buildString(builder, tabs);
            }
        }
    }

    private class TermNode implements INode {

        private FactorNode fN;
        private TermNode tM = null;
        private Lexeme multOrDiv;

        public TermNode(Tokenizer tok) throws IOException, ParserException, TokenizerException {

            if (tok.current().token() == Token.IDENT || tok.current().token() == Token.INT_LIT
                    || tok.current().token() == Token.LEFT_PAREN) {
                fN = new FactorNode(tok);
                if (tok.current().token() == Token.MULT_OP || tok.current().token() == Token.DIV_OP) {
                    multOrDiv = tok.current();
                    tok.moveNext();
                    tM = new TermNode(tok);
                }
            } else {
                throw new ParserException(PARSE_EXCEPTION_MESSAGE);
            }
        }

        @Override
        public Object evaluate(Object[] args) throws Exception {
            double factorNodeValue = Double.parseDouble(fN.evaluate(args).toString());

            if (tM == null) {
                return factorNodeValue;
            }else {
                double termNodeValue = Double.parseDouble(tM.evaluate(args).toString());
                if (multOrDiv.token() == Token.MULT_OP){
                    return factorNodeValue * termNodeValue;
                }else {
                    return factorNodeValue / termNodeValue;
                }
            }

        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {

            builder.append("\t".repeat(tabs) + "TermNode" + "\r\n");
            tabs++;
            fN.buildString(builder, tabs);
            if (multOrDiv != null) {
                builder.append("\t".repeat(tabs) + multOrDiv + "\r\n");
                if (tM != null) {
                    tM.buildString(builder, tabs);
                }
            }
        }
    }

    private class FactorNode implements INode {
        private Lexeme firstLex, rightParen;
        private ExpressionNode eN = null;

        public FactorNode(Tokenizer tok) throws IOException, ParserException, TokenizerException {


            if (tok.current().token() == Token.IDENT || tok.current().token() == Token.INT_LIT
                    || tok.current().token() == Token.LEFT_PAREN) {
                firstLex = tok.current();
                if (tok.current().token() == Token.LEFT_PAREN) {
                    tok.moveNext();
                    eN = new ExpressionNode(tok);
                    if (tok.current().token() == Token.RIGHT_PAREN) {
                        rightParen = tok.current();
                    } else {
                        throw new ParserException(PARSE_EXCEPTION_MESSAGE);
                    }
                }
                tok.moveNext();
            } else {
                throw new ParserException(PARSE_EXCEPTION_MESSAGE);
            }
        }

        @Override
        public Object evaluate(Object[] args) throws Exception {
            if (eN == null) {
                if (firstLex.token() == Token.INT_LIT) {
                    return firstLex.value();
                } else if (firstLex.token() == Token.IDENT) {
                    for (int i = 0; i < args.length; i++) {
                        ResultNode resultNode = (ResultNode) args[i];
                        if (resultNode.getId().equals(firstLex.value().toString())) {
                            return firstLex.value();
                        }
                    }
                }


            }
            return eN.evaluate(args);
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {

            builder.append("\t".repeat(tabs) + "FactorNode" + "\r\n");
            tabs++;
            builder.append("\t".repeat(tabs) + firstLex + "\r\n");
            if (firstLex.token() == Token.LEFT_PAREN) {
                eN.buildString(builder, tabs);
                builder.append("\t".repeat(tabs) + rightParen + "\r\n");
            }
        }
    }
}