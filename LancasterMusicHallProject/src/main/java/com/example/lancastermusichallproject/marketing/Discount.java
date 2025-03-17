package com.example.lancastermusichallproject.marketing;

/**
 * Represents discount details applied to ticket purchases.
 */
public class Discount {
    private int discountID;
    private String discountType;
    private double discountAmount;

    // Constructor
    public Discount(int discountID, String discountType, double discountAmount) {
        this.discountID = discountID;
        this.discountType = discountType;
        this.discountAmount = discountAmount;
    }

    // Returns the discount ID
    public int getDiscountID() {
        return discountID;
    }

    // Returns the discount type (e.g., Student, Military)
    public String getDiscountType() {
        return discountType;
    }

    // Returns the discount amount applied
    public double getDiscountAmount() {
        return discountAmount;
    }
}
