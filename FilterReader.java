package ua.pp.arsdev.steamparser;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

public class FilterReader {
    static Map<String, String> filters = new HashMap<>();
    public static void main(String[] args) throws  Exception{

    }
    public static String readStringFromFile(String filePath) throws Exception{
        FileInputStream fs = new FileInputStream(filePath);
        StringBuilder b = new StringBuilder();
        while(fs.available() > 0){
            char ch = (char) fs.read();
            String ch1 = String.valueOf(ch);
            b.append(ch1);
        }
        return b.toString();
    }
    public static Map<String, String> parseFilter() throws Exception{
        String input = readStringFromFile("E:\\STEAM_DATA\\filter.txt");
        String[] data = input.split("\r\n");
        for(int i = 0;i< data.length;i++){
            switch (i){
                case 0:
                    String ID_FILTER = data[0];
                    ID_FILTER = ID_FILTER.replaceAll("STEAM_0\\:", "");
                    String key_code = String.valueOf(ID_FILTER.toCharArray()[0]);
                    ID_FILTER = ID_FILTER.replaceAll("1:", "");
                    filters.put("ID_RANGE", key_code + "_" + ID_FILTER);
                    break;
                case 1:
                    String LEVEL = data[1].replaceAll("Steam level - from ", "");
                    LEVEL = LEVEL.split(" to ")[0] + "_" + LEVEL.split(" to ")[1];
                    filters.put("LEVEL", LEVEL);
                    break;
                case 2:
                    String GAMES_OWNED = data[2].replaceAll("Games owned - from ", "");
                    GAMES_OWNED = GAMES_OWNED.split(" to ")[0] + "_" + GAMES_OWNED.split(" to ")[1];
                    filters.put("GAMES_OWNED", GAMES_OWNED);
                    break;
                case 3:
                    String HOURS_PLAYED = data[3].replaceAll("Hours on record - ", "");
                    filters.put("HOURS_PLAYED", HOURS_PLAYED);
                    break;
                case 4:
                    String LAST_ONLINE = data[4].replaceAll("Last online - ", "").replaceAll(" years", "");
                    filters.put("LAST_ONLINE", LAST_ONLINE);
                    break;
                case 5:
                    String OWNED_PRODUCTS = data[5].replaceAll("Owned Products - ", "");
                    if(OWNED_PRODUCTS.contains(",")){
                        String[] own_prod_all = OWNED_PRODUCTS.split(",");
                        StringBuilder b = new StringBuilder();
                        for(int a = 0;a<own_prod_all.length;a++){
                            if(a != own_prod_all.length - 1) {
                                b.append(own_prod_all[a] + "_");
                            }else{
                                b.append(own_prod_all[a]);
                            }
                        }
                        filters.put("OWNED_PRODUCTS", b.toString());
                    }else{
                        filters.put("OWNED_PRODUCTS", OWNED_PRODUCTS);
                    }
                    break;
                default:
                    break;
            }
        }
       return filters;
    }
}
