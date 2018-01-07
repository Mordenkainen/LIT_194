package com.m4thg33k.lit.core.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeMap;

public class InventorySortHelper {

    public static ArrayList<ItemStack> sortByBlocksAndItems(ArrayList<ItemStack> arrayList, boolean forward)
    {
        ArrayList<ItemStack> toReturn = new ArrayList<ItemStack>();
        ArrayList<ItemStack> blocks = new ArrayList<ItemStack>();
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();

        for (ItemStack stack : arrayList)
        {
            if (stack.getItem() instanceof  ItemBlock)
            {
                blocks.add(stack.copy());
            }
            else
            {
                items.add(stack.copy());
            }
        }

        sortItemStackArray(blocks);
        sortItemStackArray(items);

        blocks = combineLikeStacks(blocks);
        items = combineLikeStacks(items);

        toReturn.addAll(blocks);
        toReturn.addAll(items);

        if (!forward)
        {
            Collections.reverse(toReturn);
        }

        return toReturn;
    }

    public static ItemStack[] sortByMod(IInventory inventory, boolean forward)
    {
        int size = inventory.getSizeInventory();
        ItemStack[] toReturn = new ItemStack[size];

        TreeMap<String, ArrayList<ItemStack>> modLists = new TreeMap<>();
        ArrayList<ItemStack> input = getItemList(inventory);
        ArrayList<ItemStack> allOfThem = new ArrayList<>();

        for (ItemStack stack : input)
        {
            String modname = getModName(stack);
            if (modLists.containsKey(modname))
            {
                modLists.get(modname).add(stack.copy());
            }
            else
            {
                ArrayList<ItemStack> toAdd = new ArrayList<>();
                toAdd.add(stack.copy());
                modLists.put(modname, toAdd);
            }
        }

        for (String key : modLists.keySet())
        {
            allOfThem.addAll(sortByBlocksAndItems(modLists.get(key), forward));
//            modLists.put(key,sortByBlocksAndItems(modLists.get(key), forward));
        }

        if (!forward)
        {
            Collections.reverse(allOfThem);
        }

        int index = 0;
        for (ItemStack stack : allOfThem)
        {
            toReturn[index] = stack;
            index++;
        }

        return toReturn;
    }

    public static ItemStack[] sortByBlocksAndItems(IInventory inventory,boolean forward)
    {
        int size = inventory.getSizeInventory();
        ItemStack[] toReturn = new ItemStack[size];

        ArrayList<ItemStack> sorted =  sortByBlocksAndItems(getItemList(inventory), forward);
        for (int i=0; i < sorted.size(); i++)
        {
            toReturn[i] = sorted.get(i);
        }

        return toReturn;
//
//        ArrayList<ItemStack> input = getItemList(inventory);
//
//
//        ArrayList<ItemStack> blocks = new ArrayList<ItemStack>();
//        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
//
//        for (int i=0; i < size; i ++)
//        {
//            ItemStack inSlot = inventory.getStackInSlot(i);
//            if (inSlot != null)
//            {
//                if (inSlot.getItem() instanceof ItemBlock)
//                {
//                    blocks.add(inSlot.copy());
//                }
//                else
//                {
//                    items.add(inSlot.copy());
//                }
//            }
//        }
//
//        sortItemStackArray(blocks);
//        sortItemStackArray(items);
//
//        blocks = combineLikeStacks(blocks);
//        items = combineLikeStacks(items);
//
//        int index = 0;
//        for (ItemStack block : blocks)
//        {
//            toReturn[index] = block;
//            index++;
//        }
//        for (ItemStack item : items)
//        {
//            toReturn[index] = item;
//            index++;
//        }
//
//        if (!forward)
//        {
//            for (int i=0; i < index/2; i++)
//            {
//                ItemStack temp = null;
//                if (toReturn[i] != null) {
//                    temp = toReturn[i].copy();
//                }
//                toReturn[i] = toReturn[index -1 - i];
//                toReturn[index - 1 - i] = temp;
//            }
//        }
//
//        return toReturn;
    }

    private static void sortItemStackArray(ArrayList<ItemStack> arrayList)
    {
        Collections.sort(arrayList, new Comparator<ItemStack>() {
            @Override
            public int compare(ItemStack first, ItemStack second) {
                int names = first.getDisplayName().compareTo(second.getDisplayName());
                if (names == 0)
                {
                    if (first.hasTagCompound())
                    {
                        if (second.hasTagCompound())
                        {
                            return first.getTagCompound().toString().compareTo(second.getTagCompound().toString());
                        }
                        else
                        {
                            return 1;
                        }
                    }
                    else
                    {
                        return -1;
                    }
                }
                else
                {
                    return names;
                }
            }
        });
    }

    private static ArrayList<ItemStack>  combineLikeStacks(ArrayList<ItemStack> arrayList)
    {
        ArrayList<ItemStack> toReturn = new ArrayList<ItemStack>();

        int index = 0;

        for (ItemStack thisStack : arrayList)
        {
            if (toReturn.size() == 0)
            {
                toReturn.add(thisStack);
                index++;
            }
            else
            {
                ItemStack leftovers = mergeItemStack(thisStack, toReturn.get(index-1));
                if (leftovers!=null && leftovers.getCount() != 0)
                {
                    toReturn.add(leftovers);
                    index++;
                }
            }
        }

        return toReturn;
    }

    private static boolean areItemStacksEqual(ItemStack stackA, ItemStack stackB)
    {
        return stackB.getItem() == stackA.getItem() && (!stackA.getHasSubtypes() || stackA.getMetadata() == stackB.getMetadata()) && ItemStack.areItemStackTagsEqual(stackA, stackB);
    }

    protected static ItemStack mergeItemStack(ItemStack source, ItemStack target)
    {
        if (target.isStackable() && areItemStacksEqual(source, target))
        {
            int j = source.getCount() + target.getCount();

            if (j <= target.getMaxStackSize())
            {
                source.setCount(0);
                target.setCount(j);
            }
            else if (target.getCount() < source.getMaxStackSize())
            {
                source.setCount(source.getMaxStackSize() - target.getCount());
                target.setCount(source.getMaxStackSize());
            }
        }

        if (source.getCount() == 0)
        {
            return null;
        }
        return source;
    }


    private static String getModName(ItemStack stack)
    {
        if (stack == null || stack.isEmpty())
        {
            return "";
        }
        return stack.getItem().getRegistryName().getResourceDomain();
    }

    private static ArrayList<ItemStack> getItemList(IInventory inventory)
    {
        ArrayList<ItemStack> toReturn = new ArrayList<ItemStack>();
        for (int i=0; i < inventory.getSizeInventory(); i++)
        {
            ItemStack inSlot = inventory.getStackInSlot(i);
            if (inSlot != null && !inSlot.isEmpty())
            {
                toReturn.add(inSlot.copy());
            }
        }

        return toReturn;
    }
}
