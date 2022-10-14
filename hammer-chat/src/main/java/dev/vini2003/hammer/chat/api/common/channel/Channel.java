/*
 * MIT License
 *
 * Copyright (c) 2020 - 2022 vini2003
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.vini2003.hammer.chat.api.common.channel;

import dev.vini2003.hammer.chat.api.common.event.ChannelEvents;
import dev.vini2003.hammer.permission.api.common.role.Role;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class Channel {
	private final String name;
	
	@Nullable
	private final Role role;
	
	private final Collection<UUID> holders = new ArrayList<>();
	
	public Channel(String name) {
		this.name = name;
		this.role = null;
	}
	
	public Channel(String name, @Nullable Role role) {
		this.name = name;
		this.role = role;
	}
	
	public boolean isIn(PlayerEntity player) {
		return holders.contains(player.getUuid());
	}
	
	public void addTo(PlayerEntity player) {
		holders.add(player.getUuid());
		
		ChannelEvents.ADD.invoker().onAdd(player, this);
	}
	
	public void removeFrom(PlayerEntity player) {
		holders.remove(player.getUuid());
		
		ChannelEvents.REMOVE.invoker().onRemove(player, this);
	}
	
	public String getName() {
		return name;
	}
	
	@Nullable
	public Role getRole() {
		return role;
	}
	
	public Collection<UUID> getHolders() {
		return holders;
	}
}
