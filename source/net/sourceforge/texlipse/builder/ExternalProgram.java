/*
 * $Id$
 *
 * Copyright (c) 2004-2005 by the TeXlapse Team.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package net.sourceforge.texlipse.builder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Properties;

import net.sourceforge.texlipse.PathUtils;


/**
 * Helper methods to run an external program.
 * 
 * @author Kimmo Karlsson
 */
public class ExternalProgram {
    
    // the command to run
    private String[] command;

    // the directory to the command in
    private File dir;

    // the process that executes the command
    private Process process;

    // output messages to this console
    private String consoleOutput;
    
    /**
     * Creates a new command runner.
     */
    public ExternalProgram() {
        this.command = null;
        this.dir = null;
        this.process = null;
        this.consoleOutput = null;
    }

    /**
     * Resets the command runner.
     * 
     * @param command command to run
     * @param dir directory to run the command in
     */
    public void setup(String[] command, File dir, String console) {
        this.command = command;
        this.dir = dir;
        this.process = null;
        this.consoleOutput = console;
    }

    /**
     * Force termination of the running process.
     */
    public void stop() {
        if (process != null) {
            process.destroy();
            // can't null the process here, because run()-method of this class is still executing
            //process = null;
        }
    }
    
    /**
     * Reads the contents of a stream.
     * 
     * @param is the stream
     * @return the contents of the stream as a String
     */
    protected String readOutput(InputStream is) {
        
        StringWriter store = new StringWriter();
        try {
            
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;

            while ((line = br.readLine()) != null) {
                store.write(line + '\n');
                if (consoleOutput != null) {
                    BuilderRegistry.printToConsole(consoleOutput + "> " + line);
                }
            }
            
        } catch (IOException e) {
        }
        
        store.flush();
        return store.getBuffer().toString();
    }

    /**
     * Runs the external program as a process and waits 
     * for the process to finish execution.
     * 
     * @param queryMessage text which will trigger the query dialog
     * @return the text produced to standard output by the process
     * @throws Exception
     */
    public String run(String[] queryMessage) throws Exception {
        return run(true, queryMessage);
    }

    /**
     * Runs the external program as a process and waits 
     * for the process to finish execution.
     * 
     * @return the text produced to standard output by the process
     * @throws Exception
     */
    public String run() throws Exception {
        return run(true, null);
    }
    
    /**
     * Runs the external program as a process and waits 
     * for the process to finish execution.
     * 
     * @param wait if true, this method will block until
     *             the process has finished execution
     * @return the text produced to standard output by the process
     * @throws Exception
     */
    protected String run(boolean wait, String[] queryMessage) throws Exception {
        
        String output = null;
        
        if ((command != null) && (dir != null)) {
            
        	StringBuffer commandSB = new StringBuffer();
        	for (int i = 0; i < command.length; i++) {
        		commandSB.append(command[i]);
        		commandSB.append(" ");
        	}
        	
            BuilderRegistry.printToConsole("running: " + commandSB.toString());
            Runtime rt = Runtime.getRuntime();
            
            // Add builder program path to environmet variables.
            // This is needed at least on Mac OS X, where Eclipse overwrites
            // the "path" environment variable, and xelatex needs its directory in the path.
            Properties envProp = PathUtils.getEnv();
            int index = command[0].lastIndexOf(File.separatorChar);
            if (index > 0) {
	            String commandPath = command[0].substring(0, index);
	            String key = PathUtils.findPathKey(envProp);
	            envProp.setProperty(key, envProp.getProperty(key) + File.pathSeparatorChar + commandPath);
            }
            
            String[] env = PathUtils.getStrings(envProp);
            process = rt.exec(command, env, dir);
            
        } else {
            throw new IllegalStateException();
        }

        if (queryMessage != null) {
            OutputScanner put = null;
            
            put = new OutputScanner(process.getInputStream(), process.getOutputStream(),
                    queryMessage, consoleOutput);
            
            boolean success = put.scanOutput();
            if (success) {
                output = put.getText();
            }
            
        } else {
        
            output = readOutput(process.getInputStream());
        }
        
        if (output == null) {
            output = "";
            process.destroy();
        }
        
        if (wait) {
            int code = process.waitFor();
        }
        
        process = null;
        
        return output;
    }
}