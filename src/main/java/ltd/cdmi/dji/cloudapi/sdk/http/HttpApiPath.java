// Copyright (C) 2026 CDMI.LTD
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package ltd.cdmi.dji.cloudapi.sdk.http;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * DJI Cloud API Pilot 上云 HTTP API 路径常量。
 *
 * <p>Pilot 通过 HTTP 调用 hivemind Server API，路径前缀按业务域区分（DJI 文档规定）：
 * <ul>
 *   <li>{@code /manage/api/v1/workspaces} — 设备拓扑（态势感知）</li>
 *   <li>{@code /map/api/v1/workspaces} — 地图元素</li>
 *   <li>{@code /media/api/v1/workspaces} — 媒体管理</li>
 *   <li>{@code /storage/api/v1/workspaces} — 存储服务（STS 凭证）</li>
 *   <li>{@code /wayline/api/v1/workspaces} — 航线管理</li>
 * </ul>
 *
 * <p>路径中的 {@code {workspace_id}} 是运行时占位符，由调用方替换为实际组织 ID；
 * {@code {element_id}} / {@code {group_id}} / {@code {wayline_id}} 同理。
 *
 * <p>本类只定义路径模板常量，不绑定具体 HTTP 客户端实现，符合 SDK
 * <a href="../../../../../../../docs/architecture-design.md">§1.3 非目标</a>：
 * 「不耦合 HTTP/WS 客户端」。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/https/overview.html">
 * DJI Pilot 上云 HTTP API 概览</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/https/overview.html")
@Verified(basis = "DJI Cloud API 官方文档 Pilot 上云 HTTP API 路径前缀与端点定义（simulator 5 个 Api 类已实现并对接 hivemind）")
public final class HttpApiPath {

    private HttpApiPath() {
    }

    // ===== 设备拓扑（/manage/api/v1/workspaces） =====

    /** 设备拓扑 API 路径前缀（与 MapElement/Storage/Media/Wayline 不同，DJI 文档规定） */
    public static final String MANAGE_BASE_PATH = "/manage/api/v1/workspaces";

    /** GET /manage/api/v1/workspaces/{workspace_id}/devices/topologies — 获取设备拓扑列表 */
    public static final String DEVICES_TOPOLOGIES = MANAGE_BASE_PATH + "/{workspace_id}/devices/topologies";

    // ===== 地图元素（/map/api/v1/workspaces） =====

    /** 地图元素 API 路径前缀 */
    public static final String MAP_BASE_PATH = "/map/api/v1/workspaces";

    /** GET /map/api/v1/workspaces/{workspace_id}/element-groups — 获取地图元素列表 */
    public static final String ELEMENT_GROUPS = MAP_BASE_PATH + "/{workspace_id}/element-groups";

    /** POST /map/api/v1/workspaces/{workspace_id}/element-groups/{group_id}/elements — 创建地图元素 */
    public static final String CREATE_ELEMENT = MAP_BASE_PATH + "/{workspace_id}/element-groups/{group_id}/elements";

    /** PUT /map/api/v1/workspaces/{workspace_id}/elements/{element_id} — 更新地图元素 */
    public static final String UPDATE_ELEMENT = MAP_BASE_PATH + "/{workspace_id}/elements/{element_id}";

    /** DELETE /map/api/v1/workspaces/{workspace_id}/elements/{element_id} — 删除地图元素 */
    public static final String DELETE_ELEMENT = MAP_BASE_PATH + "/{workspace_id}/elements/{element_id}";

    // ===== 媒体管理（/media/api/v1/workspaces） =====

    /** 媒体管理 API 路径前缀 */
    public static final String MEDIA_BASE_PATH = "/media/api/v1/workspaces";

    /** POST /media/api/v1/workspaces/{workspace_id}/fast-upload — 文件快传（秒传） */
    public static final String FAST_UPLOAD = MEDIA_BASE_PATH + "/{workspace_id}/fast-upload";

    /** POST /media/api/v1/workspaces/{workspace_id}/files/tiny-fingerprints — 获取已存在的精简指纹 */
    public static final String TINY_FINGERPRINTS = MEDIA_BASE_PATH + "/{workspace_id}/files/tiny-fingerprints";

    /** POST /media/api/v1/workspaces/{workspace_id}/upload-callback — 媒体文件上传结果上报 */
    public static final String MEDIA_UPLOAD_CALLBACK = MEDIA_BASE_PATH + "/{workspace_id}/upload-callback";

    /** POST /media/api/v1/workspaces/{workspace_id}/group-upload-callback — 文件组上传完成后回调 */
    public static final String GROUP_UPLOAD_CALLBACK = MEDIA_BASE_PATH + "/{workspace_id}/group-upload-callback";

    // ===== 存储服务（/storage/api/v1/workspaces） =====

    /** 存储服务 API 路径前缀（媒体/航线上传文件前共用获取 STS 凭证） */
    public static final String STORAGE_BASE_PATH = "/storage/api/v1/workspaces";

    /** POST /storage/api/v1/workspaces/{workspace_id}/sts — 生成上传文件临时凭证 */
    public static final String STS = STORAGE_BASE_PATH + "/{workspace_id}/sts";

    // ===== 航线管理（/wayline/api/v1/workspaces） =====

    /** 航线管理 API 路径前缀 */
    public static final String WAYLINE_BASE_PATH = "/wayline/api/v1/workspaces";

    /** GET /wayline/api/v1/workspaces/{workspace_id}/waylines — 获取航线文件列表 */
    public static final String WAYLINES = WAYLINE_BASE_PATH + "/{workspace_id}/waylines";

    /** GET /wayline/api/v1/workspaces/{workspace_id}/waylines/{wayline_id}/url — 获取航线文件下载地址 */
    public static final String WAYLINE_URL = WAYLINE_BASE_PATH + "/{workspace_id}/waylines/{wayline_id}/url";

    /** GET /wayline/api/v1/workspaces/{workspace_id}/waylines/duplicate-names — 获取重复的航线文件名称 */
    public static final String WAYLINE_DUPLICATE_NAMES = WAYLINE_BASE_PATH + "/{workspace_id}/waylines/duplicate-names";

    /** POST /wayline/api/v1/workspaces/{workspace_id}/upload-callback — 航线文件上传结果上报 */
    public static final String WAYLINE_UPLOAD_CALLBACK = WAYLINE_BASE_PATH + "/{workspace_id}/upload-callback";

    /** POST /wayline/api/v1/workspaces/{workspace_id}/favorites — 批量收藏航线文件 */
    public static final String ADD_FAVORITES = WAYLINE_BASE_PATH + "/{workspace_id}/favorites";

    /** DELETE /wayline/api/v1/workspaces/{workspace_id}/favorites — 批量取消收藏航线文件 */
    public static final String REMOVE_FAVORITES = WAYLINE_BASE_PATH + "/{workspace_id}/favorites";
}
