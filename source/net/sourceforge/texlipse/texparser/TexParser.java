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

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.ArrayList;

import net.sourceforge.texlipse.model.OutlineNode;
import net.sourceforge.texlipse.model.ParseErrorMessage;
import net.sourceforge.texlipse.model.ReferenceContainer;
import net.sourceforge.texlipse.texparser.lexer.LexerException;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.IDocument;



/**
 * Parser front-end for parsing LaTeX files and extracting some relevant
 * information from them. The front-end provides an upper-level interface
 * to the parser and does some pre-processing and error-handling.
 * 
 * @author Oskar Ojala
 */
public class TexParser {

    private IDocument inputDoc;
    private LatexParser lparser;
    
    private ArrayList errors;
    private boolean fatalErrors;
    
    private String preamble;
    

    /**
     * @param input The string representing the document to parse
     */
    public TexParser(IDocument input) {
    	this.inputDoc = input;
        this.lparser = new LatexParser();
        this.fatalErrors = false;
    }

    /**
     * Removes trailing whitespace from the document. This is needed since
     * the lexer and Eclipse have a different view of how the positions in
     * the trailing whitespace work out, so it's best to just remove it.
     * 
     * @param input The document to process
     * @return The document with trailing whitespace removed
     */
    private String rmTrailingWhitespace(String input) {
        int lastChar = input.length() - 1;
        while (lastChar >= 0 && Character.isWhitespace(input.charAt(lastChar)))
            lastChar--;
        lastChar++;
        if (lastChar < input.length())
            return input.substring(0, lastChar);
        return input;
    }
    
    /**
     * Extracts the preamble (if there is any) and stores a copy of it
     * in the field <code>preamble</code>. The preamble is assumed to
     * exist if the string contain the \documentclass command and it ends
     * where the document-environment begins or at the end of file.
     * 
     * The preamble stored will include the \beign{document} -command.
     * 
     * @param input The document
     */
    private void extractPreamble(String input) {
        if (input.indexOf("\\documentclass") == -1) {
            return;
        }
        // finds \begin {document} starting index
        int startDocIdx = input.indexOf("{document}");
        if (startDocIdx != -1) {
            int beginIdx = input.lastIndexOf("\\begin", startDocIdx);
            if (beginIdx != -1) {
                if (input.substring(beginIdx + 6, startDocIdx).matches("\\s*")) {
                    this.preamble = input.substring(0, startDocIdx + 10);
                    return;
                }
            }
        }
        this.preamble = input + "\\begin{document}";
    }

    /**
     * Parses the document
     * 
     * @throws IOException
     */
    public void parseDocument(ReferenceContainer labels, ReferenceContainer bibs) throws IOException {
        
        String input = inputDoc.get();
        
        // remove trailing ws (this is because a discrepancy in the lexer's 
        // and IDocument's line counting for trailing whitespace)
        input = this.rmTrailingWhitespace(input);
        
        this.extractPreamble(input);
        
        try {
            // start the parse
            LatexLexer lexer = new LatexLexer(new PushbackReader(new StringReader(input), 1024));
            if (this.preamble != null) {
                OutlineNode on = new OutlineNode("Preamble",
                        OutlineNode.TYPE_PREAMBLE,
                        1, null);
                lparser.parse(lexer, labels, bibs, on);
            } else {
                lparser.parse(lexer, labels, bibs);
            }
            this.errors = lparser.getErrors();
            this.fatalErrors = lparser.isFatalErrors();
        } catch (LexerException e) {
            // we must parse the lexer exception into a suitable format
            String msg = e.getMessage();
            int first = msg.indexOf('[');
            int last = msg.indexOf(']');
            String numseq = msg.substring(first + 1, last);
            String[] numbers = numseq.split(",");
            this.errors = new ArrayList(1);
            this.errors.add(new ParseErrorMessage(Integer.parseInt(numbers[0]),
                    Integer.parseInt(numbers[1]),
                    2,
                    msg.substring(last+2),
                    IMarker.SEVERITY_ERROR));
            this.fatalErrors = true;
        }
    }

    /**
     * @return The outline tree
     */
    public ArrayList getOutlineTree() {
    	return lparser.getOutlineTree();
    }

    /**
     * @return The labels
     */
    public ArrayList getLabels() {
        return lparser.getLabels();
    }

    /**
     * @return The cite-references
     */
    public ArrayList getCites() {
        return lparser.getCites();
    }
    
    /**
     * @return Returns the errors.
     */
    public ArrayList getErrors() {
        return errors;
    }
    
    /**
     * @return The bibliography files to include
     */
    public String[] getBibs() {
    	return lparser.getBibs();
    }
    
    /**
     * @return The style of the bibiliography entries
     */
    public String getBibstyle() {
    	return lparser.getBibstyle();
    }
    
    /**
     * @return Returns the preamble.
     */
    public String getPreamble() {
        return preamble;
    }
    
    /**
     * @return True if the document has an index, false otherwise
     */
    public boolean isIndex() {
        return lparser.isIndex();
    }
    
    /**
     * @return True if there were fatal errors due to which parsing couldn't be successfully completed
     */
    public boolean isFatalErrors() {
        return fatalErrors;
    }
    
    /**
     * @return Get the \ref -references
     */
    public ArrayList getRefs() {
        return lparser.getRefs();
    }
    
    /**
     * @return Get user-defined commands
     */
    public ArrayList getCommands() {
        return lparser.getCommands();
    }
}