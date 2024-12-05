package org.hexils.dnarch.da.dungeon;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.hetils.jgl17.General;
import org.hetils.jgl17.Pair;
import org.hetils.mpdl.Inventory;
import org.hetils.mpdl.NSK;
import org.hexils.dnarch.da.Condition;
import org.hexils.dnarch.da.DA_item;
import org.hexils.dnarch.da.GUI;
import org.hexils.dnarch.da.Managable;
import org.hexils.dnarch.da.objects.conditions.DungeonStart;
import org.hexils.dnarch.da.objects.conditions.Type;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static org.hetils.mpdl.General.log;
import static org.hetils.mpdl.Item.newItemStack;
import static org.hetils.mpdl.Location.toMaxMin;

public class Dungeon extends Managable {
    public static class DuplicateNameException extends Exception {
        public DuplicateNameException(String s) {
            super(s);
        }
    }
    public static final Collection<Dungeon> dungeons = new ArrayList<>();
    @Contract(pure = true)
    public static @Nullable Dungeon get(String name) {
        for (Dungeon d : dungeons)
            if (Objects.equals(d.name, name))
                return d;
        return null;
    }
    @Contract(pure = true)
    public static @Nullable Dungeon get(Location loc) {
        for (Dungeon d : dungeons)
            if (d.isWithinDungeon(loc))
                return d;
        return null;
    }

    private class Section extends org.hetils.mpdl.Location.Box {
        private String name;

        public Section(Pair<Location, Location> selection) {
            this(selection, "Section" + sections.size());
        }

        public Section(Pair<Location, Location> selection, String name) {
            super(selection);
            this.name = name;
        }

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }

    private org.hetils.mpdl.Location.Box bounding_box;
    private String name;
    private String display_name;
    private final List<DA_item> items = new ArrayList<>();
    private final List<Section> sections = new ArrayList<>();
    private Section mains;
    private final UUID creator;
    private String creator_name;
    private Condition dungeon_start;
    private boolean running = false;

    public Dungeon(UUID creator, Pair<Location, Location> sec) {
        Player p = Bukkit.getPlayer(creator);
        this.creator = creator;
        this.creator_name = p == null ? "" : p.getName();
        do {
            this.name = "Dungeon" + dungeons.size();
            this.display_name = this.name;
        } while (get(name) != null);
        this.mains = new Section(sec, "Main_Sector");
        bounding_box = new org.hetils.mpdl.Location.Box(toMaxMin(sec));
        dungeons.add(this);
    }
    public Dungeon(UUID creator, String name, Pair<Location, Location> sec) throws DuplicateNameException {
        Player p = Bukkit.getPlayer(creator);
        this.creator = creator;
        this.creator_name = p == null ? "" : p.getName();
        if (get(name) != null) throw new DuplicateNameException("");
        this.name = name;
        this.display_name = this.name;
        this.mains = new Section(sec, "Main_Sector");
        bounding_box = new org.hetils.mpdl.Location.Box(toMaxMin(sec));
        this.dungeon_start = new DungeonStart(this);
        dungeons.add(this);
    }

    public String getName() { return name; }

    public String getDisplayName() { return display_name; }

    public boolean isRunning() { return running; }

    public void start() {
        dungeon_start.trigger();
        this.running = true;
    }

    public void stop() {
        this.running = false;
    }

    public Section getMains() { return mains; }

    public UUID getCreator() { return creator; }

    public List<DA_item> getItems() { return items; }

    public String getCreatorName() { return creator_name; }

    public Condition getEventBlock(Type type) {
        if (type == null) return null;
        return switch (type) {
            case DUNGEON_START -> dungeon_start;
            default -> null;
        };
    }

    public void newSection(Pair<Location, Location> sec) {
        sections.add(new Section(sec));
        updateBoundingBox();
    }
    public void newSection(Pair<Location, Location> sec, String name) {
        sections.add(new Section(sec, name));
        updateBoundingBox();
    }

    public Pair<Location, Location> getBoundingBox() { return this.bounding_box; }

    private void updateBoundingBox() {
        List<Location> l = new ArrayList<>(sections.stream().map(Pair::key).toList());
        l.addAll(sections.stream().map(Pair::value).toList());
        l.addAll(List.of(mains.key(), mains.value()));
        bounding_box = new org.hetils.mpdl.Location.Box(toMaxMin(l));
    }


    public static final Particle[] selectParts;
    static {
        selectParts = new Particle[]{Particle.ENCHANTMENT_TABLE, Particle.COMPOSTER, Particle.SOUL_FIRE_FLAME, Particle.FLAME, Particle.END_ROD};
    }

    public void displayDungeon(Player p) {
        DungeonMaster dm = DungeonMaster.getOrNew(p);
        dm.select(bounding_box, Particle.GLOW);
        dm.select(mains, Particle.END_ROD);
        for (int i = 0; i < sections.size(); i++) {
            dm.select(sections.get(i), selectParts[i%selectParts.length]);
        }
    }

    public boolean isWithinDungeon(Location l) {
        General.Stopwatch t = new General.Stopwatch();
        t.start();
        if (!bounding_box.contains(l)) return false;
        for (Section s : sections)
            if (s.contains(l))
                return true;
        log(t.getTime());
        return mains.contains(l);
    }

    @Override
    public void createGUI() {
        this.gui = Inventory.newInv(54, display_name);
        ItemStack getDS = newItemStack(Material.CLOCK, ChatColor.GREEN + "Get dungeon start block");
        NSK.setNSK(getDS, GUI.ITEM_ACTION, "giveDungeonStartBlock");
        this.gui.setItem(5, getDS);
    }
    @Override
    public void updateGUI() {

    }
    @Override
    protected void changeField(DungeonMaster dm, @NotNull String field, String value) {

    }
    @Override
    protected void action(DungeonMaster dm, String action, String[] args) {
        log("gdfjklhjk: " + action);
        switch (action) {
            case "giveDungeonStartBlock" -> dm.give(this.dungeon_start);
        }
    }
}