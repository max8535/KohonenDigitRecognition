package com.example.diplom;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;

import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.MenuItem;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


import static javafx.scene.paint.Color.WHITE;



public class MainController {

    public Kohonen kohonen_network;

    public BufferedImage loaded_image;
    public int counter;

    public File[] test_images;
    public int test_counter = 0;
    public int TP=0;

    public int accuracy_counter=0;

    public double[] accuracies = new double[10];

    @FXML
    private Pane MainPane;
    @FXML
    private ImageView loaded_image_view;
    @FXML
    private ImageView Identified_image_view;
    @FXML
    private Label accuracy_label;

    @FXML
    private MenuItem learning_button;

    public static Image convert(BufferedImage bufferedImage) {
        WritableImage wr = null;
        if (bufferedImage != null) {
            wr = new WritableImage(bufferedImage.getWidth(), bufferedImage.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                for (int y = 0; y < bufferedImage.getHeight(); y++) {
                    pw.setArgb(x, y, bufferedImage.getRGB(x, y));
                }
            }
        }
        return wr;
    }

    public static int listFiles(File root, File[] files, int index) {
        File[] rootFiles = root.listFiles();

        if (rootFiles == null) return index;

        for (File file : rootFiles) {
            if (file.isDirectory()) {
                index = listFiles(file, files, index);
            } else {
                files[index++] = file;
            }
        }

        return index;
    }

    public static double[] convertBMPToBinaryVector(File bmpFile) throws IOException {
        BufferedImage image = ImageIO.read(bmpFile);
        double[] binaryVector = new double[784];
        int counter = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y));
                 if (color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0) {
                    binaryVector[counter] = 0;
                    counter++;
                } else {
                    binaryVector[counter] = 1;
                    counter++;
                }

            }
        }
        return binaryVector;

    }

    public static BufferedImage realVectorToImage(double[] realVector, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (double value : realVector) {
            if (value < min) min = value;
            if (value > max) max = value;
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = y * width + x;
                int grayValue = (int) ((realVector[index] - min) / (max - min) * 255);

                int pixelValue = (grayValue << 16) | (grayValue << 8) | grayValue;
                image.setRGB(x, y, pixelValue);
            }
        }

        return image;
    }
    @FXML
    protected void Network_create() {
        kohonen_network = new Kohonen();
        learning_button.setDisable(false);
    }

    @FXML
    protected void ImageLoadClick() {
        FileChooser filechooser = new FileChooser();
        filechooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("BMP", "*.bmp"));
        File file = filechooser.showOpenDialog(new Stage());
        try {
        loaded_image = ImageIO.read(file);

            BufferedImage originalImage = loaded_image;

            BufferedImage resizedImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = resizedImage.createGraphics();

            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(originalImage, 0, 0, 200, 200, null);
            g.dispose();

            Image image = convert(resizedImage);
            loaded_image_view.setImage(image);

        } catch (IOException e) {
            System.out.println("Ошибка при обработке изображения: " + e.getMessage());
        }
    }

    @FXML
    protected void Learning_500() throws IOException {


        File[] images = new File[1000];
        listFiles(new File("D:\\Mnist_1000"), images, 0);

        for (int i = 0; i < kohonen_network.getEPOCHS(); i++) {
            for (int j = 0; j < kohonen_network.getLearning_pool_size(); j++) {
                kohonen_network.trainWTA(convertBMPToBinaryVector(images[j]));
            }
        }
    }
    @FXML
    protected void Save_network() throws IOException {
        FileUtils.Save_Network_JSON(kohonen_network);
    }
    @FXML
    protected void Load_network() throws IOException {
        kohonen_network =  FileUtils.Load_Network_JSON();
        learning_button.setDisable(false);
    }

    @FXML
    protected void openDrawingPanel() {
        Stage drawingStage = new Stage();

        Canvas canvas = new Canvas(400, 400);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(javafx.scene.paint.Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setStroke(WHITE);
        gc.setLineWidth(2);

        Slider lineWidthSlider = new Slider(1, 40, 2);
        lineWidthSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            gc.setLineWidth(newValue.doubleValue());
        });

        canvas.setOnMousePressed(e -> {
            gc.beginPath();
            gc.lineTo(e.getX(), e.getY());
            gc.stroke();
        });

        canvas.setOnMouseDragged(e -> {
            gc.lineTo(e.getX(), e.getY());
            gc.stroke();
        });

        canvas.setOnMouseReleased(e -> {
            gc.closePath();
        });

        VBox root = new VBox(10);
        root.getChildren().addAll(canvas, lineWidthSlider);

        Scene scene = new Scene(root, 400, 430);

        drawingStage.setTitle("Drawing Panel");
        drawingStage.setScene(scene);
        drawingStage.show();

        drawingStage.setOnCloseRequest(event -> {
            Image image = canvas.snapshot(null, null);
            onDrawingPanelClosed(loaded_image_view, image);
            loaded_image = SwingFXUtils.fromFXImage(image,null);
            BufferedImage resizedImage = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = resizedImage.createGraphics();

            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.drawImage(loaded_image, 0, 0, 28, 28, null);
            g.dispose();

            loaded_image = resizedImage;
        });

    }
    public void onDrawingPanelClosed(ImageView imageView, Image image) {
        imageView.setImage(image);
    }
    @FXML
    protected void WeightView() throws IOException {
        double[] binaryVector = new double[784];
        int counter = 0;
        for (int y = 0; y < loaded_image.getHeight(); y++) {
            for (int x = 0; x < loaded_image.getWidth(); x++) {
                Color color = new Color(loaded_image.getRGB(x, y));
               if (color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0) {
                    binaryVector[counter] = 0;
                    counter++;
                } else {
                    binaryVector[counter] = 1;
                    counter++;
                }
            }
        }
        Neuron winner = kohonen_network.getNeurons().get(kohonen_network.recognize(binaryVector));
        Image weight_image = convert(realVectorToImage(winner.getWeights(), 28, 28));
        Identified_image_view.setImage(weight_image);

    }

    @FXML
    protected void openEdit() {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setTitle("Введите значения");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(5);
        grid.setVgap(5);

        Label label1 = new Label("Количество эпох обучения");
        TextField textField1 = new TextField();
        textField1.setText(String.valueOf(kohonen_network.getEPOCHS()));

        Label label2 = new Label("Размер обучающей выборки");
        TextField textField2 = new TextField();
        textField2.setText(String.valueOf(kohonen_network.getLearning_pool_size()));

        javafx.scene.control.Label label3 = new Label("Коэффициент обучения");
        TextField textField3 = new TextField();
        textField3.setText(String.valueOf(kohonen_network.getN()));

        grid.add(label1, 0, 0);
        grid.add(textField1, 1, 0);
        grid.add(label2, 0, 1);
        grid.add(textField2, 1, 1);
        grid.add(label3, 0, 2);
        grid.add(textField3, 1, 2);

        Button closeButton = new Button("OK");
        closeButton.setOnAction(e -> {
            try {
                kohonen_network.setEPOCHS(Integer.parseInt(textField1.getText()));
                kohonen_network.setLearning_pool_size(Integer.parseInt(textField2.getText()));
                kohonen_network.setN(Double.parseDouble(textField3.getText()));
                dialogStage.close();
            } catch (NumberFormatException ex) {

                ex.printStackTrace();
            }
        });

        grid.add(closeButton, 0, 3, 2, 1);

        Scene dialogScene = new Scene(grid, 300, 150);
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }
    @FXML
    protected void test() throws IOException {
        test_images = new File[100];
        listFiles(new File("D:\\Test_100"), test_images, 0);

        loaded_image = ImageIO.read(test_images[0]);

        BufferedImage originalImage = loaded_image;

        BufferedImage resizedImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(originalImage, 0, 0, 200, 200, null);
        g.dispose();

        Image image = convert(resizedImage);
        loaded_image_view.setImage(image);

        double[] binaryVector = new double[784];
        int counter = 0;
        for (int y = 0; y < loaded_image.getHeight(); y++) {
            for (int x = 0; x < loaded_image.getWidth(); x++) {
                Color color = new Color(loaded_image.getRGB(x, y));
                if (color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0) {
                    binaryVector[counter] = 0;
                    counter++;
                } else {
                    binaryVector[counter] = 1;
                    counter++;
                }
            }
        }
        Neuron winner = kohonen_network.getNeurons().get(kohonen_network.recognize(binaryVector));
        Image weight_image = convert(realVectorToImage(winner.getWeights(), 28, 28));
        Identified_image_view.setImage(weight_image);

    }
    @FXML
    protected void yes_step() throws IOException {

        TP++;
        test_counter++;
        if(test_counter % 10 ==0){
            double accuracy = (double) TP /10;
            accuracies[accuracy_counter] = accuracy;
            accuracy_label.setText(String.format("Accuracy = %.4f",accuracy));
            accuracy_counter++;
            TP=0;
        }
        if(test_counter==100){
            accuracy_label.setText("Конец");
        }
        loaded_image = ImageIO.read(test_images[test_counter]);

        BufferedImage originalImage = loaded_image;

        BufferedImage resizedImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(originalImage, 0, 0, 200, 200, null);
        g.dispose();

        Image image = convert(resizedImage);
        loaded_image_view.setImage(image);

        double[] binaryVector = new double[784];
        int counter = 0;
        for (int y = 0; y < loaded_image.getHeight(); y++) {
            for (int x = 0; x < loaded_image.getWidth(); x++) {
                Color color = new Color(loaded_image.getRGB(x, y));
                if (color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0) {
                    binaryVector[counter] = 0;
                    counter++;
                } else {
                    binaryVector[counter] = 1;
                    counter++;
                }
            }
        }
        Neuron winner = kohonen_network.getNeurons().get(kohonen_network.recognize(binaryVector));
        Image weight_image = convert(realVectorToImage(winner.getWeights(), 28, 28));
        Identified_image_view.setImage(weight_image);
    }

    @FXML
    protected void no_step() throws IOException {

        test_counter++;
        if(test_counter % 10 ==0){
            double accuracy = (double) TP /10;
            accuracies[accuracy_counter] = accuracy;
            accuracy_label.setText(String.format("Accuracy = %.4f",accuracy));
            accuracy_counter++;
            TP=0;
        }
        if(test_counter==100){
            accuracy_label.setText("Конец");
        }
        loaded_image = ImageIO.read(test_images[test_counter]);

        BufferedImage originalImage = loaded_image;

        BufferedImage resizedImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(originalImage, 0, 0, 200, 200, null);
        g.dispose();

        Image image = convert(resizedImage);
        loaded_image_view.setImage(image);

        double[] binaryVector = new double[784];
        int counter = 0;
        for (int y = 0; y < loaded_image.getHeight(); y++) {
            for (int x = 0; x < loaded_image.getWidth(); x++) {
                Color color = new Color(loaded_image.getRGB(x, y));
                if (color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0) {
                    binaryVector[counter] = 0;
                    counter++;
                } else {
                    binaryVector[counter] = 1;
                    counter++;
                }
            }
        }
        Neuron winner = kohonen_network.getNeurons().get(kohonen_network.recognize(binaryVector));
        Image weight_image = convert(realVectorToImage(winner.getWeights(), 28, 28));
        Identified_image_view.setImage(weight_image);
    }

}