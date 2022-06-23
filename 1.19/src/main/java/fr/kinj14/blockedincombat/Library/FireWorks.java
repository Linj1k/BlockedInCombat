package fr.kinj14.blockedincombat.Library;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

/**
 *
 * @author Kinj14
 * Allows fireworks to be launched easily
 *
 */
public class FireWorks {
	public static void SpawnFireWorks(Location loc) {
		SpawnFireWorks(loc, true, Color.GREEN, Color.BLUE, Type.STAR, true);
	}

	public static void SpawnFireWorks(Location loc, boolean flicker, Color Color, Color Fade, Type type, boolean trail) {
		Firework f = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
		f.detonate();
		FireworkMeta fm = f.getFireworkMeta();
		FireworkEffect effect = FireworkEffect.builder()
				.flicker(flicker)
				.withColor(Color)
				.withFade(Fade)
				.with(type)
				.trail(trail)
				.build();
		fm.addEffect(effect);
		f.setFireworkMeta(fm);
	}
}
