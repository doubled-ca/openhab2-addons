package org.openhab.binding.audioauthority.internal.protocol;

import org.eclipse.smarthome.core.thing.ChannelUID;
import org.openhab.binding.audioauthority.protocol.MatrixCommand;
import org.openhab.binding.audioauthority.protocol.MatrixResponse;

public final class RequestResponseFactory {

    public static MatrixCommand getIpControlCommand(ChannelUID channelUID, String parameter) {
        return new MatrixCommandImpl(channelUID.getId(), parameter);
    }

    public static MatrixCommand getIpControlCommand(String channelUID, String parameter) {
        return new MatrixCommandImpl(channelUID, parameter);
    }

    public static MatrixCommand getIpControlCommand(ChannelUID channelUID) {
        return getIpControlCommand(channelUID, null);
    }

    public static MatrixResponse getMatrixResponse(String responseData) {
        return new MatrixResponseImpl(responseData);
    }

}
