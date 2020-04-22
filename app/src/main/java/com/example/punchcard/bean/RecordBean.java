package com.example.punchcard.bean;

public class RecordBean {


    private String fileName;
    private String fileTime;

    public RecordBean(String fileName){
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileTime() {
        return fileTime;
    }
}
