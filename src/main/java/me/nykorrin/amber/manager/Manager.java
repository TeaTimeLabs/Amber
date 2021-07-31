package me.nykorrin.amber.manager;

public class Manager {

    public ManagerHandler managerHandler;

    protected Manager(ManagerHandler managerHandler) {
        this.managerHandler = managerHandler;
    }
}
