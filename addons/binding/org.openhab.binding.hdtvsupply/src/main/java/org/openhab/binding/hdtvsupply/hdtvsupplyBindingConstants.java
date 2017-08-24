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
public class hdtvsupplyBindingConstants {

    private static final String BINDING_ID = "hdtvsupply";

    // List of all Thing Type UIDs
    public static final ThingTypeUID HDBASE_T_4X4_MATRIX_SWITCHER = new ThingTypeUID(BINDING_ID, "HDMI944H100");

    // List of all Channel ids
    public static final String OUTPUT = "channel1";

}
