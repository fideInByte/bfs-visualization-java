package ui;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

public class BFS {
	private Map<String, Map<String, Float>> stateSpace = new HashMap<>();
	
	private String startState = null;
	private List<String> targetStates = new LinkedList<>();
	
	private Queue<Node> open = new LinkedList<>();
	
	private List<Node> tree = new LinkedList<>();
	private List<Node> path = new LinkedList<>();
	
	
	private Set<String> visitedStates = new HashSet<>();
	
	private List<BFSEvent> bfsEvents = new LinkedList<>(); //anim
	
	public Map<String, Map<String, Float>> getStateSpace() {
		return stateSpace;
	}
	
	public String getStartState() {
		return startState;
	}

	public List<BFSEvent> getBfsEvents(){
		return bfsEvents;
	}
	
	
	public void run(String[] args) {
       
        String stateSpaceFile = null;
        
        for(int i = 0; i < args.length;i++) {
        	if(args[i].equals("--ssf")) {
        		stateSpaceFile = args[i+1];
        		i++;
        	}
        	
        }
        
        if (stateSpaceFile == null) {
            throw new IllegalArgumentException(
                "Missing required argument: --ssf <path-to-state-space-file>"
            );
        }

        
        
        
        
        try (BufferedReader br = new BufferedReader(new FileReader(stateSpaceFile))) {//write in stateSpace map
            String line;
            
            while ((line = br.readLine()) != null) {
            	line = line.trim();
            	//comments
            	if(line.isEmpty()) {
            		continue;
            	}else if(startState==null) {
            		//first line
            		startState = line;
            		continue;
            	}else if(targetStates.isEmpty()) {
            		//second line
            		targetStates = (Arrays.asList(line.split(" ")));
            		continue;
            	}
            		
            	
       
            	
            	//remove comment if there is one
            	int commentIndex = line.indexOf("#");
            	if (commentIndex != -1) {
            	    line = line.substring(0, commentIndex).trim();
            	}

            	if (line.isEmpty()) {
            	    continue;
            	}

            	//"state : successors"
            	String[] parts = line.split("\\s*:\\s*", 2);
            	if (parts.length != 2) {
            	    throw new IllegalArgumentException("Invalid line format: " + line);
            	}

            	String state = parts[0].trim();
            	String rightSide = parts[1].trim();

            	Map<String, Float> succ = new TreeMap<>();

            	if (!rightSide.isEmpty()) {
            	    String[] transitions = rightSide.split("\\s+");

            	    for (String transition : transitions) {
            	        transition = transition.trim();
            	        if (transition.isEmpty()) continue;

            	        String[] edgeParts = transition.split("\\s*,\\s*", 2);
            	        if (edgeParts.length != 2) {
            	            throw new IllegalArgumentException("Invalid transition: " + transition + " in line: " + line);
            	        }

            	        String nextState = edgeParts[0].trim();
            	        String costText = edgeParts[1].trim();

            	        succ.put(nextState, Float.parseFloat(costText));
            	    }
            	}

            	stateSpace.put(state, succ);
                
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
        
      
        
    	System.out.println("# BFS");
    	Node goal = bfsAlgorithm();
    	if(goal == null) {
    		System.out.println("[FOUND NODE]: no");
    	}else {
    		System.out.println("[FOUND NODE]: yes");

    		//System.out.printf("TREE: ");
    		System.out.println("[STATES VISITED]: "+tree.size());
    		buildPathFromGoal(goal);
    		
    		System.out.println("[PATH LENGTH]: "+(path.size()));
    		
    		System.out.println("[TOTAL COST]: "+goal.cost);
    		
    		System.out.printf("[PATH]: ");
    		for(int i = 0; i < path.size()-1; i++) {
    			System.out.printf(path.get(i).state+" -> ");
    		}
    		System.out.println(path.get(path.size()-1).state);

    	}
       
        	
	}
	
	

	Node bfsAlgorithm() {
		bfsEvents.clear();//anim
		Node start = new Node(startState, 0, null);
		open.add(start);
		visitedStates.add(start.state);
		
		bfsEvents.add(new BFSEvent(EventStates.ENQUEUE, start.state));//anim
		
		
		while(!open.isEmpty()) {
			
			Node head = open.poll();
			bfsEvents.add(new BFSEvent(EventStates.DEQUEUE, head.state));//anim
			
			tree.add(head);
			bfsEvents.add(new BFSEvent(EventStates.VISITED, head.state));//anim
			
			if(targetStates.contains(head.state)) {
				bfsEvents.add(new BFSEvent(EventStates.GOAL_FOUND, head.state));//anim
				return head;
			}
				
				
			
			insertOpenBack(head);
				
			
			
		}
		return null;
	}
	
	boolean insertOpenBack(Node parent) {
		if(!stateSpace.containsKey(parent.state)) {
			return false;
		}
		
		Map<String, Float> children = new TreeMap<>(stateSpace.get(parent.state));
		
		for(Map.Entry<String, Float> child: children.entrySet()) {
			
			if(visitedStates.contains(child.getKey()))
				continue;
			
			visitedStates.add(child.getKey());	
			float addCost = child.getValue();
			open.add(new Node(child.getKey(), parent.cost + addCost, parent.depth + 1, parent));
			
			bfsEvents.add(new BFSEvent(EventStates.ENQUEUE, child.getKey()));//anim
			 
		}
		
		return true;
	}
	
	void buildPathFromGoal(Node goal) {
		path.clear();
	
		for(Node cur = goal; cur != null; cur = cur.parent) {
			path.add(cur);
			bfsEvents.add(new BFSEvent(EventStates.PATH, cur.state));//anim
		}
			
		
		Collections.reverse(path);
		
		return;
	}
	
	


}
