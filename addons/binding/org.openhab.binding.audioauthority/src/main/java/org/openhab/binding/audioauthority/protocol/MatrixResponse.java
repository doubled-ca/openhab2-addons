package org.openhab.binding.audioauthority.protocol;

public interface MatrixResponse {

    public interface MatrixResponseType {

        public String getChannelUID(String response);

        public boolean matches(String responseData);

        public String getParameter(String response);

    }

    public String getChannelUID();

    public String getParameter();

    public MatrixResponseType getResponseType();
}
