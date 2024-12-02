package net.qldarch.archobj;

import java.sql.Date;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProjectObj {

    @JsonProperty("australian")
    private boolean australian;

    @JsonProperty("completionpd")
    private Short completionpd;

    @JsonProperty("demolished")
    private boolean demolished;

    @JsonProperty("label")
    private String label;

    @JsonProperty("location")
    private String location;
    
    @JsonProperty("latitude")
    private double latitude;
    
    @JsonProperty("longitude")
    private double longitude;
    
    @JsonProperty("completion")
    private Date completion;
    
    @JsonProperty("summary")
    private String summary;
    
    @JsonProperty("typologies")
    private String typologies;

    @JsonProperty("associateArchitect")
    private Associate associateArchitect;
    
    @JsonProperty("associateFirm")
    private Associate associateFirm;
    
    @JsonProperty("row")
    private int row;
    
    @JsonProperty("index")
    private int index;

    public boolean getAustralian() {
        return australian;
    }
    public void setAustralian(boolean australian ) {
        this.australian = australian;
    }
    public boolean getDemolished() {
        return demolished;
    }
    public void setDemolished(boolean demolished ) {
        this.demolished = demolished;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label ) {
        this.label = label;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location ) {
        this.location = location;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude ) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude ) {
        this.longitude = longitude;
    }
    public Date getCompletion() {
        return completion;
    }
    public void setCompletion(Date completion ) {
        this.completion = completion;
    }
    public Short getCompletionpd() {
        return completionpd;
    }
    public void setCompletionpd(Short completionpd) {
        this.completionpd = completionpd;
    }
    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary ) {
        this.summary = summary;
    }
    public String getTypologies() {
        return typologies;
    }
    public void setTypologies(String typologies ) {
        this.typologies = typologies;
    }
    public void setAssociateArchitect(Associate associateArchitect ) {
        this.associateArchitect = associateArchitect;
    }
    public Associate getAssociateArchitect() {
        return associateArchitect;
    }
    public void setAssociateFirm(Associate associateFirm ) {
        this.associateFirm = associateFirm;
    }
    public Associate getAssociateFirm() {
        return associateFirm;
    }
    public int getRow(){
        return row;
    }
    public void setRow(int row ) {
        this.row = row;
    }
    public int getIndex(){
        return index;
    }
    public void setIndex(int index ) {
        this.index = index;
    }

    public static class Associate {
        private int id;
        private String label;
        private String type;

        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public String getLabel() {
            return label;
        }
        public void setLabel(String label) {
            this.label = label;
        }
        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }
    }
    

    
}
