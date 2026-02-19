package ui;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.List;

public class Solution{
	String state;
	float cost;
	int depth;
	Solution parent = null;
	
	public Solution(String s, float c, Solution p) {
		state = s;
		cost = c;
		depth = 0;
		parent = p;
	}
	
	public Solution(String s, float c, int d, Solution p) {
		state = s;
		cost = c;
		depth = d;
		parent = p;
	}
	
	
	
	//public Map<String, Float> prijelazZaStanje = new HashMap<>(); 
		static public Map<String, Map<String, Float>> stateSpace = new HashMap<>();
		static public Map<String, Float> heuristFunc = new TreeMap<>(); //keys ordered alphabetically
		
		static public String startState = null;
		static public List<String> targetStates = new LinkedList<>();
		
		static public Queue<Solution> open = new LinkedList<>();
		
		static public List<Solution> tree = new LinkedList<>();
		static public List<Solution> path = new LinkedList<>();
		
		static public String alg = null;
		
		static public Set<String> visitedStates = new HashSet<>();
		
		static public List<BFSEvent> bfsEvents = new LinkedList<>(); //anim

		

		public static void main(String[] args) {
	        //System.out.println("Running Java version: " + System.getProperty("java.version"));
	        
	        //System.out.println("Received args: " + Arrays.toString(args));
	       
	        String stateSpaceFile = null;
	        
	        for(int i = 0; i < args.length;i++) {
	        	if(args[i].equals("--ssf")) {
	        		stateSpaceFile = args[i+1];
	        		i++;
	        	}
	        	
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
	            		//System.out.println("Pocetno stanje: "+startState);
	            		continue;
	            	}else if(targetStates.isEmpty()) {
	            		//second line
	            		targetStates = (Arrays.asList(line.split(" ")));
	            		//System.out.println("Ciljna stanja: "+targetStates);
	            		continue;
	            	}
	            		
	            	//state: next_state_1,cost next_state_2,cost
	                //System.out.println(line);
	                
	                String[] words = line.split(" ");
	                String state = words[0].substring(0, words[0].length()-1);//remove ':' from state
	                
	                Map<String, Float> succ = new TreeMap<>();
	                	                
	                for(int i = 1; i < words.length; i++) {
	                	String next_state = words[i].split(",")[0];
	                	String cost = words[i].split(",")[1];
	                	succ.put(next_state, Float.parseFloat(cost));
	                }
	                
	                //add all prijelaze for that state
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
        		
        		//System.out.printf("PATH nakon sto smo izasli iz metode: ");
        		//printListWithSolutions(path);
        		System.out.println("[PATH_LENGTH]: "+(path.size()));
        		
        		System.out.println("[TOTAL_COST]: "+goal.cost);
        		
        		System.out.printf("[PATH]: ");
        		for(int i = 0; i < path.size()-1; i++) {
        			System.out.printf(path.get(i).state+" => ");
        		}
        		System.out.println(path.get(path.size()-1).state);

        	}
	       
	        		
	        

		}//main
		
		static Solution bfsAlgorithm() {
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
			//System.out.println("izlazimo iz metode");
			return null;
		}
		
		
	

		static public boolean treeContains(String state) {
			for(Solution s : tree) {
				if(s.state.equals(state))
					return true;
			}
			return false;
		}
		
		
		static public boolean insertOpenBack(Solution parent) {
			if(!stateSpace.containsKey(parent.state)) {//ako nema za parent prijelaz u stateSpace mapi
				return false;
			}
			for(Map.Entry<String, Float> child: stateSpace.get(parent.state).entrySet()) {
				
				if(visitedStates.contains(child.getKey()))
					continue;
				
				visitedStates.add(child.getKey());	
				float addCost = child.getValue();
				open.add(new Solution(child.getKey(), parent.cost + addCost, parent.depth + 1, parent));
				
				bfsEvents.add(new BFSEvent(EventStates.ENQUEUE, child.getKey()));//anim
				 
			}
			
			return true;
		}
		
		static public void buildPathFromGoal(Solution goal) {
			//System.out.println("Pozvali buildPathFromTree");
			path.clear();
		
			for(Solution cur = goal; cur != null; cur = cur.parent)
				path.add(cur);
			//System.out.println("Izlazimo iz metode");
			
			Collections.reverse(path);
			
			//anim
			for(Solution s : path) {
				bfsEvents.add(new BFSEvent(EventStates.PATH, s.state));//anim
			}
			//anim
			return;
		}
		
		static public void printListWithSolutions(List<Solution> list) {
			for(Solution s: list) {
				if(s.parent == null) {
					System.out.printf(s.state+"("+s.cost+", "+s.depth+"), parent(null)\n");
				}else {
					System.out.printf(s.state+"("+s.cost+", "+s.depth+"), parent("+s.parent.state+", "+s.parent.depth+")\n");

				}
			}
			System.out.println();
		}
		
		static public float calculateTotalCost() {
			float total = 0;
			for(Solution elem: path) {
				
				if(elem.parent!=null) {
					//System.out.println("Gledamo element "+elem.state +" do "+elem.parent.state);
					String toState = elem.state;
					String fromState = elem.parent.state;
					total += stateSpace.get(fromState).get(toState);
				}
			}
			
			return total;
		}


}
