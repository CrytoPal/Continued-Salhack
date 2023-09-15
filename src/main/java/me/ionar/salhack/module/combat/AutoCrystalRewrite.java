package me.ionar.salhack.module.combat;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.EventEra;
import me.ionar.salhack.events.entity.EntityRemovedEvent;
import me.ionar.salhack.events.network.PacketEvent;
import me.ionar.salhack.events.player.PlayerMotionUpdate;
import me.ionar.salhack.events.render.RenderEvent;
import me.ionar.salhack.events.world.TickEvent;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.CrystalUtils;
import me.ionar.salhack.util.Timer;
import me.ionar.salhack.util.entity.EntityUtil;
import me.ionar.salhack.util.entity.PlayerUtil;
import me.ionar.salhack.util.render.RenderUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AutoCrystalRewrite extends Module {
    public static final Value<BreakModes> breakMode = new Value<>("BreakMode", new String[]{"BM"}, "Mode of breaking to use", BreakModes.Always);
    public static final Value<PlaceModes> placeMode = new Value<>("PlaceMode", new String[]{"BM"}, "Mode of placing to use", PlaceModes.Most);
    public static final Value<Float> placeRadius = new Value<>("PlaceRadius", new String[]{""}, "Radius for placing", 4.0f, 0.0f, 6.0f, 0.5f);
    public static final Value<Float> breakRadius = new Value<>("BreakRadius", new String[]{""}, "Radius for BreakRadius", 4.0f, 0.0f, 6.0f, 0.5f);
    public static final Value<Float> wallsRange = new Value<>("WallsRange", new String[]{""}, "Max distance through walls", 3.5f, 0.0f, 6.0f, 0.5f);
    public static final Value<Boolean> multiPlace = new Value<>("MultiPlace", new String[]{"MultiPlaces"}, "Tries to multiplace", false);
    public static final Value<Integer> ticks = new Value<>("Ticks", new String[]{"IgnoreTicks"}, "The number of ticks to ignore on client update", 0, 0, 20, 1);
    public static final Value<Float> minDMG = new Value<>("MinDMG", new String[]{""}, "Minimum damage to do to your opponent", 4.0f, 0.0f, 20.0f, 1f);
    public static final Value<Float> maxSelfDMG = new Value<>("MaxSelfDMG", new String[]{""}, "Max self dmg for breaking crystals that will deal tons of dmg", 4.0f, 0.0f, 20.0f, 1.0f);
    public static final Value<Float> facePlace = new Value<>("FacePlace", new String[]{""}, "Required target health for faceplacing", 8.0f, 0.0f, 20.0f, 0.5f);
    public static final Value<Boolean> autoSwitch = new Value<>("AutoSwitch", new String[]{""}, "Automatically switches to crystals in your hotbar", true);
    public static final Value<Boolean> pauseIfHittingBlock = new Value<>("PauseIfHittingBlock", new String[]{""}, "Pauses when your hitting a block with a pickaxe", false);
    public static final Value<Boolean> pauseWhileEating = new Value<>("PauseWhileEating", new String[]{"PauseWhileEating"}, "Pause while eating", false);
    public static final Value<Boolean> noSuicide = new Value<>("NoSuicide", new String[]{"NS"}, "Doesn't commit suicide/pop if you are going to take fatal damage from self placed crystal", true);
    public static final Value<Boolean> antiWeakness = new Value<>("AntiWeakness", new String[]{"AW"}, "Switches to a sword to try and break crystals", true);
    public static final Value<Boolean> render = new Value<>("Render", new String[]{"Render"}, "Allows for rendering of block placements", true);
    public static final Value<Integer> red = new Value<>("Red", new String[]{"Red"}, "Red for rendering", 0x33, 0, 255, 5);
    public static final Value<Integer> green = new Value<>("Green", new String[]{"Green"}, "Green for rendering", 0xFF, 0, 255, 5);
    public static final Value<Integer> blue = new Value<>("Blue", new String[]{"Blue"}, "Blue for rendering", 0xF3, 0, 255, 5);
    public static final Value<Integer> alpha = new Value<>("Alpha", new String[]{"Alpha"}, "Alpha for rendering", 0x99, 0, 255, 5);
    public static Timer removeVisualTimer = new Timer();
    private final Timer rotationResetTimer = new Timer();
    private final ConcurrentLinkedQueue<BlockPos> placedCrystals = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<BlockPos, Float> placedCrystalsDamage = new ConcurrentHashMap<>();
    private double[] rotations = null;
    private final ConcurrentHashMap<EndCrystalEntity, Integer> attackedEnderCrystals = new ConcurrentHashMap<>();
    private String lastTarget = null;
    private int remainingTicks;
    private BlockPos lastPlaceLocation = BlockPos.ORIGIN;
    private Surround surround = null;
    private AutoCity autoCity = null;
    public enum BreakModes {
        Always,
        Smart,
        OnlyOwn
    }

    public enum PlaceModes {
        Most,
        Lethal,
    }

    public AutoCrystalRewrite() {
        super("AutoCrystalRewrite", new String[]{"AutoCrystal2"}, "Automatically places and destroys crystals", 0, -1, ModuleType.COMBAT);
    }

    @Override
    public void init() {
        surround = (Surround) SalHack.getModuleManager().getMod(Surround.class);
        autoCity = (AutoCity) SalHack.getModuleManager().getMod(AutoCity.class);
    }


    @Override
    public void onEnable() {
        super.onEnable();
        placedCrystals.clear();
        placedCrystalsDamage.clear();
        remainingTicks = 0;
        lastPlaceLocation = BlockPos.ORIGIN;
    }

    @Override
    public String getMetaData() {
        return lastTarget;
    }

    @EventHandler
    public void onEntityRemove(EntityRemovedEvent event) {
        if (event.GetEntity() instanceof EndCrystalEntity) attackedEnderCrystals.remove((EndCrystalEntity) event.GetEntity());
    }

    private boolean validateCrystal(EndCrystalEntity endCrystalEntity) {
        if (mc.player == null || mc.world == null) return false;
        if (endCrystalEntity == null || !endCrystalEntity.isAlive()) return false;
        if (attackedEnderCrystals.containsKey(endCrystalEntity) && attackedEnderCrystals.get(endCrystalEntity) > 5) return false;
        if (endCrystalEntity.distanceTo(mc.player) > (!mc.player.canSee(endCrystalEntity) ? wallsRange.getValue() : breakRadius.getValue())) return false;
        switch (breakMode.getValue()) {
            case OnlyOwn -> {
                return endCrystalEntity.distanceTo(endCrystalEntity) <= 3;
            }
            case Smart -> {
                float selfDamage = CrystalUtils.calculateDamage(mc.world, endCrystalEntity.getX(), endCrystalEntity.getY(), endCrystalEntity.getZ(), mc.player, 0);
                if (selfDamage > maxSelfDMG.getValue()) return false;
                if (noSuicide.getValue() && selfDamage >= mc.player.getHealth() + mc.player.getAbsorptionAmount()) return false;
                for (PlayerEntity player : mc.world.getPlayers()) {
                    if (player == mc.player || SalHack.getFriendManager().isFriend(player) || mc.player.isDead() || (mc.player.getHealth() + mc.player.getAbsorptionAmount()) <= 0.0f) continue;
                    float minDamage = minDMG.getValue();
                    if (player.getHealth() + player.getAbsorptionAmount() <= facePlace.getValue()) minDamage = 1f;
                    float calculatedDamage = CrystalUtils.calculateDamage(mc.world, endCrystalEntity.getX(), endCrystalEntity.getY(), endCrystalEntity.getZ(), player, 0);
                    if (calculatedDamage > minDamage) return true;
                }
                return false;
            }
            default -> {}
        }
        return true;
    }

    /*
     * Returns nearest crystal to an entity, if the crystal is not null or dead
     * @entity - entity to get the smallest distance from
     */
    public EndCrystalEntity getNearestCrystalTo(Entity entity) {
        if (mc.world == null) return null;
        List<Entity> entities = new ArrayList<>();
        mc.world.getEntities().forEach(entities::add);
        return entities.stream().filter(e -> e instanceof EndCrystalEntity && validateCrystal((EndCrystalEntity) e)).map(e -> (EndCrystalEntity) e).min(Comparator.comparing(entity::distanceTo)).orElse(null);
    }

    public void AddAttackedCrystal(EndCrystalEntity crystal) {
        if (attackedEnderCrystals.containsKey(crystal)) {
            int value = attackedEnderCrystals.get(crystal);
            attackedEnderCrystals.put(crystal, value + 1);
        } else attackedEnderCrystals.put(crystal, 1);
    }

    private boolean VerifyCrystalBlocks(BlockPos pos) {
        if (mc.player == null || mc.world == null) return false;
        if (mc.player.squaredDistanceTo(pos.toCenterPos()) > placeRadius.getValue() * placeRadius.getValue()) return false;
        if (wallsRange.getValue() > 0 && !PlayerUtil.canSeeBlock(pos) && pos.getSquaredDistance((int) mc.player.getX(), (int) mc.player.getY(), (int) mc.player.getZ()) > wallsRange.getValue() * wallsRange.getValue()) return false;
        float selfDamage = CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, mc.player, 0);
        if (selfDamage > maxSelfDMG.getValue()) return false;
        return !noSuicide.getValue() || !(selfDamage >= mc.player.getHealth() + mc.player.getAbsorptionAmount());
    }

    @EventHandler
    private void OnTick(TickEvent event) {
        if (mc.world == null || mc.player == null || mc.interactionManager == null) return;
        if (removeVisualTimer.passed(1000)) {
            removeVisualTimer.reset();
            if (!placedCrystals.isEmpty()) {
                BlockPos removed = placedCrystals.remove();
                if (removed != null) placedCrystalsDamage.remove(removed);
            }
            attackedEnderCrystals.clear();
        }
        if (NeedPause()) {
            remainingTicks = 0;
            return;
        }
        if (placeMode.getValue() == PlaceModes.Lethal && lastPlaceLocation != BlockPos.ORIGIN) {
            float damage = 0f;
            for (PlayerEntity player : mc.world.getPlayers()) {
                if (player == mc.player || SalHack.getFriendManager().isFriend(player) || mc.player.isDead() || (mc.player.getHealth() + mc.player.getAbsorptionAmount()) <= 0.0f) continue;
                float minDamage = minDMG.getValue();
                if (player.getHealth() + player.getAbsorptionAmount() <= facePlace.getValue()) minDamage = 1f;
                float calculatedDamage = CrystalUtils.calculateDamage(mc.world, lastPlaceLocation.getX() + 0.5, lastPlaceLocation.getY() + 1.0, lastPlaceLocation.getZ() + 0.5, player, 0);
                if (calculatedDamage >= minDamage && calculatedDamage > damage) damage = calculatedDamage;
            }
            if (damage == 0f) lastPlaceLocation = BlockPos.ORIGIN;
        }
        if (remainingTicks > 0) --remainingTicks;
        boolean skipUpdateBlocks = lastPlaceLocation != BlockPos.ORIGIN && placeMode.getValue() == PlaceModes.Lethal;
        ArrayList<BlockPos> placeLocations = new ArrayList<>();
        PlayerEntity playerTarget = null;
        if (!skipUpdateBlocks && remainingTicks <= 0) {
            remainingTicks = ticks.getValue();
            final List<BlockPos> cachedCrystalBlocks = CrystalUtils.findCrystalBlocks(mc.player, AutoCrystalRewrite.placeRadius.getValue()).stream().filter(this::VerifyCrystalBlocks).toList();
            if (!cachedCrystalBlocks.isEmpty()) {
                float damage = 0f;
                String target = null;
                for (PlayerEntity player : mc.world.getPlayers()) {
                    if (player == mc.player || SalHack.getFriendManager().isFriend(player) || mc.player.isDead() || (mc.player.getHealth() + mc.player.getAbsorptionAmount()) <= 0.0f) continue;
                    float minDamage = minDMG.getValue();
                    if (player.getHealth() + player.getAbsorptionAmount() <= facePlace.getValue()) minDamage = 1f;
                    for (BlockPos pos : cachedCrystalBlocks) {
                        float calculatedDamage = CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, player, 0);
                        if (calculatedDamage >= minDamage && calculatedDamage > damage) {
                            damage = calculatedDamage;
                            if (!placeLocations.contains(pos)) placeLocations.add(pos);
                            target = player.getName().getString();
                            playerTarget = player;
                        }
                    }
                }
                if (playerTarget != null) {
                    if (playerTarget.isDead() || playerTarget.getHealth() <= 0.0f) return;
                    if (!placeLocations.isEmpty()) {
                        float minDamage = minDMG.getValue();
                        if (playerTarget.getHealth() + playerTarget.getAbsorptionAmount() <= facePlace.getValue()) minDamage = 1f;
                        final float finalMinDamage = minDamage;
                        final PlayerEntity finalTarget = playerTarget;
                        placeLocations.removeIf(pos -> CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, finalTarget, 0) < finalMinDamage);
                        Collections.reverse(placeLocations);
                        lastTarget = target;
                    }
                }
            }
        }
        EndCrystalEntity crystal = getNearestCrystalTo(mc.player);
        boolean isValidCrystal = crystal != null && mc.player.distanceTo(crystal) < breakRadius.getValue();
        if (!isValidCrystal && placeLocations.isEmpty() && !skipUpdateBlocks) {
            remainingTicks = 0;
            return;
        }
        if (isValidCrystal && (skipUpdateBlocks || remainingTicks == ticks.getValue())) {
            if (antiWeakness.getValue() && mc.player.hasStatusEffect(StatusEffects.WEAKNESS)) {
                if (mc.player.getMainHandStack() == ItemStack.EMPTY || (!(mc.player.getMainHandStack().getItem() instanceof SwordItem) && !(mc.player.getMainHandStack().getItem() instanceof ToolItem))) {
                    for (int i = 0; i < 9; ++i) {
                        ItemStack stack = mc.player.getInventory().getStack(i);
                        if (stack.isEmpty()) continue;
                        if (stack.getItem() instanceof ToolItem) {
                            mc.player.getInventory().selectedSlot = i;
                            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(i));
                            break;
                        }
                    }
                }
            }
            rotations = EntityUtil.calculateLookAt(crystal.getX() + 0.5, crystal.getY() - 0.5, crystal.getZ() + 0.5, mc.player);
            rotationResetTimer.reset();
            mc.interactionManager.attackEntity(mc.player, crystal);
            mc.player.swingHand(Hand.MAIN_HAND);
            AddAttackedCrystal(crystal);
            if (!multiPlace.getValue()) return;
        }
        if (!placeLocations.isEmpty() || skipUpdateBlocks) {
            if (autoSwitch.getValue()) {
                if (mc.player.getOffHandStack().getItem() != Items.END_CRYSTAL) {
                    if (mc.player.getMainHandStack().getItem() != Items.END_CRYSTAL) {
                        for (int i = 0; i < 9; ++i) {
                            ItemStack stack = mc.player.getInventory().getStack(i);
                            if (!stack.isEmpty() && stack.getItem() == Items.END_CRYSTAL) {
                                mc.player.getInventory().selectedSlot = i;
                                mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(i));
                                break;
                            }
                        }
                    }
                }
            }
            if (mc.player.getMainHandStack().getItem() != Items.END_CRYSTAL && mc.player.getOffHandStack().getItem() != Items.END_CRYSTAL) return;
            BlockPos selectedPos = null;
            if (!skipUpdateBlocks) {
                for (BlockPos pos : placeLocations) {
                    if (CrystalUtils.canPlaceCrystal(pos)) {
                        selectedPos = pos;
                        break;
                    }
                }
            } else selectedPos = lastPlaceLocation;
            if (selectedPos == null) {
                remainingTicks = 0;
                return;
            }
            rotations = EntityUtil.calculateLookAt(selectedPos.getX() + 0.5, selectedPos.getY() - 0.5, selectedPos.getZ() + 0.5, mc.player);
            rotationResetTimer.reset();
            BlockHitResult result = PlayerUtil.rayCastBlock(new RaycastContext(PlayerUtil.getEyesPos(mc.player), selectedPos.toCenterPos().offset(Direction.DOWN, 0.5), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player), selectedPos);
            mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(mc.player.getOffHandStack().getItem() == Items.END_CRYSTAL ? Hand.OFF_HAND : Hand.MAIN_HAND, result, 0));
            mc.player.swingHand(mc.player.getOffHandStack().getItem() == Items.END_CRYSTAL ? Hand.OFF_HAND : Hand.MAIN_HAND);
            placedCrystals.remove(selectedPos);
            placedCrystals.add(selectedPos);
            if (playerTarget != null) {
                float calculatedDamage = CrystalUtils.calculateDamage(mc.world, selectedPos.getX() + 0.5, selectedPos.getY() + 1.0, selectedPos.getZ() + 0.5, playerTarget, 0);
                placedCrystalsDamage.put(selectedPos, calculatedDamage);
            }
            if (lastPlaceLocation != BlockPos.ORIGIN && lastPlaceLocation == selectedPos && placeMode.getValue() == PlaceModes.Lethal) remainingTicks = 0;
            else lastPlaceLocation = selectedPos;
        }
    }

    @EventHandler
    public void onPlayerMotionUpdate(PlayerMotionUpdate event) {
        if (event.getEra() != EventEra.PRE) return;
        if (event.isCancelled()) {
            rotations = null;
            return;
        }
        if (NeedPause()) {
            rotations = null;
            return;
        }
        if (rotationResetTimer.passed(1000)) {
            rotations = null;
        }
        if (rotations != null) {
            event.cancel();
            PlayerUtil.packetFacePitchAndYaw((float) rotations[0], (float) rotations[1]);
        }
    }

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof PlaySoundS2CPacket packet) {
            if (mc.world == null) return;
            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound().value() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                List<Entity> List = new ArrayList<>();
                mc.world.getEntities().forEach(List::add);
                List.forEach(entity -> {
                    if (entity instanceof EndCrystalEntity && entity.squaredDistanceTo(packet.getX(), packet.getY(), packet.getZ()) <= 36.0) {
                        entity.kill();
                        entity.remove(Entity.RemovalReason.KILLED);
                        entity.onRemoved();
                    }
                    placedCrystals.removeIf(Pos -> Pos.getSquaredDistance((int) packet.getX(), (int) packet.getY(), (int) packet.getZ()) <= 36.0);
                });
            }
        }
    }


    @EventHandler
    public void onRender(RenderEvent event) {
        if (!render.getValue() || mc.interactionManager == null) return;
        placedCrystals.forEach(pos -> {
            RenderUtil.drawBoundingBox(new Box(pos), 2.0f, new Color(red.getValue(), green.getValue(), blue.getValue(), 255));
            RenderUtil.drawFilledBox(event.getMatrixStack(), new Box(pos), new Color(red.getValue(), green.getValue(), blue.getValue(), alpha.getValue()));
            if (placedCrystalsDamage.containsKey(pos)) {
                final float damage = placedCrystalsDamage.get(pos);
                final String damageText = (Math.floor(damage) == damage ? (int) damage : String.format("%.1f", damage)) + "";
                RenderUtil.drawTextIn3D(damageText, pos.toCenterPos(), 0, 0.2, 0, new Color(red.getValue(), green.getValue(), blue.getValue(), 255));
            }
        });
    }


    public boolean NeedPause() {
        if (mc.interactionManager == null || mc.player == null) return true;
        if (surround.isEnabled() && !surround.isSurrounded(mc.player) && surround.hasObsidian()) {
            if (!surround.activateOnlyOnShift.getValue()) return true;
            if (!mc.options.sneakKey.isPressed()) return true;
        }
        /*

        if (_autoTrapFeet.isEnabled() && !_autoTrapFeet.IsCurrentTargetTrapped() && _autoTrapFeet.HasObsidian())
            return true;

        if (_autoMend.isEnabled())
            return true;

        if (_selfTrap.isEnabled() && !_selfTrap.IsSelfTrapped() && _surround.HasObsidian())
            return true;

        if (_holeFiller.isEnabled() && _holeFiller.IsProcessing())
            return true;
         */
        if (pauseIfHittingBlock.getValue() && mc.interactionManager.isBreakingBlock() && mc.player.getMainHandStack().getItem() instanceof ToolItem) return true;
        if (pauseWhileEating.getValue() && mc.player.isUsingItem()) return true;
        return autoCity.isEnabled();
    }
}