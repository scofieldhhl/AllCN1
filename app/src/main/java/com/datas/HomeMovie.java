package com.datas;

import android.os.Parcel;
import android.os.Parcelable;

public class HomeMovie implements Parcelable {

    private String name;
    private int nofocusImgRes, focusImgRes, titleBgId, titleIconId, mainBgId, type, status;
    private String iconUrl, iconFUrl, mainBgUrl, iconTUrl;
    private boolean haveSerIcon;

    public HomeMovie() {}

    protected HomeMovie(Parcel in) {
        name = in.readString();
        nofocusImgRes = in.readInt();
        focusImgRes = in.readInt();
        titleBgId = in.readInt();
        titleIconId = in.readInt();
        mainBgId = in.readInt();
        type = in.readInt();
        status = in.readInt();
        iconUrl = in.readString();
        iconFUrl = in.readString();
        mainBgUrl = in.readString();
        iconTUrl = in.readString();
        haveSerIcon = in.readByte() != 0;
    }

    public static final Creator<HomeMovie> CREATOR = new Creator<HomeMovie>() {
        @Override
        public HomeMovie createFromParcel(Parcel in) {
            return new HomeMovie(in);
        }

        @Override
        public HomeMovie[] newArray(int size) {
            return new HomeMovie[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNofocusImgRes() {
        return nofocusImgRes;
    }

    public void setNofocusImgRes(int nofocusImgRes) {
        this.nofocusImgRes = nofocusImgRes;
    }

    public int getFocusImgRes() {
        return focusImgRes;
    }

    public void setFocusImgRes(int focusImgRes) {
        this.focusImgRes = focusImgRes;
    }

    public int getTitleBgId() {
        return titleBgId;
    }

    public void setTitleBgId(int titleBgId) {
        this.titleBgId = titleBgId;
    }

    public int getTitleIconId() {
        return titleIconId;
    }

    public void setTitleIconId(int titleIconId) {
        this.titleIconId = titleIconId;
    }

    public int getMainBgId() {
        return mainBgId;
    }

    public void setMainBgId(int mainBgId) {
        this.mainBgId = mainBgId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getIconFUrl() {
        return iconFUrl;
    }

    public void setIconFUrl(String iconFUrl) {
        this.iconFUrl = iconFUrl;
    }

    public String getMainBgUrl() {
        return mainBgUrl;
    }

    public void setMainBgUrl(String mainBgUrl) {
        this.mainBgUrl = mainBgUrl;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setHaveSerIcon(boolean haveSerIcon) {
        this.haveSerIcon = haveSerIcon;
    }

    public boolean isHaveSerIcon() {
        return haveSerIcon;
    }

    public String getIconTUrl() {
        return iconTUrl;
    }

    public void setIconTUrl(String iconTUrl) {
        this.iconTUrl = iconTUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(nofocusImgRes);
        dest.writeInt(focusImgRes);
        dest.writeInt(titleBgId);
        dest.writeInt(titleIconId);
        dest.writeInt(mainBgId);
        dest.writeInt(type);
        dest.writeInt(status);
        dest.writeString(iconUrl);
        dest.writeString(iconFUrl);
        dest.writeString(mainBgUrl);
        dest.writeString(iconTUrl);
        dest.writeByte((byte) (haveSerIcon ? 1 : 0));
    }
}
