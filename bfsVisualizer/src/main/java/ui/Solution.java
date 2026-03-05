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
	


}
