--1002070498 Dedeepya Nallamothu
Connect to omega server
	load all the files using FileZilla

	go to DB_project2/Task2 directory and start using sql plus 

In the omega server run the java file to load all the records into the database

1.  javac -cp lib/opencsv-4.1.jar:lib/ojdbc8.jar:lib/commons-lang3-3.12.0.jar OracleCon.java
    java -cp .:lib/opencsv-4.1.jar:lib/ojdbc8.jar:lib/commons-lang3-3.12.0.jar OracleCon
	The above will load the values from csv files into the tables

2. To execute the inserted values
	in sqlplus-> 
		spool Task2-Query2.lst
		start Queries2.txt
		spool off

3. To execute the weekly display 
	in sqlplus -> 
		spool Task3-Query3.lst
		start Query3.txt
		spool off

4. To perform the transactions in part4:
	In the omega server run the java file to load all the records into the database
	javac -cp .:lib/opencsv-4.1.jar:lib/ojdbc8.jar:lib/commons-lang3-3.12.0.jar LibraryTransactions.java
   	java -cp .:lib/opencsv-4.1.jar:lib/ojdbc8.jar:lib/commons-lang3-3.12.0.jar LibraryTransactions

(Based on the task you want to perform enter the value and follow according to the instructions.)

5. For triggers 
	in sqlplus -> 
		spool Task5-Trigger.lst
		start Trigger.txt
		spool off

