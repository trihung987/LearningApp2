package me.trihung.learningapp2.Entity;

import java.io.Serializable;

public class TuVung implements Serializable {
    private String tiengAnh;
    private String tiengViet;
    private String phienAm;
    private String group;

    public TuVung(String tiengAnh, String tiengViet, String phienAm, String group) {
        this.tiengAnh = tiengAnh;
        this.tiengViet = tiengViet;
        this.phienAm = phienAm;
        this.group = group;

    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getPhienAm() {
        return phienAm;
    }

    public void setPhienAm(String phienAm) {
        this.phienAm = phienAm;
    }

    public String getTiengAnh() {
        return tiengAnh;
    }

    public void setTiengAnh(String tiengAnh) {
        this.tiengAnh = tiengAnh;
    }

    public String getTiengViet() {
        return tiengViet;
    }

    public void setTiengViet(String tiengViet) {
        this.tiengViet = tiengViet;
    }
}
