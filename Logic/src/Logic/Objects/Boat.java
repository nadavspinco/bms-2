package Logic.Objects;

import Logic.Enum.BoatTypeEnum;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Objects;

@XmlRootElement
public class Boat implements Serializable {
    private String serialBoatNumber;
    private String boatName;
    private BoatTypeEnum boatType;
    private Member ownerMember;
    private boolean isWide;
    private boolean hasCoxswain;  // for all type of boats expect from single boat & octet boat
    private boolean isCoastalBoat;
    private boolean isAvailable;
    private boolean isPrivate;
    private int numberOfRowersAllowed;

    public Boat(){ }

    public Boat(String inputBoatName, BoatTypeEnum inputBoatType, boolean inputIsCoastalBoat,
                boolean inputIsWide, String inputSerial){
        this.boatName = inputBoatName;
        this.isWide = inputIsWide;
        this.serialBoatNumber = inputSerial;
        this.hasCoxswain = checkIfHasCoxswain(inputBoatType);
        this.isCoastalBoat = inputIsCoastalBoat;
        this.boatType = inputBoatType;
        this.isAvailable = true;
        this.ownerMember = null;
        this.isPrivate = false;
        this.numberOfRowersAllowed = BoatTypeEnum.returnNumOfRowers(boatType);
    }

    public Boat(String serial, String name, BoatTypeEnum boatType){
        this.boatName = name;
        this.serialBoatNumber = serial;
        this.boatType = boatType;
        this.numberOfRowersAllowed = BoatTypeEnum.returnNumOfRowers(boatType);
        this.hasCoxswain = checkIfHasCoxswain(boatType);
        this.isAvailable = true;
        this.ownerMember = null;
        this.isPrivate = false;
        this.isCoastalBoat = false;
        this.isWide = false;
    }

    @XmlAttribute
    public void setNumberOfRowersAllowed(int numberOfRowersAllowed) {
        this.numberOfRowersAllowed = numberOfRowersAllowed;
    }

    public int getNumberOfRowersAllowed(){return numberOfRowersAllowed;}

    public String getSerialBoatNumber() {
        return serialBoatNumber;
    }

    @XmlAttribute
    public void setSerialBoatNumber(String serialBoatNumber) {
        this.serialBoatNumber = serialBoatNumber;
    }

    public String getBoatName() {
        return boatName;
    }

    @XmlAttribute
    public void setBoatName(String boatName) {
        this.boatName = boatName;
    }

    public BoatTypeEnum getBoatType() {
        return boatType;
    }

    @XmlElement
    public void setBoatType(BoatTypeEnum boatType) {
        this.boatType = boatType;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public Member getOwnerMember() {
        return ownerMember;
    }

    @XmlElement
    public void setOwnerMember(Member ownerMember) {
        this.ownerMember = ownerMember;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    @XmlAttribute
    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @XmlAttribute
    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public boolean isWide() {
        return isWide;
    }

    @XmlAttribute
    public void setWide(boolean wide) {
        isWide = wide;
    }

    public boolean isHasCoxswain() {
        return hasCoxswain;
    }

    @XmlAttribute
    public void setHasCoxswain(boolean hasCoxswain) {
        this.hasCoxswain = hasCoxswain;
    }

    public boolean isCoastalBoat() {
        return isCoastalBoat;
    }

    @XmlAttribute
    public void setCoastalBoat(boolean costalBoat) {
        isCoastalBoat = costalBoat;
    }


    private boolean checkIfHasCoxswain(BoatTypeEnum boatType){
        if (boatType == BoatTypeEnum.DoubleCoxed || boatType == BoatTypeEnum.DoublePaddleCoxed || boatType == BoatTypeEnum.QuartetBoatCoxed ||
                boatType == BoatTypeEnum.QuartetBoatPaddleCoxed || boatType == BoatTypeEnum.OctetBoatCoxed || boatType == BoatTypeEnum.OctetBoatCoxedPaddle)
            return true;
        else
            return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Boat boat = (Boat) o;
        return getSerialBoatNumber().equals(boat.getSerialBoatNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSerialBoatNumber());
    }
}
