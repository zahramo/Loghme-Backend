package ir.ac.ut.ece.ie.loghme.repository.model;

public class Food {
    private String name;
    private String description;
    private int popularity;
    private String restaurantId;
    private int price;
    private String image;

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public int getPopularity() {
        return popularity;
    }
    public String getRestaurantId() {
        return restaurantId;
    }
    public int getPrice() {
        return price;
    }
    public String getImage() {
        return image;
    }


    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public void setImage(String image) {
        this.image = image;
    }

}
