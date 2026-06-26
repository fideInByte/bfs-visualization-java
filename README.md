# BFS Visualization in Java

A Java project for visualizing the **Breadth-First Search (BFS)** algorithm step by step.

The application reads a graph definition from a `.txt` file, runs BFS, and displays the search process visually using **JavaFX**.

## Features

* step-by-step BFS visualization
* highlights node expansion and traversal order
* multiple example input files
* reconstructs and displays the final path
* accepts comments and flexible spacing in input files


## Video

A short video demonstration of the project:
`<link>`

## How BFS Works in This Project

This visualizer demonstrates **Breadth-First Search**, which explores nodes **level by level**.

Important note:

* BFS finds the path with the **fewest steps / edges**
* during BFS, each expansion step is treated uniformly; BFS ignores edge weights while choosing which node to expand next
* the displayed total path cost is determined afterwards from the costs defined in the input file

## Running the Project

Run the project with:

```bash
mvn clean javafx:run -Djavafx.args="--ssf src/main/resources/intro.txt"
```

Other example files:

```bash
mvn clean javafx:run -Djavafx.args="--ssf src/main/resources/simple_ex.txt"
mvn clean javafx:run -Djavafx.args="--ssf src/main/resources/1_to_9.txt"
mvn clean javafx:run -Djavafx.args="--ssf src/main/resources/greek_alphabet.txt"
mvn clean javafx:run -Djavafx.args="--ssf src/main/resources/california.txt"
mvn clean javafx:run -Djavafx.args="--ssf src/main/resources/21_nodes.txt"
mvn clean javafx:run -Djavafx.args="--ssf src/main/resources/100_nodes.txt"
```

## Input Format

The graph is provided through a `.txt` file.

### Structure

* **First line** — start state
* **Second line** — one or more goal states
* **Remaining lines** — transitions for each state

### Transition format

```text
state: next_state1,cost1 next_state2,cost2
```

Example:

```text
A
G
A: B,1 C,2
B: D,3 E,1
C: F,2 G,4
```

## Input Rules

* extra spaces are tolerated
* comments can be added with `#`
* each transition line must start with a state name followed by `:`
* each successor must include both a state name and a cost

## Included Example Files

The project includes several example inputs in `src/main/resources`:

* `intro.txt`
* `simple_ex.txt`
* `1_to_9.txt`
* `greek_alphabet.txt`
* `california.txt`
* `21_nodes.txt`
* `100_nodes.txt`

## Project Structure

### `Node.java`

Represents one node in the search.

Stores:

* `state` — node name
* `cost` — cost from the parent node
* `depth` — level in the search tree
* `parent` — node from which this node was reached

### `BFS.java`

Contains the main BFS logic.

Uses:

* `stateSpace` — graph built from the input file
* `open` — queue of discovered nodes waiting to be processed
* `tree` — expanded nodes
* `path` — final reconstructed path from the start state to the goal state
* `visitedStates` — prevents revisiting nodes
* `bfsEvents` — events used for visualization

### `BFSEvent.java`

Represents one event in the BFS animation.

### `EventStates.java`

Defines all possible event types used by the visualizer.

### `BFSVisualizer.java`

Contains the graphical animation logic and displays the BFS process step by step.

## Additional Notes

* In `open`, nodes are ordered by value and then alphabetically.
* BFS itself expands nodes by level.
* The final displayed path cost is calculated from the edge costs defined in the input file.





