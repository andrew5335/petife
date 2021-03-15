package kr.co.ainus.petife2.model;

public class PeticaStatus {
    private boolean hasFeederRun;
    private boolean hasWaterRun;
    private boolean hasLampOn;
    private boolean hasClcokSet;
    private boolean hasFeederFull;
    private boolean hasWaterFull;
    private boolean hasKeyLock;
    private boolean hasPowerOn;

    public PeticaStatus(boolean hasFeederRun, boolean hasWaterRun, boolean hasLampOn, boolean hasClcokSet, boolean hasFeederFull, boolean hasWaterFull, boolean hasKeyLock, boolean hasPowerOn) {
        this.hasFeederRun = hasFeederRun;
        this.hasWaterRun = hasWaterRun;
        this.hasLampOn = hasLampOn;
        this.hasClcokSet = hasClcokSet;
        this.hasFeederFull = hasFeederFull;
        this.hasWaterFull = hasWaterFull;
        this.hasKeyLock = hasKeyLock;
        this.hasPowerOn = hasPowerOn;
    }

    public boolean isHasFeederRun() {
        return hasFeederRun;
    }

    public boolean isHasWaterRun() {
        return hasWaterRun;
    }

    public boolean isHasLampOn() {
        return hasLampOn;
    }

    public boolean isHasClcokSet() {
        return hasClcokSet;
    }

    public boolean isHasFeederFull() {
        return hasFeederFull;
    }

    public boolean isHasWaterFull() {
        return hasWaterFull;
    }

    public boolean isHasKeyLock() {
        return hasKeyLock;
    }

    public boolean isHasPowerOn() {
        return hasPowerOn;
    }
}
