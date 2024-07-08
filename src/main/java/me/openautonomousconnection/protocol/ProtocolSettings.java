/*
 * Copyright (C) 2024 Open Autonomous Connection - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/Open-Autonomous-Connection
 * See LICENSE-File if exists
 */

package me.openautonomousconnection.protocol;

import me.finn.unlegitlibrary.event.EventManager;
import me.finn.unlegitlibrary.network.system.packets.PacketHandler;
import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;

public class ProtocolSettings extends DefaultMethodsOverrider {

    public String host = "82.197.95.202";
    public int port = 8345;
    public PacketHandler packetHandler = new PacketHandler();
    public EventManager eventManager = new EventManager();

}
