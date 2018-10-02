package sample;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Main extends Application {
    public Canvas canvas;

    public Slider stepsSlider;
    public Slider precisionSlider;
    public Slider shiftMinSlider;
    public Slider shiftMaxSlider;
    public Slider mouseClickMinSlider;
    public Slider mouseClickMaxSlider;
    public Slider mouseMoveMinSlider;
    public Slider mouseMoveMaxSlider;
    public Slider blurSlider;
    public Slider thresholdSlider;

    public CheckBox shuffleCheckBox;
    public CheckBox clickCheckBox;
    public CheckBox checkCheckBox;
    public ComboBox renderingModeComboBox;
    public ComboBox differenceModeComboBox;

    public TextField verticalWidthInput;
    public TextField verticalHeightInput;
    public TextField horizontalWidthInput;
    public TextField horizontalHeightInput;

    public TextField pivotXInput;
    public TextField pivotYInput;

    public TextField verticalXOffsetInput;
    public TextField verticalYOffsetInput;
    public TextField horizontalXOffsetInput;
    public TextField horizontalYOffsetInput;

    private JSONObject config;
    private Robot robot;
    private String configPath = System.getProperty("user.home") + File.separator + "PictureDifferenceBot.cfg";
    private Random random = new Random();
    private GraphicsContext graphicsContext;

    @Override
    public void start(Stage stage) throws Exception{
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("sample.fxml"))));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void initialize() {
        try {
            graphicsContext = canvas.getGraphicsContext2D();
            robot = new Robot();

            if (new File(configPath).exists()) {
                readConfig();
            }
            else {
                saveConfig();
            }
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        System.out.println("Saving config...");

        try {
            config = new JSONObject();

            config.put("pivotX", Integer.parseInt(pivotXInput.getText()));
            config.put("pivotY", Integer.parseInt(pivotYInput.getText()));

            config.put("verticalXOffset", Integer.parseInt(verticalXOffsetInput.getText()));
            config.put("verticalYOffset", Integer.parseInt(verticalYOffsetInput.getText()));
            config.put("horizontalXOffset", Integer.parseInt(horizontalXOffsetInput.getText()));
            config.put("horizontalYOffset", Integer.parseInt(horizontalYOffsetInput.getText()));

            config.put("verticalWidth", Integer.parseInt(verticalWidthInput.getText()));
            config.put("verticalHeight", Integer.parseInt(verticalHeightInput.getText()));
            config.put("horizontalWidth", Integer.parseInt(horizontalWidthInput.getText()));
            config.put("horizontalHeight", Integer.parseInt(horizontalHeightInput.getText()));

            config.put("threshold", thresholdSlider.getValue());
            config.put("blur", blurSlider.getValue());
            config.put("steps", stepsSlider.getValue());
            config.put("precision", precisionSlider.getValue());

            config.put("clickMin", mouseClickMinSlider.getValue());
            config.put("clickMax", mouseClickMaxSlider.getValue());
            config.put("shiftMin", shiftMinSlider.getValue());
            config.put("shiftMax", shiftMaxSlider.getValue());
            config.put("moveMin", mouseMoveMinSlider.getValue());
            config.put("moveMax", mouseMoveMaxSlider.getValue());

            config.put("shuffle", shuffleCheckBox.isSelected());
            config.put("click", clickCheckBox.isSelected());
            config.put("check", checkCheckBox.isSelected());

            config.put("renderingMode", renderingModeComboBox.getSelectionModel().getSelectedIndex());
            config.put("differenceMode", differenceModeComboBox.getSelectionModel().getSelectedIndex());

            FileWriter fileWriter = new FileWriter(configPath);
            fileWriter.write(config.toString(2));
            fileWriter.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void readConfig() {
        try {
            config = new JSONObject(String.join("\n", Files.readAllLines(Paths.get(configPath))));

            pivotXInput.setText(Integer.toString(config.getInt("pivotX")));
            pivotYInput.setText(Integer.toString(config.getInt("pivotY")));

            verticalXOffsetInput.setText(Integer.toString(config.getInt("verticalXOffset")));
            verticalYOffsetInput.setText(Integer.toString(config.getInt("verticalYOffset")));
            horizontalXOffsetInput.setText(Integer.toString(config.getInt("horizontalXOffset")));
            horizontalYOffsetInput.setText(Integer.toString(config.getInt("horizontalYOffset")));

            verticalWidthInput.setText(Integer.toString(config.getInt("verticalWidth")));
            verticalHeightInput.setText(Integer.toString(config.getInt("verticalHeight")));

            horizontalWidthInput.setText(Integer.toString(config.getInt("horizontalWidth")));
            horizontalHeightInput.setText(Integer.toString(config.getInt("horizontalHeight")));

            thresholdSlider.setValue(config.getDouble("threshold"));
            blurSlider.setValue(config.getDouble("blur"));
            precisionSlider.setValue(config.getDouble("precision"));
            stepsSlider.setValue(config.getDouble("steps"));

            shiftMinSlider.setValue(config.getDouble("shiftMin"));
            shiftMaxSlider.setValue(config.getDouble("shiftMax"));
            mouseMoveMinSlider.setValue(config.getDouble("moveMin"));
            mouseMoveMaxSlider.setValue(config.getDouble("moveMax"));
            mouseClickMinSlider.setValue(config.getDouble("clickMin"));
            mouseClickMaxSlider.setValue(config.getDouble("clickMax"));

            shuffleCheckBox.setSelected(config.getBoolean("shuffle"));
            clickCheckBox.setSelected(config.getBoolean("click"));
            checkCheckBox.setSelected(config.getBoolean("check"));

            renderingModeComboBox.getSelectionModel().select(config.getInt("renderingMode"));
            differenceModeComboBox.getSelectionModel().select(config.getInt("differenceMode"));
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private double randomDouble(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    private double xClick = 0, yClick = 0;
    public void onCanvasClick(MouseEvent mouseEvent) throws InterruptedException, JSONException {
        xClick = mouseEvent.getScreenX();
        yClick = mouseEvent.getScreenY();

        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            boolean pizda = canvas.getWidth() >= canvas.getHeight();
            double widthElda = pizda ? config.getInt("horizontalWidth") : config.getInt("verticalWidth");
            double heightElda = pizda ? config.getInt("horizontalHeight") : config.getInt("verticalHeight");

            java.awt.Point point = MouseInfo.getPointerInfo().getLocation();
            robot.mouseMove(
                (int) (config.getInt("pivotX") + ((mouseEvent.getSceneX() - canvas.getLayoutX()) / (canvas.getWidth() * canvas.getScaleX()) * widthElda)),
                (int) (config.getInt("pivotY") + ((mouseEvent.getSceneY() - canvas.getLayoutY()) / (canvas.getHeight() * canvas.getScaleY()) * heightElda))
            );
            click();
            robot.mouseMove(point.x, point.y);
        }
    }

    public void onCanvasDrag(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            canvas.setLayoutX(canvas.getLayoutX() + mouseEvent.getScreenX() - xClick);
            canvas.setLayoutY(canvas.getLayoutY() + mouseEvent.getScreenY() - yClick);
            xClick = mouseEvent.getScreenX();
            yClick = mouseEvent.getScreenY();
        }
    }

    public void onCanvasScroll(ScrollEvent scrollEvent) {
        double m = scrollEvent.getDeltaY() > 0 ? 1.1 : 0.9;
        canvas.setScaleX(canvas.getScaleX() * m);
        canvas.setScaleY(canvas.getScaleY() * m);
    }

    private boolean[][] blur(boolean[][] map, int radius) {
        boolean[][] newMap = new boolean[map.length][map[0].length];

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x]) {
                    for (int j = y - radius; j <= y + radius; j++) {
                        for (int i = x - radius; i <= x + radius; i++) {
                            if (j >= 0 && j < map.length && i >= 0 && i < map[0].length) {
                                newMap[j][i] = true;
                            }
                        }
                    }
                }
                else {
                    newMap[y][x] = false;
                }
            }
        }

        return newMap;
    }

    private int xMin, yMin, xMax, yMax;
    private boolean[][] map;
    private void cyka(int x, int y) {
        if (y >= 0 && y < map.length && x >= 0 && x < map[0].length && map[y][x]) {
            map[y][x] = false;

            xMin = Math.min(xMin, x);
            yMin = Math.min(yMin, y);

            xMax = Math.max(xMax, x);
            yMax = Math.max(yMax, y);

            processPoint(x, y);
        }
    }

    private void processPoint(int x, int y) {
        cyka(x, y - 1);
        cyka(x + 1, y);
        cyka(x, y + 1);
        cyka(x - 1, y);
    }

    private ArrayList<Rectangle> detect() {
        ArrayList<Rectangle> list = new ArrayList<>();

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x]) {
                    xMin = x;
                    yMin = y;
                    xMax = x;
                    yMax = y;
                    map[y][x] = false;
                    processPoint(x, y);

                    list.add(new Rectangle(xMin, yMin, xMax - xMin + 1, yMax - yMin + 1));
                }
            }
        }

        return list;
    }

    class Cyka extends Thread {
        Point[] points;
        boolean doubleClick;

        Cyka(Point[] points, boolean doubleClick) {
            this.points = points;
            this.doubleClick = doubleClick;
        }

        @Override
        public void run() {
            try {
                long start, delay = (long) (1000000000L * randomDouble(config.getDouble("moveMin"), config.getDouble("moveMax")) / points.length);
                for (Point point : points) {
                    start = System.nanoTime();
                    robot.mouseMove((int) point.x, (int) point.y);
                    while (System.nanoTime() - start < delay) ;
                }

                if (doubleClick) click();
                click();

                sleep((long) (randomDouble(config.getDouble("clickMin"), config.getDouble("clickMax")) * 1000));
            } catch (InterruptedException | JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getDifference(BufferedImage half1, BufferedImage half2, double mouseX, double mouseY) throws JSONException {
        int width = half1.getWidth(), height = half1.getHeight();
        canvas.setWidth(width);
        canvas.setHeight(height);
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRect(1, 1, width, height);

        map = new boolean[height][width];

        double threshold = config.getDouble("threshold");
        int int1, int2, r1, g1, b1, r2, g2, b2, average, renderingMode = renderingModeComboBox.getSelectionModel().getSelectedIndex();

        for (int y = 0; y < height; y++) {
            if (y < half2.getHeight()) {
                for (int x = 0; x < width; x++) {
                    if (x < half2.getWidth()) {
                        int1 = half1.getRGB(x, y);
                        int2 = half2.getRGB(x, y);

                        r1 = int1 >> 16 & 0xFF;
                        g1 = int1 >> 8 & 0xFF;
                        b1 = int1 & 0xFF;

                        r2 = int2 >> 16 & 0xFF;
                        g2 = int2 >> 8 & 0xFF;
                        b2 = int2 & 0xFF;

                        average = (Math.abs(r1 - r2) + Math.abs(g1 - g2) +  Math.abs(b1 - b2)) / 3;

                        map[y][x] = average > threshold;

                        if (renderingMode == 0) {
                            graphicsContext.setFill(Color.rgb(r1 / 2, g1 / 2, b1 / 2));
                        }
                        else if (renderingMode == 2) {
                            graphicsContext.setFill(Color.rgb(average, 0, 0));
                        }

                        graphicsContext.fillRect(x, y, 1, 1);
                    }
                }
            }
        }

        map = blur(map, (int) blurSlider.getValue());

        if (renderingMode == 0 || renderingMode == 1) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (map[y][x]) {
                        graphicsContext.setFill(Color.RED);
                        graphicsContext.fillRect(x, y, 1, 1);
                    }
                }
            }
        }

        if (checkCheckBox.isSelected()) {
            ArrayList<Rectangle> list = detect();
            if (config.getBoolean("shuffle")) Collections.shuffle(list);

            graphicsContext.setFont(new Font("Arial", 10));
            graphicsContext.setLineWidth(1);

            graphicsContext.setStroke(Color.WHITE);
            for (Rectangle rectangle : list) {
                graphicsContext.strokeRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            }

            int xScreen = config.getInt("pivotX");
            int yScreen = config.getInt("pivotY");
            Point cursorPosition = new Point(mouseX, mouseY);

            graphicsContext.setFill(Color.WHITE);
            for (int i = 0; i < list.size(); i++) {
                Rectangle rectangle = list.get(i);

                graphicsContext.fillText(Integer.toString(i + 1), rectangle.x + 2, rectangle.y + 10);

                Point nextCursorPosition = new Point(
                    xScreen + rectangle.x + rectangle.width / 2,
                    yScreen + rectangle.y + rectangle.height / 2
                );

                int cyka = (int) randomDouble(config.getDouble("shiftMin"), config.getDouble("shiftMax"));
                Point[] points = eblo(cursorPosition, nextCursorPosition, -cyka, cyka, (int) config.getDouble("steps"));
                points = getBezierPoints(points, config.getDouble("precision") / 1000d);

                for (Point point : points) {
                    graphicsContext.fillRect(point.x - xScreen + 1, point.y - yScreen + 1, 2, 2);
                }

                if (clickCheckBox.isSelected()) {
                    boolean doubleClick = i == 0;
                    Point[] finalPoints = points;
                    Platform.runLater(() -> new Cyka(finalPoints, doubleClick).run());

                }

                cursorPosition = nextCursorPosition;
            }
        }
    }

    public void onGoButtonClicked(MouseEvent mouseEvent) throws JSONException, InterruptedException {
        int differenceMode = differenceModeComboBox.getSelectionModel().getSelectedIndex();

        int width = config.getInt(differenceMode == 0 ? "verticalWidth" : "horizontalWidth");
        int height = config.getInt(differenceMode == 0 ? "verticalHeight" : "horizontalHeight");

        int x1 = config.getInt("pivotX");
        int y1 = config.getInt("pivotY");

        int x2 = x1 + config.getInt(differenceMode == 0 ? "verticalXOffset" : "horizontalXOffset");
        int y2 = y1 + config.getInt(differenceMode == 0 ? "verticalYOffset" : "horizontalYOffset");

        getDifference(
            robot.createScreenCapture(new Rectangle(x1, y1, width, height)),
            robot.createScreenCapture(new Rectangle(x2, y2, width, height)),
            mouseEvent.getScreenX(),
            mouseEvent.getSceneY()
        );
    }


    private boolean checkTextField(TextField textField) {
        return textField.getText().matches("\\d+");
    }

    public void checkTextFields() {
        if (
            checkTextField(verticalWidthInput) &&
            checkTextField(verticalHeightInput) &&
            checkTextField(horizontalWidthInput) &&
            checkTextField(horizontalHeightInput) &&
            checkTextField(pivotXInput) &&
            checkTextField(pivotYInput) &&
            checkTextField(verticalXOffsetInput) &&
            checkTextField(verticalYOffsetInput) &&
            checkTextField(horizontalXOffsetInput) &&
            checkTextField(horizontalYOffsetInput)
        ) {
            saveConfig();
        }
        else {
            Window window = canvas.getScene().getWindow();
            int offset = 25, speed = 80;
            double x = window.getX();

            DoubleProperty windowXProperty = new SimpleDoubleProperty(x);
            windowXProperty.addListener((obs, oldValue, newValue) -> window.setX(newValue.doubleValue()));

            Timeline timeline = new Timeline();
            timeline.getKeyFrames().addAll(
                new KeyFrame(new Duration(0), new KeyValue(windowXProperty, x - offset)),
                new KeyFrame(new Duration(speed), new KeyValue(windowXProperty, x + offset)),
                new KeyFrame(new Duration(2 * speed), new KeyValue(windowXProperty, x - offset)),
                new KeyFrame(new Duration(3 * speed), new KeyValue(windowXProperty, x))
            );
            timeline.play();
        }
    }

    private void checkSlidersRange(Slider min, Slider max) {
        double minValue = min.getValue(), maxValue = max.getValue();
        if (minValue > maxValue) {
            max.setValue(minValue);
        }
    }

    public void checkSliders() {
        checkSlidersRange(shiftMinSlider, shiftMaxSlider);
        checkSlidersRange(mouseClickMinSlider, mouseClickMaxSlider);
        checkSlidersRange(mouseMoveMinSlider, mouseMoveMaxSlider);
        saveConfig();
    }

    private void click() throws InterruptedException {
        robot.mousePress(InputEvent.BUTTON1_MASK);
        Thread.sleep(300);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    private class Point {
        double x, y;
        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    private Point getParametrizedPoint(Point from, Point to, double time) {
        return new Point(
            from.x + (to.x - from.x) * time,
            from.y + (to.y - from.y) * time
        );
    }

    private Point getMainPoint(Point[] points, double time) {
        if (points.length > 1) {
            Point[] connectionPoints = new Point[points.length - 1];

            for (int i = 0; i < points.length - 1; i++) {
                connectionPoints[i] = getParametrizedPoint(points[i], points[i + 1], time);
            }

            return getMainPoint(connectionPoints, time);
        }
        else {
            return points[0];
        }
    }

    private Point[] getBezierPoints(Point[] points, double precision) {
        int index = 0;
        Point[] bezierPoints = new Point[(int) Math.ceil(1 / precision)];

        for (double time = 0.0d; time < 1.0d; time += precision) {
            bezierPoints[index] = getMainPoint(points, time);
            index++;
        }

        return bezierPoints;
    }

    private Point[] eblo(Point from, Point to, double offsetMin, double offsetMax, int times) {
        Point[] points = new Point[times + 2];
        points[0] = from;

        int index = 1;
        double step = 1.0d / (times + 1);
        double time = step;

        for (int i = 0; i < times; i++) {
            points[index] = getParametrizedPoint(from, to, time);
            points[index].x += randomDouble(offsetMin, offsetMax);
            points[index].y += randomDouble(offsetMin, offsetMax);

            time += step;
            index++;
        }

        points[index] = to;

        return points;
    }
}
