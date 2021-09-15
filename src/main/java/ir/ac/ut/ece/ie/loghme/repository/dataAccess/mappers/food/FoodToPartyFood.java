package ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.food;

import ir.ac.ut.ece.ie.loghme.repository.dataAccess.mappers.foodParty.FoodPartyMapper;
import ir.ac.ut.ece.ie.loghme.repository.model.Food;
import ir.ac.ut.ece.ie.loghme.repository.model.FoodUnderSale;
import ir.ac.ut.ece.ie.loghme.repository.model.LoghmeRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FoodToPartyFood {
    private static FoodToPartyFood curInstance;

    public static FoodToPartyFood getInstance() {
        if(curInstance == null){
            curInstance = new FoodToPartyFood();
        }
        return curInstance;
    }

    public FoodUnderSale getFoodUnderSale(Food food) {
        try {
            List<Food> foodparty = FoodMapper.getInstance().findAllFoodParty();
            for(int i=0; i<foodparty.size(); i++) {
                if (foodparty.get(i).getName().equals(food.getName()) &&
                        foodparty.get(i).getRestaurantId().equals(food.getRestaurantId())) {
                    FoodUnderSale foodUnderSale = FoodPartyMapper.getInstance().
                            find(new ArrayList<>(Arrays.asList(food.getRestaurantId(), food.getName())));
                    foodUnderSale.setDescription(food.getDescription());
                    foodUnderSale.setImage(food.getImage());
                    foodUnderSale.setPopularity(food.getPopularity());
                    foodUnderSale.setOldPrice(food.getPrice());
                    return foodUnderSale;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
