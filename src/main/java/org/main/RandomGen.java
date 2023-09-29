package org.main;

import org.main.exception.RandomGenException;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;

public class RandomGen {
    private int[] randomNums;
    private float[] probabilities;
    private Random random;

    private float[] cumulativeProbabilities;

    private static final DecimalFormat df = new DecimalFormat("0.00");

    public RandomGen(int[] randomNums, float[] probabilities, Long seed) {
        this.randomNums = randomNums;
        this.probabilities = probabilities;
        initialStateCheck();

        this.random = seed == null ? new Random() : new Random(seed);

        // Calculate cumulative probabilities once so we do not have to recalculate each time nextNum is called
        cumulativeProbabilities = new float[probabilities.length];
        float cumulative = 0;
        for (int i = 0; i < probabilities.length; i++) {
            cumulative += probabilities[i];
            cumulativeProbabilities[i] = cumulative;
        }
        cumulative = round(cumulative, 2);
        if (cumulative > 1.0f) {
            throw new RandomGenException("Total probabilities provided total to greater than 1.0 and will not provide " +
                    "a good indication of probability across the dataset");
        }
    }

    public int nextNum() {
        float randomValue = random.nextFloat();
        if (randValueGreaterThanCumulativeProbability(randomValue)) {
            return randomNums[randomNums.length - 1];
        }

        for (int i = 0; i < cumulativeProbabilities.length; i++) {
            if (randomValue < cumulativeProbabilities[i]) {
                return randomNums[i];
            }
        }

        // Fallback if the probabilities don't sum up to 1 (e.g., due to rounding errors)
        return randomNums[randomNums.length - 1];
    }

    protected boolean randValueGreaterThanCumulativeProbability(float randomValue) {
        return randomValue > cumulativeProbabilities[cumulativeProbabilities.length - 1];
    }

    /**
     * Make sure the setup of the class is in the correct state to process the probabilities
     */
    private void initialStateCheck() {
        if (randomNums == null || probabilities == null) {
            throw new RandomGenException("Data provided cannot be null");
        }
        if (randomNums.length != probabilities.length) {
            throw new RandomGenException(String.format("Amount of numbers provided should match the number of probabilities provided. " +
                    "Numbers provided: %d, Probabilities provided: %d", randomNums.length, probabilities.length));
        }
        if (randomNums.length == 0) {
            throw new RandomGenException("No random numbers provided");
        }
    }

    private static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
}