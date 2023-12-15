/*
 * Copyright 2019 John Grosh <john.a.grosh@gmail.com>.
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
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.jagrosh.jdautilities.menu.OrderedMenu;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import dev.cosgy.jmusicbot.slashcommands.DJCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Michaili K.
 */
public class ForceRemoveCmd extends DJCommand {
     public ForceRemoveCmd(Bot bot) {
         super(bot);
         this.name = "forceremove";
         this.help = "Removes the specified user's entry from the queue";
         this.arguments = "<user>";
         this.aliases = bot.getConfig().getAliases(this.name);
         this.beListening = false;
         this.bePlaying = true;
         this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};

         List<OptionData> options = new ArrayList<>();
         options.add(new OptionData(OptionType.USER, "user", "user", true));
         this.options = options;

     }

     @Override
     public void doCommand(CommandEvent event) {
         if (event.getArgs().isEmpty()) {
             event.replyError("User must be mentioned!");
             return;
         }

         AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
         if (handler.getQueue().isEmpty()) {
             event.replyError("There is nothing waiting to be played!");
             return;
         }


         User target;
         List<Member> found = FinderUtil.findMembers(event.getArgs(), event.getGuild());

         if (found.isEmpty()) {
             event.replyError("User not found!");
             return;
         } else if (found.size() > 1) {
             OrderedMenu.Builder builder = new OrderedMenu.Builder();
             for (int i = 0; i < found.size() && i < 4; i++) {
                 Member member = found.get(i);
                 builder.addChoice("**" + member.getUser().getName() + "**#" + member.getUser().getDiscriminator());
             }

             builder
                     .setSelection((msg, i) -> removeAllEntries(found.get(i - 1).getUser(), event))
                     .setText("Multiple users found:")
                     .setColor(event.getSelfMember().getColor())
                     .useNumbers()
                     .setUsers(event.getAuthor())
                     .useCancelButton(true)
                     .setCancel((msg) -> {
                     })
                     .setEventWaiter(bot.getWaiter())
                     .setTimeout(1, TimeUnit.MINUTES)

                     .build().display(event.getChannel());

             return;
         } else {
             target = found.get(0).getUser();
         }

         removeAllEntries(target, event);

     }

     @Override
     public void doCommand(SlashCommandEvent event) {
         if (!checkDJPermission(event.getClient(), event)) {
             event.reply(event.getClient().getWarning() + "Cannot execute because you do not have permission.").queue();
             return;
         }
         AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
         if (handler.getQueue().isEmpty()) {
             event.reply(event.getClient().getError() + "Nothing waiting to be played!").queue();
             return;
         }

         User target = event.getOption("user").getAsUser();
         int count = ((AudioHandler) event.getGuild().getAudioManager().getSendingHandler()).getQueue().removeAll(target.getIdLong());
         if (count == 0) {
             event.reply(event.getClient().getWarning() + "**" + target.getName() + "** There are no songs waiting to play!").queue();
         } else {
             event.reply(event.getClient().getSuccess() + "**" + target.getName() + "**#" + target.getDiscriminator() + "`" + count + "` song removed from .").queue();
         }
     }

     private void removeAllEntries(User target, CommandEvent event) {
         int count = ((AudioHandler) event.getGuild().getAudioManager().getSendingHandler()).getQueue().removeAll(target.getIdLong());
         if (count == 0) {
             event.replyWarning("**" + target.getName() + "** There are no songs waiting to play!");
         } else {
             event.replySuccess("**" + target.getName() + "**#" + target.getDiscriminator() + "`" + count + "`Song deleted.");
         }
     }
}