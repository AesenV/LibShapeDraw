package com.gameminers.lsdtest;

import libshapedraw.LibShapeDraw;
import libshapedraw.primitive.Color;
import libshapedraw.shape.WireframeCuboid;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

@Mod(name="Basic LibShapeDraw Test", modid="BLSDT")
public class BasicLibShapeDrawTest {
	@EventHandler
	public void postinit(FMLPostInitializationEvent e) {
		LibShapeDraw lsd = new LibShapeDraw("BasicLSDTest");
		WireframeCuboid shape = new WireframeCuboid(0, 0, 0, 1, 1, 1);
		shape.setLineStyle(Color.TOMATO.copy(), 3.0f, true);
		lsd.addShape(shape);
	}
}
