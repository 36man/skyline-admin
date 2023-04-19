let ClusterStatus = {
  PENDING: "创建成功,未开始调度",
  RUNNING: "已经被调度至节点,至少有一个启动成功",
  SUCCEEDED: "启动成功",
  FAILED: "容器终止",
  UNKNOWN: "API Server无法正常获取到Pod对象的状态信息",
}
let ApiStatus = {
  enable: "启用",
  disable: "禁用",
  in_enable: "启用中",
  new: "新增",
}
export function getClusterStatusName(statusCode) {
  return ClusterStatus[statusCode];
}

export function getApiStatusName(statusCode) {
  return ApiStatus[statusCode];
}

