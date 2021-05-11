package com.confusinguser.confusingaddons.utils.bazaar;

import java.util.Map;

public class BazaarProduct {

    private final String productName;
    private final double buyPrice;
    private final double sellPrice;
    private final int instaSells;

    public BazaarProduct(String productName, double buyPrice, double sellPrice, int instaSells) {
        this.productName = productName;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.instaSells = instaSells;
    }

    public String getProductName() {
        return productName;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public int getInstaSells() {
        return instaSells;
    }

    public double getNPCSellPrice() {
        for (Map.Entry<String, Double> npcSellPrice : Constants.npcSellPrices.entrySet()) {
            if (npcSellPrice.getKey().contentEquals(getProductName()))
                return npcSellPrice.getValue();
        }

        if (!getProductName().equals("ENCHANTED_CARROT_ON_A_STICK"))
            return -1;
        return 0;
    }
}
