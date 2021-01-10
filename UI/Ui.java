package UI;

import Logic.Objects.Member;
import UI.Menus.ManagerMenu;
import UI.Menus.MemberMenu;
import UI.Tools.Messager;
import UI.Tools.Validator;

import java.io.IOException;
import java.util.Scanner;

public class Ui
{
    private Member memberLoggedIn;
    private static Scanner scanner = new Scanner(System.in);
    private  EngineProxy engineProxy;

    public Ui(String[] args) throws IOException {
        EngineProxy flagEngine = checkValidPortHost(args);
        if (flagEngine != null)
            engineProxy = flagEngine;
        else
            try {
                engineProxy = new EngineProxy("localhost",1989);
            }
           catch (IOException e){
               throw e;
           }
    }

    public void run() {
        try {
            runUiMainLoop();
        }catch (ConnectionLostException e){
            System.out.println("connection is lost\n goodbye");
        }
    }

    private void runUiMainLoop() {
        int answer;

        boolean logout = false; // toExit = false,
        System.out.println(Messager.getWelcomeMessage());
        do {
            memberLoggedIn = signIn();
            if (memberLoggedIn == null) {
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
            ManagerMenu managerMenu = new ManagerMenu(engineProxy,member);
            managerMenu.managerMenuRun();
        }
        else
            System.out.println("sign In fail");
    }


    private void signInAsMember(Member member) {
        if (member!= null) {
            MemberMenu memberMenu = new MemberMenu(engineProxy,member);
            memberMenu.memberMenuRun();
        }
        else
            System.out.println("sign in fail");
    }

    private Member signIn(){
        Member memberToReturn = null;
        String userEmail = getEmailFromUser();
        String userPassWord = getUserPassword();
        if(engineProxy.isMemberAlreadyLoggedIn(userEmail)){
            System.out.println("your already singed in!");
            return null;
        }
        else {
            memberToReturn = engineProxy.loginMember(userEmail,userPassWord);
            if(memberToReturn == null){
                System.out.println("One of the password or email is incorrect, try again.");
            }
        }
        return memberToReturn;
    }


    private String getUserPassword() {
        boolean isGettingPasswordProcessIsOn = true;
        String password;
        do {
            System.out.println("please enter password with 3 characters at least");
            password = scanner.nextLine();
            if (password.length() >= 3){
                isGettingPasswordProcessIsOn = false;
            }
            else{
                System.out.println("invalid password");
            }

        }while (isGettingPasswordProcessIsOn);
        return password;
    }


    public static String getEmailFromUser(){
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

    public EngineProxy checkValidPortHost(String[] args){
        String[] newArgs;
        String host = null;
        int port = 0;

        if (args == null || args.length == 0)
            return null;
        if (args.length != 2)
            return null;

        for (int i = 0; i < args.length; i++){
            if (args[i].contains("--port")) {
                newArgs = args[i].split("=");
                if (newArgs.length != 2)
                    return null;
                try {
                    port = Integer.parseInt(newArgs[1]);
                } catch (NumberFormatException e) {
                    e.getStackTrace();
                    return null;
                }
            }
            else if(args[i].contains("--host")) {
                newArgs = args[i].split("=");
                if (newArgs.length != 2)
                    return null;
                host = newArgs[1];
            }
        }
        try {
            return new EngineProxy(host,port);
        } catch (IOException e) {

        }
        return null;
    }
}
