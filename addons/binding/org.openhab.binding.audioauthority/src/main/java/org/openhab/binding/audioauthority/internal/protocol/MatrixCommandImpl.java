package org.openhab.binding.audioauthority.internal.protocol;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openhab.binding.audioauthority.AudioAuthorityBindingConstants;
import org.openhab.binding.audioauthority.protocol.MatrixCommand;
import org.openhab.binding.audioauthority.protocol.MatrixCommandResponseException;

public class MatrixCommandImpl implements MatrixCommand {

    public enum MatrixCommandType implements MatrixCommand.MatrixCommandType {

        POWER(AudioAuthorityBindingConstants.POWER_CHANNEL_PATTERN,
                AudioAuthorityBindingConstants.POWER_COMMAND_REPLACE),
        VOLUME_DB(AudioAuthorityBindingConstants.VOLUME_DB_CHANNEL_PATTERN,
                AudioAuthorityBindingConstants.VOLUME_DB_COMMAND_REPLACE),
        VOLUME_UP_DOWN(AudioAuthorityBindingConstants.VOLUME_DIMMER_CHANNEL_PATTERN,
                AudioAuthorityBindingConstants.VOLUME_DIMMER_COMMAND_REPLACE),
        MUTE(AudioAuthorityBindingConstants.MUTE_CHANNEL_PATTERN, AudioAuthorityBindingConstants.MUTE_COMMAND_REPLACE),
        OUTPUT_NAME(AudioAuthorityBindingConstants.OUTPUT_NAME_CHANNEL_PATTERN,
                AudioAuthorityBindingConstants.OUTPUT_NAME_COMMAND_REPLACE),
        INPUT_SOURCE_SWITCH(AudioAuthorityBindingConstants.INPUT_SOURCE_SWITCH_CHANNEL_PATTERN,
                AudioAuthorityBindingConstants.INPUT_SOURCE_SWITCH_COMMAND_REPLACE);

        private Pattern channelUIDPattern;
        private String commandReplace;

        private MatrixCommandType(String channelPattern, String commandReplace) {
            this.channelUIDPattern = Pattern.compile("^" + channelPattern + "$");
            this.commandReplace = commandReplace;
        }

        @Override
        public String getCommand(String channelUID) {
            Matcher m = channelUIDPattern.matcher(channelUID);
            m.matches();
            StringBuffer sb = new StringBuffer();
            m.appendReplacement(sb, commandReplace);
            return AudioAuthorityBindingConstants.COMMAND_PREFIX + sb.toString()
                    + AudioAuthorityBindingConstants.COMMAND_SUFFIX;
        }

        /**
         * Return true if the Channel Data matches with this Type
         *
         * @param channelData
         * @return
         */
        @Override
        public boolean matches(String channelData) {
            return channelUIDPattern.matcher(channelData).matches();
        }

    }

    private String channelUID;
    private String parameterValue;
    private MatrixCommandType commandType;

    public MatrixCommandImpl(String channelUID, String parameterValue) throws MatrixCommandResponseException {
        this.channelUID = channelUID;
        this.parameterValue = parameterValue;

        for (MatrixCommandType cmdType : MatrixCommandType.values()) {
            if (cmdType.matches(channelUID + parameterValue)) {
                commandType = cmdType;
            }
        }
        if (commandType == null) {
            throw new MatrixCommandResponseException("Unable to translate the command '" + channelUID
                    + "' with parameter '" + parameterValue + "' into a Matrix Command.");
        }

    }

    @Override
    public String getCommand() {
        return commandType.getCommand(channelUID + parameterValue);
    }

}
