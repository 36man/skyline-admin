import request from '@/utils/request'

/**
 * API-分页查询
 * @param params:
 * {
      isClusterLoad: false,
      clusterId: 1,
      matchCondition: "xxx",
      pageNo: 1,
      pageSize: 10
    }
 */
export function pageList(params) {
  return request({
    url: '/api/pageList',
    method: 'get',
    params
  })
}
/**
 * API-创建
 * @param params:
 * {
      clusterId: "1",
      matchCondition: "xxx",
      description: "描述",
      meno: "备注",
    }
 */
export function create(params) {
  return request({
    url: '/api',
    method: 'post'
  })
}

/**
 * API-更新
 * @param id
 * @param params: {}
 */
export function update(id, params) {
  return request({
    url: '/api/' + id,
    method: 'put'
  })
}

/**
 * API-配置
 * @param id
 * @param params:
 * {
      id: "1",
      pluginList: [
        {
          stage: 1,
          stageName: "步骤一",
          stateSn: "步骤一",
          pluginVerId: "1",
          sn: "1",
          configParams: '{"name": "张三", "age": 12}',
        }
      ]
    }
 */
export function update(params) {
  return request({
    url: '/api/configPlugin',
    method: 'put'
  })
}

/**
 * API-删除
 * @param ids
 */
export function deleteByIds(ids) {
  return request({
    url: '/api/' + ids,
    method: 'delete'
  })
}

/**
 * API-发布
 * @param ids
 */
export function applyCluster(ids) {
  return request({
    url: '/api/publish/' + ids,
    method: 'put'
  })
}
