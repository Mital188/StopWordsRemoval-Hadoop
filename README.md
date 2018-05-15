-----------------------------------------Read Me-----------------------------------------------------------------
Input:- Path to 6 large files from Gutenberg project and 1 file containing Wikipedia text on HDFS(assignment-1):
hdfs://cshadoop1/user/msm160530/assignment1
-----------------------------------------------------------------------------------------------------------------
Steps to Run the project:
1. Import the given project as maven project into eclipse
2. Run As -> Maven Build ; Goal -> Package
3. Copy the jar file created from the target folder in eclipse workspace into the user Hadoop directory
4. Run the following command for Part 1:
----------------------------------------------------------------------------------------------------------------

Hadoop command:

hadoop jar stopWord.parti-0.0.1-SNAPSHOT.jar HDFS_assignment_2.stopWord.parti.Parti hdfs://cshadoop1/user/msm160530/assignment1 hdfs://cshadoop1/user/msm160530/output

-------------------------------------------------------------------------------------------------------------------
Command to get the output:
hdfs dfs -get output/part-r-00000

To delete the output file:
hdfs dfs -rm -r /user/msm160530/output

No. of arguments: 2
-Input path to get the text files from assignmnet1
-Output path on Hadoop where the results are stored

5. Make sure the output path you give (2nd argument) is a new directory (not previously exisitng directory).
6. All the the meaningful words with their word count(Reducer Output) can be found in one text file: part-r-00000, in the user Hadoop directory.
