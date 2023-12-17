/*
 * Copyright 2016 John Grosh (jagrosh).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.jmusicbot;

import com.github.lalyos.jfiglet.FigletFont;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jmusicbot.entities.Prompt;
import com.jagrosh.jmusicbot.gui.GUI;
import com.jagrosh.jmusicbot.settings.SettingsManager;
import com.jagrosh.jmusicbot.utils.OtherUtil;
import dev.cosgy.agent.GensokyoInfoAgent;
import dev.cosgy.jmusicbot.slashcommands.admin.*;
import dev.cosgy.jmusicbot.slashcommands.dj.*;
import dev.cosgy.jmusicbot.slashcommands.general.*;
import dev.cosgy.jmusicbot.slashcommands.listeners.CommandAudit;
import dev.cosgy.jmusicbot.slashcommands.music.*;
import dev.cosgy.jmusicbot.slashcommands.owner.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author John Grosh (jagrosh)
 */
public class JMusicBot {
    public final static String PLAY_EMOJI = "▶"; // ▶
    public final static String PAUSE_EMOJI = "⏸"; // ⏸
    public final static String STOP_EMOJI = "⏹"; // ⏹
    public final static Permission[] RECOMMENDED_PERMS = {Permission.MESSAGE_SEND, Permission.VIEW_CHANNEL, Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION,
            Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_MANAGE, Permission.MESSAGE_EXT_EMOJI,
            Permission.MANAGE_CHANNEL, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.NICKNAME_CHANGE};
    public final static GatewayIntent[] INTENTS = {GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.MESSAGE_CONTENT}; // , GatewayIntent.MESSAGE_CONTENT
    public static boolean CHECK_UPDATE = true;
    public static boolean COMMAND_AUDIT_ENABLED = false;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // startup log
        Logger log = getLogger("Startup");

        try {
            System.out.println(FigletFont.convertOneLine("Jeff" + OtherUtil.getCurrentVersion()) + "\n" + "by LyAhn/KetaFPV/DubSound");
        } catch (IOException e) {
            System.out.println("Jeff" + OtherUtil.getCurrentVersion() + "\n by LyAhn/KetaFPV/DubSound");
        }


        // create prompt to handle startup
        Prompt prompt = new Prompt("Jeff ", "Switch to nogui mode. You can manually start it in nogui mode by including the -Dnogui=true flag.");

        // check deprecated nogui mode (new way of setting it is -Dnogui=true)
        for (String arg : args)
            if ("-nogui".equalsIgnoreCase(arg)) {
                prompt.alert(Prompt.Level.WARNING, "GUI", "The -nogui flag is deprecated. "
                        + "Use the -Dnogui=true flag before the jar name. Example: java -jar -Dnogui=true JMusicBot.jar");
            } else if ("-nocheckupdates".equalsIgnoreCase(arg)) {
                CHECK_UPDATE = false;
                log.info("Disabled update checking");
            } else if ("-auditcommands".equalsIgnoreCase(arg)) {
                COMMAND_AUDIT_ENABLED = true;
                log.info("Enabled logging of executed commands.");
            }

        // get and check latest version
        String version = OtherUtil.checkVersion(prompt);

        if (!System.getProperty("java.vm.name").contains("64"))
            prompt.alert(Prompt.Level.WARNING, "Java Version", "You are using an unsupported Java version. Please use the 64-bit version of Java.");

        // load config
        BotConfig config = new BotConfig(prompt);
        config.load();

        if (!config.isValid())
            return;


        if (config.getAuditCommands()) {
            COMMAND_AUDIT_ENABLED = true;
            log.info("Enabled logging of executed commands.");
        }

        // set up the listener
        EventWaiter waiter = new EventWaiter();
        SettingsManager settings = new SettingsManager();
        Bot bot = new Bot(waiter, config, settings);
        Bot.INSTANCE = bot;

        AboutCommand aboutCommand = new AboutCommand(Color.BLUE.brighter(),
                "[EvoJeff(v" + version + ")](https://github.com/LyAhn/EvoJeff)",
                new String[]{"High quality music playback", "FairQueue™ Technology", "Easy to host yourself"},
                RECOMMENDED_PERMS);
        aboutCommand.setIsAuthor(false);
        aboutCommand.setReplacementCharacter("\uD83C\uDFB6"); // 🎶

        // set up the command client
        CommandClientBuilder cb = new CommandClientBuilder()
                .setPrefix(config.getPrefix())
                .setAlternativePrefix(config.getAltPrefix())
                .setOwnerId(Long.toString(config.getOwnerId()))
                .setEmojis(config.getSuccess(), config.getWarning(), config.getError())
                .useHelpBuilder(false)
                .setLinkedCacheSize(200)
                .setGuildSettingsManager(settings)
                .setListener(new CommandAudit());

        if (config.isOfficialInvite()) {
            cb.setServerInvite("https://discord.gg/JxkY4BmEA8");
        }

        // Implementation of slash commands
        List<SlashCommand> slashCommandList = new ArrayList<>() {{
            add(new HelpCmd(bot));
            add(aboutCommand);
            if (config.isUseInviteCommand()) {
                add(new InviteCommand());
            }
            add(new PingCommand());
            add(new SettingsCmd(bot));
            //if (config.getCosgyDevHost()) add(new InfoCommand(bot));
            // General
            add(new ServerInfo(bot));
            //add(new UserInfo());
            add(new CashCmd(bot));
            // Music
            add(new LyricsCmd(bot));
            add(new NowplayingCmd(bot));
            add(new PlayCmd(bot));
            add(new SpotifyCmd(bot));
            add(new PlaylistsCmd(bot));
            add(new MylistCmd(bot));
            //add(new QueueCmd(bot));
            add(new QueueCmd(bot));
            add(new RemoveCmd(bot));
            add(new SearchCmd(bot));
            add(new SCSearchCmd(bot));
            add(new NicoSearchCmd(bot));
            add(new ShuffleCmd(bot));
            add(new SkipCmd(bot));
            add(new VolumeCmd(bot));
            // DJ
            add(new ForceRemoveCmd(bot));
            add(new ForceskipCmd(bot));
            add(new NextCmd(bot));
            add(new MoveTrackCmd(bot));
            add(new PauseCmd(bot));
            add(new PlaynextCmd(bot));
            //add(new RepeatCmd(bot));
            add(new RepeatCmd(bot));
            add(new SkipToCmd(bot));
            add(new PlaylistCmd(bot));
            add(new StopCmd(bot));
            //add(new VolumeCmd(bot));
            // Admin
            //add(new ActivateCmd(bot));
            add(new PrefixCmd(bot));
            add(new SetdjCmd(bot));
            add(new SkipratioCmd(bot));
            add(new SettcCmd(bot));
            add(new SetvcCmd(bot));
            add(new AutoplaylistCmd(bot));
            add(new ServerListCmd(bot));
            // Owner
            add(new DebugCmd(bot));
            add(new SetavatarCmd(bot));
            add(new SetgameCmd(bot));
            add(new SetnameCmd(bot));
            add(new SetstatusCmd(bot));
            add(new PublistCmd(bot));
            add(new ShutdownCmd(bot));
            //add(new LeaveCmd(bot));
        }};

        cb.addCommands(slashCommandList.toArray(new Command[0]));
        cb.addSlashCommands(slashCommandList.toArray(new SlashCommand[0]));

        if (config.useEval())
            cb.addCommand(new EvalCmd(bot));
        boolean nogame = false;
        if (config.getStatus() != OnlineStatus.UNKNOWN)
            cb.setStatus(config.getStatus());
        if (config.getGame() == null)
    //      cb.setActivity(Activity.playing(config.getPrefix() + config.getHelp() + " /help for cmds"));
            cb.setActivity(Activity.playing(" /help for cmds"));
        else if (config.getGame().getName().toLowerCase().matches("(none)")) {
            cb.setActivity(null);
            nogame = true;
        } else
            cb.setActivity(config.getGame());
        if (!prompt.isNoGUI()) {
            try {
                GUI gui = new GUI(bot);
                bot.setGUI(gui);
                gui.init();
			} catch (Exception e) {
                log.error("The GUI could not be opened. Possible causes are:\n"
                        + "Running on the server\n"
                        + "Running without a screen\n"
                        + "To hide this error, run in no-GUI mode using the -Dnogui=true flag.");
            }
        }

        log.info(config.getConfigLocation() + " I loaded the settings from");

		// attempt to log in and start
        try {
            JDA jda = JDABuilder.create(config.getToken(), Arrays.asList(INTENTS))
                    .enableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
                    .disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.EMOJI, CacheFlag.ONLINE_STATUS)
                    .setActivity(nogame ? null : Activity.playing("Loading..."))
                    .setStatus(config.getStatus() == OnlineStatus.INVISIBLE || config.getStatus() == OnlineStatus.OFFLINE
                            ? OnlineStatus.INVISIBLE : OnlineStatus.DO_NOT_DISTURB)
                    .addEventListeners(cb.build(), waiter, new Listener(bot))
                    .setBulkDeleteSplittingEnabled(true)
                    .build();
            bot.setJDA(jda);
        } catch (InvalidTokenException ex) {
            prompt.alert(Prompt.Level.ERROR, "JMusicBot", ex + "\n" +
                    "Please make sure you are editing the correct configuration file. Login with Bot token failed." +
                    "Please enter a valid Bot token (not CLIENT SECRET!)\n" +
                    "Configuration file location: " + config.getConfigLocation());
            System.exit(1);
        } catch (IllegalArgumentException ex) {
            prompt.alert(Prompt.Level.ERROR, "JMusicBot", "Some of your settings are invalid:" + ex + "\n" +
                    "Configuration file location: " + config.getConfigLocation());
            System.exit(1);
        }

        new GensokyoInfoAgent().start();
    }
}
