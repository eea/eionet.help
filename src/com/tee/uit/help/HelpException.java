package com.tee.uit.help;

/**
 * 
 */
public class HelpException extends Exception
{

    /**
     * @param t
     * @param s
     */
    public HelpException(Throwable t, String s)
    {
        super(s);
    }

    /**
     * @param s
     */
    public HelpException(String s)
    {
        super(s);
    }
}