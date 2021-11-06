package com.pandoaspen.entityengine.engine.io.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SkeletonBone {

    private transient List<SkeletonBone> childBones = new ArrayList<>();

    @SerializedName("name") private String name;
    @SerializedName("children") private List<String> childrenNames;
    @SerializedName("parent") private String parent;

    @SerializedName("head") private float[] head;
    @SerializedName("tail") private float[] tail;
    @SerializedName("length") private float length;

    @SerializedName("matrix") private float[][] matrix;
    @SerializedName("matrix_basis") private float[][] matrix_basis;
    @SerializedName("rotation_quaternion") private float[] rotation_quaternion;

    @SerializedName("ik_linear_weight") private float ikLinearWeight;
    @SerializedName("ik_max_x") private float ikMaxX;
    @SerializedName("ik_max_y") private float ikMaxY;
    @SerializedName("ik_max_z") private float ikMaxZ;
    @SerializedName("ik_min_x") private float ikMinX;
    @SerializedName("ik_min_y") private float ikMinY;
    @SerializedName("ik_min_z") private float ikMinZ;
    @SerializedName("ik_rotation_weight") private float ikRotationWeight;
    @SerializedName("ik_stiffness_x") private float ikStiffnessX;
    @SerializedName("ik_stiffness_y") private float ikStiffnessY;
    @SerializedName("ik_stiffness_z") private float ikStiffnessZ;
    @SerializedName("ik_stretch") private float ikStretch;
    @SerializedName("is_in_ik_chain") private boolean isInIkChain;

    @SerializedName("use_ik_limit_x") private boolean useIkLimitX;
    @SerializedName("use_ik_limit_y") private boolean useIkLimitY;
    @SerializedName("use_ik_limit_z") private boolean useIkLimitZ;

    @SerializedName("constraints") private List<BoneConstraint> constraints;

}
