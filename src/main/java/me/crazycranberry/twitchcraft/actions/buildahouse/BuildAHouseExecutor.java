package me.crazycranberry.twitchcraft.actions.buildahouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.crazycranberry.twitchcraft.actions.Executor;
import me.crazycranberry.twitchcraft.config.Action;
import me.crazycranberry.twitchcraft.twitch.websocket.model.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.material.Directional;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static me.crazycranberry.twitchcraft.TwitchCraft.getPlugin;
import static me.crazycranberry.twitchcraft.TwitchCraft.logger;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.getTargetedPlayers;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.maybeSendPlayerMessage;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.randomFromList;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.triggerer;

public class BuildAHouseExecutor implements Executor {
    private Map<Player, HouseTracker> houses = new HashMap<>();

    @Override
    public void execute(Message twitchMessage, Action action) {
        if (!(action instanceof BuildAHouse)) {
            logger().warning("Somehow the following action was passed to " + this.getClass().getName() + ": " + action + "\nAborting execution.");
            return;
        }
        BuildAHouse bah = (BuildAHouse) action;
        for (Player p : getTargetedPlayers(bah)) {
            maybeSendPlayerMessage(p, twitchMessage, String.format("This looks like a good place for a %shouse%s, courtesy of %s%s%s", ChatColor.GOLD, ChatColor.RESET, ChatColor.GOLD, triggerer(twitchMessage, bah), ChatColor.RESET), action);
            int randomAccentIndex = (int) (Math.random() * houseAccent.size());
            houses.put(p, HouseTracker.builder()
                .startingBlock(p.getLocation().getBlock())
                .stylings(new HouseStylings(randomFromList(houseMaterials), houseAccent.get(randomAccentIndex), beds.get(randomAccentIndex)))
                .direction(Direction.getDirection(p.getLocation().getDirection()))
                .houseIndex(0)
                .taskId(Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
                    HouseTracker house = houses.get(p);
                    if (house.getHouseIndex() >= houseSteps.size()) {
                        Bukkit.getScheduler().cancelTask(house.getTaskId());
                        return;
                    }
                    placeBlock(house, twitchMessage.getPayload().getEvent().getUser_name());
                    house.setHouseIndex(house.getHouseIndex() + 1);
                }, 0 /*<-- the initial delay */, 1 /*<-- the interval */).getTaskId())
                .build());
        }
    }

    private void placeBlock(HouseTracker house, String triggererUsername) {
        HouseStep houseStep = houseSteps.get(house.getHouseIndex());
        Vector offset = house.getDirection().getTranslation().apply(houseStep.getOffset());
        Block blockToChange = house.getStartingBlock().getRelative(offset.getBlockX(), offset.getBlockY(), offset.getBlockZ());
        blockToChange.setType(houseStep.getBlock().apply(house.getStylings()));
        // Log Directional
        if (blockToChange instanceof Directional && houseStep.getBlockFace() != null) {
            ((Directional) blockToChange).setFacingDirection(houseStep.getBlockFace());
        }
        // Stairs and other Directionals
        if (blockToChange.getState().getBlockData() instanceof org.bukkit.block.data.Directional && houseStep.getBlockFace() != null) {
            BlockState state = blockToChange.getState();
            org.bukkit.block.data.Directional directional = (org.bukkit.block.data.Directional) state.getBlockData();
            directional.setFacing(house.getDirection().getDirectionalTranslation().apply(houseStep.getBlockFace()));
            if (blockToChange.getState().getBlockData() instanceof Stairs) {
                ((Stairs) directional).setShape(Stairs.Shape.STRAIGHT);
            }
            if (blockToChange.getState().getBlockData() instanceof Door) {
                handleDoor(house, houseStep, blockToChange, (Door) directional);
            }
            if (blockToChange.getState().getBlockData() instanceof Bed) {
                handleBed(house, houseStep, blockToChange, (Bed) directional);
            }
            state.setBlockData(directional);
            state.update();
        }
        // Signs
        if (blockToChange.getState() instanceof Sign) {
            handleSign(triggererUsername, blockToChange);
        }
    }

    private void handleSign(String triggererUsername, Block blockToChange) {
        Sign sign = (Sign) blockToChange.getState();
        sign.getSide(Side.FRONT).setLine(1, String.format("%s's", triggererUsername == null ? "Chat" : triggererUsername));
        sign.getSide(Side.FRONT).setLine(2, "House");
        sign.update();
    }

    private void handleBed(HouseTracker house, HouseStep houseStep, Block blockToChange, Bed directional) {
        Block bedHeadBlock = blockToChange.getRelative(house.getDirection().getDirectionalTranslation().apply(houseStep.getBlockFace()));
        bedHeadBlock.setType(houseStep.getBlock().apply(house.getStylings()), false);
        BlockState bedHeadState = bedHeadBlock.getState();
        Bed bedHead = (Bed) bedHeadState.getBlockData();
        bedHead.setPart(Bed.Part.HEAD);
        bedHead.setFacing(house.getDirection().getDirectionalTranslation().apply(houseStep.getBlockFace()));
        bedHeadState.setBlockData(bedHead);
        bedHeadState.update();
        directional.setPart(Bed.Part.FOOT);
    }

    private void handleDoor(HouseTracker house, HouseStep houseStep, Block blockToChange, Door directional) {
        Block top = blockToChange.getRelative(BlockFace.UP);
        top.setType(houseStep.getBlock().apply(house.getStylings()), false);
        BlockState topState = top.getState();
        Door doorTop = (Door) topState.getBlockData();
        doorTop.setHalf(Bisected.Half.TOP);
        doorTop.setFacing(house.getDirection().getDirectionalTranslation().apply(houseStep.getBlockFace()));
        topState.setBlockData(doorTop);
        topState.update();
        directional.setHalf(Bisected.Half.BOTTOM);
    }

    private static final List<HouseStep> houseSteps = List.of(
            // Flooring
            new HouseStep(0, -1, 0, HouseStep::getAccent),
            new HouseStep(-1, -1, 0, HouseStep::getAccent),
            new HouseStep(-1, -1, -1, HouseStep::getAccent),
            new HouseStep(0, -1, -1, HouseStep::getAccent),
            new HouseStep(1, -1, -1, HouseStep::getAccent),
            new HouseStep(1, -1, 0, HouseStep::getAccent),
            new HouseStep(1, -1, 1, HouseStep::getAccent),
            new HouseStep(1, -1, 1, HouseStep::getAccent),
            new HouseStep(0, -1, 1, HouseStep::getAccent),
            new HouseStep(-1, -1, 1, HouseStep::getAccent),
            new HouseStep(-2, -1, 1, HouseStep::getAccent),
            new HouseStep(-2, -1, 0, HouseStep::getAccent),
            new HouseStep(-2, -1, -1, HouseStep::getAccent),
            new HouseStep(-2, -1, -2, HouseStep::getAccent),
            new HouseStep(-1, -1, -2, HouseStep::getAccent),
            new HouseStep(0, -1, -2, HouseStep::getAccent),
            new HouseStep(1, -1, -2, HouseStep::getAccent),
            new HouseStep(2, -1, -2, HouseStep::getAccent),
            new HouseStep(2, -1, -1, HouseStep::getAccent),
            new HouseStep(2, -1, 0, HouseStep::getAccent),
            new HouseStep(2, -1, 1, HouseStep::getAccent),
            new HouseStep(2, -1, 2, HouseStep::getCobbleStone),
            new HouseStep(1, -1, 2, HouseStep::getCobbleStone),
            new HouseStep(0, -1, 2, HouseStep::getCobbleStone),
            new HouseStep(-1, -1, 2, HouseStep::getCobbleStone),
            new HouseStep(-2, -1, 2, HouseStep::getCobbleStone),
            new HouseStep(-3, -1, 2, HouseStep::getCobbleStone),
            new HouseStep(-3, -1, 1, HouseStep::getCobbleStone),
            new HouseStep(-3, -1, 0, HouseStep::getCobbleStone),
            new HouseStep(-3, -1, -1, HouseStep::getCobbleStone),
            new HouseStep(-3, -1, -2, HouseStep::getCobbleStone),
            new HouseStep(-3, -1, -3, HouseStep::getCobbleStone),
            new HouseStep(-2, -1, -3, HouseStep::getCobbleStone),
            new HouseStep(-1, -1, -3, HouseStep::getCobbleStone),
            new HouseStep(0, -1, -3, HouseStep::getCobbleStone),
            new HouseStep(0, -1, -3, HouseStep::getCobbleStone),
            new HouseStep(1, -1, -3, HouseStep::getCobbleStone),
            new HouseStep(2, -1, -3, HouseStep::getCobbleStone),
            new HouseStep(3, -1, -3, HouseStep::getCobbleStone),
            new HouseStep(3, -1, -2, HouseStep::getCobbleStone),
            new HouseStep(3, -1, -1, HouseStep::getCobbleStone),
            new HouseStep(3, -1, 0, HouseStep::getAccent),
            new HouseStep(3, -1, 1, HouseStep::getCobbleStone),
            new HouseStep(3, -1, 2, HouseStep::getCobbleStone),
            // Walls
            // Row 1
            new HouseStep(3, 0, 1, HouseStep::getPlank),
            new HouseStep(3, 0, 2, HouseStep::getWood, BlockFace.UP),
            new HouseStep(2, 0, 2, HouseStep::getPlank),
            new HouseStep(1, 0, 2, HouseStep::getPlank),
            new HouseStep(0, 0, 2, HouseStep::getPlank),
            new HouseStep(-1, 0, 2, HouseStep::getPlank),
            new HouseStep(-2, 0, 2, HouseStep::getPlank),
            new HouseStep(-3, 0, 2, HouseStep::getWood, BlockFace.UP),
            new HouseStep(-3, 0, 1, HouseStep::getPlank),
            new HouseStep(-3, 0, 0, HouseStep::getPlank),
            new HouseStep(-3, 0, -1, HouseStep::getPlank),
            new HouseStep(-3, 0, -2, HouseStep::getPlank),
            new HouseStep(-3, 0, -3, HouseStep::getWood, BlockFace.UP),
            new HouseStep(-2, 0, -3, HouseStep::getPlank),
            new HouseStep(-1, 0, -3, HouseStep::getPlank),
            new HouseStep(0, 0, -3, HouseStep::getPlank),
            new HouseStep(1, 0, -3, HouseStep::getPlank),
            new HouseStep(2, 0, -3, HouseStep::getPlank),
            new HouseStep(3, 0, -3, HouseStep::getWood, BlockFace.UP),
            new HouseStep(3, 0, -2, HouseStep::getPlank),
            new HouseStep(3, 0, -1, HouseStep::getPlank),
            // Row 2
            new HouseStep(1, 1, 2, HouseStep::getGlassPane),
            new HouseStep(0, 1, 2, HouseStep::getGlassPane),
            new HouseStep(-1, 1, 2, HouseStep::getGlassPane),
            new HouseStep(-1, 1, -3, HouseStep::getGlassPane),
            new HouseStep(0, 1, -3, HouseStep::getGlassPane),
            new HouseStep(1, 1, -3, HouseStep::getGlassPane),
            new HouseStep(3, 1, 1, HouseStep::getPlank),
            new HouseStep(3, 1, 2, HouseStep::getWood, BlockFace.UP),
            new HouseStep(2, 1, 2, HouseStep::getPlank),
            new HouseStep(-2, 1, 2, HouseStep::getPlank),
            new HouseStep(-3, 1, 2, HouseStep::getWood, BlockFace.UP),
            new HouseStep(-3, 1, 1, HouseStep::getPlank),
            new HouseStep(-3, 1, 0, HouseStep::getPlank),
            new HouseStep(-3, 1, -1, HouseStep::getPlank),
            new HouseStep(-3, 1, -2, HouseStep::getPlank),
            new HouseStep(-3, 1, -3, HouseStep::getWood, BlockFace.UP),
            new HouseStep(-2, 1, -3, HouseStep::getPlank),
            new HouseStep(2, 1, -3, HouseStep::getPlank),
            new HouseStep(3, 1, -3, HouseStep::getWood, BlockFace.UP),
            new HouseStep(3, 1, -2, HouseStep::getPlank),
            new HouseStep(3, 1, -1, HouseStep::getPlank),
            // Row 3
            new HouseStep(3, 2, 0, HouseStep::getPlank),
            new HouseStep(3, 2, 1, HouseStep::getPlank),
            new HouseStep(3, 2, 2, HouseStep::getWood, BlockFace.UP),
            new HouseStep(2, 2, 2, HouseStep::getPlank),
            new HouseStep(1, 2, 2, HouseStep::getPlank),
            new HouseStep(0, 2, 2, HouseStep::getPlank),
            new HouseStep(-1, 2, 2, HouseStep::getPlank),
            new HouseStep(-2, 2, 2, HouseStep::getPlank),
            new HouseStep(-3, 2, 2, HouseStep::getWood, BlockFace.UP),
            new HouseStep(-3, 2, 1, HouseStep::getPlank),
            new HouseStep(-3, 2, 0, HouseStep::getPlank),
            new HouseStep(-3, 2, -1, HouseStep::getPlank),
            new HouseStep(-3, 2, -2, HouseStep::getPlank),
            new HouseStep(-3, 2, -3, HouseStep::getWood, BlockFace.UP),
            new HouseStep(-2, 2, -3, HouseStep::getPlank),
            new HouseStep(-1, 2, -3, HouseStep::getPlank),
            new HouseStep(0, 2, -3, HouseStep::getPlank),
            new HouseStep(1, 2, -3, HouseStep::getPlank),
            new HouseStep(2, 2, -3, HouseStep::getPlank),
            new HouseStep(3, 2, -3, HouseStep::getWood, BlockFace.UP),
            new HouseStep(3, 2, -2, HouseStep::getPlank),
            new HouseStep(3, 2, -1, HouseStep::getPlank),
            // Row 4
            new HouseStep(3, 3, 0, HouseStep::getAccent),
            new HouseStep(3, 3, 1, HouseStep::getAccent),
            new HouseStep(3, 3, 2, HouseStep::getWood, BlockFace.UP),
            new HouseStep(2, 3, 2, HouseStep::getPlank),
            new HouseStep(1, 3, 2, HouseStep::getPlank),
            new HouseStep(0, 3, 2, HouseStep::getPlank),
            new HouseStep(-1, 3, 2, HouseStep::getPlank),
            new HouseStep(-2, 3, 2, HouseStep::getPlank),
            new HouseStep(-3, 3, 2, HouseStep::getWood, BlockFace.UP),
            new HouseStep(-3, 3, 1, HouseStep::getWood, BlockFace.UP),
            new HouseStep(-3, 3, 0, HouseStep::getPlank),
            new HouseStep(-3, 3, -1, HouseStep::getPlank),
            new HouseStep(-3, 3, -2, HouseStep::getWood, BlockFace.UP),
            new HouseStep(-3, 3, -3, HouseStep::getWood, BlockFace.UP),
            new HouseStep(-2, 3, -3, HouseStep::getPlank),
            new HouseStep(-1, 3, -3, HouseStep::getPlank),
            new HouseStep(0, 3, -3, HouseStep::getPlank),
            new HouseStep(1, 3, -3, HouseStep::getPlank),
            new HouseStep(2, 3, -3, HouseStep::getPlank),
            new HouseStep(3, 3, -3, HouseStep::getWood, BlockFace.UP),
            new HouseStep(3, 3, -2, HouseStep::getAccent),
            new HouseStep(3, 3, -1, HouseStep::getAccent),
            // Row 5
            new HouseStep(3, 4, 0, HouseStep::getPlank),
            new HouseStep(3, 4, 1, HouseStep::getWood, BlockFace.UP),
            new HouseStep(-3, 4, 1, HouseStep::getWood, BlockFace.UP),
            new HouseStep(-3, 4, 0, HouseStep::getWood, BlockFace.UP),
            new HouseStep(-3, 4, -1, HouseStep::getWood, BlockFace.UP),
            new HouseStep(-3, 4, -2, HouseStep::getWood, BlockFace.UP),
            new HouseStep(3, 4, -2, HouseStep::getWood, BlockFace.UP),
            new HouseStep(3, 4, -1, HouseStep::getPlank),
            // Row 6
            new HouseStep(-3, 5, 0, HouseStep::getWood, BlockFace.UP),
            new HouseStep(-3, 5, -1, HouseStep::getWood, BlockFace.UP),
            new HouseStep(3, 5, -1, HouseStep::getWood, BlockFace.UP),
            new HouseStep(3, 5, 0, HouseStep::getWood, BlockFace.UP),
            // Roof
            // Row 1
            new HouseStep(-4, 3, -4, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(-3, 3, -4, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(-2, 3, -4, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(-1, 3, -4, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(0, 3, -4, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(1, 3, -4, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(2, 3, -4, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(3, 3, -4, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(4, 3, -4, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(-4, 3, 3, HouseStep::getStairs, BlockFace.NORTH),
            new HouseStep(-3, 3, 3, HouseStep::getStairs, BlockFace.NORTH),
            new HouseStep(-2, 3, 3, HouseStep::getStairs, BlockFace.NORTH),
            new HouseStep(-1, 3, 3, HouseStep::getStairs, BlockFace.NORTH),
            new HouseStep(0, 3, 3, HouseStep::getStairs, BlockFace.NORTH),
            new HouseStep(1, 3, 3, HouseStep::getStairs, BlockFace.NORTH),
            new HouseStep(2, 3, 3, HouseStep::getStairs, BlockFace.NORTH),
            new HouseStep(3, 3, 3, HouseStep::getStairs, BlockFace.NORTH),
            new HouseStep(4, 3, 3, HouseStep::getStairs, BlockFace.NORTH),
            // Row 2
            new HouseStep(-4, 4, -3, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(-3, 4, -3, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(-2, 4, -3, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(-1, 4, -3, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(0, 4, -3, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(1, 4, -3, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(2, 4, -3, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(3, 4, -3, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(4, 4, -3, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(-4, 4, 2, HouseStep::getStairs, BlockFace.NORTH),
            new HouseStep(-3, 4, 2, HouseStep::getStairs, BlockFace.NORTH),
            new HouseStep(-2, 4, 2, HouseStep::getStairs, BlockFace.NORTH),
            new HouseStep(-1, 4, 2, HouseStep::getStairs, BlockFace.NORTH),
            new HouseStep(0, 4, 2, HouseStep::getStairs, BlockFace.NORTH),
            new HouseStep(1, 4, 2, HouseStep::getStairs, BlockFace.NORTH),
            new HouseStep(2, 4, 2, HouseStep::getStairs, BlockFace.NORTH),
            new HouseStep(3, 4, 2, HouseStep::getStairs, BlockFace.NORTH),
            new HouseStep(4, 4, 2, HouseStep::getStairs, BlockFace.NORTH),
            // Row 3
            new HouseStep(-4, 5, -2, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(-3, 5, -2, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(-2, 5, -2, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(-1, 5, -2, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(0, 5, -2, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(1, 5, -2, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(2, 5, -2, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(3, 5, -2, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(4, 5, -2, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(-4, 5, 1, HouseStep::getStairs, BlockFace.NORTH),
            new HouseStep(-3, 5, 1, HouseStep::getStairs, BlockFace.NORTH),
            new HouseStep(-2, 5, 1, HouseStep::getStairs, BlockFace.NORTH),
            new HouseStep(-1, 5, 1, HouseStep::getStairs, BlockFace.NORTH),
            new HouseStep(0, 5, 1, HouseStep::getStairs, BlockFace.NORTH),
            new HouseStep(1, 5, 1, HouseStep::getStairs, BlockFace.NORTH),
            new HouseStep(2, 5, 1, HouseStep::getStairs, BlockFace.NORTH),
            new HouseStep(3, 5, 1, HouseStep::getStairs, BlockFace.NORTH),
            new HouseStep(4, 5, 1, HouseStep::getStairs, BlockFace.NORTH),
            // Row 4
            new HouseStep(-4, 6, -1, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(-3, 6, -1, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(-2, 6, -1, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(-1, 6, -1, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(0, 6, -1, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(1, 6, -1, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(2, 6, -1, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(3, 6, -1, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(4, 6, -1, HouseStep::getStairs, BlockFace.SOUTH),
            new HouseStep(-4, 6, 0, HouseStep::getStairs, BlockFace.NORTH),
            new HouseStep(-3, 6, 0, HouseStep::getStairs, BlockFace.NORTH),
            new HouseStep(-2, 6, 0, HouseStep::getStairs, BlockFace.NORTH),
            new HouseStep(-1, 6, 0, HouseStep::getStairs, BlockFace.NORTH),
            new HouseStep(0, 6, 0, HouseStep::getStairs, BlockFace.NORTH),
            new HouseStep(1, 6, 0, HouseStep::getStairs, BlockFace.NORTH),
            new HouseStep(2, 6, 0, HouseStep::getStairs, BlockFace.NORTH),
            new HouseStep(3, 6, 0, HouseStep::getStairs, BlockFace.NORTH),
            new HouseStep(4, 6, 0, HouseStep::getStairs, BlockFace.NORTH),
            // Amenities
            new HouseStep(-2, 0, -1, HouseStep::getChest, BlockFace.EAST),
            new HouseStep(-2, 0, -2, HouseStep::getCraftingTable),
            new HouseStep(-1, 0, -2, HouseStep::getFurnace, BlockFace.SOUTH),
            new HouseStep(-1, 0, 1, HouseStep::getBed, BlockFace.WEST),
            new HouseStep(4, 1, -1, HouseStep::getSign, BlockFace.EAST),
            new HouseStep(3, 0, 0, HouseStep::getDoor, BlockFace.EAST)
    );

    private static final List<Material> houseAccent = List.of(
            Material.WHITE_WOOL,
            Material.ORANGE_WOOL,
            Material.MAGENTA_WOOL,
            Material.LIGHT_BLUE_WOOL,
            Material.YELLOW_WOOL,
            Material.LIME_WOOL,
            Material.PINK_WOOL,
            Material.GRAY_WOOL,
            Material.LIGHT_GRAY_WOOL,
            Material.CYAN_WOOL,
            Material.PURPLE_WOOL,
            Material.BLUE_WOOL,
            Material.BROWN_WOOL,
            Material.GREEN_WOOL,
            Material.RED_WOOL,
            Material.BLACK_WOOL
    );

    private static final List<Material> beds = List.of(
            Material.WHITE_BED,
            Material.ORANGE_BED,
            Material.MAGENTA_BED,
            Material.LIGHT_BLUE_BED,
            Material.YELLOW_BED,
            Material.LIME_BED,
            Material.PINK_BED,
            Material.GRAY_BED,
            Material.LIGHT_GRAY_BED,
            Material.CYAN_BED,
            Material.PURPLE_BED,
            Material.BLUE_BED,
            Material.BROWN_BED,
            Material.GREEN_BED,
            Material.RED_BED,
            Material.BLACK_BED
    );

    private static final List<HouseMaterials> houseMaterials = List.of(
            new HouseMaterials(Material.ACACIA_LOG, Material.ACACIA_PLANKS, Material.ACACIA_STAIRS, Material.ACACIA_DOOR, Material.ACACIA_WALL_SIGN),
            new HouseMaterials(Material.BIRCH_LOG, Material.BIRCH_PLANKS, Material.BIRCH_STAIRS, Material.BIRCH_DOOR, Material.BIRCH_WALL_SIGN),
            new HouseMaterials(Material.CHERRY_LOG, Material.CHERRY_PLANKS, Material.CHERRY_STAIRS, Material.CHERRY_DOOR, Material.CHERRY_WALL_SIGN),
            new HouseMaterials(Material.DARK_OAK_LOG, Material.DARK_OAK_PLANKS, Material.DARK_OAK_STAIRS, Material.DARK_OAK_DOOR, Material.DARK_OAK_WALL_SIGN),
            new HouseMaterials(Material.JUNGLE_LOG, Material.JUNGLE_PLANKS, Material.JUNGLE_STAIRS, Material.JUNGLE_DOOR, Material.JUNGLE_WALL_SIGN),
            new HouseMaterials(Material.MANGROVE_LOG, Material.MANGROVE_PLANKS, Material.MANGROVE_STAIRS, Material.MANGROVE_DOOR, Material.MANGROVE_WALL_SIGN),
            new HouseMaterials(Material.OAK_LOG, Material.OAK_PLANKS, Material.OAK_STAIRS, Material.OAK_DOOR, Material.OAK_WALL_SIGN),
            new HouseMaterials(Material.SPRUCE_LOG, Material.SPRUCE_PLANKS, Material.SPRUCE_STAIRS, Material.SPRUCE_DOOR, Material.SPRUCE_WALL_SIGN)
    );

    @Getter
    @AllArgsConstructor
    private static class HouseStylings {
        private HouseMaterials houseMaterials;
        private Material accent;
        private Material bed;
    }

    @Getter
    @Builder
    private static class HouseTracker {
        @Setter
        private Integer houseIndex;
        private Block startingBlock;
        private HouseStylings stylings;
        private Direction direction;
        private Integer taskId;
    }

    @Getter
    private static class HouseStep {
        private Vector offset;
        private Function<HouseStylings, Material> block;
        private BlockFace blockFace;

        public HouseStep(int x, int y, int z, Function<HouseStylings, Material> block) {
            this.offset = new Vector(x, y, z);
            this.block = block;
        }

        public HouseStep(int x, int y, int z, Function<HouseStylings, Material> block, BlockFace blockFace) {
            this.offset = new Vector(x, y, z);
            this.block = block;
            this.blockFace = blockFace;
        }

        public static Material getWood(HouseStylings hs) {
            return hs.getHouseMaterials().getWood();
        }

        public static Material getPlank(HouseStylings hs) {
            return hs.getHouseMaterials().getPlank();
        }

        public static Material getStairs(HouseStylings hs) {
            return hs.getHouseMaterials().getStair();
        }

        public static Material getDoor(HouseStylings hs) {
            return hs.getHouseMaterials().getDoor();
        }

        public static Material getSign(HouseStylings hs) {
            return hs.getHouseMaterials().getSign();
        }

        public static Material getAccent(HouseStylings hs) {
            return hs.getAccent();
        }

        public static Material getBed(HouseStylings hs) {
            return hs.getBed();
        }

        public static Material getCobbleStone(HouseStylings hs) {
            return Material.COBBLESTONE;
        }

        public static Material getChest(HouseStylings hs) {
            return Material.CHEST;
        }

        public static Material getCraftingTable(HouseStylings hs) {
            return Material.CRAFTING_TABLE;
        }

        public static Material getFurnace(HouseStylings hs) {
            return Material.FURNACE;
        }

        public static Material getGlassPane(HouseStylings hs) {
            return Material.GLASS_PANE;
        }
    }

    @AllArgsConstructor
    @Getter
    private static class HouseMaterials {
        private Material wood;
        private Material plank;
        private Material stair;
        private Material door;
        private Material sign;
    }

    @AllArgsConstructor
    @Getter
    private enum Direction {
        POS_X(Direction::positiveX, Direction::positiveXDirectional),
        POS_Z(Direction::positiveZ, Direction::positiveZDirectional),
        NEG_X(Direction::negativeX, Direction::negativeXDirectional),
        NEG_Z(Direction::negativeZ, Direction::negativeZDirectional);

        private Function<Vector, Vector> translation;

        private Function<BlockFace, BlockFace> directionalTranslation;

        private static Vector positiveX(Vector input) {
            return input;
        }

        private static Vector positiveZ(Vector input) {
            return new Vector(-input.getZ(), input.getY(), input.getX());
        }

        private static Vector negativeX(Vector input) {
            return new Vector(-input.getX(), input.getY(), -input.getZ());
        }

        private static Vector negativeZ(Vector input) {
            return new Vector(input.getZ(), input.getY(), -input.getX());
        }

        private static BlockFace positiveXDirectional(BlockFace input) {
            return input;
        }

        private static BlockFace positiveZDirectional(BlockFace input) {
            switch (input) {
                case SOUTH:
                    return BlockFace.WEST;
                case WEST:
                    return BlockFace.NORTH;
                case NORTH:
                    return BlockFace.EAST;
                case EAST:
                    return BlockFace.SOUTH;
                default:
                    return input;
            }
        }

        private static BlockFace negativeZDirectional(BlockFace input) {
            switch (input) {
                case SOUTH:
                    return BlockFace.EAST;
                case WEST:
                    return BlockFace.SOUTH;
                case NORTH:
                    return BlockFace.WEST;
                case EAST:
                    return BlockFace.NORTH;
                default:
                    return input;
            }
        }

        private static BlockFace negativeXDirectional(BlockFace input) {
            switch (input) {
                case SOUTH:
                    return BlockFace.NORTH;
                case WEST:
                    return BlockFace.EAST;
                case NORTH:
                    return BlockFace.SOUTH;
                case EAST:
                    return BlockFace.WEST;
                default:
                    return input;
            }
        }

        private static Direction getDirection(Vector direction) {
            double x = direction.getX();
            double z = direction.getZ();
            if (Math.abs(x) > Math.abs(z)) {
                return x < 0 ? NEG_X : POS_X;
            } else {
                return z < 0 ? NEG_Z : POS_Z;
            }
        }
    }
}
