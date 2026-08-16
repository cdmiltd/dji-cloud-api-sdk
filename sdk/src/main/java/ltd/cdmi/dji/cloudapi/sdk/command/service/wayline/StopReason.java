package ltd.cdmi.dji.cloudapi.sdk.command.service.wayline;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * flighttask_stop.reason 停止原因。
 *
 * <p>对应 DJI Cloud API {@code flighttask_stop} 指令的 {@code reason} 字段。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html">
 * DJI Dock to Cloud MQTT API</a>
 */
public enum StopReason {
    NORMAL(0, "正常结束"),
    ANOTHER_DOCK(1, "另一机场状态机异常");

    @JsonValue
    private final int code;
    private final String desc;

    StopReason(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int code() { return code; }
    public String desc() { return desc; }
}
