package com.github.technus.tectech.thing.metaTileEntity.multi;

import com.github.technus.tectech.CommonValues;
import com.github.technus.tectech.TecTech;
import com.github.technus.tectech.Util;
import com.github.technus.tectech.auxiliary.TecTechConfig;
import com.github.technus.tectech.dataFramework.quantumDataPacket;
import com.github.technus.tectech.thing.metaTileEntity.hatch.GT_MetaTileEntity_Hatch_InputData;
import com.github.technus.tectech.thing.metaTileEntity.hatch.GT_MetaTileEntity_Hatch_OutputData;
import com.github.technus.tectech.thing.metaTileEntity.hatch.GT_MetaTileEntity_Hatch_Rack;
import com.github.technus.tectech.thing.metaTileEntity.iConstructible;
import com.github.technus.tectech.vec3pos;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch;
import gregtech.api.objects.GT_RenderedTexture;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;

import static com.github.technus.tectech.Util.StructureBuilder;
import static com.github.technus.tectech.thing.casing.GT_Container_CasingsTT.sBlockCasingsTT;
import static gregtech.api.enums.GT_Values.V;

/**
 * Created by danie_000 on 17.12.2016.
 */
public class GT_MetaTileEntity_EM_computer extends GT_MetaTileEntity_MultiblockBase_EM implements iConstructible {
    private final ArrayList<GT_MetaTileEntity_Hatch_Rack> eRacks = new ArrayList<>();
    private int maxTemp = 0;

    //region Structure
    private static final String[][] front = new String[][]{{"A  ", "A  ", "A. ", "A  ",},};
    private static final String[][] terminator = new String[][]{{"A  ", "A  ", "A  ", "A  ",},};
    private static final String[][] cap = new String[][]{{"-01", "A22", "A22", "-01",},};
    private static final String[][] slice = new String[][]{{"-01", "A!2", "A!2", "-01",},};
    private static final Block[] blockType = new Block[]{sBlockCasingsTT, sBlockCasingsTT, sBlockCasingsTT};
    private static final byte[] blockMeta = new byte[]{2, 1, 3};
    private static final String[] addingMethods = new String[]{"addToMachineList", "addRackToMachineList"};
    private static final byte[] casingTextures = new byte[]{textureOffset + 1, textureOffset + 3};
    private static final Block[] blockTypeFallback = new Block[]{sBlockCasingsTT, sBlockCasingsTT};
    private static final byte[] blockMetaFallback = new byte[]{1, 3};
    //endregion

    public GT_MetaTileEntity_EM_computer(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        eCertainMode = 5;
        eCertainStatus = -128;//no-brainer value
    }

    public GT_MetaTileEntity_EM_computer(String aName) {
        super(aName);
        eCertainMode = 5;
        eCertainStatus = -128;//no-brainer value
    }

    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_EM_computer(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive, boolean aRedstone) {
        if (aSide == aFacing) {
            return new ITexture[]{Textures.BlockIcons.CASING_BLOCKS[textureOffset + 3], new GT_RenderedTexture(aActive ? ScreenON : ScreenOFF)};
        }
        return new ITexture[]{Textures.BlockIcons.CASING_BLOCKS[textureOffset + 3]};
    }

    @Override
    public boolean EM_checkRecipe(ItemStack itemStack) {
        eAvailableData = 0;
        maxTemp = 0;
        short thingsActive = 0;
        int rackComputation;

        for (GT_MetaTileEntity_Hatch_Rack r : eRacks) {
            if (!isValidMetaTileEntity(r)) continue;
            if (r.heat > maxTemp) maxTemp = r.heat;
            rackComputation = r.tickComponents(eParamsIn[0], eParamsIn[10]);
            if (rackComputation > 0) {
                eAvailableData += rackComputation;
                thingsActive += 4;
            }
            r.getBaseMetaTileEntity().setActive(true);
        }

        for (GT_MetaTileEntity_Hatch_InputData di : eInputData)
            if (di.q != null)//ok for power losses
                thingsActive++;

        if (thingsActive > 0 && eCertainStatus == 0) {
            thingsActive += eOutputData.size();
            mEUt = -(int) (V[8] * eParamsIn[10]);
            eAmpereFlow = 1 + (thingsActive >> 2);
            mMaxProgresstime = 20;
            mEfficiencyIncrease = 10000;
            return true;
        }
        mMaxProgresstime = 0;
        mEfficiencyIncrease = 0;
        EM_stopMachine();//to stop all hatches
        return false;
    }

    @Override
    protected long EM_getAvailableData() {
        return eAvailableData;
    }

    @Override
    public void EM_checkParams() {
        if (eParamsIn[0] <= 0)
            eParamsInStatus[0] = PARAM_TOO_LOW;
        else if (eParamsIn[0] < 1)
            eParamsInStatus[0] = PARAM_LOW;
        else if (eParamsIn[0] == 1)
            eParamsInStatus[0] = PARAM_OK;
        else if (eParamsIn[0] <= 3)
            eParamsInStatus[0] = PARAM_HIGH;
        else eParamsInStatus[0] = PARAM_TOO_HIGH;

        if (eParamsIn[10] <= 0.7f)
            eParamsInStatus[10] = PARAM_TOO_LOW;
        else if (eParamsIn[10] < 0.8f)
            eParamsInStatus[10] = PARAM_LOW;
        else if (eParamsIn[10] <= 1.2f)
            eParamsInStatus[10] = PARAM_OK;
        else if (eParamsIn[10] <= 2)
            eParamsInStatus[10] = PARAM_HIGH;
        else eParamsInStatus[10] = PARAM_TOO_HIGH;

        eParamsOut[0] = maxTemp;
        eParamsOut[10] = eAvailableData;

        if (maxTemp < -10000)
            eParamsOutStatus[0] = PARAM_TOO_LOW;
        else if (maxTemp < 0)
            eParamsOutStatus[0] = PARAM_LOW;
        else if (maxTemp == 0)
            eParamsOutStatus[0] = PARAM_OK;
        else if (maxTemp <= 5000)
            eParamsOutStatus[0] = PARAM_HIGH;
        else eParamsOutStatus[0] = PARAM_TOO_HIGH;
    }

    @Override
    public void EM_outputFunction() {
        if (eOutputData.size() > 0) {
            final vec3pos pos = new vec3pos(getBaseMetaTileEntity());
            quantumDataPacket pack = new quantumDataPacket(pos, eAvailableData);
            for (GT_MetaTileEntity_Hatch_InputData i : eInputData) {
                if (i.q == null || i.q.contains(pos)) continue;
                pack = pack.unifyPacketWith(i.q);
                if (pack == null) return;
            }

            pack.computation /= eOutputData.size();

            for (GT_MetaTileEntity_Hatch_OutputData o : eOutputData)
                o.q = pack;
        }
    }

    @Override
    public void onRemoval() {
        super.onRemoval();
        for (GT_MetaTileEntity_Hatch_Rack r : eRacks)
            r.getBaseMetaTileEntity().setActive(false);
    }

    @Override
    protected void EM_stopMachine() {
        for (GT_MetaTileEntity_Hatch_Rack r : eRacks)
            r.getBaseMetaTileEntity().setActive(false);
    }

    @Override
    public boolean EM_checkMachine(IGregTechTileEntity iGregTechTileEntity, ItemStack itemStack) {
        for (GT_MetaTileEntity_Hatch_Rack rack : eRacks)
            if (isValidMetaTileEntity(rack))
                rack.getBaseMetaTileEntity().setActive(false);
        eRacks.clear();
        if (!EM_StructureCheckAdvanced(front, blockType, blockMeta, addingMethods, casingTextures, blockTypeFallback, blockMetaFallback, 1, 2, 0))
            return false;
        if (!EM_StructureCheckAdvanced(cap, blockType, blockMeta, addingMethods, casingTextures, blockTypeFallback, blockMetaFallback, 1, 2, -1))
            return false;
        byte offset = -2, totalLen = 4;
        for (; offset > -16; ) {
            if (!EM_StructureCheckAdvanced(slice, blockType, blockMeta, addingMethods, casingTextures, blockTypeFallback, blockMetaFallback, 1, 2, offset))
                break;
            totalLen++;
            offset--;
        }
        if (totalLen > 16) return false;
        if (!EM_StructureCheckAdvanced(cap, blockType, blockMeta, addingMethods, casingTextures, blockTypeFallback, blockMetaFallback, 1, 2, ++offset))
            return false;
        if (!EM_StructureCheckAdvanced(terminator, blockType, blockMeta, addingMethods, casingTextures, blockTypeFallback, blockMetaFallback, 1, 2, --offset))
            return false;
        eCertainMode = (byte) Math.min(totalLen / 3, 5);
        for (GT_MetaTileEntity_Hatch_Rack rack : eRacks)
            if (isValidMetaTileEntity(rack))
                rack.getBaseMetaTileEntity().setActive(iGregTechTileEntity.isActive());
        return eUncertainHatches.size() == 1;
    }

    @Override
    public void construct(int qty) {
        IGregTechTileEntity igt=getBaseMetaTileEntity();
        StructureBuilder(front, blockType, blockMeta, 1, 2, 0, igt);
        StructureBuilder(cap, blockType, blockMeta, 1, 2, -1, igt);

        byte offset=-2;
        for (int rackSlices = qty>12?12:qty; rackSlices>0 ; rackSlices--) {
            StructureBuilder(slice, blockType, blockMeta, 1, 2, offset--, igt);
        }

        StructureBuilder(cap, blockType, blockMeta, 1, 2, offset--, igt);
        StructureBuilder(terminator, blockType, blockMeta, 1, 2, offset,igt);
    }

    @Override
    protected void EM_extraExplosions() {
        for (MetaTileEntity tTileEntity : eRacks) tTileEntity.getBaseMetaTileEntity().doExplosion(V[9]);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                CommonValues.tecMark,
                Util.intToString(TecTech.Rnd.nextInt(), 8),
                EnumChatFormatting.AQUA.toString() + EnumChatFormatting.BOLD + "You need it to process the number above"
        };
    }

    @Override
    public boolean isFacingValid(byte aFacing) {
        return aFacing >= 2;
    }

    //NEW METHOD
    public final boolean addRackToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) return false;
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) return false;
        if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_Rack) {
            ((GT_MetaTileEntity_Hatch) aMetaTileEntity).mMachineBlock = (byte) aBaseCasingIndex;
            return eRacks.add((GT_MetaTileEntity_Hatch_Rack) aMetaTileEntity);
        }
        return false;
    }

    public static void run() {
        try {
            adderMethodMap.put("addRackToMachineList", GT_MetaTileEntity_EM_computer.class.getMethod("addRackToMachineList", IGregTechTileEntity.class, int.class));
        } catch (NoSuchMethodException e) {
            if (TecTechConfig.DEBUG_MODE) e.printStackTrace();
        }
    }
}
