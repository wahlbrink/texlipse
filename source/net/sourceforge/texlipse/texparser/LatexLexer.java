/*
 * $Id$
 *
 * Copyright (c) 2004-2005 by the TeXlapse Team.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package net.sourceforge.texlipse.texparser;

import java.io.PushbackReader;

import net.sourceforge.texlipse.texparser.lexer.Lexer;
import net.sourceforge.texlipse.texparser.lexer.LexerException;
import net.sourceforge.texlipse.texparser.node.EOF;
import net.sourceforge.texlipse.texparser.node.TArgument;
import net.sourceforge.texlipse.texparser.node.TBverbatim;
import net.sourceforge.texlipse.texparser.node.TLBrace;
import net.sourceforge.texlipse.texparser.node.TOptargument;
import net.sourceforge.texlipse.texparser.node.TRBrace;
import net.sourceforge.texlipse.texparser.node.TRBracket;
import net.sourceforge.texlipse.texparser.node.TVtext;
import net.sourceforge.texlipse.texparser.node.TWhitespace;
import net.sourceforge.texlipse.texparser.node.Token;


/**
 * Lexer for LaTeX -files. Implements tokenizing curly brace-enclosed
 * areas and verb and verbatim environments.
 * 
 * @author Oskar Ojala
 */
public class LatexLexer extends Lexer {
    
    /**
     * Counter for braces
     */
    private int count;
    
    private Token argStart;
    private StringBuffer text;
    
    /**
     * Terminator char for \verb
     */
    private char startChar;
    
    private int vline, vpos;
    
    /**
     * Creates a new lexer.
     * 
     * @param in The reader to read the character stream from
     */
    public LatexLexer(PushbackReader in) {
        super(in);
    }
    
    /**
     * We define a filter that recognizes braced strings and verbatims
     */
    protected void filter() throws LexerException {
        
        // if we're to capture a brace-block
        if (state.equals(State.BLOCKCAPT)) {
            
            // if we are just entering this state
            if (argStart == null) {
                argStart = token;
                text = new StringBuffer("");
                count = 1;
                token = null; // continue to scan the input.
            } else {
                if (token instanceof TLBrace)
                    count++;
                else if (token instanceof TRBrace)
                    count--;
                else if (token instanceof EOF) {
                    throw new LexerException("[" + argStart.getLine() + 
                            "," + (argStart.getPos() - 1) + "] There's a } missing: unexpected end of file");
                }
                if (count != 0) {
                    // accumulate the string and continue to scan the input.
                    if (token instanceof TWhitespace)
                        text.append(" ");
                    else
                        text.append(token.getText());
                    token = null;
                } else {
                    TArgument targ = new TArgument(text.toString(),
                            argStart.getLine(),
                            argStart.getPos());
                    // emit the string
                    token = targ;
                    state = State.COMCAPT;
                    argStart = null;
                }
            }
            // Capture optional argument
        } else if (state.equals(State.OPTCAPT)) {
            if (argStart == null) {
                argStart = token;
                text = new StringBuffer("");
                count = 0;
                token = null; // continue to scan the input.
            } else {
                if (token instanceof TLBrace)
                    count++;
                else if (token instanceof TRBrace)
                    count--;
                else if (token instanceof EOF) {
                    throw new LexerException("[" + argStart.getLine() + 
                            "," + argStart.getPos() + "] There's a } or a ] missing: unexpected end of file");
                }
                
                if (count != 0 || !(token instanceof TRBracket)) {
                    // accumulate the string and continue to scan the input.
                    if (token instanceof TWhitespace)
                        text.append(" ");
                    else
                        text.append(token.getText());
                    token = null;
                } else {
                    TOptargument tsl = new TOptargument(text.toString(),
                            argStart.getLine(),
                            argStart.getPos());
                    // emit the string
                    token = tsl;
                    state = State.COMCAPT;
                    argStart = null;
                }
            }
        } else if (state.equals(State.VERBATIM)) {
            // we don't really need the contents, so we discard them here
            // for better efficiency
            if (token instanceof TBverbatim) {
                vline = token.getLine();
                vpos = token.getPos();
            } else if (token instanceof TVtext || token instanceof TWhitespace) {
                token = null;
            } else if (token instanceof EOF) {
                throw new LexerException("[" + vline + "," + vpos 
                        + "] The verbatim environment isn't closed: unexpected end of file");
            }
        } else if (state.equals(State.VERB)) {
            if (token instanceof TVtext) {
                if (argStart == null) {
                    argStart = token;
                    startChar = token.getText().charAt(0);
                } else {
                    if (startChar == token.getText().charAt(0)) {
                        state = State.NORMAL;
                        startChar = '\0';
                        argStart = null;
                    }
                }
                token = null;
            } else if (token instanceof TWhitespace) {
                if (argStart == null) {
                    throw new LexerException("[" + token.getLine() + 
                            "," + token.getPos() + "] Terminator for verb can't be whitespace");
                }
                token = null;
            } else if (token instanceof EOF) {
                throw new LexerException("[" + argStart.getLine() + 
                        "," + argStart.getPos() + "] The verb-command isn't closed: unexpected end of file");
            }
        }
    }
}