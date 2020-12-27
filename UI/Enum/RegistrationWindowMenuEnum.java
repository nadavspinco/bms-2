package UI.Enum;

public enum RegistrationWindowMenuEnum {
    addRegistrationWindow, editRegistrationWindow,deleteRegistrationWindow, showAllRegistrationWindow, back;

    public static RegistrationWindowMenuEnum ConvertFromInt(int selection){
        switch (selection){
            case 1:return addRegistrationWindow;
            case 2: return editRegistrationWindow;
            case 3: return deleteRegistrationWindow;
            case 4: return showAllRegistrationWindow;
            case 5: return back;
            default: return null;
        }
    }
}
