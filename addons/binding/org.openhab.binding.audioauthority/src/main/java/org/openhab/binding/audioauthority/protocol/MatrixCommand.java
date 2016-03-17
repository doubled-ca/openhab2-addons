package org.openhab.binding.audioauthority.protocol;

public interface MatrixCommand {

    public interface MatrixCommandType {

        public String getCommand(String channelUID);

        // public String getResponse(String response);

        boolean matches(String channelData);

        // boolean matchesResponse(String responseData);

        // String getChannelUid(String responseData);

        // boolean hasParameter();

        // String parseResponseParameter(String responseData);

    }

    public String getCommand();

    // public String getResponse(String response);

    // public MatrixCommandType getCommandResponseType();
}
