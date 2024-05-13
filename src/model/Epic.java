package model;

import service.ObjectManager;

public class Epic extends Task {


    public Epic(String name, String description) {
        super(name, description);

    }

    @Override
    public String toString() {
        return super.toString() + "\nsubtasks = " +
                (ObjectManager.subtaskStorage.get(ObjectManager.getUin(this)) != null ?
                ObjectManager.subtaskStorage.get(ObjectManager.getUin(this)).toString() : null);

    }
}

