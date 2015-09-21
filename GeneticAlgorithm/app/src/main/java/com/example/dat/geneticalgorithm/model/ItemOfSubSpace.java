package com.example.dat.geneticalgorithm.model;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by DAT on 9/20/2015.
 */
public class ItemOfSubSpace {
    private int[] value;

    public ItemOfSubSpace() {
    }

    public int[] getValue() {
        return value;
    }

    public void setValue(int[] value) {
        this.value = value;
    }

    public ItemOfSubSpace(ArrayList<Integer> itemsLeft) {
        this.value = new int[3];
        generateRandomThreeUniqueValuesFromSpace(itemsLeft);
    }

    public void generateRandomThreeUniqueValuesFromSpace(ArrayList<Integer> itemsLeft) {
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            value[i] = itemsLeft.get(random.nextInt(itemsLeft.size()));
        }
    }

}
