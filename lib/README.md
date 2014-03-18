**Fair warning:** The Maven POM is unmaintained since Aesen believes Maven is
demonspawn. If you feel you're up to the task of fixing it to work with
ForgeGradle, then by all means make a pull request.

If you're attempting to build LibShapeDraw from source you will need to place
several jars in this directory:

 +  Copy the contents of your Minecraft's `bin` directory to this directory,
    including the LWJGL libraries and natives. `minecraft.jar` should be
    vanilla.

 +  Copy the Forge universal binary to this directory. Rename the archive to
    `minecraftforge-universal.jar`.

 +  Create `minecraft-deobf.jar` using MCP.

See `../README-contributing.md` for more info.
