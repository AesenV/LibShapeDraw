package libshapedraw.internal;
// THIS SOURCE FILE WAS AUTOMATICALLY GENERATED. DO NOT MANUALLY EDIT.
// Edit projects/dev/src/main/java/net/minecraft/src/mod_LibShapeDraw.java
// and then run the projects/dev/src/main/python/obfuscate.py script.

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import libshapedraw.MinecraftAccess;
import libshapedraw.primitive.ReadonlyVector3;
import libshapedraw.primitive.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Timer;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;



/**
 * Internal class. Mods using the LibShapeDraw API can safely ignore this.
 * Rather, instantiate {@link libshapedraw.LibShapeDraw}.
 * <p>
 * This is a ModLoader mod (also compatible with Forge/FML) that links itself
 * to the internal API Controller, providing it data and events from Minecraft.
 * This class does the bare minimum of processing before passing these off to
 * the controller. I.e., this class is a thin wrapper for Minecraft used by
 * LibShapeDraw.
 * <p>
 * As a wrapper, all direct interaction with Minecraft objects passes through
 * this class, making the LibShapeDraw API itself clean and free of obfuscated
 * code. (There is a single exception: LSDModDirectory.getMinecraftDir.)
 */
@Mod(modid = "LibShapeDraw", name = "LibShapeDraw", version="1.4-SNAPSHOT")
public class LibShapeDrawMod implements MinecraftAccess {

    private Minecraft minecraft;
    private Timer timer;
    private LSDController controller;
    private Object curWorld;
    private EntityClientPlayerMP curPlayer;
    private Integer curDimension;

    @Instance("LibShapeDraw")
    public static LibShapeDrawMod fmlInstanceDoNotTouch;

    public LibShapeDrawMod() {
        controller = LSDController.getInstance();
        controller.initialize(this);
    }

    @EventHandler
    public void init(FMLInitializationEvent e) {
        minecraft = Minecraft.getMinecraft();
        // Get a reference to Minecraft's timer so we can get the partial
        // tick time for rendering (it's not passed to the profiler directly).
        // 
        // There's only one Timer field declared by Minecraft so it's safe to
        // look it up by type.
        timer = (Timer) LSDUtil.getFieldValue(LSDUtil.getFieldByType(Minecraft.class, Timer.class, 0), minecraft);
        
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
        LSDController.getLog().info(getClass().getName() + " loaded");
    }

    @SubscribeEvent
    public void renderHand(RenderHandEvent e) {
    	Minecraft.getMinecraft().mcProfiler.endStartSection("LibShapeDraw");
    	render();
    	Minecraft.getMinecraft().mcProfiler.endStartSection("hand");
    }
    
    @SubscribeEvent
    public void clientConnect(ClientConnectedToServerEvent cctse) {
        LSDController.getLog().info(getClass().getName() + " new server connection");
        curWorld = null;
        curPlayer = null;
        curDimension = null;
    }

    @SubscribeEvent
    public void onTickInGame(TickEvent te) {
        if (te.phase != TickEvent.Phase.START || te.type != TickEvent.Type.CLIENT) {
            return;
        }
        if (minecraft.theWorld == null || minecraft.thePlayer == null) {
        	return;
        }
        final ReadonlyVector3 playerCoords = getPlayerCoords();

        if (curWorld != minecraft.theWorld || curPlayer != minecraft.thePlayer) {
            curWorld = minecraft.theWorld;
            curPlayer = minecraft.thePlayer;

            // Dispatch respawn event to Controller.
            final int newDimension = curPlayer.dimension;
            controller.respawn(playerCoords,
            		curDimension == null,
            		curDimension == null || curDimension != newDimension);
            curDimension = newDimension;
        }

        // Dispatch game tick event to Controller.
        controller.gameTick(playerCoords);


        return;
    }

    /** Dispatch render event to Controller. */
    protected void render() {
        controller.render(getPlayerCoords(), minecraft.gameSettings.hideGUI && minecraft.currentScreen == null);
    }

    /**
     * Get the player's current coordinates, adjusted for movement that occurs
     * between game ticks.
     */
    private ReadonlyVector3 getPlayerCoords() {
        if (curPlayer == null) {
            return Vector3.ZEROS;
        }
        final float partialTick = getPartialTick();
        return new Vector3(
        		curPlayer.prevPosX + partialTick * (curPlayer.posX - curPlayer.prevPosX),
        		curPlayer.prevPosY + partialTick * (curPlayer.posY - curPlayer.prevPosY),
        		curPlayer.prevPosZ + partialTick * (curPlayer.posZ - curPlayer.prevPosZ));
    }

    // ====
    // MinecraftAccess implementation
    // ====

    @Override
    public MinecraftAccess startDrawing(int mode) {
        Tessellator.instance.startDrawing(mode);
        return this;
    }

    @Override
    public MinecraftAccess addVertex(double x, double y, double z) {
        Tessellator.instance.addVertex(x, y, z);
        return this;
    }

    @Override
    public MinecraftAccess addVertex(ReadonlyVector3 coords) {
        Tessellator.instance.addVertex(coords.getX(), coords.getY(), coords.getZ());
        return this;
    }

    @Override
    public MinecraftAccess finishDrawing() {
        Tessellator.instance.draw();
        return this;
    }

    @Override
    public MinecraftAccess enableStandardItemLighting() {
        RenderHelper.enableStandardItemLighting();
        return this;
    }

    @Override
    public MinecraftAccess sendChatMessage(String message) {
        final boolean visible = chatWindowExists();
        LSDController.getLog().info("sendChatMessage visible=" + visible + " message=" + message);
        if (visible) {
            minecraft.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(message));
        }
        return this;
    }

    @Override
    public boolean chatWindowExists() {
        return minecraft != null && minecraft.ingameGUI != null && minecraft.ingameGUI.getChatGUI() != null;
    }

    @Override
    public float getPartialTick() {
        return timer == null ? 0.0F : timer.renderPartialTicks;
    }


    @Override
    public MinecraftAccess profilerStartSection(String sectionName) {
        minecraft.mcProfiler.startSection(sectionName);
        return this;
    }

    @Override
    public MinecraftAccess profilerEndSection() {
      	minecraft.mcProfiler.endSection();
        return this;
    }

    @Override
    public MinecraftAccess profilerEndStartSection(String sectionName) {
    	minecraft.mcProfiler.endStartSection(sectionName);
        return this;
    }
}
