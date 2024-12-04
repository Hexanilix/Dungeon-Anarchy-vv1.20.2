package org.hexils.dnarch.da.actions;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.hexils.dnarch.da.Action;
import org.hexils.dnarch.da.DM;
import org.jetbrains.annotations.NotNull;

import javax.swing.text.html.parser.*;
import java.util.List;

import static org.hetils.mpdl.Item.newItemStack;

public class EntityModification extends Action {
    private List<Entity> entities = null;

    public EntityModification() {
        super(Type.ENTITY_MOD);
    }

    @Override
    public void execute() {

    }

    @Override
    protected void resetAction() {

    }

    @Override
    protected Inventory createGUIInventory() {
        return null;
    }

    @Override
    public void updateGUI() {

    }

    @Override
    protected ItemStack toItem() {
        ItemStack i = newItemStack(Material.TRIPWIRE_HOOK, "Entity Modification");
        return i;
    }

    @Override
    protected void changeField(DM dm, @NotNull String field, String value) {

    }

    @Override
    protected void action(DM dm, String action, String[] args) {

    }
}
