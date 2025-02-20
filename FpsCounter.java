  import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
    import net.minecraft.client.MinecraftClient;
    import net.minecraft.client.util.math.MatrixStack;
    import net.minecraft.text.Text;

    public class FpsCounter {


        public void renderFps(MatrixStack matrices) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.textRenderer != null) {
                client.textRenderer.drawWithShadow(matrices, Text.literal("FPS: " + fps), 10, 10, 0xFFFFFF);
            }
        }
    }
