package org.openhab.binding.hdtvsupply.internal.protocol;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openhab.binding.hdtvsupply.HDTVSupplyBindingConstants;
import org.openhab.binding.hdtvsupply.protocol.VideoMatrixCommand;

public class VideoMatrixCommandImpl implements VideoMatrixCommand {

    public enum VideoMatrixCommandType implements VideoMatrixCommand.VideoMatrixCommandType {

        OUTPUT_SWITCH_INPUT(HDTVSupplyBindingConstants.OUTPUT_SELECTED_INPUT_PATTERN,
                HDTVSupplyBindingConstants.OUTPUT_SELECTED_INPUT_REPLACE),
        INPUT_SWITCH_EDID(HDTVSupplyBindingConstants.INPUT_SET_EDID_PATTERN,
                HDTVSupplyBindingConstants.INPUT_SET_EDID_REPLACE);

        private Pattern channelUIDPattern;
        private String commandReplace;

        private VideoMatrixCommandType(String channelPattern, String commandReplace) {
            this.channelUIDPattern = Pattern.compile("^" + channelPattern + "$");
            this.commandReplace = commandReplace;
        }

        @Override
        public String getCommand(String channelUID) {
            Matcher m = channelUIDPattern.matcher(channelUID);
            m.matches();
            StringBuffer sb = new StringBuffer();
            m.appendReplacement(sb, commandReplace);
            return HDTVSupplyBindingConstants.COMMAND_PREFIX + sb.toString()
                    + HDTVSupplyBindingConstants.COMMAND_SUFFIX;
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

    @Override
    public String getCommand() {
        // TODO Auto-generated method stub
        return null;
    }

}
