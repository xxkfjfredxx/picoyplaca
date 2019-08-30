package com.picoplaca.fredr.co.models;

/**
 * Created by Fred Rueda on 30/08/2019
 * Developer Fred Rueda
 * Email fredjruedao@gmail.com
 */
public class Plate {

    private String id;
    private String plate;
    private boolean safePassage;
    private boolean contravention;
    private String lat;
    private String lng;
    private String dateRegister;

    public Plate(String id, String plate, boolean safePassage, boolean contravention, String dateRegister) {
        this.id = id;
        this.plate = plate;
        this.safePassage = safePassage;
        this.contravention = contravention;
        this.dateRegister = dateRegister;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public boolean isSafePassage() {
        return safePassage;
    }

    public void setSafePassage(boolean safePassage) {
        this.safePassage = safePassage;
    }

    public boolean isContravention() {
        return contravention;
    }

    public void setContravention(boolean contravention) {
        this.contravention = contravention;
    }

    public String getDateRegister() {
        return dateRegister;
    }

    public void setDateRegister(String dateRegister) {
        this.dateRegister = dateRegister;
    }
}
