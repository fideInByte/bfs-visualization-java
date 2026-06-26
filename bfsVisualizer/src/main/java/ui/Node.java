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

public class Node{
	String state;
	float cost;
	int depth;
	Node parent = null;
	
	public Node(String s, float c, Node p) {
		state = s;
		cost = c;
		depth = 0;
		parent = p;
	}
	
	public Node(String s, float c, int d, Node p) {
		state = s;
		cost = c;
		depth = d;
		parent = p;
	}
	


}
