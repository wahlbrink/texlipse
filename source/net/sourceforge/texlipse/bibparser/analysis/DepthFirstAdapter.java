/* This file was generated by SableCC (http://www.sablecc.org/). */

package net.sourceforge.texlipse.bibparser.analysis;

import net.sourceforge.texlipse.bibparser.node.ABibtex;
import net.sourceforge.texlipse.bibparser.node.AConcat;
import net.sourceforge.texlipse.bibparser.node.AEntryDef;
import net.sourceforge.texlipse.bibparser.node.AEntrybraceEntry;
import net.sourceforge.texlipse.bibparser.node.AEntryparenEntry;
import net.sourceforge.texlipse.bibparser.node.AIdValOrSid;
import net.sourceforge.texlipse.bibparser.node.AKeyvalDecl;
import net.sourceforge.texlipse.bibparser.node.ANumValOrSid;
import net.sourceforge.texlipse.bibparser.node.AStrbraceStringEntry;
import net.sourceforge.texlipse.bibparser.node.AStrparenStringEntry;
import net.sourceforge.texlipse.bibparser.node.AValueValOrSid;
import net.sourceforge.texlipse.bibparser.node.Node;
import net.sourceforge.texlipse.bibparser.node.PConcat;
import net.sourceforge.texlipse.bibparser.node.PEntry;
import net.sourceforge.texlipse.bibparser.node.PKeyvalDecl;
import net.sourceforge.texlipse.bibparser.node.PStringEntry;
import net.sourceforge.texlipse.bibparser.node.Start;

public class DepthFirstAdapter extends AnalysisAdapter
{
    public void inStart(Start node)
    {
        defaultIn(node);
    }

    public void outStart(Start node)
    {
        defaultOut(node);
    }

    public void defaultIn(Node node)
    {
    }

    public void defaultOut(Node node)
    {
    }

    public void caseStart(Start node)
    {
        inStart(node);
        node.getPBibtex().apply(this);
        node.getEOF().apply(this);
        outStart(node);
    }

    public void inABibtex(ABibtex node)
    {
        defaultIn(node);
    }

    public void outABibtex(ABibtex node)
    {
        defaultOut(node);
    }

    public void caseABibtex(ABibtex node)
    {
        inABibtex(node);
        {
            Object temp[] = node.getStringEntry().toArray();
            for(int i = 0; i < temp.length; i++)
            {
                ((PStringEntry) temp[i]).apply(this);
            }
        }
        {
            Object temp[] = node.getEntry().toArray();
            for(int i = 0; i < temp.length; i++)
            {
                ((PEntry) temp[i]).apply(this);
            }
        }
        outABibtex(node);
    }

    public void inAStrbraceStringEntry(AStrbraceStringEntry node)
    {
        defaultIn(node);
    }

    public void outAStrbraceStringEntry(AStrbraceStringEntry node)
    {
        defaultOut(node);
    }

    public void caseAStrbraceStringEntry(AStrbraceStringEntry node)
    {
        inAStrbraceStringEntry(node);
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        if(node.getStringLiteral() != null)
        {
            node.getStringLiteral().apply(this);
        }
        outAStrbraceStringEntry(node);
    }

    public void inAStrparenStringEntry(AStrparenStringEntry node)
    {
        defaultIn(node);
    }

    public void outAStrparenStringEntry(AStrparenStringEntry node)
    {
        defaultOut(node);
    }

    public void caseAStrparenStringEntry(AStrparenStringEntry node)
    {
        inAStrparenStringEntry(node);
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        if(node.getStringLiteral() != null)
        {
            node.getStringLiteral().apply(this);
        }
        outAStrparenStringEntry(node);
    }

    public void inAEntrybraceEntry(AEntrybraceEntry node)
    {
        defaultIn(node);
    }

    public void outAEntrybraceEntry(AEntrybraceEntry node)
    {
        defaultOut(node);
    }

    public void caseAEntrybraceEntry(AEntrybraceEntry node)
    {
        inAEntrybraceEntry(node);
        if(node.getEntryDef() != null)
        {
            node.getEntryDef().apply(this);
        }
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        {
            Object temp[] = node.getKeyvalDecl().toArray();
            for(int i = 0; i < temp.length; i++)
            {
                ((PKeyvalDecl) temp[i]).apply(this);
            }
        }
        outAEntrybraceEntry(node);
    }

    public void inAEntryparenEntry(AEntryparenEntry node)
    {
        defaultIn(node);
    }

    public void outAEntryparenEntry(AEntryparenEntry node)
    {
        defaultOut(node);
    }

    public void caseAEntryparenEntry(AEntryparenEntry node)
    {
        inAEntryparenEntry(node);
        if(node.getEntryDef() != null)
        {
            node.getEntryDef().apply(this);
        }
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        {
            Object temp[] = node.getKeyvalDecl().toArray();
            for(int i = 0; i < temp.length; i++)
            {
                ((PKeyvalDecl) temp[i]).apply(this);
            }
        }
        outAEntryparenEntry(node);
    }

    public void inAEntryDef(AEntryDef node)
    {
        defaultIn(node);
    }

    public void outAEntryDef(AEntryDef node)
    {
        defaultOut(node);
    }

    public void caseAEntryDef(AEntryDef node)
    {
        inAEntryDef(node);
        if(node.getEntryName() != null)
        {
            node.getEntryName().apply(this);
        }
        outAEntryDef(node);
    }

    public void inAKeyvalDecl(AKeyvalDecl node)
    {
        defaultIn(node);
    }

    public void outAKeyvalDecl(AKeyvalDecl node)
    {
        defaultOut(node);
    }

    public void caseAKeyvalDecl(AKeyvalDecl node)
    {
        inAKeyvalDecl(node);
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        if(node.getValOrSid() != null)
        {
            node.getValOrSid().apply(this);
        }
        {
            Object temp[] = node.getConcat().toArray();
            for(int i = 0; i < temp.length; i++)
            {
                ((PConcat) temp[i]).apply(this);
            }
        }
        outAKeyvalDecl(node);
    }

    public void inAConcat(AConcat node)
    {
        defaultIn(node);
    }

    public void outAConcat(AConcat node)
    {
        defaultOut(node);
    }

    public void caseAConcat(AConcat node)
    {
        inAConcat(node);
        if(node.getValOrSid() != null)
        {
            node.getValOrSid().apply(this);
        }
        outAConcat(node);
    }

    public void inAValueValOrSid(AValueValOrSid node)
    {
        defaultIn(node);
    }

    public void outAValueValOrSid(AValueValOrSid node)
    {
        defaultOut(node);
    }

    public void caseAValueValOrSid(AValueValOrSid node)
    {
        inAValueValOrSid(node);
        if(node.getStringLiteral() != null)
        {
            node.getStringLiteral().apply(this);
        }
        outAValueValOrSid(node);
    }

    public void inANumValOrSid(ANumValOrSid node)
    {
        defaultIn(node);
    }

    public void outANumValOrSid(ANumValOrSid node)
    {
        defaultOut(node);
    }

    public void caseANumValOrSid(ANumValOrSid node)
    {
        inANumValOrSid(node);
        if(node.getNumber() != null)
        {
            node.getNumber().apply(this);
        }
        outANumValOrSid(node);
    }

    public void inAIdValOrSid(AIdValOrSid node)
    {
        defaultIn(node);
    }

    public void outAIdValOrSid(AIdValOrSid node)
    {
        defaultOut(node);
    }

    public void caseAIdValOrSid(AIdValOrSid node)
    {
        inAIdValOrSid(node);
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        outAIdValOrSid(node);
    }
}