/**
 * Copyright (c) 2014,2018 by the respective copyright holders.
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.audioauthoritysonaflex.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link AudioAuthoritySonaflexBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Dennis Drapeau - Initial contribution
 */
@NonNullByDefault
public class AudioAuthoritySonaflexBindingConstants {

    private static final String BINDING_ID = "audioauthoritysonaflex";

    // List of all Thing Type UIDs
    public static final ThingTypeUID SONAFLEX_SF16M_IP = new ThingTypeUID(BINDING_ID, "ipSonaflex");

    public static final int DEFAULT_UNIT_NUMBER = 1;

    // List of thing parameters names
    public final static String PROTOCOL_PARAMETER = "protocol";
    public final static String HOST_PARAMETER = "address";
    public final static String TCP_PORT_PARAMETER = "tcpPort";
    public final static String SERIAL_PORT_PARAMETER = "serialPort";
    public static final Object UNIT_NUMBER_PARAMETER = "unit";
    public static final Object ACTIVE_ZONES = "activeZones";

    public final static String IP_PROTOCOL_NAME = "IP";
    public final static String SERIAL_PROTOCOL_NAME = "serial";

    // List of all Channel ids
    public static final String CHANNEL_1 = "channel1";
}
