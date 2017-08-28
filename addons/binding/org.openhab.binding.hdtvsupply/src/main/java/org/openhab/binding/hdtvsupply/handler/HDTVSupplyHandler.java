/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.hdtvsupply.handler;

import java.util.concurrent.ScheduledFuture;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.hdtvsupply.HDTVSupplyBindingConstants;
import org.openhab.binding.hdtvsupply.connection.HDTVSupplyConnection;
import org.openhab.binding.hdtvsupply.connection.event.HDTVSupplyDisconnectionEvent;
import org.openhab.binding.hdtvsupply.connection.event.HDTVSupplyDisconnectionListener;
import org.openhab.binding.hdtvsupply.connection.event.HDTVSupplyStatusUpdateEvent;
import org.openhab.binding.hdtvsupply.connection.event.HDTVSupplyUpdateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link HDTVSupplyHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Dennis Drapeau - Initial contribution
 */
public abstract class HDTVSupplyHandler extends BaseThingHandler
        implements HDTVSupplyDisconnectionListener, HDTVSupplyUpdateListener {

    private final Logger logger = LoggerFactory.getLogger(HDTVSupplyHandler.class);

    private HDTVSupplyConnection connection;
    private ScheduledFuture<?> statusCheckerFuture;

    // private int activeZones;
    // private int currentZone = 2;

    protected abstract HDTVSupplyConnection createConnection();

    public HDTVSupplyHandler(@NonNull Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        try {
            boolean commandSent = false;
            boolean unknownCommand = false;

            if (channelUID.getId().matches(HDTVSupplyBindingConstants.OUTPUT_SELECTED_INPUT_PATTERN)) {
                commandSent = connection.sendOutputSwitchInputCommand(command, channelUID);
            } else if (channelUID.getId().matches(HDTVSupplyBindingConstants.INPUT_SET_EDID_PATTERN)) {
                commandSent = connection.sendInputSetEDID(command, channelUID);
            } else {
                unknownCommand = true;
            }

            // If the command is not unknown and has not been sent, the AVR is Offline
            if (!commandSent && !unknownCommand) {
                onDisconnection();
            }

        } catch (Exception e) {
            logger.warn("Unsupported command type received for channel {}.", channelUID.getId());
        }

        // if (channelUID.getId().equals(hdtvsupplyBindingConstants)) {
        // TODO: handle command

        // Note: if communication with thing fails for some reason,
        // indicate that by setting the status with detail information
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
        // "Could not control device at IP address x.x.x.x");
        // }
    }

    @Override
    public void initialize() {
        // TODO: Initialize the thing. If done set status to ONLINE to indicate proper working.
        // Long running initialization should be done asynchronously in background.
        updateStatus(ThingStatus.ONLINE);

        // Note: When initialization can NOT be done set the status with more details for further
        // analysis. See also class ThingStatusDetail for all available status details.
        // Add a description to give user information to understand why thing does not work
        // as expected. E.g.
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
        // "Can not access device as username and/or password are invalid");
    }

    @Override
    public void onDisconnection(HDTVSupplyDisconnectionEvent event) {
        onDisconnection();
    }

    /**
     * Process the HDTVSupply disconnection.
     */
    private void onDisconnection() {
        updateStatus(ThingStatus.OFFLINE);
    }

    @Override
    public void statusUpdateReceived(HDTVSupplyStatusUpdateEvent statusUpdateEvent) {
        // TODO Auto-generated method stub

    }

}
