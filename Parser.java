import java.io.IOException;


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

    private class ResultNode {
        private String id;
        private double currentValue;

        public ResultNode(String id, double currentValue) {
            this.id = id;
            this.currentValue = currentValue;
        }

        public double getCurrentValue() {
            return currentValue;
        }

        public String getId() {
            return id;
        }
    }

    private class BlockNode implements INode {

        private Lexeme leftCurly, rightCurly;
        private StatementsNode statement;

        public BlockNode(Tokenizer tok) throws IOException, ParserException, TokenizerException {
            if (tok.current().token() == Token.LEFT_CURLY) {
                leftCurly = tok.current();
                tok.moveNext();
                statement = new StatementsNode(tok);
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

            if (statement != null) {
                ResultNode[] evalArray = new ResultNode[100];
                return statement.evaluate(evalArray);
            }
            return null;
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {

            builder.append("\t".repeat(tabs) + "BlockNode" + "\r\n");
            builder.append("\t".repeat(tabs) + leftCurly + "\r\n");
            tabs++;
            statement.buildString(builder, tabs);
            tabs--;
            builder.append("\t".repeat(tabs) + rightCurly + "\r\n");
        }
    }

    private class StatementsNode implements INode {

        private AssignmentNode assign = null;
        private StatementsNode statement = null;
        private Lexeme lex = null;


        public StatementsNode(Tokenizer tok) throws ParserException, IOException, TokenizerException {
            if (tok.current().token() == Token.IDENT) {
                lex = tok.current();
                assign = new AssignmentNode(tok);
                statement = new StatementsNode(tok);
            }
        }

        @Override
        public Object evaluate(Object[] args) throws Exception {

            StringBuilder sb = new StringBuilder();
            if (assign != null) {
                ResultNode currentResult = (ResultNode) assign.evaluate(args);
                sb.append(currentResult.getId() + " = " + currentResult.getCurrentValue() + "\n");
                System.out.println(sb);  //ta bort innan inlämmning

                for (int i = 0; i < args.length; i++) {
                    if (args[i] != null) {

                        ResultNode curRes = (ResultNode) args[i];

                    } else {
                        args[i] = currentResult;
                        break;
                    }
                }


                sb.append(statement.evaluate(args));
            }
            return sb;
        }


        @Override
        public void buildString(StringBuilder builder, int tabs) {

            builder.append("\t".repeat(tabs) + "StatementNode" + "\r\n");
            tabs++;
            if (lex != null) {
                assign.buildString(builder, tabs);
                statement.buildString(builder, tabs);
            }
        }
    }

    private class AssignmentNode implements INode {
        private ExpressionNode expression;
        private Lexeme identifier, assign, semiColon;

        public AssignmentNode(Tokenizer tok) throws ParserException, IOException, TokenizerException {
            if (tok.current().token() == Token.IDENT) {
                identifier = tok.current();
                tok.moveNext();
                if (tok.current().token() == Token.ASSIGN_OP) {
                    assign = tok.current();
                    tok.moveNext();
                    expression = new ExpressionNode(tok);
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

            return new ResultNode(identifier.value().toString(), Double.parseDouble(expression.evaluate(args).toString()));

        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {

            builder.append("\t".repeat(tabs) + "AssignmentNode" + "\r\n");
            tabs++;
            builder.append("\t".repeat(tabs) + identifier + "\r\n");
            builder.append("\t".repeat(tabs) + assign + "\r\n");
            expression.buildString(builder, tabs);
            builder.append("\t".repeat(tabs) + semiColon + "\r\n");
        }
    }

    private class ExpressionNode implements INode {
        private ExpressionNode expression = null;
        private TermNode term, prevOperator;
        private Lexeme addOrSub;

        public ExpressionNode(Tokenizer tok) throws ParserException, IOException, TokenizerException {

            if (tok.current().token() == Token.IDENT || tok.current().token() == Token.INT_LIT
                    || tok.current().token() == Token.LEFT_PAREN) {
                term = new TermNode(tok);
                if (tok.current().token() == Token.ADD_OP || tok.current().token() == Token.SUB_OP) {
                    addOrSub = tok.current();
                    tok.moveNext();
                    expression = new ExpressionNode(tok);
                }
            } else {
                throw new ParserException(PARSE_EXCEPTION_MESSAGE);
            }
        }

        @Override
        public Object evaluate(Object[] args) throws Exception {


            Double termNodeValue = Double.parseDouble(term.evaluate(args).toString());

            if (expression == null) {
                return termNodeValue;
            } else {
                Double exprNodeValue = Double.parseDouble(expression.evaluate(args).toString());

                if (addOrSub.token() == Token.ADD_OP) {
                    return termNodeValue + exprNodeValue;
                } else {
                    return termNodeValue - exprNodeValue;
                }
            }
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {

            builder.append("\t".repeat(tabs) + "ExpressionNode" + "\r\n");
            tabs++;

            term.buildString(builder, tabs);
            if (addOrSub != null) {
                builder.append("\t".repeat(tabs) + addOrSub + "\r\n");
                expression.buildString(builder, tabs);
            }
        }
    }

    private class TermNode implements INode {
        private Double prevDouble = 0.0;
        private FactorNode factor;
        private TermNode term = null;
        private Lexeme multOrDiv;

        public TermNode(Tokenizer tok) throws IOException, ParserException, TokenizerException {

            if (tok.current().token() == Token.IDENT || tok.current().token() == Token.INT_LIT
                    || tok.current().token() == Token.LEFT_PAREN) {
                factor = new FactorNode(tok);
                if (tok.current().token() == Token.MULT_OP || tok.current().token() == Token.DIV_OP) {
                    multOrDiv = tok.current();
                    tok.moveNext();
                    term = new TermNode(tok);
                }
            } else {
                throw new ParserException(PARSE_EXCEPTION_MESSAGE);
            }
        }

        @Override
        public Object evaluate(Object[] args) throws Exception {
            Double factorNodeValue = Double.parseDouble(factor.evaluate(args).toString());

            if (term == null) {
                return factorNodeValue;
            } else {

                if (multOrDiv.token() == Token.DIV_OP && prevDouble == 0.0) {
                    if (term.multOrDiv != null && term.multOrDiv.token() == Token.DIV_OP) {
                        term.setPrevDouble(factorNodeValue);
                        return term.evaluate(args);
                    } else {
                        factorNodeValue = Double.parseDouble(factor.evaluate(args).toString());
                    }
                } else if (term.multOrDiv != null && term.multOrDiv.token() == Token.DIV_OP) {
                    Double temp = prevDouble / factorNodeValue;
                    term.setPrevDouble(temp);
                    return term.evaluate(args);
                } else {
                    Double termValue = Double.parseDouble(term.evaluate(args).toString());
                    return factorNodeValue * termValue;
                }

                /*
                Double termValue;
                if (term.multOrDiv != null && term.multOrDiv.token() == Token.DIV_OP) {
                    Double secondTermFactor = Double.parseDouble(term.getFactor().evaluate(args).toString());
                    factorNodeValue = factorNodeValue / secondTermFactor;
                    termValue = Double.parseDouble(term.term.evaluate(args).toString());
                } else {
                    termValue = Double.parseDouble(term.evaluate(args).toString());
                }
                if (multOrDiv.token() == Token.MULT_OP) {
                    return factorNodeValue * termValue;
                } else {
                    return factorNodeValue / termValue;

            }

*/
            }return factorNodeValue;
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {

            builder.append("\t".repeat(tabs) + "TermNode" + "\r\n");
            tabs++;
            factor.buildString(builder, tabs);
            if (multOrDiv != null) {
                builder.append("\t".repeat(tabs) + multOrDiv + "\r\n");
                if (term != null) {
                    term.buildString(builder, tabs);
                }
            }
        }

        public FactorNode getFactor() {
            return factor;
        }

        public void setPrevDouble(double d) {
            prevDouble = d;
        }
    }

    private class FactorNode implements INode {
        private Lexeme firstLex, rightParen;
        private ExpressionNode expression = null;

        public FactorNode(Tokenizer tok) throws IOException, ParserException, TokenizerException {

            if (tok.current().token() == Token.IDENT || tok.current().token() == Token.INT_LIT
                    || tok.current().token() == Token.LEFT_PAREN) {
                firstLex = tok.current();
                if (tok.current().token() == Token.LEFT_PAREN) {
                    tok.moveNext();
                    expression = new ExpressionNode(tok);
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
            if (expression == null) {
                if (firstLex.token() == Token.INT_LIT) {
                    return firstLex.value();
                } else if (firstLex.token() == Token.IDENT) {
                    for (int i = 0; i < args.length; i++) {

                        ResultNode resultNode = (ResultNode) args[i];
                        if (resultNode.getId().equals(firstLex.value().toString())) {
                            return resultNode.getCurrentValue();
                        }
                    }
                }
            }
            return expression.evaluate(args);
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {

            builder.append("\t".repeat(tabs) + "FactorNode" + "\r\n");
            tabs++;
            builder.append("\t".repeat(tabs) + firstLex + "\r\n");
            if (firstLex.token() == Token.LEFT_PAREN) {
                expression.buildString(builder, tabs);
                builder.append("\t".repeat(tabs) + rightParen + "\r\n");
            }
        }
    }
}