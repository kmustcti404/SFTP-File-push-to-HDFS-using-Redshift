# SFTP-File-push-to-HDFS-using-Redshift
Get files from SFTP server and push it to HDFS location


This project is utility that push files from SFTP server to HDFS location.  

We have so many files in SFTP server and say if SFTP server would be having previous pushed files. So whille next time this utility push files from SFTP server, it searches for last max timestamp of previously pushed files and fetches latest files arrived after this timestamp.

Hence we are storing previous file timestamp in redshift.


Below is the command to run

Syntax --  

java -cp .:* com.sftp.client.SFTP_Client "IP", 22,"user","password","SFTP directory","local download path","*.*","hdfs dir","HDFS uri"

Eg,

java -cp .:* com.sftp.client.SFTP_Client "x.x.x.x", 22,"Jhon","J@123","/home/jhon/sftpfiles","/home/jhon/sftpdownload/","*.*","/hawkeye/jhon/sftp/","hdfs://x.x.x.x:8020"




Written By :

Prem