package stanuwu.missingmagic.spellcomp.tricks;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import stanuwu.missingmagic.Utils;
import stanuwu.missingmagic.network.packets.BroadcastableParticles;
import stanuwu.missingmagic.spellcomp.Errors;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

import java.util.*;

public class TrickMelt extends PieceTrick {
    SpellParam<Vector3> position;

    public TrickMelt(Spell spell) {
        super(spell);
    }

    @Override
    public void initParams() {
        addParam(position = new ParamVector(SpellParam.GENERIC_NAME_POSITION, SpellParam.BLUE, false, false));
    }

    @Override
    public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
        super.addToMetadata(meta);
        meta.addStat(EnumSpellStat.POTENCY, 20);
        meta.addStat(EnumSpellStat.COST, 80);
    }

    @Override
    public Object execute(SpellContext ctx) throws SpellRuntimeException {
        Vector3 positionV = getParamValueOrDefault(ctx, position, null);
        World world = ctx.focalPoint.getEntityWorld();
        if (!ctx.isInRadius(positionV)) Errors.runtime(SpellRuntimeException.OUTSIDE_RADIUS);
        else {
            BlockPos blockpos = positionV.toBlockPos();
            doMelt(blockpos, ctx);
        }
        return null;
    }

    public static void doMelt(BlockPos pos, SpellContext ctx) {
        List<Block> stoneTypeBlocks = new ArrayList<Block>(Arrays.asList(
                Blocks.STONE,
                Blocks.COBBLESTONE,
                Blocks.ANDESITE,
                Blocks.DIORITE,
                Blocks.GRANITE,
                Blocks.BLACKSTONE,
                Blocks.POLISHED_ANDESITE,
                Blocks.POLISHED_DIORITE,
                Blocks.POLISHED_GRANITE,
                Blocks.POLISHED_BLACKSTONE,
                Blocks.BASALT,
                Blocks.POLISHED_BASALT,
                Blocks.SMOOTH_STONE,
                Blocks.STONE_BRICKS,
                Blocks.CHISELED_STONE_BRICKS,
                Blocks.CRACKED_STONE_BRICKS,
                Blocks.POLISHED_BLACKSTONE_BRICKS,
                Blocks.CHISELED_POLISHED_BLACKSTONE,
                Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS
        )) {};

        World world = ctx.focalPoint.getEntityWorld();
        BlockState state= world.getBlockState(pos);
        boolean molten = false;
        Material mat = state.getMaterial();
        if (mat == Material.ICE || mat == Material.PACKED_ICE ||mat == Material.SNOW_BLOCK) {
            world.setBlockState(pos, Blocks.WATER.getDefaultState());
            molten = true;
        } else if (mat == Material.SNOW) {
            int layers = world.getBlockState(pos).get(SnowBlock.LAYERS);
            if (layers > 7) {
                world.setBlockState(pos, Blocks.WATER.getDefaultState());
            } else {
                BlockState block = Blocks.WATER.getDefaultState().with(FlowingFluidBlock.LEVEL, layers);
                world.setBlockState(pos, block);
            }
            molten = true;
        } else if (stoneTypeBlocks.contains(state.getBlock())) {
            world.setBlockState(pos, Blocks.LAVA.getDefaultState().with(FlowingFluidBlock.LEVEL, 8));
            molten = true;
        } else if (state.getBlock() == Blocks.OBSIDIAN) {
            world.setBlockState(pos, Blocks.LAVA.getDefaultState());
            molten = true;
        } else if (mat == Material.WATER) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            molten = true;
        }
        if (molten) {
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
            for (int l = 0; l < 8; ++l) {
                Utils.BroadcastParticles(world, new BroadcastableParticles(ParticleTypes.LARGE_SMOKE, pos.add(.5+ Math.random(), .5+ Math.random(), .5+ Math.random()), world.getDimensionKey().getLocation()));
            }
        }
    }
}
