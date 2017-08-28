package org.openhab.binding.hdtvsupply.protocol;

public interface VideoMatrixCommand {

    public interface VideoMatrixCommandType {

        public String getCommand(String channelUID);

        boolean matches(String channelData);

    }

    public String getCommand();

}
