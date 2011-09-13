package gov.nasa.jpf.gVisualizer;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import gov.nasa.jpf.*;
import gov.nasa.jpf.Error;
import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.Path;
import gov.nasa.jpf.jvm.Step;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.Transition;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.util.Left;

public class JPFVisualization extends ListenerAdapter{
	  public static Logger log = JPF.getLogger("gov.nasa.jpf.report");

	  // JPF objects needed
	  protected Config conf;
	  protected JPF jpf;
	  protected Search search;
	  protected JVM vm;
	  
	  // My variables
	  private static int thread_Num=0;
	  private static int trace_Num =0;
		
	  private static List<String> transition_states_error = new ArrayList<String>();
	  private static List<List<String>> transition_states_info = new ArrayList<List<String>>();
	  private static List<List<Integer>> transition_states = new ArrayList<List<Integer>>();
	  private static List<Integer> transition_Num = new ArrayList<Integer>();
		
	  // This array is to keep track of the different program files used
	  private static List<String> programFileNames = new ArrayList<String>();
	  private CreateMainGUI testSkele;
	  
	  public JPFVisualization (Config conf, JPF jpf) {
		    this.conf = conf;
		    this.jpf = jpf;
		    search = jpf.getSearch();
		    vm = jpf.getVM();    		    
	  }
	  
	  //--- the listener interface that drives report generation

	  //--- the SearchListener interface
	  
	  /*
	   * Keeping track of the number of threads
	   */
	  public void gcEnd (JVM vm){
		  
		  if (vm.getThreadNumber() > thread_Num){
			  thread_Num = vm.getThreadNumber();
				  
		  }
		  //System.out.println("Thread CHoice: " + vm.getThreadNumber());

	  }
	  
	  public void gcBegin(JVM vm){
		  //System.out.println("GC BEGIN!: ");
	  }
	  
	  
	  //--- the SearchListener interface
	  
	  /*
	   * This method initializes certain methods and constants
	   */
	  public void searchStarted (Search search){
		 
		  
		  
		    try {
			    // Set System L&F
		        UIManager.setLookAndFeel(
		            UIManager.getSystemLookAndFeelClassName());
		    } 
		    catch (UnsupportedLookAndFeelException e) {
		       // handle exception
		    }
		    catch (ClassNotFoundException e) {
		       // handle exception
		    }
		    catch (InstantiationException e) {
		       // handle exception
		    }
		    catch (IllegalAccessException e) {
		       // handle exception
		    }

		  
	     System.out.println("\n\nSTARTED\n\n");
	  }
	  
	  /*
	   * This method is used to verify the information that was gathered
	   */
	  public void searchFinished (Search search){
		  
		     boolean showInfo = false;
		     System.out.println("\n\nFINISHED\n\n");
		     
		     //set progress bar off
		     testSkele.setProgressOff();
		     
		     if(showInfo){
		    	 System.out.println("Thread Count: " + thread_Num);
		    	 System.out.println("Trace Count: " + trace_Num);
			 
		    	 //information verification
			 
		    	 System.out.println("\n\t-XXXXXXXXXXXXXXXXXXXXXXXX- Inforamation Verification -XXXXXXXXXXXXXXXXXXXXXXXX-\n");
		    	 for(int i = 0; i < transition_Num.size(); i++){
		    		 System.out.println("Schedule: " + i + " ---> Size: " + transition_Num.get(i));
				 
				 
		    		 // information on transitions
		    		 for(int j = 0; j < transition_states.get(i).size(); j++){
		    			 System.out.println("------------------  Transition #" + j + ": " + transition_states.get(i).get(j));
		    			 System.out.println(transition_states_info.get(i).get(j));
		    		 }
				 
		    		 // error info
		    		 System.out.println("\n" + transition_states_error.get(i));
				 
		    	 }
			 }
			 
		     
		     // sorting purposes: copies original list
		     testSkele.copyOrginalSchedule();
		     
		     
		     //testSkele.calculateThreadErrors();
	  }
	  
	  public void searchConstraintHit(Search search){
		  System.out.println("\n\nSearch Constraint Hit\n\n"); 
		  
	  }

	  public void propertyViolated(Search search){
		  int transitionId = 0;
		  int transNum = 0;
		  String copySteps = "";
		  String parseProgram = "";
		  boolean addFile = true;
		  String errorDetails = "";
		  
		  // filling in the data structures
		  trace_Num++;
		  
		  // get the last error
		  Error e = search.getLastError(); 
		  errorDetails = "Error Caught By: " + e.getDescription()+ "\n" + "Details: " + e.getDetails(); 
		  transition_states_error.add(errorDetails);
		  
		  //System.out.println(errorDetails);
		  
          transitionId = e.getId() - 1;

          vm.updatePath();
          Path path = vm.getClonedPath();
          
          // add a new transition_states path
          transition_states.add(new ArrayList<Integer>());
          transition_states_info.add(new ArrayList<String>());
          
          transNum = -1;
          for(Transition t : path){
        	  transNum++;
        	  //System.out.println("-----------------------------------------------");
              String lastLine = null;
              
              // Keeps track of which thread gets executed for each transition
              // i.e. transition #0 - Thread #1 gets executed
              transition_states.get(transitionId).add(t.getThreadIndex());

              copySteps = "";
              for (Step s : t) {
            	  
                  String line = s.getLineString();
                  
                   	  // keeping the same format as ConsolePublisher 
                      if (line != null) {
                    	  
                    	  // get Source Code Class Names
                    	  parseProgram = s.getLocationString();
                    	  parseProgram = parseProgram.substring(0, parseProgram.indexOf(':'));
                    	  parseProgram = parseProgram.trim();
                    	  
                    	  // add the source code to the structure if its 1st Name found
                    	  if(programFileNames.size() <= 0){
                    		  programFileNames.add(parseProgram);
                    	  }else{
                    		  addFile = true;
                    		  for(int i = 0; i < programFileNames.size(); i++){
                    			  if(programFileNames.get(i).equals(parseProgram)){
                    				  addFile = false;
                    			  }
                    			  
                    		  }
                    		  
                    		  if(addFile){
                    			  programFileNames.add(parseProgram);
                    		  }
                    	  }
                    	  
                    	  
                    	 // System.out.println("\nXXXXXXXXXXXXX\n" + parseProgram + "\nXXXXXXXXXxx\n");
                    	  if (!line.equals(lastLine)) {     
                    		  copySteps += "  ";
                        	  copySteps += Left.format(s.getLocationString(),30);
                        	  copySteps += " : ";
                        	  copySteps += line.trim();
                        	  copySteps += "\n";
                    	  }
                      } else { // no source
                        
                      }
                      
                      lastLine = line;
                      
              }
              // this is to keep track of what steps of code gets executed at each transition
              if(copySteps.equals("")){
            	  copySteps = "JPF has no sources for bytecode executed (Most Likely from an External Library)";
              }
              transition_states_info.get(transitionId).add(copySteps);
		      
          }
         
          // insert number of transitions in this path 
          transition_Num.add(transNum + 1); 

         
          // error info being put into the transition states
          transition_states.get(transitionId).add(0);
          transition_states_info.get(transitionId).add(errorDetails);

          // we add +1 to the number of the transitions to include the last error transition 
          transition_Num.set(transitionId, transition_Num.get(transitionId) + 1);
          
          //System.out.println("\n\n0. Start");
          
		  // Top GUI
          if(transitionId == 0){ 	  
        	  testSkele = new CreateMainGUI(trace_Num, (thread_Num + 1), transition_Num, transition_states, transition_states_info, transition_states_error, programFileNames);
          }else{
        	  //System.out.println("transitionId: " + transitionId);
      		 // System.out.println(transition_states);
        	  testSkele.reDrawAllFrames(trace_Num, transition_Num, transition_states, transition_states_info, transition_states_error);  
        	  //testSkele.pack();
        	  
          }
          
       //   for(long i = 0; i < 100000000; i++){
        	  
        //  }
	  }
	  
	  public void choiceGeneratorProcessed (JVM vm){
		 // System.out.println("\n-------------CHOICE PROCESSED-------------\n");
	  }
	  
	  public void choiceGeneratorAdvanced (JVM vm) {
		 // System.out.println("\n-------------CHOICE Advanced-------------\n");
	  }
}
