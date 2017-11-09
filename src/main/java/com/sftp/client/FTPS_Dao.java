package com.sftp.client;

import com.jcraft.jsch.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FTPS_Dao {

    String SFTPHOST = null;
    int SFTPPORT;
    String SFTPUSER = null;
    String SFTPPASS = null;
    String SFTPWORKINGDIR = null;
    String TARGETFILEPATH=null;
    String FTP_FileName=null;
    Session session;
    Channel channel;
    ChannelSftp channelSftp;
    Long previous_time=null;


    Map<String,Long> files = new HashMap<String, Long>();


    public FTPS_Dao(String SFTPHOST,int SFTPPORT,String SFTPUSER,String SFTPPASS,String SFTPWORKINGDIR, String TARGETFILEPATH, String FTP_FileName, Long previous_time) throws JSchException, SftpException {
    this.SFTPHOST=SFTPHOST;
    this.SFTPPORT=SFTPPORT;
    this.SFTPUSER=SFTPUSER;
    this.SFTPPASS=SFTPPASS;
    this.TARGETFILEPATH=TARGETFILEPATH;
    this.SFTPWORKINGDIR=SFTPWORKINGDIR;
    this.FTP_FileName=FTP_FileName;
    this.previous_time=previous_time;
    }

    public String connect(){

        String ret=null;
        try {
            JSch jsch = new JSch();
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


            String stat=null;

            //SFTP file download
            stat = SFTP_File_Download(channel,SFTPWORKINGDIR,TARGETFILEPATH,FTP_FileName);


            //Realeasing all sessions
            release(channel,session);

            if(stat.equals("Files downloaded")){
                ret="SFTP success";
            }
            else
            {
                ret="SFTP failed";
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return ret;
    }


    public String release(Channel channel,Session session){
         channel.disconnect();
        System.out.println("== channel Closed ==");
        session.disconnect();
        System.out.println("== Session Closed ==");

        return "disconnected";
    }
    //

    public String SFTP_File_Download(Channel channel, String SFTPWORKINGDIR,String destination, String sourceFileName){
        String ret=null;
        try {
            channelSftp = (ChannelSftp) channel;

            //fetch latest SFTP files

            String stat = SFTP_LatestFiles(channelSftp,SFTPWORKINGDIR, sourceFileName);


            //download latest sftp files

            if(stat.equals("Files arrived")) {
                channelSftp.cd(SFTPWORKINGDIR);
                System.out.println("=== Started Download File from SFTP ===");
                for (Map.Entry<String, Long> entry : files.entrySet()) {

                    channelSftp.get(entry.getKey(), destination);
                    System.out.println("=== File download completed ===");

                }

                channelSftp.disconnect();
                System.out.println("== SFTP channel Closed ==");
                ret="Files downloaded";
            }
            else{

                ret="Files not downloaded";
            }
            }
        catch(Exception e){
            e.printStackTrace();
        }


        return ret;

    }

    public String SFTP_LatestFiles(ChannelSftp channelSftp, String SFTPWORKINGDIR, String sourceFileName){


        Vector<ChannelSftp.LsEntry> list = null;
        try {

            channelSftp.cd(SFTPWORKINGDIR);
            list = channelSftp.ls(sourceFileName);
            System.out.println(list.toString());
            Pattern p = Pattern.compile("(.*)_([0-9]*)(.*)");

            Map<String,Long> fileTimestamp = new HashMap<String, Long>();

            for (ChannelSftp.LsEntry lsEntry : list) {

                String fileName=lsEntry.getFilename();

                // Now create matcher object.
                Matcher m = p.matcher(fileName);

                //System.out.println(lsEntry.getFilename());
                if (m.find( )) {
                    //System.out.println("Found value: " + m.group(0) );
                    //System.out.println("Found value: " + m.group(1) );
//                            System.out.println("Found value: " + m.group(2) );

                    fileTimestamp.put(m.group(0), Long.valueOf(m.group(2)));

                }else {
                    System.out.println("NO MATCH");
                }
            }

            for (Map.Entry<String, Long> entry : fileTimestamp.entrySet()) {
                // System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

                if(entry.getValue()>previous_time){

                    //System.out.println(entry.getKey());

                    files.put(entry.getKey(),entry.getValue());

                }


            }

        } catch (SftpException e) {
            e.printStackTrace();
        }


        String ret = null;

        if(files.isEmpty())
            ret  = "No files arrived";
        else if(!files.isEmpty())
            ret = "Files arrived";
        
        return ret;
    }

    public void unzip(String TARGETFILEPATH){
        try {
            String s = null;


//            command[0]="cd "+TARGETFILEPATH+"&&unzip '*.zip'&&rm -rf *.zip;";
            
            Process p = Runtime.getRuntime().exec(new String[]{"bash","-c","unzip '*.zip'"},null,new File("/home/premanand.naik/sftpdownload"));

            Process p1 = Runtime.getRuntime().exec(new String[]{"bash","-c","rm -rf *.zip"},null,new File("/home/premanand.naik/sftpdownload"));



        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public void delete_files(String TARGETFILEPATH){
        try {
            String s = null;
            Process p = Runtime.getRuntime().exec("rm -rf *.*;");
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }






        } catch (IOException e) {
            e.printStackTrace();
        }


    }



}
