package risk;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.io.*;
import java.util.*;
import java.nio.*;
import java.lang.*;

public class CardFetcherGenerate implements CardFetcher {
    static ArrayList<String> cardListHeaders = new ArrayList<String>();
    static ArrayList<String> cardList= new ArrayList<String>();
    CardFetcherGenerate(){

    }


    @Override
    public String[][] getCardFile(String riskcards) throws IOException {
        //File reading
        File file = new File(
                "src/main/resources/"+riskcards);

        ArrayList<String> listString = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(
                new FileReader(file));

        // read line as string
        String line = reader.readLine();

        // checking for end of file
        while (line != null) {
            listString.add(line);
            line = reader.readLine();
        }

        // closing bufferreader String
        reader.close();

        // storing the data in arraylist to array
        String[] array
                = listString.toArray(new String[listString.size()]);

        ArrayList<String> cardList = new ArrayList<String>();
        cardListHeaders = new ArrayList<String>();

        for (int i=0; i<array.length; i++){
            if (array[i].length()>0){
                if(i==0){
                    //cardList.add(array[i].substring(5));
                    cardListHeaders.add(array[i].substring(5));
                }
                else if(array[i].charAt(0)=='['){
                    cardList.add(array[i].substring(1,array[i].length()-1));
                    cardListHeaders.add(array[i].substring(1,array[i].length()-1));
                }
                else {
                    cardList.add(array[i]);
                }
            }

        }

        //int x = cardList.indexOf("cards");
        String[][] cardArray = createCardArray(cardList);

        return cardArray;

    }

    public String[][] createCardArray(ArrayList<String> cardList){
        String[][] cardListArray = new String[cardList.size()][];
        for (int i = 0; i < cardList.size(); i++) {
            cardListArray[i] = cardList.get(i).split(" ");//Split returns an array element

        }
        return cardListArray;
    }

    @Override
    public void printCardArray(String[][] array){
        System.out.println("Cards Type: "+cardListHeaders.get(0));
        for (String[] row : array) {
            System.out.println(Arrays.toString(row));
        }
    }

}