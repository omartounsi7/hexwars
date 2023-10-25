package com.omar.model.faction;

import java.util.List;

public class Faction {
    private final String name;
    private final List<Army> armies;
    public Faction(String name, List<Army> armies) {
        this.name = name;
        this.armies = armies;
        armies.add(new Army(10));
    }
    public String getName() {
        return name;
    }
    public List<Army> getArmies(){
        return armies;
    }
    public Army getArmy(int index){
        return armies.get(index);
    }
    @Override
    public String toString() {
        StringBuilder factionString = new StringBuilder(name + " armies:\n");
        for(int i = 1; i <= armies.size(); i++) {
            factionString.append("Army #").append(i).append(": ").append(getArmy(i - 1)).append("\n");
        }
        return factionString.toString();
    }
}
