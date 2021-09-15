package ir.ac.ut.ece.ie.loghme.repository.model;
import ir.ac.ut.ece.ie.loghme.repository.exception.NotEnoughPartyFood;
import ir.ac.ut.ece.ie.loghme.repository.exception.OrderFromDifferentRestaurant;
import ir.ac.ut.ece.ie.loghme.repository.exception.RestaurantNotFound;
import ir.ac.ut.ece.ie.loghme.repository.exception.RestaurantOutOfRegion;

import java.util.ArrayList;

public class User {
    private Cart cart;
    private ArrayList<Order> orders;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String passwordHash;
    private int credit;
    private Location location = new Location();
    private int id;

    public User(String firstName, String lastName, String phoneNumber, String email, String passwordHash, int id){
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.passwordHash = passwordHash;
        this.setLocation(0,0);
        this.credit = 0;
        this.id = id;
        cart = new Cart(email);
        orders = new ArrayList<>();
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Location getLocation() {
        return location;
    }

    public int getCredit() {
        return credit;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void emptyCart() {
        cart.empty();
    }

    public Cart getCart() {
        return cart;
    }

    public void setLocation(int x, int y) {
        location.setX(x);
        location.setY(y);
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public int getId() {
        return id;
    }
}
