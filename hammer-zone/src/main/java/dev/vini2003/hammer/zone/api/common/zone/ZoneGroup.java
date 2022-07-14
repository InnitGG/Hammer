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

package dev.vini2003.hammer.zone.api.common.zone;

import dev.vini2003.hammer.core.api.common.util.NbtUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A {@link ZoneGroup} represents a group of zones.
 */
public class ZoneGroup implements Iterable<Zone> {
	private Collection<Zone> zones = new HashSet<>();
	
	private Identifier id;
	
	public ZoneGroup(Identifier id) {
		this.id = id;
	}
	
	public Collection<Zone> getZones() {
		return zones;
	}
	
	public Identifier getId() {
		return id;
	}
	
	/**
	 * Removes a zone from this group.
	 *
	 * @param zone the zone.
	 */
	public void add(Zone zone) {
		zones.add(zone);
	}
	
	/**
	 * Adds a zone to this group.
	 *
	 * @param zone the zone.
	 */
	public void remove(Zone zone) {
		zones.remove(zone);
	}
	
	@NotNull
	@Override
	public Iterator<Zone> iterator() {
		return zones.iterator();
	}
}
