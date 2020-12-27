package Logic.Enum;

import javax.xml.bind.annotation.XmlRootElement;


public enum ActivityTypeEnum{
    Sailing,
    Training;

    public static ActivityTypeEnum convertFromInt(int selection){
        switch (selection){
            case 1 : return  Sailing;
            case 2: return  Training;
            default: return null;
        }
    }

    public static String convertToString(ActivityTypeEnum activity){
        if(activity == Sailing)
            return "Rowing";
        else
            return "Group practice";
    }

    public static ActivityTypeEnum convertFromString(String activity){
        if(activity == "Rowing")
            return ActivityTypeEnum.Sailing;
        else    // activity == "Group practice"
            return ActivityTypeEnum.Training;
    }

}
