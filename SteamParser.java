package ua.pp.arsdev.steamparser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.lang.*;
import java.util.ArrayList;
import java.util.Map;

public class SteamParser {
    public static final long ID_CONST = 76561197960265728L;
    public static void main(String[] args) throws Exception {
        Map<String, String> data = FilterReader.parseFilter();
        long id_from = 0, id_to = 0, adder = 0;
        for(Map.Entry<String, String> el : data.entrySet()){
            String key = el.getKey();
            if(key.equals("ID_RANGE")){
                String value = el.getValue();
                adder = Long.parseLong(value.split("_")[0]);
                String range = value.split("_")[1];
                id_from = Long.parseLong(range.split("-")[0]);
                id_to = Long.parseLong(range.split("-")[1]);
            }
        }
        long date = System.currentTimeMillis();
        for (long id = id_from; id <= id_to; id++) {
            long STEAM_ID = (id * 2) + adder + ID_CONST;
            String s_STEAM_ID = String.valueOf(STEAM_ID);
            SteamPlayer player = new SteamPlayer(s_STEAM_ID);
            if (player.exists()) {
                    if(player.isOpenAccount()) {
                        player.parse();
                        System.out.println("https://steamcommunity.com/profiles/" + s_STEAM_ID + "/");
                        System.out.println("STEAM ID - STEAM_0:" + adder + ":" + id);
                        System.out.println("Profile Created - " + player.getMemberSince());
                        System.out.println("Last Online - " + player.getLastOnline());
                        System.out.println("Profile - " + player.getPlayerName());
                        System.out.println("Steam Level - " + player.getPlayerLevel());
                        System.out.println("Games owned - " + player.getGamesCount());
                        System.out.println("Hours on Record - " + player.getTotalHours());
                        System.out.println("--Game--");
                        ArrayList<String> games = player.getGamesOwned();
                        for (String game : games) {
                            System.out.println(game);
                        }
                    }
                }
        }
        long date1 = System.currentTimeMillis();
        System.out.println("WORKED - " + (date1 - date));
    }


}

