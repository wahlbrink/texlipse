/*
 * $Id$
 *
 * Copyright (c) 2004-2005 by the TeXlapse Team.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package net.sourceforge.texlipse.editor;

import java.util.ArrayList;

import net.sourceforge.texlipse.TexlipsePlugin;
import net.sourceforge.texlipse.model.TexDocumentModel;
import net.sourceforge.texlipse.outline.TexOutlinePage;
import net.sourceforge.texlipse.properties.TexlipseProperties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;


/**
 * The Latex editor.
 * 
 * @author Oskar Ojala
 * @author Taavi Hupponen
 */
public class TexEditor extends TextEditor {

    public final static String TEX_PARTITIONING= "__tex_partitioning";

    /** The editor's bracket matcher */
    private TexPairMatcher fBracketMatcher = new TexPairMatcher("()[]{}");
    
    private TexOutlinePage outlinePage;
    private TexDocumentModel documentModel;
    private TexCodeFolder folder;
    private ProjectionSupport fProjectionSupport;
    
    /**
     * Constructs a new editor.
     */
    public TexEditor() {
        super();
        //setRangeIndicator(new DefaultRangeIndicator());
        folder = new TexCodeFolder(this);
    }

    /** 
     * Create the part control.
     * 
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        // enable projection support (for code folder)
        ProjectionViewer projectionViewer = (ProjectionViewer) getSourceViewer();
        fProjectionSupport = new ProjectionSupport(projectionViewer,
                getAnnotationAccess(), getSharedColors());
        fProjectionSupport.addSummarizableAnnotationType("org.eclipse.ui.workbench.texteditor.error");
        fProjectionSupport.addSummarizableAnnotationType("org.eclipse.ui.workbench.texteditor.warning");
        fProjectionSupport.install();
    
        if (TexlipsePlugin.getDefault().getPreferenceStore().getBoolean(TexlipseProperties.CODE_FOLDING)) {
        	projectionViewer.doOperation(ProjectionViewer.TOGGLE);
        }
        
        // register documentModel as documentListener
        // in initializeEditor this would cause NPE
        this.getDocumentProvider().getDocument(getEditorInput()).addDocumentListener(this.documentModel);
        this.documentModel.initializeModel();
        this.documentModel.updateNow();
    }

    /** 
     * Initialize TexDocumentModel and enable latex support in projects
     * other than Latex Project.
     * 
     * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#initializeEditor()
     */
    protected void initializeEditor() {
        super.initializeEditor();
        this.documentModel = new TexDocumentModel(this);
        setSourceViewerConfiguration(new TexSourceViewerConfiguration(this));
        // register a document provider to get latex support even in non-latex projects
        if (getDocumentProvider() == null) {
            setDocumentProvider(new TexDocumentProvider());
        }
    }
    
    /** 
     * Create, configure and return the SourceViewer.
     * 
     * @see org.eclipse.ui.texteditor.AbstractTextEditor#createSourceViewer(org.eclipse.swt.widgets.Composite, org.eclipse.jface.text.source.IVerticalRuler, int)
     */
    protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
        ProjectionViewer viewer = new ProjectionViewer(parent, ruler, getOverviewRuler(), true, styles); 
        getSourceViewerDecorationSupport(viewer);
        return viewer;
    }

    /** 
     * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#configureSourceViewerDecorationSupport(org.eclipse.ui.texteditor.SourceViewerDecorationSupport)
     */
    protected void configureSourceViewerDecorationSupport(SourceViewerDecorationSupport support) {
        // copy the necessary values from plugin preferences instead of overwriting editor preferences
        getPreferenceStore().setValue(TexlipseProperties.MATCHING_BRACKETS, TexlipsePlugin.getPreference(TexlipseProperties.MATCHING_BRACKETS));
        getPreferenceStore().setValue(TexlipseProperties.MATCHING_BRACKETS_COLOR, TexlipsePlugin.getPreference(TexlipseProperties.MATCHING_BRACKETS_COLOR));
        
        support.setCharacterPairMatcher(fBracketMatcher);
        support.setMatchingCharacterPainterPreferenceKeys(TexlipseProperties.MATCHING_BRACKETS, TexlipseProperties.MATCHING_BRACKETS_COLOR);

        super.configureSourceViewerDecorationSupport(support);
    }
    
    /** 
     * @see org.eclipse.ui.texteditor.AbstractTextEditor#createActions()
     */
    protected void createActions() {
        super.createActions();
        
        IAction a = new TextOperationAction(TexlipsePlugin.getDefault().getResourceBundle(), "ContentAssistProposal.", this, ISourceViewer.CONTENTASSIST_PROPOSALS);
        a.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
        setAction("ContentAssistProposal", a);
    }
    
    /**
     * @return The source viewer of this editor
     */
    public ISourceViewer getViewer(){
    	return getSourceViewer();
    }
    
    /**
     * Used by platform to get the OutlinePage and ProjectionSupport 
     * adapter. 
     *  
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter(Class required) {
        if (IContentOutlinePage.class.equals(required)) {
            if (this.outlinePage == null) {
                this.outlinePage = new TexOutlinePage(this);
                this.documentModel.updateOutline();
            }
            return outlinePage;
        } else if (fProjectionSupport != null) {
            Object adapter = fProjectionSupport.getAdapter(getSourceViewer(), required);
            if (adapter != null)
                return adapter;
        }
        return super.getAdapter(required);
    }
    
    /**
     * @return The outline page associated with this editor
     */
    public TexOutlinePage getOutlinePage() {
        return this.outlinePage;
    }
    
    /**
     * @return Returns the documentModel.
     */
    public TexDocumentModel getDocumentModel() {
        return documentModel;
    }

    /**
     * @return the preference store of this editor
     */
    public IPreferenceStore getPreferences() {
        return getPreferenceStore();
    }

    /** 
     * Triggers parsing. If there is a way to determine whether the
     * platform is currently being shut down, triggering of parsing in 
     * such a case could be skipped.
     * 
     * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void doSave(IProgressMonitor monitor) {
        super.doSave(monitor);
        this.documentModel.updateNow();
    }
    
    /**
     * Updates the code folding of this editor.
     * 
     * @param rootNodes The document tree that correspond to folds
     * @param monitor A progress monitor for the job doing the update
     */
    public void updateCodeFolder(ArrayList rootNodes, IProgressMonitor monitor) {
        this.folder.update(rootNodes);        
    }

    /**
     * Triggers the model to be updated as soon as possible.
     * 
     * Used by drag'n'drop and copy paste actions of the outline.
     */
    public void updateModelNow() {
    	this.documentModel.updateNow();
    }
    
    /**
     * Used by outline to determine whether drag'n'drop operations
     * are permitted.
     * 
     * @return true if current model is dirty
     */
    public boolean isModelDirty() {
        return this.documentModel.isDirty();
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    public void dispose() {
        super.dispose();
    }
}
