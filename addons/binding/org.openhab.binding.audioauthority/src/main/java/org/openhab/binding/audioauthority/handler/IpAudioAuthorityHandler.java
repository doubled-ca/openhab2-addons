package org.openhab.binding.audioauthority.handler;

import org.eclipse.smarthome.core.thing.Thing;
import org.openhab.binding.audioauthority.AudioAuthorityBindingConstants;
import org.openhab.binding.audioauthority.connection.MatrixConnection;
import org.openhab.binding.audioauthority.internal.connection.IpMatrixConnection;

public class IpAudioAuthorityHandler extends AudioAuthorityHandler {

    public IpAudioAuthorityHandler(Thing thing) {
        super(thing);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected MatrixConnection createConnection() {
        String host = (String) this.getConfig().get(AudioAuthorityBindingConstants.HOST_PARAMETER);
        Integer tcpPort = ((Number) this.getConfig().get(AudioAuthorityBindingConstants.TCP_PORT_PARAMETER)).intValue();
        Integer unitNumber = ((Number) this.getConfig().get(AudioAuthorityBindingConstants.UNIT_NUMBER_PARAMETER))
                .intValue();
        return new IpMatrixConnection(host, tcpPort, unitNumber);
    }

}
