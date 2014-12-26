package eionet.help;

import java.util.Hashtable;

/**
 *
 */
public class Screen {

    /**
     * @param id
     */
    public Screen(String id) {
        this.id = null;
        descr = null;
        areas = null;
        this.id = id;
    }

    /**
     * @param area
     */
    public void addArea(Area area) {
        if (areas == null) {
            areas = new Hashtable();
        }
        areas.put(area.getID(), area);
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
     * @return
     */
    public String getID() {
        return id;
    }

    /**
     * @param areaID
     * @return
     */
    public Area getArea(String areaID) {
        return (Area) areas.get(areaID);
    }

    /**
     * @return
     */
    public Hashtable getAreas() {
        return areas;
    }

    /**
     * @param args1
     */
    public static void main(String args1[]) {
    }

    public static final String ID = "id";
    public static final String DESCR = "descr";
    private String id;
    private String descr;
    private Hashtable areas;
}
