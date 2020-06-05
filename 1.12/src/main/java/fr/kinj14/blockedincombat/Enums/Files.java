package fr.kinj14.blockedincombat.Enums;

import fr.kinj14.blockedincombat.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

public enum Files {
    LANG("lang.yml");

    private final String fileName;
    private final File dataFolder;

    protected final Main main = Main.getInstance();

    Files(String fileName){
        this.fileName = fileName;
        this.dataFolder = main.getDataFolder();
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

    public void create(){
        if(fileName == null || fileName.isEmpty()){
            throw new IllegalArgumentException("["+Main.getInstance().getPrefixDefault()+"] ResourcePath cannot be null or empty!");
        }

        InputStream in = main.getResource(fileName);
        if(in == null){
            throw new IllegalArgumentException("["+Main.getInstance().getPrefixDefault()+"] The resource '"+fileName+"' cannot be found in plugin jar!");
        }

        if(!dataFolder.exists() && !dataFolder.mkdir()){
            main.logger.severe("["+Main.getInstance().getPrefixDefault()+"] Failed to make plugin directory!");
        }

        File outFile = getFile();
        try{
            if(!outFile.exists()){
                main.logger.info("["+Main.getInstance().getPrefixDefault()+"] The "+fileName+" was not found, creation in progress ...");

                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int n;

                while((n = in.read(buf)) > 0){
                    out.write(buf, 0, n);
                }

                out.close();
                in.close();

                if(!outFile.exists()){
                    main.logger.severe("["+Main.getInstance().getPrefixDefault()+"] Unable to copy file !");
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
