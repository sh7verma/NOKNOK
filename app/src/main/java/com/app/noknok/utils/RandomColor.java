package com.app.noknok.utils;

import com.app.noknok.models.ColorModel;

import java.util.ArrayList;

/**
 * Created by dev on 8/8/17.
 */

public class RandomColor {

    ArrayList<String> drawableTopColorArrayList = new ArrayList<>();
    ArrayList<String> drawableBottomColorArrayList = new ArrayList<>();
    ArrayList<ColorModel> drawableColorArrayList = new ArrayList<>();

    public RandomColor() {

        drawableTopColorArrayList.add("#FF2C66");
        drawableBottomColorArrayList.add("#FF5F63");
        drawableTopColorArrayList.add("#FA2B1B");
        drawableBottomColorArrayList.add("#FB7B74");
        drawableTopColorArrayList.add("#F85A55");
        drawableBottomColorArrayList.add("#E7315C");
        drawableTopColorArrayList.add("#F0CD3B");
        drawableBottomColorArrayList.add("#EBB928");
        drawableTopColorArrayList.add("#FF965E");
        drawableBottomColorArrayList.add("#FFB12D");
        drawableTopColorArrayList.add("#14D264");
        drawableBottomColorArrayList.add("#6AEE82");
        drawableTopColorArrayList.add("#85C365");
        drawableBottomColorArrayList.add("#7FB963");
        drawableTopColorArrayList.add("#914FFD");
        drawableBottomColorArrayList.add("#8142E9");
        drawableTopColorArrayList.add("#C06DFF");
        drawableBottomColorArrayList.add("#B350FF");
        drawableTopColorArrayList.add("#13A6FF");
        drawableBottomColorArrayList.add("#3A82FD");
        drawableTopColorArrayList.add("#3D3D40");
        drawableBottomColorArrayList.add("#969799");
        drawableTopColorArrayList.add("#000000");
        drawableBottomColorArrayList.add("#3C4045");
        drawableTopColorArrayList.add("#008080");
        drawableBottomColorArrayList.add("#14C1C1");
        drawableTopColorArrayList.add("#FC1186");
        drawableBottomColorArrayList.add("#FF66B3");
        drawableTopColorArrayList.add("#893A1F");
        drawableBottomColorArrayList.add("#893A1F");
        drawableTopColorArrayList.add("#9494F8");
        drawableBottomColorArrayList.add("#C8C8F5");
        drawableTopColorArrayList.add("#F9AF6D");
        drawableBottomColorArrayList.add("#FED1A8");
        drawableTopColorArrayList.add("#01FF01");
        drawableBottomColorArrayList.add("#A5FEA5");
        drawableTopColorArrayList.add("#16B716");
        drawableBottomColorArrayList.add("#1EF41E");
        drawableTopColorArrayList.add("#36454F");
        drawableBottomColorArrayList.add("#5B7687");
        drawableTopColorArrayList.add("#0F52BA");
        drawableBottomColorArrayList.add("#5A9AFD");
        drawableTopColorArrayList.add("#FE5C7A");
        drawableBottomColorArrayList.add("#FF81E0");
        drawableTopColorArrayList.add("#FCCC4D");
        drawableBottomColorArrayList.add("#FFEA94");

        for(int i=0;i<drawableTopColorArrayList.size();i++){
            ColorModel colorModel=new ColorModel();
            colorModel.setGradient1(drawableTopColorArrayList.get(i));
            colorModel.setGradient2(drawableBottomColorArrayList.get(i));
            drawableColorArrayList.add(colorModel);
        }

    }



    public ColorModel getDrawable(int pos) {
        return drawableColorArrayList.get(pos);
    }

    public int size() {
        return drawableColorArrayList.size();
    }

}