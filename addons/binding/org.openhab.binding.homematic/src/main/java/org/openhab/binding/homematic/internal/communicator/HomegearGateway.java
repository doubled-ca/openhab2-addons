/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.homematic.internal.communicator;

import java.io.IOException;
import java.util.Collection;

import org.openhab.binding.homematic.internal.common.HomematicConfig;
import org.openhab.binding.homematic.internal.model.HmChannel;
import org.openhab.binding.homematic.internal.model.HmDatapoint;
import org.openhab.binding.homematic.internal.model.HmDevice;

/**
 * HomematicGateway implementation for Homegear.
 *
 * @author Gerhard Riegler - Initial contribution
 */
public class HomegearGateway extends AbstractHomematicGateway {

    protected HomegearGateway(String id, HomematicConfig config, HomematicGatewayListener eventListener) {
        super(id, config, eventListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadVariables(HmChannel channel) throws IOException {
        rpcClient.getAllSystemVariables(channel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadScripts(HmChannel channel) throws IOException {
        rpcClient.getAllScripts(channel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setVariable(HmDatapoint dp, Object value) throws IOException {
        rpcClient.setSystemVariable(dp, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void executeScript(HmDatapoint dp) throws IOException {
        rpcClient.executeScript(dp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadDeviceNames(Collection<HmDevice> devices) throws IOException {
        rpcClient.loadDeviceNames(getDefaultInterface(), devices);
    }

}
