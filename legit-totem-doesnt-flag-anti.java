import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.LivingEntity;
import net.fabricmc.fabric.api.event.entity.living.LivingEntityEvents;
import java.util.HashMap;
import java.util.UUID;

public class TotemSwapFabric implements ModInitializer {

    private HashMap<UUID, Long> lastSwapTime = new HashMap<>();

    @Override
    public void onInitialize() {
        LivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
            if (entity instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) entity;
                if (player.getInventory().contains(new ItemStack(Items.TOTEM_OF_UNDYING))) {
                    handleTotemSwap(player);
                }
            }
            return true;
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack itemStack = player.getStackInHand(hand);
            if (itemStack.getItem() == Items.TOTEM_OF_UNDYING) {
                return TypedActionResult.pass(itemStack); // Prevent swap if already holding totem
            }
            if (player.isSneaking()) {
                handleTotemSwap(player);
            }
            return TypedActionResult.pass(itemStack);
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for(PlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if(player == null) {
                    lastSwapTime.remove(player.getUuid());
                }
            }
        });
    }

    private void handleTotemSwap(PlayerEntity player) {
        if (player.getInventory().contains(new ItemStack(Items.TOTEM_OF_UNDYING))) {
            long currentTime = System.currentTimeMillis();
            if (lastSwapTime.containsKey(player.getUuid()) && (currentTime - lastSwapTime.get(player.getUuid())) < 100) {
                return; // Prevent rapid swaps.
            }

            lastSwapTime.put(player.getUuid(), currentTime);

            swapTotem(player);
        }
    }

    private void swapTotem(PlayerEntity player) {
        int totemSlot = -1;
        for (int i = 0; i < player.getInventory().size(); i++) {
            if (player.getInventory().getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
                totemSlot = i;
                break;
            }
        }

        if (totemSlot != -1) {
            ItemStack totem = player.getInventory().getStack(totemSlot);
            ItemStack heldItem = player.getMainHandStack();
            player.getInventory().setStack(totemSlot, heldItem);
            player.getMainHandStack().decrement(player.getMainHandStack().getCount()); //Clear the hand
            player.getInventory().offerOrDrop(totem); //Put totem in hand.
        }
    }
}
