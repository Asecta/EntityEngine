package com.pandoaspen.entityengine;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Subcommand;
import com.pandoaspen.entityengine.engine.EEntity;
import com.pandoaspen.entityengine.engine.EntityEngine;
import com.pandoaspen.entityengine.engine.animation.DefaultAnimationHandler;
import com.pandoaspen.entityengine.entity.BipodEntity;
import com.pandoaspen.entityengine.entity.RexEntity;
import com.pandoaspen.entityengine.entity.SimpleAIRexEntity;
import com.pandoaspen.entityengine.entity.SpinnerEntity;
import com.pandoaspen.entityengine.utils.ModelMath;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.UUID;
import java.util.function.BiFunction;

@CommandAlias("entityengine|ee")
public class EntityEngineCommand extends BaseCommand {
    @Dependency private EntityEnginePlugin plugin;

    @Subcommand("test")
    public void cmdTest(Player sender) {

        Location l = new Location(sender.getLocation().getWorld(), sender.getLocation().getX(), sender.getLocation().getY(), sender.getLocation().getZ(), -90, 0);

        ArmorStand armorStand = (ArmorStand) sender.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
        armorStand.setGravity(false);

        armorStand.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_BLOCK));
        Location origin = armorStand.getEyeLocation();

        Vector3f o = new Vector3f((float) armorStand.getLocation().getX(), (float) armorStand.getLocation().getY(), (float) armorStand.getLocation().getZ());

        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {

            Vector dirVec = sender.getEyeLocation().toVector().subtract(origin.toVector());
            dirVec.normalize();

            Quaternionf dir = new Quaternionf();
            dir.lookAlong(new Vector3f((float) dirVec.getX(), (float) dirVec.getY(), (float) dirVec.getZ()), new Vector3f(0, 1, 0));

            Matrix4f matrix4f = new Matrix4f();
            matrix4f.rotate(dir);

            ModelMath.positionStand(armorStand, o, matrix4f, false);
        }, 1, 1);

    }

    @Subcommand("spawn")
    public void cmdSpawn(Player sender, String type) {
        plugin.getEntityEngine().spawnEntity(sender.getLocation(), getEntityType(type));
    }

    public BiFunction<UUID, Location, ? extends EEntity> getEntityType(String type) {
        switch (type.toUpperCase()) {
            case "BIPOD":
                return BipodEntity::new;
            case "SPINNER":
                return SpinnerEntity::new;
            case "REX":
                return RexEntity::new;
            default:
                return null;
        }
    }

    @Subcommand("speed")
    public void cmdSpeed(Player sender, double newMult) {
        EntityEngine.speedMult = newMult;
    }

    @Subcommand("kill")
    public void cmdKill(Player sender) {
        plugin.getEntityEngine().destroy();
    }

    @Subcommand("q1")
    public void cmdQ(Player sender, float x, float y, float z) {
        DefaultAnimationHandler.q1X = x;
        DefaultAnimationHandler.q1Y = y;
        DefaultAnimationHandler.q1Z = z;
    }

    @Subcommand("q2")
    public void cmdQ2(Player sender, float x, float y, float z, float w) {
        DefaultAnimationHandler.q2X = x;
        DefaultAnimationHandler.q2Y = y;
        DefaultAnimationHandler.q2Z = z;
        DefaultAnimationHandler.q2W = w;
    }


    @Subcommand("xyz")
    public void cmdXYZ(Player sender, int x, int y, int z) {
        ModelMath.x = x;
        ModelMath.y = y;
        ModelMath.z = z;
    }

    @Subcommand("fakerex")
    public void cmdFakeRex(Player sender) {
        World world = sender.getWorld();

        Zombie zombie = (Zombie) world.spawnEntity(sender.getLocation(), EntityType.ZOMBIE);
        zombie.setSilent(true);
        zombie.setInvisible(true);
        zombie.setShouldBurnInDay(false);


        PotionEffect speedPot = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2);
        zombie.addPotionEffect(speedPot);

        SimpleAIRexEntity rexEntity = new SimpleAIRexEntity(UUID.randomUUID(), zombie);
        plugin.getEntityEngine().spawnEntity(rexEntity);
    }
}