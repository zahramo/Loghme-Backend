package ir.ac.ut.ece.ie.loghme.repository.scheduler;

import ir.ac.ut.ece.ie.loghme.repository.model.LoghmeRepository;

import java.time.LocalDateTime;

public class FoodPartyDataGetter implements Runnable {
    @Override
    public void run() {
        try {
            LoghmeRepository.getCurInstance().deletePartyFoodsFromUsersCart();
            LocalDateTime date = LocalDateTime.now();
            LoghmeRepository.getCurInstance().setFoodPartyEndTime(date.toLocalTime().toSecondOfDay() + 1800);
            LoghmeRepository.getCurInstance().getFoodPartyData();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Error in filling foodParty data");
        }
    }
}

