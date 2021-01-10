package UI.Enum;

public enum SubMenuRegiRequest {
    AddRower, RemoveRower, AddBoatType, RemoveBoatType, back;

    public static SubMenuRegiRequest convertFromInt(int number){
        switch(number){
            case 1: { return AddRower; }
            case 2: { return RemoveRower; }
            case 3: { return AddBoatType; }
            case 4: { return RemoveBoatType; }
            case 5: { return back; }
            default:{ return null; }
        }
    }
}
