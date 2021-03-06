package Game;

import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.app.Application;
import objects.Belt;
import objects.Flag;
import objects.Robot;

import java.util.*;

import Cards.*;
import objects.Wall;

public class Game {
    Boolean playing = false;
    ArrayList<Robot> players;
    int numberOfFlags;
    ArrayList<Flag> flags;
    Application application;
    String currentHands;
    ArrayList<Vector2> startPositions;
    ArrayList<Wall> wallList;
    ArrayList<Belt> beltList;
    int roundNumber = 0;

    public Game(ArrayList<Robot> playerList, Application application) {
        this.application = application;

        players = playerList;
    }

    /**
     * Resets all players position and starts the game
     */
    public void startGame() {
        if (roundNumber == 0) {
            startPositions = application.getEntities(application.getStartPositionLayer());
            playing = true;

            ArrayList<Vector2> walls = application.getEntities(application.getWallsLayer());
            wallList = getWalls(walls);

            ArrayList<Vector2> belts = application.getEntities(application.getConveyorBeltLayer());
            beltList = getBelts(belts);

            ArrayList<Vector2> entitiesList = application.getEntities(application.getFlagLayer());
            flags = sortFlags(entitiesList);


            int count = 0;
            int startPositionIndex = startPositions.size() - count - 1;
            players.get(0).setPosition(startPositions.get(startPositionIndex).x, startPositions.get(startPositionIndex).y);
            for (Robot rob : players) {
                startPositionIndex = startPositions.size() - count - 1;
                players.get(0).getClient().UpdateClientPosition(new Vector2(startPositions.get(startPositionIndex).x, startPositions.get(startPositionIndex).y), rob.getId());
                rob.setStartPosX(startPositions.get(startPositionIndex).x);
                rob.setStartPosY(startPositions.get(startPositionIndex).y);
                count++;
            }
            application.render();

            System.out.println("Server: " + players.get(0).isServer());
            System.out.println("ROBOT LENGTH: " + players.size());
            if (players.get(0).isServer()) {
                playGame();
            }
        } else {
            System.out.println("Server: " + players.get(0).isServer());
            System.out.println("ROBOT LENGTH: " + players.size());
            if (players.get(0).isServer()) {
                playGame();
            }
        }
    }


    /**
     * the games turn order
     */
    public void playGame() {
        drawStep();
    }

    /**
     * Gives each player a hand
     */
    public void drawStep(){
        String hands = "";
        for (Robot rob : players) {
            hands += rob.getId();
            hands += ",";
            rob.drawHand();
            for(ICards card : rob.getHand()){
                hands += card.getSimpleCardName();
                hands += ",";
            }
        }
        currentHands = hands;
        for (Robot rob : players){
            if(rob.isServer()){
                try {
                    // This removes the last character from the string
                    hands = hands.substring(0, hands.length() - 1);
                    rob.getClient().emitCards(hands);
                } catch (Exception e) {

                }
            }
        }
    }

    /**
     * Discards each players hand
     */
    public void discardStep(){
        for (Robot rob : players) {
            rob.discardHand();
        }
    }

    /**
     * Plays the next card for each robot in order of their priority
     * Then checks if anyone has won
     */
    public void playTurn(){
        for (int i = 0; i < 5; i++){
            ArrayList<ICards> cards = new ArrayList<>();
            for(Robot rob : players){
                cards.add(rob.getFirstCard());
            }
            //Sorts every players card so the one with highest priority goes first
            Collections.sort(cards, (c1, c2) -> {
                if (c1.getPrio() > c2.getPrio()) return -1;
                if (c1.getPrio() < c2.getPrio()) return 1;
                return 0;
            });

            for (ICards c: cards){
                for (Robot rob: players) {
                    try {
                        if (rob.getFirstCard().equals(c)) {
                            rob.moveBasedOnNextCard(true, true);
                            break;
                        }
                    } catch (Exception e) {

                    }
                }
            }
        }
        roundNumber++;
        players.get(0).getClient().emitRoundOverFlag();
    }

    /**
     * returns true if any players have visited all the flags,
     * false otherwise
     */
    public boolean checkIfWinner(){
        for(Robot player : players){
            if(player.getVisitedFlags().size() == flags.size()){
                String WinString = "";
                WinString += "Player ";
                WinString += player.getId();
                WinString += " Wins!";
                System.out.println(WinString);
                playing = false;
                return true;
            }
            application.render();
        }
        return false;
    }

    public ArrayList<Robot> getPlayers() {
        return players;
    }

    public void AddPlayer(Robot robot) {
        players.add(robot);
    }

    public Application getApplication() {
        return application;
    }

    public ArrayList<Flag> sortFlags(ArrayList<Vector2> flagList){
        ArrayList<Flag> sortedFlags = new ArrayList<>();

        for(Vector2 v : flagList){
            sortedFlags.add(new Flag(Math.round(v.x), Math.round(v.y)));
        }

        Collections.sort(sortedFlags, (c1, c2) -> {

            int id1 = application.getFlagLayer().getCell(Math.round(c1.x), Math.round(c1.y)).getTile().getId();
            int id2 = application.getFlagLayer().getCell(Math.round(c2.x), Math.round(c2.y)).getTile().getId();
            if (id1 > id2) return 1;
            if (id1 < id2) return -1;
            return 0;
        }
         );

        numberOfFlags = sortedFlags.size();
        return sortedFlags;
    }

    public boolean isPlaying() {
        return playing;
    }

    public ArrayList<Wall> getWalls(ArrayList<Vector2> walls){
        ArrayList<Wall> wallList = new ArrayList<>();
        for(Vector2 wall : walls){
            int wallID = application.getWallsLayer().getCell(Math.round(wall.x), Math.round(wall.y)).getTile().getId();
            switch (wallID){
                case 29:
                    wallList.add(new Wall(Math.round(wall.x), Math.round(wall.y), "S"));
                    break;
                case 31:
                    wallList.add(new Wall(Math.round(wall.x), Math.round(wall.y), "N"));
                    break;
                case 30:
                    wallList.add(new Wall(Math.round(wall.x), Math.round(wall.y), "W"));
                    break;
                case 32:
                    wallList.add(new Wall(Math.round(wall.x), Math.round(wall.y), "W"));
                    wallList.add(new Wall(Math.round(wall.x), Math.round(wall.y), "S"));
                    break;
                case 8:
                    wallList.add(new Wall(Math.round(wall.x), Math.round(wall.y), "E"));
                    wallList.add(new Wall(Math.round(wall.x), Math.round(wall.y), "S"));
                    break;
                }
        }
        return wallList;
    }

    public ArrayList<Belt> getBelts(ArrayList<Vector2> belts){
        ArrayList<Belt> beltList = new ArrayList<>();

        for(Vector2 belt : belts){
            int beltId = application.getConveyorBeltLayer().getCell(Math.round(belt.x), Math.round(belt.y)).getTile().getId();

            switch (beltId){
                case 50:
                    beltList.add(new Belt(Math.round(belt.x), Math.round(belt.y), "S",1));
                    break;
                case 52:
                    beltList.add(new Belt(Math.round(belt.x), Math.round(belt.y), "E",1));
                    break;
                case 49:
                    beltList.add(new Belt(Math.round(belt.x), Math.round(belt.y), "N",1));
                    break;
            }
        }

        return beltList;
    }

    public ArrayList<Belt> getBelts() {return beltList;}

    public ArrayList<Flag> getFlags() {return flags;}

    public ArrayList<Wall> getWallList() {return wallList;}


}
