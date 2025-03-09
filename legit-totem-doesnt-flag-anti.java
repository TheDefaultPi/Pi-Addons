import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;
import java.util.HashMap;
import java.util.UUID;

public class TotemSwapToggle implements ClientModInitializer {

    private boolean enabled = false;
    private KeyBinding toggleKey;
    private HashMap<UUID, Long> lastSwapTime = new HashMap<>();

    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.totemswap.toggle", 
                InputUtil.Type.KEYSYM, 
                GLFW.GLFW_KEY_Y, 
                "category.totemswap" 
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (toggleKey.wasPressed()) {
                enabled = !enabled;
                if (client.player != null) {
                    client.player.sendMessage(net.minecraft.text.Text.of("Totem Swap: " + (enabled ? "Enabled" : "Disabled")), true);
                }
            }

            if (enabled && client.player != null) {
                if (client.player.isSneaking()) {
                    handleTotemSwap(client.player);
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
            player.getMainHandStack().decrement(player.getMainHandStack().getCount()); 
            player.getInventory().offerOrDrop(totem); //Put totem in hand.
        }
    }
}
