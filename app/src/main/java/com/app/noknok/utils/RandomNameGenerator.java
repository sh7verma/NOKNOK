package com.app.noknok.utils;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by dev on 12/7/17.
 */

public class RandomNameGenerator {

    public static ArrayList<String> color = new ArrayList<>();
    public static ArrayList<String> name = new ArrayList<>();

    public static ArrayList<String> main = new ArrayList<>();

    public  ArrayList<String> add() {

        color.add("Violet");
        color.add("Coral");
        color.add("Cyan");
        color.add("Orchid");
        color.add("Ivory");
        color.add("Lavender");
        color.add("Turquoise");
        color.add("Red");
        color.add("Amber");
        color.add("Teal");
        color.add("Emerald");
        color.add("Maroon");
        color.add("Peach");
        color.add("Ruby");
        color.add("Charcoal");
        color.add("Olive");
        color.add("Black");
        color.add("Purple");
        color.add("Green");
        color.add("Lime");
        color.add("Pink");
        color.add("Sapphire");
        color.add("Blue");
        color.add("Azure");
        color.add("Indigo");


        name.add("Wagon");
        name.add("Bird");
        name.add("Panther");
        name.add("Mask");
        name.add("Pinwheel");
        name.add("Ocean");
        name.add("Crayon");
        name.add("Raft");
        name.add("Thread");
        name.add("Star");
        name.add("Football");
        name.add("Beetle");
        name.add("Tulip");
        name.add("Bow");
        name.add("Aircraft");
        name.add("Spider");
        name.add("Python");
        name.add("Saxophone");
        name.add("Mushroom");
        name.add("Dolphin");
        name.add("Castle");
        name.add("Cruise");
        name.add("Kite");
        name.add("Snowflake");
        name.add("Turtle");


        for (int i = 0; i < color.size(); i++) {

            for (int j = 0; j < name.size(); j++) {
                main.add(name.get(i) + "," + color.get(j));
            }
        }
        System.out.println(main.size());
        return main;
    }


    public String get(ArrayList<String> main) {

        int value;
        Random random = new Random();
        value = random.nextInt(main.size());
        return main.get(value);
    }

}
