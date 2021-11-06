package com.pandoaspen.entityengine.entity;

import com.pandoaspen.entityengine.engine.EEntity;
import org.bukkit.Location;

import java.io.File;
import java.net.URI;
import java.util.UUID;

public class SpinnerEntity extends EEntity {

    public SpinnerEntity(UUID id, Location location) {
        super(id, location);
    }

    @Override
    public URI getAnimationFile() {
        return new File(MODEL_FILE, "spinner.glb").toURI();
    }

    @Override
    public void tick() {
        getAnimationHandler().setCurrentAnimation("spin");

        //        if (Bukkit.getOnlinePlayers().size() == 0) return;
        //
        //        Location pLoc = Bukkit.getOnlinePlayers().iterator().next().getLocation();
        //        Vector3f position = getPosition();
        //
        //        NodeModel nodeModel = getAnimationHandler().getNodeModel("Target");
        //
        //        float[] translation = new float[]{(float) pLoc.getX(), (float) pLoc.getY(), (float) pLoc.getZ()};
        //
        //        System.out.println(String.format("%.3f, %.3f, %.3f", nodeModel.getTranslation()[0], nodeModel.getTranslation()[1], nodeModel.getTranslation()[2]));
        //
        //        nodeModel.setTranslation(translation);

        getAnimationHandler().update(20);
    }
}
