package cn.nukkit.item.customitem;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.ItemDurable;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.ByteTag;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author lt_name
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public abstract class ItemCustomTool extends ItemCustom implements ItemDurable {

    public static final int TIER_WOODEN = ItemTool.TIER_WOODEN;
    public static final int TIER_GOLD = ItemTool.TIER_GOLD;
    public static final int TIER_STONE = ItemTool.TIER_STONE;
    public static final int TIER_IRON = ItemTool.TIER_IRON;
    public static final int TIER_DIAMOND = ItemTool.TIER_DIAMOND;
    public static final int TIER_NETHERITE = ItemTool.TIER_NETHERITE;

    public static final int TYPE_NONE = ItemTool.TYPE_NONE;
    public static final int TYPE_SWORD = ItemTool.TYPE_SWORD;
    public static final int TYPE_SHOVEL = ItemTool.TYPE_SHOVEL;
    public static final int TYPE_PICKAXE = ItemTool.TYPE_PICKAXE;
    public static final int TYPE_AXE = ItemTool.TYPE_AXE;
    public static final int TYPE_SHEARS = ItemTool.TYPE_SHEARS;
    public static final int TYPE_HOE = ItemTool.TYPE_HOE;

    public static final int DURABILITY_WOODEN = ItemTool.DURABILITY_WOODEN;
    public static final int DURABILITY_GOLD = ItemTool.DURABILITY_GOLD;
    public static final int DURABILITY_STONE = ItemTool.DURABILITY_STONE;
    public static final int DURABILITY_IRON = ItemTool.DURABILITY_IRON;
    public static final int DURABILITY_DIAMOND = ItemTool.DURABILITY_DIAMOND;
    public static final int DURABILITY_NETHERITE = ItemTool.DURABILITY_NETHERITE;
    public static final int DURABILITY_FLINT_STEEL = ItemTool.DURABILITY_FLINT_STEEL;
    public static final int DURABILITY_SHEARS = ItemTool.DURABILITY_SHEARS;
    public static final int DURABILITY_BOW = ItemTool.DURABILITY_BOW;
    public static final int DURABILITY_CROSSBOW = ItemTool.DURABILITY_CROSSBOW;
    public static final int DURABILITY_TRIDENT = ItemTool.DURABILITY_TRIDENT;
    public static final int DURABILITY_FISHING_ROD = ItemTool.DURABILITY_FISHING_ROD;
    public static final int DURABILITY_CARROT_ON_A_STICK = ItemTool.DURABILITY_CARROT_ON_A_STICK;
    public static final int DURABILITY_WARPED_FUNGUS_ON_A_STICK = ItemTool.DURABILITY_WARPED_FUNGUS_ON_A_STICK;

    public ItemCustomTool(@Nonnull String id, @Nullable String name) {
        super(id, name);
    }

    public ItemCustomTool(@Nonnull String id, @Nullable String name, @Nonnull String textureName) {
        super(id, name, textureName);
    }

    @Override
    public int getCreativeCategory() {
        return 3;
    }

    @Override
    public CompoundTag getComponentsData() {
        CompoundTag data = super.getComponentsData();

        data.getCompound("components")
                .putCompound("minecraft:durability",
                        new CompoundTag().putInt("max_durability", this.getMaxDurability())
                )
                .getCompound("item_properties")
                .putInt("damage", this.getAttackDamage())
                .putInt("enchantable_value", this.getEnchantableValue());

        if (this.isPickaxe()) {
            data.getCompound("components").getCompound("item_properties")
                    .putString("enchantable_slot", "pickaxe");
            data.getCompound("components")
                    .putCompound("minecraft:digger", getPickaxeDiggerNBT());
        } else if (this.isAxe()) {
            data.getCompound("components").getCompound("item_properties")
                    .putString("enchantable_slot", "axe");
            data.getCompound("components")
                    .putCompound("minecraft:digger", getAxeDiggerNBT());
        } else if (this.isShovel()) {
            data.getCompound("components").getCompound("item_properties")
                    .putString("enchantable_slot", "shovel");
            data.getCompound("components")
                    .putCompound("minecraft:digger", getShovelDiggerNBT());
        } else if (this.isHoe()) {
            data.getCompound("components").getCompound("item_properties")
                    .putString("enchantable_slot", "hoe");
        } else if (this.isSword()) {
            data.getCompound("components").getCompound("item_properties")
                    .putString("enchantable_slot", "sword");
        }
        return data;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean useOn(Block block) {
        if (this.isUnbreakable() || isDurable() || noDamageOnBreak()) {
            return true;
        }

        if (block.getToolType() == ItemTool.TYPE_PICKAXE && this.isPickaxe() ||
                block.getToolType() == ItemTool.TYPE_SHOVEL && this.isShovel() ||
                block.getToolType() == ItemTool.TYPE_AXE && this.isAxe() ||
                block.getToolType() == ItemTool.TYPE_HOE && this.isHoe() ||
                block.getToolType() == ItemTool.TYPE_SWORD && this.isSword() ||
                block.getToolType() == ItemTool.TYPE_SHEARS && this.isShears()
        ) {
            this.meta++;
        } else if (!this.isShears() && block.calculateBreakTime(this) > 0) {
            this.meta += 2;
        } else if (this.isHoe()) {
            if (block.getId() == GRASS || block.getId() == DIRT) {
                this.meta++;
            }
        } else {
            this.meta++;
        }

        if (this.meta > this.getMaxDurability()) {
            this.count--;
        }

        return true;
    }

    @Override
    public boolean useOn(Entity entity) {
        if (this.isUnbreakable() || isDurable() || noDamageOnAttack()) {
            return true;
        }

        if ((entity != null) && !this.isSword()) {
            this.meta += 2;
        } else {
            this.meta++;
        }

        if (this.meta > this.getMaxDurability()) {
            this.count--;
        }

        return true;
    }

    private boolean isDurable() {
        if (!hasEnchantments()) {
            return false;
        }

        Enchantment durability = getEnchantment(Enchantment.ID_DURABILITY);
        return durability != null && durability.getLevel() > 0 && (100 / (durability.getLevel() + 1)) <= Utils.random.nextInt(100);
    }

    @Override
    public boolean isUnbreakable() {
        Tag tag = this.getNamedTagEntry("Unbreakable");
        return tag instanceof ByteTag && ((ByteTag) tag).data > 0;
    }

    @Override
    public boolean isTool() {
        return true;
    }

    @Override
    public int getMaxDurability() {
        return DURABILITY_WOODEN;
    }

    @Override
    public int getEnchantAbility() {
        int tier = this.getTier();
        switch (tier) {
            case TIER_STONE:
                return 5;
            case TIER_WOODEN:
            case TIER_DIAMOND:
                return 10;
            case TIER_GOLD:
                return 22;
            case TIER_IRON:
                return 14;
        }

        if (tier == TIER_NETHERITE) {
            return 15;
        }
        return 0;
    }

    /**
     * No damage to item when it's used to attack entities
     *
     * @return whether the item should take damage when used to attack entities
     */
    public boolean noDamageOnAttack() {
        return false;
    }

    /**
     * No damage to item when it's used to break blocks
     *
     * @return whether the item should take damage when used to break blocks
     */
    public boolean noDamageOnBreak() {
        return false;
    }

    /**
     * 控制工具的挖掘速度(默认0 使用工具等级计算挖掘速度)
     *
     * @return 挖掘速度
     */
    public int getDestroySpeeds() {
        return 0;
    }

    public CompoundTag getPickaxeDiggerNBT() {
        if (this.getTier() == 0) {
            return new CompoundTag().putBoolean("use_efficiency", true);
        }
        var speed = switch (this.getTier()) {
            case 6 -> 7;
            case 5 -> 6;
            case 4 -> 5;
            case 3 -> 4;
            case 2 -> 3;
            case 1 -> 2;
            default -> 1;
        };
        int customSpeed = getDestroySpeeds();
        if (customSpeed > 0) speed = customSpeed;
        CompoundTag diggerRoot = new CompoundTag().putBoolean("use_efficiency", true);
        ListTag<Tag> destroy_speeds = new ListTag<>("destroy_speeds");
        destroy_speeds.add(new CompoundTag()
                .putCompound("block",
                        new CompoundTag()
                                .putString("tags", "q.any_tag('stone', 'metal', 'diamond_pick_diggable', 'mob_spawner', 'rail')")
                )
                .putInt("speed", speed));
        return diggerRoot.putList(destroy_speeds);
    }

    public CompoundTag getAxeDiggerNBT() {
        if (this.getTier() == 0) {
            return new CompoundTag().putBoolean("use_efficiency", true);
        }
        var speed = switch (this.getTier()) {
            case 6 -> 7;
            case 5 -> 6;
            case 4 -> 5;
            case 3 -> 4;
            case 2 -> 3;
            case 1 -> 2;
            default -> 1;
        };
        int customSpeed = getDestroySpeeds();
        if (customSpeed > 0) speed = customSpeed;
        CompoundTag diggerRoot = new CompoundTag().putBoolean("use_efficiency", true);
        ListTag<Tag> destroy_speeds = new ListTag<>("destroy_speeds");
        destroy_speeds.add(new CompoundTag()
                .putCompound("block",
                        new CompoundTag()
                                .putString("tags", "q.any_tag('wood', 'pumpkin', 'plant')"))
                .putInt("speed", speed));
        return diggerRoot.putList(destroy_speeds);
    }

    public CompoundTag getShovelDiggerNBT() {
        if (this.getTier() == 0) {
            return new CompoundTag().putBoolean("use_efficiency", true);
        }
        var speed = switch (this.getTier()) {
            case 6 -> 7;
            case 5 -> 6;
            case 4 -> 5;
            case 3 -> 4;
            case 2 -> 3;
            case 1 -> 2;
            default -> 1;
        };
        int customSpeed = getDestroySpeeds();
        if (customSpeed > 0) speed = customSpeed;
        CompoundTag diggerRoot = new CompoundTag().putBoolean("use_efficiency", true);
        ListTag<Tag> destroy_speeds = new ListTag<>("destroy_speeds");
        destroy_speeds.add(new CompoundTag()
                .putCompound("block",
                        new CompoundTag()
                                .putString("tags", "q.any_tag('sand', 'dirt', 'gravel', 'snow')"))
                .putInt("speed", speed));
        return diggerRoot.putList(destroy_speeds);
    }

}
