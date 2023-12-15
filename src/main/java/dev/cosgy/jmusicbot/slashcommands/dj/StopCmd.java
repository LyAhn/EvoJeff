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
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.jagrosh.jmusicbot.queue.FairQueue;
import dev.cosgy.jmusicbot.playlist.CacheLoader;
import dev.cosgy.jmusicbot.slashcommands.DJCommand;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
  * @author John Grosh <john.a.grosh@gmail.com>
  */
public class StopCmd extends DJCommand {
     Logger log = LoggerFactory.getLogger("Stop");

     public StopCmd(Bot bot) {
         super(bot);
         this.name = "stop";
         this.help = "Stops the current song and removes the queue.";
         this.aliases = bot.getConfig().getAliases(this.name);
         this.bePlaying = false;

         List<OptionData> options = new ArrayList<>();
         options.add(new OptionData(OptionType.STRING, "option", "Enter `save` to save the playlist", false));

         this.options = options;
     }

     @Override
     public void doCommand(CommandEvent event) {
         AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
         CacheLoader cache = bot.getCacheLoader();
         FairQueue<QueuedTrack> queue = handler.getQueue();

         if (queue.size() > 0 && event.getArgs().matches("save")) {
             cache.Save(event.getGuild().getId(), handler.getQueue());
             event.reply(event.getClient().getSuccess() + "Waiting to play" + queue.size() + "Song saved and stopped playing.");
             log.info(event.getGuild().getName() + "Save playback queue and disconnect from voice channel.");
         } else {
             event.reply(event.getClient().getSuccess() + "Playback queue has been deleted and playback has stopped.");
         }
         handler.stopAndClear();
         event.getGuild().getAudioManager().closeAudioConnection();
     }

     @Override
     public void doCommand(SlashCommandEvent event) {
         if (!checkDJPermission(event.getClient(), event)) {
             event.reply(event.getClient().getWarning() + "Cannot execute because you do not have permission.").queue();
             return;
         }
         AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
         CacheLoader cache = bot.getCacheLoader();
         FairQueue<QueuedTrack> queue = handler.getQueue();

         log.debug("Size waiting for playback:" + queue.size());

         if (event.getOption("option") == null) {
             event.reply(event.getClient().getSuccess() + "Playback queue has been deleted and playback has stopped.").queue();
             log.info(event.getGuild().getName() + "Deleted the playback queue and disconnected from the voice channel.");
             handler.stopAndClear();
             event.getGuild().getAudioManager().closeAudioConnection();
             return;
         }

         if (queue.size() > 0 && event.getOption("option").getAsString().equals("save")) {
             cache.Save(event.getGuild().getId(), handler.getQueue());
             event.reply(event.getClient().getSuccess() + "Waiting to play" + queue.size() + "Song saved and stopped playing.").queue();
             log.info(event.getGuild().getName() + "Save playback queue and disconnect from voice channel.");
         } else {
             event.reply(event.getClient().getSuccess() + "Playback queue has been deleted and playback has stopped.").queue();
             log.info(event.getGuild().getName() + "Deleted the playback queue and disconnected from the voice channel.");
         }
         handler.stopAndClear();
         event.getGuild().getAudioManager().closeAudioConnection();
     }

     @Override
     public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
         String[] cmdOptions = {"save"};
         if (event.getName().equals("stop") && event.getFocusedOption().getName().equals("option")) {
             List<Command.Choice> options = Stream.of(cmdOptions)
                     .filter(word -> word.startsWith(event.getFocusedOption().getValue())) // only display words that start with the user's current input
                     .map(word -> new Command.Choice(word, word)) // map the words to choices
                     .collect(Collectors.toList());
             event.replyChoices(options).queue();
         }
     }
}