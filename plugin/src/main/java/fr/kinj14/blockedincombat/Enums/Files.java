package fr.kinj14.blockedincombat.Enums;

import fr.kinj14.blockedincombat.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

public enum Files {
    CONFIG("config.yml"),
    LANG("lang.yml"),
    Spigot("spigot.yml");

    private final String fileName;
    private File dataFolder;

    private final Main main = Main.getInstance();

    Files(String fileName){
        this.fileName = fileName;
    }

    public void setFolder(File folder){
        this.dataFolder = folder;
    }

    public File getFile(){
        return new File(dataFolder, fileName);
    }

    public FileConfiguration getConfig(){
        return YamlConfiguration.loadConfiguration(getFile());
    }

    public void save(FileConfiguration config){
        try {
            config.save(getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFileName(){
        return this.fileName;
    }

    public void create(boolean forceCreate){
        if(fileName == null || fileName.isEmpty()){
            throw new IllegalArgumentException(Main.getPrefix(true)+" ResourcePath cannot be null or empty!");
        }

        InputStream in = main.getResource(fileName);
        if(in == null){
            throw new IllegalArgumentException(Main.getPrefix(true)+" The resource '"+fileName+"' cannot be found in plugin jar!");
        }

        if(!dataFolder.exists() && !dataFolder.mkdir()){
            main.getLogger().severe(Main.getPrefix(true)+" Failed to make plugin directory!");
        }

        File outFile = getFile();
        try{
            if(forceCreate || !outFile.exists()){
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int n;

                while((n = in.read(buf)) > 0){
                    out.write(buf, 0, n);
                }

                out.close();
                in.close();

                if(!outFile.exists()){
                    main.getLogger().severe(Main.getPrefix(true)+" Unable to copy file !");
                } else if(!forceCreate) {
                    main.getLogger().info(Main.getPrefix(true)+" "+fileName+" is created !");
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void create(){
        create(false);
    }

    public void delete(){
        File file = getFile();
        if(file.exists() && !file.isDirectory()){
            file.delete();
        }
    }
}
