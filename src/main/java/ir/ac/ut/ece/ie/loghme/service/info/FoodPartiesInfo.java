package ir.ac.ut.ece.ie.loghme.service.info;

import ir.ac.ut.ece.ie.loghme.repository.model.FoodUnderSale;
import ir.ac.ut.ece.ie.loghme.repository.model.LoghmeRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class FoodPartiesInfo {
    private ArrayList<FoodPartyInfo> foods = new ArrayList<FoodPartyInfo>();
    private long remainTime = 0;


    public long getRemainTime() {
        return remainTime;
    }

    public ArrayList<FoodPartyInfo> getFoods() {
        return foods;
    }

    public FoodPartiesInfo(ArrayList<FoodPartyInfo> foods){
        LocalDateTime date = LocalDateTime.now();
        this.remainTime = LoghmeRepository.getCurInstance().getFoodPartyEndTime() - date.toLocalTime().toSecondOfDay();
        System.out.println("--remain time--");
        System.out.println(this.remainTime);
        this.foods = foods;

    }
}
