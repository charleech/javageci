package javax0.geci.fluent.syntax;

import javax0.geci.api.GeciException;
import javax0.geci.fluent.FluentBuilder;

import java.util.ArrayList;
import java.util.List;

import static javax0.geci.fluent.syntax.Lexeme.Type.*;

public class Syntax {
    private final Lexer lexer;
    private FluentBuilder builder;

    public Syntax(Lexer lexer, FluentBuilder builder) {
        this.lexer = lexer;
        this.builder = builder;
    }
    /*
    expression ::= one ( SPACE one )* ;
    one ::= WORD |
            '(' one ( '|' one)* ')' |
            '(' one ( SPACE one)* ')' |
            one '*' |
            one '+' |
            one '?'
            ;
     */

    private FluentBuilder one() {
        var lexeme = lexer.peek();
        if (lexeme.type == WORD) {
            var method = lexer.get();
            var next = lexer.peek();
            if (next.type == SYMBOL) {
                switch (next.string) {
                    case "*":
                        lexer.get();
                        return builder = builder.zeroOrMore(method.string);
                    case "?":
                        lexer.get();
                        return builder = builder.optional(method.string);
                    case "+":
                        lexer.get();
                        return builder = builder.oneOrMore(method.string);
                    default:
                        return builder = builder.one(method.string);
                }
            } else {
                return builder = builder.one(method.string);
            }
        }
        if (lexeme.string.equals("#")) {
            lexer.get();
            var next = lexer.get();
            if (next.type != WORD) {
                throw new GeciException("After # you should define an interface name");
            }
            return builder = builder.name(next.string);
        }
        if (lexeme.string.equals("(")) {
            builder = group();
            if (lexer.peek().type == SYMBOL) {
                switch (lexer.peek().string) {
                    case "*":
                        lexer.get();
                        return builder = builder.zeroOrMore(builder);
                    case "?":
                        lexer.get();
                        return builder = builder.optional(builder);
                    case "+":
                        lexer.get();
                        return builder = builder.oneOrMore(builder);
                    default:
                        return builder = builder.one(builder);
                }
            }
        }
        return null;
    }

    private FluentBuilder group() {
        lexer.get(); // step over the '('
        builder = builder.one(one());
        var next = lexer.peek();
        if (next.string.equals(")")) {
            lexer.get();
            return builder;
        }
        if (next.type == SPACE) {
            while (next.type == SPACE) {
                lexer.get();
                builder = builder.one(one());
                next = lexer.peek();
            }
            if (next.string.equals(")")) {
                lexer.get();//step over the ')'
                return builder;
            }
            throw new GeciException("Fluent expression syntax error after ( .... ) missing closing parenthesis at '"
                + lexer.rest() + "'");
        }
        if (next.string.equals("|")) {
            List<FluentBuilder> arglist = new ArrayList<>();
            arglist.add(builder);
            while (next.string.equals("|")) {
                lexer.get();
                arglist.add(builder.one(one()));
                next = lexer.peek();
            }
            if (next.string.equals(")")) {
                lexer.get();//step over the ')'
                return builder = builder.oneOf(arglist.toArray(new FluentBuilder[arglist.size()]));
            }
            throw new GeciException("Fluent expression syntax error after ( .... ) missing closing parenthesis at '"
                + lexer.rest() + "'");
        }
        throw new GeciException("Fluent expression syntax error at '" + lexer.rest() + "'");
    }

    public FluentBuilder expression() {
        builder = builder.one(one());
        while (lexer.peek().type == SPACE) {
            lexer.get();
            builder = builder.one(one());
        }
        if (lexer.peek().type != EOF) {
            throw new GeciException("Extra characters at the end: '" + lexer.rest() + "'");
        }
        return builder;
    }

}
