package org.main;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.main.exception.RandomGenException;

class RandomGenTest {

    static final long seed = 100L;

    @Test
    void testNextNumReturnsANumberFromTheNumberArray() {
        int[] inputArray = {0,1,2,3,4,5,6,7,8,9};
        float[] associatedProbabilities = {0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f};

        RandomGen randomGen = new RandomGen(inputArray, associatedProbabilities, seed);

        int result = randomGen.nextNum();

        Assertions.assertTrue(ArrayUtils.contains(inputArray, result));
    }

    @Test
    void shouldThrowRandomGenExceptionIfParametersProvidedDoNotMatchInLength() {
        RandomGenException thrown = Assertions.assertThrows(RandomGenException.class, () -> {
            int[] inputArray = {0,1,2,3,4,5,6,7,8,9,10};
            float[] associatedProbabilities = {0.1f};

            new RandomGen(inputArray, associatedProbabilities, seed);
        });

        Assertions.assertEquals("Amount of numbers provided should match the number of probabilities provided. Numbers provided: 11, Probabilities provided: 1", thrown.getMessage());
    }

    @Test
    void shouldThrowRandomGenExceptionIfParametersProvidedAreNull() {
        RandomGenException thrown = Assertions.assertThrows(RandomGenException.class, () -> {
            new RandomGen(null, null, seed);
        });

        Assertions.assertEquals("Data provided cannot be null", thrown.getMessage());
    }

    @Test
    void shouldThrowRandomGenExceptionIfNumberArrayProvidedIsEmpty() {
        RandomGenException thrown = Assertions.assertThrows(RandomGenException.class, () -> {
            int[] inputArray = {};
            float[] associatedProbabilities = {};

            new RandomGen(inputArray, associatedProbabilities, seed);
        });

        Assertions.assertEquals("No random numbers provided", thrown.getMessage());
    }

    @Test
    void shouldThrowRandomGenExceptionIfTotalProbabilityIsOverOne() {
        RandomGenException thrown = Assertions.assertThrows(RandomGenException.class, () -> {
            int[] inputArray = {1,2,3};
            float[] associatedProbabilities = {0.5f, 0.5f, 0.5f};

            new RandomGen(inputArray, associatedProbabilities, seed);
        });

        Assertions.assertEquals("Total probabilities provided total to greater than 1.0 and will not provide a good indication of probability across the dataset", thrown.getMessage());
    }

    @Test
    void shouldReturnTheSameResultsBasedOnAmountOfIterationsAndSeedUsed() {
        int[] inputArray = {0,1,2,3,4,5,6,7,8,9};
        float[] associatedProbabilities = {0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f};

        RandomGen randomGen = new RandomGen(inputArray, associatedProbabilities, seed);

        int[] countsTenThousandIterations = new int[inputArray.length];

        for (int i = 0; i < 10000; i++) {
            int num = randomGen.nextNum();
            for (int j = 0; j < inputArray.length; j++) {
                if (num == inputArray[j]) {
                    countsTenThousandIterations[j]++;
                    break;
                }
            }
        }

        Assertions.assertEquals(1019, countsTenThousandIterations[0]);
    }

    @Test
    void shouldReturnTheLastNumberInTheArrayProvidedShouldTheProbabilityNotAddUpToOne() {
        int[] inputArray = {0,1};
        float[] associatedProbabilities = {0.5f, 0.45f};

        RandomGen randomGen = new RandomGen(inputArray, associatedProbabilities, seed);

        boolean randGreaterThan = randomGen.randValueGreaterThanCumulativeProbability(0.99f);
        int result = randomGen.nextNum();

        Assertions.assertTrue(randGreaterThan);
        Assertions.assertEquals(inputArray[1], result);
    }


}