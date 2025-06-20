package purr.purr.utils;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import purr.purr.Purr;
import purr.purr.events.impl.EventChangePlayerLook;
import purr.purr.events.impl.EventPlayerTravel;
import purr.purr.events.impl.EventRotateCamera;
import purr.purr.events.impl.EventUpdateInput;
import purr.purr.modules.Player.FreeCam;
import purr.purr.utils.math.MathUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TravelChangeManager {
    /*
    TravelChangers priority:

    KillAura: 10000
    LongJump(Elytra&Firework mode): 5000
    ElytraFlight: 5000
    MoveTask/TunnelTask: 1000
    Sprint: 500
     */

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private final List<TravelChanger> changers = new CopyOnWriteArrayList<>();
    private final FreeCam.FreeCamData freeCamData = new FreeCam.FreeCamData();
    private float lastYaw;
    private float lastPitch;

    public TravelChangeManager() {
        Purr.EVENT_BUS.subscribe(this);
    }

    public void addChanger(TravelChanger changer) {
        if (!changers.contains(changer)) {
            changers.add(changer);
            filterChangers();
            freeCamData.yaw = RotateUtils.getCameraYaw();
            freeCamData.pitch = RotateUtils.getCameraPitch();
        }
    }

    @SuppressWarnings("DataFlowIssue")
    public void removeChanger(TravelChanger changer) {
        if (changers.contains(changer)) {
            changers.remove(changer);
            filterChangers();
            if (!(mc.player == null || mc.world == null) && !Purr.moduleManager.getModuleByClass(FreeCam.class).getEnable()) {
                mc.player.setYaw(RotateUtils.getCameraYaw());
                mc.player.setPitch(RotateUtils.getCameraPitch());
            }
        }
    }

    public boolean containsChanger(TravelChanger changer) {
        return changers.contains(changer);
    }

    private void filterChangers() {
        changers.sort(Comparator.comparing(TravelChanger::priority));
        Collections.reverse(changers);
    }

    public float getLastYaw() {
        return lastYaw;
    }

    public float getLastPitch() {
        return lastPitch;
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onCameraRotate(EventRotateCamera e) {
        if (!changers.isEmpty() && !Purr.moduleManager.getModuleByClass(FreeCam.class).getEnable()) {
            e.setRotation(new Vec2f(freeCamData.yaw, freeCamData.pitch));
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onChangePlayer(EventChangePlayerLook e) {
        if (!changers.isEmpty() && !Purr.moduleManager.getModuleByClass(FreeCam.class).getEnable()) {
            e.cancel();
            freeCamData.changeLookDirection(e.cursorDeltaX, e.cursorDeltaY);
        }
    }

    @EventHandler
    @SuppressWarnings({"unused", "DataFlowIssue"})
    public void onPlayerTravel(EventPlayerTravel e) {
        if (!changers.isEmpty()) {
            Float[] rots = changers.getFirst().rotateGetter().get();
            lastYaw = rots[0];
            lastPitch = rots[1];
            mc.player.setYaw(lastYaw);
            mc.player.setPitch(lastPitch);
        }
    }

    @EventHandler
    @SuppressWarnings({"unused", "DataFlowIssue"})
    public void onInputUpdate(EventUpdateInput e) {
        if (!changers.isEmpty()) {
            TravelChanger travelChanger = changers.getFirst();
            if (travelChanger.withMoveFix().get())
                moveFix(mc.player.isSneaking(), travelChanger);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    private void moveFix(boolean sneaking, TravelChanger travelChanger) {
        float forward = (mc.player.input.playerInput.forward() ? 1 : mc.player.input.playerInput.backward() ? -1 : 0);
        float sideways = (mc.player.input.playerInput.left() ? 1 : mc.player.input.playerInput.right() ? -1 : 0);

        Matrix4f matrix = new Matrix4f();
        matrix.rotate((float) Math.toRadians(mc.player.getYaw() - RotateUtils.getCameraYaw()), 0, 1, 0);
        Vec3d updatedInput = MathUtils.transformPos(matrix, sideways, 0, forward);

        forward = (float) (travelChanger.strongMoveFix().get() ? updatedInput.getZ() : Math.round(updatedInput.getZ())) * (sneaking ? (float) mc.player.getAttributeValue(EntityAttributes.SNEAKING_SPEED) : 1);
        sideways = (float) (travelChanger.strongMoveFix().get() ? updatedInput.getX() : Math.round(updatedInput.getX())) * (sneaking ? (float) mc.player.getAttributeValue(EntityAttributes.SNEAKING_SPEED) : 1);

        InputUtils.setForward(forward > 0.0f);
        InputUtils.setBackward(forward < 0.0f);
        InputUtils.setLeft(sideways > 0.0f);
        InputUtils.setRight(sideways < 0.0f);

        mc.player.travel(new Vec3d(sideways, 0, forward));
    }
}
