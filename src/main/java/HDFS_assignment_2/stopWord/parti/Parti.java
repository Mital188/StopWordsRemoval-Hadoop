package HDFS_assignment_2.stopWord.parti;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;



public class Parti extends Configured implements Tool 
{
	public static String stopWords;
	public static void main( String[] args )
	{
		try {
			int res =ToolRunner.run(new Parti(),args);
			System.exit(res);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public int run(String[] args) throws Exception {
		
		//Setup all configurations
		Configuration conf = new Configuration();
		conf.set("mapred.job.tracker", "hdfs://cshadoop1:61120");
		conf.set("yarn.resourcemanager.address", "cshadoop1.utdallas.edu:8032");
		conf.set("mapreduce.framework.name", "yarn");
		
		Job job = Job.getInstance(getConf(), "wordcount");
		job.setJarByClass(this.getClass());
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		job.setMapperClass(Map.class);
		job.setCombinerClass(Reduce.class);
		job.setReducerClass(Reduce.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		return job.waitForCompletion(true) ? 0 : 1;
	}
	public static class Map extends Mapper<LongWritable, Text, Text, IntWritable> {
		private final static IntWritable one = new IntWritable(1);
		private boolean caseSensitive = false;
		private static final Pattern WORD_BOUNDARY = Pattern.compile("\\s*\\b\\s*");

		@SuppressWarnings("rawtypes")
		protected void setup(Mapper.Context context)throws IOException,	InterruptedException {

			Configuration config = context.getConfiguration();
			this.caseSensitive = config.getBoolean("wordcount.case.sensitive", false);
			// All the stop words that are to be removed from the text file
			stopWords = "'tis,'twas,a,able,about,across,after,ain't,all,almost,also,am,among,an,and,any,are,aren't,as,at,be,because,been,but,by,can,can't,cannot,could,could've,couldn't,dear,did,didn't,do,does,doesn't,don't,either,else,ever,every,for,from,get,got,had,has,hasn't,have,he,he'd,he'll,he's,her,hers,him,his,how,how'd,how'll,how's,however,i,i'd,i'll,i'm,i've,if,in,into,is,isn't,it,it's,its,just,least,let,like,likely,may,me,might,might've,mightn't,most,must,must've,mustn't,my,neither,no,nor,not,of,off,often,on,only,or,other,our,own,rather,said,say,says,shan't,she,she'd,she'll,she's,should,should've,shouldn't,since,so,some,than,that,that'll,that's,the,their,them,then,there,there's,these,they,they'd,they'll,they're,they've,this,tis,to,too,twas,us,wants,was,wasn't,we,we'd,we'll,we're,were,weren't,what,what'd,what's,when,when,when'd,when'll,when's,where,where'd,where'll,where's,which,while,who,who'd,who'll,who's,whom,why,why'd,why'll,why's,will,with,won't,would,would've,wouldn't,yet,you,you'd,you'll,you're,you've,your";
		}

		public void map(LongWritable offset, Text lineText, Context context)throws IOException, InterruptedException {
			
			String line = lineText.toString();
			// If we need case insensitivity, then convert all the words to lower case
			if (!caseSensitive) {
				line = line.toLowerCase();
			}
			Text currentWord = new Text();
			//Matches all the words only and remove all numbers and special characters
			Pattern pattern = Pattern.compile("[^a-z]");
			for (String word : WORD_BOUNDARY.split(line)) {
				Matcher m = pattern.matcher(word);
				// ignores all the spaces and words that have length less than 5 characters
				if (word.isEmpty() || word.length() < 5 ) {
					continue;
				}
				// removes all words with special character
				else if(m.find()) {
					continue;
				}//removes all the stop words
				else if(stopwordmatch(word))
					continue;

				currentWord = new Text(word);
				context.write(currentWord,one);
			} 


		}
		//function to check if the given word is a stop word and if so returns true else false
		public static boolean stopwordmatch(String word) {
			String [] sw =stopWords.split(",");
			for (int i=0;i<sw.length;i++) {
				if(word.equalsIgnoreCase(sw[i]))
					return true;

			}
			return false;

		}

	}

	public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable> {
		@Override
		public void reduce(Text word, Iterable<IntWritable> counts, Context context)
				throws IOException, InterruptedException {
			int sum = 0;
			//Compute the total count for each words
			for (IntWritable count : counts) {
				sum += count.get();
			}
			context.write(word, new IntWritable(sum));
		}
	}
}
