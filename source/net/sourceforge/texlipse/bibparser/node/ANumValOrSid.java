/* This file was generated by SableCC (http://www.sablecc.org/). */

package net.sourceforge.texlipse.bibparser.node;

import net.sourceforge.texlipse.bibparser.analysis.Analysis;

public final class ANumValOrSid extends PValOrSid
{
    private TNumber _number_;

    public ANumValOrSid()
    {
    }

    public ANumValOrSid(
        TNumber _number_)
    {
        setNumber(_number_);

    }
    public Object clone()
    {
        return new ANumValOrSid(
            (TNumber) cloneNode(_number_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseANumValOrSid(this);
    }

    public TNumber getNumber()
    {
        return _number_;
    }

    public void setNumber(TNumber node)
    {
        if(_number_ != null)
        {
            _number_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _number_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_number_);
    }

    void removeChild(Node child)
    {
        if(_number_ == child)
        {
            _number_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_number_ == oldChild)
        {
            setNumber((TNumber) newChild);
            return;
        }

    }
}