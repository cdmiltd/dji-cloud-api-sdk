package ltd.cdmi.dji.cloudapi.sdk.command.service.wayline;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * flighttask_prepare.out_of_control_action 失控动作。
 *
 * <p>对应 DJI Cloud API {@code flighttask_prepare} 指令的 {@code out_of_control_action} 字段。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html">
 * DJI Dock to Cloud MQTT API</a>
 */
public enum OutOfControlAction {
    RETURN_HOME(0, "返航"),
    HOVER(1, "悬停"),
    LAND(2, "降落");

    @JsonValue
    private final int code;
    private final String desc;

    OutOfControlAction(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int code() { return code; }
    public String desc() { return desc; }
}
