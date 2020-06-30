/**
 * Copyright (c) 2019-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.audioauthority.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link AudioAuthorityBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Dennis Drapeau - Initial contribution
 */
@NonNullByDefault
public class AudioAuthorityBindingConstants {

    private static final String BINDING_ID = "audioauthority";

    // List of all Thing Type UIDs
    public static final ThingTypeUID SONAFLEX_SF16M_IP = new ThingTypeUID(BINDING_ID, "sonaflex_sf16m_ip");

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

    // ? on parameter group match allows matching for channel alone or channel and parameter
    // UNITNUM__ will be replaced from thing configuration prior to sending command to matrix
    public static final String POWER_CHANNEL_PATTERN = "output(1[0-6]|[1-9])#outputPower([01])?";
    public static final String POWER_COMMAND_REPLACE = "UNITNUM__O$1P$2";

    public static final String VOLUME_DB_CHANNEL_PATTERN = "output(1[0-6]|[1-9])#outputVolumeDb(-[1-8]?[0-9]|0)?";
    public static final String VOLUME_DB_COMMAND_REPLACE = "UNITNUM__O$1V$2";

    // using translated volume levels (db) instead of percent as conversion happens prior to creating MatrixCommand
    // needs to support actual number and up/down for the dimmer control
    public static final String VOLUME_DIMMER_CHANNEL_PATTERN = "output(1[0-6]|[1-9])#outputVolumeDimmer(-[1-8]?[0-9]|0|[UD])?";
    public static final String VOLUME_DIMMER_COMMAND_REPLACE = "UNITNUM__O$1V$2";

    public static final String MUTE_CHANNEL_PATTERN = "output(1[0-6]|[1-9])#outputMute([012])?";
    public static final String MUTE_COMMAND_REPLACE = "UNITNUM__O$1M$2";

    public static final String OUTPUT_NAME_CHANNEL_PATTERN = "output(1[0-6]|[1-9])#outputName([a-zA-Z0-9- \\.,/!?\\\\]{0,16})?";
    public static final String OUTPUT_NAME_COMMAND_REPLACE = "UNITNUM__O$1N\"$2\"";

    public static final String INPUT_SOURCE_SWITCH_CHANNEL_PATTERN = "output(1[0-6]|[1-9])#outputInputSwitch(20|1[0-9]|[1-9])?";
    public static final String INPUT_SOURCE_SWITCH_COMMAND_REPLACE = "UNITNUM__O$1I$2";

    // response to channel UID conversions for Protocol
    public static final String POWER_STATE_RESPONSE_PATTERN = "U([1-4])O(1[0-6]|[1-9])P([01])";
    public static final String POWER_STATE_CHANNELUID_REPLACE = "output$2#outputPower";

    public static final String VOLUME_STATE_RESPONSE_PATTERN = "U([1-4])O(1[0-6]|[1-9])V(-?[0-9]{1,2})";
    public static final String VOLUME_STATE_CHANNELUID_REPLACE = "output$2#outputVolumeDb";

    public static final String MUTE_STATE_RESPONSE_PATTERN = "U([1-4])O(1[0-6]|[1-9])M([01])";
    public static final String MUTE_STATE_CHANNELUID_REPLACE = "output$2#outputMute";

    public static final String NAME_STATE_RESPONSE_PATTERN = "U([1-4])O(1[0-6]|[1-9])N\"([a-zA-Z0-9-\\.,/!?\\\\ ]{0,16}+)\"";
    public static final String NAME_STATE_CHANNELUID_REPLACE = "output$2#outputName";

    public static final String INPUT_SWITCH_STATE_RESPONSE_PATTERN = "U([1-4])O(1[0-6]|[1-9])I(1[0-9]|[1-9]|2[0-8])";
    public static final String INPUT_SWITCH_STATE_CHANNELUID_REPLACE = "output$2#outputInputSwitch";

    // channel replace pattern so these responses can update multiple channels
    public static final String CHANNEL_VOLUME_DB_PATTERN_TO_REPLACE = "outputVolumeDb";
    public static final String CHANNEL_VOLUME_DIMMER_REPLACEMENT_TEXT = "outputVolumeDimmer";

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
