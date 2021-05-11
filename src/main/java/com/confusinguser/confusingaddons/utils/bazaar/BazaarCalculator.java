package com.confusinguser.confusingaddons.utils.bazaar;

import com.confusinguser.confusingaddons.ConfusingAddons;
import com.confusinguser.confusingaddons.utils.LangUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BazaarCalculator {

    ConfusingAddons main;

    public BazaarCalculator(ConfusingAddons main) {
        this.main = main;
    }

    public void reload() {
        List<BazaarProduct> bazaarProducts = main.getApiUtil().getBazaarProducts();
        List<MoneyMakingMethod> moneyMakingMethods = new ArrayList<>();
//        bazaarProducts = bazaarProducts.stream().filter(bazaarProduct -> bazaarProduct.getNPCSellPrice() > 0).sorted(Comparator.comparingDouble(bazaarProduct -> bazaarProduct.getSellPrice() - bazaarProduct.getNPCSellPrice())).collect(Collectors.toList());

        for (BazaarProduct bazaarProduct : bazaarProducts) {
            if (bazaarProduct.getNPCSellPrice() == -1) {
//                System.out.printf("NPC sell price for %s is missing\n", bazaarProduct.getProductName());
                continue;
            }
            moneyMakingMethods.add(new MoneyMakingMethod(bazaarProduct,
                    Math.round((bazaarProduct.getNPCSellPrice() - bazaarProduct.getSellPrice()) / bazaarProduct.getSellPrice() * 1000) / 10d,
                    MoneyMakingMethod.Strategy.NPC_SELL));
        }
        for (BazaarProduct bazaarProduct : bazaarProducts) {
            moneyMakingMethods.add(new MoneyMakingMethod(bazaarProduct,
                    Math.round((bazaarProduct.getBuyPrice() - bazaarProduct.getSellPrice()) / bazaarProduct.getSellPrice() * 1000) / 10d,
                    MoneyMakingMethod.Strategy.MARGIN_FLIP));
        }

        moneyMakingMethods.sort(Comparator.comparingDouble(MoneyMakingMethod::getProfit));
        for (MoneyMakingMethod method : moneyMakingMethods) {
            if (method.getProfit() <= 0) {
                continue;
            }
                BazaarProduct bazaarProduct = method.getBazaarProduct();
            String productName = LangUtils.beautifyString(bazaarProduct.getProductName());
            double npcSellPrice = bazaarProduct.getNPCSellPrice();
            double sellPrice = bazaarProduct.getSellPrice();
            double buyPrice = bazaarProduct.getBuyPrice();

            if (method.getStrategy() == MoneyMakingMethod.Strategy.NPC_SELL) {
                if (bazaarProduct.getNPCSellPrice() > 0 && sellPrice > 0) {
                }
            } else if (method.getStrategy() == MoneyMakingMethod.Strategy.MARGIN_FLIP) {
                System.out.printf("Margin flip %s\n  Buy Price: %s%s%s insta-sells in the last 7d\n  Sell Price: %s\n  Profit: %s%%\n",
                        productName,
                        LangUtils.useIntVersion(sellPrice) ? (int) sellPrice : sellPrice,
                        LangUtils.alignString("  Buy Price: " + bazaarProduct.getSellPrice(), 30, 2),
                        bazaarProduct.getInstaSells(),
                        LangUtils.useIntVersion(buyPrice) ? (int) buyPrice : buyPrice,
                        LangUtils.useIntVersion(method.getProfit()) ? (int) method.getProfit() : method.getProfit());
            }
        }
    }
}
