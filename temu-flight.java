import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.PlayerMoveCallback;
import net.fabricmc.fabric.api.event.player.PlayerToggleSneakCallback;
import net.fabricmc.fabric.api.event.player.PlayerToggleFlightCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Direction;
import net.minecraft.util.ActionResult;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerMovementFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        PlayerMoveCallback.EVENT.register((player, posFrom, posTo) -> {
            Vec3d velocity = player.getVelocity();

            // Forward/Backward (using sprinting as forward proxy)
            if (player.isSprinting()) {
                Vec3d direction = player.getRotationVector();
                velocity = velocity.add(direction.multiply(0.2));
            } else if (player.isSneaking()) {
                Vec3d direction = player.getRotationVector();
                velocity = velocity.add(direction.multiply(-0.2));
            }

            // Strafe Left/Right (using flying as left proxy, and sneak + sprint as right)
            if (player.getAbilities().flying) {
                Vec3d left = player.getRotationVector().crossProduct(new Vec3d(0, 1, 0)).normalize().multiply(-0.2);
                velocity = velocity.add(left);
            } else if (player.isSneaking() && player.isSprinting()) {
                Vec3d right = player.getRotationVector().crossProduct(new Vec3d(0, 1, 0)).normalize().multiply(0.2);
                velocity = velocity.add(right);
            }

            player.setVelocity(velocity);
            return ActionResult.PASS;
        });

        PlayerToggleFlightCallback.EVENT.register((player, enabled) -> {
            if (enabled) {
                Vec3d velocity = player.getVelocity();
                velocity = new Vec3d(velocity.getX(), 0.5, velocity.getZ());
                player.setVelocity(velocity);
            }
            return ActionResult.PASS;
        });

        PlayerToggleSneakCallback.EVENT.register((player, enabled) -> {
            if (enabled) {
                Vec3d velocity = player.getVelocity();
                velocity = new Vec3d(velocity.getX(), -0.5, velocity.getZ());
                player.setVelocity(velocity);
            }
            return ActionResult.PASS;
        });
    }
}
