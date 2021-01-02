package UI.Tools;

import Logic.Enum.ActivityTypeEnum;
import Logic.Enum.BoatTypeEnum;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Validator{
    public static Scanner input = new Scanner(System.in);

    public static int getIntBetween(int min, int max, String iMessage) {
        int userInput = 0;
        boolean validity = false;
        do {
            userInput = getValidInt(iMessage);
            if (userInput >= min && userInput <= max)
                validity = true;
            else
                System.out.println("Value should be between " + min + "-" + max + "." + "\n");
        } while (validity == false);

        return userInput;
    }
    public static DayOfWeek getDay() {
        int dayInput = Validator.getIntBetween(1, 7, Messager.getAllDaysMessage());
        return DayOfWeek.of(dayInput);
    }

    public static ActivityTypeEnum getActivityType() {
        int boutTypeInt = Validator.getIntBetween(1, 2, Messager.getActivityTypeMenu());
        return ActivityTypeEnum.convertFromInt(boutTypeInt);
    }

    public static BoatTypeEnum getBoatType() {
        int boutTypeInt = Validator.getIntBetween(1, 11, Messager.chooseBoatType());//8 == null
        return BoatTypeEnum.convertFromInt(boutTypeInt);
    }

    public static LocalTime getTime(String msg) {
        int hour = Validator.getIntBetween(0, 23, String.format("please enter %s time hours\n", msg));
        int minutes = Validator.getIntBetween(0, 59, String.format("please enter %s time minutes\n", msg));
        return LocalTime.of(hour, minutes);
    }

    public static int getValidInt(String iMessage) {
        int userInput = 0;
        boolean validity = false;

        do {
            System.out.print(iMessage);
            try {
                userInput = input.nextInt();
                validity = true;
            } catch (InputMismatchException exception) {
                System.out.println("This is not number, try again!");
                validity = false;
                input.nextLine();
            }
        } while (validity == false);

        return userInput;
    }
    public static void cleanBuffer()
    {
        input.next();
    }

    public static String getValidString(String iMessage) {
        String userInput = null;
        boolean validity = false;
        do {
            System.out.print(iMessage);
            userInput = input.next();
            if (validString(userInput))
                validity = true;
            else {
                System.out.println("This is not valid string, try again!\n");
                validity = false;
                input.nextLine();
            }
        } while (validity == false);

        return userInput;
    }

    public static boolean validString(String str) {
        String newStr = str.toLowerCase();
        char[] charArray = newStr.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char ch = charArray[i];
            if (!(ch >= 'a' && ch <= 'z')) {
                return false;
            }
        }

        return true;
    }

    public static String getValidDigitsInput(String iMessage) {
        String userInput = null;
        boolean validity = false;
        input.reset();

        do {
            System.out.print(iMessage);
            userInput = input.next();
            if (onlyDigits(userInput))
                validity = true;
            else {
                System.out.println("This is not valid input, digits only, try again!\n");
                validity = false;
                input.nextLine();
            }
        } while (validity == false);

        return userInput;
    }

    public static String getValidDigitsLettersInput(String iMessage){
        String userInput = null;
        boolean validity = false;

        do {
            System.out.print(iMessage);
            userInput = input.next();
            if (onlyDigitsAndLetters(userInput))
                validity = true;
            else {
                System.out.println("This is not valid input, digits and letters only, try again!\n");
                validity = false;
                input.nextLine();
            }
        } while (validity == false);

        return userInput;
    }

    public static boolean onlyDigits(String str) {
        String newStr = str.toLowerCase();
        boolean validity = true;
        for (int i = 0; i < newStr.length(); i++) {
            if (newStr.charAt(i) < '0' || newStr.charAt(i) > '9')
                return validity = false;
        }
        return validity;
    }

    public static boolean onlyDigitsAndLetters(String str){
        boolean validity = true;
        String newStr = str.toLowerCase();
        for (int i = 0; i < str.length(); i++) {
            if ((newStr.charAt(i) >= '0' && newStr.charAt(i) <= '9') ||(newStr.charAt(i) >= 'a' && newStr.charAt(i) <= 'z')) {
                return true;
            }
            else {
                return false;
            }
        }
        return validity;
    }

    public static boolean isValidEmailAddress(String email) {
        String patternString;
        patternString = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(patternString);
        java.util.regex.Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean trueOrFalseAnswer(String msg){
        int res = getIntBetween(1,2,msg + "\n1. Yes. 2. No.\n");
        return res == 1;
    }

    public static boolean checkLegalFileName(String fileName) throws Exception {
        char fileNameArr[] = fileName.toCharArray();

        boolean isLegalFileName = fileName.length() >= 5 &&
                fileNameArr[fileName.length() - 1] == 'l' &&
                fileNameArr[fileName.length() - 2] == 'm' &&
                fileNameArr[fileName.length() - 3] == 'x' &&
                fileNameArr[fileName.length() - 4] == '.';
        if (!isLegalFileName)
            throw new Exception("Not a legal xml file name.");

        return isLegalFileName;
    }
}
