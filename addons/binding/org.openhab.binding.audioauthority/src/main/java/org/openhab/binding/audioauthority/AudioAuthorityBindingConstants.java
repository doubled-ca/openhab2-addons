/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.audioauthority;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link AudioAuthorityBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Dennis Drapeau - Initial contribution
 */
public class AudioAuthorityBindingConstants {

    public static final String BINDING_ID = "audioauthority";

    // List of all Thing Type UIDs
    // public final static ThingTypeUID THING_TYPE_SAMPLE = new ThingTypeUID(BINDING_ID, "sample");
    public final static ThingTypeUID SONAFLEX_SF16M_IP = new ThingTypeUID(BINDING_ID, "ipSonaflex");

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
    public static final String RESPONSE_START_PATTERN = "[\\(\\[]";

    public static final String RESPONSE_END_PATTERN = "[\\)\\]]";

    public static final String COMMAND_PREFIX = "[";

    public static final String COMMAND_SUFFIX = "]";

    // channel UID to command conversions for Protocol
    // ? on parameter group match allows matching for channel alone or channel and parameter
    // UNITNUM__ will be replaced from thing configuration prior to sending command to matrix
    public static final String POWER_CHANNEL_PATTERN = "output(1[0-6]|[1-9])#zonePower([01])?";
    public static final String POWER_COMMAND_REPLACE = "UNITNUM__O$1P$2";

    public static final String VOLUME_DB_CHANNEL_PATTERN = "output(1[0-6]|[1-9])#zoneVolumeDb(-[1-8]?[0-9]|0)?";
    public static final String VOLUME_DB_COMMAND_REPLACE = "UNITNUM__O$1V$2";

    // using translated volume levels (db) instead of percent as conversion happens prior to creating MatrixCommand
    // needs to support actual number and up/down for the dimmer control
    public static final String VOLUME_DIMMER_CHANNEL_PATTERN = "output(1[0-6]|[1-9])#zoneVolumeDimmer(-[1-8]?[0-9]|0|[UD])?";
    public static final String VOLUME_DIMMER_COMMAND_REPLACE = "UNITNUM__O$1V$2";

    public static final String MUTE_CHANNEL_PATTERN = "output(1[0-6]|[1-9])#zoneMute([012])?";
    public static final String MUTE_COMMAND_REPLACE = "UNITNUM__O$1M$2";

    public static final String OUTPUT_NAME_CHANNEL_PATTERN = "output(1[0-6]|[1-9])#zoneName([a-zA-Z0-9- \\.,/!?\\\\]{0,16})?";
    public static final String OUTPUT_NAME_COMMAND_REPLACE = "UNITNUM__O$1N\"$2\"";

    public static final String INPUT_SOURCE_SWITCH_CHANNEL_PATTERN = "output(1[0-6]|[1-9])#zoneInputSwitch(20|1[0-9]|[1-9])?";
    public static final String INPUT_SOURCE_SWITCH_COMMAND_REPLACE = "UNITNUM__O$1I$2";

    // response to channel UID conversions for Protocol
    public static final String POWER_STATE_RESPONSE_PATTERN = "U([1-4])O(1[0-6]|[1-9])P([01])";
    public static final String POWER_STATE_CHANNELUID_REPLACE = "output$2#zonePower";

    public static final String VOLUME_STATE_RESPONSE_PATTERN = "U([1-4])O(1[0-6]|[1-9])V(-?[0-9]{1,2})";
    public static final String VOLUME_STATE_CHANNELUID_REPLACE = "output$2#zoneVolumeDb";

    public static final String MUTE_STATE_RESPONSE_PATTERN = "U([1-4])O(1[0-6]|[1-9])M([01])";
    public static final String MUTE_STATE_CHANNELUID_REPLACE = "output$2#zoneMute";

    public static final String NAME_STATE_RESPONSE_PATTERN = "U([1-4])O(1[0-6]|[1-9])N\"([a-zA-Z0-9-\\.,/!?\\\\ ]{0,16}+)\"";
    public static final String NAME_STATE_CHANNELUID_REPLACE = "output$2#zoneName";

    public static final String INPUT_SWITCH_STATE_RESPONSE_PATTERN = "U([1-4])O(1[0-6]|[1-9])I(1[0-9]|[1-9]|2[0-8])";
    public static final String INPUT_SWITCH_STATE_CHANNELUID_REPLACE = "output$2#zoneInputSwitch";

    // channel replace pattern so these responses can update multiple channels
    public static final String CHANNEL_VOLUME_DB_PATTERN_TO_REPLACE = "zoneVolumeDb";
    public static final String CHANNEL_VOLUME_DIMMER_REPLACEMENT_TEXT = "zoneVolumeDimmer";

    // Unit global commands
    public static final String UNIT_GLOBAL_MUTE_CHANNEL_PATTERN = "unitMute([0-2])?";
    public static final String UNIT_GLOBAL_MUTE_COMMAND_REPLACE = "UNITNUM__XM$1";

    public static final String UNIT_GLOBAL_VOLUME_CHANNEL_PATTERN = "unitVolume(-[1-8]?[0-9]|0|[UD])?";
    public static final String UNIT_GLOBAL_VOLUME_COMMAND_REPLACE = "UNITNUM__XV$1";
    //
    // public static final String UNIT_GLOBAL_VOLUME_DB_CHANNEL_PATTERN = "unitVolumeDb(-[1-8]?[0-9]|0)?";
    // public static final String UNIT_GLOBAL_VOLUME_DB_COMMAND_REPLACE = "UNITNUM__XV$1";
    //
    // public static final String UNIT_GLOBAL_VOLUME_UP_DOWN_CHANNEL_PATTERN = "unitVolumeUpDown([UD])?";
    // public static final String UNIT_GLOBAL_VOLUME_UP_DOWN_COMMAND_REPLACE = "UNITNUM__XV$1";
}
