package me.syeds.ahead.util;

import me.syeds.ahead.R;

import me.syeds.ahead.DemoApplication;

public class FakeOrderStorage {
    
    public static Order getOrder(Integer orderNumber) {
        Order order = null;
        switch(orderNumber) {
            case 1010:
                order = new Order(orderNumber
                                  , "Steak Sandwich"
                                  , null
                                  , 12.65
                                  , DemoApplication.getAppContext().getResources().getDrawable(R.drawable.meal1));
                break;
            case 1020:
                order = new Order(orderNumber
                                  , "Chicken Panini"
                                  , null
                                  , 7.75
                                  , DemoApplication.getAppContext().getResources().getDrawable(R.drawable.meal2));
                break;
            case 1030:
                order = new Order(orderNumber
                                  , "Chicken Tikka Boti"
                                  , null
                                  , 15.35
                                  , DemoApplication.getAppContext().getResources().getDrawable(R.drawable.meal3));
                break;
        }
        return order;
    }
    
}
