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
        private Lexeme lex;
        private StatementsNode s = null;
        StringBuilder outBuilder = new StringBuilder();
        private int level =0;

        public BlockNode(Tokenizer tok) throws IOException, TokenizerException {
            System.out.println("BlockNode ");
            if (tok.current().token() == Token.LEFT_CURLY) {
                System.out.println(tok.current());
               // lex = tok.current();
                //outBuilder.append(lex.toString());
             //   buildString(outBuilder,0);
                tok.moveNext();
                s = new StatementsNode(tok);
                System.out.println("are wer here?");
                System.out.println(tok.current());
                if (tok.current().token() == Token.RIGHT_CURLY) {
                    System.out.println(tok.current());
                    lex = tok.current();
                  //  buildString(outBuilder,0);
                    tok.moveNext();
                   // outBuilder.append(lex.toString());
                    if (tok.current().token() != Token.EOF) {
                        throw new TokenizerException(TOKENIZERMESSAGE);
                    }
                    System.out.println(tok.current());
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
            builder.append("BlockNode" + "\r\n");
            builder.append(Token.LEFT_CURLY + "\r\n");
            tabs++;
            s.buildString(builder, tabs);
           // builder.append("TEST TEST TEST TEST " + "\r\n" + lex);

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
        private Lexeme lex = null;

        public StatementsNode(Tokenizer tok) throws TokenizerException, IOException {
            System.out.println("StatementNode ");
            if (tok.current().token() == Token.IDENT) {
                lex = tok.current();
                aN = new AssignmentNode(tok);
                sN = new StatementsNode(tok);
            }
            System.out.println(tok.current());
        }

        @Override
        public Object evaluate(Object[] args) throws Exception {
            return null;
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {
            builder.append("StatementNode" + "\r\n");
            tabs++;
            if (lex != null) {
                aN.buildString(builder, tabs);
                sN.buildString(builder, tabs);
            }
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
        private Lexeme lex;

        public AssignmentNode(Tokenizer tok) throws TokenizerException, IOException {
            System.out.println("AssignmentNode ");
            if (tok.current().token() == Token.IDENT) {
                lex = tok.current();
                System.out.println(tok.current() + " ASSIGN");
                tok.moveNext();
                if (tok.current().token() == Token.ASSIGN_OP) {
                    System.out.println(tok.current() + " ASSIGN");
                    tok.moveNext();

                    eN = new ExpressionNode(tok);
                    if (tok.current().token() == Token.SEMICOLON) {
                        System.out.println(tok.current() + "end end end");
                        tok.moveNext();

                    } else {
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
            builder.append("AssignmentNode"+ "\r\n");
            tabs++;
            builder.append(lex + "\r\n");
            builder.append(Token.ASSIGN_OP + "\r\n");
            eN.buildString(builder, tabs);
            builder.append(Token.SEMICOLON + "\r\n");
        }
    }

    private class ExpressionNode implements INode {
        private ExpressionNode eN = null;
        private TermNode tM = null;
        private Lexeme lex;
        public ExpressionNode(Tokenizer tok) throws TokenizerException, IOException {

            if (tok.current().token() == Token.IDENT || tok.current().token() == Token.INT_LIT
                    || tok.current().token() == Token.LEFT_PAREN) {
                System.out.println(tok.current() + " EXPR inside if tok = term");
                tM = new TermNode(tok);
                System.out.println("error here");
                if (tok.current().token() == Token.ADD_OP || tok.current().token() == Token.SUB_OP) {
                    //terminal nodes
                    lex = tok.current();
                    System.out.println(tok.current() + " EXPR if sub/add");
                    tok.moveNext();
                    eN = new ExpressionNode(tok);
                    System.out.println("backinside expr");
                }
            }else {
                throw new TokenizerException(TOKENIZERMESSAGE);
            }

        }

        @Override
        public Object evaluate(Object[] args) throws Exception {
            return null;
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {
            builder.append("ExpressionNode" + "\r\n");
            tabs++;
            tM.buildString(builder, tabs);
            if ( lex != null) {
                builder.append(lex + "\r\n");
                eN.buildString(builder, tabs);
            }
        }
    }

    private class TermNode implements INode {

        private FactorNode fN = null;
        private TermNode tM = null;
        private Lexeme lex;
        public TermNode(Tokenizer tok) throws IOException, TokenizerException {

            if (tok.current().token() == Token.IDENT || tok.current().token() == Token.INT_LIT
                    || tok.current().token() == Token.LEFT_PAREN) {
                System.out.println(tok.current() + " Term inside if a term");
                fN = new FactorNode(tok);


                if (tok.current().token() == Token.MULT_OP || tok.current().token() == Token.DIV_OP) {
                    //terminal node
                    lex = tok.current();
                    System.out.println(tok.current() + " Term");
                    tok.moveNext();
                    tM = new TermNode(tok);
                }
                System.out.println("Closing if mul/div");
            }
            else {
                throw new TokenizerException(TOKENIZERMESSAGE);
            }
            System.out.println("closing term");
        }

        @Override
        public Object evaluate(Object[] args) throws Exception {
            return null;
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {
            builder.append("TermNode" + "\r\n");
            tabs++;
            fN.buildString(builder, tabs);
            builder.append(lex + "\r\n");
            if ( tM != null) {
                tM.buildString(builder, tabs);
            }
        }
    }

    private class FactorNode implements INode {
        private Lexeme lex;
        private ExpressionNode eN = null;
        public FactorNode(Tokenizer tok) throws IOException, TokenizerException {


            if (tok.current().token() == Token.IDENT || tok.current().token() == Token.INT_LIT
                    || tok.current().token() == Token.LEFT_PAREN) {
                System.out.println(tok.current() + " Factor inside if a Factor");
                lex = tok.current();
                //terminal node

                if (tok.current().token() == Token.LEFT_PAREN) {
                    //terminal node
                    System.out.println(tok.current());
                    tok.moveNext();
                    System.out.println(tok.current() + " inside left paren if");
                    eN = new ExpressionNode(tok);
                    System.out.println(tok.current() + " inside left paren if");

                    if (tok.current().token() == Token.RIGHT_PAREN) {
                        //terminal node
                        System.out.println(tok.current());
                    } else {
                        throw new TokenizerException(TOKENIZERMESSAGE);
                    }
                }
                tok.moveNext();
                System.out.println(tok.current() + " Factor end of factor");
            }else {
                throw new TokenizerException(TOKENIZERMESSAGE);
            }
        }

        @Override
        public Object evaluate(Object[] args) throws Exception {
            return null;
        }

        @Override
        public void buildString(StringBuilder builder, int tabs) {
            builder.append("FactorNode" + "\r\n");
            tabs++;
            builder.append(lex + "\r\n");
            if(lex.token() == Token.LEFT_PAREN){
                eN.buildString(builder, tabs);
                builder.append(Token.RIGHT_PAREN);
            }

        }
    }
}
