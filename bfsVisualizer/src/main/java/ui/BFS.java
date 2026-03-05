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

public class BFS {//remove what is not needed!!!
	private Map<String, Map<String, Float>> stateSpace = new HashMap<>();
	
	private String startState = null;
	private List<String> targetStates = new LinkedList<>();
	
	private Queue<Solution> open = new LinkedList<>();
	
	private List<Solution> tree = new LinkedList<>();
	private List<Solution> path = new LinkedList<>();
	
	
	private Set<String> visitedStates = new HashSet<>();
	
	private List<BFSEvent> bfsEvents = new LinkedList<>(); //anim
	
	public Map<String, Map<String, Float>> getStateSpace() {
		return stateSpace;
	}

	public List<BFSEvent> getBfsEvents(){
		return bfsEvents;
	}
	
	
	public void run(String[] args) {
		
        //System.out.println("Running Java version: " + System.getProperty("java.version"));
        
        //System.out.println("Received args: " + Arrays.toString(args));
       
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
            	if(line.contains("#") || line.isEmpty()) {
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
            		
            	
                
                String[] words = line.split(" ");
                String state = words[0].substring(0, words[0].length()-1);//remove ':' from state
                
                Map<String, Float> succ = new TreeMap<>();
                	                
                for(int i = 1; i < words.length; i++) {
                	System.out.println("Gledamo words[i]="+words[i]);
                	
                	String next_state = words[i].split(",")[0];
                	System.out.println("next_state je "+words);
                	String cost = words[i].split(",")[1];
                	succ.put(next_state, Float.parseFloat(cost));
                }
                
                stateSpace.put(state, succ);
                
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
        
      
        
    	System.out.println("# BFS");
    	Solution goal = bfsAlgorithm();
    	if(goal == null) {
    		System.out.println("[FOUND_SOLUTION]: no");
    	}else {
    		System.out.println("[FOUND_SOLUTION]: yes");

    		//System.out.printf("TREE: ");
    		//printListWithSolutions(tree);
    		System.out.println("[STATES_VISITED]: "+tree.size());
    		buildPathFromGoal(goal);
    		
    		//printListWithSolutions(path);
    		System.out.println("[PATH_LENGTH]: "+(path.size()));
    		
    		System.out.println("[TOTAL_COST]: "+goal.cost);
    		
    		System.out.printf("[PATH]: ");
    		for(int i = 0; i < path.size()-1; i++) {
    			System.out.printf(path.get(i).state+" => ");
    		}
    		System.out.println(path.get(path.size()-1).state);

    	}
       
        	
	}
	
	

	Solution bfsAlgorithm() {
		bfsEvents.clear();//anim
		//System.out.println("bfsAlgorithm called");
		Solution start = new Solution(startState, 0, null);
		open.add(start);
		visitedStates.add(start.state);
		
		bfsEvents.add(new BFSEvent(EventStates.ENQUEUE, start.state));//anim
		
		
		while(!open.isEmpty()) {
			//Solution head = open.get(0);
			Solution head = open.poll();
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
	
	boolean insertOpenBack(Solution parent) {
		if(!stateSpace.containsKey(parent.state)) {
			return false;
		}
		
		Map<String, Float> children = new TreeMap<>(stateSpace.get(parent.state));
		
		for(Map.Entry<String, Float> child: children.entrySet()) {
			
			if(visitedStates.contains(child.getKey()))
				continue;
			
			visitedStates.add(child.getKey());	
			float addCost = child.getValue();
			open.add(new Solution(child.getKey(), parent.cost + addCost, parent.depth + 1, parent));
			
			bfsEvents.add(new BFSEvent(EventStates.ENQUEUE, child.getKey()));//anim
			 
		}
		
		return true;
	}
	
	void buildPathFromGoal(Solution goal) {
		path.clear();
	
		for(Solution cur = goal; cur != null; cur = cur.parent)
			path.add(cur);
		
		Collections.reverse(path);
		
		//anim
		for(Solution s : path) {
			bfsEvents.add(new BFSEvent(EventStates.PATH, s.state));//anim
		}
		//anim
		return;
	}
	
	


}
