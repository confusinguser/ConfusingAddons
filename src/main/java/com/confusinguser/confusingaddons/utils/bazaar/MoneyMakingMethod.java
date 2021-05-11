package com.confusinguser.confusingaddons.utils.bazaar;

public class MoneyMakingMethod {
    private final BazaarProduct bazaarProduct;
    private final double profit;
    private final Strategy strategy;

    public MoneyMakingMethod(BazaarProduct bazaarProduct, double profit, Strategy strategy) {
        this.bazaarProduct = bazaarProduct;
        this.profit = profit;
        this.strategy = strategy;
    }

    public BazaarProduct getBazaarProduct() {
        return bazaarProduct;
    }

    public double getProfit() {
        return profit;
    }

    public Strategy getStrategy() {
        return strategy;
    }


    public enum Strategy {
        MARGIN_FLIP,
        NPC_SELL
    }
}
