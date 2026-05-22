package com.example.myapplicationproject;

import java.util.Random;

public class QuestionEngine {
    public enum Type {TEXT_INPUT, YES_NO}

    public Type currentType;
    public String questionText;
    public String correctTextAnswer;
    public boolean correctBooleanAnswer;
    public int currentRadix = 10;

    private final Random random = new Random();

    public void generate(int level) {
        if (level == 1) {
            currentRadix = 10;
        } else if (level == 2) {
            currentRadix = 2;
        } else if (level == 3) {
            currentRadix = 8;
        } else if (level == 4) {
            currentRadix = 16;
        } else {
            int[] allRadixes = {2, 8, 10, 16};
            currentRadix = allRadixes[random.nextInt(allRadixes.length)];
        }


        currentType = random.nextBoolean() ? Type.TEXT_INPUT : Type.YES_NO;

        int maxBound = 10 + (level * 5);
        int num1 = random.nextInt(maxBound) + 1;
        int num2 = random.nextInt(maxBound) + 1;

        if (num1 < num2) {
            int temp = num1;
            num1 = num2;
            num2 = temp;
        }

        boolean isAddition = random.nextBoolean();
        int mathResult = isAddition ? (num1 + num2) : (num1 - num2);
        String op = isAddition ? "+" : "-";

        String strNum1 = Integer.toString(num1, currentRadix).toUpperCase();
        String strNum2 = Integer.toString(num2, currentRadix).toUpperCase();

        if (currentType == Type.TEXT_INPUT) {
            questionText = strNum1 + " " + op + " " + strNum2 + " = ?\n(СС: " + currentRadix + ")";
            correctTextAnswer = Integer.toString(mathResult, currentRadix).toUpperCase();

        } else {
            boolean generateCorrectComparison = random.nextBoolean();
            int offset = (random.nextBoolean() ? 1 : 2) * (random.nextBoolean() ? 1 : -1);
            int comparisonVal = generateCorrectComparison ? mathResult : mathResult + offset;
            if (comparisonVal < 0) comparisonVal = 0;

            boolean isLessOp = random.nextBoolean();
            String compSign = isLessOp ? "<" : ">";

            String strCompVal = Integer.toString(comparisonVal, currentRadix).toUpperCase();

            questionText = strNum1 + " " + op + " " + strNum2 + " " + compSign + " " + strCompVal + "?\n(СС: " + currentRadix + ")";

            if (isLessOp) {
                correctBooleanAnswer = (mathResult < comparisonVal);
            } else {
                correctBooleanAnswer = (mathResult > comparisonVal);
            }
        }
    }
}