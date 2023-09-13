package me.ionar.salhack.module.combat;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.EventEra;
import me.ionar.salhack.events.entity.EntityRemovedEvent;
import me.ionar.salhack.events.network.PacketEvent;
import me.ionar.salhack.events.player.PlayerMotionUpdate;
import me.ionar.salhack.events.render.RenderEvent;
import me.ionar.salhack.events.world.TickEvent;
import me.ionar.salhack.managers.FriendManager;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.CrystalUtils;
import me.ionar.salhack.util.Timer;
import me.ionar.salhack.util.entity.EntityUtil;
import me.ionar.salhack.util.entity.PlayerUtil;
import me.ionar.salhack.util.render.RenderUtil;
import net.minecraft.client.MinecraftClient;
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
import java.util.stream.Collectors;

public class AutoCrystalRewrite extends Module {
    public static final Value<BreakModes> BreakMode = new Value<BreakModes>("BreakMode", new String[]{"BM"}, "Mode of breaking to use", BreakModes.Always);
    public static final Value<PlaceModes> PlaceMode = new Value<PlaceModes>("PlaceMode", new String[]{"BM"}, "Mode of placing to use", PlaceModes.Most);
    public static final Value<Float> PlaceRadius = new Value<Float>("PlaceRadius", new String[]{""}, "Radius for placing", 4.0f, 0.0f, 5.0f, 0.5f);
    public static final Value<Float> BreakRadius = new Value<Float>("BreakRadius", new String[]{""}, "Radius for BreakRadius", 4.0f, 0.0f, 5.0f, 0.5f);
    public static final Value<Float> WallsRange = new Value<Float>("WallsRange", new String[]{""}, "Max distance through walls", 3.5f, 0.0f, 5.0f, 0.5f);
    public static final Value<Boolean> MultiPlace = new Value<Boolean>("MultiPlace", new String[]{"MultiPlaces"}, "Tries to multiplace", false);
    public static final Value<Integer> Ticks = new Value<Integer>("Ticks", new String[]{"IgnoreTicks"}, "The number of ticks to ignore on client update", 2, 0, 20, 1);

    public static final Value<Float> MinDMG = new Value<Float>("MinDMG", new String[]{""}, "Minimum damage to do to your opponent", 4.0f, 0.0f, 20.0f, 1f);
    public static final Value<Float> MaxSelfDMG = new Value<Float>("MaxSelfDMG", new String[]{""}, "Max self dmg for breaking crystals that will deal tons of dmg", 4.0f, 0.0f, 20.0f, 1.0f);
    public static final Value<Float> FacePlace = new Value<Float>("FacePlace", new String[]{""}, "Required target health for faceplacing", 8.0f, 0.0f, 20.0f, 0.5f);
    public static final Value<Boolean> AutoSwitch = new Value<Boolean>("AutoSwitch", new String[]{""}, "Automatically switches to crystals in your hotbar", true);
    public static final Value<Boolean> PauseIfHittingBlock = new Value<Boolean>("PauseIfHittingBlock", new String[]{""}, "Pauses when your hitting a block with a pickaxe", false);
    public static final Value<Boolean> PauseWhileEating = new Value<Boolean>("PauseWhileEating", new String[]{"PauseWhileEating"}, "Pause while eating", false);
    public static final Value<Boolean> NoSuicide = new Value<Boolean>("NoSuicide", new String[]{"NS"}, "Doesn't commit suicide/pop if you are going to take fatal damage from self placed crystal", true);
    public static final Value<Boolean> AntiWeakness = new Value<Boolean>("AntiWeakness", new String[]{"AW"}, "Switches to a sword to try and break crystals", true);

    public static final Value<Boolean> Render = new Value<Boolean>("Render", new String[]{"Render"}, "Allows for rendering of block placements", true);
    public static final Value<Integer> Red = new Value<Integer>("Red", new String[]{"Red"}, "Red for rendering", 0x33, 0, 255, 5);
    public static final Value<Integer> Green = new Value<Integer>("Green", new String[]{"Green"}, "Green for rendering", 0xFF, 0, 255, 5);
    public static final Value<Integer> Blue = new Value<Integer>("Blue", new String[]{"Blue"}, "Blue for rendering", 0xF3, 0, 255, 5);
    public static final Value<Integer> Alpha = new Value<Integer>("Alpha", new String[]{"Alpha"}, "Alpha for rendering", 0x99, 0, 255, 5);

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

    private AutoCrystalRewrite Mod = null;
    public static Timer _removeVisualTimer = new Timer();
    private Timer _rotationResetTimer = new Timer();
    private ConcurrentLinkedQueue<BlockPos> _placedCrystals = new ConcurrentLinkedQueue<>();
    private ConcurrentHashMap<BlockPos, Float> _placedCrystalsDamage = new ConcurrentHashMap<>();
    private double[] _rotations = null;
    private ConcurrentHashMap<EndCrystalEntity, Integer> _attackedEnderCrystals = new ConcurrentHashMap<>();
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private String _lastTarget = null;
    private int _remainingTicks;
    private BlockPos _lastPlaceLocation = BlockPos.ORIGIN;

    // Modules used for pausing

    // private SurroundModule _surround = null;
    // private AutoTrapFeet _autoTrapFeet = null;
    // private AutoMendArmorModule _autoMend = null;
    // private SelfTrapModule _selfTrap = null;
    // private HoleFillerModule _holeFiller = null;
    // private AutoCityModule _autoCity = null;

    @Override
    public void init() {
        Mod = this;

        // initalize the mods as needed
        // _surround = (SurroundModule) ModuleManager.Get().GetMod(SurroundModule.class);
        // _autoTrapFeet = (AutoTrapFeet) ModuleManager.Get().GetMod(AutoTrapFeet.class);
        // _autoMend = (AutoMendArmorModule) ModuleManager.Get().GetMod(AutoMendArmorModule.class);
        // _selfTrap = (SelfTrapModule) ModuleManager.Get().GetMod(SelfTrapModule.class);
        // _holeFiller = (HoleFillerModule) ModuleManager.Get().GetMod(HoleFillerModule.class);
        // _autoCity = (AutoCityModule) ModuleManager.Get().GetMod(AutoCityModule.class);
    }


    @Override
    public void onEnable() {
        super.onEnable();

        // clear placed crystals, we don't want to display them later on
        _placedCrystals.clear();
        _placedCrystalsDamage.clear();

        // also reset ticks on enable, we need as much speed as we can get.
        _remainingTicks = 0;

        // reset this, we will get a new one
        _lastPlaceLocation = BlockPos.ORIGIN;
    }

    @Override
    public String getMetaData() {
        // display our target name
        return _lastTarget;
    }

    @EventHandler
    public void onEntityRemove(EntityRemovedEvent event) {
        if (event.GetEntity() instanceof EndCrystalEntity) {
            // we don't need null things in this list.
            _attackedEnderCrystals.remove((EndCrystalEntity) event.GetEntity());
        }
    }

    private boolean ValidateCrystal(EndCrystalEntity e) {
        if (e == null || !e.isAlive())
            return false;

        if (_attackedEnderCrystals.containsKey(e) && _attackedEnderCrystals.get(e) > 5)
            return false;

        if (e.distanceTo(mc.player) > (!mc.player.canSee(e) ? WallsRange.getValue() : BreakRadius.getValue()))
            return false;

        switch (BreakMode.getValue()) {
            case OnlyOwn -> {
                return e.distanceTo(e) <= 3;
            }
            case Smart -> {
                float selfDamage = CrystalUtils.calculateDamage(mc.world, e.getX(), e.getY(), e.getZ(), mc.player, 0);
                if (selfDamage > MaxSelfDMG.getValue())
                    return false;
                if (NoSuicide.getValue() && selfDamage >= mc.player.getHealth() + mc.player.getAbsorptionAmount())
                    return false;

                // iterate through all players, and crystal positions to find the best position for most damage
                for (PlayerEntity player : mc.world.getPlayers()) {
                    // Ignore if the player is us, a friend, dead, or has no health (the dead variable is sometimes delayed)
                    if (player == mc.player || FriendManager.Get().IsFriend(player) || mc.player.isDead() || (mc.player.getHealth() + mc.player.getAbsorptionAmount()) <= 0.0f)
                        continue;

                    // store this as a variable for faceplace per player
                    float minDamage = MinDMG.getValue();

                    // check if players health + gap health is less than or equal to faceplace, then we activate faceplacing
                    if (player.getHealth() + player.getAbsorptionAmount() <= FacePlace.getValue())
                        minDamage = 1f;

                    float calculatedDamage = CrystalUtils.calculateDamage(mc.world, e.getX(), e.getY(), e.getZ(), player, 0);

                    if (calculatedDamage > minDamage)
                        return true;
                }
                return false;
            }
            default -> {
            }
        }

        return true;
    }

    /*
     * Returns nearest crystal to an entity, if the crystal is not null or dead
     * @entity - entity to get smallest distance from
     */
    public EndCrystalEntity GetNearestCrystalTo(Entity entity) {
        List<Entity> entities = new ArrayList<>();
        mc.world.getEntities().forEach(entities::add);
        return entities.stream().filter(e -> e instanceof EndCrystalEntity && ValidateCrystal((EndCrystalEntity) e)).map(e -> (EndCrystalEntity) e).min(Comparator.comparing(entity::distanceTo)).orElse(null);
    }

    public void AddAttackedCrystal(EndCrystalEntity crystal) {
        if (_attackedEnderCrystals.containsKey(crystal)) {
            int value = _attackedEnderCrystals.get(crystal);
            _attackedEnderCrystals.put(crystal, value + 1);
        } else
            _attackedEnderCrystals.put(crystal, 1);
    }

    private boolean VerifyCrystalBlocks(BlockPos pos) {
        // check distance
        if (mc.player.squaredDistanceTo(pos.toCenterPos()) > PlaceRadius.getValue() * PlaceRadius.getValue())
            return false;

        // check walls range
        if (WallsRange.getValue() > 0) {
            if (!PlayerUtil.CanSeeBlock(pos))
                if (pos.getSquaredDistance((int) mc.player.getX(), (int) mc.player.getY(), (int) mc.player.getZ()) > WallsRange.getValue() * WallsRange.getValue())
                    return false;
        }

        // check self damage
        float selfDamage = CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, mc.player, 0);

        // make sure self damage is not greater than maxselfdamage
        if (selfDamage > MaxSelfDMG.getValue())
            return false;

        // no suicide, verify self damage won't kill us
        if (NoSuicide.getValue() && selfDamage >= mc.player.getHealth() + mc.player.getAbsorptionAmount())
            return false;

        // it's an ok position.
        return true;
    }

    @EventHandler
    private void OnTick(TickEvent event) {
        // this is our 1 second timer to remove our attackedEnderCrystals list, and remove the first placedCrystal for the visualizer.
        if (_removeVisualTimer.passed(1000)) {
            _removeVisualTimer.reset();

            if (!_placedCrystals.isEmpty()) {
                BlockPos removed = _placedCrystals.remove();

                if (removed != null)
                    _placedCrystalsDamage.remove(removed);
            }

            _attackedEnderCrystals.clear();
        }

        if (NeedPause()) {
            _remainingTicks = 0;
            return;
        }

        // override
        if (PlaceMode.getValue() == PlaceModes.Lethal && _lastPlaceLocation != BlockPos.ORIGIN) {
            float damage = 0f;

            PlayerEntity trappedTarget = null;

            // verify that this location will exceed lethal damage for atleast one enemy.
            // iterate through all players, and crystal positions to find the best position for most damage
            for (PlayerEntity player : mc.world.getPlayers()) {
                // Ignore if the player is us, a friend, dead, or has no health (the dead variable is sometimes delayed)
                if (player == mc.player || FriendManager.Get().IsFriend(player) || mc.player.isDead() || (mc.player.getHealth() + mc.player.getAbsorptionAmount()) <= 0.0f)
                    continue;

                // store this as a variable for faceplace per player
                float minDamage = MinDMG.getValue();

                // check if players health + gap health is less than or equal to faceplace, then we activate faceplacing
                if (player.getHealth() + player.getAbsorptionAmount() <= FacePlace.getValue())
                    minDamage = 1f;

                float calculatedDamage = CrystalUtils.calculateDamage(mc.world, _lastPlaceLocation.getX() + 0.5, _lastPlaceLocation.getY() + 1.0, _lastPlaceLocation.getZ() + 0.5, player, 0);

                if (calculatedDamage >= minDamage && calculatedDamage > damage) {
                    damage = calculatedDamage;
                    trappedTarget = player;
                }
            }

            if (damage == 0f || trappedTarget == null) {
                // set this back to null
                _lastPlaceLocation = BlockPos.ORIGIN;
            }
        }


        if (_remainingTicks > 0) {
            --_remainingTicks;
        }

        boolean skipUpdateBlocks = _lastPlaceLocation != BlockPos.ORIGIN && PlaceMode.getValue() == PlaceModes.Lethal;

        // create a list of available place locations
        ArrayList<BlockPos> placeLocations = new ArrayList<BlockPos>();
        PlayerEntity playerTarget = null;

        // if we don't need to skip update, get crystal blocks
        if (!skipUpdateBlocks && _remainingTicks <= 0) {
            _remainingTicks = Ticks.getValue();

            // this is the most expensive code, we need to get valid crystal blocks.
            final List<BlockPos> cachedCrystalBlocks = CrystalUtils.findCrystalBlocks(mc.player, AutoCrystalRewrite.PlaceRadius.getValue()).stream().filter(pos -> VerifyCrystalBlocks(pos)).collect(Collectors.toList());

            // this is where we will iterate through all players (for most damage) and cachedCrystalBlocks
            if (!cachedCrystalBlocks.isEmpty()) {
                float damage = 0f;
                String target = null;

                // iterate through all players, and crystal positions to find the best position for most damage
                for (PlayerEntity player : mc.world.getPlayers()) {
                    // Ignore if the player is us, a friend, dead, or has no health (the dead variable is sometimes delayed)
                    if (player == mc.player || FriendManager.Get().IsFriend(player) || mc.player.isDead() || (mc.player.getHealth() + mc.player.getAbsorptionAmount()) <= 0.0f)
                        continue;

                    // store this as a variable for faceplace per player
                    float minDamage = MinDMG.getValue();

                    // check if players health + gap health is less than or equal to faceplace, then we activate faceplacing
                    if (player.getHealth() + player.getAbsorptionAmount() <= FacePlace.getValue())
                        minDamage = 1f;

                    // iterate through all valid crystal blocks for this player, and calculate the damages.
                    for (BlockPos pos : cachedCrystalBlocks) {
                        float calculatedDamage = CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, player, 0);

                        if (calculatedDamage >= minDamage && calculatedDamage > damage) {
                            damage = calculatedDamage;
                            if (!placeLocations.contains(pos))
                                placeLocations.add(pos);
                            target = player.getName().getString();
                            playerTarget = player;
                        }
                    }
                }

                // playerTarget can nullptr during client tick
                if (playerTarget != null) {
                    // the player could have died during this code run, wait till next tick for doing more calculations.
                    if (playerTarget.isDead() || playerTarget.getHealth() <= 0.0f)
                        return;

                    // ensure we have place locations
                    if (!placeLocations.isEmpty()) {
                        // store this as a variable for faceplace per player
                        float minDamage = MinDMG.getValue();

                        // check if players health + gap health is less than or equal to faceplace, then we activate faceplacing
                        if (playerTarget.getHealth() + playerTarget.getAbsorptionAmount() <= FacePlace.getValue())
                            minDamage = 1f;

                        final float finalMinDamage = minDamage;
                        final PlayerEntity finalTarget = playerTarget;

                        // iterate this again, we need to remove some values that are useless, since we iterated all players
                        placeLocations.removeIf(pos -> CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, finalTarget, 0) < finalMinDamage);

                        // at this point, the place locations list is in asc order, we need to reverse it to get to desc
                        Collections.reverse(placeLocations);

                        // store our last target name.
                        _lastTarget = target;
                    }
                }
            }
        }

        // at this point, we are going to destroy/place crystals.

        // Get nearest crystal to the player, we will need to null check this on the timer.
        EndCrystalEntity crystal = GetNearestCrystalTo(mc.player);

        // get a valid crystal in range, and check if it's in break radius
        boolean isValidCrystal = crystal != null ? mc.player.distanceTo(crystal) < BreakRadius.getValue() : false;

        // no where to place or break
        if (!isValidCrystal && placeLocations.isEmpty() && !skipUpdateBlocks) {
            _remainingTicks = 0;
            return;
        }

        if (isValidCrystal && (skipUpdateBlocks ? true : _remainingTicks == Ticks.getValue())) // we are checking null here because we don't want to waste time not destroying crystals right away
        {
            if (AntiWeakness.getValue() && mc.player.hasStatusEffect(StatusEffects.WEAKNESS)) {
                if (mc.player.getMainHandStack() == ItemStack.EMPTY || (!(mc.player.getMainHandStack().getItem() instanceof SwordItem) && !(mc.player.getMainHandStack().getItem() instanceof ToolItem))) {
                    for (int i = 0; i < 9; ++i) {
                        ItemStack stack = mc.player.getInventory().getStack(i);

                        if (stack.isEmpty())
                            continue;

                        if (stack.getItem() instanceof ToolItem || stack.getItem() instanceof SwordItem) {
                            mc.player.getInventory().selectedSlot = i;
                            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(i));
                            break;
                        }
                    }
                }
            }

            // get facing rotations to the crystal
            _rotations = EntityUtil.calculateLookAt(crystal.getX() + 0.5, crystal.getY() - 0.5, crystal.getZ() + 0.5, mc.player);
            _rotationResetTimer.reset();

            // swing arm and attack the entity
            mc.interactionManager.attackEntity(mc.player, crystal);
            mc.player.swingHand(Hand.MAIN_HAND);
            AddAttackedCrystal(crystal);

            // if we are not multiplacing return here, we have something to do for this tick.
            if (!MultiPlace.getValue())
                return;
        }

        // verify the placeTimer is ready, selectedPosition is not 0,0,0 and the event isn't already cancelled
        if (!placeLocations.isEmpty() || skipUpdateBlocks) {
            // auto switch
            if (AutoSwitch.getValue()) {
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

            // no need to process the code below if we are not using off hand crystal or main hand crystal
            if (mc.player.getMainHandStack().getItem() != Items.END_CRYSTAL && mc.player.getOffHandStack().getItem() != Items.END_CRYSTAL)
                return;

            BlockPos selectedPos = null;

            // iterate through available place locations
            if (!skipUpdateBlocks) {
                for (BlockPos pos : placeLocations) {
                    // verify we can still place crystals at this location, if we can't we try next location
                    if (CrystalUtils.canPlaceCrystal(pos)) {
                        selectedPos = pos;
                        break;
                    }
                }
            } else
                selectedPos = _lastPlaceLocation;

            // nothing found... this is bad, wait for next tick to correct it
            if (selectedPos == null) {
                _remainingTicks = 0;
                return;
            }

            // get facing rotations to the position, store them for the motion tick to handle it
            _rotations = EntityUtil.calculateLookAt(selectedPos.getX() + 0.5, selectedPos.getY() - 0.5, selectedPos.getZ() + 0.5, mc.player);
            _rotationResetTimer.reset();

            // create a raytrace between player's position and the selected block position
            BlockHitResult result = PlayerUtil.rayCastBlock(new RaycastContext(PlayerUtil.getEyesPos(mc.player), selectedPos.toCenterPos().offset(Direction.DOWN, 0.5), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player), selectedPos);

            // this will allow for bypassing placing through walls afaik
            Direction facing;

            if (result == null || result.getSide() == null)
                facing = Direction.UP;
            else
                facing = result.getSide();

            mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(mc.player.getOffHandStack().getItem() == Items.END_CRYSTAL ? Hand.OFF_HAND : Hand.MAIN_HAND, result, 0));
            mc.player.swingHand(mc.player.getOffHandStack().getItem() == Items.END_CRYSTAL ? Hand.OFF_HAND : Hand.MAIN_HAND);

            // if placedcrystals already contains this position, remove it because we need to have it at the back of the list
            if (_placedCrystals.contains(selectedPos))
                _placedCrystals.remove(selectedPos);

            // adds the selectedPos to the back of the placed crystals list
            _placedCrystals.add(selectedPos);

            if (playerTarget != null) {
                float calculatedDamage = CrystalUtils.calculateDamage(mc.world, selectedPos.getX() + 0.5, selectedPos.getY() + 1.0, selectedPos.getZ() + 0.5, playerTarget, 0);

                _placedCrystalsDamage.put(selectedPos, calculatedDamage);
            }

            if (_lastPlaceLocation != BlockPos.ORIGIN && _lastPlaceLocation == selectedPos) {
                // reset ticks, we don't need to do more rotations for this position, so we can crystal faster.
                if (PlaceMode.getValue() == PlaceModes.Lethal)
                    _remainingTicks = 0;
            } else // set this to our last place location
                _lastPlaceLocation = selectedPos;
        }
    }

    @EventHandler
    public void onPlayerMotionUpdate(PlayerMotionUpdate event) {
        // we only want to run this event on pre motion, but don't reset rotations here
        if (event.getEra() != EventEra.PRE)
            return;

        if (event.isCancelled()) {
            _rotations = null;
            return;
        }

        // if the previous event isn't cancelled, or if we don't need to pause.
        if (NeedPause()) {
            _rotations = null;
            return;
        }

        // in order to not flag NCP, we don't want to reset our pitch after we have nothing to do, so do it every second. more legit
        if (_rotationResetTimer.passed(1000)) {
            _rotations = null;
        }

        // rotations are valid, cancel this update and use our custom rotations instead.
        if (_rotations != null) {
            event.cancel();
            PlayerUtil.PacketFacePitchAndYaw((float) _rotations[1], (float) _rotations[0]);
        }
    }

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof PlaySoundS2CPacket packet) {
            if (mc.world == null)
                return;

            // we need to remove crystals on this packet, because the server sends packets too slow to remove them
            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                // loadedEntityList is not thread safe, create a copy and iterate it

                List<Entity> e_List = new ArrayList<>();
                mc.world.getEntities().forEach(e -> e_List.add(e));
                e_List.forEach(e ->
                {
                    // if it's an endercrystal, within 6 distance, set it to be dead
                    if (e instanceof EndCrystalEntity)
                        if (e.squaredDistanceTo(packet.getX(), packet.getY(), packet.getZ()) <= 36.0) {
                            e.kill();
                            e.remove(Entity.RemovalReason.KILLED);
                            e.onRemoved();
                        }

                    // remove all crystals within 6 blocks from the placed crystals list
                    _placedCrystals.removeIf(p_Pos -> p_Pos.getSquaredDistance((int) packet.getX(), (int) packet.getY(), (int) packet.getZ()) <= 36.0);
                });
            }
        }
    }


    @EventHandler
    public void onRender(RenderEvent event) {
        if (!Render.getValue())
            return;

        _placedCrystals.forEach(pos ->
        {

            RenderUtil.drawBoundingBox(new Box(pos), 2.0f, new Color(Red.getValue(), Green.getValue(), Blue.getValue(), 255));
            RenderUtil.drawFilledBox(event.getMatrixStack(), new Box(pos), new Color(Red.getValue(), Green.getValue(), Blue.getValue(), Alpha.getValue()));


            if (_placedCrystalsDamage.containsKey(pos)) {
                final float damage = _placedCrystalsDamage.get(pos);
                final String damageText = (Math.floor(damage) == damage ? (int) damage : String.format("%.1f", damage)) + "";
                RenderUtil.drawTextIn3D(damageText, pos.toCenterPos(), 0, 0.2, 0, new Color(Red.getValue(), Green.getValue(), Blue.getValue(), 255));
            }
        });
    }


    public boolean NeedPause() {
        /// We need to pause if we have surround enabled, and don't have obsidian

        /*
         if (_surround.isEnabled() && !_surround.IsSurrounded(mc.player) && _surround.HasObsidian()) {
             if (!_surround.ActivateOnlyOnShift.getValue())
                 return true;

            if (!mc.options.sneakKey.isKeyDown())
                return true;
        }

        if (_autoTrapFeet.isEnabled() && !_autoTrapFeet.IsCurrentTargetTrapped() && _autoTrapFeet.HasObsidian())
            return true;

        if (_autoMend.isEnabled())
            return true;

        if (_selfTrap.isEnabled() && !_selfTrap.IsSelfTrapped() && _surround.HasObsidian())
            return true;

        if (_holeFiller.isEnabled() && _holeFiller.IsProcessing())
            return true;
         */

        if (PauseIfHittingBlock.getValue() && mc.interactionManager.isBreakingBlock() && mc.player.getMainHandStack().getItem() instanceof ToolItem)
            return true;

        if (PauseWhileEating.getValue() && mc.player.isUsingItem())
            return true;

        // if (_autoCity.isEnabled())
        //    return true;

        return false;
    }

    public String getTarget() {
        return _lastTarget;
    }
}