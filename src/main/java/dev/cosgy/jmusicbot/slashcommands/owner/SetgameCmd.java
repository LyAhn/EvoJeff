/*
 * Copyright 2018 John Grosh (jagrosh).
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
package dev.cosgy.jmusicbot.slashcommands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jmusicbot.Bot;
import dev.cosgy.jmusicbot.slashcommands.OwnerCommand;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SetgameCmd extends OwnerCommand {
    public SetgameCmd(Bot bot) {
        this.name = "setgame";
        this.help = "Sets the game the bot is playing";
        this.arguments = "[action] [game]";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = false;
        this.children = new OwnerCommand[]{
                new PlayingCmd(),
                new SetlistenCmd(),
                new SetstreamCmd(),
                new SetwatchCmd(),
                new SetCompetingCmd(),
                new NoneCmd()
        };
    }

    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {
    }

    @Override
    protected void execute(CommandEvent event) {
        String title = event.getArgs().toLowerCase().startsWith("playing") ? event.getArgs().substring(7).trim() : event.getArgs();
        try {
            event.getJDA().getPresence().setActivity(title.isEmpty() ? null : Activity.playing(title));
            event.reply(event.getClient().getSuccess() + " **" + event.getSelfUser().getName()
                    + "** is " + (title.isEmpty() ? "There is nothing left." : "Currently playing `" + title + "`."));
        } catch (Exception e) {
            event.reply(event.getClient().getError() + "Could not set status.");
        }
    }

    private class NoneCmd extends OwnerCommand {
        private NoneCmd() {
            this.name = "none";
            this.aliases = new String[]{"none"};
            this.help = "Reset status.";
            this.guildOnly = false;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            event.getJDA().getPresence().setActivity(null);
            event.reply("Status has been reset.").queue();
        }

        @Override
        protected void execute(CommandEvent event) {
            event.getJDA().getPresence().setActivity(null);
            event.reply("Status has been reset.");
        }
    }

    private class PlayingCmd extends OwnerCommand {
        private PlayingCmd() {
            this.name = "playing";
            this.aliases = new String[]{"twitch", "streaming"};
            this.help = "Sets the game the bot is playing.";
            this.arguments = "<title>";
            this.guildOnly = false;
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "title", "Game Title", true));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            String title = event.getOption("title").getAsString();
            try {
                event.getJDA().getPresence().setActivity(Activity.playing(title));
                event.reply(event.getClient().getSuccess() + " **" + event.getJDA().getSelfUser().getName()
                        + "** is " + "Currently playing `" + title + "`.");
            } catch (Exception e) {
                event.reply(event.getClient().getError() + "Could not set status.").queue();
            }
        }

        @Override
        protected void execute(CommandEvent event) {
        }
    }

    private class SetstreamCmd extends OwnerCommand {
        private SetstreamCmd() {
            this.name = "stream";
            this.aliases = new String[]{"twitch", "streaming"};
            this.help = "Set the game the bot is playing to the stream.";
            this.arguments = "<username> <game>";
            this.guildOnly = false;
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "user", "username", true));
            options.add(new OptionData(OptionType.STRING, "game", "game title", true));
            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            try {
                event.getJDA().getPresence().setActivity(Activity.streaming(event.getOption("game").getAsString(), "https://twitch.tv/" + event.getOption("user").getAsString ()));
                event.reply(event.getClient().getSuccess() + "**" + event.getJDA().getSelfUser().getName()
                        + "** is currently streaming `" + event.getOption("game").getAsString() + "`).").queue();
            } catch (Exception e) {
                event.reply(event.getClient().getError() + "Failed to set game.").queue();
            }
        }

        @Override
        protected void execute(CommandEvent event) {
            String[] parts = event.getArgs().split("\\s+", 2);
            if (parts.length < 2) {
                event.replyError("Please enter your username and the name of the 'game to stream'");
                return;
            }
            try {
                event.getJDA().getPresence().setActivity(Activity.streaming(parts[1], "https://twitch.tv/" + parts[0]));
                event.replySuccess("**" + event.getSelfUser().getName()
                        + "** is currently streaming `" + parts[1] + "`.");
            } catch (Exception e) {
                event.reply(event.getClient().getError() + "Failed to configure game.");
            }
        }
    }

    private class SetlistenCmd extends OwnerCommand {
        private SetlistenCmd() {
            this.name = "listen";
            this.aliases = new String[]{"listening"};
            this.help = "Set the game the bot is listening to";
            this.arguments = "<title>";
            this.guildOnly = false;
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "title", "title", true));
            this.options = options;
        }

        @Override
         protected void execute(SlashCommandEvent event) {
             String title = event.getOption("title").getAsString();
             try {
                 event.getJDA().getPresence().setActivity(Activity.listening(title));
                 event.reply(event.getClient().getSuccess() + "**" + event.getJDA().getSelfUser().getName() + "** is currently listening to `" + title + "` .").queue();
             } catch (Exception e) {
                 event.reply(event.getClient().getError() + "Failed to set game.").queue();
             }
         }

         @Override
         protected void execute(CommandEvent event) {
             if (event.getArgs().isEmpty()) {
                 event.replyError("Please include the title you are listening to!");
                 return;
             }
             String title = event.getArgs().toLowerCase().startsWith("to") ? event.getArgs().substring(2).trim() : event.getArgs();
             try {
                 event.getJDA().getPresence().setActivity(Activity.listening(title));
                 event.replySuccess("**" + event.getSelfUser().getName() + "** is currently listening to `" + title + "`.");
             } catch (Exception e) {
                 event.reply(event.getClient().getError() + "Failed to configure game.");
             }
         }
     }

     private class SetwatchCmd extends OwnerCommand {
         private SetwatchCmd() {
             this.name = "watch";
             this.aliases = new String[]{"watching"};
             this.help = "Set the game the bot is watching";
             this.arguments = "<title>";
             this.guildOnly = false;
             List<OptionData> options = new ArrayList<>();
             options.add(new OptionData(OptionType.STRING, "title", "title", true));
             this.options = options;
         }

         @Override
         protected void execute(SlashCommandEvent event) {
             String title = event.getOption("title").getAsString();
             try {
                 event.getJDA().getPresence().setActivity(Activity.watching(title));
                 event.reply(event.getClient().getSuccess() + "**" + event.getJDA().getSelfUser().getName() + "** is currently looking at `" + title + "` .").queue();
             } catch (Exception e) {
                 event.reply(event.getClient().getError() + "Failed to set game.").queue();
             }
         }

         @Override
         protected void execute(CommandEvent event) {
             if (event.getArgs().isEmpty()) {
                 event.replyError("Please enter the title you are viewing.");
                 return;
             }
             String title = event.getArgs();
             try {
                 event.getJDA().getPresence().setActivity(Activity.watching(title));
                 event.replySuccess("**" + event.getSelfUser().getName() + "** is currently looking at `" + title + "`.");
             } catch (Exception e) {
                 event.reply(event.getClient().getError() + "Failed to configure game.");
             }
         }
     }

     private class SetCompetingCmd extends OwnerCommand {
         private SetCompetingCmd() {
             this.name = "competing";
             this.help = "Set the game the bot is participating in";
             this.arguments = "<title>";
             this.guildOnly = false;
             List<OptionData> options = new ArrayList<>();
             options.add(new OptionData(OptionType.STRING, "title", "game title", true));
             this.options = options;
         }

         @Override
         protected void execute(SlashCommandEvent event) {
             String title = event.getOption("title").getAsString();
             try {
                 event.getJDA().getPresence().setActivity(Activity.competing(title));
                 event.reply(event.getClient().getSuccess() + "**" + event.getJDA().getSelfUser().getName() + "** is currently competing for `" + title + "` .").queue();
             } catch (Exception e) {
                 event.reply(event.getClient().getError() + "Failed to set game.").queue();
             }
         }

         @Override
         protected void execute(CommandEvent event) {
             if (event.getArgs().isEmpty()) {
                 event.replyError("Please enter the title you are participating in.");
                 return;
             }
             String title = event.getArgs();
             try {
                 event.getJDA().getPresence().setActivity(Activity.watching(title));
                 event.replySuccess("**" + event.getSelfUser().getName() + "** is currently participating in `" + title + "`.");
             } catch (Exception e) {
                 event.reply(event.getClient().getError() + "Failed to configure game.");
             }
         }
     }
}