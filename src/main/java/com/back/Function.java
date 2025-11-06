package com.back;

//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.List;
import java.util.Scanner;

public class Function {
    static final String DB_PATH = "db/wiseSaying/";
    static final String LAST_ID_FILE = DB_PATH + "lastId.txt";
    //static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void saveSayToFile(SayList s) throws IOException {
        File file = new File(DB_PATH + s.id + ".json");
        try (Writer writer = new FileWriter(file)) {
            writer.write("{\n");
            writer.write("\"id\": \"" + s.id + ",\n");
            writer.write("\"saying\": \"" + s.saying + "\",\n");
            writer.write("\"author\": \"" + s.author + "\",\n");
            writer.write("}");
        }
    }

    public static void saveLastId(int lastId) throws IOException {
        try (FileWriter writer = new FileWriter(LAST_ID_FILE)) {
            writer.write(String.valueOf(lastId));
        }
    }

    public static int loadData(List<SayList> list) throws IOException {
        int lastId = 0;
        File folder = new File(DB_PATH);
        if (!folder.exists()) folder.mkdirs();

        File idFile = new File(LAST_ID_FILE);
        if (idFile.exists()) {
            try (Scanner idScanner = new Scanner(idFile)) {
                if (idScanner.hasNextInt()) {
                    lastId = idScanner.nextInt();
                }
            }
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null) {
            for (File file : files) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    int id = 0;
                    String saying = "";
                    String author = "";
                    String line;
                    while ((line = br.readLine()) != null) {
                        line = line.trim();
                        if (line.startsWith("\"id\"")){
                            id = Integer.parseInt(line.replaceAll("[^0-9]",""));
                        } else if (line.startsWith("\"saying\"")) {
                            saying = line.split(":")[1].trim().replaceAll("[\",]","");
                        } else if (line.startsWith("\"author\"")) {
                            author = line.split(":")[1].trim().replaceAll("[\",]","");
                        }
                    }
                    list.add(new SayList(id, saying, author));

                }
            }
        }

        return lastId;
    }

    public static void deleteFile(int id) {
        File f = new File(DB_PATH + id + ".json");
        if (f.exists()) {
            boolean deleted = f.delete();
            if (!deleted) {
                System.out.println(id + ".json 파일을 삭제하지 못했습니다.");
            }
        }
    }

    // 빌드 명령
    public static void buildDataJson(List<SayList> list) throws IOException {
        File file = new File(DB_PATH + "data.json");
        try (FileWriter writer = new FileWriter(file)) {
        writer.write("[\n");
        for (int i = 0; i < list.size(); i++) {
            SayList s = list.get(i);
            writer.write("  {\n");
            writer.write("    \"id\": " + s.id + ",\n");
            writer.write("    \"saying\": \"" + s.saying + "\",\n");
            writer.write("    \"author\": \"" + s.author + "\"\n");
            writer.write("  }");
            if (i < list.size() - 1) writer.write(",\n"); // 마지막 쉼표 생략
        }
        writer.write("\n]");
    }
    }
}
