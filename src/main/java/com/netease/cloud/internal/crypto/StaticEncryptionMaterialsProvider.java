package com.netease.cloud.internal.crypto;

import java.io.Serializable;
import java.util.Map;

public class StaticEncryptionMaterialsProvider
        implements EncryptionMaterialsProvider, Serializable {
    private final EncryptionMaterials materials;

    public StaticEncryptionMaterialsProvider(EncryptionMaterials materials) {
        this.materials = materials;
    }

    public EncryptionMaterials getEncryptionMaterials() {
        return materials;
    }

    public void refresh() {}

    public EncryptionMaterials getEncryptionMaterials(
            final Map<String, String> materialDescIn) {
        if (materials == null) {
            return null;  // nothing to match descriptions against, and no accessor
        }
        final Map<String,String> materialDesc =
            materials.getMaterialsDescription();
        if (materialDescIn != null &&  materialDescIn.equals(materialDesc)) {
            return materials;   // matching description
        }
        EncryptionMaterialsAccessor accessor = materials.getAccessor();
        if (accessor != null) {
            EncryptionMaterials accessorMaterials =
                accessor.getEncryptionMaterials(materialDescIn);
            if (accessorMaterials != null) {
                return accessorMaterials;   // accessor decided materials
            }
        }
        // The condition that there are
        //   1) no input materials description (typically from NOS); and
        //   2) no materials description for the current client-side materials; and
        //   3) the client's material accessor has no corresponding materials
        //      for null or empty materials description;
        // implies that the only sensible materials is the current client-side materials
        // (which has no description).
        boolean noMaterialDescIn = materialDescIn == null || materialDescIn.size() == 0;
        boolean noMaterialDesc = materialDesc == null || materialDesc.size() == 0;
        return noMaterialDescIn && noMaterialDesc ? materials : null;
    }
}
