package kr.co.ainus.peticaexcutor;

import kr.co.ainus.peticaexcutor.type.CommandType;
import kr.co.ainus.peticaexcutor.type.DataType;
import kr.co.ainus.peticaexcutor.type.ModeType;

public class Protocol22Byte extends Protocol{
    private byte count;
    private byte hour2;
    private byte min2;
    private byte timer2;
    private byte hour3;
    private byte min3;
    private byte timer3;
    private byte hour4;
    private byte min4;
    private byte timer4;
    private byte hour5;
    private byte min5;
    private byte timer5;

    public Protocol22Byte(CommandType commandType, ModeType modeType, byte count, byte hour, byte min, byte timer, byte hour2, byte min2, byte timer2, byte hour3, byte min3, byte timer3, byte hour4, byte min4, byte timer4, byte hour5, byte min5, byte timer5) {
        super(commandType, modeType, DataType.ON, hour, min, timer);
        this.count = count;
        this.hour2 = hour2;
        this.min2 = min2;
        this.timer2 = timer2;
        this.hour3 = hour3;
        this.min3 = min3;
        this.timer3 = timer3;
        this.hour4 = hour4;
        this.min4 = min4;
        this.timer4 = timer4;
        this.hour5 = hour5;
        this.min5 = min5;
        this.timer5 = timer5;
    }

    public byte getCount() {
        return count;
    }

    public void setCount(byte count) {
        this.count = count;
    }

    public byte getHour2() {
        return hour2;
    }

    public void setHour2(byte hour2) {
        this.hour2 = hour2;
    }

    public byte getMin2() {
        return min2;
    }

    public void setMin2(byte min2) {
        this.min2 = min2;
    }

    public byte getTimer2() {
        return timer2;
    }

    public void setTimer2(byte timer2) {
        this.timer2 = timer2;
    }

    public byte getHour3() {
        return hour3;
    }

    public void setHour3(byte hour3) {
        this.hour3 = hour3;
    }

    public byte getMin3() {
        return min3;
    }

    public void setMin3(byte min3) {
        this.min3 = min3;
    }

    public byte getTimer3() {
        return timer3;
    }

    public void setTimer3(byte timer3) {
        this.timer3 = timer3;
    }

    public byte getHour4() {
        return hour4;
    }

    public void setHour4(byte hour4) {
        this.hour4 = hour4;
    }

    public byte getMin4() {
        return min4;
    }

    public void setMin4(byte min4) {
        this.min4 = min4;
    }

    public byte getTimer4() {
        return timer4;
    }

    public void setTimer4(byte timer4) {
        this.timer4 = timer4;
    }

    public byte getHour5() {
        return hour5;
    }

    public void setHour5(byte hour5) {
        this.hour5 = hour5;
    }

    public byte getMin5() {
        return min5;
    }

    public void setMin5(byte min5) {
        this.min5 = min5;
    }

    public byte getTimer5() {
        return timer5;
    }

    public void setTimer5(byte timer5) {
        this.timer5 = timer5;
    }

    @Override
    public byte[] getValue() {
        return new byte[] {
                getCommandType().getValue()
                , getModeType().getValue()
                , getCount()
                , getHour1()
                , getMin1()
                , getTimer1()
                , getHour2()
                , getMin2()
                , getTimer2()
                , getHour3()
                , getMin3()
                , getTimer3()
                , getHour4()
                , getMin4()
                , getTimer4()
                , getHour5()
                , getMin5()
                , getTimer5()
        };
    }
}
