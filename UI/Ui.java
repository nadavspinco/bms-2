package UI;

import Logic.*;
import Logic.Objects.Member;
import UI.Menus.ManagerMenu;
import UI.Menus.MemberMenu;
import UI.Tools.Messager;
import UI.Tools.Validator;

import java.util.Scanner;


public class Ui
{
    private Member memberLoggedIn;
    private static Scanner scanner = new Scanner(System.in);
    private  SystemManagement systemManagement;
    private  XmlManagement xmlManagement;
    public Ui(){
        systemManagement = new SystemManagement();
        xmlManagement =new XmlManagement(systemManagement);
        try{
            systemManagement = xmlManagement.importSystemManagementDetails();
            xmlManagement = new XmlManagement(systemManagement);
        }
        catch (Exception exception){

        }
    }
    public void run() {
        runUiMainLoop();
    }

    private void runUiMainLoop() {
        int answer;
        boolean logout = false; // toExit = false,
        System.out.println(Messager.getWelcomeMessage());
        do {

            memberLoggedIn = signIn();
            if (memberLoggedIn == null) {
                System.out.println("One of the password or email is incorrect, try again.");
                continue;
            }
            else if(memberLoggedIn.getIsManager() == true){
                answer = Validator.getIntBetween(1,2,"As what type of member you want to log in?\n" + "1.Manger.\n2.Member.\n");
                if(answer == 1)
                    signInAsManager(memberLoggedIn);
                else
                    signInAsMember(memberLoggedIn);
            }
            else
                signInAsMember(memberLoggedIn);

            answer = Validator.getIntBetween(1,2,"Do you want out of the system?\n"+"1.Yes \n2.No");
            logout = (answer == 1);
        }while(!logout);
    }

    private void signInAsManager(Member member)
    {
        if(member!= null) {
            ManagerMenu managerMenu = new ManagerMenu(systemManagement,member);
            managerMenu.managerMenuRun();
        }
        else
            System.out.println("sign In fail");
    }


    private void signInAsMember(Member member) {
        if (member!= null) {
            MemberMenu memberMenu = new MemberMenu(systemManagement,member);
            memberMenu.memberMenuRun();
        }
        else
            System.out.println("sign in fail");
    }

    private Member signIn()
    {
        String userEmail = getEmailFromUser();
        String userPassWord = getUserPassword();
        return systemManagement.loginMember(userEmail,userPassWord);
    }


    private String getUserPassword() {
        boolean isGettingPasswordProcessIsOn = true;
        String password;
        do {
            System.out.println("please enter password with 3 characters at least");
            password = scanner.nextLine();
            if (password.length() >= 3)
            {
                isGettingPasswordProcessIsOn = false;
            }
            else
            {
                System.out.println("invalid password");
            }

        }while (isGettingPasswordProcessIsOn);
        return password;
    }


    public static String getEmailFromUser()
    {
        boolean isGettingProcessIsOn = true;
        String userEmail;
        do {
            System.out.println(Messager.getEmailRequestString());
            userEmail = scanner.nextLine();
            if(!Validator.isValidEmailAddress(userEmail))            {
                System.out.println(Messager.getInvalidEmailString());
            }
            else {
                isGettingProcessIsOn = false;
            }
        }while (isGettingProcessIsOn);
        return userEmail;
    }
}
