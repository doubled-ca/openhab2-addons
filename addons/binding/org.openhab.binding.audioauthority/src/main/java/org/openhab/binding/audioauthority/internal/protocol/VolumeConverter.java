package org.openhab.binding.audioauthority.internal.protocol;

import java.text.DecimalFormat;

public final class VolumeConverter {

    private static final String IP_CONTROL_VOLUME_FORMAT = "##";
    private static final String IP_CONTROL_VOLUME_DEFAULT_VALUE = "-30";

    private static final double MAX_IP_CONTROL_VOLUME = 0;
    private static final double MIN_IP_CONTROL_VOLUME = -80;
    // private static final double MAX_DB_VOLUME = 0;
    private static final double MIN_DB_VOLUME = -80;

    private static final DecimalFormat FORMATTER = new DecimalFormat(IP_CONTROL_VOLUME_FORMAT);

    /**
     * Return the double value of the volume from the value received in the IpControl response.
     *
     * @param ipControlVolume
     * @return the volume in Db
     */
    public static double convertFromIpControlVolumeToDb(String ipControlVolume) {
        double ipControlVolumeInt = Double.parseDouble(ipControlVolume);

        // assumes the length of ranges are the same between IP control and dB volumes
        return ipControlVolumeInt - MIN_IP_CONTROL_VOLUME + MIN_DB_VOLUME;
    }

    /**
     * Return the string parameter to send to the AVR based on the given volume.
     *
     * @param volumeDb
     * @return the volume for IpControlRequest*100d
     */
    public static String convertFromDbToIpControlVolume(double volumeDb) {

        // assumes the length of ranges are the same between IP control and dB volumes
        return formatIpControlVolume(volumeDb - MIN_DB_VOLUME + MIN_IP_CONTROL_VOLUME);
    }

    /**
     * Return the String parameter to send to the Matrix based on the given percentage of the max volume level.
     *
     * @param volumePercent
     * @return the volume for IpControlRequest
     */
    public static String convertFromPercentToIpControlVolume(double volumePercent) {
        double ipControlVolume = (volumePercent * (MAX_IP_CONTROL_VOLUME - MIN_IP_CONTROL_VOLUME)) / 100
                + MIN_IP_CONTROL_VOLUME;
        return formatIpControlVolume(ipControlVolume);
    }

    /**
     * Return the percentage of the max volume level from the value received in the IpControl response.
     *
     * @param ipControlVolume
     * @return the volume percentage
     */
    public static double convertFromIpControlVolumeToPercent(String ipControlVolume) {
        double ipControlVolumeInt = Double.parseDouble(ipControlVolume);
        return ((ipControlVolumeInt - MIN_IP_CONTROL_VOLUME) * 100d) / (MAX_IP_CONTROL_VOLUME - MIN_IP_CONTROL_VOLUME);
    }

    /**
     * Format the given double value to an IpControl volume.
     *
     * @param ipControlVolume
     * @return
     */
    private static String formatIpControlVolume(double ipControlVolume) {
        String result = IP_CONTROL_VOLUME_DEFAULT_VALUE;
        // DecimalFormat is not ThreadSafe
        synchronized (FORMATTER) {
            result = FORMATTER.format(Math.round(ipControlVolume));
        }
        return result;
    }

}
