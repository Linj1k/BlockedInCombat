package fr.kinj14.blockedincombat.Enums;

import fr.kinj14.blockedincombat.Main;

public enum GameState {
    WAITING,
    STARTING,
    GENERATE_MAP,
    PLAYING,
    FINISH;

    public static boolean isState(GameState state){
        return Main.getInstance().Game_State == state;
    }

    public static void setState(GameState state){
        Main.getInstance().Game_State = state;
    }

    public static GameState getState(){
        return Main.getInstance().Game_State;
    }
}
