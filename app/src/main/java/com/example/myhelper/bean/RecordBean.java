package com.example.myhelper.bean;

public class RecordBean {


    private String fileMsg;
    private String fileName;
    private String fileTime;
    private String filePath;

    public RecordBean(String fileMsg,String fileName,String fileTime,String filePath){
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileMsg = fileMsg;
        this.fileTime = fileTime;

    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileMsg() {
        return fileMsg;
    }

    public String getFileTime() {
        return fileTime;
    }
}
