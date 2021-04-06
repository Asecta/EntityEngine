package com.pandoaspen.entityengine.engine.animation;

import com.pandoaspen.entityengine.engine.api.AnimationObject;
import com.pandoaspen.entityengine.engine.utils.AnimationUtils;
import com.pandoaspen.entityengine.utils.StandPosition;
import com.pandoaspen.entityengine.utils.StandUtils;
import de.javagl.jgltf.model.NodeModel;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.joml.Matrix4f;

@RequiredArgsConstructor
public class StandEntityAnimationObject implements AnimationObject {

    private ArmorStand host;

    @Override
    public void update(Matrix4f matrix4f) {
        Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("EntityEngine"), () -> {
            StandPosition standPosition = StandUtils.getStandPosition(matrix4f);
            this.host.teleport(standPosition.getBukkitLocation(Bukkit.getWorlds().get(0)));
            this.host.setHeadPose(standPosition.getHeadPosition());
        });
    }

    @Override
    public void create(Matrix4f matrix4f, NodeModel nodeModel) {
        ItemStack headItem = AnimationUtils.parseItemStack(nodeModel);
        StandPosition standPosition = StandUtils.getStandPosition(matrix4f);

        Location location = standPosition.getBukkitLocation(Bukkit.getWorlds().get(0));

        this.host = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        this.host.setHeadPose(standPosition.getHeadPosition());

        this.host.setInvisible(true);
        this.host.setInvulnerable(true);
        this.host.setGravity(false);
        this.host.getEquipment().setHelmet(headItem);
    }

    @Override
    public void destroy() {
        if (this.host != null) this.host.remove();
    }
}
