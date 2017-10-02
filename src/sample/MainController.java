package sample;

import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public class MainController {

    public Label alphabet_text_view;
    public Label is_probabilities_ready_text_view;
    public Label is_tree_built_text_view;
    public Label string_to_encode_text_view;
    public Label encoded_string;
    public Label K_text_view;
    public Label decoded_text_text_view;
    private HuffmanTree huffmanTree;
    private String alphabet;
    private ArrayList<Double> probabilities;
    private String stringToEncode;

    private static String readLineByLineJava8(File file) {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(file.getPath()), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return contentBuilder.toString();
    }

    public void getAlphabet(MouseEvent mouseEvent) {

        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") + "/src/sample"));
        //Show save file dialog
        File file = fileChooser.showOpenDialog(null);

        if (file == null) {
            return;
        }

        alphabet = readLineByLineJava8(file).replace("\n", "");
        alphabet_text_view.setText(alphabet);

    }

    public void getProbabilities(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") + "/src/sample"));
        //Show save file dialog
        File file = fileChooser.showOpenDialog(null);
        if (file == null) {
            return;
        }
        String probabilities_string = readLineByLineJava8(file);
        probabilities = new ArrayList<>();
        double sum = 0;
        for (String s : probabilities_string.split("\n")) {
            double valueOf = Double.valueOf(s);
            probabilities.add(valueOf);
            sum += valueOf;
        }
        if (Double.compare(sum, 1f) != 0) {
            is_probabilities_ready_text_view.setText("Probabilities error");
            return;
        }
        is_probabilities_ready_text_view.setText("Done");
    }

    public void buildHuffmanTree(MouseEvent mouseEvent) {
        if (alphabet.isEmpty() || probabilities.isEmpty()) {
            return;
        }

        Double[] probsIndouble = new Double[alphabet.length()];
        probsIndouble = probabilities.toArray(probsIndouble);

        huffmanTree = HuffmanTree.buildHuffmanTree(alphabet.toCharArray(), probsIndouble);
        if (huffmanTree != null) {
            is_tree_built_text_view.setText("Built");
        }
    }

    public void getStringToEncode(MouseEvent mouseEvent) {
        if (huffmanTree == null) {
            return;
        }
        FileChooser fileChooser = new FileChooser();
        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") + "/src/sample"));
        //Show save file dialog
        File file = fileChooser.showOpenDialog(null);
        if (file == null) {
            return;
        }

        stringToEncode = readLineByLineJava8(file).replace("\n", "");

        string_to_encode_text_view.setText(stringToEncode);
    }

    public void encodeString(MouseEvent mouseEvent) {
        if (huffmanTree == null) {
            return;
        }
        String incode = huffmanTree.incode(stringToEncode);
        encoded_string.setText(incode);

        huffmanTree.printCodes();
    }

    public void getK(MouseEvent mouseEvent) {
        double sumEntropy = 0;
        for (double p : probabilities) {
            if (Double.compare(p, 0) == 0) {
                continue;
            }
            sumEntropy -= p * Math.log(p) / Math.log(2);
        }

        double K;
        K = 1 - sumEntropy / (Math.log(probabilities.size()) / Math.log(2));

        StringBuilder stringBuilder = new StringBuilder();
        String[] codeTable = huffmanTree.codeTable();
        for (int i = 0; i < codeTable.length; i++) {
            if (codeTable[i] != null) {
                stringBuilder
                        .append((char) i)
                        .append(" -> ")
                        .append(codeTable[i])
                        .append("\n");
            }
        }
        String codes = stringBuilder.toString();

//        String codes = huffmanTree.getPrintedTree();
        char[] chars = alphabet.toCharArray();
        double averageLength = 0f;
        double craftSum = 0;
        for (int i = 0; i < alphabet.length(); i++) {
            averageLength += codeTable[(int) chars[i]].length() * probabilities.get(i);
            craftSum += Math.pow(2, -codeTable[(int) chars[i]].length());
        }


        K_text_view.setText("Redundancy = " + String.valueOf(K) + "\n"
                + "Average length = " + String.valueOf(averageLength) + "\n"
                + "Craft sum = " + String.valueOf(craftSum) + "\n"
                + codes + "\n"
        );
    }

    public void getTextToDecode(MouseEvent mouseEvent) {
        if (huffmanTree == null) {
            return;
        }
        FileChooser fileChooser = new FileChooser();
        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") + "/src/sample"));
        //Show save file dialog
        File file = fileChooser.showOpenDialog(null);
        if (file == null) {
            return;
        }
        String stringToDecode = readLineByLineJava8(file).replace("\n", "");

        String decodedString = huffmanTree.decode(stringToDecode);

        decoded_text_text_view.setText(decodedString);

        FileChooser fileChooserOut = new FileChooser();
        //Set extension filter
        fileChooserOut.getExtensionFilters().add(extFilter);

        fileChooserOut.setInitialDirectory(new File(System.getProperty("user.dir") + "/src/sample"));
        //Show save file dialog
        File fileOutDecoded = fileChooser.showOpenDialog(null);

        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(fileOutDecoded));
            writer.write(decodedString);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
