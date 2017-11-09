package com.sftp.client;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class HDFSConnect {




    public static void connectToHdfs(String unixPath,String hdfsPath, String hdfsURI) {
        Configuration conf = new Configuration();
//        conf.set("fs.defaultFS", "hdfs://10.86.11.24:8020");


        try {

            FileSystem fs = FileSystem.get(conf);

            System.out.println("== Connected to HDFS == "+fs.getUri());
            FileStatus[] fsStatus = fs.listStatus(new Path(hdfsPath));
            for (int i = 0; i < fsStatus.length; i++) {
                System.out.println(fsStatus[i].getPath().toString());
            }

            //pushing to hdfs

            File[] filename = new File(unixPath).listFiles();

            for (int i = 0; i < filename.length; i++) {

                fs.copyFromLocalFile(new Path(filename[i].getAbsolutePath()),new Path(hdfsPath));

            }

            System.out.println("File pushed to HDFS");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }
