package UI.Tools;

import java.time.DayOfWeek;

public class Messager {
    public static String getAllDaysMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        int index = 1;
        for (DayOfWeek day : DayOfWeek.values()) {
            stringBuilder.append(String.format("%d. %s\n", index, day.toString()));
            index++;
        }
        return stringBuilder.toString();
    }

    public static String getWelcomeMessage() {
        return "Welcome to Nadav & Eitan Boathouse";
    }

    public static String chooseBoatType() {
        return String.format("Choose boat's Type according to the number near to:\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n", "" +
                        "1. SingleBoat",
                "2. DoubleBoat", "3. DoubleBoatPaddle.", "4. DoubleCoxed.", "5. DoublePaddleCoxed.",
                "6. QuartetBoat.", "7. QuartetBoatPaddle.", "8. QuartetBoatCoxed.", "9. QuartetBoatPaddleCoxed.",
                "10. OctetBoatCoxed.", "11. OctetBoatCoxedPaddle.");
    }

    public static String ChooseMemberLevelMessage() {
        return String.format("Choose your rowing level according to the number near to:\n %s\n %s\n %s\n", "" +
                "1. Beginner.", "2. Professional.", "3. World Class");
    }

    public static String getEmailRequestString() {
        return "please Enter your Email address";
    }

    public static String getNameRequestMessage() {
        return "please enter your name";
    }

    public static String getInvalidEmailString() {
        return "invalid Email! \n";
    }

    public static String chooseSubMenuManageBoatListMessage() {
        return String.format("Choose the one of the options according to the number near to:\n%s\n%s\n%s\n%s\n%s\n", "" +
                "1. Add new Boat.", "2. Remove boat.", "3. Update boat", "4. Show all boats.", "5. Back.");
    }

    public static String chooseSubMenuManageMemberListMessage() {
        return String.format("Choose the one of the options according to the number near to:\n %s\n%s\n%s\n%s\n%s\n", "" +
                "1. Add new Member.", "2. Remove Member.", "3. Edit Member.", "4. Show all members.", "5. Back.");
    }

    public static String managerMenuPrintMessage() {
        return String.format("Choose one of the following options:\n" +
                "1. Manage boat list.\n" +
                "2. Manage members list.\n" +
                "3. Manage registration window.\n" +
                "4. Manage all registrations request.\n" +
                "5. Show all registration request which need to assign.\n" +
                "6. Show all registration that confirmed.\n" +
                "7. Assignment Menu.\n" +
                "8. XML Menu.\n" +
                "9. Exit.\n");
    }

    public static String memberMenuPrintMessage() {
        return String.format("Choose one of the following options:\n" +
                "1. Change name member.\n" +
                "2. Change phone number.\n" +
                "3. Change email.\n" +
                "4. Change password.\n" +
                "5. Show all my future confirmed-registration.\n" +
                "6. Show history of confirmed-registration requests 7 days ago.\n" +
                "7. Manage register request.\n" +
                "8. Exit.\n");
    }

    public static String memberFieldToUpdate() {
        return String.format("Choose the one of the options according to the number near to:\n %s\n %s\n %s\n %s\n %s\n %s\n %s\n", "" +
                        "1. Edit level.", "2. Edit age.", "3. Extend end date.", "4. Cancel private boat.",
                "5. Edit Phone Number.", "6. Add private boat.", "7. Back.");
    }
    public static String getMangeRegistrationMenu() {
        return String.format("%s\n%s\n%s\n%s\n%s\n",
                "1. Add new Registration ",
                "2. Edit Registration ",
                "3. Delete Registration ",
                "4. Show all Registration",
                "5. Exit");
    }
    public static String getMangeRegistrationWindowMenu() {
        return String.format("%s\n%s\n%s\n%s\n%s\n",
                "1. Add new Registration Window ",
                "2. Edit Registration Window ",
                "3. Delete Registration Window ",
                "4. Show all Registration Windows ",
                "5. Exit");
    }

    public static String getActivityTypeMenu() {
        return String.format("%s\n%s\n",
                "1. Sailing", "2. Training");
    }

    public static String boatFieldToUpdate() {
        return String.format("Choose the one of the options according to the number near to:\n %s\n %s\n %s\n %s\n %s\n %s\n", "" +
                        "1. Change boat's name.", "2. Change is wide boat.", "3. Change is coastal boat.",
                "4. Fix boat.", "5. Disable a boat.", "6. back.");
    }

    public static String subMenuRegiRequestMessge() {
        return String.format("Choose one of the options according to the number near to: \n%s\n%s\n%s\n%s\n%s",
                "1. Add rower.",
                "2. Remove rower.",
                "3. Add new boat type.",
                "4. Remove boat type.",
                "5. Exit");
    }

    public static String chooseImportExportMessage() {
        return String.format("What action would you like to do?" +
                "\n1. Import xml data." +
                "\n2. Export xml data." +
                "\n3. Back.\n");
    }

    public static String importXmlMenu() {
        return String.format("What kind of detail do you want to import: \n%s\n%s\n%s\n%s\n",
                "1. Rowers.",
                "2. Boats.",
                "3. Window Registration.",
                "4. Back");
    }

    public static String exportXmlMenu() {
        return String.format("What kind of detail do you want to export: \n%s\n%s\n%s\n%s\n",
                "1. Rowers.",
                "2. Boats.",
                "3. Window Registration.",
                "4. Back");
    }

    public static String manageReqiRequestByManager() {
        return String.format("What kind of action do you want to do: \n%s\n%s\n%s\n%s\n",
                "1. Remove rower from the registration request.",
                "2. Remove the registration request.",
                "3. Add boat type to registration request.",
                "4. Back");
    }
}