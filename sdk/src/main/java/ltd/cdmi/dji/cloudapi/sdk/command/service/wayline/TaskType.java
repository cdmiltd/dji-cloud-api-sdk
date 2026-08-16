package ltd.cdmi.dji.cloudapi.sdk.command.service.wayline;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * flighttask_prepare.task_type 任务类型。
 *
 * <p>对应 DJI Cloud API {@code flighttask_prepare} 指令的 {@code task_type} 字段。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html">
 * DJI Dock to Cloud MQTT API</a>
 */
public enum TaskType {
    IMMEDIATE(0, "立即任务"),
    SCHEDULED(1, "定时任务"),
    CONDITIONAL(2, "条件任务");

    @JsonValue
    private final int code;
    private final String desc;

    TaskType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int code() { return code; }
    public String desc() { return desc; }
}
