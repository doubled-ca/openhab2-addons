/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.hdtvsupply;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link hdtvsupplyBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Dennis Drapeau - Initial contribution
 */
public class HDTVSupplyBindingConstants {

    private static final String BINDING_ID = "hdtvsupply";

    // List of all Thing Type UIDs
    public static final ThingTypeUID HDBASE_T_4X4_MATRIX_SWITCHER = new ThingTypeUID(BINDING_ID, "HDMI944H100");

    // List of all Channel ids
    public static final String OUTPUT_NAME_PATTERN = "output([1-4])#name(\\w+)?";
    // public static final String OUTPUT_NAME_REPLACE = ""

    public static final String OUTPUT_PREFERRED_EDID_PATTERN = "output([1-4])#preferredEDID(1[0-5]|[1-9])?";
    // public static final String OUTPUT_PREFERRED_EDID_REPLACE = "";

    public static final String OUTPUT_SELECTED_INPUT_PATTERN = "output([1-4])#selectedInput([1-4])?";
    public static final String OUTPUT_SELECTED_INPUT_REPLACE = "A55B0203$200$10000000000";

    public static final String QUERY_OUTPUT_CURRENT_INPUT_REPLACE_PATTERN = "A55B0201$100000000000000";

    public static final String INPUT_NAME_PATTERN = "input([1-4])#name(\\w+)?";
    // public static final String = "";

    public static final String INPUT_SET_EDID_PATTERN = "input([1-4])#EDID(1[0-5]|[1-9])?";
    public static final String INPUT_SET_EDID_REPLACE = "A55B0302$200$10000000000";

    public static final String COMMAND_PREFIX = null;

    public static final String COMMAND_SUFFIX = null;
}
