import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileHandler {

    public String data = "";


    public void readFile(String directory) {
        File chosenFile = new File(directory);

        try (Scanner fileReader = new Scanner(chosenFile)) {
            while (fileReader.hasNextLine()) {
                String data = fileReader.nextLine();
                System.out.println(data);
            }
        }
        catch (FileNotFoundException e){
            System.out.println("File not found");
            e.printStackTrace();
        }
    }

    public String getData() {
        return data;
    }
}
