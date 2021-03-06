package com.github.technus.tectech.thing.metaTileEntity.multi;

import com.github.technus.tectech.Reference;
import com.github.technus.tectech.mechanics.constructable.IConstructable;
import com.github.technus.tectech.mechanics.dataTransport.QuantumDataPacket;
import com.github.technus.tectech.mechanics.structure.adders.IHatchAdder;
import com.github.technus.tectech.mechanics.structure.Structure;
import com.github.technus.tectech.thing.metaTileEntity.hatch.GT_MetaTileEntity_Hatch_InputData;
import com.github.technus.tectech.thing.metaTileEntity.hatch.GT_MetaTileEntity_Hatch_OutputData;
import com.github.technus.tectech.thing.metaTileEntity.multi.base.GT_MetaTileEntity_MultiblockBase_EM;
import com.github.technus.tectech.thing.metaTileEntity.multi.base.INameFunction;
import com.github.technus.tectech.thing.metaTileEntity.multi.base.IStatusFunction;
import com.github.technus.tectech.thing.metaTileEntity.multi.base.Parameters;
import com.github.technus.tectech.thing.metaTileEntity.multi.base.render.TT_RenderedExtendedFacingTexture;
import com.github.technus.tectech.util.CommonValues;
import com.github.technus.tectech.util.Vec3Impl;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import static com.github.technus.tectech.mechanics.structure.Structure.adders;
import static com.github.technus.tectech.thing.casing.GT_Block_CasingsTT.textureOffset;
import static com.github.technus.tectech.thing.casing.GT_Block_CasingsTT.texturePage;
import static com.github.technus.tectech.thing.casing.TT_Container_Casings.sBlockCasingsTT;
import static com.github.technus.tectech.thing.metaTileEntity.multi.base.LedStatus.*;
import static com.github.technus.tectech.util.CommonValues.V;
import static net.minecraft.util.StatCollector.translateToLocal;

/**
 * Created by danie_000 on 17.12.2016.
 */
public class GT_MetaTileEntity_EM_switch extends GT_MetaTileEntity_MultiblockBase_EM implements IConstructable {
    //region structure
    private static final String[][] shape = new String[][]{
            {"   ", " . ", "   ",},
            {"   ", " 0 ", "   ",},
            {"   ", "   ", "   ",},
    };
    private static final Block[] blockType = new Block[]{sBlockCasingsTT};
    private static final byte[] blockMeta = new byte[]{3};
    private static final IHatchAdder<GT_MetaTileEntity_EM_switch>[] addingMethods = adders(
            GT_MetaTileEntity_EM_switch::addClassicToMachineList);
    private static final short[] casingTextures = new short[]{textureOffset + 1};
    private static final Block[] blockTypeFallback = new Block[]{sBlockCasingsTT};
    private static final byte[] blockMetaFallback = new byte[]{1};
    private static final String[] description = new String[]{
            EnumChatFormatting.AQUA + translateToLocal("tt.keyphrase.Hint_Details") + ":",
            "1 - Classic/Data Hatches or Computer casing",//1 - Classic/Data Hatches or Computer casing
    };
    //endregion

    //region parameters
    private static final INameFunction<GT_MetaTileEntity_EM_switch> ROUTE_NAME =
            (base, p) -> (p.parameterId() == 0 ? translateToLocal("tt.keyword.Destination") + " " : translateToLocal("tt.keyword.Weight") + " ") + p.hatchId();
    private static final IStatusFunction<GT_MetaTileEntity_EM_switch> WEI_STATUS =
            (base, p) -> {
                double v = p.get();
                if (Double.isNaN(v)) return STATUS_WRONG;
                if (v < 0) return STATUS_TOO_LOW;
                if (v == 0) return STATUS_LOW;
                if (Double.isInfinite(v)) return STATUS_HIGH;
                return STATUS_OK;
            };
    private static final IStatusFunction<GT_MetaTileEntity_EM_switch> DST_STATUS =
            (base, p) -> {
                if (base.weight[p.hatchId()].getStatus(false).isOk) {
                    double v = p.get();
                    if (Double.isNaN(v)) return STATUS_WRONG;
                    v = (int) v;
                    if (v <= 0) return STATUS_TOO_LOW;
                    if (v >= base.eOutputHatches.size()) return STATUS_TOO_HIGH;
                    return STATUS_OK;
                }
                return STATUS_NEUTRAL;
            };
    protected Parameters.Group.ParameterIn[] dst;
    protected Parameters.Group.ParameterIn[] weight;
    //endregion

    public GT_MetaTileEntity_EM_switch(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_EM_switch(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_EM_switch(mName);
    }

    @Override
    public boolean checkMachine_EM(IGregTechTileEntity iGregTechTileEntity, ItemStack itemStack) {
        return structureCheck_EM(shape, blockType, blockMeta, addingMethods, casingTextures, blockTypeFallback, blockMetaFallback, 1, 1, 0);
    }

    @Override
    public boolean checkRecipe_EM(ItemStack itemStack) {
        short thingsActive = 0;
        for (GT_MetaTileEntity_Hatch_InputData di : eInputData) {
            if (di.q != null) {
                thingsActive++;
            }
        }

        if (thingsActive > 0) {
            thingsActive += eOutputData.size();
            mEUt = -(int) V[7];
            eAmpereFlow = 1 + (thingsActive >> 2);
            mMaxProgresstime = 20;
            mEfficiencyIncrease = 10000;
            return true;
        }
        return false;
    }

    @Override
    public void outputAfterRecipe_EM() {
        if (!eOutputData.isEmpty()) {
            double total = 0;
            double weight;
            for (int i = 0; i < 10; i++) {//each param pair
                weight = this.weight[i].get();
                if (weight > 0 && dst[i].get() >= 0) {
                    total += weight;//Total weighted div
                }
            }

            Vec3Impl pos = new Vec3Impl(getBaseMetaTileEntity());
            QuantumDataPacket pack = new QuantumDataPacket(0L).unifyTraceWith(pos);
            if (pack == null) {
                return;
            }
            for (GT_MetaTileEntity_Hatch_InputData hatch : eInputData) {
                if (hatch.q == null || hatch.q.contains(pos)) {
                    continue;
                }
                pack = pack.unifyPacketWith(hatch.q);
                if (pack == null) {
                    return;
                }
            }

            long remaining = pack.getContent();

            double dest;
            for (int i = 0; i < 10; i++) {
                dest = dst[i].get();
                weight = this.weight[i].get();
                if (weight > 0 && dest >= 0) {
                    int outIndex = (int) dest - 1;
                    if (outIndex < 0 || outIndex >= eOutputData.size()) {
                        continue;
                    }
                    GT_MetaTileEntity_Hatch_OutputData out = eOutputData.get(outIndex);
                    if (Double.isInfinite(total)) {
                        if (Double.isInfinite(weight)) {
                            out.q = new QuantumDataPacket(remaining).unifyTraceWith(pack);
                            break;
                        }
                    } else {
                        long part = (long) Math.floor(pack.getContent() * weight / total);
                        if (part > 0) {
                            remaining -= part;
                            if (remaining > 0) {
                                out.q = new QuantumDataPacket(part).unifyTraceWith(pack);
                            } else if (part + remaining > 0) {
                                out.q = new QuantumDataPacket(part + remaining).unifyTraceWith(pack);
                                break;
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                CommonValues.TEC_MARK_EM,
                translateToLocal("gt.blockmachines.multimachine.em.switch.desc.0"),//User controlled computation power routing
                EnumChatFormatting.AQUA.toString() + EnumChatFormatting.BOLD + translateToLocal("gt.blockmachines.multimachine.em.switch.desc.1")//Quality of service is a must
        };
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive, boolean aRedstone) {
        if (aSide == aFacing) {
            return new ITexture[]{Textures.BlockIcons.casingTexturePages[texturePage][1], new TT_RenderedExtendedFacingTexture(aActive ? GT_MetaTileEntity_MultiblockBase_EM.ScreenON : GT_MetaTileEntity_MultiblockBase_EM.ScreenOFF)};
        }
        return new ITexture[]{Textures.BlockIcons.casingTexturePages[texturePage][1]};
    }

    public final static ResourceLocation activitySound = new ResourceLocation(Reference.MODID + ":fx_hi_freq");

    @Override
    @SideOnly(Side.CLIENT)
    protected ResourceLocation getActivitySound() {
        return activitySound;
    }

    @Override
    protected void parametersInstantiation_EM() {
        dst = new Parameters.Group.ParameterIn[10];
        weight = new Parameters.Group.ParameterIn[10];
        for (int i = 0; i < 10; i++) {
            Parameters.Group hatch = parametrization.getGroup(i);
            dst[i] = hatch.makeInParameter(0, i, ROUTE_NAME, DST_STATUS);
            weight[i] = hatch.makeInParameter(1, 0, ROUTE_NAME, WEI_STATUS);
        }
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        Structure.builder(shape, blockType, blockMeta, 1, 1, 0, getBaseMetaTileEntity(), getExtendedFacing(), hintsOnly);
    }

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
    }
}