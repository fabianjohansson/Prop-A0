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
            ArrayList<Object> varList = new ArrayList<>();
            Object[] objects;
            objects = varList.toArray();
            String answer = "";
            if (s != null) { ;
                Object a = s.evaluate(objects);
                varList = (ArrayList)a;
                for (Object o : varList){
                    if( o instanceof EvaluatedStatement ){
                        EvaluatedStatement ev = (EvaluatedStatement)o;
                        answer = ev.getID() + " = " + ev.getValue();
                        answer += " = " + ev.getValue();
                    }
                }
            } else {

                answer = " \n";
            }return answer;
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
            ArrayList<Object> varList = new ArrayList<>(Arrays.asList(args));
            Object[] objects;
            objects = varList.toArray();
            if (lex != null) {
                aN.evaluate(objects);

                sN.evaluate(objects); // det andra statementet som ska byggas.
                return varList;
            } else {
                return varList;
            }
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
            ArrayList<Object> varList = new ArrayList<>(Arrays.asList(args));
            Object o = identifier.value();
            String s = (String)o;
            EvaluatedStatement es = new EvaluatedStatement(s);
            varList.add(es);
            Object[] objects;
            objects = varList.toArray();
            Object a = eN.evaluate(objects);
            varList = (ArrayList)a;

            return varList;
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

            String completeStatement = "";
            Object value, value2;
            double d1;
            value = tM.evaluate(null);
            completeStatement += value + "";
            if(addOrSub != null){
                String s1 = null;
                value2 = eN.evaluate(null);
                s1 = value2.toString();
                Character ch = s1.charAt(0);
                if(Character.isLetter(ch)){
                    completeStatement += addOrSub.value().toString() + " "+ s1;
                }else if(Character.isDigit(ch)){
                    d1 = Integer.parseInt(s1);
                    completeStatement += addOrSub.value().toString() + " "+s1;
                }
            }
            return completeStatement;
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
            return null;
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
            return null;
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