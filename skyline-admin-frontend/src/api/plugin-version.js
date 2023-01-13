import request from '@/utils/request'

export function getList(params) {
  return request({
    url: '/plugin/version/list',
    method: 'get',
    params
  })
}
