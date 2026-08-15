/**
 * Pilot 上云（指令飞行）services 请求 POJO。
 *
 * <p>对应 DJI Pilot-to-Cloud drc.html 文档的指令飞行 services 方法：
 * {@code poi_mode_enter}、{@code poi_circle_speed_set} 等。
 *
 * <p><b>注</b>：这些方法属于 Pilot 上云（遥控器侧），Topic 与 Dock 上云相同
 * （{@code thing/product/{gateway_sn}/services}），但设备端为 DJI RC Plus 2 遥控器。
 * simulator 未实现这些方法，字段依据 DJI 文档。
 */
package ltd.cdmi.dji.cloudapi.sdk.command.service.pilot;
