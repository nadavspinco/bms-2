package UI.Enum;

public enum SubMenuManageBoatListEnum {
    addBoat, removeBoat, updateBoat, showAllBoats, back;

    public static SubMenuManageBoatListEnum convertFromInt(int number){
        switch(number){
            case 1: { return addBoat; }
            case 2: { return removeBoat; }
            case 3: { return updateBoat; }
            case 4: { return showAllBoats; }
            case 5: { return back; }
            default:{ return null; }
        }
    }
}
