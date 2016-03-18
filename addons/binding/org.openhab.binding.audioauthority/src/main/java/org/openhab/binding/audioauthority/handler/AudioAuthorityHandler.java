/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.audioauthority.handler;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.audioauthority.AudioAuthorityBindingConstants;
import org.openhab.binding.audioauthority.connection.MatrixConnection;
import org.openhab.binding.audioauthority.connection.event.MatrixDisconnectionEvent;
import org.openhab.binding.audioauthority.connection.event.MatrixDisconnectionListener;
import org.openhab.binding.audioauthority.connection.event.MatrixStatusUpdateEvent;
import org.openhab.binding.audioauthority.connection.event.MatrixUpdateListener;
import org.openhab.binding.audioauthority.internal.protocol.MatrixResponseImpl;
import org.openhab.binding.audioauthority.internal.protocol.RequestResponseFactory;
import org.openhab.binding.audioauthority.internal.protocol.VolumeConverter;
import org.openhab.binding.audioauthority.protocol.MatrixCommandResponseException;
import org.openhab.binding.audioauthority.protocol.MatrixResponse;
import org.openhab.binding.audioauthority.protocol.states.MuteStateValues;
import org.openhab.binding.audioauthority.protocol.states.PowerStateValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link AudioAuthorityHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Dennis Drapeau - Initial contribution
 */
public abstract class AudioAuthorityHandler extends BaseThingHandler
        implements MatrixUpdateListener, MatrixDisconnectionListener {

    private Logger logger = LoggerFactory.getLogger(AudioAuthorityHandler.class);

    private MatrixConnection connection;
    private ScheduledFuture<?> statusCheckerFuture;

    private int activeZones;
    private int currentZone = 2;

    protected abstract MatrixConnection createConnection();

    public AudioAuthorityHandler(Thing thing) {
        super(thing);
        this.connection = createConnection();
        this.activeZones = ((Number) this.getConfig().get(AudioAuthorityBindingConstants.ACTIVE_ZONES)).intValue();
        this.connection.addUpdateListener(this);
        this.connection.addDisconnectionListener(this);

    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // TODO: handle command

        // Note: if communication with thing fails for some reason,
        // indicate that by setting the status with detail information
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
        // "Could not control device at IP address x.x.x.x");

        try {
            boolean commandSent = false;
            boolean unknownCommand = false;

            if (channelUID.getId().matches(AudioAuthorityBindingConstants.MUTE_CHANNEL_PATTERN)) {
                commandSent = connection.sendMuteCommand(command, channelUID);
            } else if (channelUID.getId().matches(AudioAuthorityBindingConstants.POWER_CHANNEL_PATTERN)) {
                commandSent = connection.sendPowerCommand(command, channelUID);
            } else if (channelUID.getId().matches(AudioAuthorityBindingConstants.VOLUME_DB_CHANNEL_PATTERN)
                    || channelUID.getId().matches(AudioAuthorityBindingConstants.VOLUME_DIMMER_CHANNEL_PATTERN)) {
                commandSent = connection.sendVolumeCommand(command, channelUID);
            } else if (channelUID.getId().matches(AudioAuthorityBindingConstants.INPUT_SOURCE_SWITCH_CHANNEL_PATTERN)) {
                commandSent = connection.sendInputSourceCommand(command, channelUID);
            } else if (channelUID.getId().matches(AudioAuthorityBindingConstants.OUTPUT_NAME_CHANNEL_PATTERN)) {
                commandSent = connection.sendNameCommand(command, channelUID);
            } else {
                unknownCommand = true;
            }

            // If the command is not unknown and has not been sent, the AVR is Offline
            if (!commandSent && !unknownCommand) {
                onDisconnection();
            }

        } catch (MatrixCommandResponseException e) {
            logger.warn("Unsupported command type received for channel {}.", channelUID.getId());
        }

    }

    @Override
    public void initialize() {
        // TODO: Initialize the thing. If done set status to ONLINE to indicate proper working.
        // Long running initialization should be done asynchronously in background.
        updateStatus(ThingStatus.ONLINE);

        logger.debug("Initializing handler for AudioAuthority Matrix @{}", connection.getConnectionName());
        super.initialize();

        for (int i = 0; i < activeZones; i++) {
            connection.sendZoneQuery(connection.getUnitNumber(), i + 1);
        }

        // Start the status checker
        Runnable statusChecker = new Runnable() {
            @Override
            public void run() {
                try {
                    logger.debug("Checking status of Matrix @{}", connection.getConnectionName());
                    checkStatus();
                } catch (LinkageError e) {
                    logger.warn(
                            "Failed to check the status for Matrix @{}. If a Serial link is used to connect to the AVR, please check that the Bundle org.openhab.io.transport.serial is available. Cause: {}",
                            connection.getConnectionName(), e.getMessage());

                    // try to reconnect Socket
                    connection.reConnect();

                    // Stop to check the status of this AVR.

                    if (statusCheckerFuture != null) {
                        statusCheckerFuture.cancel(false);
                    }
                }
            }
        };
        statusCheckerFuture = scheduler.scheduleWithFixedDelay(statusChecker, 1, 5, TimeUnit.MINUTES);

        // Note: When initialization can NOT be done set the status with more details for further
        // analysis. See also class ThingStatusDetail for all available status details.
        // Add a description to give user information to understand why thing does not work
        // as expected. E.g.
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
        // "Can not access device as username and/or password are invalid");
    }

    /**
     * Close the connection and stop the status checker.
     */
    @Override
    public void dispose() {
        super.dispose();
        if (statusCheckerFuture != null) {
            statusCheckerFuture.cancel(true);
        }
        if (connection != null) {
            connection.close();
        }
    }

    /**
     * Check the status of the Matrix. Return true if the Matrix is online, else return false.
     *
     * @return
     */
    private void checkStatus() {
        // If the power query request has not been sent, the connection to the
        // AVR has failed. So update its status to OFFLINE.
        if (!connection.sendZoneQuery(1, currentZone)) {
            updateStatus(ThingStatus.OFFLINE);
        } else {
            // IF the power query has succeeded, the AVR status is ONLINE.
            updateStatus(ThingStatus.ONLINE);
            currentZone = currentZone == activeZones ? 1 : currentZone + 1;
        }
    }

    @Override
    public void onDisconnection(MatrixDisconnectionEvent event) {
        onDisconnection();
    }

    /**
     * Process the Matrix disconnection.
     */
    private void onDisconnection() {
        updateStatus(ThingStatus.OFFLINE);
    }

    /**
     * Called when a Power ON state update is received from the AVR.
     */
    public void onPowerOn() {
        // When the AVR is Powered ON, query the volume, the mute state and the source input
        // connection.sendVolumeQuery();
        // connection.sendMuteQuery();
        // connection.sendSourceInputQuery();
    }

    /**
     * Called when a Power OFF state update is received from the AVR.
     */
    public void onPowerOff() {
        // When the Matrix is Powered OFF, update the status of channels to Undefined
        // updateState(AudioAuthorityBindingConstants.ZONE_INPUT_SWITCH_CHANNEL, new StringType(StringUtils.EMPTY));
        // updateState(AudioAuthorityBindingConstants.ZONE_MUTE_CHANNEL, UnDefType.UNDEF);
        // updateState(AudioAuthorityBindingConstants.ZONE_NAME_CHANNEL, new StringType(StringUtils.EMPTY));
        // updateState(AudioAuthorityBindingConstants.ZONE_POWER_CHANNEL, UnDefType.UNDEF);
        // updateState(AudioAuthorityBindingConstants.ZONE_VOLUME_DB_CHANNEL, UnDefType.UNDEF);
        // updateState(AudioAuthorityBindingConstants.ZONE_VOLUME_DIMMER_CHANNEL, UnDefType.UNDEF);
        // updateState(AudioAuthorityBindingConstants.ZONE_STEREO_CHANNEL, UnDefType.UNDEF);
    }

    @Override
    public void statusUpdateReceived(MatrixStatusUpdateEvent event) {
        try {
            MatrixResponse response = RequestResponseFactory.getMatrixResponse(event.getData());

            // TODO fix dependence on implementation
            switch ((MatrixResponseImpl.MatrixResponseType) response.getResponseType()) {
                case POWER_STATE:
                    managePowerStateUpdate(response);
                    break;

                case VOLUME_STATE:
                    manageVolumeLevelUpdate(response);
                    break;

                case MUTE_STATE:
                    manageMuteStateUpdate(response);
                    break;

                case INPUT_SWITCH_STATE:
                    manageInputSourceChannelUpdate(response);
                    break;

                case NAME_STATE:
                    manageOutputNameChannelUpdate(response);
                    break;
                // case DISPLAY_INFORMATION:
                // manageDisplayedInformationUpdate(response);
                // break;

                default:
                    logger.debug("Response Type Not Implemented from Matrix @{}. Response discarded: {}",
                            event.getData(), event.getConnection());

            }
        } catch (MatrixCommandResponseException e) {
            logger.debug("Unkown response type from Matrix @{}. Response discarded: {}", event.getData(),
                    event.getConnection());
        }
    }

    private void manageOutputNameChannelUpdate(MatrixResponse response) {
        StringType state = StringType.valueOf(response.getParameter());
        updateState(response.getChannelUID(), state);
    }

    /**
     * Notify an AVR power state update to OpenHAB
     *
     * @param response
     */
    private void managePowerStateUpdate(MatrixResponse response) {
        OnOffType state = PowerStateValues.ON_VALUE.equals(response.getParameter()) ? OnOffType.ON : OnOffType.OFF;

        // When a Power ON state update is received, call the onPowerOn method.
        if (OnOffType.ON == state) {
            onPowerOn();
        } else {
            onPowerOff();
        }

        updateState(response.getChannelUID(), state);
    }

    /**
     * Notify an AVR volume level update to OpenHAB
     *
     * @param response
     */
    private void manageVolumeLevelUpdate(MatrixResponse response) {
        String dbChannel = response.getChannelUID();
        updateState(dbChannel,
                new DecimalType(VolumeConverter.convertFromIpControlVolumeToDb(response.getParameter())));

        updateState(
                dbChannel.replaceFirst(AudioAuthorityBindingConstants.CHANNEL_VOLUME_DB_PATTERN_TO_REPLACE,
                        AudioAuthorityBindingConstants.CHANNEL_VOLUME_DIMMER_REPLACEMENT_TEXT),
                new PercentType((int) VolumeConverter.convertFromIpControlVolumeToPercent(response.getParameter())));
    }

    /**
     * Notify an AVR mute state update to OpenHAB
     *
     * @param response
     */
    private void manageMuteStateUpdate(MatrixResponse response) {
        updateState(response.getChannelUID(),
                response.getParameter().equals(MuteStateValues.OFF_VALUE) ? OnOffType.OFF : OnOffType.ON);
    }

    /**
     * Notify an AVR input source channel update to OpenHAB
     *
     * @param response
     */
    private void manageInputSourceChannelUpdate(MatrixResponse response) {
        String value = response.getParameter();

        // Stereo inputs set to mono outputs return responses as I21-28
        // need to convert responses to input commands so UI can update responses I21-28 from matrix
        // on mono output channels

        switch (value.toString()) {
            case "21":
                value = "1";
                break;
            case "22":
                value = "3";
                break;
            case "23":
                value = "5";
                break;
            case "24":
                value = "7";
                break;
            case "25":
                value = "9";
                break;
            case "26":
                value = "11";
                break;
            case "27":
                value = "13";
                break;
            case "28":
                value = "15";
                break;
            default:
                break;
        }

        updateState(response.getChannelUID(), new DecimalType(value));
    }
}
