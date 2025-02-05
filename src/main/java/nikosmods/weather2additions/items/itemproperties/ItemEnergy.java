package nikosmods.weather2additions.items.itemproperties;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ItemEnergy extends Item {
    private final int maxEnergy;
    private final int throughputIn;
    private final int throughputOut;
    private int stored = 50000;

    @Override
    public boolean isBarVisible(ItemStack p_150899_) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack p_150900_) {
        return (getTag(p_150900_).getInt("CurrentEnergy") * 13) / maxEnergy;
    }

    @Override
    public int getBarColor(ItemStack p_150901_) {
        int barColour = 0xFF00FF;
        if (getBarWidth(p_150901_) < 4) {
            barColour = 0xFF0000;
        }
        else if (getBarWidth(p_150901_) < 8)  {
            barColour = 0xFFFF00;
        }
        else if (getBarWidth(p_150901_) <= 13) {
            barColour = 0x00FF00;
        }
        return barColour;
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        IEnergyStorage energyStorage = new IEnergyStorage() {
            @Override
            public int receiveEnergy(int i, boolean b) {
                int transferred = Math.min(maxEnergy - getEnergyStored(), i);
                if (!b) {
                    getTag(stack).putInt("CurrentEnergy", getEnergyStored() + transferred);
                }

                return transferred;
            }

            @Override
            public int extractEnergy(int energy, boolean simulate) {
                int transferred = Math.min(energy, getTag(stack).getInt("CurrentEnergy"));
                transferred = Math.min(transferred, throughputOut);
                if (!simulate) {
                    getTag(stack).putInt("CurrentEnergy", getEnergyStored() - transferred);
                }
                return transferred;
            }

            @Override
            public int getEnergyStored() {
                return getTag(stack).getInt("CurrentEnergy");
            }

            @Override
            public int getMaxEnergyStored() {
                return maxEnergy;
            }

            @Override
            public boolean canExtract() {
                return throughputIn > 0;
            }

            @Override
            public boolean canReceive() {
                return throughputOut > 0;
            }
        };
        return new ICapabilityProvider() {
            @Override
            public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
                if (capability == ForgeCapabilities.ENERGY) {
                    return LazyOptional.of(() -> energyStorage).cast();
                }
                return LazyOptional.empty();
            }
        };
    }

    public CompoundTag getTag(ItemStack stack) {
        if (!stack.hasTag()) {
            CompoundTag tag = new CompoundTag();
            stack.setTag(tag);
            tag.putInt("CurrentEnergy", maxEnergy);
        }
        return stack.getTag();
    }

    public ItemEnergy(Properties properties, int paramMaxEnergy, int throughputIn, int throughputOut) {
        super(properties);
        maxEnergy = paramMaxEnergy;
        this.throughputIn = throughputIn;
        this.throughputOut = throughputOut;
    }
    public int getMaxEnergy() {
        return maxEnergy;
    }

}
