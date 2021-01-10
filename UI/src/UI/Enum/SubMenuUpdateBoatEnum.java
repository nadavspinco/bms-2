package UI.Enum;

public enum SubMenuUpdateBoatEnum {
    updateName, updateIsWide, updateIsCoastal, fixBoat, disableBoat, back;

    public static SubMenuUpdateBoatEnum convertFromInt(int number){
        switch(number){
            case 1: { return updateName; }
            case 2: { return updateIsWide; }
            case 3: { return updateIsCoastal; }
            case 4: { return fixBoat; }
            case 5: { return disableBoat; }
            case 6: { return back; }
            default:{ return null; }
        }
    }
}

