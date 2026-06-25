package ui;




import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class BFSVisualizer extends Application {//Application - will call start(Stage) automatically
	//stage = the window
	//scene = the contents inside the window
	
	
    private final Pane graphPane = new Pane();
    private final Map<String, Circle> nodeCircles = new HashMap<>();

    //--- Animation ---
    private final List<BFSEvent> events = new ArrayList<>(); //ENQUEUE, DEQUEUE, VISITED...
    private int eventIndex = 0;
    private boolean isPlaying = false;

    @Override
    public void start(Stage stage) {
    	
        BFS bfs = new BFS();
        String[] args = getParameters().getRaw().toArray(new String[0]);
        bfs.run(args);

        //border -> easy to get to the top/center/bottom/left/right regions
        BorderPane root = new BorderPane();
        root.setCenter(graphPane);

        Button stepBtn = new Button("Step");
        Button playBtn = new Button("Play");
        Button resetBtn = new Button("Reset");

        HBox controls = new HBox(10, stepBtn, playBtn, resetBtn); //10px spacing betw them
        root.setBottom(controls);

        //draw graph
        buildGraph(bfs.getStateSpace(), bfs.getStartState());

        //events.clear();
        events.addAll(bfs.getBfsEvents());
        reset(); //reset event index and node color

        
        stepBtn.setOnAction(e -> applyNextEvent());
        playBtn.setOnAction(e -> {
            isPlaying = !isPlaying;
            playBtn.setText(isPlaying ? "Pause" : "Play");
            if (isPlaying) playLoop();
        });
        resetBtn.setOnAction(e -> reset());

        //Creates the scene from root layout with window size 960×720
        stage.setScene(new Scene(root, 960, 720));
        stage.setTitle("BFS Visualizer");
        stage.show();
        
    }
    
    
    
    private void buildGraph(Map<String, Map<String, Float>> stateSpace, String startState) {
        graphPane.getChildren().clear();
        nodeCircles.clear();

        // 1) Collect all nodes
        Set<String> allNodes = new TreeSet<>();
        allNodes.addAll(stateSpace.keySet());
        for (Map<String, Float> succ : stateSpace.values()) {
            allNodes.addAll(succ.keySet());
        }

        // 2) Compute BFS depth levels starting from the real root
        Map<String, Integer> depthMap = new HashMap<>();
        Map<Integer, List<String>> levels = new HashMap<>();

        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.add(startState);
        visited.add(startState);
        depthMap.put(startState, 0);
        levels.computeIfAbsent(0, k -> new ArrayList<>()).add(startState);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            int currentDepth = depthMap.get(current);

            Map<String, Float> children = stateSpace.get(current);
            if (children == null) continue;

            // alphabetical order of children
            Map<String, Float> sortedChildren = new java.util.TreeMap<>(children);

            for (String child : sortedChildren.keySet()) {
                if (visited.contains(child)) continue;

                visited.add(child);
                queue.add(child);

                int childDepth = currentDepth + 1;
                depthMap.put(child, childDepth);
                levels.computeIfAbsent(childDepth, k -> new ArrayList<>()).add(child);
            }
        }

        // 3) Put unreachable nodes on the last level
        int maxDepth = levels.keySet().stream().max(Integer::compareTo).orElse(0);
        for (String node : allNodes) {
            if (!depthMap.containsKey(node)) {
                levels.computeIfAbsent(maxDepth + 1, k -> new ArrayList<>()).add(node);
            }
        }

        // 4) Place nodes level by level
        double sceneWidth = 960;
        double topMargin = 80;
        double levelGap = 120;

        for (Map.Entry<Integer, List<String>> entry : levels.entrySet()) {
            int depth = entry.getKey();
            List<String> nodesAtLevel = entry.getValue();
            nodesAtLevel.sort(String::compareTo);

            double y = topMargin + depth * levelGap;
            int count = nodesAtLevel.size();

            for (int i = 0; i < count; i++) {
                String node = nodesAtLevel.get(i);

                double x;
                if (count == 1) {
                    x = sceneWidth / 2.0;
                } else {
                    x = 100 + i * ((sceneWidth - 200) / (count - 1.0));
                }

                Circle c = new Circle(x, y, 22, Color.WHITE);
                c.setStroke(Color.BLACK);
                nodeCircles.put(node, c);
            }
        }

        // 5) Draw edges
        for (Map.Entry<String, Map<String, Float>> entry : stateSpace.entrySet()) {
            String from = entry.getKey();
            for (String to : entry.getValue().keySet()) {
                drawEdge(from, to);
            }
        }

        // 6) Draw circles and labels
        for (String node : nodeCircles.keySet()) {
            Circle c = nodeCircles.get(node);

            Text label = new Text(node);
            label.setX(c.getCenterX() - 4 * node.length());
            label.setY(c.getCenterY() + 5);

            graphPane.getChildren().addAll(c, label);
        }
    }
    
    private void drawEdge(String from, String to) {
        Circle a = nodeCircles.get(from);
        Circle b = nodeCircles.get(to);
        if (a == null || b == null) return;

        double startX = a.getCenterX();
        double startY = a.getCenterY();
        double endX = b.getCenterX();
        double endY = b.getCenterY();

        double dx = endX - startX;
        double dy = endY - startY;
        double angle = Math.atan2(dy, dx);

        double radius = a.getRadius(); // assumes both circles have same radius

        // move start point to edge of source circle
        double lineStartX = startX + radius * Math.cos(angle);
        double lineStartY = startY + radius * Math.sin(angle);

        // move end point to edge of target circle
        double lineEndX = endX - radius * Math.cos(angle);
        double lineEndY = endY - radius * Math.sin(angle);

        Line line = new Line(lineStartX, lineStartY, lineEndX, lineEndY);
        line.setStroke(Color.GRAY);
        line.setStrokeWidth(2);

        double arrowLength = 14;
        double arrowAngle = Math.PI / 7;

        double x1 = lineEndX - arrowLength * Math.cos(angle - arrowAngle);
        double y1 = lineEndY - arrowLength * Math.sin(angle - arrowAngle);

        double x2 = lineEndX - arrowLength * Math.cos(angle + arrowAngle);
        double y2 = lineEndY - arrowLength * Math.sin(angle + arrowAngle);

        Line arrow1 = new Line(lineEndX, lineEndY, x1, y1);
        Line arrow2 = new Line(lineEndX, lineEndY, x2, y2);

        arrow1.setStroke(Color.GRAY);
        arrow2.setStroke(Color.GRAY);
        arrow1.setStrokeWidth(2);
        arrow2.setStrokeWidth(2);

        graphPane.getChildren().addAll(line, arrow1, arrow2);
    }
    
    private void reset() {
        eventIndex = 0;
        isPlaying = false;
        nodeCircles.values().forEach(c -> c.setFill(Color.WHITE));
    }
    
    private boolean applyNextEvent() {
        if (eventIndex >= events.size()) return false;

        BFSEvent ev = events.get(eventIndex++);
        Circle c = nodeCircles.get(ev.value);
        if (c == null) return true;

        switch (ev.state) {
            case ENQUEUE -> c.setFill(Color.KHAKI);
            case DEQUEUE -> c.setFill(Color.LIGHTBLUE);
            case VISITED -> c.setFill(Color.LIGHTGREEN);
            case GOAL_FOUND -> c.setFill(Color.ORANGERED);
            case PATH -> c.setFill(Color.RED);
        }
        return true;
    }
    
    private void playLoop() {
        if (!isPlaying) return;

        if (!applyNextEvent()) {
            isPlaying = false;
            return;
        }

        PauseTransition pause = new PauseTransition(Duration.seconds(0.6));
        pause.setOnFinished(e -> playLoop());
        pause.play();
    }


    public static void main(String[] args) {
    	launch(args);
    }
}
