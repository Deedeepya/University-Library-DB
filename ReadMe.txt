1. Connect to omega server
	load all the files using FileZilla

	go to DB_project1 directory and start using sql plus 

Task1: CREATE TABLES
	start create_tables.txt
	(this command loads all the tables with its references into the sql database.)
	
Task2:INSERT INTO TABLES

ALTER TABLE DEPARTMENT DISABLE CONSTRAINT FK_MGR_SSN_EMPLOYEE;  
ALTER TABLE EMPLOYEE DISABLE CONSTRAINT FK_SUPER_SSN_EMPLOYEE;
ALTER TABLE DEPT_LOCATIONS DISABLE CONSTRAINT FK_DNUMBER_DEPARTMENT;
ALTER TABLE PROJECT DISABLE CONSTRAINT FK_DNUM_DEPARTMENT;
ALTER TABLE WORKS_ON DISABLE CONSTRAINT FK_ESSN_EMPLOYEE_W;
ALTER TABLE WORKS_ON DISABLE CONSTRAINT FK_PNO_PROJECT;
ALTER TABLE DEPENDENT DISABLE CONSTRAINT FK_ESSN_EMPLOYEE_D;  

In the omega server run the java file to load all the records into the database

javac -cp lib/opencsv-4.1.jar:lib/ojdbc8.jar:lib/commons-lang3-3.12.0.jar Task2/OracleCon.java
java -cp .:lib/opencsv-4.1.jar:lib/ojdbc8.jar:lib/commons-lang3-3.12.0.jar Task2.OracleCon

Now as the records are been loaded into the tables 

Perform the below operations

ALTER TABLE DEPARTMENT ENABLE CONSTRAINT FK_MGR_SSN_EMPLOYEE;
ALTER TABLE EMPLOYEE ENABLE CONSTRAINT FK_SUPER_SSN_EMPLOYEE;
ALTER TABLE DEPT_LOCATIONS ENABLE CONSTRAINT FK_DNUMBER_DEPARTMENT;
ALTER TABLE PROJECT ENABLE CONSTRAINT FK_DNUM_DEPARTMENT;
ALTER TABLE WORKS_ON ENABLE CONSTRAINT FK_ESSN_EMPLOYEE_W;
ALTER TABLE WORKS_ON ENABLE CONSTRAINT FK_PNO_PROJECT;
ALTER TABLE DEPENDENT ENABLE CONSTRAINT FK_ESSN_EMPLOYEE_D;

Outputs are present in insert_output.lst

Task3: INSERT 3 RECORDS THAT VIOLATE INTEGRITY CONSTRAINTS.
	 spool violate_delete.lst
	 start violate.txt
	 spool off
	constraints are mentioned in read_violate.txt
	outputs are present in violate_delete.lst

Task4: DELETE 3 RECORDS THAT ARE INSERTED WHICH VIOLATES INTEGRITY CONSTRAINT
	 spool delete_violate_output.lst
	 start delete_violate.txt
	 spool off
	outputs are present in delete_violate_output.lst

Task5: INSERT 3 RECORDS THAT DONT VIOLATE INTEGRITY CONSTRAINTS.
	 spool dont_violate_output.lst
	 start dont_violate.txt
	 spool off
	outputs are present in dont_violate_output.lst

Task 6: Create trigger
	 SPOOL trigger_output.lst
	 start trigger.txt
	 spool off
	outputs are present in trigger_output.lst
	
