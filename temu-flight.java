import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent; // Using flight event as jump proxy
import org.bukkit.event.player.PlayerToggleSprintEvent; // Using sprint event as jump proxy
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class PlayerMovementModifier extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Vector velocity = player.getVelocity();

        //Forward/Backward
        if (player.isSprinting()) { //Using sprinting as a forward proxy.
          velocity.add(player.getLocation().getDirection().multiply(0.2));
        } else if (player.isSneaking()) {
          velocity.add(player.getLocation().getDirection().multiply(-0.2));
        }

        //Strafe Left/Right
        if (player.isFlying()) { // Using flying as a left proxy.
            Vector left = player.getLocation().getDirection().clone().crossProduct(new Vector(0,1,0)).normalize().multiply(-0.2);
            velocity.add(left);
        } else if (player.isSneaking() && player.isSprinting()) { //using sneak and sprint as a right proxy.
            Vector right = player.getLocation().getDirection().clone().crossProduct(new Vector(0,1,0)).normalize().multiply(0.2);
            velocity.add(right);
        }

        player.setVelocity(velocity);
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        if (event.isFlying()) {
            Player player = event.getPlayer();
            Vector velocity = player.getVelocity();
            velocity.setY(0.5);
            player.setVelocity(velocity);
        }
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        if (event.isSneaking()) {
            Player player = event.getPlayer();
            Vector velocity = player.getVelocity();
            velocity.setY(-0.5);
            player.setVelocity(velocity);
        }
    }
}
