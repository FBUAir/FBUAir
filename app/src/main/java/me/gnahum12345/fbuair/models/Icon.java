package me.gnahum12345.fbuair.models;

import android.graphics.drawable.Drawable;

public class Icon {

    private Drawable drawable;
    private String name;
    private boolean added = false;

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
