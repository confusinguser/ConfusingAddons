package com.confusinguser.confusingaddons.utils;

import com.confusinguser.confusingaddons.ConfusingAddons;

public class LangUtils {
    ConfusingAddons main;

    public LangUtils(ConfusingAddons main) {
        this.main = main;
    }

    public String getMinecraftColorCodeFromDouble(double input) {
        if (input <= .33) {
            return "c";
        }
        if (input <= .66) {
            return "e";
        }
        if (input <= .99) {
            return "a";
        }
        return "2";
    }


    public String getFloorNameFromNumber(int floor) {
        if (floor == 0) {
            return "Entrance";
        } else {
            return "Floor " + floor;
        }
    }
}
