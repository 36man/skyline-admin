import request from '@/utils/request'

/**
 * 集群-分页查询
 * @param params:
 * {
      domain: "www.baidu.com",
      bizKey: "test-cluster",
      clusterName: "测试集群",
      pageNo: 1,
      pageSize: 10
    }
 */
export function pageList(params) {
  return request({
    url: '/cluster/pageList',
    method: 'get',
    params
  })
}

/**
 * 集群-根据ID查询
 * @param id
 */
export function findById(id) {
  return request({
    url: '/cluster/' + id,
    method: 'get'
  })
}

/**
 * 集群-创建
 * @param params:
 * {
      clusterName: "测试集群",
      domain: "www.baidu.com",
      bizKey: "test-cluster",
      instanceCount: 5,
      configShare: false,
      configUrl: "nacos://192.168.0.110:8080",
      configUser: "nacos",
      configSecret: "123",
      useQuota: "4c8g",
      configItem: {
        databaseUrl: "jdbc:mysql://localhost:3306/xxxx",
        databaseUser: "root",
        databasePassword: "123",
        databaseShare: false,
      },
      meno: "备注",
    }
 */
export function create(params) {
  return request({
    url: '/cluster',
    method: 'post'
  })
}

/**
 * 集群-更新
 * @param id
 * @param params: {}
 */
export function update(id, params) {
  return request({
    url: '/cluster/' + id,
    method: 'put'
  })
}

/**
 * 集群-删除
 * @param id
 */
export function deleteById(id) {
  return request({
    url: '/cluster/' + id,
    method: 'delete'
  })
}

/**
 * 集群-启动
 * @param id
 */
export function applyCluster(id) {
  return request({
    url: '/cluster/applyCluster/' + id,
    method: 'put'
  })
}
