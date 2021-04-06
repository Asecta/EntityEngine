package au.edu.federation.caliko;

/**
 * @author jsalvo
 */
public enum BaseboneConstraintType {
    NONE,         // No constraint - basebone may rotate freely
    GLOBAL_ROTOR, // World-space rotor constraint
    LOCAL_ROTOR,  // Rotor constraint in the coordinate space of (i.e. relative to) the direction of the connected bone
    GLOBAL_HINGE, // World-space hinge constraint
    LOCAL_HINGE   // Hinge constraint in the coordinate space of (i.e. relative to) the direction of the connected bone
}
