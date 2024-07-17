package com.example.diplom;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;



import javax.swing.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {

    public static void Save_Network_JSON(Kohonen network) {

        Gson gson = new Gson();

        JFileChooser fileChooser = new JFileChooser();

        int result = fileChooser.showSaveDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getPath();

            try (FileWriter writer = new FileWriter(filePath)) {
                gson.toJson(network, writer);
                JOptionPane.showMessageDialog(null, "Объект успешно сохранен в файле " + filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Отменено пользователем");
        }
    }
    public static Kohonen Load_Network_JSON() {
        Kohonen network=null;
        JFileChooser fileChooser = new JFileChooser();

        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try (FileReader reader = new FileReader(selectedFile)) {

                Gson gson = new GsonBuilder().create();

                network = gson.fromJson(reader, Kohonen.class);


            } catch (IOException | JsonSyntaxException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Выбран файл неверного формата или поврежденный файл");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Отменено пользователем");
        }
        return network;
    }
}
