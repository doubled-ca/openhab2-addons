/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.hdtvsupply.internal;

import static org.openhab.binding.hdtvsupply.HDTVSupplyBindingConstants.HDBASE_T_4X4_MATRIX_SWITCHER;

import java.util.Collections;
import java.util.Set;

import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.openhab.binding.hdtvsupply.handler.IpHDTVSupplyHandler;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

/**
 * The {@link HDTVSupplyHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Dennis Drapeau - Initial contribution
 */
@Component(service = ThingHandlerFactory.class, immediate = true, configurationPolicy = ConfigurationPolicy.OPTIONAL, name = "binding.hdtvsupply")
public class HDTVSupplyHandlerFactory extends BaseThingHandlerFactory {

    private static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections
            .singleton(HDBASE_T_4X4_MATRIX_SWITCHER);

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(HDBASE_T_4X4_MATRIX_SWITCHER)) {
            return new IpHDTVSupplyHandler(thing);
        }

        return null;
    }
}
