package ui;




import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        buildGraph(bfs.getStateSpace());

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
    
    private void buildGraph(Map<String, Map<String, Float>> stateSpace) {
        graphPane.getChildren().clear();
        nodeCircles.clear();

        //collect all nodes
        Set<String> nodes = new TreeSet<>();
        nodes.addAll(stateSpace.keySet());
        for (Map<String, Float> succ : stateSpace.values()) nodes.addAll(succ.keySet());

  
        double cx = 480, cy = 320, r = 250; //big circle
        List<String> list = new ArrayList<>(nodes);

        //create circles
        for (int i = 0; i < list.size(); i++) {
            String n = list.get(i);
            double angle = 2 * Math.PI * i / Math.max(1, list.size());
            double x = cx + r * Math.cos(angle);
            double y = cy + r * Math.sin(angle);
            
            Circle c = new Circle(x, y, 22, Color.WHITE);
            c.setStroke(Color.BLACK);

            nodeCircles.put(n, c);
        }

        //draw edges
        for (Map.Entry<String, Map<String, Float>> entry : stateSpace.entrySet()) {
            String from = entry.getKey();
            for (String to : entry.getValue().keySet()) {
                drawEdge(from, to);
            }
        }

        // add nodes + labels on top
        for (String n : list) {
            Circle c = nodeCircles.get(n);
            Text label = new Text(c.getCenterX() - 6, c.getCenterY() + 5, n);
            graphPane.getChildren().addAll(c, label);
        }
    }
    
    private void drawEdge(String from, String to) {
        Circle a = nodeCircles.get(from);
        Circle b = nodeCircles.get(to);
        if (a == null || b == null) return;

        Line line = new Line(a.getCenterX(), a.getCenterY(), b.getCenterX(), b.getCenterY());
        line.setStroke(Color.LIGHTGRAY);

        // edges go first so they stay behind nodes
        graphPane.getChildren().add(line);
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
