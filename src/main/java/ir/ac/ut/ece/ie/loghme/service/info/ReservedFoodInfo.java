package ir.ac.ut.ece.ie.loghme.service.info;

import ir.ac.ut.ece.ie.loghme.repository.model.LoghmeRepository;

public class ReservedFoodInfo {
    private String name;
    private int num;
    private int totalCost;
    private int isUnderSale;

    public String getName() {
        return name;
    }

    public int getNum() {
        return num;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public int getIsUnderSale() {
        return isUnderSale;
    }

    public ReservedFoodInfo(String name, int num, int totalCost, int isUnderSale){
        this.name = name;
        this.num = num;
        this.totalCost = totalCost;
        this.isUnderSale = isUnderSale;
    }
}
