package org.openhab.binding.audioauthority.internal.protocol;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.openhab.binding.audioauthority.AudioAuthorityBindingConstants;
import org.openhab.binding.audioauthority.protocol.MatrixCommandResponseException;
import org.openhab.binding.audioauthority.protocol.MatrixResponse;

public class MatrixResponseImpl implements MatrixResponse {

    public enum MatrixResponseType implements MatrixResponse.MatrixResponseType {
        POWER_STATE(AudioAuthorityBindingConstants.POWER_STATE_RESPONSE_PATTERN,
                AudioAuthorityBindingConstants.POWER_STATE_CHANNELUID_REPLACE),
        VOLUME_STATE(AudioAuthorityBindingConstants.VOLUME_STATE_RESPONSE_PATTERN,
                AudioAuthorityBindingConstants.VOLUME_STATE_CHANNELUID_REPLACE),
        MUTE_STATE(AudioAuthorityBindingConstants.MUTE_STATE_RESPONSE_PATTERN,
                AudioAuthorityBindingConstants.MUTE_STATE_CHANNELUID_REPLACE),
        NAME_STATE(AudioAuthorityBindingConstants.NAME_STATE_RESPONSE_PATTERN,
                AudioAuthorityBindingConstants.NAME_STATE_CHANNELUID_REPLACE),
        INPUT_SWITCH_STATE(AudioAuthorityBindingConstants.INPUT_SWITCH_STATE_RESPONSE_PATTERN,
                AudioAuthorityBindingConstants.INPUT_SWITCH_STATE_CHANNELUID_REPLACE);

        private Pattern responsePattern;
        private String channelReplace;

        private MatrixResponseType(String pattern, String channelReplace) {
            this.responsePattern = Pattern.compile("^" + AudioAuthorityBindingConstants.RESPONSE_START_PATTERN + pattern
                    + AudioAuthorityBindingConstants.RESPONSE_END_PATTERN + "$");
            this.channelReplace = channelReplace;
        }

        @Override
        public String getChannelUID(String response) {
            Matcher m = responsePattern.matcher(response);
            m.matches();
            StringBuffer sb = new StringBuffer();
            m.appendReplacement(sb, channelReplace);
            return sb.toString();
        }

        @Override
        public boolean matches(String responseData) {
            return responsePattern.matcher(responseData).matches();
        }

        @Override
        public String getParameter(String response) {
            Matcher m = responsePattern.matcher(response);
            String result = null;
            if (m.matches()) {
                result = m.group(m.groupCount());
            }
            ;
            return result;
        }

    }

    private MatrixResponseType responseType;
    private String responseData;
    private String parameter;
    private String channelUID;

    public MatrixResponseImpl(String response) throws MatrixCommandResponseException {

        if (StringUtils.isEmpty(response)) {
            throw new MatrixCommandResponseException("responseData is empty. Cannot parse the response.");
        }
        for (MatrixResponseType responseType : MatrixResponseType.values()) {
            if (responseType.matches(response)) {
                this.responseType = responseType;
            }
        }

        if (this.responseType == null) {
            throw new MatrixCommandResponseException("Unable to parse the response: " + response);
        }

        this.responseData = response;
        this.parameter = responseType.getParameter(responseData);
        this.channelUID = responseType.getChannelUID(responseData);
    }

    @Override
    public String getChannelUID() {
        return channelUID;
    }

    @Override
    public String getParameter() {
        return this.parameter;
    }

    @Override
    public MatrixResponseType getResponseType() {
        return this.responseType;
    }

}
