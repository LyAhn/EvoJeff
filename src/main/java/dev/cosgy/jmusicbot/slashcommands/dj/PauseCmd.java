/*
 * Copyright 2018 John Grosh <john.a.grosh@gmail.com>.
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
package dev.cosgy.jmusicbot.slashcommands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.PlayStatus;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import dev.cosgy.jmusicbot.slashcommands.DJCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
  * @author John Grosh <john.a.grosh@gmail.com>
  */
public class PauseCmd extends DJCommand {
     Logger log = LoggerFactory.getLogger("Pause");

     public PauseCmd(Bot bot) {
         super(bot);
         this.name = "pause";
         this.help = "Pause the current song";
         this.aliases = bot.getConfig().getAliases(this.name);
         this.bePlaying = true;
     }

     @Override
     public void doCommand(CommandEvent event) {
         AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
         if (handler.getPlayer().isPaused()) {
             event.replyWarning("The song is already paused. `" + event.getClient().getPrefix() + " You can unpause it using play`.");
             return;
         }
         handler.getPlayer().setPaused(true);
         log.info(event.getGuild().getName() + "paused" + handler.getPlayer().getPlayingTrack().getInfo().title + "at");
         event.replySuccess("**" + handler.getPlayer().getPlayingTrack().getInfo().title + "** has been paused. `" + event.getClient().getPrefix() + " play ` can be used to unpause.");

         Bot.updatePlayStatus(event.getGuild(), event.getGuild().getSelfMember(), PlayStatus.PAUSED);
     }

     @Override
     public void doCommand(SlashCommandEvent event) {
         if (!checkDJPermission(event.getClient(), event)) {
             event.reply(event.getClient().getWarning() + "Cannot execute because you do not have permission.").queue();
             return;
         }
         AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
         if (handler.getPlayer().isPaused()) {
             event.reply(event.getClient().getWarning() + "The song is already paused. `" + event.getClient().getPrefix() + " Use play` to unpause. ").queue();
             return;
         }
         handler.getPlayer().setPaused(true);
         log.info(event.getGuild().getName() + "paused" + handler.getPlayer().getPlayingTrack().getInfo().title + "at");
         event.reply(event.getClient().getSuccess() + "**" + handler.getPlayer().getPlayingTrack().getInfo().title + "** has been paused. `" + event. getClient().getPrefix() + " You can unpause using play`).queue();

         Bot.updatePlayStatus(event.getGuild(), event.getGuild().getSelfMember(), PlayStatus.PAUSED);
     }
}