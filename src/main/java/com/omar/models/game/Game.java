package com.omar.models.game;

import com.omar.models.faction.Army;
import com.omar.models.faction.Faction;
import com.omar.models.world.TileStatus;
import com.omar.models.world.World;

import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Game {
    private final World world;
    private final Faction [] factions;
    private final Scanner scanner;
    private GameStatus status;
    private Turn whosturn;
    public Game(Scanner scanner, World world, Faction [] factions) {
        this.scanner = scanner;
        this.world = world;
        this.factions = factions;
        this.status = GameStatus.ACTIVE;
        this.whosturn = Turn.P1TURN;
    }
    private void makeMove(){
        Faction currPlayer = factions[1];
        Faction otherPlayer = factions[0];
        TileStatus currTileOccupier = TileStatus.P2OCCUPIED;
        if(whosturn == Turn.P1TURN){
            currPlayer = factions[0];
            otherPlayer = factions[1];
            currTileOccupier = TileStatus.P1OCCUPIED;
            System.out.println("Player 1's turn.");
        } else if(whosturn == Turn.P2TURN){
            System.out.println("Player 2's turn.");
        }
        System.out.println(currPlayer);

        int armyChoice;
        while (true) {
            System.out.println("Enter an army's number to select it.");
            System.out.println("Enter -1 to skip your move.");
            if (scanner.hasNextInt()) {
                armyChoice = scanner.nextInt();
                if (armyChoice >= 1 && armyChoice <= currPlayer.getArmies().size()) {
                    break;
                } else if(armyChoice == -1) {
                    return;
                } else {
                    System.out.println("Invalid input!");
                }
            } else {
                scanner.next();
                System.out.println("Invalid input!");
            }
        }

        Army selectedArmy = currPlayer.getArmy(armyChoice - 1);
        System.out.println("You have selected army #" + armyChoice + ": " + selectedArmy);
        int armyPosition = selectedArmy.getPosition();
        Set<Integer> possibleMoves = world.getTileNeighbors(armyPosition);
        System.out.println("Possible new positions are: " + possibleMoves);

        int posChoice;
        while (true) {
            System.out.println("Enter the new position of army #" + armyChoice);
            if (scanner.hasNextInt()) {
                posChoice = scanner.nextInt();
                if (possibleMoves.contains(posChoice)) {
                    break;
                } else {
                    System.out.println("Invalid input!");
                }
            } else {
                scanner.next();
                System.out.println("Invalid input!");
            }
        }

        world.getTile(armyPosition).setStatus(TileStatus.EMPTY);

        if(world.getTile(posChoice).getStatus() == TileStatus.EMPTY){ // Tile is empty, move there.
            selectedArmy.setPosition(posChoice);
            world.getTile(posChoice).setStatus(currTileOccupier);
        } else if(world.getTile(posChoice).getStatus() == currTileOccupier){ // Tile is occupied by allies.
            List<Army> armies = currPlayer.getArmies();
            int size = armies.size();
            for (int i = 0 ; i < size ; i++) {
                if(armies.get(i).getPosition() == posChoice){
                    int firepower = armies.get(i).getFirepower();
                    selectedArmy.setFirepower(selectedArmy.getFirepower() + firepower);
                    armies.remove(i);
                    selectedArmy.setPosition(posChoice);
                    break;
                }
            }
        } else { // // Tile is occupied by enemies. Combat.
            List<Army> enemyArmies = otherPlayer.getArmies();
            int size = enemyArmies.size();
            for(int i = 0 ; i < size ; i++) {
                if(enemyArmies.get(i).getPosition() == posChoice){
                    int firepower = selectedArmy.getFirepower();
                    int enemyFirepower = enemyArmies.get(i).getFirepower();

                    if(enemyFirepower > firepower){
                        enemyArmies.get(i).setFirepower(enemyFirepower - firepower);
                        currPlayer.getArmies().remove(selectedArmy);
                    } else if (enemyFirepower < firepower) {
                        selectedArmy.setFirepower(firepower - enemyFirepower);
                        enemyArmies.remove(i);
                        selectedArmy.setPosition(posChoice);
                    } else {
                        currPlayer.getArmies().remove(selectedArmy);
                        enemyArmies.remove(i);
                    }
                    break;
                }
            }
        }
    }
    public void play(){
        while(status == GameStatus.ACTIVE){
            makeMove();
            if(whosturn == Turn.P1TURN){
                whosturn = Turn.P2TURN;
            } else if(whosturn == Turn.P2TURN){
                whosturn = Turn.P1TURN;
            }
        }
        if(status == GameStatus.P1WINS){
            System.out.println(factions[0].getName() + " has won!");
        } else if(status == GameStatus.P2WINS) {
            System.out.println(factions[1].getName() + " has won!");
        }
        System.out.println("Game over.");
        scanner.close();
    }
}
