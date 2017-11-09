package com.sftp.client;
import com.jcraft.jsch.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.io.IOUtils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

public class SFTP_Client {

    public static void main(String args[]){



        String SFTPHOST = args[0];
        int    SFTPPORT = Integer.parseInt(args[1]);
        String SFTPUSER = args[2];
        String SFTPPASS = args[3];
        String SFTPWORKINGDIR = args[4];
        String destinationFilePath=args[5];
        String FTPFileName=args[6];

        String unixPath=destinationFilePath;
        String hdfsPath=args[7];
        String hdfsURI=args[8];




        //redshift write
        Long maxTimestamp = null;

        RedshiftWrite.redshiftUrl = args[9];
        RedshiftWrite.masterUsername = args[10];
        RedshiftWrite.password = args[11];

        Long filestamp;

        try {

            filestamp = RedshiftWrite.read();

            FTPS_Dao dao=new FTPS_Dao(SFTPHOST,SFTPPORT,SFTPUSER,SFTPPASS,SFTPWORKINGDIR,destinationFilePath,FTPFileName,filestamp);
            dao.connect();




            if(!dao.files.isEmpty()) {
                maxTimestamp = Collections.max(dao.files.values());


                //HDFS push
                dao.unzip(destinationFilePath);

                HDFSConnect.connectToHdfs(unixPath,hdfsPath,hdfsURI);



                File[] filename = new File(unixPath).listFiles();

                for (File file : filename) {
                    file.delete();
                }



                RedshiftWrite.write(maxTimestamp);
            }
        }
        catch (JSchException e) {
            e.printStackTrace();
        } catch (SftpException e) {
            e.printStackTrace();
        }
    }

}
