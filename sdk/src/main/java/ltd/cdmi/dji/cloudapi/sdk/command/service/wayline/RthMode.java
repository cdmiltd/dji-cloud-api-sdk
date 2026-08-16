package ltd.cdmi.dji.cloudapi.sdk.command.service.wayline;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * flighttask_prepare.rth_mode 返航模式。
 *
 * <p>对应 DJI Cloud API {@code flighttask_prepare} 指令的 {@code rth_mode} 字段。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html">
 * DJI Dock to Cloud MQTT API</a>
 */
public enum RthMode {
    SMART(0, "智能高度"),
    SETTING_HEIGHT(1, "设定高度");

    @JsonValue
    private final int code;
    private final String desc;

    RthMode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int code() { return code; }
    public String desc() { return desc; }
}
