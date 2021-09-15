package ir.ac.ut.ece.ie.loghme.repository.model;

public class FoodUnderSale extends Food {
    private int count;
    private int oldPrice;
    private int valid;

    public void setValid(int valid) {
        this.valid = valid;
    }

    public int getValid() {
        return valid;
    }

    public int getCount() {
        return count;
    }

    public int getOldPrice() {
        return oldPrice;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setOldPrice(int oldPrice) {
        this.oldPrice = oldPrice;
    }
}
