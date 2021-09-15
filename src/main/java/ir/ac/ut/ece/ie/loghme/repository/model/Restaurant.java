package ir.ac.ut.ece.ie.loghme.repository.model;

import ir.ac.ut.ece.ie.loghme.repository.exception.FoodAlreadyExist;
import ir.ac.ut.ece.ie.loghme.repository.exception.FoodNotFound;

import java.util.ArrayList;

public class Restaurant {
    private String id;
    private String name;
    private String description;
    private Location location;
    private String logo;
    private ArrayList<Food> menu = new ArrayList<Food>();
    private ArrayList<FoodUnderSale> foodPartyMenu = new ArrayList<FoodUnderSale>();

    public void setName(String name){
        this.name = name;
    }

    public String getId() {
        return id;
    }
    public String getName(){
        return name;
    }
    public String getDescription() {
        return description;
    }
    public Location getLocation() {
        return location;
    }
    public String getLogo() {
        return logo;
    }
    public ArrayList<Food> getMenu() {
        return menu;
    }

    public ArrayList<FoodUnderSale> getFoodPartyMenu() {
        return foodPartyMenu;
    }

    public void setFoodPartyMenu(ArrayList<FoodUnderSale> foodPartyMenu) {
        this.foodPartyMenu = foodPartyMenu;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(Location location) {
        this.location = location; }

    public void setLogo(String logo) {
        this.logo = logo;
    }
    public void setMenu(ArrayList<Food> menu) {
        this.menu = menu;
    }

    public Restaurant(){};

    public void addFood(Food food) throws FoodAlreadyExist {
        if(!doesFoodExist(food.getName())){
            menu.add(food);
        }
        else{
            throw new FoodAlreadyExist();
        }
    }

    private boolean doesFoodExist(String foodName){
        for(int i=0; i<menu.size(); i++){
            if(menu.get(i).getName().equals(foodName)) return true;
        }
        return false;
    }

    public Food getFood(String foodName) throws FoodNotFound {
        for(int i=0; i<menu.size(); i++)
            if(menu.get(i).getName().equals(foodName))
                return menu.get(i);

        throw new FoodNotFound();
    }

    public FoodUnderSale getPartyFood(String foodName) throws FoodNotFound {
        for(int i=0; i<foodPartyMenu.size(); i++)
            if(foodPartyMenu.get(i).getName().equals(foodName))
                return foodPartyMenu.get(i);
        throw new FoodNotFound();
    }

    public void decreaseFoodPartyCount(FoodUnderSale food, int amount){
        for(int i=0; i<foodPartyMenu.size(); i++){
            if(foodPartyMenu.get(i).getName().equals(food.getName())){
                foodPartyMenu.get(i).setCount(foodPartyMenu.get(i).getCount()-amount);
//                if(foodPartyMenu.get(i).getCount() == 0){
//                    foodPartyMenu.remove(i);
//                }
            }
        }
    }

    public float getAverageFoodsPopulations(){
        float sum = 0;
        for(int i=0; i<menu.size(); i++)
            sum += menu.get(i).getPopularity();
        return sum/menu.size();
    }

    public void setFoodsRestaurant(){
        for(int i=0; i<menu.size(); i++){
            menu.get(i).setRestaurantId(id);
        }
     }

     public void emptyFoodPartyMenu(){
        foodPartyMenu.clear();
     }


    public void finalize()
    {
        menu.clear();
        foodPartyMenu.clear();
    }

}
