package nz.tomasborsje.duskfall.core;

import net.kyori.adventure.nbt.BinaryTag;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

public class ItemStackTags {
    public static final @NotNull Tag<BinaryTag> MMO_CORE_DATA = Tag.NBT("MMO_DATA");
    public static final @NotNull Tag<BinaryTag> MMO_STAT_MODS = Tag.NBT("MMO_STAT_MODS");
}
