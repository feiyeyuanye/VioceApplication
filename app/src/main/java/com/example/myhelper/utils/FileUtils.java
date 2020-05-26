package com.example.myhelper.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2016/7/10.
 */
public class FileUtils {

//    public interface onFileCompleListener {
//        void onFileComple();
//    }

    //遍历所有文件
    public static List<File> getAllFiles() {
        List<File> files = new ArrayList<>();
        getFileFromDir(files, Environment.getExternalStorageDirectory());
        return files;
    }

    public static void getFileFromDir(List<File> list, File dir) {
        File[] files = dir.listFiles();
        if (files == null)
            return;
        for (File file : files) {
            if (file.isDirectory()) {
                getFileFromDir(list, file);
            }
            list.add(file);
        }
    }

    public static List<File> getCurrFiles(String dir) {
        List<File> list = new ArrayList<>();
        File file = new File(dir);
        File[] files = file.listFiles();
        if (files == null)
            return list;
        //文件夹在上
        for (File cfile : files) {
            if (cfile.isDirectory())
                list.add(cfile);
        }
        for (File cfile : files) {
            if (!cfile.isDirectory())
                list.add(cfile);
        }
        return list;
    }


    public static String getTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(time);
    }

    //排序方法
    //名称排序
    public static void sortByName(List<File> list, boolean b) {
        if (b) {
            Collections.sort(list, new Comparator<File>() {
                @Override
                public int compare(File file, File t1) {
                    return file.getName().compareToIgnoreCase(t1.getName());
                }
            });
        } else {
            Collections.sort(list, new Comparator<File>() {
                @Override
                public int compare(File file, File t1) {
                    return t1.getName().compareToIgnoreCase(file.getName());
                }
            });
        }
    }


    //文件大小排序
    public static void sortBySize(List<File> list, boolean b) {
        //从小到大
        if (b) {
            Collections.sort(list, new Comparator<File>() {
                @Override
                public int compare(File file, File t1) {
                    return (int) (file.length() - t1.length());
                }
            });
        } else {
            Collections.sort(list, new Comparator<File>() {
                @Override
                public int compare(File file, File t1) {
                    return -(int) (file.length() - t1.length());
                }
            });
        }
    }

    public static void sortByTime(List<File> list, boolean b) {
        //从小到大
        if (b) {
            Collections.sort(list, new Comparator<File>() {
                @Override
                public int compare(File file, File t1) {
                    return (int) (file.lastModified() - t1.lastModified());
                }
            });
        } else {
            Collections.sort(list, new Comparator<File>() {
                @Override
                public int compare(File file, File t1) {
                    return -(int) (file.lastModified() - t1.lastModified());
                }
            });
        }
    }


    //删除
    public static final void deleteFile(File file, OnFileDeteteListener listener) {
        List<File> deleteFile = new ArrayList<>();
        getFileFromDir(deleteFile, file);
        for (File delete : deleteFile) {
            delete.delete();
        }
    }


    public interface OnFileDeteteListener {
        //文件夹名
        public void onFileDetete(String name);
    }


    public static final void MoveFileNew(List<File> from, File to) {
        for (File file : from) {
            String path = file.getParent();
            String toFile = file.getAbsolutePath().replace(path, to.getAbsolutePath());
            file.renameTo(new File(toFile));
        }
    }


    //移动
    public static final void moveFileList(List<File> from, File to) {
        for (File file : from) {
            List<File> fromFiles = new ArrayList<>();
            fromFiles.add(file);
            String path = "";
            if (file.isDirectory()) {
                path = file.getParent();
            }
            getFileFromDir(fromFiles, file);
            for (File copyFile :
                    fromFiles) {
                //from == copyFile
                File toFile = new File(copyFile.getAbsolutePath().replace(path, to.getAbsolutePath()));
                copyFile.renameTo(toFile);
            }
        }
    }


    public static final void copyFile(File from, File to) {
        to.getParentFile().mkdirs();
        try {
            InputStream is = new FileInputStream(from);
            OutputStream os = new FileOutputStream(to);
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = is.read(buf)) != -1) {
                os.write(buf, 0, len);
                os.flush();
            }
            os.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
