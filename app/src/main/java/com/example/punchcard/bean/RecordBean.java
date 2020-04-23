package com.example.punchcard.bean;

public class RecordBean {


    private String fileName;
    private String filePath;

    public RecordBean(String fileName,String filePath){
        this.fileName = fileName;
        this.filePath = filePath;

    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }
}
