package com.m4thg33k.lit.api;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.EnumFacing;

public class LitStateProps {

    public static final PropertyEnum<EnumFacing> CARDINALS = PropertyEnum.create("facing",EnumFacing.class,EnumFacing.Plane.HORIZONTAL);
    public static final PropertyEnum<EnumFacing> CONNECTIONS = PropertyEnum.create("connections",EnumFacing.class,EnumFacing.VALUES);
}
