let ClusterStatus = {
  PENDING: {name: "等待调度", tagType: "info"},
  RUNNING: {name: "启动中", tagType: ""},
  SUCCEEDED: {name: "启动成功", tagType: "success"},
  FAILED: {name: "容器终止", tagType: "danger"},
  UNKNOWN: {name: "未知状态", tagType: "warning"},
}
let ApiStatus = {
  enable: {name: "启用", tagType: "success"},
  disable: {name: "禁用", tagType: "danger"},
  in_enable: {name: "启用中", tagType: "success"},
  new: {name: "新增", tagType: "info"},
}
export function getClusterStatusName(statusCode) {
  let s = ClusterStatus[statusCode];
  return s ? s.name : "";
}
export function getClusterStatusTagType(statusCode) {
  let s = ClusterStatus[statusCode];
  return s ? s.tagType : "";
}
export function getApiStatusName(statusCode) {
  let s = ApiStatus[statusCode];
  return s ? s.name : "";
}
export function getApiStatusTagType(statusCode) {
  let s = ApiStatus[statusCode];
  return s ? s.tagType : "";
}

