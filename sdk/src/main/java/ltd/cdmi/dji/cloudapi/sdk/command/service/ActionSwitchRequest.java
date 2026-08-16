package ltd.cdmi.dji.cloudapi.sdk.command.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * 通用开关命令请求（action 字段）。
 *
 * <p>适用于 {@code air_conditioner_mode_switch} / {@code alarm_state_switch} /
 * {@code battery_store_mode_switch} / {@code battery_maintenance_switch} 等指令。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html">
 * DJI Dock to Cloud MQTT API</a>
 */
@JsonInclude(NON_NULL)
public record ActionSwitchRequest(
    @JsonProperty("action") Integer action
) {}
