package ltd.cdmi.dji.cloudapi.sdk.command.service.wayline;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * flighttask_prepare.wayline_precision_type 航线精度类型。
 *
 * <p>对应 DJI Cloud API {@code flighttask_prepare} 指令的 {@code wayline_precision_type} 字段。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html">
 * DJI Dock to Cloud MQTT API</a>
 */
public enum WaylinePrecisionType {
    GPS(0, "GPS 任务"),
    RTK(1, "高精度 RTK 任务");

    @JsonValue
    private final int code;
    private final String desc;

    WaylinePrecisionType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int code() { return code; }
    public String desc() { return desc; }
}
