package com.example.dat.geneticalgorithm.model;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by DAT on 9/19/2015.
 */
public class Population extends AsyncTask<Object, Void, Object> {
    private ArrayList<Organism> listOfOrganisms = new ArrayList<>(), tempPopulation, duelList;
    private Random random = new Random();
    private int lengthOfBitString, numberOfGenerations, numberOfOrganism, numberOfReproductions;
    private double mutateChance, strongChance, dieChance;
    private ArrayList<Integer> space, tempSpace;
    private ArrayList<ItemOfSubSpace> subSpace;

    public Population(int numberOfGenerations, int numberOfOrganism, int lengthOfBitString, int numberOfReproductions, double mutateChance, double strongChance, double dieChance) {
        this.numberOfGenerations = numberOfGenerations;
        this.numberOfOrganism = numberOfOrganism;
        this.lengthOfBitString = lengthOfBitString;
        this.numberOfReproductions = numberOfReproductions;
        this.mutateChance = mutateChance;
        this.strongChance = strongChance;
        this.dieChance = dieChance;
        generateSpace(lengthOfBitString);
        generateSubSpace(lengthOfBitString);
    }

    public ArrayList<Organism> generatePopulation() {
        for (int i = 0; i < numberOfOrganism; i++) {
            Organism organism = new Organism(lengthOfBitString);
            organism.generateRandomBitString();
            listOfOrganisms.add(organism);
        }
        return listOfOrganisms;
    }

    ArrayList<GraphicResult> graphicResults = new ArrayList<>();

    public ArrayList<GraphicResult> getGraphicResults() {
        return graphicResults;
    }

    public void startOperations() {

        if (listOfOrganisms.size() > 0) {

            for (int i = 0; i < numberOfGenerations; i++) {
                //Reproduction
                tempPopulation = new ArrayList<>(listOfOrganisms);
                for (int j = 0; j < numberOfReproductions; j++) {
                    int randIndexFather = random.nextInt(tempPopulation.size());
                    Organism father = tempPopulation.get(randIndexFather);
                    tempPopulation.remove(randIndexFather);
                    int randIndexMother = random.nextInt(tempPopulation.size());
                    Organism mother = tempPopulation.get(randIndexMother);
                    tempPopulation.remove(randIndexMother);
                    crossOver(father, mother, tempPopulation);
                }
                //Mutation
                for (int j = 0; j < listOfOrganisms.size(); j++) {
                    mutate(listOfOrganisms.get(j));
                }
                //Selection
                duelList = new ArrayList<>(listOfOrganisms);
                for (int j = 0; j < numberOfReproductions; j++) {
                    int ranIndex1 = random.nextInt(duelList.size());
                    Organism organism1 = duelList.get(ranIndex1);
                    duelList.remove(ranIndex1);
                    int ranIndex2 = random.nextInt(duelList.size());
                    Organism organism2 = duelList.get(ranIndex2);
                    duelList.remove(ranIndex2);
                    selection(organism1, organism2);
                }
                Log.d("Population", getListOfOrganisms().size() + "");
                graphicResults.add(calculate());
            }
        }
    }

    private GraphicResult calculate() {
        for (Organism organism : listOfOrganisms) {
            int finalPenalty = countPenalty(organism, subSpace, space);
            organism.setFinalPenalty(finalPenalty);
        }
        int biggestPenalty = listOfOrganisms.get(0).getFinalPenalty();
        int smallestPenalty = listOfOrganisms.get(0).getFinalPenalty();
        int[] arrayForFindingMiddlePenalty = new int[listOfOrganisms.size()];
        for (int i = 0; i < listOfOrganisms.size(); i++) {
            arrayForFindingMiddlePenalty[i] = listOfOrganisms.get(i).getFinalPenalty();
            if (biggestPenalty < listOfOrganisms.get(i).getFinalPenalty())
                biggestPenalty = listOfOrganisms.get(i).getFinalPenalty();
            if (smallestPenalty > listOfOrganisms.get(i).getFinalPenalty())
                smallestPenalty = listOfOrganisms.get(i).getFinalPenalty();
        }
        Log.d("biggestPenalty", biggestPenalty + "");
        Log.d("smallestPenalty", smallestPenalty + "");
        Arrays.sort(arrayForFindingMiddlePenalty);
        int middlePenalty = arrayForFindingMiddlePenalty[arrayForFindingMiddlePenalty.length / 2];
        //Log.d("array", arrayForFindingMiddlePenalty.toString());
        Log.d("middlePenalty", middlePenalty + "");
        GraphicResult graphicResult = new GraphicResult();
        graphicResult.setAverage(middlePenalty);
        graphicResult.setStrongest(smallestPenalty);
        graphicResult.setWeakest(biggestPenalty);
        return graphicResult;
    }


    private void mutate(Organism organism) {
        int[] normalBitString = organism.getBitstring();
        int[] mutatedBitString = normalBitString.clone();

        for (int i = 0; i < mutatedBitString.length; i++) {
            double chanceToMutate = random.nextDouble();
            if (chanceToMutate <= this.mutateChance) {
                if (mutatedBitString[i] == 1)
                    mutatedBitString[i] = 0;
                else
                    mutatedBitString[i] = 1;
            }
        }
      /*  Log.d("Normal", normalBitString.toString());
        Log.d("Mutant", mutatedBitString.toString());
        Log.d("Space", space.toString());
        Log.d("SubSpace", subSpace.toString());*/
        int[] chosenOne = organism.doInBackground(normalBitString, mutatedBitString, subSpace, space, this.strongChance);
        //int[] chosenOne = organism.duel(normalBitString, mutatedBitString, subSpace, space, this.strongChance);
        organism.setBitstring(chosenOne);
        //turn counterArray back to zero preparing for Selection
        organism.initiateCounterArray();
        //Log.d("After Mutation", organism.toString());
    }

    public void crossOver(Organism father, Organism mother, ArrayList<Organism> tempPopulation) {
        int cross_over_point = random.nextInt(lengthOfBitString);
        int[] fatherBitString = father.getBitstring();
        int[] motherBitString = mother.getBitstring();
        int[] val_child1 = fatherBitString.clone(), val_child2 = motherBitString.clone();
        for (int i = 0; i < cross_over_point; i++) {
            val_child1[i] = motherBitString[i];
            val_child2[i] = fatherBitString[i];
        }

       /* Log.d("Father", fatherBitString.toString());
        Log.d("Mother", motherBitString.toString());
        Log.d("Child 1", val_child1.toString());
        Log.d("Child 2", val_child2.toString());*/
        Organism child1 = new Organism(lengthOfBitString);
        child1.setBitstring(val_child1);
        Organism child2 = new Organism(lengthOfBitString);
        child2.setBitstring(val_child2);
        tempPopulation.add(child1);
        tempPopulation.add(child2);
        listOfOrganisms.add(child1);
        listOfOrganisms.add(child2);
        Log.d("Born", child1.toString() + " and " + child2.toString());

    }

    private void selection(Organism organism1, Organism organism2) {
        Organism organism = (Organism) doInBackground(organism1, organism2, subSpace, space, dieChance);
        if (organism != null) {
            listOfOrganisms.remove(organism);
            Log.d("Kill", organism.toString());
        }
    }

    public Organism duel(Organism organism1, Organism organism2, ArrayList<ItemOfSubSpace> subSpace, ArrayList<Integer> space, double dieChance) {

        int penaltyOfOrganism1 = countPenalty(organism1, subSpace, space);
        int penaltyOfOrganism2 = countPenalty(organism2, subSpace, space);
        Random random = new Random();
        double chance = random.nextDouble();
        Organism organismToKill = null;
        if (chance <= dieChance) {
            if (penaltyOfOrganism1 > penaltyOfOrganism2)
                organismToKill = organism1;
            else
                organismToKill = organism2;
        }
        return organismToKill;
    }

    public int countPenalty(Organism organism, ArrayList<ItemOfSubSpace> subSpace, ArrayList<Integer> space) {
        int[] bitString = organism.getBitstring();
        int[] counterArray = organism.getCounterArray();
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
        // Log.d("penalty", penalty + "");
        organism.initiateCounterArray();
        return penalty;
    }

    private ArrayList generateSpace(int lengthOfBitString) {
        if (space == null)
            space = new ArrayList<>();
        int numberOfElementsInSpace = lengthOfBitString * 3;

        for (int i = 0; i < numberOfElementsInSpace; i++) {
            space.add(i);
        }
        //Log.d("Space", space.toString());
        return space;
    }

    private ArrayList generateSubSpace(int lengthOfBitString) {
        if (subSpace == null)
            subSpace = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < lengthOfBitString; i++) {
            tempSpace = new ArrayList<>(space);
            ItemOfSubSpace itemOfSubSpace = new ItemOfSubSpace();
            int[] val = new int[3];
            for (int j = 0; j < 3; j++) {
                int selectedNumber = random.nextInt(tempSpace.size());
                val[j] = tempSpace.get(selectedNumber);
                tempSpace.remove(selectedNumber);
            }
            itemOfSubSpace.setValue(val);
            subSpace.add(itemOfSubSpace);
        }
        return subSpace;
    }

    public ArrayList<Integer> getSpace() {
        return space;
    }

    public ArrayList<ItemOfSubSpace> getSubSpace() {
        return subSpace;
    }

    public ArrayList<Organism> getListOfOrganisms() {
        return listOfOrganisms;
    }

    @Override
    protected Object doInBackground(Object... params) {
        Organism organism1 = (Organism) params[0];
        Organism organism2 = (Organism) params[1];
        ArrayList<ItemOfSubSpace> subSpace = (ArrayList<ItemOfSubSpace>) params[2];
        ArrayList<Integer> space = (ArrayList<Integer>) params[3];
        double dieChance = (double) params[4];
        Organism organismToKill = duel(organism1, organism2, subSpace, space, dieChance);
        return organismToKill;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);

    }


}
