package au.edu.federation.caliko;

import au.edu.federation.utils.VectorUtils;
import org.joml.Matrix3f;
import org.joml.Vector3f;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A FabrikStructure3D contains one or more FabrikChain3D objects, which we can solve using the FABRIK (Forward And
 * Backward Reaching Inverse Kinematics) algorithm for specified target locations.
 * <p>
 * The FabrikStructure3D class is merely a convenient holder for a list of FabrikChain3D objects which allows
 * multiple chains to have their target location updated, as well as solving and drawing the multiple chains
 * attached to the FabrikStructure3D object using one method call per structure.
 * <p>
 * If you do not intend on attaching multiple FabrikChain3D objects into a complex structure, for example one with
 * multiple effectors, then you may be better served by creating individual FabrikChain3D objects and using those
 * objects directly.
 *
 * @author Al Lansley
 * @version 0.5 - 19/06/2019
 **/

public class IKStructure implements Serializable {
    private static final long serialVersionUID = 1L;

    // ---------- Private Properties ----------

    private static final String NEW_LINE = System.lineSeparator();

    /**
     * The string name of this FabrikStructure3D - can be used for creating Maps, if required.
     */
    private String mName;

    /**
     * The main substance of a FabrikStructure3D is a List of FabrikChain3D objects.
     * <p>
     * Each FabrikChain3D in the mChains vector is independent of all others, but shares the same target location as any/all other chains
     * which exist in this structure.
     */
    private List<IKChain> mChains = new ArrayList<>();

    // --------- Public Methods ----------

    /**
     * Default constructor.
     */
    public IKStructure() {
    }

    /**
     * Naming constructor.
     *
     * @param name The name you wish to call the structure.
     */
    public IKStructure(String name) {
        mName = name;
    }

    /**
     * Solve the structure for the given target location.
     * <p>
     * All chains in this structure are solved for the given target location EXCEPT those which have embedded targets enabled, which are
     * solved for the target location embedded in the chain.
     * <p>
     * After this method has been executed, the configuration of all IK chains attached to this structure will have been updated.
     *
     * @param newTargetLocation The location of the target for which we will attempt to solve all chains attached to this structure.
     */

    public void solveForTarget(Vector3f newTargetLocation) {
        int numChains = mChains.size();
        int connectedChainNumber;

        // Loop over all chains in this structure...
        for (int loop = 0; loop < numChains; ++loop) {
            // Get this chain, and get the number of the chain in this structure it's connected to (if any)
            IKChain thisChain = mChains.get(loop);
            connectedChainNumber = thisChain.getConnectedChainNumber();

            // If this chain isn't connected to another chain then update as normal...
            if (connectedChainNumber == -1) {
                thisChain.solveForTarget(newTargetLocation);
            } else // ...however, if this chain IS connected to another chain...
            {
                // ... get the host chain and bone which this chain is connected to
                IKChain hostChain = mChains.get(connectedChainNumber);
                IKBone hostBone = hostChain.getBone(thisChain.getConnectedBoneNumber());
                if (hostBone.getBoneConnectionPoint() == BoneConnectionPoint.START) {
                    thisChain.setBaseLocation(hostBone.getStartLocation());
                } else {
                    thisChain.setBaseLocation(hostBone.getEndLocation());
                }

                // Now that we've clamped the base location of this chain to the start or end point of the bone in the chain we are connected to, it's
                // time to deal with any base bone constraints...

                // What type of base bone constraint is this (connected to another) chain using?
                BaseboneConstraintType constraintType = thisChain.getBaseboneConstraintType();
                switch (constraintType) {
                    // None or global basebone constraints? Nothing to do, because these will be handled in FabrikChain3D.solveIK() as we do not
                    // need information from another chain to handle them.
                    case NONE:         // Nothing to do because there's no basebone constraint
                    case GLOBAL_ROTOR: // Nothing to do because the basebone constraint is not relative to bones in other chains in this structure
                    case GLOBAL_HINGE: // Nothing to do because the basebone constraint is not relative to bones in other chains in this structure
                        break;

                    // If we have a local rotor or hinge constraint then we must calculate the relative basebone constraint before calling updateTarget
                    case LOCAL_ROTOR:
                    case LOCAL_HINGE: {
                        // Get the direction of the bone this chain is connected to and create a rotation matrix from it.
                        Matrix3f connectionBoneMatrix = VectorUtils.createRotationMatrix(hostBone.getDirectionUV());

                        // We'll then get the basebone constraint UV and multiply it by the rotation matrix of the connected bone
                        // to make the basebone constraint UV relative to the direction of bone it's connected to.
                        Vector3f relativeBaseboneConstraintUV = VectorUtils.matMulVec(connectionBoneMatrix, thisChain.getBaseboneConstraintUV()).normalize();

                        // Update our basebone relative constraint UV property
                        thisChain.setBaseboneRelativeConstraintUV(relativeBaseboneConstraintUV);

                        // Updat the relative reference constraint UV if we hav a local hinge
                        if (constraintType == BaseboneConstraintType.LOCAL_HINGE) {
                            thisChain.setBaseboneRelativeReferenceConstraintUV(VectorUtils.matMulVec(connectionBoneMatrix, thisChain.getBone(0).getJoint().getHingeReferenceAxis()));
                        }
                        break;
                    }

                    // No need for a default - constraint types are enums and we've covered them all.
                }

                // NOTE: If the base bone constraint type is NONE then we don't do anything with the base bone constraint of the connected chain.

                // Finally, update the target and solve the chain
                // Update the target and solve the chain
                if (!thisChain.getEmbeddedTargetMode()) {
                    thisChain.solveForTarget(newTargetLocation);
                } else {
                    thisChain.solveForEmbeddedTarget();
                }

            } // End of if chain is connected to another chain section

        } // End of loop over chains

    } // End of updateTarget method

    /**
     * Add a FabrikChain3D to this FabrikStructure3D.
     * <p>
     * In effect, the chain is added to the mChains list of FabrikChain3D objects, and the mNumChains property is incremented.
     * <p>
     * Adding a chain using this method adds the chain to the structure, but does not connect it to any existing chain
     * in the structure. If you wish to connect a chain, use one of the connectChain methods instead.
     * <p>
     * All chains in a structure share the same target, and all chains in the structure can be solved for the target location
     * via a single call to updateTarget.
     *
     * @param chain (FabrikChain3D)	The FabrikChain3D to add to this structure.
     * @see #connectChain(IKChain, int, int, BoneConnectionPoint)
     * @see #connectChain(IKChain, int, int)
     **/

    public void addChain(IKChain chain) {
        mChains.add(chain);
    }

    /**
     * Remove a FabrikChain3D from this FabrikStructure3D by its index.
     * <p>
     * In effect, the chain is removed from the mChains list of FabrikChain3D objects, and the mNumChains property is decremented.
     *
     * @param chainIndex The index of the chain to remove from the mChains list of FabrikChain3D objects.
     **/
    public void removeChain(int chainIndex) {
        mChains.remove(chainIndex);
    }

    /**
     * Connect a chain to an existing chain in this structure.
     * <p>
     * Both chains and bones are are zero indexed.
     * <p>
     * If the existingChainNumber or existingBoneNumber specified to connect to does not exist in this structure
     * then an IllegalArgumentExeception is thrown.
     *
     * @param newChain            The chain to connect to this structure
     * @param existingChainNumber The index of the chain to connect the new chain to.
     * @param existingBoneNumber  The index of the bone to connect the new chain to within the existing chain.
     */

    public void connectChain(IKChain newChain, int existingChainNumber, int existingBoneNumber) {
        // Does this chain exist? If not throw an IllegalArgumentException
        if (existingChainNumber > this.mChains.size()) {
            throw new IllegalArgumentException("Cannot connect to chain " + existingChainNumber + " - no such chain (remember that chains are zero indexed).");
        }

        // Do we have this bone in the specified chain? If not throw an IllegalArgumentException
        if (existingBoneNumber > mChains.get(existingChainNumber).getNumBones()) {
            throw new IllegalArgumentException("Cannot connect to bone " + existingBoneNumber + " of chain " + existingChainNumber + " - no such bone (remember that bones are zero indexed).");
        }

        // Make a copy of the provided chain so any changes made to the original do not affect this chain
        IKChain relativeChain = new IKChain(newChain);

        // Connect the copy of the provided chain to the specified chain and bone in this structure
        relativeChain.connectToStructure(this, existingChainNumber, existingBoneNumber);

        // The chain as we were provided should be centred on the origin, so we must now make it
        // relative to the start location of the given bone in the given chain.

        // Get the connection point so we know to connect at the start or end location of the bone we're connecting to
        BoneConnectionPoint connectionPoint = this.getChain(existingChainNumber).getBone(existingBoneNumber).getBoneConnectionPoint();
        Vector3f connectionLocation;
        if (connectionPoint == BoneConnectionPoint.START) {
            connectionLocation = mChains.get(existingChainNumber).getBone(existingBoneNumber).getStartLocation();
        } else // If it's BoneConnectionPoint.END then we set the connection point to be the end location of the bone we're connecting to
        {
            connectionLocation = mChains.get(existingChainNumber).getBone(existingBoneNumber).getEndLocation();
        }
        relativeChain.setBaseLocation(connectionLocation);

        // When we have a chain connected to a another 'host' chain, the chain is which is connecting in
        // MUST have a fixed base, even though that means the base location is 'fixed' to the connection
        // point on the host chain, rather than a static location.
        relativeChain.setFixedBaseMode(true);

        // Translate the chain we're connecting to the connection point
        for (int loop = 0; loop < relativeChain.getNumBones(); ++loop) {
            Vector3f origStart = relativeChain.getBone(loop).getStartLocation();
            Vector3f origEnd = relativeChain.getBone(loop).getEndLocation();

            Vector3f translatedStart = origStart.add(connectionLocation, new Vector3f());
            Vector3f translatedEnd = origEnd.add(connectionLocation, new Vector3f());

            relativeChain.getBone(loop).setStartLocation(translatedStart);
            relativeChain.getBone(loop).setEndLocation(translatedEnd);
        }

        this.addChain(relativeChain);
    }

    /**
     * Connect a chain to an existing chain in this structure.
     * <p>
     * Both chains and bones are are zero indexed.
     * <p>
     * If the existingChainNumber or existingBoneNumber specified to connect to does not exist in this structure
     * then an IllegalArgumentExeception is thrown.
     *
     * @param newChain            The chain to connect to this structure
     * @param existingChainNumber The index of the chain to connect the new chain to.
     * @param existingBoneNumber  The index of the bone to connect the new chain to within the existing chain.
     * @param boneConnectionPoint Whether the new chain should connect to the START or END of the specified bone in the specified chain.
     */

    public void connectChain(IKChain newChain, int existingChainNumber, int existingBoneNumber, BoneConnectionPoint boneConnectionPoint) {
        // Does this chain exist? If not throw an IllegalArgumentException
        if (existingChainNumber > this.mChains.size()) {
            throw new IllegalArgumentException("Cannot connect to chain " + existingChainNumber + " - no such chain (remember that chains are zero indexed).");
        }

        // Do we have this bone in the specified chain? If not throw an IllegalArgumentException
        if (existingBoneNumber > mChains.get(existingChainNumber).getNumBones()) {
            throw new IllegalArgumentException("Cannot connect to bone " + existingBoneNumber + " of chain " + existingChainNumber + " - no such bone (remember that bones are zero indexed).");
        }

        // Make a copy of the provided chain so any changes made to the original do not affect this chain
        IKChain relativeChain = new IKChain(newChain);

        // Connect the copy of the provided chain to the specified chain and bone in this structure
        relativeChain.connectToStructure(this, existingChainNumber, existingBoneNumber);

        // The chain as we were provided should be centred on the origin, so we must now make it
        // relative to the start location of the given bone in the given chain.

        // Set the connection point and use it to get the connection location
        this.getChain(existingChainNumber).getBone(existingBoneNumber).setBoneConnectionPoint(boneConnectionPoint);
        Vector3f connectionLocation;
        if (boneConnectionPoint == BoneConnectionPoint.START) {
            connectionLocation = mChains.get(existingChainNumber).getBone(existingBoneNumber).getStartLocation();
        } else // If it's BoneConnectionPoint.END then we set the connection point to be the end location of the bone we're connecting to
        {
            connectionLocation = mChains.get(existingChainNumber).getBone(existingBoneNumber).getEndLocation();
        }
        relativeChain.setBaseLocation(connectionLocation);

        // When we have a chain connected to a another 'host' chain, the chain is which is connecting in
        // MUST have a fixed base, even though that means the base location is 'fixed' to the connection
        // point on the host chain, rather than a static location.
        relativeChain.setFixedBaseMode(true);

        // Translate the chain we're connecting to the connection point
        for (int loop = 0; loop < relativeChain.getNumBones(); ++loop) {
            Vector3f origStart = relativeChain.getBone(loop).getStartLocation();
            Vector3f origEnd = relativeChain.getBone(loop).getEndLocation();

            Vector3f translatedStart = origStart.add(connectionLocation, new Vector3f());
            Vector3f translatedEnd = origEnd.add(connectionLocation, new Vector3f());

            relativeChain.getBone(loop).setStartLocation(translatedStart);
            relativeChain.getBone(loop).setEndLocation(translatedEnd);
        }

        this.addChain(relativeChain);
    }

    /**
     * Return the number of chains in this structure.
     *
     * @return The number of chains in this structure.
     */

    public int getNumChains() {
        return this.mChains.size();
    }

    /**
     * Return the specified chain from this structure.
     * <p>
     * Chain numbers are zero indexed. If the specified chain does not exist in this structure
     * then an IllegalArgumentException is thrown.
     *
     * @param chainNumber The specified chain from this structure.
     * @return The specified FabrikChain3D from this chain.
     */

    public IKChain getChain(int chainNumber) {
        return mChains.get(chainNumber);
    }

    /**
     * Set the fixed base mode on all chains in this structure.
     *
     * @param fixedBaseMode Whether all chains should operate in fixed base mode (true) or not (false).
     */
    public void setFixedBaseMode(boolean fixedBaseMode) {
        for (int loop = 0; loop < this.mChains.size(); ++loop) {
            mChains.get(loop).setFixedBaseMode(fixedBaseMode);
        }
    }

    /**
     * Set the name of this structure, capped to 100 characters if required.
     *
     * @param name The name to set.
     */

    public void setName(String name) {
        mName = name;
    }

    /**
     * @return The name for this structure
     */

    public String getName() {
        return this.mName;
    }

    /**
     * Return a concise, human readable description of this FabrikStructure3D.
     * <p>
     * If further details on a specific chain are required, then you should get and print each chain individually.
     *
     * @return A concise, human readable description of this FabrikStructure3D.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("----- FabrikStructure3D: " + mName + " -----" + NEW_LINE);

        sb.append("Number of chains: " + this.mChains.size() + NEW_LINE);

        for (int loop = 0; loop < this.mChains.size(); ++loop) {
            sb.append(mChains.get(loop).toString());
        }

        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mChains == null) ? 0 : mChains.hashCode());
        result = prime * result + ((mName == null) ? 0 : mName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        IKStructure other = (IKStructure) obj;
        if (mChains == null) {
            if (other.mChains != null) {
                return false;
            }
        } else if (!mChains.equals(other.mChains)) {
            return false;
        }
        if (mName == null) {
            if (other.mName != null) {
                return false;
            }
        } else if (!mName.equals(other.mName)) {
            return false;
        }
        return true;
    }

} // End of FabrikStructure3D class
