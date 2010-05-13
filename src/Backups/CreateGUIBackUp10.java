package Backups;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

public class CreateGUIBackUp10 extends JFrame {
	
	// to generate random numbers
	private Dimension scrnsize;
	
	private List<List<Integer>> transition_states_color;
	private List<List<Integer>> transition_states_ours;
	private List<String> transition_errors;
	private List<List<String>> transition_states_info_ours;
	
	private final PaintTopGUIBackUp10 topPaint;
	private Color[] threadColor;
	private Color[] genColors;
	private int tranistionInfoPos = 0; // we are going to process transition info line by line, this keep tracks of which line we are on
	private int buttonID = 0;
	private String buttonCMD = "";
	private String currentLine = "";
	private String currentLineProgram = "";
	private int currentLineProgramIndex =0;
	private int startIndex = 0;
	private int endIndex = 0;
	private int curTrace = 0;
	private boolean programLineFound = false;
	
	private Highlighter.HighlightPainter[] threadHighlighter;
	private final List<String> programFileNames_ours;
	
	private final List<List<JButton>> leftPanelBtns;
	private final List<JLabel> leftPanelLbls;
	private int curLeft_rowPos = 0;
	private int curLeft_colPos = 0;
	
	private JScrollPane[] scrollingArea;
	private JTabbedPane tabbedPaneCenter;
	private JTabbedPane tabbedPaneEast;
	private JTextArea[] programInfo;
	
	private int[][] transitionInfoPos;
	private int transitionInfoPos_size;

	public CreateGUIBackUp10(int trace_Num, int thread_Num, List<Integer> transition_Num, List<List<Integer>> transition_states, List<List<String>> transition_states_info, List<String> transition_states_error, List<String> programFileNames){

        // Get the default toolkit
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		int state = 0;
		this.transition_states_ours = transition_states;
		this.transition_errors = transition_states_error;
		this.programFileNames_ours = programFileNames;
		this.transition_states_color = transition_states;
		this.transition_states_info_ours = transition_states_info;
		transitionInfoPos = new int[trace_Num][2];
	
		// Get the current screen size
		scrnsize = toolkit.getScreenSize();
		scrnsize.width = scrnsize.width;
		scrnsize.height = scrnsize.height - 20;
		
		//this.setExtendedState(MAXIMIZED_BOTH);
		this.setPreferredSize(scrnsize);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout(5,5));
		mainPanel.setBackground(Color.WHITE);
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(1, trace_Num));
		
		final JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new GridLayout(150, 1));
		leftPanel.setBackground(Color.white);
		
		JButton threadLegend;
		JLabel legendText;
		
		// Borders
		Border blackline = BorderFactory.createLineBorder(Color.black);
		Border raisedbevel = BorderFactory.createRaisedBevelBorder();
		Border loweredbevel = BorderFactory.createLoweredBevelBorder();
		Border compoundBorderRaised = BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(
		                10, 5, 10, 5, Color.black), raisedbevel);

		Border compoundBorderLowered = BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(
		                2, 2, 2, 2, Color.black), loweredbevel);
		
        leftPanel.setBorder(compoundBorderRaised);
        topPanel.setBorder(blackline);
        
		
		// Defining a Color to each thread and creating a seperate highlighter for each color
        genColors = new Color[10];
        threadHighlighter = new Highlighter.HighlightPainter[10];
        this.genThreadColors();
		threadColor = new Color[thread_Num];
	    for (int i=0; i < thread_Num; i++){						
			 // assign colors
			 threadColor[i] = genColors[i];
		 }		
		   
	   // Text Area for transitions
	   final JTextArea transitions = new JTextArea("");
	   transitions.setColumns(100);
	   transitions.setRows(20);
	   transitions.setBackground(Color.white);

	   // COME BACK
	   // mainPanel.add(scrollingArea, BorderLayout.CENTER);
		
		Vector<JPanel> schedPaneArray = new Vector();
		Vector<JPanel> schedPaneArrayWrap = new Vector();

		// Left Panel Buttons and Labels
		leftPanelBtns = new ArrayList<List<JButton>>();
		leftPanelLbls = new ArrayList<JLabel>();
		
		for(int j = 0; j < trace_Num; j++){
			//schedPaneArray.add(new JPanel());
			schedPaneArrayWrap.add(new JPanel());
			schedPaneArrayWrap.get(j).setBorder(blackline);

			leftPanelBtns.add(new ArrayList<JButton>());
			JLabel tempLbl = new JLabel("Schedule: " + j);
			leftPanelLbls.add(tempLbl);
			
			//schedPaneArray.get(j).setLayout(new GridBagLayout());
			schedPaneArrayWrap.get(j).setLayout(new BorderLayout(10,10));
			schedPaneArrayWrap.get(j).setBackground(Color.WHITE);
			
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			
		
		  // Adding buttons for each transition (add '+ 1' for the error button)
		  JButton[] threadButton = new JButton[transition_Num.get(j) + 1];
		 // System.out.println(j + " --> Size: " + transition_Num.get(j));
		  for(int i = 0; i < transition_Num.get(j) + 1; i++){
			threadButton[i] = new JButton("Col: " + j + "x Row: " + i );
			leftPanelBtns.get(j).add(new JButton("Col: " + j + "x Row: " + i ));
		  }
		
		  for(int i=0; i < transition_states.get(j).size(); i++){
		     final String info = transition_states_info.get(j).get(i);
		   
		     c.gridx = 0;
		     c.gridy = i;
		   
		     state = transition_states.get(j).get(i);	     
		     leftPanelBtns.get(j).get(i).setText("Sched: " + j + "| Transition: " + i );
		     leftPanelBtns.get(j).get(i).setBackground(threadColor[state]);
		     leftPanelBtns.get(j).get(i).setForeground(Color.BLACK);
		     curTrace = j;
		     
		     // when you press these buttons on the left, transition info will be displayed at the bottom
		     leftPanelBtns.get(j).get(i).addActionListener(
					    new ActionListener() {
					        public void actionPerformed(ActionEvent e) {	
					        	String cmdStr[] = e.getActionCommand().split("[\\|]+");
					        	Pattern number = Pattern.compile("(.*)([: ])([0-9]+).*");
					        	Matcher partOne = number.matcher(cmdStr[0]);
					        	Matcher partTwo = number.matcher(cmdStr[1]);
					        	
					        	if(partOne.matches() && partTwo.matches()){
					        		
					        	    final int rowValue = Integer.parseInt(partOne.group(3));
					        	    final int colValue = Integer.parseInt(partTwo.group(3));
					        	    
					        	    // reset the old button highlighted color
							    	leftPanelBtns.get(curLeft_rowPos).get(curLeft_colPos).setBackground(threadColor[transition_states_ours.get(curLeft_rowPos).get(curLeft_colPos)]);
							    	leftPanelBtns.get(curLeft_rowPos).get(curLeft_colPos).setForeground(Color.BLACK);
							    	
							    	// set colors for both top panel and left panel
					        	    topPaint.setHighlight(colValue, rowValue, null, 0);
							        leftPanelBtns.get(rowValue).get(colValue).setForeground(Color.RED);
					        	    remove(leftPanel, rowValue, colValue,leftPanelBtns, transitions);
					        	    
					        	    // assign new current button highlighted
					        	    curLeft_rowPos = rowValue;
					        	    curLeft_colPos = colValue;
	
   	
					        	}

					        	
					        	transitions.setText(info);
					        	// Indicates that a same button has been pressed
					        	if(buttonID == e.getID() && buttonCMD.equals(e.getActionCommand())){
					        		//tranistionInfoPos++; 
					        		buttonID = e.getID();
					        		buttonCMD = e.getActionCommand();
					        		
					        	}
					        	else{ // new button is pressed
					        		tranistionInfoPos = 0;
					        		buttonID = e.getID();
					        		buttonCMD = e.getActionCommand();

					        	}
					        					        	
					        	// try highlighting text
					        	try{
						        	  
					        	  // check if the line is in a program
					        	  while(!programLineFound){
					        	       // if program line is not found
					        		  //System.out.println(currentLine);
					        	       if(!programLineFound){
					        	    	 tranistionInfoPos++;
					        	         startIndex = transitions.getLineStartOffset(tranistionInfoPos);
							             endIndex = transitions.getLineEndOffset(tranistionInfoPos);
							             currentLine = transitions.getText().substring(startIndex, endIndex);
							             
					        	       } 
					        		  
					        		  
					        	       for(int i = 0; i < programFileNames_ours.size(); i++){
					        		       if(currentLine.contains(programFileNames_ours.get(i)) && (!programLineFound) ){
					        		    	   programLineFound = true;
					        		    	   currentLineProgram = programFileNames_ours.get(i);
					        		    	   
					        		    	   currentLineProgramIndex = i;
					        		    	   //System.out.println("Program Name: " + currentLineProgram + " -- At index: " + currentLineProgramIndex);
					        		    	  
					        		       }
					        	       }
					        	       
					        	       

					        	  }
					        	  programLineFound = false;
					        	  transitions.getHighlighter().removeAllHighlights();
					        	  int stateColor = transition_states_color.get(curLeft_rowPos).get(curLeft_colPos);
					        	  transitions.getHighlighter().addHighlight( transitions.getLineStartOffset(tranistionInfoPos), transitions.getLineEndOffset(tranistionInfoPos), threadHighlighter[stateColor] );
					        	 
					        	  /*
					        	   * This part of the code takes care of highlighting the code in java programs
					        	   */
					        	  tabbedPaneCenter.setSelectedIndex(currentLineProgramIndex); // highlights tab
					        	  currentLine = currentLine.substring(currentLine.indexOf(": ")+2); // finds the actual line of code
					        	  
					        	  // need to go through the code and determine what the line number is
					        	  String temp = programInfo[currentLineProgramIndex].getText();
					        	  startIndex = temp.indexOf(currentLine);
					    					        	  
					        	  programInfo[currentLineProgramIndex].getHighlighter().removeAllHighlights();
					        	  programInfo[currentLineProgramIndex].getHighlighter().addHighlight( startIndex, startIndex+currentLine.length(), threadHighlighter[stateColor] );
	  
					        	}
					        	catch (BadLocationException bl){
					        		System.out.println("Bad Location: " + bl.toString());
					        		tranistionInfoPos = 0;
					        	}
					        }
					    }
					);

		}
		  
		  // this code takes care of last error button
		  int lastBtn = leftPanelBtns.get(j).size()-1;
		  leftPanelBtns.get(j).get(lastBtn).setText("Error Info");
		  leftPanelBtns.get(j).get(lastBtn).setBackground(Color.GRAY);
		  leftPanelBtns.get(j).get(lastBtn).setForeground(Color.BLACK);
		  
		  leftPanelBtns.get(j).get(lastBtn).addActionListener(
				  new ActionListener() {
					  public void actionPerformed(ActionEvent e) {
						  transitions.setText(transition_errors.get(curTrace));
					  }
				  }
			);
		 
        // defaults
		  topPanel.add(schedPaneArrayWrap.get(j));
		
	    }
		

        
		Dimension topScrollDim = new Dimension();
		topScrollDim.height = scrnsize.height / 3;
		topScrollDim.width = scrnsize.width;
		
		topPaint = new PaintTopGUIBackUp10(trace_Num, thread_Num, transition_Num, transition_states, transition_states_info, transition_states_error);
		topPaint.setPreferredSize(topScrollDim);
		
		topPaint.addMouseListener( new MouseAdapter() {
		    public void mousePressed( MouseEvent e ) {
		    	// reset the old button highlighted color
		    	leftPanelBtns.get(curLeft_rowPos).get(curLeft_colPos).setForeground(Color.BLACK);
		    	
		    	curLeft_rowPos = topPaint.getTraceNumber(e.getX());
		    	curLeft_colPos = topPaint.getTransitionNumber(curLeft_rowPos, e.getY());
		        //System.out.println("Trace Pos: " + trace_yPos);
		    	
		    	//find all the other occurrences of this transition
		    	findAllTransitionInfo(curLeft_rowPos, curLeft_colPos);
		    	
		    	// highlight the button pressed / all the occurrences of simalar transitions
		        topPaint.setHighlight(curLeft_colPos, curLeft_rowPos, transitionInfoPos, transitionInfoPos_size);
		        
		        if(curLeft_colPos >= leftPanelBtns.get(curLeft_rowPos).size() - 1){
		        	leftPanelBtns.get(curLeft_rowPos).get(curLeft_colPos-1).setForeground(Color.RED);
		        }else{
		        	leftPanelBtns.get(curLeft_rowPos).get(curLeft_colPos).setForeground(Color.RED);
		        }
		        
		        
		        remove(leftPanel, curLeft_rowPos, curLeft_colPos, leftPanelBtns, transitions);
		        topPaint.repaint();
		        
		    }
		} );
		mainPanel.add(topPaint, BorderLayout.NORTH);
		
        JScrollPane leftScroll = new JScrollPane(leftPanel);	
        Dimension leftScrollDim = new Dimension();
        leftScrollDim.height = scrnsize.height;
        leftScrollDim.width = scrnsize.width / 9;
        leftScroll.setPreferredSize(leftScrollDim);
        
		mainPanel.add(leftScroll, BorderLayout.WEST);
        this.add(mainPanel);
        
        // text area for each program file (we also upload all information to these areas)
        programInfo = new JTextArea[programFileNames.size()];
        String programName = "";
        String line = "";
        for (int i = 0; i < programInfo.length; i++){
        	programInfo[i] = new JTextArea("");
        	programName = programFileNames.get(i);
        	try{
        	    BufferedReader reader = new BufferedReader(new FileReader("src/BankRaceCondition/" + programName));
        	    line = reader.readLine();
        	    while(line != null){
        	    	programInfo[i].append(line + "\n");
        	    	line = reader.readLine();
        	    }  
        	    
        	}
        	catch(IOException e){
        		System.out.println("IOException: " + e.toString());
        	}
        	
        }
     
        
        // scrolling area for each program file 
        // we add '+ 1' because we want last tab to hold default transitions info
        scrollingArea = new JScrollPane[programFileNames.size() + 1];
        for (int i = 0; i < programInfo.length; i++){
        	scrollingArea[i] = new JScrollPane(programInfo[i]);
        	scrollingArea[i].setBorder(blackline);
        }
        
        // transitions info part
        scrollingArea[scrollingArea.length - 1] = new JScrollPane(transitions);
    	scrollingArea[scrollingArea.length - 1].setBorder(blackline);
       
   
        // Tabbed Stuff ( adding program file names to tabs)
    	tabbedPaneCenter = new JTabbedPane();
    	tabbedPaneEast = new JTabbedPane();
        
        for (int i = 0; i < programFileNames.size(); i++){
        	tabbedPaneCenter.addTab(programFileNames.get(i), scrollingArea[i]);
        }
        
        tabbedPaneEast.addTab("Transition Info", scrollingArea[scrollingArea.length - 1]);
        Dimension eastTabDim = new Dimension();
        eastTabDim.height = scrnsize.height;
        eastTabDim.width = scrnsize.width / 3;    
        tabbedPaneEast.setPreferredSize(eastTabDim);
        
        
        mainPanel.add(tabbedPaneCenter, BorderLayout.CENTER);
        mainPanel.add(tabbedPaneEast, BorderLayout.EAST);
 
        this.pack();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
	}
	
	/*
	 * - clears the left panel 
	 * - adds current set of buttons to left panel
	 */	
	public void remove(JPanel leftPanel, int j, int k, List<List<JButton>> leftPanelBtns,  JTextArea scrollText){
		int size = leftPanelBtns.get(j).size();
		topPaint.repaint();
		
		leftPanel.removeAll();
		scrollText.setText("");
      
		leftPanel.setLayout(new GridLayout(size, 1));
		leftPanel.setBackground(Color.white);
		//leftPanel.add(leftPanelLbls.get(j));
		
		for (int l = 0; l < size; l++){
			leftPanel.add(leftPanelBtns.get(j).get(l));
		}
		
		//this.setPreferredSize(new Dimension(this.MAXIMIZED_HORIZ, this.MAXIMIZED_VERT));
		//this.setExtendedState(MAXIMIZED_BOTH);
		this.setPreferredSize(scrnsize);
		leftPanel.repaint();
		this.pack();
	}
	
	/*
	 * This method generates colors for up to 10 different threads (RGB values)
	 * - We are not using red because it is the high lighter color
	 */
	public void genThreadColors(){
		
		genColors[0] = new Color(58,98,132); // blue
		threadHighlighter[0] = new DefaultHighlighter.DefaultHighlightPainter( genColors[0] );
		
		genColors[1] = new Color(51,157,70); // green
		threadHighlighter[1] = new DefaultHighlighter.DefaultHighlightPainter( genColors[1] );
		
		genColors[2] = new Color(255,127,50); // orange
		threadHighlighter[2] = new DefaultHighlighter.DefaultHighlightPainter( genColors[2] );
		
		genColors[3] = new Color(145,105,171); // purple
		threadHighlighter[3] = new DefaultHighlighter.DefaultHighlightPainter( genColors[3] );
		
		genColors[4] = new Color(136,89,79); // brown
		threadHighlighter[4] = new DefaultHighlighter.DefaultHighlightPainter( genColors[4] );
		
		genColors[5] = new Color(226,123,182); // pink
		threadHighlighter[5] = new DefaultHighlighter.DefaultHighlightPainter( genColors[5] );
		
		genColors[6] = new Color(88,88,88); // grey
		threadHighlighter[6] = new DefaultHighlighter.DefaultHighlightPainter( genColors[6] );
		
		genColors[7] = new Color(84,143,174); // teal
		threadHighlighter[7] = new DefaultHighlighter.DefaultHighlightPainter( genColors[7] );
		
		genColors[8] = new Color(191,186,57); // gold
		threadHighlighter[8] = new DefaultHighlighter.DefaultHighlightPainter( genColors[8] );
		
		genColors[9] = new Color(1,71,189); // royal blue
		threadHighlighter[9] = new DefaultHighlighter.DefaultHighlightPainter( genColors[9] );
	}
	
	/*
	 * this Method, given the position the transition info, finds out the positions
	 * of all other similar transition info's
	 */
	public void findAllTransitionInfo(int row, int col){
		transitionInfoPos_size = 0;
		String matchInfo = transition_states_info_ours.get(row).get(col);
		
		for(int i = 0; i < transition_states_info_ours.size(); i++){
			for(int j = 0; j < transition_states_info_ours.get(i).size(); j++){
				
				// 
				if( matchInfo.equals(transition_states_info_ours.get(i).get(j))){
					//System.out.println("Match Found!");
					transitionInfoPos[transitionInfoPos_size][0] = i;
					transitionInfoPos[transitionInfoPos_size][1] = j;
					
					transitionInfoPos_size++;		
				}
			}
			
		}
		
	}
	
}
