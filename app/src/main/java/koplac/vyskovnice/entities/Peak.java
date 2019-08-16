package koplac.vyskovnice.entities;

import koplac.vyskovnice.MainActivity;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Defines the object peak del objeto pico
 * @author Antonio Manuel Sanchez Amat
 * @author Juan Jose Espinosa Sanchez
 */
public class Peak implements Serializable {
    private int id;
    private String name;
    private String category;
    private String peakgroup;
    private String longitude;
    private String latitude;
    private String height;
    private String distance_from_start;
    private String mountains;
    private String region;
    private String plants_animals;
    private String reservation;
    private String geology;
    private String springs;
    private String hydrology;
    private String buildings;
    private String history;
    private String acces;
    private String attractions;
    private String view;
    private String link;
    private String notes;
    private int picture;

    /**
     * Contructor method
     */
    public Peak(){}

    /**
     * Contructor methond with all the peak data
     * @param id  identification number
     * @param name name
     * @param category category
     * @param latitude latitude coordinate
     * @param longitude longitude coordinate
     * @param height  height above sea level
     * @param distance_from_start distance from freedom square
     * @param mountains set of mountains where is situated
     * @param region region
     * @param plants_animals fauna y flora of the zone
     * @param reservation natural reserve
     * @param geology geology
     * @param springs springs
     * @param hydrology hydrology
     * @param buildings near buildings
     * @param history history of the zone
     * @param acces acces to the zone
     * @param attractions near attractions
     * @param view views
     * @param link links
     * @param notes notes
     * @param picture picture
     */
    public Peak(int id, String name, String category, String peakgroup, String latitude, String longitude, String height, String distance_from_start,
                String mountains, String region, String plants_animals, String reservation, String geology,
                String springs, String hydrology, String buildings, String history, String acces, String attractions,
                String view, String link, String notes, int picture) {
        this.id = id;
        this.name = name;
        this.category= category;
        this.peakgroup = peakgroup;
        this.longitude = longitude;
        this.latitude = latitude;
        this.height = height;
        this.distance_from_start = distance_from_start;
        this.mountains = mountains;
        this.region = region;
        this.plants_animals = plants_animals;
        this.reservation = reservation;
        this.geology = geology;
        this.springs = springs;
        this.hydrology = hydrology;
        this.buildings = buildings;
        this.history = history;
        this.acces = acces;
        this.attractions = attractions;
        this.view = view;
        this.link = link;
        this.notes = notes;
        this.picture = picture;
    }

    /**
     * Metodo constructor
     * @param id identification number
     * @param name name
     * @param height height above sea level
     * @param longitude longitude coordinate
     * @param latitude latitude coordinate
     * @param picture picture
     */
    public Peak(int id, String name, String height, String longitude, String latitude, int picture) {
        this.id = id;
        this.name = name;
        this.height = height;
        this.longitude = longitude;
        this.latitude = latitude;
        this.picture = picture;
    }

    /**
     * Returns an peak object given a id
     * @param id id of the peak we want to retrieve
     * @return object Peak
     */
    public static Peak seekPeak(int id){
        Peak searchedPeak;

        searchedPeak=null;
        for(Peak auxPeak: MainActivity.peaks){
            if(auxPeak.getId()==id){
                searchedPeak=auxPeak;
                break;
            }
        }

        return searchedPeak;
    }

    /**
     * Returns a list of peak object given a list of id´s
     * @param  peakIds list of id of the peaks we want to retrieve
     * @return  list of peak objects
     */
    public static List<Peak> seekPeaks(List<Integer> peakIds){
        List<Peak> peakFounded;

        peakFounded =new LinkedList<>();
        for(int id:peakIds){
            peakFounded.add(Peak.seekPeak(id));
        }
        return peakFounded;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCategory(){return category;}
    public void setCategory(String category){this.category=category;}
    public String getHeight() {
        return height;
    }
    public void setHeight(String height) {
        this.height = height;
    }
    public String getDistance_from_start() {
        return distance_from_start;
    }
    public void setDistance_from_start(String distance_from_start) {
        this.distance_from_start = distance_from_start;
    }
    public String getMountains() {
        return mountains;
    }
    public void setMountains(String mountains) {
        this.mountains = mountains;
    }
    public String getRegion() {
        return region;
    }
    public void setRegion(String region) {
        this.region = region;
    }
    public String getPlants_animals() {
        return plants_animals;
    }
    public void setPlants_animals(String plants_animals) {
        this.plants_animals = plants_animals;
    }
    public String getReservation() {
        return reservation;
    }
    public void setReservation(String reservation) {
        this.reservation = reservation;
    }
    public String getGeology() {
        return geology;
    }
    public void setGeology(String geology) {
        this.geology = geology;
    }
    public String getSprings() {
        return springs;
    }
    public void setSprings(String springs) {
        this.springs = springs;
    }
    public String getHydrology() {
        return hydrology;
    }
    public void setHydrology(String hydrology) {
        this.hydrology = hydrology;
    }
    public String getBuildings() {
        return buildings;
    }
    public void setBuildings(String buildings) {
        this.buildings = buildings;
    }
    public String getHistory() {
        return history;
    }
    public void setHistory(String history) {
        this.history = history;
    }
    public String getAcces() {
        return acces;
    }
    public void setAcces(String acces) {
        this.acces = acces;
    }
    public String getAttractions() {
        return attractions;
    }
    public void setAttractions(String attractions) {
        this.attractions = attractions;
    }
    public String getView() {
        return view;
    }
    public void setView(String view) {
        this.view = view;
    }
    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public String getLongitude() {
        return longitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    public String getLatitude() {
        return latitude;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    public int getPicture() {
        return picture;
    }
    public void setPicture(int picture) {
        this.picture = picture;
    }

}
