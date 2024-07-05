package risk;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.io.*;
import java.util.*;
import java.nio.*;
import java.lang.*;

public class MapFetcherGenerate implements MapFetcher {
    static ArrayList<String> mapFileHeaders = new ArrayList<String>();
    static ArrayList<String> continentList= new ArrayList<String>();
    static ArrayList<String> countriesList= new ArrayList<String>();
    static ArrayList<String> bordersList= new ArrayList<String>();
    MapFetcherGenerate(){

    }


    @Override
    public String[][][] getMapFile(String map) throws IOException {
        //File reading
        File file = new File(
                "src/main/resources/"+map);

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

        ArrayList<String> mapFile = new ArrayList<String>();
        mapFileHeaders = new ArrayList<String>();

        for (int i=0; i<array.length; i++){
            if (array[i].length()>0){
                if(i==0){
                    mapFile.add(array[i].substring(5));
                    mapFileHeaders.add(array[i].substring(5));
                }
                else if(array[i].charAt(0)=='['){
                    mapFile.add(array[i].substring(1,array[i].length()-1));
                    mapFileHeaders.add(array[i].substring(1,array[i].length()-1));
                }
                else {
                    mapFile.add(array[i]);
                }
            }

        }

        int i = mapFile.indexOf("continents");
        int j = mapFile.indexOf("countries");
        int k = mapFile.indexOf("borders");

        continentList.addAll(mapFile.subList(i+1, j));
        countriesList.addAll(mapFile.subList(j+1, k));
        bordersList.addAll(mapFile.subList(k+1, mapFile.size()));

        String[][] continents = createMapArray(continentList);
        String[][] countries = createMapArray(countriesList);
        String[][] borders = createMapArray(bordersList);
        //printMapArray(continents);
        return new String[][][]{continents, countries, borders};


    }

    public String[][] createMapArray(ArrayList<String> mapList){
        List<List<String>> doomList = new ArrayList<>();
        int i;

        for(i=0; i<mapList.size(); i++){
            doomList.add(Arrays.asList(mapList.get(i).split(" ")));
        }

        //Declare 2D array with known row size and allows for variable column size
        String[][] mapListArray = new String[doomList.size()][];
        for (i = 0; i < doomList.size(); i++) {
            List<String> arrayRow = doomList.get(i); //Each list i is row of the 2D array
            mapListArray[i] = arrayRow.toArray(new String[arrayRow.size()]);
        }
        return mapListArray;

    }

    @Override
    public void printMapArray(String[][][] array){
        System.out.println("Map name: "+MapFetcherGenerate.mapFileHeaders.get(0));
        for (int i = 0; i < array.length; i++) {
            System.out.println(MapFetcherGenerate.mapFileHeaders.get(i+1) + ":");
            for (String[] row : array[i]) {
                System.out.println(Arrays.toString(row));
            }
            System.out.println();
        }

    }

}