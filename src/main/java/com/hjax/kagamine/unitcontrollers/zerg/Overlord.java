package com.hjax.kagamine.unitcontrollers.zerg;

import java.util.ArrayList;
import java.util.List;

import com.github.ocraft.s2client.protocol.debug.Color;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.hjax.kagamine.Vector2d;
import com.hjax.kagamine.economy.Base;
import com.hjax.kagamine.economy.BaseManager;
import com.hjax.kagamine.game.Game;
import com.hjax.kagamine.game.GameInfoCache;
import com.hjax.kagamine.game.HjaxUnit;
import com.hjax.kagamine.knowledge.Scouting;

public class Overlord {
	public static void on_frame(HjaxUnit u) {
		if (Scouting.overlords.containsKey(u.tag())) {
			pressure(u, Scouting.overlords.get(u.tag()));
		} else if (u.distance(BaseManager.main_base().location) > 10) {
			pressure(u, BaseManager.main_base);
		}
	}
	
	private static void pressure(HjaxUnit ovie, Base target) {

		List<Vector2d> negative_pressure = new ArrayList<>();
		List<Vector2d> positive_pressure = new ArrayList<>();
		
		Vector2d min = Game.min_point();
		Vector2d max = Game.max_point();
		
		negative_pressure.add(new Vector2d(0, (float) (500 / Math.pow(max.getY() - ovie.location().getY(), 3))));
		negative_pressure.add(new Vector2d(0, (float) (-500 / Math.pow(ovie.location().getY() - min.getY(), 3))));
		negative_pressure.add(new Vector2d((float) (500 / Math.pow(max.getX() - ovie.location().getX(), 3)), 0));
		negative_pressure.add(new Vector2d((float) (-500 / Math.pow(ovie.location().getX() - min.getX(), 3)), 0));
		
		positive_pressure.add(ovie.location().directionTo(target.location).scale(10));
		
		Game.draw_line(ovie.location(), target.location, Color.GREEN);
		
		for (HjaxUnit enemy: GameInfoCache.get_units(Alliance.ENEMY)) {
			if (Game.hits_air(enemy.type())) {
				negative_pressure.add(ovie.location().directionTo(enemy.location()).scale((float) (300 / Math.pow(ovie.location().distance(enemy.location()), 2))));
			} 
		}
		
		float x = 0;
		float y = 0;

		for (Vector2d v : negative_pressure) {
			x -= v.getX();
			y -= v.getY();
		}

		for (Vector2d v : positive_pressure) {
			x += v.getX();
			y += v.getY();
		}
		Vector2d pressure = new Vector2d(x, y).normalized();

		ovie.move(Vector2d.of(ovie.location().getX() + 4 * pressure.getX(), ovie.location().getY() + 4 * pressure.getY()));
	}
}
