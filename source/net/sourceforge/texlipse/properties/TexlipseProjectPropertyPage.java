/*
 * $Id$
 *
 * Copyright (c) 2004-2005 by the TeXlapse Team.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package net.sourceforge.texlipse.properties;

import net.sourceforge.texlipse.TexlipsePlugin;
import net.sourceforge.texlipse.builder.BuilderChooser;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;


/**
 * Project's properties page.
 * 
 * @author Kimmo Karlsson
 */
public class TexlipseProjectPropertyPage extends PropertyPage {

    // text field for project source file name
    private Text sourceFileField;
    
    // text field for output file name
    private Text outFileField;
    
    // text field for bib file
    private Text tempDirField;

    // builder choosing component
    private BuilderChooser builderChooser;
    
    /**
     * Constructor for the property page.
     */
    public TexlipseProjectPropertyPage() {
        super();
    }

    /**
     * Creates the layout of property page.
     * 
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    protected Control createContents(Composite parent) {

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

        TexlipsePreferencePage.addSeparator(1, composite);
        addMainSection(composite);
        TexlipsePreferencePage.addSeparator(1, composite);
        addOutSection(composite);
        TexlipsePreferencePage.addSeparator(1, composite);
        addTempDirSection(composite);
        TexlipsePreferencePage.addSeparator(1, composite);
        addFormatSection(composite);

        performDefaults();

        return composite;
    }

    /**
     * Create project main file section of the page.
     * @param parent parent component
     */
    private void addMainSection(Composite parent) {
        Composite composite = createDefaultComposite(parent, 2);

        //Label for path field
        Label pathLabel = new Label(composite, SWT.NONE);
        pathLabel.setText(TexlipsePlugin.getResourceString("propertiesMainFileLabel"));
        pathLabel.setLayoutData(new GridData());
        pathLabel.setToolTipText(TexlipsePlugin.getResourceString("propertiesMainFileTooltip"));
        
        // Path text field
        sourceFileField = new Text(composite, SWT.SINGLE | SWT.WRAP | SWT.BORDER);
        sourceFileField.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        sourceFileField.setToolTipText(TexlipsePlugin.getResourceString("propertiesMainFileTooltip"));
        sourceFileField.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                validateSourceFileField();
            }});
    }

    /**
     * Check that:
     * - subdir exists
     * - subdir is direct subdir of project dir
     * - file exists
     * - file extension is tex or ltx
     */
    private void validateSourceFileField() {
        
        String text = sourceFileField.getText();
        if (text == null) {
            setValid(true);
            return;
        }
        
        text = text.trim();
        if (text.length() == 0) {
            setValid(true);
            return;
        }
        
        // if there is invalid characters
        if (text.indexOf(':') >= 0 || text.indexOf(';') >= 0) {
            setValid(false);
            return;
        }
        
        String dir = null;
        String file = "";
        
        int index = text.lastIndexOf('/');
        if (index < 0) {
            file = text;
        } else {
            dir = text.substring(0, index);
            file = text.substring(index+1);
        }
        
        if (dir != null && !projectFileExists(dir)) {
            setValid(false);
            return;
        }

        setValid(projectFileExists(text) && (file.endsWith(".tex") || file.endsWith(".ltx")));
    }

    /**
     * Create project main file section of the page.
     * @param parent parent component
     */
    private void addOutSection(Composite parent) {
        Composite composite = createDefaultComposite(parent, 2);

        //Label for path field
        Label pathLabel = new Label(composite, SWT.NONE);
        pathLabel.setText(TexlipsePlugin.getResourceString("propertiesOutFileLabel"));
        pathLabel.setLayoutData(new GridData());
        pathLabel.setToolTipText(TexlipsePlugin.getResourceString("propertiesOutFileTooltip"));
        
        // Path text field
        outFileField = new Text(composite, SWT.SINGLE | SWT.WRAP | SWT.BORDER);
        outFileField.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        outFileField.setToolTipText(TexlipsePlugin.getResourceString("propertiesOutFileTooltip"));
        outFileField.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                validateOutputFileField();
            }});
    }

    /**
     * Check that:
     * - subdir exists
     * - subdir is direct subdir of project dir
     * - file has the extension of the output format
     */
    private void validateOutputFileField() {
        
        String text = outFileField.getText();
        if (text == null) {
            setValid(true);
            return;
        }
        
        text = text.trim();
        if (text.length() == 0) {
            setValid(true);
            return;
        }
        
        // if there is invalid characters
        if (text.indexOf(':') >= 0 || text.indexOf(';') >= 0) {
            setValid(false);
            return;
        }
        
        String dir = null;
        String file = "";
        
        int index = text.lastIndexOf('/');
        if (index < 0) {
            file = text;
        } else {
            dir = text.substring(0, index);
            file = text.substring(index+1);
        }
        
        if (dir != null && !projectFileExists(dir)) {
            setValid(false);
            return;
        }
        
        String format = builderChooser.getSelectedFormat();
        if (format != null) {
            setValid(file.endsWith(format));
        }
    }

    /**
     * Create the bib dir section of the page.
     * @param parent parent component
     */
    private void addTempDirSection(Composite parent) {
        Composite composite = createDefaultComposite(parent, 3);

        //Label for path field
        Label label = new Label(composite, SWT.NONE);
        label.setText(TexlipsePlugin.getResourceString("propertiesTempDirLabel"));
        label.setLayoutData(new GridData());
        label.setToolTipText(TexlipsePlugin.getResourceString("propertiesTempDirTooltip"));

        // Path text field
        tempDirField = new Text(composite, SWT.SINGLE | SWT.WRAP | SWT.BORDER);
        tempDirField.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        tempDirField.setToolTipText(TexlipsePlugin.getResourceString("propertiesTempDirTooltip"));
        tempDirField.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                validateTempFileField();}});
    }

    /**
     * 
     */
    private void validateTempFileField() {
        String text = tempDirField.getText();
        if (text != null && text.length() > 0) {
            if (text.indexOf(':') >= 0 || text.indexOf(';') >= 0) {
                setValid(false);
                //setMessage(TexlipsePlugin.getResourceString("propertiesInvalidDirChars"));
            } else {
                boolean exists = projectFileExists(text);
                setValid(exists);
                if (!exists) {
                    //setMessage(TexlipsePlugin.getResourceString("propertiesInvalidDirChars"));
                }
            }
        } else {
            setValid(true);
        }
    }
    
    /**
     * Create output file format section of the page.
     * @param parent parent component
     */
    private void addFormatSection(Composite parent) {

        builderChooser = new BuilderChooser(parent);
        builderChooser.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                String fmt = builderChooser.getSelectedFormat();
                if (fmt != null) {
                    setOutputExtension(fmt);
                }
            }});
    }

    /**
     * Change the output file's extension according to the parameter.
     * This method only changes the value in the textfield, 
     * not in the properties.
     * 
     * @param ext new file extension
     */
    private void setOutputExtension(String ext) {
        
        String text = outFileField.getText();
        if (text == null) {
            return;
        }
        
        text = text.trim();
        if (text.length() == 0) {
            text = sourceFileField.getText();
            if (text == null) {
                return;
            }
            text = text.trim();
            if (text.length() == 0) {
                return;
            }
        }

        int index = text.lastIndexOf('.');
        if (index < 0) {
            outFileField.setText(text + "." + ext);
        } else {
            String base = text.substring(0, index+1);
            outFileField.setText(base + ext);
        }
    }
    
    /**
     * Create a standard container for the text field sections of the page.
     * @param parent parent component
     * @param columns number of columns in the grid layout
     * @return the container
     */
    private Composite createDefaultComposite(Composite parent, int columns) {
        
        Composite composite = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = columns;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL));

        return composite;
    }

    /**
     * Called when the defaults-button is pressed.
     */
    protected void performDefaults() {
        super.performDefaults();

        IResource project = (IResource) getElement();

        // read source file name
        String srcDir = TexlipseProperties.getProjectProperty(project,
                TexlipseProperties.SOURCE_DIR_PROPERTY);
        if (srcDir == null) {
            srcDir = "";
        } else if (srcDir.length() > 0 && !srcDir.endsWith("/")) {
            srcDir += '/';
        }
        
        String srcFile = TexlipseProperties.getProjectProperty(project,
                TexlipseProperties.MAINFILE_PROPERTY);
        sourceFileField.setText((srcFile != null) ? (srcDir+srcFile) : "");
        
        // read temp dir
        String temp = TexlipseProperties.getProjectProperty(project,
                TexlipseProperties.TEMP_DIR_PROPERTY);
        tempDirField.setText((temp != null) ? temp : "");

        // find out the default builder
        String str = TexlipseProperties.getProjectProperty(project,
                TexlipseProperties.BUILDER_NUMBER);
        int num = 0;
        if (str == null) {
            str = TexlipsePlugin.getPreference(TexlipseProperties.BUILDER_NUMBER);
        }
        try {
            num = Integer.parseInt(str);
        } catch (NumberFormatException e) {
        }
        builderChooser.setSelectedBuilder(num);

        // read output file name
        String outDir = TexlipseProperties.getProjectProperty(project,
                TexlipseProperties.OUTPUT_DIR_PROPERTY);
        if (outDir == null) {
            outDir = "";
        } else if (outDir.length() > 0 && !outDir.endsWith("/")) {
            outDir += '/';
        }
        
        String outFile = TexlipseProperties.getProjectProperty(project,
                TexlipseProperties.OUTPUTFILE_PROPERTY);
        outFileField.setText((outFile != null) ? (outDir+outFile) : "");
        
        // set status of the page
        validateSourceFileField();
        validateOutputFileField();
        validateTempFileField();
    }
    
    /**
     * Called when the ok-button is pressed.
     * Stores the values in the text fields to persistent properties.
     * 
     * @return false, if properties dialog should NOT be closed
     */
    public boolean performOk() {

        IResource project = (IResource) getElement();
        String srcDir = null;
        String outDir = null;
        
        // check source file & source dir
        String srcFile = sourceFileField.getText();
        if (srcFile != null) {
            srcFile = srcFile.trim();
            int index = srcFile.lastIndexOf('/');
            if (index > 0) {
                srcDir = srcFile.substring(0, index+1);
                srcFile = srcFile.substring(index+1);
            }
        }
        
        // check temp dir
        String tmpDir = tempDirField.getText();
        if (tmpDir != null) {
            tmpDir = tmpDir.trim();
        }
        
        // check the preferred output format for this project
        String format = builderChooser.getSelectedFormat();
        if (format == null) {
            format = TexlipseProperties.OUTPUT_FORMAT_DVI;
        }

        // check output file & output dir
        String outFile = outFileField.getText();
        if (outFile != null) {
            outFile = outFile.trim();
            int index = outFile.lastIndexOf('/');
            if (index > 0) {
                outDir = outFile.substring(0, index+1);
                outFile = outFile.substring(index+1);
            }
        }
        
        if ((outFile == null || outFile.length() == 0)
                && (srcFile != null && srcFile.length() > 0)) {
            // if no output given, make one up
            outFile = srcFile.substring(0, srcFile.lastIndexOf('.')+1) + format;
            outDir = srcDir;
        }

        int num = builderChooser.getSelectedBuilder();
        if (num == -1) {
            num = 0;
        }
        TexlipseProperties.setProjectProperty(project,
                TexlipseProperties.BUILDER_NUMBER, num+"");
        
        // save values
        TexlipseProperties.setProjectProperty(project,
                TexlipseProperties.MAINFILE_PROPERTY, srcFile);

        TexlipseProperties.setProjectProperty(project,
                TexlipseProperties.SOURCE_DIR_PROPERTY, srcDir);
                
        TexlipseProperties.setProjectProperty(project,
                TexlipseProperties.OUTPUTFILE_PROPERTY, outFile);
                
        TexlipseProperties.setProjectProperty(project,
                TexlipseProperties.OUTPUT_DIR_PROPERTY, outDir);
                
        TexlipseProperties.setProjectProperty(project,
                TexlipseProperties.TEMP_DIR_PROPERTY, tmpDir);
                
        TexlipseProperties.setProjectProperty(project,
                TexlipseProperties.OUTPUT_FORMAT, format);

        return true;
    }
    
    /**
     * Find out if the given file exists in the project.
     * 
     * @param filename the file name assumed to be in the project's main directory
     * @return true, if the given file exists
     */
    private boolean projectFileExists(String filename) {

        IResource res = (IResource) getElement();
        IProject project = res.getProject();
        
        IResource file = project.findMember(filename);
        return file != null && file.exists();
    }
}