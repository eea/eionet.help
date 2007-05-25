package com.tee.uit.help;

import com.tee.uit.security.*;
import java.io.PrintStream;
import java.util.*;

/**
 * 
 */
public class RemoteService
{

    /**
     * 
     */
    public RemoteService()
    {
        user = null;
    }

    /**
     * @param user
     */
    public RemoteService(AppUser user)
    {
        this.user = null;
        this.user = user;
    }

    /**
     * @return
     * @throws HelpException
     * @throws SignOnException
     */
    public Vector getScreens()
        throws HelpException, SignOnException
    {
        guard();
        Vector v = new Vector();
        Hashtable screens = Helps.getHelps();
        if(screens == null)
            return v;
        Hashtable hash;
        for(Iterator iter = screens.values().iterator(); iter.hasNext(); v.add(hash))
        {
            Screen screen = (Screen)iter.next();
            hash = new Hashtable();
            hash.put("id", screen.getID());
            hash.put("descr", screen.getDescription());
        }

        return v;
    }

    /**
     * @param screenID
     * @return
     * @throws HelpException
     * @throws SignOnException
     */
    public Vector getAreas(String screenID)
        throws HelpException, SignOnException
    {
        guard();
        Vector v = new Vector();
        Hashtable screens = Helps.getHelps();
        if(screens == null)
            return v;
        Screen screen = (Screen)screens.get(screenID);
        if(screen == null)
            return v;
        Hashtable areas = screen.getAreas();
        if(areas == null)
            return v;
        Hashtable hash;
        for(Iterator iter = areas.values().iterator(); iter.hasNext(); v.add(hash))
        {
            Area area = (Area)iter.next();
            hash = new Hashtable();
            hash.put("id", area.getID());
            hash.put("descr", area.getDescription());
            hash.put("popup-width", area.getPopupWidth());
            hash.put("popup-length", area.getPopupLength());
            hash.put("screen_id", area.getScreenID());
            hash.put("md5", area.getMd5());
        }

        return v;
    }

    /**
     * @param screenID
     * @param areaID
     * @return
     * @throws HelpException
     * @throws SignOnException
     */
    public Hashtable getHtmls(String screenID, String areaID)
        throws HelpException, SignOnException
    {
        guard();
        Hashtable hash = new Hashtable();
        Hashtable screens = Helps.getHelps();
        if(screens == null)
            return hash;
        Screen screen = (Screen)screens.get(screenID);
        if(screen == null)
            return hash;
        Area area = screen.getArea(areaID);
        if(area == null)
        {
            return hash;
        } else
        {
            hash = area.getHTMLs();
            return hash;
        }
    }

    /**
     * @param id
     * @param descr
     * @return
     * @throws HelpException
     * @throws SignOnException
     */
    public String createScreen(String id, String descr)
        throws HelpException, SignOnException
    {
        guard("hc");
        Helps.createScreen(id, descr);
        Helps.reset();
        return id;
    }

    /**
     * @param screenID
     * @param id
     * @param descr
     * @return
     * @throws HelpException
     * @throws SignOnException
     */
    public String createArea(String screenID, String id, String descr)
        throws HelpException, SignOnException
    {
        guard("hc");
        Helps.createArea(screenID, id, descr);
        Helps.reset();
        return id;
    }

    /**
     * @param ids
     * @return
     * @throws HelpException
     * @throws SignOnException
     */
    public String removeScreens(Vector ids)
        throws HelpException, SignOnException
    {
        guard("hc");
        Helps.removeScreens(ids);
        Helps.reset();
        return "";
    }

    /**
     * @param screenID
     * @param ids
     * @return
     * @throws HelpException
     * @throws SignOnException
     */
    public String removeAreas(String screenID, Vector ids)
        throws HelpException, SignOnException
    {
        guard("hc");
        Helps.removeAreas(screenID, ids);
        Helps.reset();
        return "";
    }

    /**
     * @param screenID
     * @param areaID
     * @param text
     * @return
     * @throws HelpException
     * @throws SignOnException
     */
    public String editHtml(String screenID, String areaID, String text)
        throws HelpException, SignOnException
    {
        guard("hc");
        Helps.editHtml(screenID, areaID, text);
        Helps.reset();
        return "";
    }

    /**
     * @param screenID
     * @param areaID
     * @param html
     * @param popupWidth
     * @param popupLength
     * @return
     * @throws HelpException
     * @throws SignOnException
     */
    public String editArea(String screenID, String areaID, String html, String popupWidth, String popupLength)
        throws HelpException, SignOnException
    {
        guard("hc");
        Helps.editArea(screenID, areaID, html, popupWidth, popupLength);
        Helps.reset();
        return "";
    }

    /**
     * @return
     * @throws HelpException
     * @throws SignOnException
     */
    public String reset()
        throws HelpException, SignOnException
    {
        guard("hc");
        Helps.reset();
        return "";
    }

    /**
     * @return
     */
    public static String[] methodNames()
    {
        String r[] = {
            "getScreens", "getAreas", "getHtmls", "createScreen", "createArea", "removeScreens", "removeAreas", "editHtml", "editArea", "reset"
        };
        return r;
    }

    /**
     * @return
     */
    public static String[] valueTypes()
    {
        String r[] = {
            "ARRAY", "ARRAY", "STRUCT", "STRING", "STRING", "STRING", "STRING", "STRING", "STRING", "STRING"
        };
        return r;
    }

    /**
     * @throws SignOnException
     */
    private void guard()
        throws SignOnException
    {
        if(user == null)
            throw new SignOnException("Not authenticated!");
        else
            return;
    }

    /**
     * @param prm
     * @throws SignOnException
     */
    private void guard(String prm)
        throws SignOnException
    {
        guard();
        String prms = AccessController.getPermissions(user.getUserName());
        if(prms != null && prms.indexOf("/:" + prm) == -1)
            throw new SignOnException("Not permitted!");
        else
            return;
    }

    /**
     * @param args
     */
    public static void main(String args[])
    {
        try
        {
            AppUser u = new AppUser();
            u.authenticate("test", "xxx");
            RemoteService r = new RemoteService(u);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace(System.out);
        }
    }

    AppUser user;
}