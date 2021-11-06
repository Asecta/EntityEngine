package com.pandoaspen.entityengine.engine.io.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class BoneConstraint {

    @SerializedName("name") private String name;
    @SerializedName("distance") private double distance;
    @SerializedName("ik_type") private String ikType;
    @SerializedName("influence") private double influence;
    @SerializedName("iterations") private int iterations;
    @SerializedName("limit_mode") private String limitMode;

    @SerializedName("reference_axis") private String referenceAxis;
    @SerializedName("target") private String target;
    @SerializedName("subtarget") private String subTarget;
    @SerializedName("pole_target") private String poleTarget;
    @SerializedName("pole_subtarget") private String poleSubTarget;
    @SerializedName("pole_angle") private double poleAngle;

    @SerializedName("orient_weight") private double orientWeight;

    @SerializedName("use_location") private boolean useLocation;
    @SerializedName("use_rotation") private boolean useRotation;
    @SerializedName("use_stretch") private boolean useStretch;
    @SerializedName("use_tail") private boolean useTail;

}
