package com.sftp.client;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import junit.framework.TestCase;
import org.junit.Test;

public class FTPS_DaoTest extends TestCase {

    String SFTPHOST = "10.86.11.10";
    int    SFTPPORT = 22;
    String SFTPUSER = "ram.kumar.choudhary";
    String SFTPPASS = "QHyr32&&bb";
    String SFTPWORKINGDIR = "/home/ram.kumar.choudhary/SFTP";
    String destinationFilePath="C:\\SFTP\\";
    String FTPFileName="*.txt";

    String unixPath=destinationFilePath;
    String hdfsPath="/hawkeye/sftp/";
    String hdfsURI="hdfs://10.86.11.24:8020";

    //redshift write
    Long maxTimestamp = null;

    Long filestamp;
    FTPS_Dao dao;
    Session session;
    Channel channel;
    JSch jsch;


    public void setUp() throws Exception {
        super.setUp();

        RedshiftWrite.redshiftUrl = "jdbc:redshift://10.86.13.152:5439/test";
        RedshiftWrite.masterUsername = "dbadmin";
        RedshiftWrite.password = "<)R3gektY4";

        filestamp = RedshiftWrite.read();
        dao=new FTPS_Dao(SFTPHOST,SFTPPORT,SFTPUSER,SFTPPASS,SFTPWORKINGDIR,destinationFilePath,FTPFileName,filestamp);



    }

    public void tearDown() throws Exception {
    }


    public void testConnect() throws Exception {


        assertEquals("SFTP success",dao.connect());


    }

    public void testRelease() throws Exception {

        jsch = new JSch();
        session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);


        session.setPassword(SFTPPASS);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        System.out.println("== Session established ==");
        channel = session.openChannel("sftp");
        channel.connect();
        System.out.println("== Channel Opened ==");


        assertEquals("disconnected",dao.release(channel,session));
    }

    public void testSFTP_File_Download() throws Exception {

        String SFTPWORKINGDIR = "/home/ram.kumar.choudhary/SFTP";
        String destinationFilePath="C:\\SFTP\\";
        String FTPFileName="*.txt";

        jsch = new JSch();
        session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);


        session.setPassword(SFTPPASS);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        System.out.println("== Session established ==");
        channel = session.openChannel("sftp");
        channel.connect();
        System.out.println("== Channel Opened ==");


        assertEquals("Files downloaded",dao.SFTP_File_Download(channel,SFTPWORKINGDIR,destinationFilePath,FTPFileName));


    }

    public void testSFTP_LatestFiles() throws Exception {

        jsch = new JSch();
        ChannelSftp channelSftp;
        session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);


        session.setPassword(SFTPPASS);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        System.out.println("== Session established ==");
        channel = session.openChannel("sftp");
        channel.connect();
        System.out.println("== Channel Opened ==");

        channelSftp = (ChannelSftp) channel;

        if(dao.SFTP_LatestFiles(channelSftp,SFTPWORKINGDIR, FTPFileName).equals("No files arrived"))
        assertEquals("No files arrived",dao.SFTP_LatestFiles(channelSftp,SFTPWORKINGDIR, FTPFileName));
        else
        assertEquals("Files arrived",dao.SFTP_LatestFiles(channelSftp,SFTPWORKINGDIR, FTPFileName));

    }

}