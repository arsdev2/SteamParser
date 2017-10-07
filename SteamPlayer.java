package ua.pp.arsdev.steamparser;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SteamPlayer {
    private String STEAM_ID = "";
    public  Document STEAM_ID_AND_BADGES_1, STEAM_ID_AND_SLASH, EU_AND_STEAM_ID_AND_SLASH, STEAM_ID_AND_GAMES;
    public SteamPlayer(String STEAM_ID) throws Exception{
        this.STEAM_ID= STEAM_ID;
        STEAM_ID_AND_SLASH = Jsoup.connect("https://steamcommunity.com/profiles/" + STEAM_ID + "/").followRedirects(true).get();
        STEAM_ID_AND_BADGES_1 = Jsoup.connect("https://steamcommunity.com/profiles/" + STEAM_ID + "/badges/1").get();
    }
    public void parse() throws Exception{
        EU_AND_STEAM_ID_AND_SLASH = Jsoup.connect("https://steamid.eu/profile/" + STEAM_ID + "/").validateTLSCertificates(false).get();
        STEAM_ID_AND_GAMES = Jsoup.connect("https://steamcommunity.com/profiles/" + STEAM_ID + "/games/?tab=all").get();
    }
    public boolean exists(){
        String input = STEAM_ID_AND_SLASH.html();
        return !input.contains("Sorry!");
    }
    public  String getMemberSince() throws Exception {
        Document doc = STEAM_ID_AND_BADGES_1;
        return doc.getElementsByClass("badge_description").html();
    }
    public boolean IsProfileDesigned() throws Exception{
        Document doc =STEAM_ID_AND_SLASH;
        String  d = doc.html();
        if(d.contains("This user has not yet")){
            return false;
        }else {
            return true;
        }
    }
    public boolean isOpenAccount() throws Exception{
        Document doc = STEAM_ID_AND_BADGES_1;
        String  d = doc.html();
        if(d.contains("This user has not yet") || d.contains("This profile is private.")){
            return false;
        }else {
            return true;
        }
    }
    public String getPlayerName() throws Exception {
        Document doc = STEAM_ID_AND_SLASH;
        return doc.getElementsByClass("actual_persona_name").html().split("\\n")[0];
    }
    public String getPlayerLevel() throws Exception {
        Document doc = STEAM_ID_AND_SLASH;
        String level = "";
        if(IsProfileDesigned()) {
            if (doc.getElementsByClass("friendPlayerLevelNum").size() != 0) {
                level = doc.getElementsByClass("friendPlayerLevelNum").get(0).html();
            }
        }else {
            Document doc1 = EU_AND_STEAM_ID_AND_SLASH;
            level = doc1.html().split("Level: ")[1];
            level = String.valueOf(level.toCharArray()[0]) + String.valueOf(level.toCharArray()[1]) + String.valueOf(level.toCharArray()[2]) + String.valueOf(level.toCharArray()[3]);
            Pattern regex = Pattern.compile("[0-9]");
            Matcher mat = regex.matcher(level);
            StringBuilder b = new StringBuilder();
            while (mat.find()) {
                b.append(mat.group());
            }
            level = b.toString();
        }
        return level;
    }
    public String getLastOnline() throws Exception {
        if(IsProfileDesigned()) {
            Document doc = STEAM_ID_AND_SLASH;
            String input = doc.getElementsByClass("profile_in_game_name").html();
            if (input.contains("Last Online")) {
                return input;
            } else {
                input = doc.getElementsByClass("profile_in_game_header").html();
                return input;
            }
        }else{
            Document in = EU_AND_STEAM_ID_AND_SLASH;
            String last = in.select("body > div.container > div.row > div > div.panel.mainwrap > div.panel.mainwrap > div.container-fluid > div.col-lg-10.col-md-10.col-sm-10 > div.col-lg-6.col-md-5.col-sm-5 > div > div.panel-body > b:nth-child(1)").html();
            return last;
        }
    }
    public ArrayList<String> getGamesOwned() throws Exception {
        ArrayList<String> gameData = new ArrayList<>();
        Document doc = STEAM_ID_AND_GAMES;
        String gameName = doc.getElementsByTag("script").get(doc.getElementsByTag("script").size() - 1).html();
        String rgGames = gameName.split("\r\n")[0];
        String json = rgGames.split(" = ")[1].replaceAll(";", "");
        if(!json.contains("appid")){return gameData;}
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(json);
        JSONArray arr = (JSONArray) obj;
        for(Object o : arr) {
            JSONObject obj1 = (JSONObject) o;
            String name = String.valueOf(obj1.get("name"));
            String hours = String.valueOf(obj1.get("hours_forever"));
            if(!hours.equals("null")) {
                gameData.add(name + " - " + hours + "hr");
            }else{
                gameData.add(name);
            }
        }
        return gameData;
    }
    public double getTotalHours() throws Exception {
        double count = 0;
        Document doc = STEAM_ID_AND_GAMES;
        String gameName = doc.getElementsByTag("script").get(doc.getElementsByTag("script").size() - 1).html();
        String rgGames = gameName.split("\r\n")[0];
        String json = rgGames.split(" = ")[1].replaceAll(";", "");
        if(!json.contains("appid")){return 0;}
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(json);
        JSONArray arr = (JSONArray) obj;
        for(Object o : arr) {
            JSONObject obj1 = (JSONObject) o;
            String hours = String.valueOf(obj1.get("hours_forever"));
            if(!hours.equals("null")) {
                if(hours.contains(",")){
                    hours = hours.replaceAll(",", ".");
                }
                count += Double.parseDouble(hours);
            }
        }
        return count;
    }
    public String getGamesCount() throws Exception {
        Document doc = STEAM_ID_AND_GAMES;
        int count = 0;
        String gameName = doc.getElementsByTag("script").get(doc.getElementsByTag("script").size() - 1).html();
        String rgGames = gameName.split("\r\n")[0];
        String json = rgGames.split(" = ")[1].replaceAll(";", "");
        if(!json.contains("appid")){return "";}
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(json);
        JSONArray arr = (JSONArray) obj;
        for(Object o : arr) {
            count ++;
        }
        return String.valueOf(count);
    }
}
