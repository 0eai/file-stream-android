package com.hash.include.filestream.model;


public class Fs {
    private String type;
    private String path;
    private String name;
    private String extname;
    private String size;
    private String atimeMs;
    private String mtimeMs;
    private String ctimeMs;
    private String birthtimeMs;

    public Fs(String type, String path, String name, String extname, String size,
                 String atimeMs, String mtimeMs, String ctimeMs, String birthtimeMs) {
        this.type = type;
        this.path = path;
        this.name = name;
        this.extname = extname;
        this.size = size;
        this.atimeMs = atimeMs;
        this.mtimeMs = mtimeMs;
        this.ctimeMs = ctimeMs;
        this.birthtimeMs = birthtimeMs;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getExtname() {
        return extname;
    }

    public void setExtname(String extname) {
        this.extname = extname;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getAtimeMs() {
        return atimeMs;
    }

    public void setAtimeMs(String atimeMs) {
        this.atimeMs = atimeMs;
    }

    public String getMtimeMs() {
        return mtimeMs;
    }

    public void setMtimeMs(String mtimeMs) {
        this.mtimeMs = mtimeMs;
    }

    public String getCtimeMs() {
        return ctimeMs;
    }

    public void setCtimeMs(String ctimeMs) {
        this.ctimeMs = ctimeMs;
    }

    public String getBirthtimeMs() {
        return birthtimeMs;
    }

    public void setBirthtimeMs(String birthtimeMs) {
        this.birthtimeMs = birthtimeMs;
    }
}