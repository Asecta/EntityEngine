package com.pandoaspen.entityengine.engine.animation;

import com.pandoaspen.entityengine.engine.api.AnimationObject;
import com.pandoaspen.entityengine.engine.utils.AnimationUtils;
import com.pandoaspen.entityengine.utils.ModelMath;
import de.javagl.jgltf.model.NodeModel;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@RequiredArgsConstructor
public class StandEntityAnimationObject implements AnimationObject {

    private final boolean small;

    private ArmorStand host;

    @Override
    public void update(Matrix4f matrix4f) {
        Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("EntityEngine"), () -> {
            ModelMath.positionStand(host, new Vector3f(0, 0, 0), matrix4f, small);
        });
    }

    @Override
    public void create(Matrix4f matrix4f, NodeModel nodeModel) {

        World world = Bukkit.getWorlds().get(0);

        Location l = new Location(world, 0, 0, 0, -90, 0);

        this.host = (ArmorStand) world.spawnEntity(l, EntityType.ARMOR_STAND);

        ItemStack headItem = AnimationUtils.parseItemStack(nodeModel);

        Vector3f origin = matrix4f.transformPosition(0, 0, 0, new Vector3f());
        ModelMath.positionStand(host, origin, matrix4f, small);

        this.host.setVisible(false);
        this.host.setInvulnerable(true);
        this.host.setGravity(false);
        this.host.setSmall(small);
        this.host.getEquipment().setHelmet(headItem);
    }

    @Override
    public void destroy() {
        if (this.host != null) this.host.remove();
    }
}
