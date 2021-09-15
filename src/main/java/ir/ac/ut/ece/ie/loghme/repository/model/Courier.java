package ir.ac.ut.ece.ie.loghme.repository.model;

public class Courier {
    private String id;
    private int velocity;
    private Location location;

    public Location getLocation() {
        return location;
    }

    public int getVelocity() {
        return velocity;
    }

    public String getId() {
        return id;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setId(String id) {
        this.id = id;
    }
}
