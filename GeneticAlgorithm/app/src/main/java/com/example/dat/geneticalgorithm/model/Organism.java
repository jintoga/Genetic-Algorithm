package com.example.dat.geneticalgorithm.model;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by DAT on 9/19/2015.
 */
public class Organism extends AsyncTask<Object, Void, int[]> {
    private int[] bitstring;
    private int[] counterArray;
    int lengthOfBitString;

    int finalPenalty;

    public Organism(int lengthOfBitString) {
        this.lengthOfBitString = lengthOfBitString;
        initiateCounterArray();
    }

    public int[] getBitstring() {
        return bitstring;
    }

    public void setBitstring(int[] bitstring) {
        this.bitstring = bitstring;
    }

    public int[] getCounterArray() {
        return counterArray;
    }

    public int getFinalPenalty() {
        return finalPenalty;
    }

    public void setFinalPenalty(int finalPenalty) {
        this.finalPenalty = finalPenalty;
    }

    public int[] generateRandomBitString() {

        bitstring = new int[lengthOfBitString];
        for (int i = 0; i < lengthOfBitString; i++) {
            Random random = new Random();
            int chance = random.nextInt(2);
            if (chance == 1)
                bitstring[i] = 1;
            else
                bitstring[i] = 0;
        }
        return bitstring;
    }

    public int[] duel(int[] normal, int[] mutant, ArrayList<ItemOfSubSpace> subSpace, ArrayList<Integer> space, double strongChance) {
        int penaltyOfNormal = countPenalty(normal, subSpace, space);
        int penaltyOfMutant = countPenalty(mutant, subSpace, space);
        Random random = new Random();
        double chance = random.nextDouble();

        int[] chosenOne;
        //if strongChance happens then choose the STRONGER ONE
        if (chance <= strongChance) {
            if (penaltyOfMutant < penaltyOfNormal) {        //penalty bigger means weaker
                chosenOne = mutant;
            } else {
                chosenOne = normal;
            }
        } else { //otherwise choose the WEAKER ONE
            if (penaltyOfMutant > penaltyOfNormal) {
                chosenOne = mutant;
            } else {
                chosenOne = normal;
            }
        }
        return chosenOne;
    }

    public int countPenalty(int[] bitString, ArrayList<ItemOfSubSpace> subSpace, ArrayList<Integer> space) {

        for (int i = 0; i < bitString.length; i++) {
            if (bitString[i] == 1) {
                int[] selectedValue = subSpace.get(i).getValue();
                for (int j = 0; j < selectedValue.length; j++) {
                    for (int k = 0; k < space.size(); k++) {
                        int tmpVal1 = selectedValue[j];
                        int tmpVal2 = space.get(k);
                        //Log.d("Compare", tmpVal1 + " " + tmpVal2);
                        if (selectedValue[j] == space.get(k)) {
                            //Log.d("counterArray length", counterArray.length + "");
                            counterArray[k]++;

                        }
                    }
                }
            }
        }
        int penalty = 0;
        for (int i = 0; i < counterArray.length; i++) {
            if (counterArray[i] == 0 || counterArray[i] > 1) {
                penalty++;
            }
        }
        return penalty;
    }

    public void initiateCounterArray() {
        counterArray = new int[lengthOfBitString * 3];
        for (int i = 0; i < counterArray.length; i++) {
            counterArray[i] = 0;
        }
    }


    @Override
    protected int[] doInBackground(Object... params) {
        //Log.d("doInBackground", "doing in background");
        int[] normal = (int[]) params[0];
        int[] mutant = (int[]) params[1];
        ArrayList<ItemOfSubSpace> subSpace = (ArrayList<ItemOfSubSpace>) params[2];
        ArrayList<Integer> space = (ArrayList<Integer>) params[3];
        double strongChance = (double) params[4];

        int[] chosenOne = duel(normal, mutant, subSpace, space, strongChance);
        return chosenOne;
    }


}
