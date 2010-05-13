package ThreadGUI;


import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;


public class ParseFileJPF extends JFrame{
	
	private static int thread_Num=0;
	//private static int[] transition_Num = new int[1000];
	
	private static int trace_Num =0;
	//private static Vector<Integer>[] transition_statesB;
	//private static Vector<String>[] transition_states_infoB;
	
	private static List<String> transition_states_error = new ArrayList<String>();
	private static List<List<String>> transition_states_info = new ArrayList<List<String>>();
	private static List<List<Integer>> transition_states = new ArrayList<List<Integer>>();
	private static List<Integer> transition_Num = new ArrayList<Integer>();
	
	// This array is to keep track of the different program files used
	private static List<String> programFileNames = new ArrayList<String>();
	private static CreateGUI test;
	private Dimension scrnsize;

	
	/*
	 * Parses the file given to retrieve certain information
	 */
	public static void parseFile(String fileName){
			
		// Pattern for matching Java program File names
		Pattern filePattern = Pattern.compile("\\s(.+).java.*");
		
		// Pattern for matching the final error for the schedule
		Pattern endError = Pattern.compile("error #([0-9]+): (.*)");
		
		// Pattern for matching thread number
		Pattern threadPattern = Pattern.compile(".* thread: ([0-9]+)");
		int fileThreadNum=0;
		
		// Pattern for matching transition number
		Pattern transitionPattern = Pattern.compile("(.*)transition #([0-9]+)(.*)");
		int fileTransitionNum=0;
		
		// Pattern for matching trace number
		Pattern tracePattern = Pattern.compile("(.*)trace #([0-9]+)");
		int fileTraceNum=0;
		
		// Keep track on when to start ALL trace gathering
		boolean getInfoAll = false;
		
		// These variables are used to gather specific transition info JPF outputs
		String transition_info = "";
		boolean start_gathering = false;
		Pattern stopPattern = Pattern.compile("([=]+).*"); // to stop taking down transition info

		try {
			
		    FileReader input = new FileReader(fileName);
		    BufferedReader buffRead = new BufferedReader(input);
		    
		    String line ="";
		    String fileNameFound = "";
		    line = buffRead.readLine();
		    boolean addFile = true;
		    
		    while(line != null){
		    	
		    	// matcher for file name
		    	Matcher fileNameMatch = filePattern.matcher(line);
		    	if(fileNameMatch.matches()){
		    		addFile = true;
		    		fileNameFound = fileNameMatch.group(1);
		    		fileNameFound = fileNameFound.trim();
		    		
		    		  if(!fileNameFound.matches("(.*)\\s(.*)")){
		    		
		    		  //System.out.println("Found File: " + fileNameFound);
		    		
		    		  if(programFileNames.size() == 0){
		    			programFileNames.add(fileNameFound + ".java");
		    		  }
		    		  else{
		    			for(int i = 0; i < programFileNames.size(); i++){
		    				if(programFileNames.get(i).equals(fileNameFound + ".java")){
		    					addFile = false;
		    				}
		    			}
		    			
		    			// if file name is not found in the current list, add it to list
		    			if(addFile){
		    				programFileNames.add(fileNameFound + ".java");
		    			}
		    		  }
		    		}
		    	}
		    	
		    	
		    	// matcher for final error
		    	Matcher errorMatch = endError.matcher(line);
		    	if(errorMatch.matches()){
		    		transition_states_error.add(errorMatch.group(2));	    		
		    	}

		    	// matcher for trace
		    	Matcher traceMatch = tracePattern.matcher(line);
		    	// parse file to see the trace number
		    	if(traceMatch.matches()){
		    	   fileTraceNum = Integer.parseInt(traceMatch.group(2));
		    	   trace_Num = fileTraceNum - 1;
		    	   transition_states.add(new ArrayList<Integer>());
		    	   transition_states_info.add(new ArrayList<String>());
		    	   transition_Num.add(0);
		    	   //System.out.println("TraceNum: " + trace_Num);
		    	   getInfoAll = true;
		    	}
		    	
		    	if(getInfoAll){
		    		
			    	// matcher for thread
			    	Matcher threadMatch = threadPattern.matcher(line);
			    	
			    	// parse file to see the schedule of the thread
			    	if(threadMatch.matches()){
			    	   fileThreadNum = Integer.parseInt(threadMatch.group(1));
			    	   transition_states.get(trace_Num).add(fileThreadNum);
			    	   
			    	   // keeps track of the overall number of threads
			    	   if(fileThreadNum > thread_Num){ 
			    		   thread_Num = fileThreadNum;
			    	   }
			    	}
			    	    	
			    	// matcher for transition
			    	Matcher transitionMatch = transitionPattern.matcher(line);
			    	// parse file to see the transitions of the threads
			    	if(transitionMatch.matches()){
			    	   fileTransitionNum = Integer.parseInt(transitionMatch.group(2));
			    	   //transition_Num[trace_Num] = fileTransitionNum;
			    	   
			    	   if(fileTransitionNum > transition_Num.get(trace_Num)){
			    	       transition_Num.set(trace_Num, fileTransitionNum);
			    	   }
			    	   
			    	   // gather information on the transition
			    	   if(fileTransitionNum == 0){
			    		    transition_info = "";
			    		    start_gathering = true;
			    	   }else{
			    		   transition_states_info.get(trace_Num).add(transition_info);
			    		   transition_info = "";
			    	   }
			    	}
			    	
			    	// to check when to stop taking down info
			    	Matcher stopMatch = stopPattern.matcher(line);
			    	if(stopMatch.matches()){
	
			    		// Saves information to the transition_state_info variable
			    		if(trace_Num == 0){
			    			if((!traceMatch.matches()) && start_gathering){
				    			transition_states_info.get(trace_Num).add(transition_info);
			    			}

			    		}
			    		else{
				    		if((traceMatch.matches())){
				    		    transition_states_info.get(trace_Num-1).add(transition_info);
				    		}
				    		
				    		// this one is to add info for last transition of last trace
				    		else if((!traceMatch.matches()) && start_gathering){
				    			transition_states_info.get(trace_Num).add(transition_info);
				    		}
			    		}
			    		
			    		start_gathering = false;
			    	}
			    	
			    	// transition info gathering
			    	if((start_gathering) && (!traceMatch.matches()) && (!threadMatch.matches()) && (!transitionMatch.matches())){
			    		transition_info += "\n" + line;
			    	}
		    		
		    	}
		    	
		    	line = buffRead.readLine();
		    }
		  
		    
		    input.close();
		    
		}
		catch(IOException e){
			
		}
		
		trace_Num++;
		thread_Num++;
		for(int i = 0; i < transition_Num.size(); i++){
			transition_Num.set(i, transition_Num.get(i) + 1);
		}
		
	}
	
	
	public static void main(String[] args){
    	// Start by parsing the given JPF report (Hard coded for now)
		
		//parseFile("Gowth_JPF_report_MyRace"); // My Race Condition
		//parseFile("Gowth_JPF_report_TheirRace"); // Their Race Condition
	    //parseFile("Gowth_JPF_report_Deadlock"); // Their Deadlock example
		//parseFile("Gowth_JPF_report_Deadlock_conc-1-RA"); // Their "oldclassic" example
		//parseFile("Gowth_JPF_report_MyRaceMulti"); // My Race Condition
		
		parseFile("Gowth_JPF_report_ALL"); // My Race Condition
		//parseFile("Test_Gowritharan_ALL_Multi"); // My Race Condition
		//parseFile("Test_Gowritharan_Oldclassic"); // Deadlock Condition

		//test = new CreateGUI(trace_Num, thread_Num, transition_Num, transition_states, transition_states_info, transition_states_error, programFileNames);
		
	}
		

}
