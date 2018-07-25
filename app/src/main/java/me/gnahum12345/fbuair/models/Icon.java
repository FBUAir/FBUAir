package me.gnahum12345.fbuair.models;

import android.graphics.drawable.Drawable;

public class Icon {

    Drawable drawable;
    String name;
    boolean selected = false;

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
