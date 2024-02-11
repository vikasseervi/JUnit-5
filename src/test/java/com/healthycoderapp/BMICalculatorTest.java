package com.healthycoderapp;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class BMICalculatorTest {

//    private String environment = "development_environment";
    private String environment = "production_environment";

    // must be static as they run only once and to avoid recomputing expensive operations.. like database connection;
    @BeforeAll
    static void beforeAll(){
        System.out.println("@BeforeAll: DataBase connection");
    }

    @AfterAll
    static void afterAll(){
        System.out.println("@AfterAll: Terminating Database connection");
    }

    @Nested
    class isDietRecommendedTests {
        //    @Test
        //    void should_ReturnTrue_When_DietRecommended(){
        //        // given
        //        double weight = 89.0;
        //        double height = 1.72;
        //
        //        // when
        //        boolean recommended = BMICalculator.isDietRecommended(weight, height);
        //
        //        // then
        //        assertTrue(recommended);
        //    }

        @ParameterizedTest(name = "weight={0}, height={1}") // 0, 1 are indices i.e., {89, 1.72}
        //    @ValueSource(doubles = {70, 89, 95, 100}) // for one value
        //    @CsvSource(value = {"89, 1.72", "110, 1.78", "95, 1.75"}) // for multiple values; CSV => comma separated values
        @CsvFileSource(resources = "/diet-recommended-input-data.csv", numLinesToSkip = 1) // skips the 1st line which denotes the name like weight, height
        void should_ReturnTrue_When_DietRecommended(double coderWeight, double coderHeight){
            // given
            double weight = coderWeight;
            double height = coderHeight;

            // when
            boolean recommended = BMICalculator.isDietRecommended(weight, height);

            // then
            assertTrue(recommended);
        }

        @Test
        void should_ReturnFalse_When_DietRecommended(){
            // given
            double weight = 50.0;
            double height = 1.82;

            // when
            boolean recommended = BMICalculator.isDietRecommended(weight, height);

            // then
            assertFalse(recommended);
        }

        @Test
        void should_ThrowArithmeticException_When_HeightZero(){
            // given
            double weight = 50.0;
            double height = 0.0;

            // when
            Executable executable = () -> BMICalculator.isDietRecommended(weight, height);

            // then
            assertThrows(ArithmeticException.class, executable);
        }
    }

    @Nested
    @DisplayName(">>>FindCoderWithWorstBMITests")
    class FindCoderWithWorstBMITests{
        @Test
        @DisplayName(">>>ReturnCoderWithWorstBMI_When_CoderListNotEmpty") // skips the test in the particular OS;
        @DisabledOnOs(OS.WINDOWS)
        void should_ReturnCoderWithWorstBMI_When_CoderListNotEmpty(){
            // given
            List<Coder> coders = new ArrayList<>();
            coders.add(new Coder(1.80, 60.0));
            coders.add(new Coder(1.82, 98.0));
            coders.add(new Coder(1.82, 67.0));

            // when
            Coder coderWorstBMI = BMICalculator.findCoderWithWorstBMI(coders);

            // then
            // assertEquals(1.82,coderWorstBMI.getHeight()); //if fails, will stop here
            // assertEquals(98,coderWorstBMI.getWeight());
            assertAll(
                    () -> assertEquals(1.82, coderWorstBMI.getHeight()),
                    () -> assertEquals(98, coderWorstBMI.getWeight())
            );
        }

        @Test
        void should_ReturnNullWorstBMICoder_When_CoderListIsEmpty(){
            // given
            List<Coder> coders = new ArrayList<>();

            // when
            Coder coderWorstBMI = BMICalculator.findCoderWithWorstBMI(coders);

            // then
            assertNull(coderWorstBMI);
        }

        @Test
        void should_ReturnCoderWithWorstBMIIn1ms_When_CoderListHas10000Coders(){
            // given
            assumeTrue(BMICalculatorTest.this.environment.equals("production_environment")); // skips if assumption fails;
            List<Coder> coders = new ArrayList<>();
            for (int i = 0; i < 10000; i++) {
                coders.add(new Coder(1+i, 10+i));
            }

            // when
            Executable executable = () -> BMICalculator.findCoderWithWorstBMI(coders);

            // then
            assertTimeout(Duration.ofMillis(500), executable);

        }


    }

    @Nested
    @Disabled // skips the tests
    class GetBMIScoresTests{
        @Test
        void should_ReturnCorrectBMScoreArray_When_CoderListNotEmpty(){
            // given
            List<Coder> coders = new ArrayList<>();
            coders.add(new Coder(1.80, 60.0));
            coders.add(new Coder(1.82, 98.0));
            coders.add(new Coder(1.82, 64.7));
            double[] expected = {18.52, 29.59, 19.53};

            // when
            double[] bmiScores = BMICalculator.getBMIScores(coders);

            // then
            assertArrayEquals(expected, bmiScores);
        }
    }
}