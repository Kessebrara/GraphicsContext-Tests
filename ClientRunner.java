import java.awt.event.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.*;
import javafx.concurrent.*;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.scene.effect.Lighting;
import javafx.scene.effect.Light;
import javafx.scene.effect.Effect;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Affine;





public class ClientRunner extends Application
{
    private int WIDTH = 800;
    private int HEIGHT = 600;

    public static final String SPLASH_IMAGE = "uber_splash.png";
    private static final int SPLASH_WIDTH = 250;
    private static final int SPLASH_HEIGHT = 375;
    private ProgressBar loadProgress;
    private Label progressText;
    private Pane splashLayout;
    private Stage mainStage;
    private Random random = new Random();
    private Canvas canvas = new Canvas(WIDTH, HEIGHT);
    
    
    private Point mouse = new Point();
    private Point center = new Point();
    private Point player = new Point();
    
    Image playerImage = new Image("player.png");
    double angle;

    static long lastNanoTime = System.nanoTime();          
    

    public static void main(String[] args) 
    {
        launch(args);
    }

    @Override
    public void init()
    {
        ImageView splash = new ImageView(new Image(SPLASH_IMAGE));
        loadProgress = new ProgressBar();
        loadProgress.setPrefWidth(SPLASH_WIDTH - 20);
        progressText = new Label("PROGRESS TEXT ?!");
        splashLayout = new VBox();        
        splashLayout.getChildren().addAll(splash, loadProgress, progressText);
        progressText.setAlignment(Pos.CENTER);
        
        splashLayout.setStyle
        (
            "-fx-padding: 5; " +
            "-fx-background-color: cornsilk; " +
            "-fx-border-width:5; " +
            "-fx-border-color: " +
            "linear-gradient(" +
            "to bottom, " +
            "chocolate, " +
            "derive(chocolate, 50%)" +
            ");"
        );
        splashLayout.setEffect(new DropShadow());
    }

    @Override
    public void start(Stage initStage) throws Exception 
    {
        final Task<ObservableList<String>> loadingTask = new Task<ObservableList<String>>()
        {
            @Override
            protected ObservableList<String> call() throws InterruptedException
            {
                ObservableList<String> assetList = FXCollections.<String>observableArrayList();
                ObservableList<String> availableFriends =FXCollections.observableArrayList
                (
                    "Loading world",
                    "Still loading"
                );
                
                updateMessage("Loading processes . . .");
                for (int i = 0; i < availableFriends.size(); i++) {
                    Thread.sleep(400);
                    updateProgress(i + 1, availableFriends.size());
                    String nextFriend = availableFriends.get(i);
                    assetList.add(nextFriend);
                    updateMessage("Assets loading! " + nextFriend);
                }
                Thread.sleep(400);
                updateMessage("Launching game. Good luck!");

                return assetList;
            }
        };
        
        showSplash(initStage, loadingTask,() -> showMainStage(loadingTask.valueProperty()));
        new Thread(loadingTask).start();
    }

    private void showMainStage(ReadOnlyObjectProperty<ObservableList<String>> friends)
    {
        mainStage = new Stage(StageStyle.DECORATED);       
        mainStage.setTitle("Incredible Next Gen Title");
        Group root = new Group();
        Scene scene = new Scene(root);
        mainStage.setScene(scene);
        WIDTH = (int)Screen.getPrimary().getVisualBounds().getWidth();
        HEIGHT = (int)Screen.getPrimary().getVisualBounds().getHeight();
        center = new Point(WIDTH/2, HEIGHT/2);
        canvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add(canvas);
        mainStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        mainStage.setFullScreen(true);
        //scene.setCursor(Cursor.CROSSHAIR); // Currently redraw rate lags behind cursor drawing.
        scene.setCursor(Cursor.NONE);

        final ListView<String> peopleView = new ListView<>();
        peopleView.itemsProperty().bind(friends);

        ArrayList<String> input = new ArrayList<String>();

        // INITIATION

        scene.setOnKeyPressed(new EventHandler<KeyEvent>()
            {
                public void handle(KeyEvent e)
                {
                    if(e.getCode() == e.getCode().ESCAPE)
                    {
                        System.exit(1);
                    }
                }
            });

        scene.setOnKeyReleased(new EventHandler<KeyEvent>()
            {
                public void handle(KeyEvent e)
                {   
                }
            });
        scene.setOnMousePressed(new EventHandler<MouseEvent>()
            {
                public void handle(MouseEvent e)
                {
                }

            });
        scene.setOnMouseDragged(new EventHandler<MouseEvent>()
            {
                public void handle(MouseEvent e)
                {
                }
            });
        scene.setOnMouseMoved(new EventHandler<MouseEvent>()
            {
                public void handle(MouseEvent e)
                {
                    mouse = new Point(e.getX(), e.getY());
                }
            });

        GraphicsContext g = canvas.getGraphicsContext2D();
        
        LongValue lastNanoTime = new LongValue(System.nanoTime());
        
        Effect defaultEffect = g.getEffect(null);
        Affine defaultAffine = g.getTransform();

        new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
                double elapsedTime = (currentNanoTime - lastNanoTime.value) / 1000000000.0;
                lastNanoTime.value = currentNanoTime;  
                
                g.setEffect(defaultEffect);
                g.setTransform(defaultAffine);
                
                angle = Math.toDegrees(player.angle(center));
                Light.Distant light = new Light.Distant();
                light.setAzimuth(angle);
                
                Lighting lighting = new Lighting();
                lighting.setLight(light);
                lighting.setSurfaceScale(8.0);
                
                g.setFill(Color.BLACK);
                g.fillRect(0, 0, WIDTH, HEIGHT);
                
                g.setFill(Color.ORANGE);                
                g.fillText("Hit ESC to close", 16, 16);
                
                g.setEffect(defaultEffect);
                
                g.setFill(Color.WHITE);
                g.fillOval(center.getX() - 16 + 64 * Math.cos(player.angle(center)), 
                    center.getY() - 16 + 64 * Math.sin(player.angle(center)), 32, 32);
                
                g.setEffect(lighting);
                
                if(player.distance(mouse) > elapsedTime * 128)
                player.translatePolar(mouse, elapsedTime * 128);
                
                g.setFill(Color.BLUE);
                g.fillOval(mouse.getX() - 4, mouse.getY() - 4, 8, 8);
                
                g.setFill(Color.RED);
                g.fillOval(center.getX() - 64, center.getY() - 64, 128, 128);
                
                g.translate(player.getX(), player.getY());
                g.rotate(Math.toDegrees(player.angle(mouse)) - 90);
                
                angle = Math.toDegrees(player.angle(center)) - (Math.toDegrees(player.angle(mouse)) - 90);
                light = new Light.Distant();
                light.setAzimuth(angle);
                
                lighting.setLight(light);
                lighting.setSurfaceScale(8.0);
                
                g.setEffect(lighting);
                
                g.drawImage(playerImage, - playerImage.getWidth()/2, 
                    - playerImage.getHeight()/2);
            }
        }.start();

        mainStage.show();

        mainStage.setOnCloseRequest(new EventHandler<WindowEvent>()
            {
                @Override
                public void handle(WindowEvent e)
                {
                    System.exit(1);
                }
            });
    }

    private void showSplash(final Stage initStage, Task<?> task, 
        InitCompletionHandler initCompletionHandler)
    {
        progressText.textProperty().bind(task.messageProperty());
        loadProgress.progressProperty().bind(task.progressProperty());
        task.stateProperty().addListener((observableValue, oldState, newState) -> 
        {
            if (newState == Worker.State.SUCCEEDED) 
            {
                loadProgress.progressProperty().unbind();
                loadProgress.setProgress(1);
                initStage.toFront();
                FadeTransition fadeSplash = new FadeTransition(Duration.seconds(1.2), splashLayout);
                fadeSplash.setFromValue(1.0);
                fadeSplash.setToValue(0.0);
                fadeSplash.setOnFinished(actionEvent -> initStage.hide());
                fadeSplash.play();

                initCompletionHandler.complete();
            } // todo add code to gracefully handle other task states.
        });

        Scene splashScene = new Scene(splashLayout, Color.TRANSPARENT);
        final Rectangle2D bounds = Screen.getPrimary().getBounds();
        initStage.setScene(splashScene);
        initStage.setX(bounds.getMinX() + bounds.getWidth() / 2 - SPLASH_WIDTH / 2);
        initStage.setY(bounds.getMinY() + bounds.getHeight() / 2 - SPLASH_HEIGHT / 2);
        initStage.initStyle(StageStyle.TRANSPARENT);
        initStage.setAlwaysOnTop(true);
        initStage.show();
    }

    public interface InitCompletionHandler
    {
        void complete();
    }

}