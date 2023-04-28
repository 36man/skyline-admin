import request from '@/utils/request'

/**
 * 插件-分页查询
 * @param params:
 * {
      pluginName: "测试插件",
      classDefine: "Test.class",
      maintainer: "张三",
      pageNo: 1,
      pageSize: 10
    }
 */
export function pageList(params) {
  return request({
    url: '/plugin/pageList',
    method: 'get',
    params
  })
}
/**
 * 插件-上传
 * @param file
 */
export function upload(file) {
  return request({
    url: '/plugin/upload',
    method: 'post',
  })
}
/**
 * 插件-删除
 * @param id
 */
export function deleteById(id) {
  return request({
    url: '/plugin/' + id,
    method: 'delete',
  })
}
/**
 * 插件-启用
 * @param id
 */
export function enableById(id) {
  return request({
    url: '/plugin/enable/' + id,
    method: 'post',
  })
}
/**
 * 插件-禁用
 * @param id
 */
export function disableById(id) {
  return request({
    url: '/plugin/disable/' + id,
    method: 'post',
  })
}
/**
 * 插件-更新
 * @param params:
 * {
      id: "1",
      pluginName: "Test.class",
      maintainer: "张三",
    }
 */
export function update(params) {
  return request({
    url: '/plugin',
    method: 'post',
    params
  })
}



/**
 * 插件版本-分页查询
 * @param params:
 * {
      pluginId: "1",
      ver: "1.0.1",
      pageNo: 1,
      pageSize: 10
    }
 */
export function verPageList(params) {
  return request({
    url: '/plugin/ver/pageList',
    method: 'get',
    params
  })
}
/**
 * 插件版本-搜索
 * @param params:
 * {
      keyword: "xxx",
      pageNo: 1,
      pageSize: 10
    }
 */
export function verSearch(params) {
  return request({
    url: '/plugin/ver/search',
    method: 'get',
    params
  })
}
/**
 * 插件版本-启用
 * @param id
 */
export function verEnableById(id) {
  return request({
    url: '/plugin/ver/enable/' + id,
    method: 'post',
  })
}
/**
 * 插件版本-禁用
 * @param id
 */
export function verDisableById(id) {
  return request({
    url: '/plugin/ver/disable/' + id,
    method: 'post',
  })
}
