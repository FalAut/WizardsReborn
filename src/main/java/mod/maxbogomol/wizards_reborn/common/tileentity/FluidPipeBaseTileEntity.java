package mod.maxbogomol.wizards_reborn.common.tileentity;

import mod.maxbogomol.wizards_reborn.api.alchemy.IFluidPipePriority;
import mod.maxbogomol.wizards_reborn.api.alchemy.PipePriorityMap;
import mod.maxbogomol.wizards_reborn.utils.PacketUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;

public abstract class FluidPipeBaseTileEntity extends PipeBaseTileEntity implements IFluidPipePriority, TickableBlockEntity {

    public static final int PRIORITY_BLOCK = 0;
    public static final int PRIORITY_PIPE = PRIORITY_BLOCK;
    public static final int MAX_PUSH = 120;

    static Random random = new Random();
    boolean[] from = new boolean[Direction.values().length];
    public boolean clogged = false;
    public FluidTank tank;
    public LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> tank);
    Direction lastTransfer;
    boolean syncTank = true;
    boolean syncCloggedFlag = true;
    boolean syncTransfer = true;
    int ticksExisted;
    int lastRobin;

    public FluidPipeBaseTileEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        initFluidTank();
    }

    protected void initFluidTank() {
        tank = new FluidTank(getCapacity()) {
            @Override
            protected void onContentsChanged() {
                FluidPipeBaseTileEntity.this.syncTank = true;
                FluidPipeBaseTileEntity.this.setChanged();
            }
        };
    }

    public void onLoad() {
        syncTransfer = true;
        syncCloggedFlag = true;
        if (level instanceof ServerLevel serverLevel) {
            for (ServerPlayer serverplayer : serverLevel.getServer().getPlayerList().getPlayers()) {
                serverplayer.connection.send(this.getUpdatePacket());
            }
            this.resetSync();
        }
    }

    public abstract int getCapacity();

    @Override
    public int getPriority(Direction facing) {
        return PRIORITY_PIPE;
    }

    public void setFrom(Direction facing, boolean flag) {
        from[facing.get3DDataValue()] = flag;
    }

    public void resetFrom() {
        for (Direction facing : Direction.values()) {
            setFrom(facing, false);
        }
    }

    protected boolean isFrom(Direction facing) {
        return from[facing.get3DDataValue()];
    }

    protected boolean isAnySideUnclogged() {
        for (Direction facing : Direction.values()) {
            if (!getConnection(facing).transfer)
                continue;
            BlockEntity tile = level.getBlockEntity(worldPosition.relative(facing));
            if (tile instanceof FluidPipeBaseTileEntity && !((FluidPipeBaseTileEntity) tile).clogged)
                return true;
        }
        return false;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, (e) -> e.getUpdateTag());
    }

    public void tick() {
        if (!level.isClientSide()) {
            if (!loaded)
                initConnections();
            ticksExisted++;
            boolean fluidMoved = false;
            FluidStack passStack = tank.drain(MAX_PUSH, IFluidHandler.FluidAction.SIMULATE);
            if (!passStack.isEmpty()) {
                PipePriorityMap<Integer, Direction> possibleDirections = new PipePriorityMap<>();
                IFluidHandler[] fluidHandlers = new IFluidHandler[Direction.values().length];

                for (Direction facing : Direction.values()) {
                    if (!getConnection(facing).transfer)
                        continue;
                    if (isFrom(facing))
                        continue;
                    BlockEntity tile = level.getBlockEntity(getBlockPos().relative(facing));
                    if (tile != null) {
                        IFluidHandler handler = tile.getCapability(ForgeCapabilities.FLUID_HANDLER, facing.getOpposite()).orElse(null);
                        if (handler != null) {
                            int priority = PRIORITY_BLOCK;
                            if (tile instanceof IFluidPipePriority)
                                priority = ((IFluidPipePriority) tile).getPriority(facing.getOpposite());
                            if (isFrom(facing.getOpposite()))
                                priority -= 5;
                            possibleDirections.put(priority, facing);
                            fluidHandlers[facing.get3DDataValue()] = handler;
                        }
                    }
                }

                for (int key : possibleDirections.keySet()) {
                    ArrayList<Direction> list = possibleDirections.get(key);
                    for (int i = 0; i < list.size(); i++) {
                        Direction facing = list.get((i + lastRobin) % list.size());
                        IFluidHandler handler = fluidHandlers[facing.get3DDataValue()];
                        fluidMoved = pushStack(passStack, facing, handler);
                        if (lastTransfer != facing) {
                            syncTransfer = true;
                            lastTransfer = facing;
                            setChanged();
                        }
                        if (fluidMoved) {
                            lastRobin++;
                            break;
                        }
                    }
                    if (fluidMoved)
                        break;
                }
            }

            if (tank.getFluidAmount() <= 0) {
                if (lastTransfer != null && !fluidMoved) {
                    syncTransfer = true;
                    lastTransfer = null;
                    setChanged();
                }
                fluidMoved = true;
                resetFrom();
            }
            if (clogged == fluidMoved) {
                clogged = !fluidMoved;
                syncCloggedFlag = true;
                setChanged();
            }
        }
    }

    private boolean pushStack(FluidStack passStack, Direction facing, IFluidHandler handler) {
        int added = handler.fill(passStack, IFluidHandler.FluidAction.SIMULATE);
        if (added > 0) {
            handler.fill(passStack, IFluidHandler.FluidAction.EXECUTE);
            this.tank.drain(added, IFluidHandler.FluidAction.EXECUTE);
            passStack.setAmount(passStack.getAmount() - added);
            return passStack.getAmount() <= 0;
        }

        if (isFrom(facing))
            setFrom(facing, false);
        return false;
    }

    protected void resetSync() {
        syncTank = false;
        syncCloggedFlag = false;
        syncTransfer = false;
    }

    protected boolean requiresSync() {
        return true;
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains("clogged"))
            clogged = nbt.getBoolean("clogged");
        if (nbt.contains("tank"))
            tank.readFromNBT(nbt.getCompound("tank"));
        if (nbt.contains("lastTransfer"))
            lastTransfer = readNullableFacing(nbt.getInt("lastTransfer"));
        for(Direction facing : Direction.values())
            if(nbt.contains("from"+facing.get3DDataValue()))
                from[facing.get3DDataValue()] = nbt.getBoolean("from"+facing.get3DDataValue());
        if (nbt.contains("lastRobin"))
            lastRobin = nbt.getInt("lastRobin");
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        writeTank(nbt);
        writeCloggedFlag(nbt);
        writeLastTransfer(nbt);
        for(Direction facing : Direction.values())
            nbt.putBoolean("from"+facing.get3DDataValue(),from[facing.get3DDataValue()]);
        nbt.putInt("lastRobin",lastRobin);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        if (syncTank)
            writeTank(nbt);
        if (syncCloggedFlag)
            writeCloggedFlag(nbt);
        if (syncTransfer)
            writeLastTransfer(nbt);
        return nbt;
    }

    private void writeCloggedFlag(CompoundTag nbt) {
        nbt.putBoolean("clogged", clogged);
    }

    private void writeLastTransfer(CompoundTag nbt) {
        nbt.putInt("lastTransfer", writeNullableFacing(lastTransfer));
    }

    private void writeTank(CompoundTag nbt) {
        nbt.put("tank", tank.writeToNBT(new CompoundTag()));
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!this.remove && cap == ForgeCapabilities.FLUID_HANDLER) {
            return holder.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide) {
            PacketUtils.SUpdateTileEntityPacket(this);
            this.resetSync();
        }
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        holder.invalidate();
    }

    public static Direction readNullableFacing(int index) {
        return index > 0 ? Direction.from3DDataValue(index) : null;
    }

    public static int writeNullableFacing(Direction facing) {
        return facing != null ? facing.get3DDataValue() : -1;
    }
}
