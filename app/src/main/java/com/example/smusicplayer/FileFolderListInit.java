package com.example.smusicplayer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FileFolderListInit {
    private List<String> folderList = new ArrayList<>();
    private final List<String> fileList = new ArrayList<>();
    private File rootDir;


    public void setRootDir(File dir){
        rootDir = dir;
        folderList = readFolderListToStorage();
    }

    public List<String> getFileList(){
        return fileList;
    }

    public List<String> getFolderList(){
        return folderList;
    }

    public void writeFolderListToStorage(){
        String fileName = "folderslist.txt";
        File directory = new File(rootDir, "data");
        File foldersList = new File(directory, fileName);

        if(!directory.exists()){
            directory.mkdir();
        }
        if (!foldersList.exists()){
            try {
                foldersList.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter fileWriter = new FileWriter(foldersList);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for (String folderName : folderList) {
                bufferedWriter.write(folderName);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> readFolderListToStorage(){
        List<String> folderList = new ArrayList<>();
        String fileName = "folderslist.txt";
        File directory = new File(rootDir, "data");
        File foldersList = new File(directory, fileName);

        if(foldersList.exists()) {
            try {
                FileReader folderReader = new FileReader(foldersList);
                BufferedReader bufferedReader = new BufferedReader(folderReader);
                String line;
                while (((line = bufferedReader.readLine()) != null)) {
                    folderList.add(line);
                }
                folderReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return folderList;
    }
}
