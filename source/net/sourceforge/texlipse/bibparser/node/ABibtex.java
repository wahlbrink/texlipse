/* This file was generated by SableCC (http://www.sablecc.org/). */

package net.sourceforge.texlipse.bibparser.node;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import net.sourceforge.texlipse.bibparser.analysis.Analysis;


public final class ABibtex extends PBibtex
{
    private final LinkedList _stringEntry_ = new TypedLinkedList(new StringEntry_Cast());
    private final LinkedList _entry_ = new TypedLinkedList(new Entry_Cast());

    public ABibtex()
    {
    }

    public ABibtex(
        List _stringEntry_,
        List _entry_)
    {
        {
            this._stringEntry_.clear();
            this._stringEntry_.addAll(_stringEntry_);
        }

        {
            this._entry_.clear();
            this._entry_.addAll(_entry_);
        }

    }
    public Object clone()
    {
        return new ABibtex(
            cloneList(_stringEntry_),
            cloneList(_entry_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseABibtex(this);
    }

    public LinkedList getStringEntry()
    {
        return _stringEntry_;
    }

    public void setStringEntry(List list)
    {
        _stringEntry_.clear();
        _stringEntry_.addAll(list);
    }

    public LinkedList getEntry()
    {
        return _entry_;
    }

    public void setEntry(List list)
    {
        _entry_.clear();
        _entry_.addAll(list);
    }

    public String toString()
    {
        return ""
            + toString(_stringEntry_)
            + toString(_entry_);
    }

    void removeChild(Node child)
    {
        if(_stringEntry_.remove(child))
        {
            return;
        }

        if(_entry_.remove(child))
        {
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        for(ListIterator i = _stringEntry_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set(newChild);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

        for(ListIterator i = _entry_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set(newChild);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

    }

    private class StringEntry_Cast implements Cast
    {
        public Object cast(Object o)
        {
            PStringEntry node = (PStringEntry) o;

            if((node.parent() != null) &&
                (node.parent() != ABibtex.this))
            {
                node.parent().removeChild(node);
            }

            if((node.parent() == null) ||
                (node.parent() != ABibtex.this))
            {
                node.parent(ABibtex.this);
            }

            return node;
        }
    }

    private class Entry_Cast implements Cast
    {
        public Object cast(Object o)
        {
            PEntry node = (PEntry) o;

            if((node.parent() != null) &&
                (node.parent() != ABibtex.this))
            {
                node.parent().removeChild(node);
            }

            if((node.parent() == null) ||
                (node.parent() != ABibtex.this))
            {
                node.parent(ABibtex.this);
            }

            return node;
        }
    }
}