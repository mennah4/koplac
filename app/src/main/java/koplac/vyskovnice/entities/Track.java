package koplac.vyskovnice.entities;

import org.osmdroid.util.GeoPoint;

import java.util.List;

/**
 * Definition of the path object
 * @author Antonio Manuel Sanchez Amat
 * @author Juan Jose Espinosa Sanchez
 */
public class Track {
    private int id;
    private String name;
    private float length;
    private int pathColor;
    private List<GeoPoint> trackPoints;
    private List<GeoPoint> checkPoints;

    /**
     * Constructor Method
     * @param id identification
     * @param name name
     * @param length length
     * @param pathColor color
     * @param trackPoints points of the path
     * @param checkPoints path checkpoints
     */
    public Track(final int id,final String name,final float length,final int pathColor,List<GeoPoint> trackPoints,List<GeoPoint> checkPoints){
        this.id=id;
        this.name=name;
        this.length=length;
        this.pathColor=pathColor;
        this.trackPoints=trackPoints;
        this.checkPoints=checkPoints;
    }

    /**
     * Returns the id of the path
     * @return id of the path
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the name of the path
     * @return name of the path
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the color of the path
     * @return color of the path
     */
    public int getPathColor(){return pathColor;}

    /**
     * Assigns a id to the path
     * @param id del pico
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Assigns a name to the path
     * @param name name of the path
     */
    public void setName(String name) {
        this.name = name;
    }

}
