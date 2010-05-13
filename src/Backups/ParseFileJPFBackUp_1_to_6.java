package Backups;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ParseFileJPFBackUp_1_to_6 {
	
	private static int thread_Num=0;
	private static int transition_Num=0;
	private static int trace_Num =0;
	private static Vector<Integer> transition_states = new Vector();
	
	private static Vector<String> transition_states_info = new Vector();
	
	
	/*
	 * Parses the file given to retrieve certain information
	 */
	public static void parseFile(String fileName){
		// Pattern for matching thread number
		Pattern threadPattern = Pattern.compile(".* thread: ([0-9]+)");
		int fileThreadNum=0;
		
		// Pattern for matching transition number
		Pattern transitionPattern = Pattern.compile("(.*)transition #([0-9]+)(.*)");
		int fileTransitionNum=0;
		
		// Pattern for matching trace number
		Pattern tracePattern = Pattern.compile("(.*)trace #([0-9]+)");
		int fileTraceNum=0;
		
		// These variables are used to gather transition info JPF outputs
		String transition_info = "";
		boolean start_gathering = false;
		Pattern stopPattern = Pattern.compile("([=]+).*"); // to stop taking down transition info
		
		try {
			
		    FileReader input = new FileReader(fileName);
		    BufferedReader buffRead = new BufferedReader(input);
		    
		    String line ="";
		    line = buffRead.readLine();
		    
		    while(line != null){
		    	// matcher for transition
		    	Matcher transitionMatch = transitionPattern.matcher(line);
		    	// parse file to see the transitions of the threads
		    	if(transitionMatch.matches()){
		    	   fileTransitionNum = Integer.parseInt(transitionMatch.group(2));
		    	   transition_Num = fileTransitionNum;
		    	   
		    	   // gather information on the transition
		    	   if(fileTransitionNum == 0){
		    		   transition_info = "";
		    		   start_gathering = true;
		    	   }else{
		    		   transition_states_info.add(transition_info);
		    		   transition_info = "";
		    	   }
		    	}
		    	
		    	// matcher for thread
		    	Matcher threadMatch = threadPattern.matcher(line);
		    	
		    	// parse file to see the schedule of the thread
		    	if(threadMatch.matches()){
		    	   fileThreadNum = Integer.parseInt(threadMatch.group(1));
		    	   transition_states.add(fileThreadNum);
		    	   
		    	   // keeps track of the overall number of threads
		    	   if(fileThreadNum > thread_Num){ 
		    		   thread_Num = fileThreadNum;
		    	   }
		    	}
		    	
		    	// matcher for trace
		    	Matcher traceMatch = tracePattern.matcher(line);
		    	// parse file to see the trace number
		    	if(traceMatch.matches()){
		    	   fileTraceNum = Integer.parseInt(traceMatch.group(2));
		    	   trace_Num = fileTraceNum;
		    	}
		    	
		    	
		    	// to check when to stop taking down info
		    	Matcher stopMatch = stopPattern.matcher(line);
		    	if(stopMatch.matches()){
		    		start_gathering = false;
		    	}
		    	
		    	// transition info gathering
		    	if((start_gathering) && (!traceMatch.matches()) && (!threadMatch.matches()) && (!transitionMatch.matches())){
		    		transition_info += "\n" + line;
		    	}

		    	
		    	
		    	line = buffRead.readLine();
		    }
		    
		    // add last info
		    transition_states_info.add(transition_info);
		    
		    // thread and transition number is always +1
		    thread_Num++;
		    transition_Num++;
		    	    
		    input.close();
		    
		}
		catch(IOException e){
			
		}
		
	}
	
	
	public static void main(String[] args){
		// Start by parsing the given JPF report (Hard coded for now)
		
		parseFile("Gowth_JPF_report_MyRace"); // My Race Condition
		//parseFile("Gowth_JPF_report_TheirRace"); // Their Race Condition
	    //parseFile("Gowth_JPF_report_Deadlock"); // Their Deadlock example
		//parseFile("Gowth_JPF_report_Deadlock_conc-1-RA"); // Their "oldclassic" example
		//parseFile("Gowth_JPF_report_MyRaceMulti"); // My Race Condition
		//parseFile("Gowth_JPF_report_ALL"); // My Race Condition
		
		
		// Create a GUI from the given 
		System.out.println("Trace Number: " + trace_Num);
		System.out.println("Transitions: " + transition_Num);
		System.out.println("Number of Threads: " + thread_Num);		
		
		CreateGUIBackUp1 test = new CreateGUIBackUp1(trace_Num, thread_Num, transition_Num, transition_states, transition_states_info);

	}

}
