package com.pandoaspen.entityengine;

import au.edu.federation.caliko.IKBone;
import au.edu.federation.caliko.IKChain;
import au.edu.federation.caliko.IKStructure;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Subcommand;
import com.pandoaspen.entityengine.engine.EEntity;
import com.pandoaspen.entityengine.engine.EntityEngine;
import com.pandoaspen.entityengine.entity.BipodEntity;
import com.pandoaspen.entityengine.entity.SpinnerEntity;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.joml.Vector3f;

import java.util.UUID;
import java.util.function.BiFunction;

@CommandAlias("entityengine|ee")
public class EntityEngineCommand extends BaseCommand {
    @Dependency private EntityEnginePlugin plugin;

    @Subcommand("spawn")
    public void cmdSpawn(Player sender, String type) {
        plugin.getEntityEngine().spawnEntity(sender.getLocation(), getEntityType(type));
    }

    public BiFunction<UUID, Location, ? extends EEntity> getEntityType(String type) {
        switch (type.toUpperCase()) {
            case "BIPOD": return BipodEntity::new;
            case "SPINNER": return SpinnerEntity::new;
            default: return null;
        }
    }

    @Subcommand("speed")
    public void cmdSpeed(Player sender, double newMult) {
        EntityEngine.speedMult = newMult;
    }

    @Subcommand("kill")
    public void cmdKill(Player sender) {

        plugin.getEntityEngine().destroy();
        ;

    }

    static float tX = 0, tY = 0, tZ = 0;

    @Subcommand("testik")
    public void cmdTestIK(Player sender, int bones, int delay, int dist) {
        Location l = sender.getLocation();

        tX = (float) l.getX() + 1f;
        tY = (float) l.getY() + 1f;
        tZ = (float) l.getZ() + 1f;

        Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {

            IKStructure structure;

            public void setup() {
                structure = new IKStructure();

                Colour4f boneColour = new Colour4f();

                IKChain chain = new IKChain();

                Vector3f direction = new Vector3f(1, 0, 0);

                Vector3f startLoc = new Vector3f(tX, tY, tZ);
                Vector3f endLoc = startLoc.add(direction.mul(1, new Vector3f()), new Vector3f());

                IKBone basebone = new IKBone(new Vector3f(startLoc), new Vector3f(endLoc));
                basebone.setColour(boneColour);
                chain.addBone(basebone);

                for (int boneLoop = 0; boneLoop < bones; boneLoop++) {
                    chain.addConsecutiveBone(new Vector3f(direction), 1, boneColour);
                }

                structure.addChain(chain);
            }

            @Override
            public void run() {
                if (structure == null) {
                    setup();
                    return;
                }

                for (int chainIdx = 0; chainIdx < structure.getNumChains(); chainIdx++) {
                    for (int boneIdx = 0; boneIdx < structure.getChain(chainIdx).getNumBones(); boneIdx++) {
                        drawBone(structure.getChain(chainIdx).getBone(boneIdx));
                    }
                }

                try {

                    Player p = Bukkit.getPlayer("Asecta");
                    Location loc = p.getEyeLocation().add(p.getLocation().getDirection().multiply(dist));

                    structure.solveForTarget(new Vector3f((float) loc.getX(), (float) loc.getY(), (float) loc.getZ()));
                } catch (Exception exception) {
                }
            }
        }, delay, delay);
    }

    public void drawBone(IKBone bone) {
        Vector3f v = bone.getEndLocation().sub(bone.getStartLocation(), new Vector3f()).normalize().mul(.1f);

        Vector3f loc = new Vector3f(bone.getStartLocation());
        for (int i = 0; i < 10; i++) {
            Bukkit.getWorlds().get(0).spawnParticle(Particle.REDSTONE, loc.x, loc.y, loc.z, 1, new Particle.DustOptions(Color.AQUA, .5f));
            loc.add(v, loc);
        }
    }
}
