package com.pandoaspen.entityengine;

import com.pandoaspen.entityengine.utils.ModelMath;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@RequiredArgsConstructor
public class TestListener implements Listener {

    private final EntityEnginePlugin plugin;

    private ArmorStand stand;

    private Vector3f origin;
    private Matrix4f matrix4f;

    @EventHandler
    public void on(PlayerInteractEvent event) {

        if (event.getItem() == null) return;
        if (event.getItem().getType() != Material.STICK) return;

        if (stand != null) return;

        System.out.println("creating");


        Location location = event.getPlayer().getLocation();
        location.setYaw(-90);
        location.setPitch(0);

        stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        stand.setGravity(false);
        stand.setInvulnerable(true);
        stand.setVisible(false);

        stand.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_BLOCK));

        matrix4f = new Matrix4f();
        matrix4f.translate(bukkitToVector(location.toVector()));

        ModelMath.positionStand(stand, new Vector3f(0, 0, 0), matrix4f, false);
    }

    @EventHandler
    public void onMove(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        if (event.getItem().getType() != Material.GOLDEN_HOE) return;

        if (event.getAction() == Action.PHYSICAL) return;
        boolean leftClick = isLeftClick(event);

        Vector3f direction = bukkitToVector(event.getPlayer().getLocation().getDirection()).normalize();
        if (!leftClick) direction.mul(-1);

        matrix4f.translateLocal(direction);

        ModelMath.positionStand(stand, new Vector3f(0, 0, 0), matrix4f, false);
    }

    //    @EventHandler
    //    public void onRot(PlayerInteractEvent event) {
    //        if (event.getItem() == null) return;
    //        Vector3f up;
    //
    //        switch (event.getItem().getType()) {
    //            case DIAMOND_SHOVEL:
    //                up = new Vector3f(0,1,0);
    //                break;
    //            case GOLD_SPADE:
    //                up = new Vector3f(1,0,0);
    //                break;
    //            case IRON_SPADE:
    //                up = new Vector3f(0,0,1);
    //                break;
    //            default:
    //                return;
    //        }
    //
    //        if (event.getAction() == Action.PHYSICAL) return;
    //        boolean leftClick = isLeftClick(event);
    //
    //        if (!leftClick) up.mul(-1);
    //
    //
    //        matrix4f.rotate((float) Math.toRadians(5), up);
    //
    //
    //        ModelMath.positionStand(stand, new Vector3f(0,0,0), matrix4f, false);
    //    }

    public Vector3f bukkitToVector(Vector vector) {
        return new Vector3f((float) vector.getX(), (float) vector.getY(), (float) vector.getZ());
    }


    public boolean isLeftClick(PlayerInteractEvent event) {
        switch (event.getAction()) {
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                return true;
            default:
                return false;
        }
    }
}
