package eionet.help;

import java.util.Hashtable;

/**
 *
 */
public class Area {

    /**
     * @param screenID
     * @param id
     */
    public Area(String screenID, String id) {
        this.id = null;
        this.screenID = null;
        descr = null;
        popupWidth = null;
        popupLength = null;
        md5 = null;
        htmls = new Hashtable();
        this.screenID = screenID;
        this.id = id;
    }

    /**
     * @return
     */
    public String getID() {
        return id;
    }

    /**
     * @param descr
     */
    public void setDescription(String descr) {
        this.descr = descr;
    }

    /**
     * @return
     */
    public String getDescription() {
        return descr;
    }

    /**
     * @param popupWidth
     */
    public void setPopupWidth(String popupWidth) {
        this.popupWidth = popupWidth;
    }

    /**
     * @return
     */
    public String getPopupWidth() {
        return popupWidth;
    }

    /**
     * @param popupLength
     */
    public void setPopupLength(String popupLength) {
        this.popupLength = popupLength;
    }

    /**
     * @return
     */
    public String getPopupLength() {
        return popupLength;
    }

    /**
     * @param md5
     */
    public void setMd5(String md5) {
        this.md5 = md5;
    }

    /**
     * @return
     */
    public String getMd5() {
        return md5;
    }

    /**
     * @return
     */
    public String getScreenID() {
        return screenID;
    }

    /**
     * @param html
     * @param lang
     */
    public void setHTML(String html, String lang) {
        if (html != null && lang != null) {
            htmls.put(lang, html);
        }
    }

    /**
     * @param lang
     * @return
     */
    public String getHTML(String lang) {
        if (lang == null) {
            lang = "";
        }
        String html = (String) htmls.get(lang);
        if (html == null) {
            html = (String) htmls.get("");
        }
        if (html == null) {
            html = "";
        }
        return html;
    }

    /**
     * @return
     */
    public Hashtable getHTMLs() {
        return htmls;
    }

    /**
     * @param htmls
     */
    public void setHTMLs(Hashtable htmls) {
        this.htmls = htmls;
    }

    /**
     * @param args1
     */
    public static void main(String args1[]) {
    }

    public static final String DEFAULT_LANG = "";
    public static final String ID = "id";
    public static final String SCREEN_ID = "screen_id";
    public static final String DESCR = "descr";
    public static final String POPUP_WIDTH = "popup-width";
    public static final String POPUP_LENGTH = "popup-length";
    public static final String MD5 = "md5";
    private String id;
    private String screenID;
    private String descr;
    private String popupWidth;
    private String popupLength;
    private String md5;
    private Hashtable htmls;
}
