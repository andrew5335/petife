package kr.co.ainus.petica_api.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import kr.co.ainus.petica_api.model.domain.Petica;

public class DeviceResponse extends ApiResponse {
    @Expose
    @SerializedName("items")
    private List<Petica> items;
    @Expose
    @SerializedName("itemCount")
    private int itemcount;
    @Expose
    @SerializedName("reason")
    private int reason;
    @Expose
    @SerializedName("succeed")
    private boolean succeed;

    public List<Petica> getItems() {
        return items;
    }

    public void setItems(List<Petica> items) {
        this.items = items;
    }

    public int getItemcount() {
        return itemcount;
    }

    public void setItemcount(int itemcount) {
        this.itemcount = itemcount;
    }

    public int getReason() {
        return reason;
    }

    public void setReason(int reason) {
        this.reason = reason;
    }

    public boolean getSucceed() {
        return succeed;
    }

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }
}
